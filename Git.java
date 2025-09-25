import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


public class Git {
    public static String getPath(String filePath, String folderName){
        return (filePath == null ? "" : filePath + "/") + folderName;
    }

    public static boolean directoryExists(String filePath, String folderName){
        String path = getPath(filePath, folderName);
        File newDir = new File(path);
        return newDir.exists() && newDir.isDirectory();
    }

    public static boolean fileExists(String filePath, String fileName){
        String path = getPath(filePath, fileName);
        File f = new File(path);
        return f.exists() && f.isFile();
    }

    public static void makeDirectory(String filePath, String folderName){
        if (directoryExists(filePath, folderName)){
            return;
        }
        String path = getPath(filePath, folderName);
        File newDir = new File(path);
        newDir.mkdir();
    }

    private static void deleteDirectory(File file)
    {
        for (File subfile : file.listFiles()) {
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }
            subfile.delete();
        }
        file.delete();
    }

    public static void deleteDirectory(String filePath, String folderName){
        if (!directoryExists(filePath, folderName)){
            return;
        }
        String path = getPath(filePath, folderName);
        File dir = new File(path);
        deleteDirectory(dir);
    }

    public static void makeFile(String filePath, String fileName){
        if (fileExists(filePath, fileName)){
            return;
        }
        String path = getPath(filePath, fileName);
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void deleteFile(String filePath, String fileName){
        if (fileExists(filePath, fileName)){
            String path = getPath(filePath, fileName);
            File f = new File(path);
            f.delete();
        }
    }

    public static String readFile(String filePath, String fileName){
        String path = getPath(filePath, fileName);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (sb.length() > 1){
            sb.deleteCharAt(sb.length() - 1); //remove terminator
        }
        return sb.toString();
    }

    public static void writeToFile(String filePath, String fileName, String content){
        String path = getPath(filePath, fileName);
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendToFile(String filePath, String fileName, String content){
        String path = getPath(filePath, fileName);
        try (FileWriter fw = new FileWriter(path, true); 
            BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String hash(String content){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = digest.digest(content.getBytes(StandardCharsets.UTF_8)); //these two lines googled from library
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (Exception e) {
        }
        return null;
    }

    public static String hashFile(String filePath, String fileName){
        return hash(readFile(filePath, fileName));
    }
    
    public static void intializeRepo(){
        if (fileExists("git/objects", "index") && fileExists("git/objects", "HEAD") 
        && directoryExists("git", "objects") && directoryExists(null, "git")){
            System.out.println("Git Repository Already Exists");
            return;
        }
        deleteFile(null, "git"); //in case "cloggs" options
        makeDirectory(null, "git");

        deleteFile("git", "objects"); //in case "cloggs" options
        makeDirectory("git", "objects");

        deleteDirectory("git", "index"); //in case "cloggs" options
        makeFile("git/objects", "index");

        deleteDirectory("git", "HEAD"); //in case "cloggs" options
        makeFile("git/objects", "HEAD");
    }

    public static void blobify(String filePath, String fileName){
        String content = readFile(filePath, fileName);
        String hash = hash(content);
        makeFile("git/objects", hash);
        writeToFile("git/objects", hash, content);
    }

    public static boolean alreadyIndexed(String indexEntry){
        indexEntry = indexEntry.replace("\n", "");
        return readFile("git/objects", "index").contains(indexEntry);
        //If I cared, I could implement Boyer-Moore or KMP to make this fast
    }

    public static boolean isIndexEmpty(){
        return readFile("git/objects", "index").equals("");
    }

    public static void index(String filePath, String fileName){ //assumes not already there!
        String content = readFile(filePath, fileName);
        String hash = hash(content);    
        String addition = (isIndexEmpty() ? "" : "\n") + hash + " " + getPath(filePath, fileName);
        if (alreadyIndexed(addition)){
            return;
        }
        appendToFile("git/objects", "index", addition);
    }

    public static void main(String[] args){
        intializeRepo();
        blobify("git/objects", "HEAD");
        index("git/objects", "HEAD");
    }
}
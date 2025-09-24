import java.io.BufferedReader;
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
        return newDir.exists();
    }

    public static boolean fileExists(String filePath, String fileName){
        String path = getPath(filePath, fileName);
        File f = new File(path);
        return f.exists();
    }

    public static void makeDirectory(String filePath, String folderName){
        if (directoryExists(filePath, folderName)){
            return;
        }
        String path = (filePath == null ? "" : filePath + "/") + folderName;
        File newDir = new File(path);
        newDir.mkdir();
    }

    public static void makeFile(String filePath, String fileName){
        if (fileExists(filePath, fileName)){
            return;
        }
        String path = (filePath == null ? "" : filePath + "/") + fileName;
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (Exception e){
            e.printStackTrace();
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
        if (fileExists("git/objects", "index") && fileExists("git/objects", "HEAD")){
            System.out.println("Git Repository Already Exists");
            return;
        }
        makeDirectory(null, "git");
        makeDirectory("git", "objects");
        makeFile("git/objects", "index");
        makeFile("git/objects", "HEAD");
        System.out.println("Git Repository Created");
    }

    public static void blobify(String filePath, String fileName){
        String content = readFile(filePath, fileName);
        String hash = hash(content);
        makeFile("git/objects", hash);
        writeToFile("git/objects", hash, content);
    }

    public static void main(String[] args){
        intializeRepo();
        System.out.println(hashFile("git/objects", "index"));
        blobify("git/objects", "index");
    }
}
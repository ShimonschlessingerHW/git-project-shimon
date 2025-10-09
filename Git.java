import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashSet;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.time.*;
import java.time.format.*;

//LOOK AT TOP OF README FOR AN IMPORTANT EXPLANATION!

public class Git {
    public static final boolean COMPRESSING = !true;
    
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
    //IF THE HEAD EVER HAS STUFF OTHER THAN THE LATEST COMMIT, EDIT THIS METHOD
    public static String retrieveLatestCommit(){
        return readFile("git", "HEAD");
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

    public static void cleanGit(){
        deleteDirectory(null, "git");
        intializeRepo();
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

    public static String readFileCompressed(String filePath, String fileName){
        return null;
    }

    public static void writeToFile(String filePath, String fileName, String content){
        String path = getPath(filePath, fileName);
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFileCompressed(String filePath, String fileName, String content){

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
        if (COMPRESSING){
            content = compress(content);
        }
        writeToFile("git/objects", hash, content);
    }

    public static boolean alreadyIndexed(String str){
        return readFile("git/objects", "index").contains(str);
    }

    public static boolean isIndexEmpty(){
        return readFile("git/objects", "index").equals("");
    }

    public static void index(String filePath, String fileName){ //assumes not already there!
        String path = getPath(filePath, fileName);
        String entry = hashFile(filePath, fileName) + " " + path;
        if (alreadyIndexed(entry)){
            //already entirely in there; do nothing
        } else if (alreadyIndexed(path)){
            //updating file
            String indexFile = readFile("git/objects", "index");
            int pathIndex = indexFile.indexOf(path);
            int hashIndex = indexFile.substring(0, pathIndex).lastIndexOf("\n") + 1;
            String newFile = indexFile.substring(0, hashIndex) + entry + indexFile.substring(pathIndex + path.length());
            writeToFile("git/objects", "index", newFile);
        } else {
            if (!isIndexEmpty()){
                appendToFile("git/objects", "index", "\n");
            }
            appendToFile("git/objects", "index", entry);
        }
    }

    public static String compress(String input) {
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        Deflater deflater = new Deflater();
        deflater.setInput(inputBytes);
        deflater.finish();
        byte[] buffer = new byte[1024];
        int compressedLength = deflater.deflate(buffer);
        deflater.end();
        return Base64.getEncoder().encodeToString(java.util.Arrays.copyOf(buffer, compressedLength));
    }

    public static String decompress(String compressedBase64) {
        byte[] compressedBytes = Base64.getDecoder().decode(compressedBase64);
        Inflater inflater = new Inflater();
        inflater.setInput(compressedBytes);
        byte[] buffer = new byte[1024];
        try {
            int decompressedLength = inflater.inflate(buffer);
            inflater.end();
            return new String(buffer, 0, decompressedLength, StandardCharsets.UTF_8);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public static String makeTree(String filePath, String fileName){ //returns hash value
        String path = getPath(filePath, fileName);
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()){
            throw new IllegalArgumentException();
        }
        StringBuilder sb = new StringBuilder();
        for (File subfile : dir.listFiles()) {
            String subpath = subfile.getParent();
            String subname = subfile.getName();
            String newEntry = new String();
            if (subfile.isFile()){
                blobify(subpath, subname);
                newEntry = "blob " + hashFile(subpath, subname) + " " + getPath(subpath, subname);
            } else if (subfile.isDirectory()){
                String subtreeHash = makeTree(subpath, subname);
                newEntry = "tree " + subtreeHash + " " + getPath(subpath, subname);
            }
            sb.append(newEntry);
            sb.append("\n");
        }
        sb.deleteCharAt(sb.length() - 1); //removes terminal new line
        
        String treeContents = sb.toString();
        String treeHash = hash(treeContents);
        makeFile("git/objects", treeHash);
        writeToFile("git/objects", treeHash, treeContents);
        return treeHash;
    }

    //IGNORE: Incorrect programming attempt
    
    // public static void makeIndexTree(){
    //     String indexFile = readFile("git/objects", "index");
    //     String[] entriesArr = indexFile.split("\n");
    //     Stack<String> entries = new Stack<String>();
    //     for (int i = 0; i < entriesArr.length; i++){
    //         entries.add("blob " + entriesArr[i]);
    //     }
    //     entries.sort(Comparator.comparing((String s) -> s.split(" ")[2]).reversed());        
        
    //     while (entries.size() > 0){
    //         //above sorts by depth (ie. number of /s) to make sure subfolders get done first.
    //         String topPath = entries.peek().split(" ")[2];
    //         if (topPath.lastIndexOf("/") == -1){
    //             //no subdirectories; make root file for it
    //             //MAKE ROOT FILE
    //             String rootEntry = entries.pop();
    //             String blobName = hash(rootEntry);
    //             makeFile(null, blobName);
    //             writeToFile(null, blobName, rootEntry);
    //         }
    //         String newDir = topPath.substring(0, topPath.lastIndexOf("/"));
    //         StringBuilder treeFile = new StringBuilder();
    //         while (entries.size() > 0){
    //             String nextPath = entries.peek().split(" ")[2];
    //             if (!nextPath.contains(newDir)){
    //                 break;
    //             }
    //             treeFile.append(entries.pop());
    //             treeFile.append("\n");
    //         }
    //         if (treeFile.length() > 0){
    //             treeFile.deleteCharAt(treeFile.length() - 1);
    //         }
    //         String treeContent = treeFile.toString();
    //         String blobName = hash(treeContent);
    //         makeFile("git/objects", blobName);
    //         writeToFile("git/objects", blobName, treeContent);
    //         entries.push("tree " + blobName + " " + newDir);
    //     }
    // }

    //NOTE: Does not automatically BLOB everything inside of it!
    //That should have been done when indexed anyway.
    public static String makeIndexTree(){
        StringBuilder rootTreeContents = new StringBuilder();
        String indexFile = readFile("git/objects", "index");
        String[] entriesArr = indexFile.split("\n");
        ArrayList<String> entries = new ArrayList<String>();
        HashSet<String> directories = new HashSet<String>();
        for (int i = 0; i < entriesArr.length; i++){
            String path = entriesArr[i].split(" ")[1]; //no tree/blob prefix yet
            if (path.contains("/")){
                String directory = path.substring(0, path.indexOf("/"));
                directories.add(directory);
                entries.add("blob " + entriesArr[i]);
            } else { //is a file
                rootTreeContents.append("blob " + entriesArr[i]);
                rootTreeContents.append("\n");
            }
        }
        for (String directory : directories){
            String treeHash = makeIndexTreeHelper(entries, directory);
            rootTreeContents.append("tree " + treeHash + " " + directory);
            rootTreeContents.append("\n");
        }
        if (rootTreeContents.length() > 0){
            rootTreeContents.deleteCharAt(rootTreeContents.length() - 1);
        }
        String contents = rootTreeContents.toString();
        String hash = hash(contents);
        makeFile(null, hash);
        writeToFile(null, hash, contents);
        return hash;
    }

    //returns tree hash
    public static String makeIndexTreeHelper(ArrayList<String> entries, String directoryPrefix){
        ArrayList<String> subentries = new ArrayList<String>();
        for (String entry : entries){
            String path = entry.split(" ")[2];
            if (path.contains(directoryPrefix)){
                subentries.add(entry);
            }
        }
        HashSet<String> treeEntryRows = new HashSet<String>(); //get only unqiue adds
        for (String subentry : subentries){
            String subpath = subentry.split(" ")[2].substring(directoryPrefix.length() + 1);
            if (subpath.contains("/")){ //a directory
                String firstFolder = subpath.substring(0, subpath.indexOf("/"));
                String subTreeHash = makeIndexTreeHelper(subentries, directoryPrefix + "/" + firstFolder);
                treeEntryRows.add("tree " + subTreeHash + " " + directoryPrefix + "/" + firstFolder);
            } else { //a file
                treeEntryRows.add(subentry); //already formatted nicely
            }
        }
        StringBuilder entryContentSB = new StringBuilder();
        for (String s : treeEntryRows){
            entryContentSB.append(s);
            entryContentSB.append("\n");
        }
        if (entryContentSB.length() > 0){
            entryContentSB.deleteCharAt(entryContentSB.length() - 1);
        }
        String entryContent = entryContentSB.toString();
        String treeHash = hash(entryContent);
        makeFile("git/objects", treeHash);
        writeToFile("git/objects", treeHash, entryContent);
        return treeHash;
    }
    public static String makeCommit(){
        StringBuilder outputSB = new StringBuilder();
        outputSB.append("tree: "+makeIndexTree()+"\n");
        outputSB.append("parent: "+retrieveLatestCommit()+"\n");
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter author name:");
        outputSB.append("author: "+sc.nextLine()+"\n");
        sc.reset();
        //i hope geeksForGeeks was right about what this code does
        outputSB.append("date: "+ LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + "\n");
        System.out.println("Enter commit message:");
        outputSB.append("message: "+sc.nextLine());
        sc.close();
        String commitHash = hash(outputSB.toString());
        makeFile("git/objects", commitHash);
        writeToFile("git/objects", commitHash, outputSB.toString());
        // change this if more info needs to be stored on the HEAD
        writeToFile("git", "HEAD", commitHash);
        return commitHash;
    }
    public static void main(String[] args){
        cleanGit();
        intializeRepo();
        index("A/B/D", "f3");
        index("A/B", "f2");
        index("A/C", "f4");
        index("A", "f1");
        index(null, ".gitignore");
        makeIndexTree();   
    }
}
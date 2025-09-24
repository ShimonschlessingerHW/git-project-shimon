import java.io.File;

public class Git {
    public static boolean directoryExists(String filePath, String folderName){
        String path = (filePath == null ? "" : filePath + "/") + folderName;
        File newDir = new File(path);
        return newDir.exists();
    }

    public static boolean fileExists(String filePath, String fileName){
        String path = (filePath == null ? "" : filePath + "/") + fileName;
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
    public static void main(String[] args){
        intializeRepo();
    }
}
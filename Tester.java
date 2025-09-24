public class Tester {
    public static boolean initializedContainsEverything(){
        if (!Git.directoryExists(null, "git")){
            return false;
        }
        if (!Git.directoryExists("git", "objects")){
            return false;
        }
        if (!Git.fileExists("git/objects", "index")){
            return false;
        }
        if (!Git.fileExists("git/objects", "HEAD")){
            return false;
        }
        return true;
    }
    
    public static void initializationTester(){
        System.out.print("Initialization Test: ");
        //clear test
        Git.deleteDirectory(null, "git");
        Git.intializeRepo();
        if (!initializedContainsEverything()){
            System.out.println("FAILED 1");
            return;
        }

        //missing only HEAD
        Git.deleteFile("git/objects", "HEAD");
        Git.intializeRepo();
        if (!initializedContainsEverything()){
            System.out.println("FAILED 2");
            return;
        }

        //missing objects dir
        Git.deleteDirectory("git", "objects");
        Git.intializeRepo();
        if (!initializedContainsEverything()){
            System.out.println("FAILED 3");
            return;
        }

        //replaced objects dir with file named "objects"
        Git.deleteDirectory("git", "objects");
        Git.makeFile("git", "objects");
        Git.intializeRepo();
        if (!initializedContainsEverything()){
            System.out.println("FAILED 4");
            return;
        }
        
        //reinitialize
        Git.intializeRepo();
        System.out.println("PASSED");
    }

    public static void main(String[] args){
        initializationTester();
    }
}

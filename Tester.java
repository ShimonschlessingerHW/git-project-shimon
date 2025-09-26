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

    public static void hashTester(){
        System.out.print("Hash Tester: ");
        //Correct answers from https://emn178.github.io/online-tools/sha1.html
        Git.writeToFile("git/objects", "index", "");
        if (!Git.hashFile("git/objects", "index").equals("da39a3ee5e6b4b0d3255bfef95601890afd80709")){
            System.out.println("FAILED 1");
            return;
        }

        Git.writeToFile("git/objects", "index", "a different string");
        if (!Git.hashFile("git/objects", "index").equals("eb0108c532f81f16e01cf9470d6c1cd45311bff5")){
            System.out.println("FAILED 2");
            return;
        }

        Git.writeToFile("git/objects", "index", "assuredly not a coincidence by now!");
        if (!Git.hashFile("git/objects", "index").equals("dcc3d4d5ac0f4bf2033486141642035086d7a2b7")){
            System.out.println("FAILED 3");
            return;
        }

        //reset index file
        Git.writeToFile("git/objects", "index", "");
        System.out.println("PASSED");

    }

    public static void blobTester(){
        System.out.print("BLOB Tester: ");
        //make test files
        Git.makeDirectory(null, "testFiles");
        Git.makeFile("testFiles", "file1");
        Git.makeFile("testFiles", "file2");
        Git.makeFile("testFiles", "file3");
        //file1 stays blank
        Git.writeToFile("testFiles", "file2", "hello");
        Git.writeToFile("testFiles", "file3", "SOMETHING ELSE?!");

        //test 1
        Git.blobify("testFiles", "file1");
        String expectedHash = Git.hash("");
        if (!Git.fileExists("git/objects", expectedHash) || !Git.readFile("git/objects", expectedHash).equals("")){
            Git.deleteDirectory(null, "testFiles");
            System.out.println("FAILED 1");
            return; 
        }

        //test 2
        Git.blobify("testFiles", "file2");
        expectedHash = Git.hash("hello");
        if (!Git.fileExists("git/objects", expectedHash) || !Git.readFile("git/objects", expectedHash).equals("hello")){
            Git.deleteDirectory(null, "testFiles");
            System.out.println("FAILED 2"); 
            return;
        }

        //test 3
        Git.blobify("testFiles", "file3");
        expectedHash = Git.hash("SOMETHING ELSE?!");
        if (!Git.fileExists("git/objects", expectedHash) || !Git.readFile("git/objects", expectedHash).equals("SOMETHING ELSE?!")){
            Git.deleteDirectory(null, "testFiles");
            System.out.println("FAILED 3"); 
            return;
        } 

        Git.deleteDirectory(null, "testFiles");
        System.out.println("PASSED");
    }

    public static void indexTester(){
        System.out.print("Index Tester: ");
        //make test files
        Git.makeDirectory(null, "testFiles");
        Git.makeFile("testFiles", "file1");
        Git.makeFile("testFiles", "file2");
        Git.makeFile("testFiles", "file3");
        //file1 stays blank
        Git.writeToFile("testFiles", "file2", "hello");
        Git.writeToFile("testFiles", "file3", "SOMETHING ELSE?!");

        //test 1
        Git.index("testFiles", "file1");
        String indexEntry = Git.hash("") + " testFiles/file1";
        if (!Git.alreadyIndexed(indexEntry)){
            Git.deleteDirectory(null, "testFiles");
            System.out.println("FAILED 1"); 
            return;
        }

        //test 2
        Git.index("testFiles", "file2");
        indexEntry = Git.hash("hello") + " testFiles/file2";
        if (!Git.alreadyIndexed(indexEntry)){
            Git.deleteDirectory(null, "testFiles");
            System.out.println("FAILED 2"); 
            return;
        }

        //test 3
        Git.index("testFiles", "file3");
        indexEntry = Git.hash("SOMETHING ELSE?!") + " testFiles/file3";
        if (!Git.alreadyIndexed(indexEntry)){
            Git.deleteDirectory(null, "testFiles");
            System.out.println("FAILED 3");
            return;
        } 

        Git.deleteDirectory(null, "testFiles");
        System.out.println("PASSED");
    }

    public static void main(String[] args){
        initializationTester();
        hashTester();
        blobTester();
        indexTester();
    }
}

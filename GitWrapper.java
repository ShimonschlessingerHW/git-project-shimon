import java.nio.file.Paths;
import java.time.*;
import java.time.format.*;
public class GitWrapper {
    /**
     * Initializes a new Git repository.
     * This method creates the necessary directory structure
     * and initial files (index, HEAD) required for a Git repository.
     */
    public void init() {
        Git.intializeRepo();
    };

    /**
     * Stages a file for the next commit.
     * This method adds a file to the index file.
     * If the file does not exist, it throws an IOException.
     * If the file is a directory, it throws an IOException.
     * If the file is already in the index, it does nothing.
     * If the file is successfully staged, it creates a blob for the file.
     * @param filePath The path to the file to be staged.
     */
    public void add(String filePath) {
        //Shimone, this is what you get when you split the parent path, and file name
        String parentPath = Paths.get(filePath).getParent().toString();
        String childPath = Paths.get(filePath).getFileName().toString();
        Git.index(parentPath, childPath);
        Git.blobify(parentPath, childPath);
    };

    /**
     * Creates a commit with the given author and message.
     * It should capture the current state of the repository by building trees based on the index file,
     * writing the tree to the objects directory,
     * writing the commit to the objects directory,
     * updating the HEAD file,
     * and returning the commit hash.
     * 
     * The commit should be formatted as follows:
     * tree: <tree_sha>
     * parent: <parent_sha>
     * author: <author>
     * date: <date>
     * summary: <summary>
     *
     * @param author  The name of the author making the commit.
     * @param message The commit message describing the changes.
     * @return The SHA1 hash of the new commit.
     */
    public String commit(String author, String message) {
        //this is literally a ctrl c + ctrl v without the scanner
        StringBuilder outputSB = new StringBuilder();
        outputSB.append("tree: "+Git.makeIndexTree()+"\n");
        outputSB.append("parent: "+Git.retrieveLatestCommit()+"\n");
        outputSB.append("author: "+author+"\n");
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter validDateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        outputSB.append("date: "+ localDateTime.format(validDateTimeFormatter) + "\n");
        outputSB.append("message: "+message);
        String commitHash = Git.hash(outputSB.toString());
        Git.makeFile("git/objects", commitHash);
        Git.writeToFile("git/objects", commitHash, outputSB.toString());
        // change this if more info needs to be stored on the HEAD
        Git.writeToFile("git", "HEAD", commitHash);
        return commitHash;
    };

     /**
     * EXTRA CREDIT:
     * Checks out a specific commit given its hash.
     * This method should read the HEAD file to determine the "checked out" commit.
     * Then it should update the working directory to match the
     * state of the repository at that commit by tracing through the root tree and
     * all its children.
     *
     * @param commitHash The SHA1 hash of the commit to check out.
     */
    public void checkout(String commitHash) {
        // to-do: implement functionality here

    };
}
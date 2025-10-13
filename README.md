Bad Design Choices:
For some awful reason, I chose to always pass two parameters to each function for the file path (sorry!). They are called "filePath" and "fileName/folderName."
The file path refers to how to get *to* the file, and the file/folder name refers to the name of said file itself. If the file is in the root directory, "filePath" is null. 
Here are some examples:
"git/objects/index" --> filePath: "git/objects" and fileName: "index"
".gitignore" --> filePath: null and fileName: ".gitignore"
"git/objects" --> filePath: "git" and folderName: "objects"

To get from these two parameters to the full name used in the File class, use the "getPath" method.
In the opposite direction, given a full file path, if you need to extract these two parameters, use the .getParent() and .getName() methods already built into the File class.

--

GP 2.1

Directory Exists: Takes a file path and the directory name and checks existence
File Exists: Ditto with files of directories

Make Directory: Takes a file path and the directory and makes it IF not already there
Make File: Ditto with files instead of directory

Initialize Repo: If they don't exist, makes a "git" directory containing an "objects" direcotry with two files "index" and "HEAD." If they all already exists, it logs "Git Repository Already Exists" 

--

GP 2.2

Hash: Outputs the SHA1 hash from the String representing data.
Hash File: Outputs the SHA1 hash from the relevant file path.

--

GP 2.3

Write to File: Takes filePath and string and writes data to the file.

Blobbify: Takes filePath to a file with data and makes BLOB file: in object folder, have a BLOB file whose name is its hash with its contents.

--

GP 2.1.1

intializationTester: Run to test if Git.initializeRepo() works correctly.
Fixed a bug in initializeRepo to ensure that if, e.g, a file were named "objects," it would be replaced by the directory "objects" as desired

--

GP 2.2.1

hashTester: tests various different file value on the "index" file and running the hashFile method on it.

--

GP 2.4

index: takes in filePath to an ORIGINAL text file and updates the index fiel with its BLOB. Uses a helper method "alreadyIndexed" to detect if this entry has already been made ot prevent duplicate entries.

--

GP 2.3.1

blobTester: Makes a test folder with three subfiles and BLOBs them. Then programmatically verifies that they exist in the objects folder with the correct title and body.

--

GP 2.4.1

indexTester: Does the exact same thing as blobTester but verifies everything exists in hte index file

--

GP 2.4.2

cleanGit: Fully resets the git directory, removing all objects fiels and resetting the HEAD and index file. Does this by just deleting the whole folder and remaking it. Resets git folder to have empty index and HEAD files as well as deleting all extraneous BLOB files.

-- 

GP 2.3.2

compress: takes String and outputs a compressed version of it using zip
decompress: takes a compressed String and returns the original

--

GP 3.2

makeTree: from file path, recursively creates tree by makign subtrees and blobifying subfiles. returns the name of the hash!

--

GP 4.1

makeIndexTree now returns the hash of the root tree

--

GP 4.2

makeCommit: Generates a new commit file by calling makeIndexTree to make a new root hash and prompting the user for the author name and commit message. appends the HEAD with the commit's hash
retriveLastCommit: reads the HEAD (which shoudl have the most recent commit hash) CHANGE THIS IF ANY OTHER STUFF IS IN THE HEAD FILE

--

GP 4.3

GitWrapper - new wrapper class for Git
init: calls Git.initRepo
add: indexes and blobs the filepath given
commit: copy of Git.makeCommit without the scanner
checkout: WIP (I'm not quite sure how it's supposed to work)

--

Final Testing + Extras

FIXED: HEAD is no longer in the objects folder
FIXED: Date no longer throws exceptions when making commits
full wraper test implemented
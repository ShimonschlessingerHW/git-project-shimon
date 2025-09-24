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
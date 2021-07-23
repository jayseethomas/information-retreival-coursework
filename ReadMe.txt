
*Make sure Terminal (or what is used on PC) has full access to 'folder'*
1. Move all files to one 'folder'
	- all java files
	- invertedindex.ser, porterindex.ser, locindex.ser, porterlocindex.ser, QueryFile, QueryResults


3. Go to terminal / command line (no idea how you do it on a pc)

4. Type: cd "folder-path-here/"
	- make sure to  include the quotes "" if path has spaces 
	- make sure to end  with a /
	- i.e : cd "folder-path/"

5. Type: javac SearchEngine.java


6. Type: java SearchEngine -InvertedIndexSer "<path>invertedindex.ser.txt" -PorterIndexSer "<path>porterindex.ser.txt" -LocIndexSer "<path>locindex.ser.txt" -PorterLocIndexSer "<path>porterlocindex.ser.txt" -Queries "<path>QueryFile.txt" -Result "<path>QueryResults.txt" -PorterFlag "Yes/No" -InvertedIndex "<path>invertedindex.txt" -PorterIndex "<path>porterindex.txt" 

	6a. i.e : java SearchEngine -InvertedIndex "invertedindex.ser.txt" -PorterIndex "porterindex.ser.txt" -LocIndex "locindex.ser.txt" -PorterLocIndex "porterlocindex.ser.txt" -Queries "QueryFile.txt" -Result "QueryResults.txt" -PorterFlag "Yes" -InvertedIndex "invertedindex.txt" -PorterIndex "porterindex.txt" 


	6b. Must pass the serialized files into the program as this version does not compute the whole thing as the first phase.


7. To do a query go to 'QueryFile.txt'
	- follow the convention of <Action> <Term> 
	- each action should be on a new line
	- max 100 queries / actions
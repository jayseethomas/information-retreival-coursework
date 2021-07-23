import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;



public class SearchEngine implements Serializable {
	
	
	
	public static PorterStemmer ps = new PorterStemmer();
	




	stopWords stoplist = new stopWords();

	// <word, <docid, freq in docc id> > = index and porter index
	// <word, <docid, loc in doc id> > = locindex and porter location index

	static HashMap<String, Map<String, Integer>> index;
	static HashMap<String, Map<String, String>> locindex;

	static HashMap<String, Map<String, Integer>> Porterindex;
	static HashMap<String, Map<String, String>> porterLocindex;



	SearchEngine() {
		index = new HashMap<String, Map<String, Integer>>();
		locindex = new HashMap<String, Map<String, String>>();
		Porterindex = new HashMap<String, Map<String, Integer>>();
		porterLocindex = new HashMap<String, Map<String, String>>();

	}
	
	
	
	// buildIndex is no longer used as our index is built and we just read in and out to an sere file at the start of the program
	// I have left it in here because in phase2 serelization was a requiement 
	// and that is showed in the buildIndex method 
	// It however is never called

	public void buildIndex(String stringss, String invertedIndex, String porterIndex2) throws IOException {

		// 1. create new file processing object
		fileProcessing hw = new fileProcessing();
		// 2. create var docNum that holds which document number we are processing at
		// the moment
		// from 1-200 representing our 200 corpus
		int docNum = 1;

		// 3. create a string array of 200 named fileNames
		// it will hold the 200 filenames we have to index
		// get the names of the files via a loop suing the directory path
		String[] fileNames = new String[200];
		for (int i = 1; i <= fileNames.length; i++) {
			fileNames[i - 1] = (stringss + i + ".html");
			// System.out.println(i);
		}

		// 4. for each file in array fileNames
		for (String file : fileNames) {

			// a. read file in and store it in a string called file
			String fileContent = hw.readAllBytesJava7(file);

			// b. use regex to try and parse the html in the string
			fileContent = fileContent.replaceAll("\\n", " ");
			fileContent = fileContent.replaceAll("<script.*?>.*?<\\/script>", " ");
			fileContent = fileContent.replaceAll("<noscript.*?>.*?<\\/noscript>", " ");
			fileContent = fileContent.replaceAll("<style.*?>.*?<\\/style>", " ");
			fileContent = fileContent.replaceAll("\\d", " ");

			// fileContent = fileContent.replaceAll("<a.*?>\\n*?<\\/a>", " ");
			// fileContent = fileContent.replaceAll("href=\".*?>\\n*?\"", " ");

			fileContent = fileContent.replaceAll("\\<(.+?)\\>", " ");
			fileContent = fileContent.replaceAll("&.*?;", " ");
			fileContent = fileContent.replaceAll("[!\"\\#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]^_‘{|}~—]+", " ");
			fileContent = fileContent.replaceAll("©", " ");

			fileContent = fileContent.trim().replaceAll("/\\s\\s+/g", " ");

			// c. split the string at every not word to create an array that holds
			// every word of the file
			String[] fileWords = fileContent.split("\\W+");

			// System.out.println(fileWords[0]);

			// d. for every word in fileWords
			int j = 1; // location counter 

			for (String word : fileWords) {
								

				// 1a. convert to lowercase to make matching easy and make sure word is not all
				// whitespace
				if (word.trim().length() > 0) {
					word = word.toLowerCase();
					// System.out.println(word);

					// 1b. check if word is in the stoplist
					if ((stoplist.h.contains(word)) == false) {
						// 1c. if not in the stop list check to see if the word (term) exists in the
						// index
						// if it does not it means it is the first instance of that word (term)
						// create a index value variable (stores string that says which document the key
						// is found in, and an int for frequency of the key in that document)
						Map<String, Integer> indexValue = index.get(word);
						Map<String, String> indexValue2 = locindex.get(word);


						if (indexValue == null && indexValue2 == null) {
							// 1d. check to see if the word has ever been found if not add it to index list
							// create a new hashmap for indexValue
							// add the term to index
							indexValue = new HashMap<>();
							indexValue2 = new HashMap<>();
							index.put(word, indexValue);
							locindex.put(word, indexValue2);
							
						}

						// 1e. check to see if the word has been found in this document before
						// it will be a new word of this document if it's currentFreq is null
						// if first instance initialize freq to 0
						String freqKey = String.valueOf(docNum);
						Integer currentFreq = indexValue.get(freqKey);
						String currentLoc = indexValue2.get(freqKey);

						Integer Freq;
						String locString = " ";
						if (currentFreq == null && currentLoc == null) {
							Freq = 0;
							locString = Integer.toString(j);
							
						} else {
							Freq = currentFreq;
							locString= locString.concat(" ").concat(Integer.toString(j));

						}
						// 1f. update the frequency in the index
						indexValue.put(freqKey, (Freq + 1));
						indexValue2.put(freqKey, locString);

					}
				}
				
				j++; // increment location counter 
				//System.out.println(j);
			}
			// d. increment the document number
			docNum++;

			// System.out.println(Arrays.asList(index)); // method 1

		}

		/*
		 * for (Entry<String, Map<String, Integer>> entry : index.entrySet()) {
		 * System.out.println(entry.getKey() + ":" + entry.getValue().toString());
		 * System.out.println("docs where " + entry.getKey() + " are index : " +
		 * entry.getValue().keySet().toString()); System.out.println( "frequency of " +
		 * entry.getKey() + "in documents :" + entry.getValue().values().toString());
		 * Object[] s = entry.getValue().values().toArray(); int sum = 0; for (int k =
		 * 0; k < s.length; k++) { sum = sum + (int) s[k]; }
		 * 
		 * System.out.println("total frequency of " + entry.getKey() + " : " + sum);
		 * 
		 * }
		 */

		BufferedWriter outfile3 = new BufferedWriter(new FileWriter(invertedIndex));
		outfile3.write(String.valueOf("term") + " ");
		outfile3.write(String.valueOf("\t"));
		outfile3.write(String.valueOf("document") + " ");
		outfile3.write("\n");

		for (Entry<String, Map<String, Integer>> entry : index.entrySet()) {
			outfile3.write(String.valueOf(entry.getKey() + " "));
			outfile3.write(String.valueOf("\t"));
			outfile3.write(entry.getValue().keySet().toString() + " ");
			outfile3.write("\n");

		}

		outfile3.close();
		
		// System.out.println(locindex);
		
		buildPorter(index,porterIndex2);



	}

	private void buildPorter(HashMap<String, Map<String, Integer>> index2, String porterIndex2) throws IOException {
		
		// takes the invertedindex and applies porter's algo
		for (Entry<String, Map<String, Integer>> entry : index.entrySet()) {
			String w = String.valueOf(entry.getKey()) ;
			String wstemmed = ps.stemWord(w);
			Map<String, Integer> indexValue = Porterindex.get(wstemmed);

			if (indexValue == null) {
				// 1d. check to see if the word has ever been found if not add it to index list
				// create a new hashmap for indexValue
				// add the term to index
				indexValue = new HashMap<>();
				Porterindex.put(wstemmed, entry.getValue());
				
			}
			
			indexValue.putAll(entry.getValue());
	

		}
		
		// saves porter's algorithm n invertedindeex to a file
		BufferedWriter outfile4 = new BufferedWriter(new FileWriter(porterIndex2));
		outfile4.write(String.valueOf("term") + " ");
		outfile4.write(String.valueOf("\t"));
		outfile4.write(String.valueOf("document") + " ");
		outfile4.write("\n");

		for (Entry<String, Map<String, Integer>> entry : Porterindex.entrySet()) {
			outfile4.write(String.valueOf(entry.getKey() + " "));
			outfile4.write(String.valueOf("\t"));
			outfile4.write(entry.getValue().keySet().toString() + " ");
			outfile4.write("\n");

		}

		outfile4.close();
		
		
		//System.out.println(Porterindex);
		
		
		// takes locationindex and applies porter's algorithm and stores in porterLocationindex
		for (Entry<String, Map<String, String>> entry : locindex.entrySet()) {
			String w = String.valueOf(entry.getKey()) ;
			String wstemmed = ps.stemWord(w);
			Map<String, String> indexValue = porterLocindex.get(wstemmed);

			if (indexValue == null) {
				// 1d. check to see if the word has ever been found if not add it to index list
				// create a new hashmap for indexValue
				// add the term to index
				indexValue = new HashMap<>();
				porterLocindex.put(wstemmed, entry.getValue());
				
			}
			
			indexValue.putAll(entry.getValue());
	

		}
		
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		


		// process the flags in the terminal
		int i = 0;
		int j;
		String arg;
		String InvertedIndexSer = "";
		String PorterIndexSer = "";
		String LocIndexSer = "";
		String PorterLocIndexSer = "";

		
		String InvertedIndex = "";
		String PorterIndex = "";

		
		
		String Queries = "";
		String Results = "";
		
		String solvePString = "";

		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i];
			i++;

			if (arg.equals("-InvertedIndexSer")) {
				InvertedIndexSer = args[i];
				i++;
			} else if (arg.equals("-PorterIndexSer")) {
				PorterIndexSer = args[i];
				i++;
			} else if (arg.equals("-LocIndexSer")) {
				LocIndexSer = args[i];
				i++;
			} else if (arg.equals("-PorterLocIndexSer")) {
				PorterLocIndexSer = args[i];
				i++;
			} else if (arg.equals("-Queries")) {
				Queries = args[i];
				i++;
			}
			else if(arg.equals("-Result")) {
				Results = args[i];
				i++;
			}
			else if(arg.equals("-PorterFlag")) {
				solvePString = args[i];
				i++;
				
			} else if (arg.equals("-InvertedIndex")) {
				InvertedIndex = args[i];
				i++;
			} else if (arg.equals("-PorterIndex")) {
				PorterIndex = args[i];
				i++;
			}else {
				System.out.println(arg + " is an invalid flag");
				i++;

			}

		}

		// 1. create  new searchEngine obj
		SearchEngine se = new SearchEngine();
		
		/* these are from phase 1 
		 * se.buildIndex(CorpusDir, InvertedIndex, PorterIndex);
		 * se.callSere();
		 */
		
		
		// 2. deserelize the files and store in their proper objects
		se.Unsere(InvertedIndexSer,PorterIndexSer,LocIndexSer,PorterLocIndexSer);
		
		// 3. print inverted index and porter index 
		se.printInvertAndPorter(InvertedIndex,PorterIndex);

		
		// 4. decide if solving via porter or not and process the queries and then store results
		if(solvePString.equalsIgnoreCase("yes") )
		{
			//  porter 
			se.solveAQueryPorter(Queries, Results);
			 
		}
		else {
			// no porter 
			se.solveAQuery(Queries, Results);

			
		}
		
		

	}

	private void printInvertAndPorter(String invertedIndex, String porterIndex2) throws IOException {
		// WRITES OUT PORTERINDEX
		BufferedWriter outfile4 = new BufferedWriter(new FileWriter(porterIndex2));
		outfile4.write(String.valueOf("term") + " ");
		outfile4.write(String.valueOf("\t"));
		outfile4.write(String.valueOf("document") + " ");
		outfile4.write("\n");

		for (Entry<String, Map<String, Integer>> entry : Porterindex.entrySet()) {
			outfile4.write(String.valueOf(entry.getKey() + " "));
			outfile4.write(String.valueOf("\t"));
			outfile4.write(entry.getValue().keySet().toString() + " ");
			outfile4.write("\n");

		}

		outfile4.close();

		
		
		// WRITES OUT INVERTEDINDEX
		BufferedWriter outfile3 = new BufferedWriter(new FileWriter(invertedIndex));
		outfile3.write(String.valueOf("term") + " ");
		outfile3.write(String.valueOf("\t"));
		outfile3.write(String.valueOf("document") + " ");
		outfile3.write("\n");

		for (Entry<String, Map<String, Integer>> entry : index.entrySet()) {
			outfile3.write(String.valueOf(entry.getKey() + " "));
			outfile3.write(String.valueOf("\t"));
			outfile3.write(entry.getValue().keySet().toString() + " ");
			outfile3.write("\n");

		}

		outfile3.close();
		
	}



	private void Unsere(String invertedIndex, String porterIndex2, String locIndex2, String porterLocIndex2) throws ClassNotFoundException, IOException {
		/*	
		 * the follow codes create the objects of the
		 * invertedindex,portersindex,locationindex,and locationporterindex
		 * from a serelization file
		 * 
		 */
		
		
		// inverted index
		HashMap<String, Map<String, Integer>> newinvertedindex = null;	
		newinvertedindex = FileDesere1(invertedIndex, newinvertedindex);
		SearchEngine.index = newinvertedindex;
        //System.out.println(SearchEngine.index);
        
        
        // porter index
        HashMap<String, Map<String, Integer>> newporterindex = null;	
		newporterindex = FileDesere1(porterIndex2, newporterindex);
		SearchEngine.Porterindex = newporterindex;
        //System.out.println(SearchEngine.Porterindex);
        
        
        // location index
	     HashMap<String, Map<String, String>> newlocindex = null;	
	     newlocindex = FileDesere2(locIndex2, newlocindex);
	     SearchEngine.locindex = newlocindex;
	     //System.out.println(SearchEngine.locindex);

		// porter location index
	     HashMap<String, Map<String, String>> newplocindex = null;	
	     newplocindex = FileDesere2(porterLocIndex2, newplocindex);
	     SearchEngine.porterLocindex = newplocindex;
	     //System.out.println(SearchEngine.porterLocindex);

	}



	private void callSere() throws IOException, ClassNotFoundException {
		/*	
		 * the follow codes create the serialization objects of the
		 * invertedindex,portersindex,locationindex,and locationporterindex
		 * 
		 * 
		 * the hashmap class is serelized by default and i use this tutoria
		 * https://javahungry.blogspot.com/2017/11/how-to-serialize-hashmap-in-java-with-example.html
		 * 
		 */
		
		
		// inverted index sere
		String serializeFileName = "invertedindex.ser.txt";
		FileSere1(serializeFileName, SearchEngine.index); // for inverted index and porters index
			
	        
	     // porter index sere
	     serializeFileName = "porterindex.ser.txt";
	     FileSere1(serializeFileName, SearchEngine.Porterindex); // for inverted index and porters index
	   
			
	    
	     // loc sere
	     serializeFileName = "locindex.ser.txt";
	     FileSere2(serializeFileName, SearchEngine.locindex); // for inverted index and porters index
	   

	     // porterr loc sere
	     serializeFileName = "porterlocindex.ser.txt";
		 FileSere2(serializeFileName, SearchEngine.porterLocindex); // for inverted index and porters index
		     
		     
		            
		     
		
		
	}
	
	



	private static HashMap<String, Map<String, String>> FileDesere2(String deserilizeFileName,
			HashMap<String, Map<String, String>> newlocindex) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(deserilizeFileName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		newlocindex = (HashMap<String, Map<String, String>>)ois.readObject();
		ois.close();
		fis.close();
		return newlocindex;
	}

	private static void FileSere2(String serializeFileName, HashMap<String, Map<String, String>> locindex2) throws IOException {
		FileOutputStream fos = new FileOutputStream(serializeFileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(locindex2);
        oos.close();
        fos.close();
		
	}

	private static HashMap<String, Map<String, Integer>> FileDesere1(String deserilizeFileName, HashMap<String, Map<String, Integer>> newindex) throws ClassNotFoundException, IOException{
			FileInputStream fis = new FileInputStream(deserilizeFileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			newindex = (HashMap<String, Map<String, Integer>>)ois.readObject();
			ois.close();
			fis.close();
			return newindex;
	}

	

	private static void FileSere1(String serializeFileName, HashMap<String, Map<String, Integer>> index2 ) throws IOException {
		FileOutputStream fos = new FileOutputStream(serializeFileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(index2);
        oos.close();
        fos.close();
		
	}

	public void solveAQuery(String queries, String results) throws IOException {

		// 1. open the file with the queries process it
		// return array with each line
		fileProcessing qfp = new fileProcessing();
		String[] queryArray = qfp.fileToArray2(queries);

		BufferedWriter outfile1 = new BufferedWriter(new FileWriter(results));

		for (String qu : queryArray) {
			// System.out.println(qu);
			// 2. write the query out to the file and aa new line
			outfile1.write(String.valueOf(qu) + " ");
			outfile1.write("\n");

			// System.out.println(qu);

			// 3. split the string at every non word
			// create an array of eaach word in the query
			// should be <Action> <Term>
			// where <Action> is a process to be done and stored in queryWords[0]
			// <Term> is the term we're looking for in the documents
			String[] queryWords = qu.split("\\W+");

			// 4. if <Action> is query
			if (queryWords[0].toLowerCase().contentEquals("query")) {
				// System.out.println(queryWords[1].toLowerCase());
				// System.out.println(index.containsKey(queryWords[1].toLowerCase()));

				// a. make sure <Term> exists in the index and if it does exist get the value(
				// <string,integer>, <docid, freq>)
				// that has term as a key
				if (index.containsKey(queryWords[1].toLowerCase()) == true) {
					Map<String, Integer> entry = index.get(queryWords[1]);
					// System.out.println(entry.keySet().toString());
					// System.out.println();

					// b. output the documents where term is found
					// the keyset of the value ( <string,integer>, <docid, freq>)
					outfile1.write("found in the following documents: ");
					outfile1.write("\n");
					outfile1.write(String.valueOf(entry.keySet().toString()) + " ");
					outfile1.write("\n");
					outfile1.write("\n");

				}

				else {
					outfile1.write(String.valueOf("term does not exist") + " ");
					outfile1.write("\n");
					outfile1.write("\n");
				}

			}

			// 5. if <Action> is frequency
			else if (queryWords[0].toLowerCase().contentEquals("frequency")) {

				// System.out.println(queryWords[1].toLowerCase());
				// System.out.println(index.containsKey(queryWords[1].toLowerCase()));

				// a. make sure <Term> exists in the index and if it does exist get the value(
				// <string,integer>, <docid, freq>)
				// that has term as a key
				if (index.containsKey(queryWords[1].toLowerCase()) == true) {
					Map<String, Integer> entry = index.get(queryWords[1]);

					// b. return the values of entry in a collection and convert that collection to
					// an array
					Collection<Integer> docIds = entry.values();
					int[] array = docIds.stream().mapToInt(i -> i).toArray();
					// System.out.println(entry.values());
					// System.out.println(array.length);

					// c. loop thru aray to get the sum of totaal freq
					int sum = 0;
					for (int a : array) {
						sum = sum + a;
						// System.out.println(sum);
					}
					// System.out.println(sum);
					// System.out.println();
					// outfile1.write(String.valueOf(entry.values()) + " ");

					// d. write it to file
					outfile1.write("# documents : " + String.valueOf(array.length) + " ");
					outfile1.write("\n");
					outfile1.write("total freq # : " + String.valueOf(sum) + " ");
					outfile1.write("\n");
					outfile1.write("\n");

				} else {
					// System.out.println("term does not exist");

					outfile1.write(String.valueOf("term does not exist") + " ");
					outfile1.write("\n");
					outfile1.write("\n");

				}

			}
			// 6. this was mentioned in the phase1 non submissions
			else if (queryWords[0].toLowerCase().contentEquals("contains")) {

				// System.out.println(queryWords[1].toLowerCase());
				// System.out.println(index.containsKey(queryWords[1].toLowerCase()));

				if (index.containsKey(queryWords[1].toLowerCase()) == true) {
					// System.out.println(queryWords[1].toLowerCase() + "is in at least one
					// document");
					// System.out.println();
					outfile1.write(String.valueOf(queryWords[1].toLowerCase()) + " is in at least one document");
					outfile1.write("\n");
					outfile1.write("\n");
				} else {
					outfile1.write(String.valueOf("term does not exist") + " ");
					outfile1.write("\n");
					outfile1.write("\n");

				}

			}

			// 7. this was mentioned in the phase1 non submissions
			else if (queryWords[0].toLowerCase().contentEquals("many")) {

				// System.out.println(queryWords[1].toLowerCase());
				// System.out.println(index.containsKey(queryWords[1].toLowerCase()));

				if (index.containsKey(queryWords[1].toLowerCase()) == true) {
					Map<String, Integer> entry = index.get(queryWords[1]);
					// System.out.println(entry.values());
					System.out.println(queryWords[1].toLowerCase() + " is in " + entry.values().size() + " documents");
					System.out.println(entry.values());
					System.out.println();

				}

				else {
					outfile1.write(String.valueOf("term does not exist") + " ");
					outfile1.write("\n");
					outfile1.write("\n");

				}

			}

			// 8. invalid command
			else {

				outfile1.write(String.valueOf("query command does not exist") + " ");
				outfile1.write("\n");
				outfile1.write("\n");

			}

		}

		outfile1.close();

	}
	
	
	public void solveAQueryPorter(String queries, String results) throws IOException {

		// 1. open the file with the queries process it
		// return array with each line
		fileProcessing qfp = new fileProcessing();
		String[] queryArray = qfp.fileToArray2(queries);

		BufferedWriter outfile1 = new BufferedWriter(new FileWriter(results));

		for (String qu : queryArray) {
			// System.out.println(qu);
			// 2. write the query out to the file and aa new line
			outfile1.write(String.valueOf(qu) + " ");
			outfile1.write("\n");

			// System.out.println(qu);

			// 3. split the string at every non word
			// create an array of eaach word in the query
			// should be <Action> <Term>
			// where <Action> is a process to be done and stored in queryWords[0]
			// <Term> is the term we're looking for in the documents
			String[] queryWords = qu.split("\\W+");
			

			// 4. if <Action> is query
			if (queryWords[0].toLowerCase().contentEquals("query")) {
				// System.out.println(queryWords[1].toLowerCase());
				// System.out.println(index.containsKey(queryWords[1].toLowerCase()));

				// a. make sure <Term> exists in the index and if it does exist get the value(
				// <string,integer>, <docid, freq>)
				// that has term as a key
				// also stem <term> first to fit with porter index
				
				String w = ps.stemWord(queryWords[1].toLowerCase());
				
		
				
				if (Porterindex.containsKey(w) == true) {
					Map<String, Integer> entry = Porterindex.get(w);
					// System.out.println(entry.keySet().toString());
					// System.out.println();

					// b. output the documents where term is found
					// the keyset of the value ( <string,integer>, <docid, freq>)
					outfile1.write("found in the following documents: ");
					outfile1.write("\n");
					outfile1.write(String.valueOf(entry.keySet().toString()) + " ");
					outfile1.write("\n");
					outfile1.write("\n");

				}

				else {
					outfile1.write(String.valueOf("term does not exist") + " ");
					outfile1.write("\n");
					outfile1.write("\n");
				}

			}

			// 5. if <Action> is frequency
			else if (queryWords[0].toLowerCase().contentEquals("frequency")) {

				// System.out.println(queryWords[1].toLowerCase());
				// System.out.println(index.containsKey(queryWords[1].toLowerCase()));

				// a. make sure <Term> exists in the index and if it does exist get the value(
				// <string,integer>, <docid, freq>)
				// that has term as a key
				// also be sure to stem
				
				String w = ps.stemWord(queryWords[1].toLowerCase());

				if (Porterindex.containsKey(w) == true) {
					Map<String, Integer> entry = Porterindex.get(w);

					// b. return the values of entry in a collection and convert that collection to
					// an array
					Collection<Integer> docIds = entry.values();
					int[] array = docIds.stream().mapToInt(i -> i).toArray();
					// System.out.println(entry.values());
					// System.out.println(array.length);

					// c. loop thru aray to get the sum of totaal freq
					int sum = 0;
					for (int a : array) {
						sum = sum + a;
						// System.out.println(sum);
					}
					// System.out.println(sum);
					// System.out.println();
					// outfile1.write(String.valueOf(entry.values()) + " ");

					// d. write it to file
					outfile1.write("# documents : " + String.valueOf(array.length) + " ");
					outfile1.write("\n");
					outfile1.write("total freq # : " + String.valueOf(sum) + " ");
					outfile1.write("\n");
					outfile1.write("\n");

				} else {
					// System.out.println("term does not exist");

					outfile1.write(String.valueOf("term does not exist") + " ");
					outfile1.write("\n");
					outfile1.write("\n");

				}

			}
			// 6. this was mentioned in the phase1 non submissions
			else if (queryWords[0].toLowerCase().contentEquals("contains")) {

				// System.out.println(queryWords[1].toLowerCase());
				// System.out.println(index.containsKey(queryWords[1].toLowerCase()));
				
				String w = ps.stemWord(queryWords[1].toLowerCase());


				if (Porterindex.containsKey(w) == true) {
					// System.out.println(queryWords[1].toLowerCase() + "is in at least one
					// document");
					// System.out.println();
					outfile1.write(String.valueOf(w) + " is in at least one document");
					outfile1.write("\n");
					outfile1.write("\n");
				} else {
					outfile1.write(String.valueOf("term does not exist") + " ");
					outfile1.write("\n");
					outfile1.write("\n");

				}

			}

			// 7. this was mentioned in the phase1 non submissions
			else if (queryWords[0].toLowerCase().contentEquals("many")) {

				// System.out.println(queryWords[1].toLowerCase());
				// System.out.println(index.containsKey(queryWords[1].toLowerCase()));
				
				
				String w = ps.stemWord(queryWords[1].toLowerCase());

				if (Porterindex.containsKey(w) == true) {
					Map<String, Integer> entry = Porterindex.get(w);
					// System.out.println(entry.values());
					System.out.println(w + " is in " + entry.values().size() + " documents");
					System.out.println(entry.values());
					System.out.println();

				}

				else {
					outfile1.write(String.valueOf("term does not exist") + " ");
					outfile1.write("\n");
					outfile1.write("\n");

				}

			}

			// 8. invalid command
			else {

				outfile1.write(String.valueOf("query command does not exist") + " ");
				outfile1.write("\n");
				outfile1.write("\n");

			}

		}

		outfile1.close();

	}
}

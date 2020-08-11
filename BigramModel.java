package il.ac.tau.cs.sw1.ex5;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;


public class BigramModel {
	public static final int MAX_VOCABULARY_SIZE = 14000;
	public static final String VOC_FILE_SUFFIX = ".voc";
	public static final String COUNTS_FILE_SUFFIX = ".counts";
	public static final String SOME_NUM = "some_num";
	public static final int ELEMENT_NOT_FOUND = -1;
	
	String[] mVocabulary;
	int[][] mBigramCounts;
	
	
	// DO NOT CHANGE THIS !!! 
	public void initModel(String fileName) throws IOException{
		long start1=System.currentTimeMillis();
		mVocabulary = buildVocabularyIndex(fileName);
		long end1=System.currentTimeMillis();
		System.out.println(end1-start1);
		long start2=System.currentTimeMillis();

		mBigramCounts = buildCountsArray(fileName, mVocabulary);
		
		long end2=System.currentTimeMillis();
		System.out.println(end2-start2);
		
	}
	
	
	
	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public String[] buildVocabularyIndex(String fileName) throws IOException{ // Q 1
		BufferedReader reader= new BufferedReader(new FileReader(new File(fileName))); 
		//String legalWords=""; //we'll add the legal words to this string and will split the string to the vocabulary array at the end
		String [] vocabulary=new String[MAX_VOCABULARY_SIZE];
		int countLegalWords=0; //counter for the legal words so we can break when we reach 14,000 legal words
		for (String line=reader.readLine(); line!=null; line=reader.readLine()){
			if (countLegalWords==MAX_VOCABULARY_SIZE) {  //break loop if reached 14,000 words
				break;
			}

			String[] wordsInLine=line.split(" ");			
			for (String word: wordsInLine) {    //looping over the words of line from the reader
				if (countLegalWords==MAX_VOCABULARY_SIZE) {  //break loop if reached 14,000 words
					break;
				}
				
				word=word.toLowerCase();
				//if (!isContain(legalWords, word)){ "^*[a-z]+$" //  word.matches("\\d") 
				if (checkMatch(word) && notContains(vocabulary, word, countLegalWords)) {    //check if word is legal(if it contains an alphabetical letter)
					vocabulary[countLegalWords]=word;
					//legalWords+=word+" ";
					countLegalWords++;
				}
				else if (isNumber(word) && notContains(vocabulary, SOME_NUM, countLegalWords)) {   //check if int
					vocabulary[countLegalWords]=SOME_NUM;

					//legalWords+=SOME_NUM+" ";
					countLegalWords++;
				}
			}
		}
		
		reader.close();	
		if(countLegalWords!=MAX_VOCABULARY_SIZE) {
			vocabulary=Arrays.copyOfRange(vocabulary, 0, countLegalWords);
		}
		//String[] vocabulary=legalWords.split(" ");	//split the string of the legal words into an array of the right size
		return vocabulary;
	}
	

	private boolean isNumber(String word) {  // a function to check if the string contains an integer
		if (word==null || word.isEmpty()) {
			return false;
		}
		int wordLen=word.length();
		for (int i=0; i<wordLen; i++) {	//looping over the chars and checking if they are digits
			char c=word.charAt(i);
			if (!Character.isDigit(c)) {	//if one of the chars is not a digit, then the word does not represent an integer
				return false;
			}
		}
		return true;
	}


	private boolean checkMatch(String word) { // a function to check if the word is legal
		if (word==null || word.isEmpty()) {
			return false;
		}
		int wordLen=word.length();
		for (int i=0; i<wordLen; i++) {
			char c=word.charAt(i);	//checking each char of the word
			if (c>='a' && c<='z') {	//if one of the chars is a letter then the word is legal
				return true;
			}
		}
		return false;	//if none of the chars is a letter then the word is illegal
	}

	
	private static boolean notContains(String[] vocabulary, String word, int len ){ // a function to check if the word is already in the sentence of the legal words
		//String[] vocSentence=sentence.split(" "); // spliting the sentence to an array of the words
		for (int i=0; i<len; i++) {	//looping over the array
			if (vocabulary[i].equals(word)) {	//checking for each word if it equals to the word we are looking for
				return false;	//if equals, return false 
			}
		}
		return true;
	}
	
	
	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException{ // Q - 2
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName))); 
		int num=vocabulary.length;
		int[][] countsArray=new int[num][num];  //building an array of arrays of the size of the vocabulary
		for (String line=reader.readLine(); line!=null; line=reader.readLine()) {	//looping over the lines in the file
			String[] wordsInLine=line.split(" ");	//spliting the lines to an array of the words
			int numOfWordsInLine=wordsInLine.length;
			if (numOfWordsInLine>=2) {	//if there are more than 2 words in the line
				if (isNumber(wordsInLine[0])){	//first of all, we check if one of the first word in the line is an integer
					wordsInLine[0]=SOME_NUM;	//if it is, change their value to SOME_NUM
				}
				
				int i=findIndex(vocabulary, wordsInLine[0].toLowerCase());	//get the index of the first word				
				for (int index=1 ; index<numOfWordsInLine ; index++) {
					if (isNumber(wordsInLine[index])){	//check if the next word is an integer
						wordsInLine[index]=SOME_NUM;		//if so, change it to SUM_NUM
					}
					int j=findIndex(vocabulary, wordsInLine[index].toLowerCase());
					if (i!=ELEMENT_NOT_FOUND && j!=ELEMENT_NOT_FOUND) {		//if both of the 2 words are in the vocabulary
						countsArray[i][j]++;	//the add +1 to the value of the countsArray in place [i][j] 
					}
					i=j;	//the value of i becomes the value of j (instead of checking it's value again) 	
				}
			}
		}
		reader.close();
		return countsArray;
	}
	

	private int findIndex(String[] vocabulary, String word) {	//a function to find the index of a word in the vocabulary
		int len=vocabulary.length;									//(for some reason the results are different if I use findWordIndex function) 
		for (int i=0; i<len; i++) {
			if (vocabulary[i].equals(word)){
				return i;
			}
		}
		return ELEMENT_NOT_FOUND;
	}



	/*
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: fileName is a legal file path
	 */
	public void saveModel(String fileName) throws IOException{ // Q-3
		vocFile(fileName);
		countsFile(fileName);		
	}
	

	private void vocFile(String fileName) throws IOException{
		String vocFileName=fileName + VOC_FILE_SUFFIX;
		FileWriter vocFile=new FileWriter(vocFileName);
		int vocLen=mVocabulary.length;
		vocFile.write(vocLen + " words" +System.lineSeparator());
		for (int i=0; i<vocLen; i++) {
			vocFile.write(i+","+mVocabulary[i]+System.lineSeparator());
		}
		vocFile.close();
	}
	
	
	private void countsFile(String fileName) throws IOException {
		String countsFileName=fileName + COUNTS_FILE_SUFFIX;
		FileWriter countsFile=new FileWriter(countsFileName);
		int num=mBigramCounts.length;
		for (int i=0; i<num; i++) {
			for (int j=0; j<num; j++) {
				if (mBigramCounts[i][j]!=0){
					countsFile.write(i+ ","+ j+ ":"+ mBigramCounts[i][j]+System.lineSeparator());

				}
			}
		}
		countsFile.close();	
	}


	
	/*
	 * @pre: fileName is a legal file path
	 */
	public void loadModel(String fileName) throws IOException{ // Q - 4
		loadVoc(fileName);
		loadCounts(fileName);		
	}

	
	private void loadVoc(String fileName) throws IOException{
		String vocFileName=fileName + VOC_FILE_SUFFIX;
		BufferedReader vocReader=new BufferedReader(new FileReader(new File(vocFileName))); 
		String firstLine=vocReader.readLine();
		String[] arrayFirstLine=firstLine.split(" ");
		int vocLength=Integer.parseInt(arrayFirstLine[0]);
		mVocabulary=new String[vocLength];
		for(int i=0; i<vocLength; i++) {
			String line=vocReader.readLine();
			int index=line.indexOf(',');
			mVocabulary[i]=line.substring(index+1);
		}
		vocReader.close();
	}
	
	
	private void loadCounts(String fileName) throws IOException {
		String countsFileName=fileName + COUNTS_FILE_SUFFIX;
		BufferedReader countsReader= new BufferedReader(new FileReader(new File(countsFileName))); 
		int size=getArraySize(countsFileName)+1;
		mBigramCounts=new int[size][size];
		int[][] arr=new int[size][size];
		for (String line=countsReader.readLine(); line!=null; line=countsReader.readLine()) {
			String [] wordsInLine=line.split(",");
			int i=Integer.valueOf(wordsInLine[0]);
			int j=Integer.valueOf(wordsInLine[1].split(":")[0]);
			int value_ij=Integer.valueOf(wordsInLine[1].split(":")[1]);
			mBigramCounts[i][j]=value_ij;
			arr[i][j]=value_ij;
		}
		countsReader.close();
	}


	
	
	private int getArraySize(String fileName) throws IOException {
		BufferedReader countsReader= new BufferedReader(new FileReader(new File(fileName))); 
		int size=0;
		for (String line=countsReader.readLine(); line!=null; line=countsReader.readLine()) {
			String [] wordsInLine=line.split(",");
			int firstNum=Integer.valueOf(wordsInLine[0]);
			int secondNum=Integer.valueOf(wordsInLine[1].split(":")[0]);
			if (firstNum>size || secondNum>size) {
				size=Math.max(firstNum, secondNum);
			}
		}
		countsReader.close();
		return size;
	}



	/*
	 * @pre: word is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: word is in lowercase
	 * @post: $ret = -1 if word is not in vocabulary, otherwise $ret = the index of word in vocabulary
	 */
	public int getWordIndex(String word){  // Q - 5
		 return findIndex(mVocabulary, word);
	}
	
	
	
	/*
	 * @pre: word1, word2 are in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = the count for the bigram <word1, word2>. if one of the words does not
	 * exist in the vocabulary, $ret = 0
	 */
	public int getBigramCount(String word1, String word2){ //  Q - 6
		int index_word1=getWordIndex(word1);
		int index_word2=getWordIndex(word2);
		if (index_word1==-1 || index_word2==-1) {
			return 0;
		}
		else {
			return mBigramCounts[index_word1][index_word2];
		}
	}
	
	
	/*
	 * @pre word in lowercase, and is in mVocabulary
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post $ret = the word with the lowest vocabulary index that appears most fequently after word (if a bigram starting with
	 * word was never seen, $ret will be null
	 */
	public String getMostFrequentProceeding(String word){ //  Q - 7
		int wordIndex=getWordIndex(word);
		int searchedIndex=-1;
		int mostFrequent=0;
		for (String searchedWord: mVocabulary) {
			int Index=getWordIndex(searchedWord);
			int word_bigramCount=mBigramCounts[wordIndex][Index];
			if (word_bigramCount>mostFrequent) {
				searchedIndex=Index;
				mostFrequent=word_bigramCount;
			}
		}
		if (searchedIndex!=-1) {
			String searchedWord=mVocabulary[searchedIndex];
			return searchedWord;
		}
		return null;
	}
	
	
	/* @pre: sentence is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: each two words in the sentence are are separated with a single space
	 * @post: if sentence is is probable, according to the model, $ret = true, else, $ret = false
	 */
	public boolean isLegalSentence(String sentence){  //  Q - 8
		if (sentence.equals("")){
			return true;
		}
		
		String[] wordsInSentence=sentence.split(" ");
		int numOfWords=wordsInSentence.length;
		if (numOfWords==1) {
			if (getWordIndex(wordsInSentence[0])==-1){
				return false;
			}
			else {
				return true;
			}
		}
		
		for (int i=0; i<numOfWords-1; i++) {
			String word1=wordsInSentence[i];
			String word2=wordsInSentence[i+1];
			int bigramCount=getBigramCount(word1, word2);
			if (bigramCount==0){
				return false;
			}
		}
		return true;
	}
	
	
	
	/*
	 * @pre: arr1.length = arr2.legnth
	 * post if arr1 or arr2 are only filled with zeros, $ret = 0, otherwise
	 */
	public static double calcCosineSim(int[] arr1, int[] arr2){ //  Q - 9
		int lenArr=arr1.length;
		double numerator=0;
		double arr1_denominator=0;
		double arr2_denominator=0;
		for (int i=0; i<lenArr; i++) {
			int ai=arr1[i];
			int bi=arr2[i];
			numerator+=ai*bi;
			arr1_denominator+=ai*ai;
			arr2_denominator+=bi*bi;		
		}
		if (arr1_denominator==0 || arr2_denominator==0) {
			return 0;
		}
		double denominator=Math.sqrt(arr1_denominator)*Math.sqrt(arr2_denominator);
		double finalCalc=numerator/denominator;
		return finalCalc;
	}

	
	/*
	 * @pre: word is in vocabulary
	 * @pre: the method initModel was called (the language model is initialized), 
	 * @post: $ret = w implies that w is the word with the largest cosineSimilarity(vector for word, vector for w) among all the
	 * other words in vocabulary
	 */
	public String getClosestWord(String word){ //  Q - 10
		int vocLen=mVocabulary.length;
		if (vocLen==1){
			return mVocabulary[0];
		}
		
		int[] wordVector=getVector(word);
		int wordIndex=getWordIndex(word);
		double cosineSim=-1;
		String closestWord="";
		for (int i=0; i<vocLen; i++) {
			String w=mVocabulary[i];
			int[] wVector=getVector(w);

			double checkCosineSim=-1;
			if (i!=wordIndex) {
				checkCosineSim=calcCosineSim(wordVector,wVector);
			}
			else {
				checkCosineSim=0;
			}
			
			if (checkCosineSim>cosineSim) {
				cosineSim=checkCosineSim;
				closestWord=w;			
			}
		}
		
		return closestWord;	
	}


	private int[] getVector(String word) {
		int wordIndex=getWordIndex(word);
		int vocLen=mVocabulary.length;
		int[] wordVector=new int[vocLen];
		for (int i=0; i<vocLen; i++) {
			wordVector[i]=mBigramCounts[wordIndex][i];			
		}
		return wordVector;
	}
	
}

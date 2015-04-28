package knn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class KNearestNeighborEmail {
	
	private final static String trainingFileName = "8category.training.txt";
	private final static String testFileName = "8category.testing.txt";
	private static ArrayList<Integer> trainingClass = new ArrayList<>();
	private static ArrayList<Integer> testClass = new ArrayList<Integer>();
	private static ArrayList<Integer> predictedTestClass = new ArrayList<Integer>();
	private static Set<String> uniqueWordsSet = new HashSet<String>();
	private static ArrayList<String> uniqueWordsList = new ArrayList<String>();
	
	private static float[][] traininFeatureVectorsArray = new float[1900][44033]; 
	
	private static float[][] testingFeatureVectorsArray = new float[263][44033]; 

	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		
		
		File file = new File(trainingFileName);
        String line;       
        String currWord;
      
        
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null) {
        
        	String[] words = line.split(" ");
        	int classNum = Integer.parseInt(words[0]);
            trainingClass.add(classNum);
            for(int ctr = 1; ctr < words.length;ctr++){
            	currWord = words[ctr].split(":")[0];
            	uniqueWordsSet.add(currWord.trim());
            }

        }
        
        System.out.println("UniqueWordsSet size = " + uniqueWordsSet.size());
        uniqueWordsList.addAll(uniqueWordsSet);
        Collections.sort(uniqueWordsList);
        //System.out.println(featureVectorList);
        
        
        //load actual test values to testClass Arraylist
        getAllTestClassValues();
        
        //construct feature vector for training dataset
        constructFeatureVectorsForTraining(trainingFileName);
        System.out.println("traininFeatureVectorsArray = " + traininFeatureVectorsArray.length);
        
        //construct feature vector for test dataset 
        constructFeatureVectorsForTest(testFileName);
        System.out.println("testingFeatureVectorsArray = " + testingFeatureVectorsArray.length);
        
        
        //for each line of test feature vecctor, call getKNN()
        
        
        System.out.println("-----------------------------------------\nComputing KNN");
        for(int ctr = 0 ; ctr < testingFeatureVectorsArray.length; ctr++){
        	predictedTestClass.add(getKNN(traininFeatureVectorsArray, testingFeatureVectorsArray[ctr], 5));
        }
        
//        System.out.println("predicted Class size = " + predictedTestClass.size());
//        System.out.println("test Class size = " + testClass.size());
        int correctClassification = 0;
        for(int ctr = 0; ctr < testClass.size(); ctr++){
        	if(testClass.get(ctr) == predictedTestClass.get(ctr)){
        		correctClassification++;
        	}
        }
        
        System.out.println("Correct classification count = " + correctClassification);
        float accuracy = (float)(correctClassification)/testClass.size();
        System.out.println("Accuracy = " + accuracy*100);
        
        
	}
	
	public static void getAllTestClassValues() throws NumberFormatException, IOException{
		File file = new File(testFileName);
        String line;       
        String currWord;
      
        
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null) {
        
        	String[] words = line.split(" ");
        	int classNum = Integer.parseInt(words[0]);
            testClass.add(classNum);

        }
	}
	
	public static void constructFeatureVectorsForTraining( String fileName) throws IOException{
		File file = new File(fileName);
		int lineNum = 0;
        String line;       
        String currWord;
        String[] currLine;
        float currWordFreq = 0;
        float totalLineFreq = 0;
        ArrayList<ArrayList<Float>> featureVectors = new ArrayList<ArrayList<Float>>();
        ArrayList<Float> currLineFeatureVector;
    	
        Map<String, Float> currLineWordmap = new HashMap<String, Float>();
        
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null) {
        	lineNum++;
        	//System.out.println("lineNum = "+ lineNum);
        	String[] words = line.split(" ");
        	//System.out.println("Num of words in curr line = " + words.length);
        	totalLineFreq = 0;
        	currLineWordmap.clear();
        	
        	for(int ctr = 1; ctr < words.length;ctr++){
        		currLine = words[ctr].split(":");
            	currWord = currLine[0].trim();
            	currWordFreq = Float.parseFloat(currLine[1]);
            	totalLineFreq+=(currWordFreq*currWordFreq);
            	currLineWordmap.put(currWord, currWordFreq);
            }
        	
        	//reset the frequency of the words 
        	for (Entry<String, Float> entry : currLineWordmap.entrySet()) {
        	    String key = entry.getKey();
        	    Float value = entry.getValue();
        	    Float newValue = (float) (value/Math.sqrt(totalLineFreq));
        	    entry.setValue(newValue);
        	}
        	
        	//construct feature vector for this line
        	//currLineFeatureVector = new ArrayList<Float>();
        	int wordNotFound = 0;
        	String featureVectorWord;
        	for(int i=0; i<uniqueWordsList.size(); i++){
        		featureVectorWord = uniqueWordsList.get(i);
        		if(currLineWordmap.containsKey(featureVectorWord)){
        			//currLineFeatureVector.add(currLineWordmap.get(featureVectorWord));
        			traininFeatureVectorsArray[lineNum-1][i] = currLineWordmap.get(featureVectorWord);
        		}
        		else {
        			//currLineFeatureVector.add((float) 0);
        			traininFeatureVectorsArray[lineNum-1][i] = (float) 0 ;
        			wordNotFound++;
        		}
        		
        	}
 
        	currLineFeatureVector = null;
        }
        
       
        
	}
	
	public static void constructFeatureVectorsForTest(String fileName) throws NumberFormatException, IOException{
		System.out.println("In constructFeatureVectorsForTest ");
		File file = new File(fileName);
		int lineNum = 0;
        String line;       
        String currWord;
        String[] currLine;
        float currWordFreq = 0;
        int totalLineFreq = 0;
        ArrayList<ArrayList<Float>> featureVectors = new ArrayList<ArrayList<Float>>();
        Map<String, Float> currLineWordmap = new HashMap<String, Float>();
        
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null) {
        	lineNum++;
        	String[] words = line.split(" ");
        	//System.out.println("Numof words = " + words.length);
        	totalLineFreq = 0;
        	currLineWordmap.clear();
        	
        	for(int ctr = 1; ctr < words.length;ctr++){
        		currLine = words[ctr].split(":");
            	currWord = currLine[0];
            	currWordFreq = Float.parseFloat(currLine[1]);
            	if(uniqueWordsList.contains(currWord)){
            		totalLineFreq+=(currWordFreq*currWordFreq);
            		currLineWordmap.put(currWord, currWordFreq);
            	}
            	
            }
        	
        	//reset the frequency of the words 
        	for (Entry<String, Float> entry : currLineWordmap.entrySet()) {
        	    String key = entry.getKey();
        	    Float value = entry.getValue();
        	    Float newValue = (float) (value/Math.sqrt(totalLineFreq));
        	    entry.setValue(newValue);
        	}
        	
        	int wordNotFound = 0;
        	//construct feature vector for this line
        	ArrayList<Float> currLineFeatureVector = new ArrayList<Float>();
        	for(int i=0; i<uniqueWordsList.size(); i++){
        		if(currLineWordmap.containsKey(uniqueWordsList.get(i))){
        			currLineFeatureVector.add(currLineWordmap.get(uniqueWordsList.get(i)));
        			testingFeatureVectorsArray[lineNum-1][i] = currLineWordmap.get(uniqueWordsList.get(i));
        			
        		}
        		else {
        			currLineFeatureVector.add((float) 0);
        			testingFeatureVectorsArray[lineNum-1][i] = (float) 0; 
        			wordNotFound++;
        		}
        	}
        	
        	featureVectors.add(currLineFeatureVector);
        	
        	currLineWordmap.clear();
        	currLineFeatureVector.clear();
        	
        	//System.out.println("LineNum = " + lineNum);
        
        }
        
	}
	
	public static Integer getKNN(float[][] fullTrainingFeaturesList, float[] testFeatures, int k){
		
		//compare the test features with each line of fullTrainingFeaturesList
		ArrayList<Float> neighborsDistances = new ArrayList<Float>();
		ArrayList<Float> distancesList = new ArrayList<Float>();
		
		for(int line = 0; line < fullTrainingFeaturesList.length; line++){
			float sumOfDiff = 0;
			float diff = 0;
			for(int index =0; index < fullTrainingFeaturesList[0].length; index++){
				//System.out.print(fullTrainingFeaturesList[line][index] + " - " + testFeatures[index]);
				diff = Math.abs(fullTrainingFeaturesList[line][index] - testFeatures[index]);
				sumOfDiff+=(diff * diff);
			}
			
			//System.out.println("sum of diff = " + sumOfDiff);
			neighborsDistances.add((float) Math.sqrt(sumOfDiff));
			distancesList.add((float) Math.sqrt(sumOfDiff));
			
			
		}
		Collections.sort(distancesList);
		
		ArrayList<Integer> predValues = new ArrayList<Integer>();
		Map<Integer, Integer> classFreqMap = new HashMap<Integer, Integer>();
		int NNValue = 0;
		
		for(int i = 0 ; i < k; i++){
			//System.out.println("dist = "+distancesList.get(i));
			int index = neighborsDistances.indexOf(distancesList.get(i));
			//System.out.println("look for index = " + index);
			int predictedClassVal = trainingClass.get(index);
			if(i == 0)
				NNValue = predictedClassVal;
			if(classFreqMap.containsKey(predValues)){
				classFreqMap.put(predictedClassVal, classFreqMap.get(predictedClassVal) + 1);
			}
			else {
				classFreqMap.put(predictedClassVal, 1);
			}
			
		}
		Entry<Integer, Integer> maxEntry = null;
		
		for (Entry<Integer, Integer> entry : classFreqMap.entrySet()) {
			if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
		        maxEntry = entry;
		    }
        }
		
		int retValue ;
		if(maxEntry.getValue() > 1){
			retValue = maxEntry.getKey();
		}
		else {
			//return the closest neighbor
			retValue = NNValue;
		}
		//System.out.println("Predicted Class Val = " + maxEntry.getKey());
		return (NNValue);
		
	}

}

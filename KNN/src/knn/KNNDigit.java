package knn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class KNNDigit {
	
	private static ArrayList<Integer> trainingLabels = new ArrayList<Integer>();
	private static ArrayList<Integer> testingLabels = new ArrayList<Integer>();
	private static ArrayList<Integer> predictedTestClass = new ArrayList<Integer>();
	
	private static int[][] trainingDataVector = new int[5000][784];
	private static int[][] testingDataVector = new int[1000][784];
	
	private static final String trainingImagesFile = "digitdata/trainingimages";
	private static final String testingImagesFile = "digitdata/testimages";
	
	private static final String trainingLabelsFile = "digitdata/traininglabels";
	private static final String testingLabelsFile = "digitdata/testlabels";
	
	public static void main(String args[]) throws NumberFormatException, IOException{
	
		File file = new File(trainingLabelsFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
                
        String line;       
         
        while ((line = br.readLine()) != null) {
            trainingLabels.add(Integer.parseInt(line));
        }
        
        
        file = new File(testingLabelsFile);
        br = new BufferedReader(new FileReader(file));
                
        line = "";       
         
        while ((line = br.readLine()) != null) {
            testingLabels.add(Integer.parseInt(line));
        }
        
        //generate feature vectors for trainingimages
        constructFeatureVectorsForTraining(trainingImagesFile);
        
        //generate feature vectors for test imges
        constructFeatureVectorsForTest(testingImagesFile);
        
        System.out.println("-----------------------------------------\nComputing KNN");
        for(int ctr = 0 ; ctr < testingDataVector.length; ctr++){
        	predictedTestClass.add(getKNN(trainingDataVector, testingDataVector[ctr], 5));
        }
        
//        System.out.println("predicted Class size = " + predictedTestClass.size());
//        System.out.println("test Class size = " + testClass.size());
        int correctClassification = 0;
        for(int ctr = 0; ctr < testingLabels.size(); ctr++){
        	if(testingLabels.get(ctr) == predictedTestClass.get(ctr)){
        		correctClassification++;
        	}
        }
        
        System.out.println("Correct classification count = " + correctClassification);
        float accuracy = (float)(correctClassification)/testingLabels.size();
        System.out.println("Accuracy = " + accuracy*100);
        
        
		
	}//end main
	
	public static void constructFeatureVectorsForTraining(String fileName) throws NumberFormatException, IOException{
		File file = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
                
        String line;       
        int lineNum = 0; 
        int imageNum = 0;
        StringBuilder strBuilder = new StringBuilder();
                
        while ((line = br.readLine()) != null) {
        	lineNum++;
//            System.out.println(lineNum);
            if(lineNum > 1 && lineNum % 28 == 0){
            	strBuilder.append(line);
//        	    System.out.println(strBuilder.length());
        	    for(int indx = 0; indx < 784; indx++){
        		    if(strBuilder.charAt(indx) == ' '){
        		 	   trainingDataVector[imageNum][indx] = 0;
        		    }
        		    else {
        			    trainingDataVector[imageNum][indx] = 1;
        		    }
        	    }
        	    strBuilder = new StringBuilder();
        	    imageNum++;
//        	    System.out.println("ImageNum = " + imageNum);
            }
            else {
        	    strBuilder.append(line);
            }
            
        }
        
        
	}
	
	public static void constructFeatureVectorsForTest(String fileName) throws NumberFormatException, IOException{
		File file = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
                
        String line;       
        int lineNum = 0; 
        int imageNum = 0;
        StringBuilder strBuilder = new StringBuilder();
                
        while ((line = br.readLine()) != null) {
        	lineNum++;
//            System.out.println(lineNum);
            if(lineNum > 1 && lineNum % 28 == 0){
            	strBuilder.append(line);
//        	    System.out.println(strBuilder.length());
        	    for(int indx = 0; indx < 784; indx++){
        		    if(strBuilder.charAt(indx) == ' '){
        		 	   testingDataVector[imageNum][indx] = 0;
        		    }
        		    else {
        			    testingDataVector[imageNum][indx] = 1;
        		    }
        	    }
        	    strBuilder = new StringBuilder();
        	    imageNum++;
//        	    System.out.println("ImageNum = " + imageNum);
            }
            else {
        	    strBuilder.append(line);
            }
            
        }
        
        
	}
	
public static Integer getKNN(int[][] fullTrainingFeaturesList, int[] testFeatures, int k){
		
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
			int predictedClassVal = trainingLabels.get(index);
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

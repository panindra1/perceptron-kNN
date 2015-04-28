/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perceptron;

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
import java.util.Random;
import java.util.Set;

/**
 *
 * @author panindra
 */
public class PerceptronEmailClassification {    
    Map<Integer, ArrayList<Double>> mweightVectorMap = new HashMap<>();
    Map<Integer, ArrayList<Integer>> mConfusionMap = new HashMap<>();
    Set<String> mUniqueDictSet = new HashSet<>();
    int mTValue = 0;
    double mAlphaFactor = 1;
    int mAccuracy = 0;
    int mNumOfClasses = 8;
    
    
    
    public void computeEmailClassification(int epoch) throws IOException {
        String filename = "8category.training.txt";
        computeUniqueDict(filename);                
        initializeVectorMaps();
                
        computeTrainingModel(filename, epoch);
        
        applyOnData(filename);
        
        mTValue = 0;
        mAccuracy = 0;
        mConfusionMap.clear();
                
        filename = "8category.testing.txt";
        applyOnData(filename);
        computeConfusionMatrix();        
    }
    
    void computeUniqueDict(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        
        String line;       
        
        while ((line = br.readLine()) != null) {
            String[] lineArr = line.split(" ");
            for(String word : lineArr) {
                if(word.contains(":")) {
                    String key = word.split(":")[0];                    
                    mUniqueDictSet.add(key);                    
                }                
            }                                        
         }
    }
    
    void computeTrainingModel(String filename, int epoch) throws FileNotFoundException, IOException {
        File file = new File(filename);
        
        ArrayList<String> list = new ArrayList<>(mUniqueDictSet);
        Collections.sort(list);
        
        ArrayList<String> wordsInDOc = new ArrayList<>();
        ArrayList<Integer> vectorOfEachDoc = new ArrayList<>();                
        
        int correctClass = 0;        
        String line = null;
        
        for(int i = 0; i < epoch; i++) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                vectorOfEachDoc = new ArrayList<>();
                wordsInDOc.clear();

                String[] lineArr = line.split(" ");            
                correctClass = Integer.parseInt(lineArr[0]);

                for(String word : lineArr) {
                    if(word.contains(":")) 
                        wordsInDOc.add(word.split(":")[0]);
                }
                Collections.sort(wordsInDOc);

                for(String word : mUniqueDictSet) {                
                    vectorOfEachDoc.add(Collections.frequency(wordsInDOc, word));                
                }                        

                processClass(vectorOfEachDoc, correctClass);
            }        
        }
    }
        
    void computeConfusionMatrix() {
        ArrayList<Integer> totalValMat  = new ArrayList<>();
        for(int index = 0 ; index < mConfusionMap.size(); index++) {
            int val = 0;
            for(int col = 0 ; col < mConfusionMap.size(); col++) {
                val+= mConfusionMap.get(index).get(col);                
            }            
            totalValMat.add(val);
            //System.out.println("Confusion Matrix :" + confusionMap.get(index));            
        }
        
        for(int index = 0 ; index < mConfusionMap.size(); index++) {
            for(int col = 0 ; col < mConfusionMap.size(); col++) {
                System.out.print(String.format("%6.2f ",((double)(mConfusionMap.get(index).get(col)) / totalValMat.get(index) * 100)));            
            }
            System.out.println(" ");
        }    
    }
    
    void applyOnData(String filename) throws IOException {
        //For testing
        filename = filename;
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        ArrayList<String> wordsInDOc = new ArrayList<>();
        ArrayList<Integer> vectorOfEachDoc = new ArrayList<>();                
        int correctClass;
        
        int totalTestEntries = 0;
        while ((line = br.readLine()) != null) {                       
            vectorOfEachDoc = new ArrayList<>();
            wordsInDOc.clear();
            
            String[] lineArr = line.split(" "); 
            correctClass = Integer.parseInt(lineArr[0]);
            
            for(String word : lineArr) {
                if(word.contains(":")) 
                    wordsInDOc.add(word.split(":")[0]);
            }
            
            Collections.sort(wordsInDOc);
            for(String word : mUniqueDictSet) {                
                vectorOfEachDoc.add(Collections.frequency(wordsInDOc, word));                
            }
            
            checkAccuracy(vectorOfEachDoc, correctClass);
            
            totalTestEntries++;            
        }        
        
        System.out.println("The Correctly classified classes are "+ mAccuracy); 
        System.out.println("The accuracy is "+ mAccuracy * 1.0/totalTestEntries);    
    }
    
    void checkAccuracy(ArrayList<Integer> linesToProcess, int correctClass) {
        int cls = giveClassifiedClass(linesToProcess);
               
        if(correctClass == cls) {
            mAccuracy++;
        }        
        
        if(mConfusionMap.containsKey(correctClass)) {                    
            ArrayList<Integer> confusionVals =mConfusionMap.get(correctClass);
            confusionVals.set(cls, confusionVals.get(cls) + 1);
            mConfusionMap.put(correctClass, confusionVals);
        }
        else {
            ArrayList<Integer> confusionVals = new ArrayList<>(mNumOfClasses);
            for(int m = 0 ; m < mNumOfClasses; m++) 
               confusionVals.add(0);

            confusionVals.set(cls, 1);
            mConfusionMap.put(correctClass, confusionVals);
        }                                 
    }
    
    int giveClassifiedClass(ArrayList<Integer> linesToProcess) {
        ArrayList<Double> values = new ArrayList<>();
        for(int key = 0; key < mNumOfClasses; key++) {
            ArrayList<Double> weightVector =  mweightVectorMap.get(key);
            int bias = 1;                
            double value = 0;
            
            for(int x = 0; x < linesToProcess.size(); x++){                                
                value = value + (weightVector.get(x) * linesToProcess.get(x) +  bias);                
            }
            values.add(value);
        }   
        //System.out.println("Values :" + values);
        double max = values.get(0);
        int cls = 0;
        for(int i = 0; i < mNumOfClasses; i++) {            
            if(values.get(i) > max) {
                max = values.get(i);
                cls = i;
            }
        }
        return cls;
    }
    
    void processClass(ArrayList<Integer> linesToProcess, int correctClass){                 
        
        mTValue++;
        mAlphaFactor =  (1000 * 1.0/ (1000 + mTValue));
        
        int cls = giveClassifiedClass(linesToProcess);
        
        if(correctClass != cls) {            
            ArrayList<Double> weightVector =  mweightVectorMap.get(correctClass);

            for(int x = 0; x < linesToProcess.size(); x++){                                
                weightVector.set(x, weightVector.get(x) + (mAlphaFactor * linesToProcess.get(x)) );                                                  
            }            
            mweightVectorMap.put(correctClass, weightVector);
                        
            weightVector =  mweightVectorMap.get(cls);
            for(int x = 0; x < linesToProcess.size(); x++){                                
                weightVector.set(x, weightVector.get(x) - (mAlphaFactor * linesToProcess.get(x)) );                                  
            }
            mweightVectorMap.put(cls, weightVector);            
        }
    }
    
    void initializeVectorMaps() {
        Random ran = new Random();
        ArrayList<Double> randArr;
        for(int i =0; i < mNumOfClasses; i ++) {            
            randArr = new ArrayList<>();
            double x = ran.nextInt(5);
            
            for(int j =0; j < mUniqueDictSet.size(); j ++) {                
                randArr.add(x);
            }
            mweightVectorMap.put(i, randArr);            
        }
    }    
}

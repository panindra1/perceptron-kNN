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
    static Map<Integer, ArrayList<Double>> mweightVectorMap = new HashMap<>();
    static Set<String> mUniqueDictSet = new HashSet<>();
    static int mTValue = 0;
    static double mAlphaFactor = 1;
    static int mAccuracy = 0;
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
                
        String filename = "8category.training.txt";
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
        
        initializeVectorMaps();
        
        ArrayList<String> list = new ArrayList<>(mUniqueDictSet);
        Collections.sort(list);
        
        ArrayList<String> wordsInDOc = new ArrayList<>();
        ArrayList<Integer> vectorOfEachDoc = new ArrayList<>();                
        
        int correctClass = 0;
        int epoch = 20;
        
        for(int i = 0; i < epoch; i++) {
            br = new BufferedReader(new FileReader(file));
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
        
        
          //For testing
        filename = "8category.testing.txt";
        file = new File(filename);
        br = new BufferedReader(new FileReader(file));
                                
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
    
    static void checkAccuracy(ArrayList<Integer> linesToProcess, int correctClass) {
        ArrayList<Double> values = new ArrayList<>();
        for(int key = 0; key < 10; key++) {
            ArrayList<Double> weightVector =  mweightVectorMap.get(key);            
            double value = 0;
            
            for(int x = 0; x < linesToProcess.size(); x++){                                
                value = value + (weightVector.get(x) * linesToProcess.get(x));                
            }
            values.add(value);
        }   

        double max = -1;int cls = 0;
        for(int i = 0; i < 10; i++) {            
            if(values.get(i) > max) {
                max = values.get(i);
                cls = i;
            }
        }

        if(correctClass == cls) {
            mAccuracy++;
        }
        
    }
    
    public static void processClass(ArrayList<Integer> linesToProcess, int correctClass){         
        mTValue++;
        mAlphaFactor =  (1000 * 1.0/ (1000 + mTValue));
        
        ArrayList<Double> values = new ArrayList<>();
        for(int key = 0; key < 10; key++) {
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
        for(int i = 0; i < 10; i++) {            
            if(values.get(i) > max) {
                max = values.get(i);
                cls = i;
            }
        }
        //System.out.println("Actual CLass :" + correctClass);
        //System.out.println("Predicted CLass :" + cls);
        
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
    
    static void initializeVectorMaps() {
        Random ran = new Random();
        ArrayList<Double> randArr;
        for(int i =0; i < 10; i ++) {            
            randArr = new ArrayList<>();
            double x = ran.nextInt(10);
            
            for(int j =0; j < mUniqueDictSet.size(); j ++) {                
                randArr.add(x);
            }
            mweightVectorMap.put(i, randArr);            
        }
    }
    
}

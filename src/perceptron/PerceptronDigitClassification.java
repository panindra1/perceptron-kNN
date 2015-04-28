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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author panindra
 */
public class PerceptronDigitClassification {

    /**
     * @param args the command line arguments
     */
    Map<Integer, ArrayList<Double>> mweightVectorMap = new HashMap<>();
    Map<Integer, ArrayList<Integer>> mConfusionMap = new HashMap<>();    
    int mTValue = 0;
    final int mNumOfClasses = 10;
    double mAlphaFactor = 1;
    int mAccuracy = 0;
        
    
    void computeDigitClassification(int epoch) throws FileNotFoundException, IOException {
        initializeVectorMaps();
        
        String filename = "traininglabels";
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;       
        
        ArrayList<Integer> indexOfClass = new ArrayList<>();
        
        while ((line = br.readLine()) != null) {
            indexOfClass.add(Integer.parseInt(line));
        }
                
                
        filename = "trainingimages";
        computeTrainingModel(filename, indexOfClass, epoch);
        
        //For testing
        filename = "testlabeles";
        applyOnData(filename);
        computeConfusionMatrix();        
    }
    
    void computeTrainingModel(String filename, ArrayList<Integer> indexOfClass, int epoch) throws FileNotFoundException, IOException {
        File file = new File(filename);
        
        String line = null;
        BufferedReader br = null;           
       
        for(int i = 0; i < epoch; i++) {
            ArrayList<char[]> linesToProcess = new ArrayList<>();
            int lineNum = 0;        
            int classNum = 0;
        
            
            br = new BufferedReader(new FileReader(file));               
            while ((line = br.readLine()) != null) {            
                linesToProcess.add(line.toCharArray());                    
                if(lineNum == 27) {                
                    lineNum = 0;                
                    processClass(convertToList(linesToProcess), indexOfClass.get(classNum));                
                    linesToProcess.clear();
                    classNum++;
                }
                else {
                    lineNum++;                    
                }
            }
        }
     }
     
    void applyOnData(String filename) throws FileNotFoundException, IOException {
        filename = "testlabels";
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
                        
        ArrayList<Integer> indexOfClass = new ArrayList<>();
        int i = 0;
        String line = null;
        
        while ((line = br.readLine()) != null) {
            indexOfClass.add(Integer.parseInt(line));
            i++;
        }
        
        int lineNum = 0;
        int classNum = 0;
        ArrayList<char[]> linesToProcess = new ArrayList<>();
        filename = "testimages";
        file = new File(filename);
        br = new BufferedReader(new FileReader(file));

        while ((line = br.readLine()) != null) {
            linesToProcess.add(line.toCharArray());
            if (lineNum == 27) {
                lineNum = 0;
                checkAccuracy(convertToList(linesToProcess), indexOfClass.get(classNum));
                linesToProcess.clear();
                classNum++;
            } else {
                lineNum++;                
            }
        }
        System.out.println("The Correctly classified classes are "+ mAccuracy); 
        System.out.println("The accuracy is "+ mAccuracy * 1.0/i);
        
    }
    void processClass(ArrayList<Integer> linesToProcess, int correctClass){         
        int cls = giveClassifiedClass(linesToProcess);
        mTValue++;
        mAlphaFactor =  (1000 * 1.0/ (1000 + mTValue));
                
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
    
    void initializeVectorMaps() {
        Random ran = new Random();
        ArrayList<Double> randArr;
        for(int i =0; i < mNumOfClasses; i ++) {            
            randArr = new ArrayList<>();
            double x = ran.nextInt(mNumOfClasses);
            
            for(int j =0; j < 784; j ++) {                
                randArr.add(x);
            }
            mweightVectorMap.put(i, randArr);
            
        }
    }
    
    ArrayList<Integer> convertToList(ArrayList<char[]> linesToProcess) {
        ArrayList<Integer> intArrayList = new ArrayList<>();
        int lineLength = linesToProcess.get(0).length;
        for(int ctr = 0; ctr < lineLength; ctr++){            
            char[] currLine = linesToProcess.get(ctr);
            for(int x = 0; x < linesToProcess.size(); x++){                 
                if((currLine[x] == ' ') || currLine[x] == 0){
                    intArrayList.add(0);
                }
                else {
                    intArrayList.add(1);
                }                
            }
            
        }
      return intArrayList;
    }            
}

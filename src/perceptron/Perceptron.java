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
public class Perceptron {

    /**
     * @param args the command line arguments
     */
    static Map<Integer, ArrayList<Double>> mweightVectorMap = new HashMap<>();
    static int mTValue = 0;
    static double mAlphaFactor = 1;
    static int mAccuracy = 0;
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        initializeVectorMaps();
        
        String filename = "traininglabels";
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
                
        String line;       
        ArrayList<Integer> indexOfClass = new ArrayList<>();
        
        while ((line = br.readLine()) != null) {
            indexOfClass.add(Integer.parseInt(line));
        }
                      
        int epoch = 5;
                
        filename = "trainingimages";
        file = new File(filename);
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
        
        //For testing
        filename = "testlabels";
        file = new File(filename);
        br = new BufferedReader(new FileReader(file));
                        
        indexOfClass = new ArrayList<>();
        int i = 0;
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

        double max = -1;
        int cls = 0;
        for(int i = 0; i < 10; i++) {
            
            if(values.get(i) > max) {
                max = values.get(i);
                cls = i;
            }
        }

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
    
    static void initializeVectorMaps() {
        Random ran = new Random();
        ArrayList<Double> randArr;
        for(int i =0; i < 10; i ++) {            
            randArr = new ArrayList<>();
            
            
            for(int j =0; j < 784; j ++) {
                double x = ran.nextInt(100);
                randArr.add(x);
            }
            mweightVectorMap.put(i, randArr);
            
        }
    }
    
    static ArrayList<Integer> convertToList(ArrayList<char[]> linesToProcess) {
        ArrayList<Integer> intArrayList = new ArrayList<>();
        int lineLength = linesToProcess.get(0).length;
        for(int ctr = 0; ctr < lineLength; ctr++){
            int[] intArr = new int[lineLength];
            
            for(int x = 0; x < linesToProcess.size(); x++){ 
                char[] currLine = linesToProcess.get(x);
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

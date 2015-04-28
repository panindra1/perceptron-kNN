/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perceptron;

import java.io.IOException;

/**
 *
 * @author panindra
 */
public class Perceptron {
    public static void main(String[] args) throws IOException {  
        int epoch = 10;
        //PerceptronDigitClassification pd = new PerceptronDigitClassification();
        //pd.computeDigitClassification(epoch);
        
        PerceptronEmailClassification pe = new PerceptronEmailClassification();
        pe.computeEmailClassification(epoch);
                        
    }            
}

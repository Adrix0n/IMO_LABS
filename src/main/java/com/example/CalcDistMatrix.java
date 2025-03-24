package com.example;

import java.util.ArrayList;

public class CalcDistMatrix {
    public static ArrayList<ArrayList<Long>> calcDistMatrix(ArrayList<ArrayList<Long>> arrayIn){
        ArrayList<ArrayList<Long>> arrayOut = new ArrayList<>();

        ArrayList<Long> array1;
        ArrayList<Long> array2;

        for(int i = 0;i<arrayIn.size();i++){
            array1 = arrayIn.get(i);
            ArrayList<Long> rowRes = new ArrayList<>();
            for(int j = 0; j<arrayIn.size();j++){
                array2 = arrayIn.get(j);
                Double a = Math.pow(Double.valueOf(array1.get(0)) - Double.valueOf(array2.get(0)),2.0);
                Double b = Math.pow(Double.valueOf(array1.get(1)) - Double.valueOf(array2.get(1)),2.0);
                Long distance = Math.round(Math.sqrt(a + b));
                rowRes.add(distance);
            }
            arrayOut.add(rowRes);
        }
        return arrayOut;
    }
}

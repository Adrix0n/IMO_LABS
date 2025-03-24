package com.example;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class DataLoader {
    public static ArrayList<ArrayList<Long>> dataLoader(String filename){
        ArrayList<ArrayList<Long>> array = new ArrayList<>();
        File file = new File(filename);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(filename);
        if( is== null){
            return array;
        }
        Scanner reader = new Scanner(is);
        while (reader.hasNextLine()) {
            String data = reader.nextLine();
            if(!Character.isDigit(data.charAt(0))){
                continue;
            }
            String[] parts = data.split(" ");
            ArrayList<Long> row = new ArrayList<>();
            row.add(Long.parseLong(parts[1]));
            row.add(Long.parseLong(parts[2]));
            array.add(row);
        }
        reader.close();
        return array;
    }
}

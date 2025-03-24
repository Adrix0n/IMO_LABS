package com.example.imolab1;

import java.util.ArrayList;
import java.util.Random;

public abstract class TSPAlgorithm {
    public ArrayList<ArrayList<Long>> distMat;
    int distMatStartSize;
    TSPAlgorithm(ArrayList<ArrayList<Long>> distMat){
        this.distMat = distMat;
        distMatStartSize = distMat.size();
    }
    public ArrayList<ArrayList<Long>> distMatNodes = new ArrayList<>();
    public ArrayList<ArrayList<Integer>> arrayOut = new ArrayList<>();
    public void genFirstNode(){
        Random random = new Random();
        int startIdx = random.nextInt(distMat.size());
        distMatNodes.add(distMat.get(startIdx));
        distMat.remove(distMat.get(startIdx));
    }

    public void calcEdges(){
        ArrayList<Integer> zeroIndices = new ArrayList<>();
        for(ArrayList<Long> arr : distMatNodes){
            for(int j = 0; j<arr.size();j++){
                if(arr.get(j)==0){
                    zeroIndices.add(j);
                }
            }
        }
        if (zeroIndices.size() < 2) return;
        for(int i = 0; i<zeroIndices.size()-1;i++){
            ArrayList<Integer> row = new ArrayList<>();
            row.add(zeroIndices.get(i));
            row.add(zeroIndices.get(i+1));
            arrayOut.add(row);
        }
        ArrayList<Integer> tmp = new ArrayList<>();
        tmp.add(zeroIndices.get(0));
        tmp.add(zeroIndices.get(zeroIndices.size()-1));
        arrayOut.add(tmp);
    }

    public ArrayList<ArrayList<Integer>> getEdges(){
        return arrayOut;
    }

    public void flushCycle(){
        distMatNodes.clear();
    }
    public abstract void algorithm(int numOfNodes);

    public void process(int numOfCycles){
        for(int i = 0; i < numOfCycles - 1; i++){
            genFirstNode();
            algorithm(distMatStartSize/numOfCycles);
            calcEdges();
            flushCycle();
        }
        genFirstNode();
        algorithm(distMat.size());
        calcEdges();
        flushCycle();
    }

    public int getNodeIndex(ArrayList<Long> node){
        for(int i = 0; i<node.size();i++){
            if(node.get(i)==0){
                return i;
            }
        }
        return 0;
    }

    public long calculateInsCost(ArrayList<Long> a, ArrayList<Long> b, ArrayList<Long> c) {
        int bIndex = getNodeIndex(b);
        int cIndex = getNodeIndex(c);
        return a.get(cIndex) + c.get(bIndex) - a.get(bIndex); // cost(a,c) + cost(c,b) - cost(a,b)
    }

}

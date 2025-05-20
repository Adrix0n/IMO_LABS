package com.example.imolab7;

import com.example.imolab1.TSPAlgorithm;

import java.util.ArrayList;
import java.util.Random;

public class MarkovTSP extends TSPAlgorithm {

    public ArrayList<ArrayList<Double>> probs;
    public ArrayList<Integer> usedIdx;
    public MarkovTSP(ArrayList<ArrayList<Long>> distMat, ArrayList<ArrayList<Double>> probs) {
        super(distMat);
        this.probs = probs;
        this.usedIdx = new ArrayList<>();
    }

    @Override
    public void algorithm(int numOfNodes) {
        ArrayList<Long> currEl = this.distMatNodes.get(0);
        int currElIdx = this.getNodeIndex(currEl);
        usedIdx.add(currElIdx);
        ArrayList<Long> newEl = new ArrayList<>();
        while (distMatNodes.size() < numOfNodes && !distMat.isEmpty()) {
            int choosedIdx = chooseIdx(probs.get(currElIdx));
            //System.out.println("size:" + distMatNodes.size());
            //TODO: Poprawić, bo często się powtarzaja
            while(usedIdx.contains(choosedIdx)){choosedIdx = chooseIdx(probs.get(currElIdx));}
            currElIdx = choosedIdx;
            for(int i = 0; i<distMat.size();i++){
                if(this.getNodeIndex(distMat.get(i))==choosedIdx){
                    newEl = distMat.get(i);
                    usedIdx.add(choosedIdx);
                    break;
                }
            }
            //checkProbs();
            updateProbs(choosedIdx);
            distMatNodes.add(newEl);
            distMat.remove(newEl);
        }
    }

    public static int chooseIdx(ArrayList<Double> probs){
        double los = Math.random();
        double sum = 0.0;

        for (int i = 0; i < probs.size(); i++) {
            sum += probs.get(i);
            if (los < sum) {
                return i;
            }
        }
        Random rand = new Random();

        return rand.nextInt(probs.size());
    }

    public void updateProbs(int choosedIdx){
        int size = this.probs.size();
        double epsilon = 0.000000001;

        for(int i = 0;i<size;i++){
            double del = this.probs.get(i).get(choosedIdx);
            if(del+epsilon>=1){
                del = 0.0;
            }
            for(int j = 0;j<size;j++){
                if(j==choosedIdx){
                    this.probs.get(i).set(j,0.0);
                }else{
                    this.probs.get(i).set(j, this.probs.get(i).get(j)/(1.0-del));
                }
            }
        }
    }

    public void checkProbs(){
        ArrayList<Double> sums = new ArrayList<>();
        for(int i =0 ;  i< this.probs.size();i++){
            double sum = 0.0;
            for(int j =0 ;  j< this.probs.size();j++){
                sum += this.probs.get(i).get(j);
            }
            sums.add(sum);
        }
        System.out.println(sums);
    }

}

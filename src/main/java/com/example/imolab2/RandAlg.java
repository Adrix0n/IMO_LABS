package com.example.imolab2;

import com.example.imolab1.TSPAlgorithm;

import java.util.ArrayList;
import java.util.Random;

public class RandAlg extends TSPAlgorithm {
    public RandAlg(ArrayList<ArrayList<Long>> distMat) {
        super(distMat);
    }
    @Override
    public void algorithm(int numOfNodes) {
        numOfNodes+=1;
        while (distMatNodes.size() < numOfNodes && !distMat.isEmpty()) {
            Random random = new Random();
            int rand = random.nextInt(distMat.size());
            ArrayList<Long> bestNode = distMat.get(rand);
            distMatNodes.add(bestNode);
            distMat.remove(bestNode);
        }
    }
}

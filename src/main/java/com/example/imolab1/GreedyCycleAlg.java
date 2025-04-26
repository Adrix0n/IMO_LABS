package com.example.imolab1;

import java.util.ArrayList;

public class GreedyCycleAlg extends TSPAlgorithm {
    public GreedyCycleAlg(ArrayList<ArrayList<Long>> distMat) {
        super(distMat);
    }
    @Override
    public void algorithm(int numOfNodes) {
        //numOfNodes+=1; //TODO: Do usunięcia
        while (distMatNodes.size() < numOfNodes && !distMat.isEmpty()) {
            ArrayList<Long> bestNode = null;
            int bestInsertionIndex = -1;
            long minInsertionCost = Long.MAX_VALUE;

            for (ArrayList<Long> node : distMat) {
                for (int i = 0; i < distMatNodes.size(); i++) {
                    long cost = calculateInsCost(distMatNodes.get(i), distMatNodes.get((i + 1) % distMatNodes.size()), node);
                    if (cost < minInsertionCost) {
                        minInsertionCost = cost;
                        bestNode = node;
                        bestInsertionIndex = i+1;
                    }
                }
            }

            if (bestNode != null) {
                distMatNodes.add(bestInsertionIndex, bestNode);
                distMat.remove(bestNode);
            } else {
                break;
            }
        }
    }
}

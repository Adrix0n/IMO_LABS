package com.example.imolab1;

import java.util.ArrayList;

public class WeightedRegretAlg extends TSPAlgorithm {
    long weightBest;
    long weightSecond;
    public WeightedRegretAlg(ArrayList<ArrayList<Long>> distMat, long weightBest, long weightSecond) {

        super(distMat);
        this.weightBest = weightBest;
        this.weightSecond = weightSecond;
        }

        @Override
        public void algorithm(int numOfNodes) {
            //numOfNodes+=1;
            while (distMatNodes.size() < numOfNodes && !distMat.isEmpty()) {
                ArrayList<ArrayList<Long>> regretList = new ArrayList<>();
                for (ArrayList<Long> node : new ArrayList<>(distMat)) {
                    long minCost = Long.MAX_VALUE;
                    long secMinCost = Long.MAX_VALUE;
                    long bestIdx = -1;
                    long secBestIdx = -1;
                    for (int i =0; i<distMatNodes.size();i++){
                        long cost = calculateInsCost(distMatNodes.get(i),distMatNodes.get((i+1)%distMatNodes.size()),node);
                        if (cost < minCost) {
                            secMinCost = minCost;
                            secBestIdx = bestIdx;
                            minCost = cost;
                            bestIdx = i + 1;
                        } else if (cost < secMinCost) {
                            secMinCost = cost;
                            secBestIdx = i + 1;
                        }
                    }
                    ArrayList<Long> regretInfo = new ArrayList<>();
                    regretInfo.add(bestIdx);
                    regretInfo.add(minCost);
                    regretInfo.add(secBestIdx);
                    regretInfo.add(secMinCost);

                    regretList.add(regretInfo);
                }

                long bestRegret = Long.MIN_VALUE;
                int regretListIdx = -1;
                for(int i = 0; i<regretList.size();i++){
                    long regret = -(weightBest * regretList.get(i).get(1) + weightSecond * regretList.get(i).get(3));
                    if(regret > bestRegret){
                        bestRegret = regret;
                        regretListIdx = i;
                    }
                }

                if (regretListIdx >-1) {
                    distMatNodes.add(regretList.get(regretListIdx).get(0).intValue(), distMat.get(regretListIdx));
                    distMat.remove(distMat.get(regretListIdx));
                }else{
                    break;
                }
            }
        }


}

package com.example.imolab1;

import java.util.ArrayList;

public class NearestNeighbourAlg extends TSPAlgorithm{
    public NearestNeighbourAlg(ArrayList<ArrayList<Long>> distMat) {
        super(distMat);
    }
    @Override
        public void algorithm(int numOfNodes) {
            ArrayList<Long> currEl = this.distMatNodes.get(0);
            int currElIdx = this.getNodeIndex(currEl);
            while (distMatNodes.size() < numOfNodes && !distMat.isEmpty()) {
                ArrayList<Long> newEl = distMat.get(0);
                Long minDist = newEl.get(currElIdx);

                for(ArrayList<Long> el : distMat){
                    int elIdx = this.getNodeIndex(el);
                    if(currEl.get(elIdx) < minDist){
                        newEl = el;
                        minDist = currEl.get(elIdx);
                    }
                }
                distMatNodes.add(newEl);
                currEl = newEl;
                distMat.remove(newEl);
            }
        }
}

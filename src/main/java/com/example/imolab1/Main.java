package com.example.imolab1;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;


import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import static com.example.CalcDistMatrix.calcDistMatrix;
import static com.example.DataLoader.dataLoader;
import static com.example.VisualizeResults.visualizeResults;

public class Main {




    public static ArrayList<ArrayList<Integer>> nearestNeighbourAlg(ArrayList<ArrayList<Long>> distMat) {
        class NnAlg extends TSPAlgorithm {
            NnAlg(ArrayList<ArrayList<Long>> distMat) {
                super(distMat);
            }

            @Override
            public void algorithm(int numOfNodes) {
                ArrayList<Long> currEl = this.distMatNodes.get(0);
                int currElIdx = this.getNodeIndex(currEl);
                for(int i = 0; i<numOfNodes;i++){
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
        ArrayList<ArrayList<Long>> fdistMat = new ArrayList<>(distMat);
        NnAlg algorithm = new NnAlg(fdistMat);
        algorithm.process(2);
        return algorithm.getEdges();
    }

    public static ArrayList<ArrayList<Integer>> greedyCycleAlg(ArrayList<ArrayList<Long>> distMat) {
        ArrayList<ArrayList<Long>> fdistMat = new ArrayList<>(distMat);
        GreedyCycleAlg algorithm = new GreedyCycleAlg(fdistMat);
        algorithm.process(2);
        return algorithm.getEdges();
    }
    public static ArrayList<ArrayList<Integer>> regretCycleAlg(ArrayList<ArrayList<Long>> distMat) {
        class RegretCycleAlg extends TSPAlgorithm {
            RegretCycleAlg(ArrayList<ArrayList<Long>> distMat) {
                super(distMat);
            }

            @Override
            public void algorithm(int numOfNodes) {
                numOfNodes+=1;
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
                        long regret = regretList.get(i).get(3) - regretList.get(i).get(1);
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
        ArrayList<ArrayList<Long>> fdistMat = new ArrayList<>(distMat);
        RegretCycleAlg algorithm = new RegretCycleAlg(fdistMat);
        algorithm.process(2);
        return algorithm.getEdges();
    }
    public static ArrayList<ArrayList<Integer>> regretCycleAlgWithWeights(ArrayList<ArrayList<Long>> distMat, long weightBest, long weightSecond) {
        class RegretCycleAlg extends TSPAlgorithm {
            RegretCycleAlg(ArrayList<ArrayList<Long>> distMat) {
                super(distMat);
            }

            @Override
            public void algorithm(int numOfNodes) {
                numOfNodes+=1;
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
        ArrayList<ArrayList<Long>> fdistMat = new ArrayList<>(distMat);
        RegretCycleAlg algorithm = new RegretCycleAlg(fdistMat);
        algorithm.process(2);
        return algorithm.getEdges();
    }


    public static Long countCost(ArrayList<ArrayList<Long>> distMat, ArrayList<ArrayList<Integer>> edges){
        Long sum = 0L;
        for(ArrayList<Integer> edge: edges){
            sum += distMat.get(edge.get(0)).get(edge.get(1));
        }
        return sum;
    }

    public static void main(String[] args) {
        String[] filenames = {"kroA200.tsp","kroB200.tsp"};
        ArrayList<ArrayList<ArrayList<Double>>> costs = new ArrayList<>();
        double done = 0;
        int iterations = 100;
        ArrayList<ArrayList<ArrayList<Integer>>> bestEdges = new ArrayList<>();
        for(String filename: filenames){
            ArrayList<ArrayList<Double>> costForInstance = new ArrayList<>();
            ArrayList<ArrayList<Long>> nodes = dataLoader(filename);
            ArrayList<ArrayList<Long>> distMat = calcDistMatrix(nodes);
            ArrayList<Double>  minMaxAvgCosts;

            bestEdges.add(new ArrayList<>());
            minMaxAvgCosts = new ArrayList<>();
            double minCost = Long.MAX_VALUE;
            double maxCost = Long.MIN_VALUE;
            double avgCost = 0.0;
            for(int i = 0; i<iterations; i++){
                ArrayList<ArrayList<Integer>> edges = nearestNeighbourAlg(distMat);
                long cost = countCost(distMat,edges);
                if(cost < minCost) {
                    minCost = cost;
                    bestEdges.set(bestEdges.size()-1,edges);
                }
                if(cost > maxCost) maxCost = cost;
                avgCost += cost;
                done += 1.0/iterations;
                if(i%8 == 0){
                    System.out.println("Ukończono: " + done/(double)(4*filenames.length)*100 + "%");
                }
            }
            avgCost = avgCost/(double)iterations;
            minMaxAvgCosts.add(minCost);
            minMaxAvgCosts.add(maxCost);
            minMaxAvgCosts.add(avgCost);
            costForInstance.add(minMaxAvgCosts);

            bestEdges.add(new ArrayList<>());
            minMaxAvgCosts = new ArrayList<>();
            minCost = Long.MAX_VALUE;
            maxCost = Long.MIN_VALUE;
            avgCost = 0.0;
            for(int i = 0; i<iterations; i++){
                ArrayList<ArrayList<Integer>> edges = greedyCycleAlg(distMat);
                long cost = countCost(distMat,edges);
                if(cost < minCost) {
                    minCost = cost;
                    bestEdges.set(bestEdges.size()-1,edges);
                }
                if(cost > maxCost) maxCost = cost;
                avgCost += cost;
                done += 1.0/iterations;
                if(i%8 == 0){
                    System.out.println("Ukończono: " + done/(double)(4*filenames.length)*100 + "%");
                }
            }
            avgCost = avgCost/(double)iterations;
            minMaxAvgCosts.add(minCost);
            minMaxAvgCosts.add(maxCost);
            minMaxAvgCosts.add(avgCost);
            costForInstance.add(minMaxAvgCosts);

            bestEdges.add(new ArrayList<>());
            minMaxAvgCosts = new ArrayList<>();
            minCost = Long.MAX_VALUE;
            maxCost = Long.MIN_VALUE;
            avgCost = 0.0;
            for(int i = 0; i<iterations; i++){
                ArrayList<ArrayList<Integer>> edges = regretCycleAlg(distMat);
                long cost = countCost(distMat,edges);
                if(cost < minCost) {
                    minCost = cost;
                    bestEdges.set(bestEdges.size()-1,edges);
                }
                if(cost > maxCost) maxCost = cost;
                avgCost += cost;
                done += 1.0/iterations;
                if(i%8 == 0){
                    System.out.println("Ukończono: " + done/(double)(4*filenames.length)*100 + "%");
                }
            }
            avgCost = avgCost/(double)iterations;
            minMaxAvgCosts.add(minCost);
            minMaxAvgCosts.add(maxCost);
            minMaxAvgCosts.add(avgCost);
            costForInstance.add(minMaxAvgCosts);

            bestEdges.add(new ArrayList<>());
            minMaxAvgCosts = new ArrayList<>();
            minCost = Long.MAX_VALUE;
            maxCost = Long.MIN_VALUE;
            avgCost = 0.0;
            for(int i = 0; i<iterations; i++){
                ArrayList<ArrayList<Integer>> edges = regretCycleAlgWithWeights(distMat,10,-9);
                long cost = countCost(distMat,edges);
                if(cost < minCost) {
                    minCost = cost;
                    bestEdges.set(bestEdges.size()-1,edges);
                }
                if(cost > maxCost) maxCost = cost;
                avgCost += cost;
                done += 1.0/iterations;
                if(i%8 == 0){
                    System.out.println("Ukończono: " + done/(double)(4*filenames.length)*100 + "%");
                }
            }
            avgCost = avgCost/(double)iterations;
            minMaxAvgCosts.add(minCost);
            minMaxAvgCosts.add(maxCost);
            minMaxAvgCosts.add(avgCost);
            costForInstance.add(minMaxAvgCosts);

            costs.add(costForInstance);

            for(ArrayList<ArrayList<Integer>> edges : bestEdges){
                visualizeResults(nodes,edges);
            }
            bestEdges = new ArrayList<>();
        }
        System.out.println(costs);
        String output ="";
        for(int i = 0; i<costs.size();i++){
            for(int j = 0; j <costs.get(i).size();j++){
                output = output + "min: " + costs.get(i).get(j).get(0) + ", ";
                output = output + "max: " + costs.get(i).get(j).get(1) + ", ";
                output = output + "avg: " + costs.get(i).get(j).get(2) + ";";
            }
            output +="\n";
        }
        System.out.println(output);
    }
}

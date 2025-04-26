package com.example.imolab4;

import com.example.imolab1.GreedyCycleAlg;
import com.example.imolab2.RandAlg;

import java.util.*;

import static com.example.CalcDistMatrix.calcDistMatrix;
import static com.example.DataLoader.dataLoader;
import static com.example.VisualizeResults.visualizeResults;
import static com.example.imolab2.Main.*;
import  com.example.imolab3.CandidatesLS;


public class Main {
    public static ArrayList<ArrayList<Integer>> copyEdges(ArrayList<ArrayList<Integer>> edges){
        ArrayList<ArrayList<Integer>> copyEdges = new ArrayList<>();
        for (ArrayList<Integer> edge : edges) {
            copyEdges.add((ArrayList<Integer>) edge.clone());
        }
        return copyEdges;
    }

    public static void perturbation1(ArrayList<ArrayList<Integer>> edges, boolean edgeSwap, boolean nodeSwap, int k){
        int cycle2startIdx = edges.size()/2;
        Random rand = new Random();
        int idx1 = -1, idx2 = -1;
        if(edgeSwap&&nodeSwap){k/=2;}
        if(edgeSwap){
            for(int i =0;i<k/2;i++){
                idx1 = rand.nextInt(edges.size()/2);
                do{idx2 = rand.nextInt(edges.size()/2);}while(idx1==idx2);
                swapEdges(edges,edges.get(idx1),edges.get(idx2));

                idx1 = rand.nextInt(edges.size()/2) + cycle2startIdx;
                do{idx2 = rand.nextInt(edges.size()/2) + cycle2startIdx;}while(idx1==idx2);
                swapEdges(edges,edges.get(idx1),edges.get(idx2));
            }
        }
        if(nodeSwap){
            for(int i =0;i<k;i++){
                //Poprawić bo znany problem z indeksem 0
                idx1 = rand.nextInt(edges.size()/2 - 1) + 1;
                idx2 = rand.nextInt(edges.size()/2 - 1) + cycle2startIdx + 1;
                swapNodes(edges,edges.get(idx1).get(0),edges.get(idx2).get(0));
            }
        }
    }

    public static void perturbation_destroy(ArrayList<ArrayList<Integer>> edges, int p){
        Random rand = new Random();
        int r = -1;
        ArrayList<Integer> idxRem = new ArrayList<>();
        for(int i =0; i<edges.size();i++){
            r = rand.nextInt(100);
            if(r>p) continue;
            idxRem.add(i);
        }
        for(int i=idxRem.size()-1;i>-1;i--){
            edges.remove((int)idxRem.get(i));
        }
    }

    public static ArrayList<ArrayList<Integer>> MSLS(ArrayList<ArrayList<Long>> distMat, int k, int times){
        ArrayList<ArrayList<Integer>> bestEdges = new ArrayList<>();
        for(int i = 0; i<times; i++){
            ArrayList<ArrayList<Long>> cDistMat = new ArrayList<>(distMat);
            RandAlg ra = new RandAlg(cDistMat);
            ra.process(2);
            ArrayList<ArrayList<Integer>> edgesRA = ra.getEdges();
            ArrayList<ArrayList<Integer>> candidates = CandidatesLS.findCandidates(distMat,k);
            while(CandidatesLS.findSteepestCandidates(distMat,edgesRA,candidates)){}
            if(bestEdges.isEmpty()){bestEdges = edgesRA;}
            else if(countCost(distMat,edgesRA) < countCost(distMat,bestEdges)){ bestEdges = edgesRA;}
        }
        return bestEdges;
    }

    public static ArrayList<ArrayList<Integer>> ILS(ArrayList<ArrayList<Long>> distMat, int  k, Long time){
        ArrayList<ArrayList<Integer>> bestEdges = new ArrayList<>();
        ArrayList<ArrayList<Long>> cDistMat = new ArrayList<>(distMat);
        RandAlg ra = new RandAlg(cDistMat);
        ra.process(2);
        ArrayList<ArrayList<Integer>> edgesRA = ra.getEdges();
        bestEdges = copyEdges(edgesRA);
        ArrayList<ArrayList<Integer>> candidates = CandidatesLS.findCandidates(distMat,k);
        Long startTime = System.nanoTime(), endTime = System.nanoTime();
        while(CandidatesLS.findSteepestCandidates(distMat,edgesRA,candidates)){}
        while(endTime-startTime<time){
            edgesRA = copyEdges(bestEdges);
            perturbation1(edgesRA,true,true,12);
            while(CandidatesLS.findSteepestCandidates(distMat,edgesRA,candidates)){}
            if(countCost(distMat,edgesRA) < countCost(distMat,bestEdges)){ bestEdges = copyEdges(edgesRA);}
            endTime = System.nanoTime();
        }
        return bestEdges;
    }

    public static ArrayList<Integer> getNodeCon(ArrayList<ArrayList<Integer>> edges, int numOfNodes){
        ArrayList<Integer> res = new ArrayList<>();
        for(int i = 0; i<numOfNodes;i++){
            int conCount = 0;
            for(ArrayList<Integer> edge : edges){
                if(edge.get(0) == i){
                    conCount+=1;
                }
                if(edge.get(1)==i){
                    conCount+=1;
                }
            }
            res.add(conCount);
        }
        return res;
    }

    public static void repairAlg(ArrayList<ArrayList<Integer>> edges, int numOfNodes){
        ArrayList<Integer> nodeCon = getNodeCon(edges, numOfNodes);
        //miejsce startu: max lancuch, losowy lancuch
        ArrayList<ArrayList<ArrayList<Integer>>> chainList = new ArrayList<>();
        // Idea łączenia łańcuchów poprzez np greedy search (najmniejszy koszt podłączenia łańcucha do tworzonego ciągu
        // Idea łączenia łańcuchów przy pomocy najbliższego sąsiada (patrzymy na krańce łańcuchów i łaczymy najbliższego

    }

    public static ArrayList<ArrayList<Integer>> LNS(ArrayList<ArrayList<Long>> distMat, int p, Long time){
        ArrayList<ArrayList<Integer>> bestEdges = new ArrayList<>();
        ArrayList<ArrayList<Long>> cDistMat = new ArrayList<>(distMat);
        RandAlg ra = new RandAlg(cDistMat);
        ra.process(2);
        ArrayList<ArrayList<Integer>> edgesRA = ra.getEdges();
        bestEdges = copyEdges(edgesRA);
        Long startTime = System.nanoTime(), endTime = System.nanoTime();
        while(endTime-startTime<time){
            edgesRA = copyEdges(bestEdges);
            perturbation_destroy(edgesRA,p);
            repairAlg(edgesRA, distMat.size());
            if(countCost(distMat,edgesRA) < countCost(distMat,bestEdges)){ bestEdges = copyEdges(edgesRA);}
            endTime = System.nanoTime();
        }
        return bestEdges;
    }

    public static double standardDeviation(List<Long> values) {
        int n = values.size();
        if (n == 0) return 0.0;

        double mean = values.stream().mapToLong(Long::longValue).average().orElse(0.0);

        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .sum() / n;

        return Math.sqrt(variance);
    }

    public static void main(String[] args) {
        // Testy
        //String[] filenames = {"kroA200.tsp","kroB200.tsp"};
        String[] filenames = {"test.tsp","test.tsp"};
        ArrayList<ArrayList<Long>> nodes = dataLoader(filenames[1]);
        ArrayList<ArrayList<Long>> distMat = calcDistMatrix(nodes);
        ArrayList<ArrayList<Long>> cDistMat;
        ArrayList<ArrayList<Integer>> edgesRA, copyEdges;

        ArrayList<ArrayList<Integer>> bestEdgesLM = null,bestEdgesCAN = null,bestEdgesST = null;

        Long minCostMSLS = Long.MAX_VALUE,minCostILS = Long.MAX_VALUE,minCostLNS = Long.MAX_VALUE;
        Long maxCostMSLS = 0L,maxCostILS = 0L,maxCostLNS = 0L;
        Long avgCostMSLS = 0L,avgCostILS = 0L,avgCostLNS = 0L;

        Long minTimeMSLS = Long.MAX_VALUE,minTimeILS = Long.MAX_VALUE,minTimeLNS = Long.MAX_VALUE;
        Long maxTimeMSLS = Long.MIN_VALUE,maxTimeILS = Long.MIN_VALUE,maxTimeLNS = Long.MIN_VALUE;
        Long avgTimeMSLS = 0L,avgTimeILS = 0L,avgTimeLNS = 0L;

        Long initCost = 0L, resCost = 0L, startTime = 0L, endTime = 0L, timeTime = 0L, timeMSLS = 0L;

        List<Long> costResMSLS = new ArrayList<>(),costResILS = new ArrayList<>(),costResLNS = new ArrayList<>();

        int iterations = 10;
        double done = 0.0;
        for(int i =0;i<iterations;i++){
            startTime = System.nanoTime();
            copyEdges = MSLS(distMat,11, 100);
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            timeMSLS = timeTime;

            avgTimeMSLS += timeTime;
            if(timeTime > maxTimeMSLS) maxTimeMSLS = timeTime;
            if(timeTime < minTimeMSLS) minTimeMSLS = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostMSLS += resCost;
            if(resCost>maxCostMSLS) maxCostMSLS = resCost;
            if(bestEdgesLM == null || resCost< minCostMSLS){
                bestEdgesLM = copyEdges(copyEdges);
                minCostMSLS = resCost;
            }
            costResMSLS.add(resCost);
            done+=1.0/(3.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");



            startTime = System.nanoTime();
            copyEdges = ILS(distMat,12,timeMSLS);
            endTime = System.nanoTime();
            timeTime = endTime - startTime;

            avgTimeILS += timeTime;
            if(timeTime > maxTimeILS) maxTimeILS = timeTime;
            if(timeTime < minTimeILS) minTimeILS = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostILS += resCost;
            if(resCost>maxCostILS) maxCostILS = resCost;
            if(bestEdgesCAN == null || resCost< minCostILS){
                bestEdgesCAN = copyEdges(copyEdges);
                minCostILS = resCost;
            }
            costResILS.add(resCost);
            done+=1.0/(3.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");




            startTime = System.nanoTime();
            copyEdges = LNS(distMat,30,timeMSLS);
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            avgTimeLNS += timeTime;
            if(timeTime > maxTimeLNS) maxTimeLNS = timeTime;
            if(timeTime < minTimeLNS) minTimeLNS = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostLNS += resCost;
            if(resCost>maxCostLNS) maxCostLNS = resCost;
            if(bestEdgesST == null || resCost< minCostLNS){
                bestEdgesST = copyEdges(copyEdges);
                minCostLNS = resCost;
            }
            costResLNS.add(resCost);
            done+=1.0/(3.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");
        }

        System.out.println("MSLS: " + avgCostMSLS/iterations + " (" +  minCostMSLS + " - " + maxCostMSLS + ")");
        System.out.println("ILS: " + avgCostILS/iterations + " (" +  minCostILS + " - " + maxCostILS + ")");
        System.out.println("LNS: "  + avgCostLNS/iterations + " (" +  minCostLNS + " - " + maxCostLNS + ")");

        double giga = 1000000000.0;
        System.out.println("Time MSLS: " + (double)avgTimeMSLS/iterations/giga  + " (" + (double)minTimeMSLS/giga + " - " + (double)maxTimeMSLS/giga +")");
        System.out.println("Time ILS: " + (double)avgTimeILS/iterations/giga  + " (" + (double)minTimeILS/giga + " - " + (double)maxTimeILS/giga +")");
        System.out.println("Time LNS: " + (double)avgTimeLNS/iterations/giga  + " (" + (double)minTimeLNS/giga + " - " + (double)maxTimeLNS/giga +")");



        System.out.println("SD MSLS:" + standardDeviation(costResMSLS));
        System.out.println("SD ILS:" + standardDeviation(costResILS));
        System.out.println("SD LNS:" + standardDeviation(costResLNS));


        System.out.println("Best MSLS edges:" + bestEdgesLM);
        System.out.println("Best ILS edges:" + bestEdgesCAN);
        System.out.println("Best LNS edges:" + bestEdgesST);


        visualizeResults(nodes,bestEdgesLM);
        visualizeResults(nodes,bestEdgesCAN);
        visualizeResults(nodes,bestEdgesST);
    }
}

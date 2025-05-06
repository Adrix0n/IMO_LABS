package com.example.imolab4;

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

        //pre-destroy
        edges.remove(edges.size()/2-1);
        edges.remove(edges.size()-1);


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

    public static ArrayList<ArrayList<Integer>> getChains(ArrayList<ArrayList<Integer>> edges, int numOfNodes){
        ArrayList<ArrayList<ArrayList<Integer>>> chainListEdges = new ArrayList<>();

        //TODO: Poprawić, pamiętać o cyklu i istnieniu połączenia ostatni - pierwszy edge
        // Chyba zrobione

        // Utworzenie listy łańcuchów w formie krawędzi
        ArrayList<ArrayList<Integer>> chain = new ArrayList<>();
        chain.add(edges.get(0));
        for(int i = 1; i < edges.size(); i++){
            if(chain.get(chain.size()-1).get(1)==edges.get(i).get(0)){
                chain.add(edges.get(i));
            }else{
                chainListEdges.add(copyEdges(chain)); //Może bez copyedges
                chain = new ArrayList<>();
                chain.add(edges.get(i));
            }
        }

        // Transformacja łańcuchów z formy krawędziowej na wierzchołkową
        ArrayList<ArrayList<Integer>> chainListNodes = new ArrayList<>();
        for(ArrayList<ArrayList<Integer>> chainE : chainListEdges) {
            ArrayList<Integer> chainNode = new ArrayList<>();
            for(int i = 0; i < chainE.size();i++){
                chainNode.add(chainE.get(i).get(0));
                if(i + 1 == chainE.size()){
                    chainNode.add(chainE.get(i).get(1));
                }
            }
            chainListNodes.add(chainNode);
        }

        // Dodanie wierzchołków nieposiadających krawędzi do listy
        ArrayList<Integer> hasEdges = new ArrayList<>(Collections.nCopies(200, 0));
        //System.out.println(hasEdges);
        for(ArrayList<Integer> chainN : chainListNodes){
            for (Integer integer : chainN) {
                hasEdges.set(integer, 1);
            }
        }
        for(int i = 0; i < numOfNodes; i++){
            if(hasEdges.get(i)<1){
                ArrayList<Integer> oneChain = new ArrayList<>();
                oneChain.add(i);
                chainListNodes.add(oneChain);
            }
        }

        return chainListNodes;
    }

    public static long countCostChain(Integer a, Integer b, Integer c, Integer d, ArrayList<ArrayList<Long>> distMat) {
        return distMat.get(a).get(b) + distMat.get(c).get(d) - distMat.get(a).get(d);
    }

    public static ArrayList<ArrayList<Integer>> greedyCycleRepair(ArrayList<ArrayList<Integer>> chainList,ArrayList<ArrayList<Long>> distMat, int numOfNodes, int numOfCycles){
        ArrayList<ArrayList<Integer>> resEdges = new ArrayList<>();
        int maxCycleSize = numOfNodes/numOfCycles;


        for(int kk = 0; kk<numOfCycles; kk++) {

            // wybierz losowy lancuch startowy gdzie size > 1
            Random rand = new Random();
            ArrayList<Integer> cycleChain = new ArrayList<>();
           // while (cycleChain.size() < 2) { // co jeśli zostaną tylko wierzchołki ??
                cycleChain = chainList.get(rand.nextInt(chainList.size()));
            //}
            chainList.remove(cycleChain);

            while (cycleChain.size() < maxCycleSize && !chainList.isEmpty()) {
                long minCost = Long.MAX_VALUE;
                int chainIdx = -1;
                boolean rev = false;
                int cycleChainIdx = -1;
                // Wyszukujemy najlepszego łańcucha do dodania
                for (int i = 0; i < cycleChain.size(); i++) {
                    for (int j = 0; j < chainList.size(); j++) {
                        long addCost = countCostChain(
                                cycleChain.get(i),
                                chainList.get(j).get(0),
                                chainList.get(j).get(chainList.get(j).size() - 1),
                                cycleChain.get((i + 1) % cycleChain.size()),
                                distMat
                        );
                        if (addCost < minCost) {
                            minCost = addCost;
                            chainIdx = j;
                            rev = false;
                            cycleChainIdx = i;
                        }
                        addCost = countCostChain(
                                cycleChain.get(i),
                                chainList.get(j).get(chainList.get(j).size() - 1),
                                chainList.get(j).get(0),
                                cycleChain.get((i + 1) % cycleChain.size()),
                                distMat
                        );
                        if (addCost < minCost) {
                            minCost = addCost;
                            chainIdx = j;
                            rev = true;
                            cycleChainIdx = i;
                        }
                    }
                }
                // Jeśli znaleziono łańcuch to dodajemy do cyklu
                if (chainIdx > -1) {
                    ArrayList<Integer> chainToAdd = chainList.get(chainIdx);
                    for (int i = 0; i < chainToAdd.size(); i++) {
                        if (rev) {
                            cycleChain.add(cycleChainIdx + 1, chainToAdd.get(i));
                        } else {
                            cycleChain.add(cycleChainIdx + 1, chainToAdd.get(chainToAdd.size() - 1 - i));
                        }
                    }
                    chainList.remove(chainIdx);
                }
            }
            // Usuwanie nadmiarowych wierzchołków, aby zachować rozmiar cyklu
            while (cycleChain.size() > maxCycleSize) {
                int nodeToRemove = -1;
                long bestDelta = Long.MAX_VALUE;
                for (int i = 0; i < cycleChain.size(); i++) {
                    int k = i - 1;
                    if (i == 0) {
                        k = cycleChain.size() - 1;
                    }
                    long delta = -countCostChain(
                            cycleChain.get(k),
                            cycleChain.get(i),
                            cycleChain.get(i),
                            cycleChain.get((i + 1) % cycleChain.size()),
                            distMat);
                    if (delta < bestDelta) {
                        bestDelta = delta;
                        nodeToRemove = i;
                    }
                }
                if (nodeToRemove > -1) {
                    ArrayList<Integer> oneNode = new ArrayList<>();
                    oneNode.add(cycleChain.get(nodeToRemove));
                    chainList.add(oneNode);
                    cycleChain.remove(nodeToRemove);
                }
            }

            // Dodanie krawędzi do wynikowego rozwiązania
            for (int i = 0; i < cycleChain.size(); i++) {
                ArrayList<Integer> edge = new ArrayList<>();
                edge.add(cycleChain.get(i));
                edge.add(cycleChain.get((i + 1) % cycleChain.size()));
                resEdges.add(edge);
            }

        }
        //System.out.println("GC repair edges"+resEdges);
        return resEdges;
    }

    public static ArrayList<ArrayList<Integer>> repairAlg(ArrayList<ArrayList<Integer>> edges, ArrayList<ArrayList<Long>> distMat, int numOfNodes){
        ArrayList<Integer> nodeCon = getNodeCon(edges, numOfNodes);
        //miejsce startu: max lancuch, losowy lancuch
        ArrayList<ArrayList<Integer>> chainList = getChains(edges, numOfNodes);
        //System.out.println("ChainList: " + chainList);
        // Idea łączenia łańcuchów poprzez np greedy search (najmniejszy koszt podłączenia łańcucha do tworzonego ciągu
        // Idea łączenia łańcuchów przy pomocy najbliższego sąsiada (patrzymy na krańce łańcuchów i łaczymy najbliższego
        return greedyCycleRepair(chainList, distMat, numOfNodes,2);
    }

    public static ArrayList<ArrayList<Integer>> LNS(ArrayList<ArrayList<Long>> distMat, int p, Long time, boolean withLS){
        Long startTime = System.nanoTime(), endTime = System.nanoTime();
        ArrayList<ArrayList<Integer>> bestEdges = new ArrayList<>();
        ArrayList<ArrayList<Long>> cDistMat = new ArrayList<>(distMat);
        RandAlg ra = new RandAlg(cDistMat);
        ra.process(2);
        ArrayList<ArrayList<Integer>> edgesRA = ra.getEdges();
        bestEdges = copyEdges(edgesRA);
        //System.out.println(bestEdges);
        if(withLS){
            while(findSwapGreedyAndSteepest(distMat,edgesRA,true,true));
        }
        Integer iterations =0;
        while(endTime-startTime<time){
            edgesRA = copyEdges(bestEdges);
            perturbation_destroy(edgesRA,p);
            edgesRA = repairAlg(edgesRA,distMat, distMat.size());
            if(withLS){
                while(findSwapGreedyAndSteepest(distMat,edgesRA,true,true));
            }
            if(countCost(distMat,edgesRA) < countCost(distMat,bestEdges)){ bestEdges = copyEdges(edgesRA);}
            endTime = System.nanoTime();
            iterations+=1;
        }
        ArrayList<Integer> addedge = new ArrayList<>();
        addedge.add(iterations);
        bestEdges.add(addedge);
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
        String[] filenames = {"kroA200.tsp","kroB200.tsp"};
        //String[] filenames = {"test.tsp","test.tsp"};
        ArrayList<ArrayList<Long>> nodes = dataLoader(filenames[0]);
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
        double giga = 1000000000.0;


        int iterations = 1;
        int knn = 12;
        int MSLStimes = 20;
        int ILSpertK = 12;
        int destroyPerc = 30;

        double done = 0.0;
        for(int i =0;i<iterations;i++){
            startTime = System.nanoTime();
            copyEdges = MSLS(distMat,knn+1, MSLStimes);
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
            System.out.println("Ukonczono: " +done*100+"%, Czas: " + (double)timeTime/giga + "s.");



            startTime = System.nanoTime();
            copyEdges = ILS(distMat,ILSpertK,timeMSLS);
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
            System.out.println("Ukonczono: " +done*100+"%, Czas: " + (double)timeTime/giga + "s.");




            startTime = System.nanoTime();
            copyEdges = LNS(distMat,destroyPerc,timeMSLS,true);
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
            System.out.println("Ukonczono: " +done*100+"%, Czas: " + (double)timeTime/giga + "s.");
        }

        System.out.println("MSLS: " + avgCostMSLS/iterations + " (" +  minCostMSLS + " - " + maxCostMSLS + ")");
        System.out.println("ILS: " + avgCostILS/iterations + " (" +  minCostILS + " - " + maxCostILS + ")");
        System.out.println("LNS: "  + avgCostLNS/iterations + " (" +  minCostLNS + " - " + maxCostLNS + ")");


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

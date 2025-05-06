package com.example.imolab5;

import com.example.imolab2.RandAlg;
import com.example.imolab3.CandidatesLS;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.example.CalcDistMatrix.calcDistMatrix;
import static com.example.DataLoader.dataLoader;
import static com.example.VisualizeResults.visualizeResults;
import static com.example.imolab2.Main.*;
import static com.example.imolab4.Main.*;


public class Main {
    public static ArrayList<ArrayList<Integer>> copyEdges(ArrayList<ArrayList<Integer>> edges){
        ArrayList<ArrayList<Integer>> copyEdges = new ArrayList<>();
        for (ArrayList<Integer> edge : edges) {
            copyEdges.add((ArrayList<Integer>) edge.clone());
        }
        return copyEdges;
    }

    public static ArrayList<EdgesWithCost> genStartPopulation(ArrayList<ArrayList<Long>> distMat,  int pop_size){
        ArrayList<EdgesWithCost> resList = new ArrayList<>();
        for(int i =0;i<pop_size;i++){
            ArrayList<ArrayList<Long>> cDistMat = new ArrayList<>(distMat);
            RandAlg ra = new RandAlg(cDistMat);
            ra.process(2);
            ArrayList<ArrayList<Integer>> edgesRA = ra.getEdges();
            //TODO: Może inne lokalne przeszukiwanie
            while(findSwapGreedy(distMat,edgesRA,50)){}
            long cost = countCost(distMat,edgesRA);
            EdgesWithCost ewc = new EdgesWithCost();
            ewc.edges = copyEdges(edgesRA);
            ewc.cost = cost;
            resList.add(ewc);
        }
        return resList;
    }

    public static EdgesWithCost recombine(EdgesWithCost ewc1, EdgesWithCost ewc2, ArrayList<ArrayList<Long>> distMat){
        ArrayList<ArrayList<Integer>> destroyedEdges = deleteOneFromAnother(ewc1.edges,ewc2.edges);
        ArrayList<ArrayList<Integer>> newEdges = repairAlg(destroyedEdges,distMat,ewc1.edges.size()/2);
        EdgesWithCost resEwc = new EdgesWithCost();
        resEwc.edges = newEdges;
        resEwc.cost = countCost(distMat,newEdges);
        return resEwc;
    }

    public static ArrayList<ArrayList<Integer>> deleteOneFromAnother(ArrayList<ArrayList<Integer>> edges_out,ArrayList<ArrayList<Integer>> edges_ref ){
        ArrayList<ArrayList<Integer>> resEdges = copyEdges(edges_out);
        ArrayList<Integer> idxToRemove = new ArrayList<>();
        for(int i = 0; i < edges_out.size();i++){
            for(int j = 0;j<edges_ref.size();j++){
                boolean toRemove = true;
                if(edges_out.get(i).equals(edges_ref.get(j))){
                    toRemove = false;
                }
                if(toRemove){
                    idxToRemove.add(i);
                }
            }
        }
        for(int i=idxToRemove.size()-1;i>-1;i--){
            resEdges.remove(i);
        }
        return resEdges;
    }

    public static void addOrDiscard(ArrayList<EdgesWithCost> population, EdgesWithCost newEwc){
        int worstIdx = -1;
        long worstCost = 0;
        for(int i = 0; i<population.size();i++){
            if(population.get(i).cost > worstCost){
                worstCost = population.get(i).cost;
                worstIdx = i;
            }
        }
        if(newEwc.cost < worstCost){
            for (EdgesWithCost edgesWithCost : population) {
                if (edgesWithCost.cost == newEwc.cost) {
                    return;
                }
            }
            population.remove(worstIdx);
            population.add(newEwc);
        }
    }

    public static ArrayList<ArrayList<Integer>> HAE(ArrayList<ArrayList<Long>> distMat,  int pop_size){
        ArrayList<EdgesWithCost> population = genStartPopulation(distMat,pop_size);
        Random rand = new Random();
        int idx1 = rand.nextInt(pop_size);
        int idx2 = rand.nextInt(pop_size);
        while(idx1==idx2){idx2 = rand.nextInt(pop_size);}
        EdgesWithCost newEwc = recombine(population.get(idx1),population.get(idx2),distMat);
        addOrDiscard(population,newEwc);

        int bestIdx = 0;
        long bestCost = Long.MAX_VALUE;
        for(int i =0; i<population.size();i++){
            if(population.get(i).cost<bestCost){
                bestCost = population.get(i).cost;
                bestIdx = i;
            }
        }
        return population.get(bestIdx).edges;
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

        ArrayList<EdgesWithCost> asd = genStartPopulation(distMat,20);
        for(EdgesWithCost asdasd: asd){
            System.out.println(asdasd.cost);
        }




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
            copyEdges = LNS(distMat,destroyPerc,timeMSLS);
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

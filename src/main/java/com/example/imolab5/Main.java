package com.example.imolab5;

import com.example.imolab1.GreedyCycleAlg;
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
        //System.out.println(destroyedEdges);
        if(destroyedEdges.isEmpty()){
            ArrayList<Integer> p = new ArrayList<>();
            p.add(0);
            p.add(1);
            destroyedEdges.add(p);
        }
        ArrayList<ArrayList<Integer>> newEdges = repairAlg(destroyedEdges,distMat,ewc1.edges.size());
        EdgesWithCost resEwc = new EdgesWithCost();
        resEwc.edges = newEdges;
        resEwc.cost = countCost(distMat,newEdges);
        return resEwc;
    }

    public static ArrayList<ArrayList<Integer>> deleteOneFromAnother(ArrayList<ArrayList<Integer>> edges_out,ArrayList<ArrayList<Integer>> edges_ref ){
        ArrayList<ArrayList<Integer>> resEdges = copyEdges(edges_out);
        ArrayList<Integer> idxToRemove = new ArrayList<>();
        resEdges.remove(resEdges.size()/2-1);
        resEdges.remove(resEdges.size()-1);
        for(int i = 0; i < resEdges.size();i++){
            boolean toRemove = true;
            for(int j = 0;j<edges_ref.size();j++){
                if(edges_out.get(i).equals(edges_ref.get(j))){
                    toRemove = false;
                    break;
                }
            }
            if(toRemove){
                idxToRemove.add(i);
            }
        }
        //System.out.println(idxToRemove);
        for(int i=idxToRemove.size()-1;i>-1;i--){
            resEdges.remove((int)idxToRemove.get(i));
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

    public static ArrayList<ArrayList<Integer>> HAE(ArrayList<ArrayList<Long>> distMat,  int pop_size, Long maxTime, boolean withLS){
        Long startTime = System.nanoTime();
        Long endTime = System.nanoTime();

        ArrayList<EdgesWithCost> population = genStartPopulation(distMat,pop_size);
        Random rand = new Random();
        Integer iterations = 0;
        while(endTime-startTime<maxTime){
            int idx1 = rand.nextInt(pop_size);
            int idx2 = rand.nextInt(pop_size);
            while(idx1==idx2){idx2 = rand.nextInt(pop_size);}
            EdgesWithCost newEwc = recombine(population.get(idx1),population.get(idx2),distMat);
            if(withLS){
                while(findSwapGreedyAndSteepest(distMat,newEwc.edges,false,true));
                newEwc.cost = countCost(distMat,newEwc.edges);
            }
            addOrDiscard(population,newEwc);
            endTime = System.nanoTime();
            iterations += 1;
        }

        int bestIdx = 0;
        long bestCost = Long.MAX_VALUE;
        for(int i =0; i<population.size();i++){
            if(population.get(i).cost<bestCost){
                bestCost = population.get(i).cost;
                bestIdx = i;
            }
        }

        ArrayList<ArrayList<Integer>> resEdges = population.get(bestIdx).edges;
        ArrayList<Integer> addedge = new ArrayList<>();
        addedge.add(iterations);
        resEdges.add(addedge);
        return resEdges;
    }

    public static ArrayList<ArrayList<Integer>> MSGC(ArrayList<ArrayList<Long>> distMat, long maxTime){
        long startTime = System.nanoTime();
        long endTime = System.nanoTime();
        ArrayList<ArrayList<Integer>> bestEdges = new ArrayList<>();
        Integer iterations = 0;
        while(endTime-startTime<maxTime){
            ArrayList<ArrayList<Long>> cDistMat = new ArrayList<>(distMat);
            GreedyCycleAlg gca = new GreedyCycleAlg(cDistMat);
            gca.process(2);
            ArrayList<ArrayList<Integer>> edgesGCA = gca.getEdges();
            if(bestEdges.isEmpty()){bestEdges = edgesGCA;}
            else if(countCost(distMat,edgesGCA) < countCost(distMat,bestEdges)){ bestEdges = edgesGCA;}
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


        //Testy:
        //LNS lp, LNS w/o lp, HAE w/o lp, HAE lp, GC MS
        // Zliczać liczbę iteracji
        // Testy
        String[] filenames = {"kroA200.tsp","kroB200.tsp"};
        //String[] filenames = {"test.tsp","test.tsp"};
        ArrayList<ArrayList<Long>> nodes = dataLoader(filenames[0]);
        ArrayList<ArrayList<Long>> distMat = calcDistMatrix(nodes);
        ArrayList<ArrayList<Long>> cDistMat;
        ArrayList<ArrayList<Integer>> edgesRA, copyEdges;

        ArrayList<ArrayList<Integer>> bestEdgesLNSlp = null,bestEdgesLNS = null, bestEdgesHAElp = null,bestEdgesHAE = null,bestEdgesMSGC= null;

        Long minCostLNSlp = Long.MAX_VALUE, minCostLNS = Long.MAX_VALUE, minCostHAElp = Long.MAX_VALUE, minCostHAE = Long.MAX_VALUE,minCostMSGC = Long.MAX_VALUE;
        Long maxCostLNSlp = 0L,maxCostLNS = 0L,maxCostHAElp = 0L,maxCostHAE = 0L,maxCostMSGC = 0L;
        Long avgCostLNSlp = 0L,avgCostLNS = 0L,avgCostHAElp = 0L,avgCostHAE = 0L,avgCostMSGC = 0L;

        Long minTimeLNSlp = Long.MAX_VALUE,minTimeLNS = Long.MAX_VALUE,minTimeHAElp = Long.MAX_VALUE,minTimeHAE = Long.MAX_VALUE,minTimeMSGC = Long.MAX_VALUE;
        Long maxTimeLNSlp = Long.MIN_VALUE,maxTimeLNS = Long.MIN_VALUE,maxTimeHAElp = Long.MIN_VALUE,maxTimeHAE = Long.MIN_VALUE,maxTimeMSGC = Long.MIN_VALUE;
        Long avgTimeLNSlp = 0L,avgTimeLNS = 0L,avgTimeHAElp = 0L,avgTimeHAE = 0L,avgTimeMSGC = 0L;

        Long minIterLNSlp = Long.MAX_VALUE,minIterLNS = Long.MAX_VALUE,minIterHAElp = Long.MAX_VALUE,minIterHAE = Long.MAX_VALUE,minIterMSGC = Long.MAX_VALUE;
        Long maxIterLNSlp = 0L,maxIterLNS = 0L,maxIterHAElp = 0L,maxIterHAE = 0L,maxIterMSGC = 0L;
        Long avgIterLNSlp = 0L,avgIterLNS = 0L,avgIterHAElp = 0L,avgIterHAE = 0L,avgIterMSGC = 0L;

        Long initCost = 0L, resCost = 0L, startTime = 0L, endTime = 0L, timeTime = 0L, timeMSLS = 0L;
        Integer iter = 0;

        List<Long> costResLNSlp = new ArrayList<>(),costResLNS = new ArrayList<>(),costResHAElp = new ArrayList<>(),costResHAE = new ArrayList<>(),costResMSGC = new ArrayList<>();
        double giga = 1000000000.0;

        int iterations = 5;
        int knn = 12;
        int MSLStimes = 20;
        int ILSpertK = 12;
        int destroyPerc = 30;
        int pop_size = 20;
        //KROA
        timeMSLS = 343 * (long)giga;
        //KROB
        //timeMSLS = 298 * (long)giga;
        //Test
        //timeMSLS = 10 * (long)giga;


        double done = 0.0;
        for(int i =0;i<iterations;i++){
            // 1
            startTime = System.nanoTime();
            copyEdges = LNS(distMat,destroyPerc,timeMSLS,true);
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            timeMSLS = timeTime;

            iter = copyEdges.get(copyEdges.size()-1).get(0);
            copyEdges.remove(copyEdges.size()-1);
            if(iter<minIterLNSlp){minIterLNSlp = (long)iter;}
            if(iter>maxIterLNSlp){maxIterLNSlp = (long)iter;}
            avgIterLNSlp += iter;

            avgTimeLNSlp += timeTime;
            if(timeTime > maxTimeLNSlp) maxTimeLNSlp = timeTime;
            if(timeTime < minTimeLNSlp) minTimeLNSlp = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostLNSlp += resCost;
            if(resCost>maxCostLNSlp) maxCostLNSlp = resCost;
            if(bestEdgesLNSlp == null || resCost< minCostLNSlp){
                bestEdgesLNSlp = copyEdges(copyEdges);
                minCostLNSlp = resCost;
            }
            costResLNSlp.add(resCost);
            done+=1.0/(5.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%, Czas: " + (double)timeTime/giga + "s.");


            // 2
            startTime = System.nanoTime();
            copyEdges = LNS(distMat,destroyPerc,timeMSLS,false);
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            timeMSLS = timeTime;

            iter = copyEdges.get(copyEdges.size()-1).get(0);
            copyEdges.remove(copyEdges.size()-1);
            if(iter<minIterLNS){minIterLNS = (long)iter;}
            if(iter>maxIterLNS){maxIterLNS = (long)iter;}
            avgIterLNS += iter;

            avgTimeLNS += timeTime;
            if(timeTime > maxTimeLNS) maxTimeLNS = timeTime;
            if(timeTime < minTimeLNS) minTimeLNS = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostLNS += resCost;
            if(resCost>maxCostLNS) maxCostLNS = resCost;
            if(bestEdgesLNS == null || resCost< minCostLNS){
                bestEdgesLNS = copyEdges(copyEdges);
                minCostLNS = resCost;
            }
            costResLNS.add(resCost);
            done+=1.0/(5.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%, Czas: " + (double)timeTime/giga + "s.");



            // 3
            startTime = System.nanoTime();
            copyEdges = HAE(distMat,pop_size,timeMSLS,true);
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            timeMSLS = timeTime;

            iter = copyEdges.get(copyEdges.size()-1).get(0);
            copyEdges.remove(copyEdges.size()-1);
            if(iter<minIterHAElp){minIterHAElp = (long)iter;}
            if(iter>maxIterHAElp){maxIterHAElp = (long)iter;}
            avgIterHAElp += iter;

            avgTimeHAElp += timeTime;
            if(timeTime > maxTimeHAElp) maxTimeHAElp = timeTime;
            if(timeTime < minTimeHAElp) minTimeHAElp = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostHAElp += resCost;
            if(resCost>maxCostHAElp) maxCostHAElp = resCost;
            if(bestEdgesHAElp == null || resCost< minCostHAElp){
                bestEdgesHAElp = copyEdges(copyEdges);
                minCostHAElp = resCost;
            }
            costResHAElp.add(resCost);
            done+=1.0/(5.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%, Czas: " + (double)timeTime/giga + "s.");



            // 4
            startTime = System.nanoTime();
            copyEdges = HAE(distMat,pop_size,timeMSLS,false);
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            timeMSLS = timeTime;

            iter = copyEdges.get(copyEdges.size()-1).get(0);
            copyEdges.remove(copyEdges.size()-1);
            if(iter<minIterHAE){minIterHAE = (long)iter;}
            if(iter>maxIterHAE){maxIterHAE = (long)iter;}
            avgIterHAE += iter;

            avgTimeHAE += timeTime;
            if(timeTime > maxTimeHAE) maxTimeHAE = timeTime;
            if(timeTime < minTimeHAE) minTimeHAE = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostHAE += resCost;
            if(resCost>maxCostHAE) maxCostHAE = resCost;
            if(bestEdgesHAE == null || resCost< minCostHAE){
                bestEdgesHAE = copyEdges(copyEdges);
                minCostHAE = resCost;
            }
            costResHAE.add(resCost);
            done+=1.0/(5.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%, Czas: " + (double)timeTime/giga + "s.");


            // 5
            startTime = System.nanoTime();
            copyEdges = MSGC(distMat,timeMSLS);
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            timeMSLS = timeTime;

            iter = copyEdges.get(copyEdges.size()-1).get(0);
            copyEdges.remove(copyEdges.size()-1);
            if(iter<minIterMSGC){minIterMSGC = (long)iter;}
            if(iter>maxIterMSGC){maxIterMSGC = (long)iter;}
            avgIterMSGC += iter;

            avgTimeMSGC += timeTime;
            if(timeTime > maxTimeMSGC) maxTimeMSGC = timeTime;
            if(timeTime < minTimeMSGC) minTimeMSGC = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostMSGC += resCost;
            if(resCost>maxCostMSGC) maxCostMSGC = resCost;
            if(bestEdgesMSGC == null || resCost< minCostMSGC){
                bestEdgesMSGC = copyEdges(copyEdges);
                minCostMSGC = resCost;
            }
            costResMSGC.add(resCost);
            done+=1.0/(5.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%, Czas: " + (double)timeTime/giga + "s.");
        }

        System.out.println("LNSlp: " + avgCostLNSlp/iterations + " (" +  minCostLNSlp + " - " + maxCostLNSlp + ")");
        System.out.println("LNS: " + avgCostLNS/iterations + " (" +  minCostLNS + " - " + maxCostLNS + ")");
        System.out.println("HAElp: "  + avgCostHAElp/iterations + " (" +  minCostHAElp + " - " + maxCostHAElp + ")");
        System.out.println("HAE: " + avgCostHAE/iterations + " (" +  minCostHAE + " - " + maxCostHAE + ")");
        System.out.println("MSGC: "  + avgCostMSGC/iterations + " (" +  minCostMSGC + " - " + maxCostMSGC + ")");


        System.out.println("Time LNSlp: " + (double)avgTimeLNSlp/iterations/giga  + " (" + (double)minTimeLNSlp/giga + " - " + (double)maxTimeLNSlp/giga +")");
        System.out.println("Time LNS: " + (double)avgTimeLNS/iterations/giga  + " (" + (double)minTimeLNS/giga + " - " + (double)maxTimeLNS/giga +")");
        System.out.println("Time HAElp: " + (double)avgTimeHAElp/iterations/giga  + " (" + (double)minTimeHAElp/giga + " - " + (double)maxTimeHAElp/giga +")");
        System.out.println("Time HAE: " + (double)avgTimeHAE/iterations/giga  + " (" + (double)minTimeHAE/giga + " - " + (double)maxTimeHAE/giga +")");
        System.out.println("Time MSGC: " + (double)avgTimeMSGC/iterations/giga  + " (" + (double)minTimeMSGC/giga + " - " + (double)maxTimeMSGC/giga +")");

        System.out.println("Iterations LNSlp: " + (double)avgIterLNSlp/iterations  + " (" + (double)minIterLNSlp + " - " + (double)maxIterLNSlp +")");
        System.out.println("Iterations LNS: " + (double)avgIterLNS/iterations  + " (" + (double)minIterLNS + " - " + (double)maxIterLNS +")");
        System.out.println("Iterations HAElp: " + (double)avgIterHAElp/iterations  + " (" + (double)minIterHAElp + " - " + (double)maxIterHAElp +")");
        System.out.println("Iterations HAE: " + (double)avgIterHAE/iterations  + " (" + (double)minIterHAE + " - " + (double)maxIterHAE +")");
        System.out.println("Iterations MSGC: " + (double)avgIterMSGC/iterations  + " (" + (double)minIterMSGC + " - " + (double)maxIterMSGC +")");


        System.out.println("Best LNSlp edges:" + bestEdgesLNSlp);
        System.out.println("Best LNS edges:" + bestEdgesLNS);
        System.out.println("Best HAElp edges:" + bestEdgesHAElp);
        System.out.println("Best HAE edges:" + bestEdgesHAE);
        System.out.println("Best MSGC edges:" + bestEdgesMSGC);


        visualizeResults(nodes,bestEdgesLNSlp);
        visualizeResults(nodes,bestEdgesLNS);
        visualizeResults(nodes,bestEdgesHAElp);
        visualizeResults(nodes,bestEdgesHAE);
        visualizeResults(nodes,bestEdgesMSGC);
    }
}

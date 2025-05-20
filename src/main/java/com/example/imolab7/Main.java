package com.example.imolab7;

import com.example.imolab1.GreedyCycleAlg;
import com.example.imolab1.NearestNeighbourAlg;
import com.example.imolab1.WeightedRegretAlg;
import com.example.imolab2.RandAlg;
import com.example.imolab3.CandidatesLS;
import com.example.imolab5.EdgesWithCost;

import java.util.ArrayList;
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

    public static ArrayList<ArrayList<ArrayList<Integer>>> genTrainSolutions(ArrayList<ArrayList<Long>>distMat, int n){
        ArrayList<ArrayList<ArrayList<Integer>>> res = new ArrayList<>();
        for(int i =0;i<n;i++){
            ArrayList<ArrayList<Long>> cDistMat = new ArrayList<>(distMat);
            ArrayList<ArrayList<Integer>> edges = new ArrayList<>();

//            // Algorytm random
//            RandAlg ra = new RandAlg(cDistMat);
//            ra.process(2);
//            edges = ra.getEdges();

            // Algorytm NN
//            NearestNeighbourAlg NNAlg = new NearestNeighbourAlg(cDistMat);
//            NNAlg.process(2);
//            edges = NNAlg.getEdges();

            //Algorytm z żalem ważonym
//            WeightedRegretAlg WRAlg = new WeightedRegretAlg(cDistMat,10,9);
//            WRAlg.process(2);
//            edges = WRAlg.getEdges();

            //GreedyCycles
            GreedyCycleAlg GCA = new GreedyCycleAlg(cDistMat);
            GCA.process(2);
            edges = GCA.getEdges();



            // Być może lokalne przeszukiwanie
            //while(findSwapGreedyAndSteepest(distMat,edges,false,false)||findSwapGreedyAndSteepest(distMat,edges,false,true)){}

            res.add(edges);
        }

        return res;
    }



    public static ArrayList<ArrayList<Double>> countProbs(ArrayList<ArrayList<ArrayList<Integer>>> edgesList){
        ArrayList<ArrayList<Double>> res = new ArrayList<>();
        for(int i = 0; i < edgesList.get(0).size();i++){
            ArrayList<Double> row = new ArrayList<>();
            for(int j = 0; j< edgesList.get(0).size();j++){
                row.add(0.0);
            }
            res.add(row);
        }
        ArrayList<Double> nodeOutputsCount = new ArrayList<>();
        for(int i = 0; i<edgesList.get(0).size();i++){
            nodeOutputsCount.add(0.0);
        }
        for(ArrayList<ArrayList<Integer>> edges: edgesList){
            for(ArrayList<Integer> edge: edges){
                nodeOutputsCount.set(edge.get(0),nodeOutputsCount.get(edge.get(0)) + 1.0);
                nodeOutputsCount.set(edge.get(1),nodeOutputsCount.get(edge.get(1)) + 1.0);
                res.get(edge.get(0)).set(edge.get(1),(double)res.get(edge.get(0)).get(edge.get(1)) + 1.0);
                res.get(edge.get(1)).set(edge.get(0),(double)res.get(edge.get(1)).get(edge.get(0)) + 1.0);
            }
        }
        //normalizacja
        for(int i = 0; i<res.size();i++){
            for(int j = 0; j<res.size();j++){
                res.get(i).set(j, res.get(i).get(j)/nodeOutputsCount.get(i));
            }
        }

        return res;
    }


    public static ArrayList<ArrayList<Integer>> LNS2(ArrayList<ArrayList<Long>> distMat, int p, Long time, boolean withLS, ArrayList<ArrayList<Integer>> edges){
        Long startTime = System.nanoTime(), endTime = System.nanoTime();
        ArrayList<ArrayList<Integer>> bestEdges = new ArrayList<>();
        ArrayList<ArrayList<Integer>> edgesRA = copyEdges(edges);
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

    public static void updateProbs(ArrayList<ArrayList<Double>> probs, ArrayList<ArrayList<Integer>> edges, double lr){
        int size = probs.size();

        for(int k = 0; k < edges.size(); k++){
            int choosedIdx = edges.get(k).get(1);
            int row = edges.get(k).get(0);
            double up = probs.get(row).get(choosedIdx) * lr;
            for(int j = 0; j < size; j++){
                if(j == choosedIdx){
                    probs.get(row).set(j, (probs.get(row).get(j)+up)/(1.0+up));
                }else{
                    probs.get(row).set(j, probs.get(row).get(j)/(1.0+up));
                }
            }
        }
    }
    public static ArrayList<ArrayList<Double>> deepCopyMatrix(ArrayList<ArrayList<Double>> matrix) {
        ArrayList<ArrayList<Double>> copy = new ArrayList<>();

        for (ArrayList<Double> row : matrix) {
            ArrayList<Double> newRow = new ArrayList<>(row); // kopiuje zawartość wiersza
            copy.add(newRow); // dodaje nowy wiersz do skopiowanej macierzy
        }

        return copy;
    }


    public static ArrayList<ArrayList<Integer>> markovAlg(ArrayList<ArrayList<Long>> distMat, long maxTime){
        long startTime = System.nanoTime();
        long endTime = System.nanoTime();
        long mTime = System.nanoTime();
        ArrayList<ArrayList<Long>> cDistMat = new ArrayList<>(distMat);
        long giga = 1000000000;
        ArrayList<ArrayList<Integer>> res;
        ArrayList<ArrayList<ArrayList<Integer>>> trainSolutions = genTrainSolutions(distMat,180);
        endTime = System.nanoTime();
        System.out.println("Czas generowania rozwiązań szkoleniowych: "+ (double)(endTime-startTime)/giga + " s.");
        mTime = System.nanoTime();
        ArrayList<ArrayList<Double>> probs = countProbs(trainSolutions);
        endTime = System.nanoTime();
        System.out.println("Czas liczenia prawdopodobieństw: "+ (double)(endTime-mTime)/giga + " s.");

        probs = addExplorationProb(probs,0.05);
        System.out.println(probs.get(0));
        long bestCost = Long.MAX_VALUE;
        ArrayList<ArrayList<Integer>> bestEdges = new ArrayList<>();
        // Czy markov z HAE?
        int iter = 0;
        while(endTime-startTime<maxTime){
            cDistMat = new ArrayList<>(distMat);
            MarkovTSP MarkovAlg = new MarkovTSP(cDistMat,deepCopyMatrix(probs));
            MarkovAlg.process(2);
            res = MarkovAlg.getEdges();
            while(findSwapGreedyAndSteepest(distMat,res,false,true)||findSwapGreedyAndSteepest(distMat,res,false,false)){}
            long cost = countCost(distMat,res);
            if(cost<bestCost){
                bestCost =cost;
                bestEdges = res;
                updateProbs(probs,res,0.4);
            }

            iter+=1;
            endTime = System.nanoTime();
        }
        System.out.println("Iters: "+iter);
        System.out.println(probs.get(0));

        return bestEdges;

    }



    public static ArrayList<ArrayList<Double>> addExplorationProb(ArrayList<ArrayList<Double>> probs, double p){
        ArrayList<ArrayList<Double>> res = new ArrayList<>();
        int size = probs.size();
        double full = 1/(double)size;
        for(int i = 0; i<size;i++){
            for(int j = 0; j<size;j++){
                probs.get(i).set(j, p*full + (1.0-p)*probs.get(i).get(j));
            }
        }
        return probs;
    }


    public static void main(String[] args) {
        String[] filenames = {"kroA200.tsp","kroB200.tsp"};
        long giga = 1000000000;

        long maxTime = 600*giga;

        ArrayList<ArrayList<Long>> nodes = dataLoader(filenames[0]);
        ArrayList<ArrayList<Long>> distMat = calcDistMatrix(nodes);
        ArrayList<ArrayList<Integer>> edges = markovAlg(distMat,maxTime);
        System.out.println(edges);
        System.out.println(countCost(distMat,edges));
        visualizeResults(nodes,edges);



    }
}

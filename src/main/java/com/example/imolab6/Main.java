package com.example.imolab6;

import com.example.imolab1.GreedyCycleAlg;
import com.example.imolab2.RandAlg;
import com.example.imolab5.EdgesWithCost;
import org.graphstream.graph.Edge;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static com.example.CalcDistMatrix.calcDistMatrix;
import static com.example.DataLoader.dataLoader;
import static com.example.VisualizeResults.visualizeResults;
import static com.example.imolab2.Main.*;
import static com.example.imolab4.Main.LNS;
import static com.example.imolab4.Main.repairAlg;


public class Main {
    public static ArrayList<ArrayList<Integer>> copyEdges(ArrayList<ArrayList<Integer>> edges){
        ArrayList<ArrayList<Integer>> copyEdges = new ArrayList<>();
        for (ArrayList<Integer> edge : edges) {
            copyEdges.add((ArrayList<Integer>) edge.clone());
        }
        return copyEdges;
    }

    // W zasadzie to samo co genStartPopulation
    public static ArrayList<EdgesWithCost> genEwc(ArrayList<ArrayList<Long>> distMat, int n){
        ArrayList<EdgesWithCost> res = new ArrayList<>();
        for(int i = 0; i < n; i++){
            ArrayList<ArrayList<Long>> cDistMat = new ArrayList<>(distMat);
            RandAlg ra = new RandAlg(cDistMat);
            ra.process(2);
            ArrayList<ArrayList<Integer>> edgesRA = ra.getEdges();
            while(findSwapGreedy(distMat,edgesRA,100)){}
            long cost = countCost(distMat,edgesRA);
            EdgesWithCost ewc = new EdgesWithCost();
            ewc.edges = copyEdges(edgesRA);
            ewc.cost = cost;
            res.add(ewc);
            if(i%10==0){
                System.out.println(i+"/"+n + " done");
            }
        }
        return res;
    }

    public static void saveEwc(ArrayList<EdgesWithCost> ewcList, String filename){
        //String asd = ewcList.get(0).edges.toString();
        //System.out.println(asd);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (EdgesWithCost ewc : ewcList) {
                writer.write(ewc.cost + ";" + ewc.edges.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<ArrayList<Integer>> parseEdges(String str){
        ArrayList<ArrayList<Integer>> res = new ArrayList<>();
        str = str.replaceAll("\\[\\[","").replaceAll("]]","");
        String[] edges = str.split("], \\[");
        for(String str_edge: edges){
            String[] numbers = str_edge.split(",");
            ArrayList<Integer> edgesArray = new ArrayList<>();
            for(String number : numbers){
                edgesArray.add(Integer.parseInt(number.trim()));
            }
            res.add(edgesArray);
        }
        return res;
    }

    public static ArrayList<EdgesWithCost> readEwc(String filename){
        ArrayList<EdgesWithCost> res = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] costEdges = line.split(";", 2);
                long cost = Long.parseLong(costEdges[0]);
                ArrayList<ArrayList<Integer>> edges = parseEdges(costEdges[1]);
                res.add(new EdgesWithCost(cost, edges));
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }



        return res;
    }

    public static double similarityNodePairs(ArrayList<ArrayList<Integer>> edgesA, ArrayList<ArrayList<Integer>> edgesB){
        int maxNodeNum = edgesA.size();
        double inSameCycleCount = 0.0;
        double compareCount = 0.0;
        int secondCycleStartIdx = edgesA.size()/2;
        for(int i = 0; i < maxNodeNum; i++){
            for(int j = i+1; j < maxNodeNum; j++){
                int idx1A = -1, idx1B = -1, idx2A = -1, idx2B = -1;
                for(int k = 0; k<edgesA.size();k++){
                    if(edgesA.get(k).get(0) == i){idx1A = k;}
                    if(edgesA.get(k).get(0) == j){idx2A = k;}
                    if(edgesB.get(k).get(0) == i){idx1B = k;}
                    if(edgesB.get(k).get(0) == j){idx2B = k;}
                }
                if(idx1A < secondCycleStartIdx && idx2A < secondCycleStartIdx){
                    compareCount += 1.0;
                    if(idx1B < secondCycleStartIdx && idx2B < secondCycleStartIdx){
                        inSameCycleCount += 1.0;
                    }
                    if(idx1B >= secondCycleStartIdx && idx2B >= secondCycleStartIdx){
                        inSameCycleCount += 1.0;
                    }
                }
                if(idx1A >= secondCycleStartIdx && idx2A >= secondCycleStartIdx){
                    compareCount += 1.0;
                    if(idx1B < secondCycleStartIdx && idx2B < secondCycleStartIdx){
                        inSameCycleCount += 1.0;
                    }
                    if(idx1B >= secondCycleStartIdx && idx2B >= secondCycleStartIdx){
                        inSameCycleCount += 1.0;
                    }
                }
            }
        }

        return inSameCycleCount/compareCount;
    }

    public static double similaritySameEdges(ArrayList<ArrayList<Integer>> edgesA, ArrayList<ArrayList<Integer>> edgesB){
        double sameEdgeCount = 0.0;
        for(int i = 0; i<edgesA.size();i++){
            ArrayList<Integer> edgeA = edgesA.get(i);
            for(int j = 0; j<edgesB.size();j++){
                ArrayList<Integer> edgeB = edgesB.get(j);
                if(edgeA.get(0) == edgeB.get(0) && edgeA.get(1) == edgeB.get(1)){
                    sameEdgeCount += 1.0;
                }
                if(edgeA.get(1) == edgeB.get(0) && edgeA.get(0) == edgeB.get(1)){
                    sameEdgeCount += 1.0;
                }
            }
        }

        return sameEdgeCount/(double)edgesA.size();
    }

    // Funkcja zapisjąca wyniki cost;sim
    // liczenie średniej sim dla 1000
    //wykresy python mpl

    public static void saveCostSim(ArrayList<Long> costs, ArrayList<Double> sim, String filename){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i<costs.size();i++) {
                writer.write(costs.get(i) + ";" + sim.get(i));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static double avgSim(EdgesWithCost ewc,ArrayList<EdgesWithCost> ewcList, boolean isSimNodePair, int UsedIdx){
        double res = 0.0;
        double count = ewcList.size();
        for(int i = 0; i < ewcList.size(); i++){
            if(i==UsedIdx){count-=1;continue;}
            if(isSimNodePair){
                res += similarityNodePairs(ewc.edges,ewcList.get(i).edges);
            }else{
                res += similaritySameEdges(ewc.edges,ewcList.get(i).edges);
            }
        }

        return res/count;
    }


    public static void main(String[] args) {

        String[] filenames = {"kroA200.tsp", "kroB200.tsp"};
        //String[] filenames = {"test.tsp","test.tsp"};

        ArrayList<ArrayList<Long>> nodes = dataLoader(filenames[0]);
        ArrayList<ArrayList<Long>> distMat = calcDistMatrix(nodes);
        ArrayList<EdgesWithCost> greedySolutions = new ArrayList<>();
        greedySolutions = genEwc(distMat, 1000);
        saveEwc(greedySolutions, "./solutionsKROA.txt");
        greedySolutions = readEwc("./solutionsKROA.txt");
        double sim = similarityNodePairs(greedySolutions.get(1).edges, greedySolutions.get(7).edges);
        System.out.println(sim);
        sim = similaritySameEdges(greedySolutions.get(1).edges, greedySolutions.get(7).edges);
        System.out.println(sim);

        System.exit(0);


        double perc = 0.1;
        //System.out.println(greedySolutions.get(0).edges);

        String[] fnames = {"KROA", "KROB"};
        long startTime,endTime,timeTime;
        double giga = 1000000000.0;
        double done = 0.0;
        for (int fn = 0; fn < 2; fn++) {
            // załaduj rozwiązania
            ArrayList<EdgesWithCost> solutions = readEwc("./solutions" + fnames[fn] + ".txt");
            ArrayList<EdgesWithCost> bestSolutionList = readEwc("./bestsolution" + fnames[fn] + ".txt");
            EdgesWithCost bestSolution = bestSolutionList.get(0);

            startTime = System.nanoTime();
            //Podobieństwo do najlepszego
            ArrayList<Long> costs = new ArrayList<>();
            ArrayList<Double> simsNP = new ArrayList<>();
            ArrayList<Double> simsED = new ArrayList<>();
            for (int i = 0; i < solutions.size(); i++) {
                costs.add(solutions.get(i).cost);
                simsNP.add(similarityNodePairs(bestSolution.edges, solutions.get(i).edges));
                simsED.add(similaritySameEdges(bestSolution.edges, solutions.get(i).edges));
            }
            saveCostSim(costs, simsNP, "./costsimNP" + fnames[fn] + ".txt");
            saveCostSim(costs, simsED, "./costsimED" + fnames[fn] + ".txt");
            endTime = System.nanoTime();
            timeTime = endTime- startTime;
            done+=1.0/(1001.0     * 2.0);
            System.out.println("Ukonczono: " +done*100+"%, Czas: " + (double)timeTime/giga + "s.");


            //Podobieństwo do solutions
            costs = new ArrayList<>();
            simsNP = new ArrayList<>();
            simsED = new ArrayList<>();
            ArrayList<EdgesWithCost> solutionsRed = new ArrayList<>();
            for(int i =0;i<(int)((double) solutions.size()*perc);i++){
                solutionsRed.add(solutions.get(i));
            }
            for (int i = 0; i < solutions.size(); i++) {
                startTime = System.nanoTime();
                costs.add(solutions.get(i).cost);
                simsNP.add(avgSim(solutions.get(i), solutionsRed, true, i));
                simsED.add(avgSim(solutions.get(i), solutionsRed, false, i));
                endTime = System.nanoTime();
                timeTime = endTime- startTime;
                done+=1.0/(1001.0 * 2.0);
                System.out.println("Ukonczono: " +done*100+"%, Czas: " + (double)timeTime/giga + "s.");
            }
            saveCostSim(costs, simsNP, "./costavgsimNP" + fnames[fn] + ".txt");
            saveCostSim(costs, simsED, "./costavgsimED" + fnames[fn] + ".txt");

        }
    }
}

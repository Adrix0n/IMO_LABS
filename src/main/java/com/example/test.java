package com.example;

import com.example.imolab1.GreedyCycleAlg;
import com.example.imolab2.RandAlg;

import java.util.ArrayList;

import static com.example.CalcDistMatrix.calcDistMatrix;
import static com.example.DataLoader.dataLoader;
import static com.example.VisualizeResults.visualizeResults;
import static com.example.imolab1.Main.nearestNeighbourAlg;

public class test {

    public static void main(String[] args) {
        ArrayList<ArrayList<Long>> nodes =  dataLoader("test10.tsp");
        ArrayList<ArrayList<Long>> distMat =  calcDistMatrix(nodes);

        RandAlg ra = new RandAlg(distMat);
        ra.process(2);
        ArrayList<ArrayList<Integer>> edges = ra.getEdges();
//
//        GreedyCycleAlg gcp = new GreedyCycleAlg(distMat);
//        gcp.process(2);
//        ArrayList<ArrayList<Integer>> edges = gcp.getEdges();
        System.out.println(edges);
        visualizeResults(nodes,edges);
    }
}

package com.example.imolab3;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.imolab2.Main.*;
import static com.example.imolab2.Main.swapEdges;

public class CandidatesLS {
    public static ArrayList<ArrayList<Integer>> findCandidates(ArrayList<ArrayList<Long>> distMat, int k){
        ArrayList<ArrayList<Integer>> candidates = new ArrayList<>();
        for(int i=0;i<distMat.size();i++){
            ArrayList<Integer> candidate = new ArrayList<>();
            ArrayList<Long> list = new ArrayList<>(distMat.get(i));
            Collections.sort(list);
            for(int j = 0;j<k;j++){
                for(int ii =0 ;ii< distMat.size();ii++){
                    if(list.get(j)==distMat.get(i).get(ii) && i!=ii){
                        candidate.add(ii);
                    }
                }
            }
            candidates.add(candidate);
        }
        return candidates;
    }

    public static ArrayList<ArrayList<Integer>> getEdgesFromNodes(ArrayList<ArrayList<Integer>> edges, int node1, int node2){
        ArrayList<ArrayList<Integer>> res = new ArrayList<>();
        int firstCycleEndIdx = edges.size()/2-1;
        int idx1 = -1;
        int idx2 = -1;
        for(int i=0;i<edges.size();i++){
            if(edges.get(i).get(0).equals(node1)){
                idx1 = i;
                res.add(edges.get(i));
            }
            if(edges.get(i).get(0).equals(node2)){
                idx2 = i;
                res.add(edges.get(i));
            }
        }
        ArrayList<Integer> t = new ArrayList<>();
        t.add(idx1);
        t.add(idx2);
        res.add(t);
        return res;
    }

    public static boolean findSteepestCandidates(ArrayList<ArrayList<Long>> distMat, ArrayList<ArrayList<Integer>> edges, ArrayList<ArrayList<Integer>> candidates){
        int firstCycleEndIdx = edges.size()/2-1;
        //swap edges
        long bestDelta = Long.MAX_VALUE;
        ArrayList<Integer> swapEdge1 = null,swapEdge2 = null;
        int swapNode1 = -1, swapNode2 = -1;
        for(int i=0;i<edges.size();i++){
            ArrayList<Integer> candidate = candidates.get(i);
            for(int j=0;j<candidate.size();j++){
                ArrayList<ArrayList<Integer>> edgesAndIndices =  getEdgesFromNodes(edges,i,candidate.get(j));
                //Czasem jakimś cudem nie znajduje jendej z krawędzi, wtedy skip
                if(edgesAndIndices.get(0).isEmpty() || edgesAndIndices.get(1).isEmpty()){
                    continue;
                }
                ArrayList<Integer> indices = edgesAndIndices.get(2);
                int idx1 = indices.get(0), idx2 = indices.get(1);
                // Sprawdzić czy w tym samym cyklu
                //System.out.println(edge1 +" "+ edge2);
                if((idx1<=firstCycleEndIdx&&idx2>firstCycleEndIdx)||(idx1>firstCycleEndIdx&&idx2<=firstCycleEndIdx)){
                    // swap nodes
                    int node2 = candidate.get(j);
                    long delta = calcDeltaNode(distMat,edges,i,node2);
                    if(delta<bestDelta){
                        ArrayList<ArrayList<Integer>> edges2 = new ArrayList<>();
                        for(ArrayList<Integer> edge: edges){
                            edges2.add((ArrayList<Integer>) edge.clone());
                        }
                        swapNodes(edges2,i,node2);
                        if(validateCycles(edges2)){
                            bestDelta = delta;
                            swapNode1 = i;
                            swapNode2 = node2;
                        }
                    }
                }else{
                    ArrayList<Integer> edge1 = edgesAndIndices.get(0);
                    ArrayList<Integer> edge2 = edgesAndIndices.get(1);
                    long delta = calcDelta(distMat,edge1,edge2);
                    if(delta<bestDelta){
                        bestDelta = delta;
                        swapEdge1 = edge1;
                        swapEdge2 = edge2;
                        swapNode1 = -1;
                        swapNode2 = -1;
                    }
                }
            }
        }

//        for(int i=0;i<candidates.size();i++){
//            int node1 = i;
//            ArrayList<Integer> candidate = candidates.get(i);
//            for(int j=0;j<candidate.size();j++){
//                int node2 = candidate.get(j);
//                long delta = calcDeltaNode(distMat,edges,node1,node2);
//                if(delta<bestDelta){
//                    bestDelta = delta;
//                    swapNode1 = node1;
//                    swapNode2 = node2;
//                }
//            }
//        }

        if(bestDelta<0){
            if(swapNode1>-1 && swapNode2 >-1){
                swapNodes(edges,swapNode1,swapNode2);
                return true;
            } else if (swapEdge1!=null && swapEdge2!=null) {
                swapEdges(edges,swapEdge1,swapEdge2);
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}

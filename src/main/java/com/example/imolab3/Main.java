package com.example.imolab3;

import com.example.imolab2.RandAlg;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.CalcDistMatrix.calcDistMatrix;
import static com.example.DataLoader.dataLoader;
import static com.example.VisualizeResults.visualizeResults;
import static com.example.imolab2.Main.*;

public class Main {

    public static Boolean validateCycles2(ArrayList<ArrayList<Integer>> edges, ArrayList<Integer> edge1, ArrayList<Integer> edge2){
        ArrayList<ArrayList<Integer>> edges2= new ArrayList<>();
        for(ArrayList<Integer> edge: edges){
            edges2.add((ArrayList<Integer>) edge.clone());
        }
        swapEdges(edges2,edge1,edge2);
        int cyclesCounter = 1;
        int niezgonoscCounter = cyclesCounter;
        ArrayList<ArrayList<Integer>> niezgodne = new ArrayList<>();
        for(int i=0;i<edges2.size()-1;i++){
            if(!Objects.equals(edges2.get(i).get(1), edges2.get(i + 1).get(0))){
                cyclesCounter+=1;
                if(cyclesCounter>niezgonoscCounter){
                    niezgonoscCounter+=1;
                    niezgodne.add(edges2.get(i));
                    niezgodne.add(edges2.get(i+1));
                }
            }

        }
        if(cyclesCounter>2){
            System.out.println("niezgodnosc");
            System.out.println(cyclesCounter);
            System.out.println(niezgodne);
            System.out.println(edges2);
//                System.out.println(edges.get(i));
//                System.out.println(edges.get(i+1));

        }
        return cyclesCounter == 2;
    }

    public static void findSwapSteepest(ArrayList<ArrayList<Long>> distMat, ArrayList<ArrayList<Integer>> edges, ArrayList<MoveWithScore> LM){
        int firstCycleEndIdx = findCycleEnd(edges);
        // swap edges

        // Przejśćie po pierwszym cyklu
        ArrayList<Integer> swapEdge1 = null,swapEdge2 = null;
        ArrayList<Integer> edge1, edge2;
        for(int i = 1; i<=firstCycleEndIdx; i++){
            for(int j = i+1; j<=firstCycleEndIdx;j++){
                edge1 = edges.get(i);
                edge2 = edges.get(j);
                if(Objects.equals(edge1.get(0), edge2.get(0))){
                    continue;
                }

                long delta = calcDelta(distMat,edge1,edge2);

                if(delta < 0){
                    if(validateCycles2(edges,edge1,edge2)){
                        ArrayList<ArrayList<Integer>> edgesList = new ArrayList<>();
                        edgesList.add(edge1);
                        edgesList.add(edge2);
                        MoveWithScore mwc = new MoveWithScore(edgesList,delta);
                        extentLM(LM,mwc);
                    }
                }
            }
        }

        //Przejście po drugim cyklu
        for(int i = firstCycleEndIdx+1+1; i<edges.size(); i++){
            for(int j = i+1; j<edges.size();j++){
                edge1 = edges.get(i);
                edge2 = edges.get(j);
                if(Objects.equals(edge1.get(0), edge2.get(0))){
                    continue;
                }

                long delta = calcDelta(distMat,edge1,edge2);
                if(delta < 0){
                    if(validateCycles2(edges,edge1,edge2)){
                        ArrayList<ArrayList<Integer>> edgesList = new ArrayList<>();
                        edgesList.add(edge1);
                        edgesList.add(edge2);
                        MoveWithScore mwc = new MoveWithScore(edgesList,delta);
                        extentLM(LM,mwc);
                    }
                }
            }
        }

        // swap nodes
        ArrayList<Integer> edge3 = null, edge4 = null;
        int node1 = -1, node2 = -1, swapNode1 = -1, swapNode2 = -1;

        // swap nodes between cycles
        for(int i = 1; i <= firstCycleEndIdx; i++){
            for(int j = firstCycleEndIdx+1+1; j < distMat.size(); j++){
                node1 = edges.get(i).get(0);
                node2 = edges.get(j).get(0);

                long delta = calcDeltaNode(distMat,edges,edges.get(i).get(0),edges.get(j).get(0));

                if(delta < 0 && !Objects.equals(edges.get(i).get(0), edges.get(j).get(0))){
                    ArrayList<ArrayList<Integer>> edges2 = new ArrayList<>();
                    for(ArrayList<Integer> edge: edges){
                        edges2.add((ArrayList<Integer>) edge.clone());
                    }
                    swapNodes(edges2,node1,node2);
                    if(validateCycles(edges2)){
                        ArrayList<ArrayList<Integer>> edgesList = nodeSwapToEdges(edges,node1,node2);
                        MoveWithScore mwc = new MoveWithScore(edgesList,delta);
                        extentLM(LM,mwc);
                    }
                }
            }
        }

//        if(bestDelta<0){
//            System.out.println("bestDelta: " + bestDelta);
//            if(swapNode1!=-1){
//                System.out.println("Podmieniono: "+swapNode1 + " " + swapNode2);
//                swapNodes(edges,swapNode2,swapNode1);
//            }else {
//                System.out.println("Podmieniono: "+swapEdge1 + " " + swapEdge2);
//                swapEdges(edges, swapEdge1, swapEdge2);
//            }
//        }else{
//            return false;
//        }
//        return true;
//
    }


    public static void extentLM(ArrayList<MoveWithScore> LM, MoveWithScore newMoves){
        // Czy nowy
        // Rozważyc odwrotną kolejność np. [1,30] i [30,1] to to samo



        MoveWithScore newMovesRev = new MoveWithScore();
        newMovesRev.score = newMoves.score;
        for(ArrayList<Integer> edge: newMoves.edgeList){
            ArrayList<Integer> edge2 = new ArrayList<>();
            edge2.add(edge.get(1));
            edge2.add(edge.get(0));
            newMovesRev.edgeList.add(edge2);
        }

        for(MoveWithScore mvc : LM){
            if(mvc.edgeList.equals(newMoves.edgeList) || mvc.edgeList.equals(newMovesRev.edgeList) ){
                return;
            }
        }

        //Dobra kolejność
        MoveWithScore copyToAdd = newMoves.copy();
        boolean notAdded = true;
        for(int i=0;i<LM.size();i++){
            if(copyToAdd.score < LM.get(i).score){
                LM.add(i,copyToAdd);
                notAdded = false;
                break;
            }
        }
        if(notAdded){
            LM.add(copyToAdd);
        }
    }


    public static ArrayList<ArrayList<Integer>> nodeSwapToEdges(ArrayList<ArrayList<Integer>> edges, Integer node1, Integer node2){
        ArrayList<Integer> edgeA0 = null, edgeA1 = null, edgeB0 = null ,edgeB1 = null;

        for(ArrayList<Integer> edge:  edges){
            if(Objects.equals(edge.get(1), node1)){
                edgeA0 = edge;
            }
            if(Objects.equals(edge.get(0), node1)){
                edgeA1 = edge;
            }
            if(Objects.equals(edge.get(1), node2)){
                edgeB0 = edge;
            }
            if(Objects.equals(edge.get(0), node2)){
                edgeB1 = edge;
            }
        }
        if(edgeA0==null || edgeA1==null || edgeB0==null || edgeB1==null){
            System.out.println(edgeA0);
            System.out.println(edgeA1);
            System.out.println(edgeB0);
            System.out.println(edgeB1);
            throw new RuntimeException("Krawędź jest nullem, a nie powinna");
        }

        // Chyba nie potrzebne tutaj sprawdznie
//        if(!(edgeB1==edgeA0)){
//            swapEdges(edges,edgeA0,edgeB1);
//        }
//        if(!(edgeA1==edgeB0)){
//            swapEdges(edges,edgeB0,edgeA1);
//        }

        ArrayList<ArrayList<Integer>> res = new ArrayList<>();
        res.add(edgeA0);
        res.add(edgeB1);

        res.add(edgeB0);
        res.add(edgeA1);

        return res;
    }

    public static int checkForApply(ArrayList<ArrayList<Integer>> edges, MoveWithScore mwc){
        int equals = 0;
        int size = mwc.edgeList.size();
        int equalsRev = 0;

        for(ArrayList<Integer> edge: mwc.edgeList){
            for(int i=0;i<edges.size();i++){
                if(edge.equals(edges.get(i))){
                    equals+=1;
                }
                if(Objects.equals(edge.get(0), edges.get(i).get(1)) && Objects.equals(edge.get(1), edges.get(i).get(0))){
                    equalsRev+=1;
                }
            }
        }

        if(equals + equalsRev > size){
            System.out.println("BLAD:"+equals +" "+equalsRev);

        }
        if(equals + equalsRev < size){
            return -1;
        } else if (equals<size && equalsRev < size) {
            return 0;
        }else if (equals==size){
            return 1;
        }else{
            return 2;
        }
    }


    public static void main(String[] args) {
        String[] filenames = {"kroA200.tsp","kroB200.tsp"};
        ArrayList<ArrayList<Long>> nodes = dataLoader(filenames[0]);
        ArrayList<ArrayList<Long>> distMat = calcDistMatrix(nodes);

        ArrayList<ArrayList<Long>> cdistMat = new ArrayList<>(distMat);
        RandAlg ra = new RandAlg(cdistMat);
        ra.process(2);
        ArrayList<ArrayList<Integer>> edgesRA = ra.getEdges();


        ArrayList<MoveWithScore> LM = new ArrayList<>();
        MoveWithScore m;
        ArrayList<Integer> idxToRemove;
        findSwapSteepest(distMat,edgesRA,LM);
        System.out.println(LM.get(0));
        System.out.println(LM.get(1));
        System.out.println(LM.get(2));

        visualizeResults(nodes,edgesRA);
        System.out.println(edgesRA);
        Long initCost = countCost(distMat,edgesRA);
        do{
            System.out.println(LM.get(0));
            m = null;
            idxToRemove = new ArrayList<>();

            for(int i=0;i<LM.size();i++){
                int checkResult = checkForApply(edgesRA,LM.get(i));
                switch(checkResult){
                    case -1: // Usuwane krawędzie nie występują w rozwiązaniu
                        idxToRemove.add(i);
                        break;
                    case 0: // Krawędzie w różnych kierunkach
                        break;
                    case 1: // Krawędzie w jednym kierunku
                        m = LM.get(i);
                        for(int k = 0;k<m.edgeList.size();k+=2){
                            if(!validateCycles2(edgesRA,m.edgeList.get(k),m.edgeList.get(k+1))){
                                m=null;
                                break;
                            }
                        }
                        if(m!=null) {
                            idxToRemove.add(i);
                        }
                        break;
                    case 2: // Krawędzie w odwrotnym kierunku
                        m = LM.get(i);
                        for(int j=0;j<m.edgeList.size();j++){
                            ArrayList<Integer> newEdge = new ArrayList<>();
                            newEdge.add(m.edgeList.get(j).get(1));
                            newEdge.add(m.edgeList.get(j).get(0));
                            m.edgeList.set(j,newEdge);
                        }
                        for(int k = 0;k<m.edgeList.size();k+=2){
                            if(!validateCycles2(edgesRA,m.edgeList.get(k),m.edgeList.get(k+1))){
                                m=null;
                                break;
                            }
                        }
                        if(m!=null) {
                            idxToRemove.add(i);
                        }
                        break;
                }
                if(m!=null){
                    break;
                }
            }

            if(m!=null){
                //Wykonanie ruchu
                if(m.edgeList.size()==2){
                    System.out.println("bestDelta: " + m.score);
                    System.out.println("Podmieniono: "+m.edgeList.get(0) + " " + m.edgeList.get(1));
                    swapEdges(edgesRA,m.edgeList.get(0),m.edgeList.get(1));
                }
                if(m.edgeList.size()==4){
                    System.out.println("bestDelta: " + m.score);
                    System.out.println("Podmieniono: "+m.edgeList.get(0)  + " " + m.edgeList.get(1));
                    System.out.println("Podmieniono: "+m.edgeList.get(2)  + " " + m.edgeList.get(3));
                    swapNodes(edgesRA,m.edgeList.get(1).get(0),m.edgeList.get(3).get(0));
                }
            }
            for(int j=idxToRemove.size()-1;j>-1;j--){
                LM.remove(j);
            }
//            System.out.println("Koszt po: " + countCost(distMat,edgesRA));
        }while(m!=null);

//        System.out.println(LM.get(0));
//        System.out.println(LM.get(2));
//        System.out.println(LM.get(4));
//        System.out.println(LM.get(6));
//        System.out.println(LM.size());
        System.out.println("Koszt przed: "+ initCost );
        visualizeResults(nodes,edgesRA);
        System.out.println("Koszt po: " + countCost(distMat,edgesRA));

    }
}

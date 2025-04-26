package com.example.imolab3;

import com.example.imolab1.GreedyCycleAlg;
import com.example.imolab2.RandAlg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
            //System.out.println("niezgodnosc");
            //System.out.println(cyclesCounter);
            //System.out.println(niezgodne);
            //System.out.println(edges2);
//                System.out.println(edges.get(i));
//                System.out.println(edges.get(i+1));

        }
        return cyclesCounter == 2
                && !Objects.equals(edges.get(edges.size() / 2 - 1).get(1), edges.get(edges.size() / 2).get(0))
                && edges.get(0).get(0) == edges.get(edges.size()/2-1).get(1)
                && edges.get(edges.size()/2).get(0) == edges.get(edges.size()-1).get(1);    }

    public static Boolean validateCycles3(ArrayList<ArrayList<Integer>> edges, int node1, int node2){
        ArrayList<ArrayList<Integer>> edges2= new ArrayList<>();
        for(ArrayList<Integer> edge: edges){
            edges2.add((ArrayList<Integer>) edge.clone());
        }
        swapNodes(edges2,node1,node2);
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
            //System.out.println("niezgodnosc");
            //System.out.println(cyclesCounter);
            //System.out.println(niezgodne);
            //System.out.println(edges2);
//                System.out.println(edges.get(i));
//                System.out.println(edges.get(i+1));

        }
        return cyclesCounter == 2
                && !Objects.equals(edges.get(edges.size() / 2 - 1).get(1), edges.get(edges.size() / 2).get(0))
                && edges.get(0).get(0) == edges.get(edges.size()/2-1).get(1)
                && edges.get(edges.size()/2).get(0) == edges.get(edges.size()-1).get(1);    }




    public static void findSwapSteepest(ArrayList<ArrayList<Long>> distMat, ArrayList<ArrayList<Integer>> edges, ArrayList<MoveWithScore> LM){
        int firstCycleEndIdx = edges.size()/2-1;
        // swap edges

        long bestDelta = Long.MAX_VALUE;
        // Przejśćie po pierwszym cyklu
        ArrayList<Integer> swapEdge1 = null,swapEdge2 = null;
        ArrayList<Integer> edge1, edge2;
        for(int i = 0; i<=firstCycleEndIdx; i++){
            for(int j = i; j<=firstCycleEndIdx;j++){
                edge1 = edges.get(i);
                edge2 = edges.get(j);
                if(Objects.equals(edge1.get(0), edge2.get(0))){
                    continue;
                }

                long delta = calcDelta(distMat,edge1,edge2);
                if(delta < 0){
                        if(delta<bestDelta){bestDelta=delta;};
                        ArrayList<ArrayList<Integer>> edgesList = new ArrayList<>();
                        edgesList.add(edge1);
                        edgesList.add(edge2);
                        MoveWithScore mwc = new MoveWithScore(edgesList,delta);
                        extentLM(LM,mwc);

                }
            }
        }

        //Przejście po drugim cyklu
        for(int i = firstCycleEndIdx+1; i<edges.size(); i++){
            for(int j = i+1; j<edges.size();j++){
                edge1 = edges.get(i);
                edge2 = edges.get(j);
                if(Objects.equals(edge1.get(0), edge2.get(0))){
                    continue;
                }

                long delta = calcDelta(distMat,edge1,edge2);
                if(delta < 0){
                        if(delta<bestDelta){bestDelta=delta;};
                        ArrayList<ArrayList<Integer>> edgesList = new ArrayList<>();
                        edgesList.add(edge1);
                        edgesList.add(edge2);
                        MoveWithScore mwc = new MoveWithScore(edgesList,delta);
                        extentLM(LM,mwc);
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
                        MoveWithScore mwc2 = new MoveWithScore(edgesList,delta);
                        extentLM(LM,mwc2);
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

    public static boolean findSwapSteepest2(ArrayList<ArrayList<Long>> distMat, ArrayList<ArrayList<Integer>> edges){
        int firstCycleEndIdx = edges.size()/2-1;
        // swap edges
        long bestDelta = 0;

        // Przejśćie po pierwszym cyklu
        ArrayList<Integer> swapEdge1 = null,swapEdge2 = null;
        ArrayList<Integer> edge1, edge2;
        for(int i = 0; i<=firstCycleEndIdx; i++){
            for(int j = 0; j<=firstCycleEndIdx;j++){
                edge1 = edges.get(i);
                edge2 = edges.get(j);
                if(Objects.equals(edge1.get(0), edge2.get(0))){
                    continue;
                }

                long delta = calcDelta(distMat,edge1,edge2);
                if(delta < bestDelta){
                    bestDelta = delta;
                    swapEdge1 = edge1;
                    swapEdge2 = edge2;
                }
            }
        }

        //Przejście po drugim cyklu
        for(int i = firstCycleEndIdx+1; i<edges.size(); i++){
            for(int j = firstCycleEndIdx+1; j<edges.size();j++){
                edge1 = edges.get(i);
                edge2 = edges.get(j);
                if(Objects.equals(edge1.get(0), edge2.get(0))){
                    continue;
                }

                long delta = calcDelta(distMat,edge1,edge2);
                if(delta < bestDelta){
                    bestDelta = delta;
                    swapEdge1 = edge1;
                    swapEdge2 = edge2;
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

                if(delta < bestDelta && !Objects.equals(edges.get(i).get(0), edges.get(j).get(0))){
                    ArrayList<ArrayList<Integer>> edges2 = new ArrayList<>();
                    for(ArrayList<Integer> edge: edges){
                        edges2.add((ArrayList<Integer>) edge.clone());
                    }
                    swapNodes(edges2,node1,node2);
                    if(validateCycles(edges2)){
                        bestDelta = delta;
                        swapNode1 = node1;
                        swapNode2 = node2;
                    }
                }
            }
        }

        if(bestDelta<0){
            //System.out.println("bestDelta: " + bestDelta);
            if(swapNode1!=-1){
                //System.out.println("Podmieniono: "+swapNode1 + " " + swapNode2);
                swapNodes(edges,swapNode2,swapNode1);
            }else {
                //System.out.println("Podmieniono: "+swapEdge1 + " " + swapEdge2);
                swapEdges(edges, swapEdge1, swapEdge2);
            }
        }else{
            return false;
        }
        return true;
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

        // Sprawdzamy czy istnieje
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
            //System.out.println(edgeA0);
            //System.out.println(edgeA1);
            //System.out.println(edgeB0);
            //System.out.println(edgeB1);
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

        if(!(edgeB1.get(0)==edgeA0.get(0))) {
            res.add(edgeA0);
            res.add(edgeB1);
        }
        if(!(edgeA1.get(0)==edgeB0.get(0))) {
            res.add(edgeB0);
            res.add(edgeA1);
        }
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
            System.out.println("BLAD: size "+ size+",eq: " +equals +"eqRev: "+equalsRev);
        }
        if(equals + equalsRev < size){
            return -1;
        } else if (equals<size && equalsRev < size) {
            return 0;
        }else if (equals==size){
            return 1;
        }else if (equalsRev==size){
            return 2;
        }else{
            return -1;
        }
    }

    public static void addNewMovesToLM(ArrayList<MoveWithScore> LM, ArrayList<ArrayList<Integer>> edges, ArrayList<ArrayList<Long>> distMat, MoveWithScore mwc){
        int cycle2idx = edges.size()/2;
        boolean isEdgeSwap = mwc.edgeList.size()==2;

        ArrayList<Integer> newEdge1 = new ArrayList<>();
        ArrayList<Integer> newEdge2 = new ArrayList<>();

        //swap edges
        if(isEdgeSwap){
            // Znadjuje te do wymiany w edges, ale możliwe że trzeba szukać tych o odwróconym kierunku
            int newEdge1idx = -1;
            int newEdge2idx = -1;
            newEdge1.add(mwc.edgeList.get(0).get(0));
            newEdge1.add(mwc.edgeList.get(1).get(0));
            newEdge2.add(mwc.edgeList.get(0).get(1));
            newEdge2.add(mwc.edgeList.get(1).get(1));

            ArrayList<Integer> newEdge3 = new ArrayList<>();
            ArrayList<Integer> newEdge4 = new ArrayList<>();
            newEdge3.add(newEdge1.get(1));
            newEdge3.add(newEdge1.get(0));
            newEdge4.add(newEdge2.get(1));
            newEdge4.add(newEdge2.get(0));
            int newEdge3idx = -1;
            int newEdge4idx = -1;


            for(int i =0;i<edges.size();i++){
                if(edges.get(i).equals(newEdge1)){
                    newEdge1idx = i;
                    //System.out.println("newEdge1idx"+ i);
                }
                if(edges.get(i).equals(newEdge2)){
                    newEdge2idx = i;
                }
                if(edges.get(i).equals(newEdge3)){
                    newEdge3idx = i;
                }
                if(edges.get(i).equals(newEdge4)){
                    newEdge4idx = i;
                }
            }
            if(newEdge1idx==-1){
                newEdge1idx = newEdge3idx;
                newEdge1 = newEdge3;
            }
            if(newEdge2idx==-1){
                newEdge2idx = newEdge4idx;
                newEdge2 = newEdge4;
                //throw new VerifyError("Indeks = -1: "+ newEdge1idx +" " + newEdge2idx + " "+ newEdge3idx +" " + newEdge4idx);
            }

            if(newEdge1idx<cycle2idx){
                for(int i = 0;i<cycle2idx;i++){
                    long delta = calcDelta(distMat,newEdge1,edges.get(i));
                    if(delta < 0){
                            ArrayList<ArrayList<Integer>> edgesList = new ArrayList<>();
                            edgesList.add(newEdge1);
                            edgesList.add(edges.get(i));
                            MoveWithScore mwc2 = new MoveWithScore(edgesList,delta);
                            extentLM(LM,mwc2);
                    }
                }
                for(int i = 0;i<cycle2idx;i++){
                    long delta = calcDelta(distMat,newEdge2,edges.get(i));
                    if(delta < 0){
                            ArrayList<ArrayList<Integer>> edgesList = new ArrayList<>();
                            edgesList.add(newEdge2);
                            edgesList.add(edges.get(i));
                            MoveWithScore mwc2 = new MoveWithScore(edgesList,delta);
                            extentLM(LM,mwc2);
                    }
                }
            }else{
                for(int i = cycle2idx;i<edges.size();i++){
                    long delta = calcDelta(distMat,newEdge1,edges.get(i));
                    if(delta < 0){
                            ArrayList<ArrayList<Integer>> edgesList = new ArrayList<>();
                            edgesList.add(newEdge1);
                            edgesList.add(edges.get(i));
                            MoveWithScore mwc2 = new MoveWithScore(edgesList,delta);
                            extentLM(LM,mwc2);
                    }
                }
                for(int i = cycle2idx;i<edges.size();i++){
                    long delta = calcDelta(distMat,newEdge2,edges.get(i));
                    if(delta < 0){
                            ArrayList<ArrayList<Integer>> edgesList = new ArrayList<>();
                            edgesList.add(newEdge2);
                            edgesList.add(edges.get(i));
                            MoveWithScore mwc2 = new MoveWithScore(edgesList,delta);
                            extentLM(LM,mwc2);
                    }
                }
            }
        }else{
            // swap nodes
            int node1 = -1, node2 = -1, swapNode1 = -1, swapNode2 = -1;
            // swap nodes between cycles
            node1 = mwc.edgeList.get(0).get(0);
            node2 = mwc.edgeList.get(0).get(1);

            int node1idx = -1;
            int node2idx = -1;
            for(int i =0;i<edges.size();i++){
                if(edges.get(i).get(0)==node1){
                    node1idx = i;
                }
                if(edges.get(i).get(0)==node2){
                    node2idx = i;
                }
            }
            if(node1idx<cycle2idx){
                for(int j = cycle2idx + 1; j < edges.size(); j++){
                    int node12 = edges.get(j).get(0);
                    long delta = calcDeltaNode(distMat,edges,node1,node12);
                    if(delta < 0){
                        ArrayList<ArrayList<Integer>> edges2 = new ArrayList<>();
                        for(ArrayList<Integer> edge: edges){
                            edges2.add((ArrayList<Integer>) edge.clone());
                        }
                        swapNodes(edges2,node1,node12);
                        if(validateCycles(edges2)){
                            MoveWithScore mwc2 = new MoveWithScore(nodeSwapToEdges(edges,node1,node12),delta);
                            extentLM(LM,mwc2);
                        }
                    }
                }
                for(int j = 1; j < cycle2idx; j++){
                    int node22 = edges.get(j).get(0);
                    long delta = calcDeltaNode(distMat,edges,node2,node22);
                    if(delta < 0){
                        ArrayList<ArrayList<Integer>> edges2 = new ArrayList<>();
                        for(ArrayList<Integer> edge: edges){
                            edges2.add((ArrayList<Integer>) edge.clone());
                        }
                        swapNodes(edges2,node2,node22);
                        if(validateCycles(edges2)){
                            MoveWithScore mwc2 = new MoveWithScore(nodeSwapToEdges(edges,node2,node22),delta);
                            extentLM(LM,mwc2);
                        }
                    }
                }
            }else{
                for(int j = 1; j < cycle2idx; j++){
                    int node12 = edges.get(j).get(0);
                    long delta = calcDeltaNode(distMat,edges,node1,node12);
                    if(delta < 0){
                        ArrayList<ArrayList<Integer>> edges2 = new ArrayList<>();
                        for(ArrayList<Integer> edge: edges){
                            edges2.add((ArrayList<Integer>) edge.clone());
                        }
                        swapNodes(edges2,node1,node12);
                        if(validateCycles(edges2)){
                            MoveWithScore mwc2 = new MoveWithScore(nodeSwapToEdges(edges,node1,node12),delta);
                            extentLM(LM,mwc2);
                        }
                    }
                }
                for(int j = cycle2idx+1; j < edges.size(); j++){
                    int node22 = edges.get(j).get(0);
                    long delta = calcDeltaNode(distMat,edges,node2,node22);
                    if(delta < 0){
                        ArrayList<ArrayList<Integer>> edges2 = new ArrayList<>();
                        for(ArrayList<Integer> edge: edges){
                            edges2.add((ArrayList<Integer>) edge.clone());
                        }
                        swapNodes(edges2,node2,node22);
                        if(validateCycles(edges2)){
                            MoveWithScore mwc2 = new MoveWithScore(nodeSwapToEdges(edges,node2,node22),delta);
                            extentLM(LM,mwc2);
                        }
                    }
                }
            }
        }
    }

    public static void shuffleEdges(ArrayList<ArrayList<Integer>> edges){
        int firstCycleEnd = edges.size()/2-1;

        // Zakładamy że edges.size() >= 52
        ArrayList<Integer> elem0 = edges.get(0);
        ArrayList<Integer> elem51 = edges.get(firstCycleEnd+1);

// Usuń elementy z indeksów 0 i 51 (51 po usunięciu 0 to stary 50)
        edges.remove(0);           // Usuwa indeks 0
        edges.remove(firstCycleEnd);          // Po wcześniejszym usunięciu 0, stary 51 ma teraz indeks 50

// Dodaj element z 0 na pozycję 50
        edges.add(firstCycleEnd, elem0);

// Dodaj element z 51 (czyli stary 50) na koniec
        edges.add(elem51);
        //edges.add(firstCycleEnd,edges.get(0));
        //edges.remove(0);

//        for(int i=0;i<10;i++){
//            edges.add(edges.size()-1,edges.get(firstCycleEnd+1));
//            edges.remove(firstCycleEnd+1);
//        }

    }

    public static void proceedLMAlgorithm(ArrayList<ArrayList<Integer>> edges, ArrayList<ArrayList<Long>> distMat){
        ArrayList<MoveWithScore> LM = new ArrayList<>();
        MoveWithScore m;
        ArrayList<Integer> idxToRemove;
        findSwapSteepest(distMat,edges,LM);
        //System.out.println(LM.get(0));
        //System.out.println(LM.get(1));
        //System.out.println(LM.get(2));

        //System.out.println(edges);
        int cc =0;
        do{
            cc++;
            if(cc==1000){
                //ArrayList<ArrayList<Long>> nodes = dataLoader("test.tsp");
                System.out.println(edges);
                //visualizeResults(nodes,edges);
                System.in.mark(3);
            }
            if(cc==1001){
                //ArrayList<ArrayList<Long>> nodes = dataLoader("test.tsp");
                System.out.println(edges);
                //visualizeResults(nodes,edges);
                System.in.mark(3);
            }

            m = null;
            idxToRemove = new ArrayList<>();
            for(int i=0;i<LM.size();i++){
                int checkResult = checkForApply(edges,LM.get(i));
                switch(checkResult){
                    case -1: // Usuwane krawędzie nie występują w rozwiązaniu
                        idxToRemove.add(i);
                        break;
                    case 0: // Krawędzie w różnych kierunkach
                        break;
                    case 1: // Krawędzie w jednym kierunku
                        m = LM.get(i);
                        if(m.edgeList.size()==2&&!checkIfEdgesInSameCycles(edges,m.edgeList.get(0),m.edgeList.get(1))){
                            m=null;
                            break;
                        }else{
                            if(m.edgeList.size()==4){
                                if(!validateCycles3(edges,m.edgeList.get(1).get(0),m.edgeList.get(3).get(0))){
                                    m=null;
                                    break;
                            }
                            }else{
                                for(int k = 0;k<m.edgeList.size();k+=2){

                                    if(!validateCycles2(edges,m.edgeList.get(k),m.edgeList.get(k+1))){
                                        m=null;
                                        break;
                                    }
                                }
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
                        if(m.edgeList.size()==2&&!checkIfEdgesInSameCycles(edges,m.edgeList.get(0),m.edgeList.get(1))){
                            m=null;
                            break;
                        }else{
                            if(m.edgeList.size()==4){
                                if(!validateCycles3(edges,m.edgeList.get(1).get(0),m.edgeList.get(3).get(0))){
                                    m=null;
                                    break;
                                }
                            }else{
                                for(int k = 0;k<m.edgeList.size();k+=2){

                                    if(!validateCycles2(edges,m.edgeList.get(k),m.edgeList.get(k+1))){
                                        m=null;
                                        break;
                                    }
                                }
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
//                    System.out.println("bestDelta: " + m.score);
//                    System.out.println("Podmieniono krawędzie: "+m.edgeList.get(0) + " " + m.edgeList.get(1));
                    swapEdges(edges,m.edgeList.get(0),m.edgeList.get(1));
                }
                if(m.edgeList.size()==4){
//                    System.out.println("bestDelta: " + m.score);
//                    System.out.println("Podmieniono wierzchołki: "+m.edgeList.get(1).get(0)  + " " + m.edgeList.get(3).get(0));
//                    System.out.println("obl. delta:" +calcDeltaNode(distMat,edges,m.edgeList.get(1).get(0),m.edgeList.get(3).get(0)));
                    swapNodes(edges,m.edgeList.get(1).get(0),m.edgeList.get(3).get(0));
                }
            }
            for(int j=idxToRemove.size()-1;j>-1;j--){
                LM.remove(j);
            }
            if(m!=null){
                addNewMovesToLM(LM,edges,distMat,m);
                //shuffleEdges(edges);
            }
//            System.out.println("Koszt po: " + countCost(distMat,edgesRA));
        }while(m!=null);
    }

    public int getNodeIndex(ArrayList<Long> node){
        for(int i = 0; i<node.size();i++){
            if(node.get(i)==0){
                return i;
            }
        }
        return 0;
    }



    public static ArrayList<Integer> findOutEdgeFromNode(ArrayList<ArrayList<Integer>> edges, int node){
        for (ArrayList<Integer> edge : edges) {
            if (edge.get(0) == node){
                return edge;
            }
        }
        return new ArrayList<Integer>();
    }

    public static boolean checkIfEdgesInSameCycles(ArrayList<ArrayList<Integer>> edges, ArrayList<Integer> edge1, ArrayList<Integer> edge2){
        int firstCycleEndIdx = edges.size()/2-1;

        int idx1 = -1;
        int idx2 = -1;
        for(int i=0;i<edges.size();i++){
            if(edges.get(i).equals(edge1)){
                idx1 = i;
            }
            if(edges.get(i).equals(edge2)){
                idx2 = i;
            }
        }
        if(idx1<=firstCycleEndIdx && idx2 <=firstCycleEndIdx){
            return true;
        } else if (idx1>firstCycleEndIdx && idx2>firstCycleEndIdx) {
            return true;
        }else{
            return false;
        }
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
        ArrayList<ArrayList<Long>> nodes = dataLoader(filenames[1]);
        ArrayList<ArrayList<Long>> distMat = calcDistMatrix(nodes);
        ArrayList<ArrayList<Long>> cDistMat;
        ArrayList<ArrayList<Integer>> edgesGCA, edgesRA, copyEdges;

        ArrayList<ArrayList<Integer>> bestEdgesLM = null,bestEdgesCAN = null,bestEdgesST = null,bestEdgesGC = null;

        Long minCostLM = Long.MAX_VALUE,minCostCAN = Long.MAX_VALUE,minCostST = Long.MAX_VALUE,minCostGC = Long.MAX_VALUE;
        Long maxCostLM = 0L,maxCostCAN = 0L,maxCostST = 0L,maxCostGC = 0L;
        Long avgCostLM = 0L,avgCostCAN = 0L,avgCostST = 0L,avgCostGC = 0L;

        Long minTimeLM = Long.MAX_VALUE,minTimeCAN = Long.MAX_VALUE,minTimeST = Long.MAX_VALUE,minTimeGC = Long.MAX_VALUE;
        Long avgTimeLM = 0L,avgTimeCAN = 0L,avgTimeST = 0L,avgTimeGC = 0L;
        Long maxTimeLM = Long.MIN_VALUE,maxTimeCAN = Long.MIN_VALUE,maxTimeST = Long.MIN_VALUE,maxTimeGC = Long.MIN_VALUE;

        Long initCost = 0L, resCost = 0L, startTime = 0L, endTime = 0L, timeTime = 0L;

        List<Long> costResLM = new ArrayList<>(),costResCAN = new ArrayList<>(),costResST = new ArrayList<>(),costResGC = new ArrayList<>();
        ArrayList<ArrayList<Integer>> initEdges = null;
        int iterations = 10;
        double done = 0.0;
        for(int i =0;i<iterations;i++){
            cDistMat = new ArrayList<>(distMat);
            RandAlg ra = new RandAlg(cDistMat);
            ra.process(2);
            edgesRA = ra.getEdges();


            boolean validrun = false;
            copyEdges = copyEdges(edgesRA);
            while(!validrun){
                try{
                    copyEdges = copyEdges(edgesRA);
                    startTime = System.nanoTime();
                    proceedLMAlgorithm(copyEdges,distMat);
                    endTime = System.nanoTime();
                    timeTime = endTime - startTime;
                    if(!validateCycles(edgesRA)){
                        throw new Exception("Cos nie działa");
                    }
                    validrun=true;
                }catch (Exception e){
                    cDistMat = new ArrayList<>(distMat);
                    ra = new RandAlg(cDistMat);
                    ra.process(2);
                    edgesRA = ra.getEdges();
                    System.out.println(e);
                };
            }
            avgTimeLM += timeTime;
            if(timeTime > maxTimeLM) maxTimeLM = timeTime;
            if(timeTime < minTimeLM) minTimeLM = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostLM += resCost;
            if(resCost>maxCostLM) maxCostLM = resCost;
            if(bestEdgesLM == null || resCost< minCostLM){
                bestEdgesLM = copyEdges(copyEdges);
                minCostLM = resCost;
            }
            costResLM.add(resCost);
            done+=1.0/(4.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");

            validrun = false;
            while(!validrun){
                try{
                    copyEdges = copyEdges(edgesRA);
                    startTime = System.nanoTime();
                    ArrayList<ArrayList<Integer>> candidates = CandidatesLS.findCandidates(distMat,11);
                    int cc = 0;
                    while(CandidatesLS.findSteepestCandidates(distMat,copyEdges,candidates)){
                        cc++;
                        if(cc>2000){
                            throw new Exception("nieskończona pętla");
                        }
                    };
                    endTime = System.nanoTime();
                    timeTime = endTime - startTime;
                    validrun = true;
                }catch (Exception e){
                    cDistMat = new ArrayList<>(distMat);
                    ra = new RandAlg(cDistMat);
                    ra.process(2);
                    edgesRA = ra.getEdges();
                    System.out.println(e);
                }
            }
            avgTimeCAN += timeTime;
            if(timeTime > maxTimeCAN) maxTimeCAN = timeTime;
            if(timeTime < minTimeCAN) minTimeCAN = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostCAN += resCost;
            if(resCost>maxCostCAN) maxCostCAN = resCost;
            if(bestEdgesCAN == null || resCost< minCostCAN){
                bestEdgesCAN = copyEdges(copyEdges);
                minCostCAN = resCost;
            }
            costResCAN.add(resCost);
            done+=1.0/(4.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");



            copyEdges = copyEdges(edgesRA);
            startTime = System.nanoTime();
            while(findSwapSteepest2(distMat,copyEdges)){};
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            avgTimeST += timeTime;
            if(timeTime > maxTimeST) maxTimeST = timeTime;
            if(timeTime < minTimeST) minTimeST = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostST += resCost;
            if(resCost>maxCostST) maxCostST = resCost;
            if(bestEdgesST == null || resCost< minCostST){
                bestEdgesST = copyEdges(copyEdges);
                minCostST = resCost;
            }
            costResST.add(resCost);
            done+=1.0/(4.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");


            cDistMat = new ArrayList<>(distMat);
            GreedyCycleAlg gca = new GreedyCycleAlg(cDistMat);

            startTime = System.nanoTime();
            gca.process(2);
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            avgTimeGC += timeTime;
            if(timeTime > maxTimeGC) maxTimeGC = timeTime;
            if(timeTime < minTimeGC) minTimeGC = timeTime;

            edgesGCA = gca.getEdges();
            copyEdges = copyEdges(edgesGCA);

            resCost = countCost(distMat,copyEdges);
            avgCostGC += resCost;
            if(resCost>maxCostGC) maxCostGC= resCost;
            if(bestEdgesGC == null || resCost< minCostGC){
                bestEdgesGC = copyEdges(copyEdges);
                minCostGC = resCost;
            }
            costResGC.add(resCost);
            done+=1.0/(4.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");
        }

        System.out.println("LM: " + avgCostLM/iterations + " (" +  minCostLM + " - " + maxCostLM + ")");
        System.out.println("CAN: " + avgCostCAN/iterations + " (" +  minCostCAN + " - " + maxCostCAN + ")");
        System.out.println("ST: "  + avgCostST/iterations + " (" +  minCostST + " - " + maxCostST + ")");
        System.out.println("GC: " + + avgCostGC/iterations + " (" +  minCostGC + " - " + maxCostGC + ")");

        double giga = 1000000000.0;
        System.out.println("Time LM: " + (double)avgTimeLM/iterations/giga  + " (" + (double)minTimeLM/giga + " - " + (double)maxTimeLM/giga +")");
        System.out.println("Time CAN: " + (double)avgTimeCAN/iterations/giga  + " (" + (double)minTimeCAN/giga + " - " + (double)maxTimeCAN/giga +")");
        System.out.println("Time ST: " + (double)avgTimeST/iterations/giga  + " (" + (double)minTimeST/giga + " - " + (double)maxTimeST/giga +")");
        System.out.println("Time GC: " + + (double)avgTimeGC/iterations/giga  + " (" + (double)minTimeGC/giga + " - " + (double)maxTimeGC/giga +")");


        System.out.println("SD LM:" + standardDeviation(costResLM));
        System.out.println("SD CAN:" + standardDeviation(costResCAN));
        System.out.println("SD ST:" + standardDeviation(costResST));
        System.out.println("SD GC:" + standardDeviation(costResGC));

        System.out.println("Best LM edges:" + bestEdgesLM);
        System.out.println("Best CAN edges:" + bestEdgesCAN);
        System.out.println("Best ST edges:" + bestEdgesST);
        System.out.println("Best GC edges:" + bestEdgesGC);



        visualizeResults(nodes,bestEdgesLM);
        visualizeResults(nodes,bestEdgesCAN);
        visualizeResults(nodes,bestEdgesST);
        visualizeResults(nodes,bestEdgesGC);

    }
}

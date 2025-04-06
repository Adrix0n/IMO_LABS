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
            //System.out.println("niezgodnosc");
            //System.out.println(cyclesCounter);
            //System.out.println(niezgodne);
            //System.out.println(edges2);
//                System.out.println(edges.get(i));
//                System.out.println(edges.get(i+1));

        }
        return cyclesCounter == 2;
    }

    public static void findSwapSteepest(ArrayList<ArrayList<Long>> distMat, ArrayList<ArrayList<Integer>> edges, ArrayList<MoveWithScore> LM){
        int firstCycleEndIdx = findCycleEnd(edges);
        //int firstCycleEndIdx = 49;
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
        if(equals + equalsRev > size){
            return 0;
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

    public static void addNewMovesToLM(ArrayList<MoveWithScore> LM, ArrayList<ArrayList<Integer>> edges, ArrayList<ArrayList<Long>> distMat, MoveWithScore mwc){
        int cycle2idx = findCycleEnd(edges) + 1;
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
                newEdge1 = newEdge3;}
            if(newEdge2idx==-1){
                newEdge2idx = newEdge4idx;
                newEdge2 = newEdge4;
                //throw new VerifyError("Indeks = -1: "+ newEdge1idx +" " + newEdge2idx + " "+ newEdge3idx +" " + newEdge4idx);
            }
            if(newEdge1idx<cycle2idx){
                for(int i = 1;i<cycle2idx;i++){
                    long delta = calcDelta(distMat,newEdge1,edges.get(i));
                    if(delta < 0){
                        if(validateCycles2(edges,newEdge1,edges.get(i))){
                            ArrayList<ArrayList<Integer>> edgesList = new ArrayList<>();
                            edgesList.add(newEdge1);
                            edgesList.add(edges.get(i));
                            MoveWithScore mwc2 = new MoveWithScore(edgesList,delta);
                            extentLM(LM,mwc2);
                        }
                    }
                }
                for(int i = 1;i<cycle2idx;i++){
                    long delta = calcDelta(distMat,newEdge2,edges.get(i));
                    if(delta < 0){
                        if(validateCycles2(edges,newEdge2,edges.get(i))){
                            ArrayList<ArrayList<Integer>> edgesList = new ArrayList<>();
                            edgesList.add(newEdge2);
                            edgesList.add(edges.get(i));
                            MoveWithScore mwc2 = new MoveWithScore(edgesList,delta);
                            extentLM(LM,mwc2);
                        }
                    }
                }
            }else{
                for(int i = cycle2idx+1;i<edges.size();i++){
                    long delta = calcDelta(distMat,newEdge1,edges.get(i));
                    if(delta < 0){
                        if(validateCycles2(edges,newEdge1,edges.get(i))){
                            ArrayList<ArrayList<Integer>> edgesList = new ArrayList<>();
                            edgesList.add(newEdge1);
                            edgesList.add(edges.get(i));
                            MoveWithScore mwc2 = new MoveWithScore(edgesList,delta);
                            extentLM(LM,mwc2);
                        }
                    }
                }
                for(int i = cycle2idx+1;i<edges.size();i++){
                    long delta = calcDelta(distMat,newEdge2,edges.get(i));
                    if(delta < 0){
                        if(validateCycles2(edges,newEdge2,edges.get(i))){
                            ArrayList<ArrayList<Integer>> edgesList = new ArrayList<>();
                            edgesList.add(newEdge2);
                            edgesList.add(edges.get(i));
                            MoveWithScore mwc2 = new MoveWithScore(edgesList,delta);
                            extentLM(LM,mwc2);
                        }
                    }
                }
            }
        }else{
            // swap nodes
            int node1 = -1, node2 = -1, swapNode1 = -1, swapNode2 = -1;
            // swap nodes between cycles
            node1 = mwc.edgeList.get(1).get(0);
            node2 = mwc.edgeList.get(3).get(0);

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
                            ArrayList<ArrayList<Integer>> edgesList = nodeSwapToEdges(edges,node1,node12);
                            MoveWithScore mwc2 = new MoveWithScore(edgesList,delta);
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
                            ArrayList<ArrayList<Integer>> edgesList = nodeSwapToEdges(edges,node2,node22);
                            MoveWithScore mwc2 = new MoveWithScore(edgesList,delta);
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
                            ArrayList<ArrayList<Integer>> edgesList = nodeSwapToEdges(edges,node1,node12);
                            MoveWithScore mwc2 = new MoveWithScore(edgesList,delta);
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
                            ArrayList<ArrayList<Integer>> edgesList = nodeSwapToEdges(edges,node2,node22);
                            MoveWithScore mwc2 = new MoveWithScore(edgesList,delta);
                            extentLM(LM,mwc2);
                        }
                    }
                }
            }
        }
    }

    public static void shuffleEdges(ArrayList<ArrayList<Integer>> edges){
        int firstCycleEnd = findCycleEnd(edges);

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
            //System.out.println(LM.get(0));
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
                    //System.out.println("bestDelta: " + m.score);
                    //System.out.println("Podmieniono: "+m.edgeList.get(0) + " " + m.edgeList.get(1));
                    swapEdges(edgesRA,m.edgeList.get(0),m.edgeList.get(1));
                }
                if(m.edgeList.size()==4){
                    //System.out.println("bestDelta: " + m.score);
                    //System.out.println("Podmieniono: "+m.edgeList.get(0)  + " " + m.edgeList.get(1));
                    //System.out.println("Podmieniono: "+m.edgeList.get(2)  + " " + m.edgeList.get(3));
                    swapNodes(edgesRA,m.edgeList.get(1).get(0),m.edgeList.get(3).get(0));
                }
            }
            for(int j=idxToRemove.size()-1;j>-1;j--){
                LM.remove(j);
            }
            if(m!=null){
                addNewMovesToLM(LM,edgesRA,distMat,m);
                //shuffleEdges(edgesRA);
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

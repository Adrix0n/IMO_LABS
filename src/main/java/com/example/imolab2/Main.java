package com.example.imolab2;

import com.example.imolab1.GreedyCycleAlg;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

import static com.example.CalcDistMatrix.calcDistMatrix;
import static com.example.DataLoader.dataLoader;
import static com.example.VisualizeResults.visualizeResults;


// Edge nie mogą być takie same w calcDelta
public class Main {

    public static Long calcDelta(ArrayList<ArrayList<Long>> distMat,ArrayList<Integer> edge1, ArrayList<Integer> edge2){
        Long delta = 0L;
        delta -= distMat.get(edge1.get(0)).get(edge1.get(1));
        delta -= distMat.get(edge2.get(0)).get(edge2.get(1));
        delta += distMat.get(edge1.get(0)).get(edge2.get(0));
        delta += distMat.get(edge1.get(1)).get(edge2.get(1));
        // zmienić kolejność krawedzi w edges, bo zmienia się kierunek
        return delta;
    }

    public static Long calcDeltaNode(ArrayList<ArrayList<Long>> distMat,ArrayList<ArrayList<Integer>> edges, Integer node1, Integer node2){
        Long delta = 0L;
        ArrayList<Integer> edge1 = null, edge2 = null, edge3 = null, edge4 = null;
        //System.out.println("node1 i 2: " + node1 + "  " + node2);
        for(int i = 0;i<edges.size();i++){
            if(Objects.equals(edges.get(i).get(0), node1)){
                edge1 = edges.get(i);
                if(i==0){
                    for(int j=0;j<edges.size();j++){
                        if(Objects.equals(edges.get(j).get(1), node1)){
                            edge2 = edges.get(j);
                        }
                    }
                }else{
                    edge2 = edges.get(i-1);
                }
            }
            if(Objects.equals(edges.get(i).get(0), node2)){
                edge3 = edges.get(i);
                if(i==0){
                    for(int j=0;j<edges.size();j++){
                        if(Objects.equals(edges.get(j).get(1), node2)){
                            edge4 = edges.get(j);
                        }
                    }
                }else{
                    edge4 = edges.get(i-1);
                }
            }
        }

        //System.out.println(edge1);
        //System.out.println(edge2);
        //System.out.println(edge3);
        //System.out.println(edge4);


        if(edge1 == null || edge2 == null || edge3 == null || edge4 == null){
            throw new RuntimeException("Krawędź null a nie powinna");
        }

        if(Objects.equals(edge1.get(0), edge4.get(0))||Objects.equals(edge2.get(0), edge3.get(0))){
            if(Objects.equals(edge1.get(0), edge4.get(0))){
                delta -= distMat.get(edge2.get(0)).get(edge2.get(1));
                delta -= distMat.get(edge3.get(0)).get(edge3.get(1));

                delta += distMat.get(edge2.get(0)).get(edge3.get(0));
                delta += distMat.get(edge1.get(0)).get(edge3.get(1));
            }else{
                delta -= distMat.get(edge4.get(0)).get(edge4.get(1));
                delta -= distMat.get(edge1.get(0)).get(edge1.get(1));

                delta += distMat.get(edge4.get(0)).get(edge2.get(1));
                delta += distMat.get(edge3.get(0)).get(edge1.get(1));
            }
            //System.out.println("FIREEEE");
        }else{
            delta -= distMat.get(edge1.get(0)).get(edge1.get(1));
            delta -= distMat.get(edge2.get(0)).get(edge2.get(1));
            delta -= distMat.get(edge3.get(0)).get(edge3.get(1));
            delta -= distMat.get(edge4.get(0)).get(edge4.get(1));

            delta += distMat.get(edge2.get(0)).get(edge3.get(0));
            delta += distMat.get(edge3.get(0)).get(edge1.get(1));
            delta += distMat.get(edge4.get(0)).get(edge1.get(0));
            delta += distMat.get(edge1.get(0)).get(edge3.get(1));
        }

        return delta;
    }


    public static void swapEdges(ArrayList<ArrayList<Integer>> edges, ArrayList<Integer> edge1, ArrayList<Integer> edge2){
        int idx1 = -1;
        int idx2 = -1;
        for(int i = 0; i<edges.size(); i++){
            if(Objects.equals(edges.get(i).get(0), edge1.get(0))
                && Objects.equals(edges.get(i).get(1), edge1.get(1))){
                idx1 = i;
                break;
            }
        }
        for(int i = 0; i<edges.size(); i++){
            if(Objects.equals(edges.get(i).get(0), edge2.get(0))
                    && Objects.equals(edges.get(i).get(1), edge2.get(1))){
                idx2 = i;
                break;
            }
        }
        if(idx1 > idx2){
            int t = idx1;
            idx1 = idx2;
            idx2 = t;
        }

        ArrayList<Integer> tedge = edges.get(idx1);
        Integer tval = tedge.get(1);
        tedge.set(1,edges.get(idx2).get(0));
        edges.set(idx1,tedge);

        tedge = edges.get(idx2);
        tedge.set(0,tval);
        edges.set(idx2,tedge);

        for(int i=idx1+1;i<idx2;i++){
            tedge = edges.get(i);
            tval = tedge.get(0);
            tedge.set(0,tedge.get(1));
            tedge.set(1,tval);
            edges.set(i,tedge);
        }

        for(int i=1;i<=(idx2-idx1)/2;i++){
            tedge = edges.get(idx1+i);
            edges.set(idx1+i,edges.get(idx2-i));
            edges.set(idx2-i,tedge);
        }
    }

    public static void swapNodes(ArrayList<ArrayList<Integer>> edges, Integer node1, Integer node2){
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

        //System.out.println("Krawedzie do zamiany");
        //System.out.println(edgeA0);
        //System.out.println(edgeA1);
        //System.out.println(edgeB0);
        //System.out.println(edgeB1);
//        if(edgeA1==edgeB0||edgeB1==edgeA0){
//            System.out.println("Te same krawedzie!!!");
//            System.out.println(edgeA0);
//            System.out.println(edgeA1);
//            System.out.println(edgeB0);
//            System.out.println(edgeB1);
//        }
        if(!(edgeB1==edgeA0)){
            swapEdges(edges,edgeA0,edgeB1);
        }
        if(!(edgeA1==edgeB0)){
            swapEdges(edges,edgeB0,edgeA1);
        }

    }

    public static Integer findCycleEnd(ArrayList<ArrayList<Integer>> edges){
        for(int i=0; i<edges.size();i++){
            if(edges.get(i).get(1)!=edges.get(i+1).get(0)){
                return i;
            }
        }
        return -1;
    }

    public static Boolean findSwapSteepest(ArrayList<ArrayList<Long>> distMat,ArrayList<ArrayList<Integer>> edges){
        int firstCycleEndIdx = findCycleEnd(edges);
        long bestDelta = 0L;
        // swap edges

        // Przejśćie po pierwszym cyklu
        ArrayList<Integer> swapEdge1 = null,swapEdge2 = null;
        ArrayList<Integer> edge1, edge2;
        for(int i = 0; i<=firstCycleEndIdx; i++){
            for(int j = i+1; j<=firstCycleEndIdx;j++){
                edge1 = edges.get(i);
                edge2 = edges.get(j);
                if(Objects.equals(edge1.get(0), edge2.get(0))){
                    continue;
                }

                //do poprawy
                ArrayList<ArrayList<Integer>> edges2= new ArrayList<>();
                for(ArrayList<Integer> edge: edges){
                    edges2.add((ArrayList<Integer>) edge.clone());
                }
                swapEdges(edges2,edge1,edge2);

                long delta = calcDelta(distMat,edge1,edge2);
                if(delta < bestDelta && validateCycles(edges2)){
                    bestDelta = delta;
                    swapEdge1 = edge1;
                    swapEdge2 = edge2;
                }
            }
        }

        //Przejście po drugim cyklu
        for(int i = firstCycleEndIdx+1; i<edges.size(); i++){
            for(int j = firstCycleEndIdx+2; j<edges.size();j++){
                edge1 = edges.get(i);
                edge2 = edges.get(j);
                if(Objects.equals(edge1.get(0), edge2.get(0))){
                    continue;
                }

                long delta = calcDelta(distMat,edge1,edge2);

                //Do poprawy
                ArrayList<ArrayList<Integer>> edges2= new ArrayList<>();
                for(ArrayList<Integer> edge: edges){
                    edges2.add((ArrayList<Integer>) edge.clone());
                }
                swapEdges(edges2,edge1,edge2);

                if(delta < bestDelta && validateCycles(edges2)){
                    bestDelta = delta;
                    swapEdge1 = edge1;
                    swapEdge2 = edge2;
                }
            }
        }

        // swap nodes

        ArrayList<Integer> edge3 = null, edge4 = null;
        int node1 = -1, node2 = -1, swapNode1 = -1, swapNode2 = -1;
        for(int i = 0; i<=firstCycleEndIdx; i++){
            for(int j = i+1; j<=firstCycleEndIdx; j++){
                node1 = edges.get(i).get(0);
                node2 = edges.get(j).get(0);
                //System.out.println(i + " pierwszy cykl, wewnatrz");


                ArrayList<ArrayList<Integer>> edges2 = new ArrayList<>();
                for(ArrayList<Integer> edge: edges){
                    edges2.add((ArrayList<Integer>) edge.clone());
                }
                swapNodes(edges2,node1,node2);

                Long delta = calcDeltaNode(distMat,edges,node1,node2);
                if(!validateCycles(edges2)){
                    System.out.println(node1);
                    System.out.println(node2);
                }
                if(delta < bestDelta && node1 != node2 && validateCycles(edges2)){
                    bestDelta = delta;
                    swapNode1 = node1;
                    swapNode2 = node2;
                }
            }
        }

        //Przejście po drugim cyklu
        for(int i = firstCycleEndIdx+1; i<edges.size(); i++){
            for(int j = firstCycleEndIdx+2; j<edges.size();j++){
                node1 = edges.get(i).get(0);
                node2 = edges.get(j).get(0);
                //System.out.println(i + " drugi cykl, wewnatrz");

                ArrayList<ArrayList<Integer>> edges2 = new ArrayList<>();
                for(ArrayList<Integer> edge: edges){
                    edges2.add((ArrayList<Integer>) edge.clone());
                }
                swapNodes(edges2,node1,node2);

                Long delta = calcDeltaNode(distMat,edges,node1,node2);
                if(delta < bestDelta && node1 != node2 && validateCycles(edges2)){
                    bestDelta = delta;
                    swapNode1 = node1;
                    swapNode2 = node2;
                }
            }
        }

        // swap nodes between cycles
        for(int i = 0; i <= firstCycleEndIdx; i++){
            for(int j = firstCycleEndIdx+1; j < distMat.size(); j++){
                //System.out.println(i + " zewnątrz");
                Long delta = calcDeltaNode(distMat,edges,edges.get(i).get(0),edges.get(j).get(0));
                if(delta < bestDelta && !Objects.equals(edges.get(i).get(0), edges.get(j).get(0))){
                    bestDelta = delta;
                    swapNode1 = edges.get(i).get(0);
                    swapNode2 = edges.get(j).get(0);
                }
            }
        }

        if(bestDelta<0){
            System.out.println("bestDelta: " + bestDelta);
            if(swapNode1!=-1){
                System.out.println("Podmieniono: "+swapNode1 + " " + swapNode2);
                swapNodes(edges,swapNode2,swapNode1);
            }else {
                System.out.println("Podmieniono: "+swapEdge1 + " " + swapEdge2);



                swapEdges(edges, swapEdge1, swapEdge2);
            }
        }else{
            return false;
        }
        return true;
    }

    public static Boolean findSwapGreedy(ArrayList<ArrayList<Long>> distMat){


        return false;
    }

    public static Boolean validateCycles(ArrayList<ArrayList<Integer>> edges){
        int cyclesCounter = 1;
        int niezgonoscCounter = cyclesCounter;
        ArrayList<ArrayList<Integer>> niezgodne = new ArrayList<>();
        for(int i=0;i<edges.size()-1;i++){
            if(!Objects.equals(edges.get(i).get(1), edges.get(i + 1).get(0))){
                cyclesCounter+=1;
                if(cyclesCounter>niezgonoscCounter){
                    niezgonoscCounter+=1;
                    niezgodne.add(edges.get(i));
                    niezgodne.add(edges.get(i+1));
                }
            }

        }
        if(cyclesCounter>2){
            System.out.println("niezgodnosc");
            System.out.println(cyclesCounter);
            System.out.println(niezgodne);
            System.out.println(edges);
//                System.out.println(edges.get(i));
//                System.out.println(edges.get(i+1));

        }
        return cyclesCounter == 2;
    }

    public static void main(String[] args) {
        String[] filenames = {"kroA200.tsp","kroB200.tsp"};
        ArrayList<ArrayList<Long>> nodes = dataLoader(filenames[0]);
        ArrayList<ArrayList<Long>> distMat = calcDistMatrix(nodes);

        ArrayList<ArrayList<Long>> cDistMat = new ArrayList<>(distMat);
        GreedyCycleAlg gca = new GreedyCycleAlg(cDistMat);
        gca.process(2);
        ArrayList<ArrayList<Integer>> edges = gca.getEdges();
        System.out.println("Pierwsza walidacja");
        System.out.println(edges.size());
        validateCycles(edges);
        //System.out.println(edges);


        visualizeResults(nodes,edges);
        while(findSwapSteepest(distMat,edges)){
            //visualizeResults(nodes,edges);
        }
        System.out.println();
        visualizeResults(nodes,edges);
    }
}

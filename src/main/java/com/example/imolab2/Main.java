package com.example.imolab2;

import com.example.imolab1.GreedyCycleAlg;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

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

    // Problem w greedy rozwiązany na szybko, trzeba w każdej pętli dodać do i i j po jedynce, aby nie zaczynały od zerowego node cyklu
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
                node1 = edges.get(i).get(0);
                node2 = edges.get(j).get(0);
                ArrayList<ArrayList<Integer>> edges2 = new ArrayList<>();
                for(ArrayList<Integer> edge: edges){
                    edges2.add((ArrayList<Integer>) edge.clone());
                }
                swapNodes(edges2,node1,node2);

                Long delta = calcDeltaNode(distMat,edges,edges.get(i).get(0),edges.get(j).get(0));
                if(delta < bestDelta && !Objects.equals(edges.get(i).get(0), edges.get(j).get(0)) && validateCycles(edges2)){
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

    public static Boolean findSwapGreedy(ArrayList<ArrayList<Long>> distMat,ArrayList<ArrayList<Integer>> edges, int p){
        Random random = new Random();
        int rand = random.nextInt(100);

        int firstCycleEndIdx = findCycleEnd(edges);
        long bestDelta = 0L;
        ArrayList<Integer> swapEdge1 = null,swapEdge2 = null;
        ArrayList<Integer> edge1, edge2;
        int node1 = -1, node2 = -1, swapNode1 = -1, swapNode2 = -1;
        if(rand<p){
            for(int i = 1; i<=firstCycleEndIdx; i++){
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
                        break;
                    }
                }
                if(swapEdge1!=null){
                    break;
                }
            }

            //Przejście po drugim cyklu
            for(int i = firstCycleEndIdx+2; i<edges.size(); i++){
                for(int j = firstCycleEndIdx+3; j<edges.size();j++){
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
                        break;
                    }
                }
                if(swapEdge1!=null){
                    break;
                }
            }
        }
        if(swapEdge1==null){
            for(int i = 1; i<=firstCycleEndIdx; i++){
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
                        break;
                    }
                }
                if(swapNode1!=-1){
                    break;
                }
            }

            // swap nodes between cycles
            for(int i = 1; i <= firstCycleEndIdx; i++){
                for(int j = firstCycleEndIdx+2; j < distMat.size(); j++){
                    //System.out.println(i + " zewnątrz");
                    node1 = edges.get(i).get(0);
                    node2 = edges.get(j).get(0);
                    ArrayList<ArrayList<Integer>> edges2 = new ArrayList<>();
                    for(ArrayList<Integer> edge: edges){
                        edges2.add((ArrayList<Integer>) edge.clone());
                    }
                    swapNodes(edges2,node1,node2);
                    Long delta = calcDeltaNode(distMat,edges,edges.get(i).get(0),edges.get(j).get(0));
                    if(delta < bestDelta && !Objects.equals(edges.get(i).get(0), edges.get(j).get(0)) && validateCycles(edges2)){
                        bestDelta = delta;
                        swapNode1 = edges.get(i).get(0);
                        swapNode2 = edges.get(j).get(0);
                        break;
                    }
                }
                if(swapNode1!=-1){
                    break;
                }
            }

            //Przejście po drugim cyklu
            for(int i = firstCycleEndIdx+2; i<edges.size(); i++){
                for(int j = firstCycleEndIdx+3; j<edges.size();j++){
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
                        break;
                    }
                }
                if(swapNode1!=-1){
                    break;
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

    public static Boolean findSwapGreedyAndSteepest(ArrayList<ArrayList<Long>> distMat,ArrayList<ArrayList<Integer>> edges, Boolean isSteepest, Boolean isEdgeSwap){
        Random random = new Random();
        int rand = random.nextInt(100);

        int firstCycleEndIdx = findCycleEnd(edges);
        long bestDelta = 0L;
        ArrayList<Integer> swapEdge1 = null,swapEdge2 = null;
        ArrayList<Integer> edge1, edge2;
        int node1 = -1, node2 = -1, swapNode1 = -1, swapNode2 = -1;
        if(isEdgeSwap){
            for(int i = 0; i<=firstCycleEndIdx; i++){
                for(int j = i+1; j<=firstCycleEndIdx;j++){
                    edge1 = edges.get(i);
                    edge2 = edges.get(j);
                    if(Objects.equals(edge1.get(0), edge2.get(0))){
                        continue;
                    }
                    long delta = calcDelta(distMat,edge1,edge2);
                    if(delta < bestDelta){
                        ArrayList<ArrayList<Integer>> edges2= new ArrayList<>();
                        for(ArrayList<Integer> edge: edges){
                            edges2.add((ArrayList<Integer>) edge.clone());
                        }
                        swapEdges(edges2,edge1,edge2);
                        if(validateCycles(edges2)){
                            bestDelta = delta;
                            swapEdge1 = edge1;
                            swapEdge2 = edge2;
                            if(!isSteepest){
                                break;
                            }
                        }
                    }
                }
                if(swapEdge1!=null&&!isSteepest){
                    break;
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

                    if(delta < bestDelta){
                        ArrayList<ArrayList<Integer>> edges2= new ArrayList<>();
                        for(ArrayList<Integer> edge: edges){
                            edges2.add((ArrayList<Integer>) edge.clone());
                        }
                        swapEdges(edges2,edge1,edge2);
                        if(validateCycles(edges2)){
                            bestDelta = delta;
                            swapEdge1 = edge1;
                            swapEdge2 = edge2;
                            if(!isSteepest){
                                break;
                            }
                        }
                    }
                }
                if(swapEdge1!=null&&!isSteepest){
                    break;
                }
            }
        }else{
            for(int i = 1; i<=firstCycleEndIdx; i++){
                for(int j = i+1; j<=firstCycleEndIdx; j++){
                    node1 = edges.get(i).get(0);
                    node2 = edges.get(j).get(0);

                    //System.out.println(i + " pierwszy cykl, wewnatrz");

                    Long delta = calcDeltaNode(distMat,edges,node1,node2);
                    if(delta < bestDelta && node1 != node2){
                        ArrayList<ArrayList<Integer>> edges2 = new ArrayList<>();
                        for(ArrayList<Integer> edge: edges){
                            edges2.add((ArrayList<Integer>) edge.clone());
                        }
                        swapNodes(edges2,node1,node2);
                        if(validateCycles(edges2)){
                            bestDelta = delta;
                            swapNode1 = node1;
                            swapNode2 = node2;
                            if(!isSteepest){
                                break;
                            }
                        }
                    }
                }
                if(swapNode1!=-1&&!isSteepest){
                    break;
                }
            }

            // swap nodes between cycles
            for(int i = 1; i <= firstCycleEndIdx; i++){
                for(int j = firstCycleEndIdx+2; j < distMat.size(); j++){
//                for(int i = 1; i < distMat.size(); i++){
//                    for(int j = i+2; j < distMat.size(); j++){
                    //System.out.println(i + " zewnątrz");
                    node1 = edges.get(i).get(0);
                    node2 = edges.get(j).get(0);
                    Long delta = calcDeltaNode(distMat,edges,edges.get(i).get(0),edges.get(j).get(0));
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
                            if(!isSteepest){
                                break;
                            }
                        }
                    }
                }
                if(swapNode1!=-1&&!isSteepest){
                    break;
                }
            }

            //Przejście po drugim cyklu
            for(int i = firstCycleEndIdx+2; i<edges.size(); i++){
                for(int j = firstCycleEndIdx+3; j<edges.size();j++){
                    node1 = edges.get(i).get(0);
                    node2 = edges.get(j).get(0);
                    //System.out.println(i + " drugi cykl, wewnatrz");

                    Long delta = calcDeltaNode(distMat,edges,node1,node2);
                    if(delta < bestDelta && node1 != node2 ){
                        ArrayList<ArrayList<Integer>> edges2 = new ArrayList<>();
                        for(ArrayList<Integer> edge: edges){
                            edges2.add((ArrayList<Integer>) edge.clone());
                        }
                        swapNodes(edges2,node1,node2);
                        if(validateCycles(edges2)){
                            bestDelta = delta;
                            swapNode1 = node1;
                            swapNode2 = node2;
                            if(!isSteepest){
                                break;
                            }
                        }
                    }
                }
                if(swapNode1!=-1&&!isSteepest){
                    break;
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

    public static Long countCost(ArrayList<ArrayList<Long>> distMat, ArrayList<ArrayList<Integer>> edges){
        Long sum = 0L;
        for(ArrayList<Integer> edge: edges){
            sum += distMat.get(edge.get(0)).get(edge.get(1));
        }
        return sum;
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

    public static ArrayList<ArrayList<Integer>> copyEdges(ArrayList<ArrayList<Integer>> edges){
        ArrayList<ArrayList<Integer>> copyEdges = new ArrayList<>();
        for (ArrayList<Integer> edge : edges) {
            copyEdges.add((ArrayList<Integer>) edge.clone());
        }
        return copyEdges;
    }

    public static void main(String[] args) {
//        Zadanie polega na implementacji lokalnego przeszukiwania w wersjach stromej (steepest) i
//        zachłannej (greedy), z dwoma różnym rodzajami sąsiedztwa, starując albo z rozwiązań losowych, albo
//        z rozwiązań uzyskanych za pomocą jednej z heurystyk opracowanych w ramach poprzedniego
//        zadania. W sumie 8 kombinacji - wersji lokalnego przeszukiwania.
        // Testy
        String[] filenames = {"kroA200.tsp","kroB200.tsp"};
        ArrayList<ArrayList<Long>> nodes = dataLoader(filenames[0]);
        ArrayList<ArrayList<Long>> distMat = calcDistMatrix(nodes);
        ArrayList<ArrayList<Long>> cDistMat;
        ArrayList<ArrayList<Integer>> edgesGCA, edgesRA, copyEdges;

        ArrayList<ArrayList<Integer>> bestEdgesRATT = null,bestEdgesRATF = null,bestEdgesRAFT = null,bestEdgesRAFF = null;
        ArrayList<ArrayList<Integer>> bestEdgesGCATT = null,bestEdgesGCATF = null,bestEdgesGCAFT = null,bestEdgesGCAFF = null;
        Long bestCostRATT = Long.MAX_VALUE,bestCostRATF = Long.MAX_VALUE,bestCostRAFT = Long.MAX_VALUE,bestCostRAFF = Long.MAX_VALUE;
        Long maxCostRATT = 0L,maxCostRATF = 0L,maxCostRAFT = 0L,maxCostRAFF = 0L;
        Long avgCostRATT = 0L,avgCostRATF = 0L,avgCostRAFT = 0L,avgCostRAFF = 0L;
        Long bestCostGCATT = Long.MAX_VALUE,bestCostGCATF = Long.MAX_VALUE,bestCostGCAFT = Long.MAX_VALUE,bestCostGCAFF = Long.MAX_VALUE;
        Long maxCostGCATT = 0L,maxCostGCATF = 0L,maxCostGCAFT = 0L,maxCostGCAFF = 0L;
        Long avgCostGCATT = 0L,avgCostGCATF = 0L,avgCostGCAFT = 0L,avgCostGCAFF = 0L;

        Long minTimeRATT = Long.MAX_VALUE,minTimeRATF = Long.MAX_VALUE,minTimeRAFT = Long.MAX_VALUE,minTimeRAFF = Long.MAX_VALUE;
        Long avgTimeRATT = 0L,avgTimeRATF = 0L,avgTimeRAFT = 0L,avgTimeRAFF = 0L;
        Long maxTimeRATT = Long.MIN_VALUE,maxTimeRATF = Long.MIN_VALUE,maxTimeRAFT = Long.MIN_VALUE,maxTimeRAFF = Long.MIN_VALUE;

        Long minTimeGCATT = Long.MAX_VALUE,minTimeGCATF = Long.MAX_VALUE,minTimeGCAFT = Long.MAX_VALUE,minTimeGCAFF =Long.MAX_VALUE;
        Long avgTimeGCATT = 0L,avgTimeGCATF = 0L,avgTimeGCAFT = 0L,avgTimeGCAFF = 0L;
        Long maxTimeGCATT = Long.MIN_VALUE,maxTimeGCATF = Long.MIN_VALUE,maxTimeGCAFT = Long.MIN_VALUE,maxTimeGCAFF =Long.MIN_VALUE;

        ArrayList<ArrayList<Integer>> bestEdgesRATTinit = null,bestEdgesRATFinit = null,bestEdgesRAFTinit = null,bestEdgesRAFFinit = null;
        ArrayList<ArrayList<Integer>> bestEdgesGCATTinit = null,bestEdgesGCATFinit = null,bestEdgesGCAFTinit = null,bestEdgesGCAFFinit = null;
        Long bestCostRATTinit = Long.MAX_VALUE,bestCostRATFinit = Long.MAX_VALUE,bestCostRAFTinit = Long.MAX_VALUE,bestCostRAFFinit = Long.MAX_VALUE;
        Long bestCostGCATTinit = Long.MAX_VALUE,bestCostGCATFinit = Long.MAX_VALUE,bestCostGCAFTinit = Long.MAX_VALUE,bestCostGCAFFinit = Long.MAX_VALUE;




        Long initCost = 0L, resCost = 0L, startTime = 0L, endTime = 0L, timeTime = 0L;
        ArrayList<ArrayList<Integer>> initEdges = null;
        int iterations = 50;
        double done = 0.0;
        for(int i =0;i<iterations;i++){
            cDistMat = new ArrayList<>(distMat);
            RandAlg ra = new RandAlg(cDistMat);
            ra.process(2);
            edgesRA = ra.getEdges();

            cDistMat = new ArrayList<>(distMat);
            GreedyCycleAlg gca = new GreedyCycleAlg(cDistMat);
            gca.process(2);
            edgesGCA = gca.getEdges();

            copyEdges = copyEdges(edgesRA);
            initCost = countCost(distMat,copyEdges);
            initEdges = copyEdges(copyEdges);
            startTime = System.nanoTime();
            while(findSwapGreedyAndSteepest(distMat,copyEdges,true,true)){};
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            avgTimeRATT += timeTime;
            if(timeTime > maxTimeRATT) maxTimeRATT = timeTime;
            if(timeTime < minTimeRATT) minTimeRATT = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostRATT += resCost;
            if(resCost>maxCostRATT) maxCostRATT = resCost;
            if(bestEdgesRATT == null || resCost< bestCostRATT){
                bestEdgesRATT = copyEdges(copyEdges);
                bestCostRATT = resCost;
                bestEdgesRATTinit = initEdges;
                bestCostRATTinit = initCost;
            }
            done+=1.0/(8.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");

            copyEdges = copyEdges(edgesRA);
            initCost = countCost(distMat,copyEdges);
            initEdges = copyEdges(copyEdges);
            startTime = System.nanoTime();
            while(findSwapGreedyAndSteepest(distMat,copyEdges,true,false)){};
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            avgTimeRATF += timeTime;
            if(timeTime > maxTimeRATF) maxTimeRATF = timeTime;
            if(timeTime < minTimeRATF) minTimeRATF = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostRATF += resCost;
            if(resCost>maxCostRATF) maxCostRATF = resCost;
            if(bestEdgesRATF == null || resCost< bestCostRATF){
                bestEdgesRATF = copyEdges(copyEdges);
                bestCostRATF = resCost;
                bestEdgesRATFinit = initEdges;
                bestCostRATFinit = initCost;
            }
            done+=1.0/(8.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");


            copyEdges = copyEdges(edgesRA);
            initCost = countCost(distMat,copyEdges);
            initEdges = copyEdges(copyEdges);
            startTime = System.nanoTime();
            while(findSwapGreedyAndSteepest(distMat,copyEdges,false,true)){};
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            avgTimeRAFT += timeTime;
            if(timeTime > maxTimeRAFT) maxTimeRAFT = timeTime;
            if(timeTime < minTimeRAFT) minTimeRAFT = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostRAFT += resCost;
            if(resCost>maxCostRAFT) maxCostRAFT = resCost;
            if(bestEdgesRAFT == null || resCost< bestCostRAFT){
                bestEdgesRAFT = copyEdges(copyEdges);
                bestCostRAFT = resCost;
                bestEdgesRAFTinit = initEdges;
                bestCostRAFTinit = initCost;
            }
            done+=1.0/(8.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");


            copyEdges = copyEdges(edgesRA);
            initCost = countCost(distMat,copyEdges);
            initEdges = copyEdges(copyEdges);
            startTime = System.nanoTime();
            while(findSwapGreedyAndSteepest(distMat,copyEdges,false,false)){};
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            avgTimeRAFF += timeTime;
            if(timeTime > maxTimeRAFF) maxTimeRAFF = timeTime;
            if(timeTime < minTimeRAFF) minTimeRAFF = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostRAFF += resCost;
            if(resCost>maxCostRAFF) maxCostRAFF = resCost;
            if(bestEdgesRAFF == null || resCost< bestCostRAFF){
                bestEdgesRAFF = copyEdges(copyEdges);
                bestCostRAFF = resCost;
                bestEdgesRAFFinit = initEdges;
                bestCostRAFFinit = initCost;
            }
            done+=1.0/(8.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");


            copyEdges = copyEdges(edgesGCA);
            initCost = countCost(distMat,copyEdges);
            initEdges = copyEdges(copyEdges);
            startTime = System.nanoTime();
            while(findSwapGreedyAndSteepest(distMat,copyEdges,true,true)){};
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            avgTimeGCATT += timeTime;
            if(timeTime > maxTimeGCATT) maxTimeGCATT = timeTime;
            if(timeTime < minTimeGCATT) minTimeGCATT = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostGCATT += resCost;
            if(resCost>maxCostGCATT) maxCostGCATT = resCost;
            if(bestEdgesGCATT == null || resCost< bestCostGCATT){
                bestEdgesGCATT = copyEdges(copyEdges);
                bestCostGCATT = resCost;
                bestEdgesGCATTinit = initEdges;
                bestCostGCATTinit = initCost;
            }
            done+=1.0/(8.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");



            copyEdges = copyEdges(edgesGCA);
            initCost = countCost(distMat,copyEdges);
            initEdges = copyEdges(copyEdges);
            startTime = System.nanoTime();
            while(findSwapGreedyAndSteepest(distMat,copyEdges,true,false)){};
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            avgTimeGCATF += timeTime;
            if(timeTime > maxTimeGCATF) maxTimeGCATF = timeTime;
            if(timeTime < minTimeGCATF) minTimeGCATF = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostGCATF += resCost;
            if(resCost>maxCostGCATF) maxCostGCATF = resCost;
            if(bestEdgesGCATF == null || resCost< bestCostGCATF){
                bestEdgesGCATF = copyEdges(copyEdges);
                bestCostGCATF = resCost;
                bestEdgesGCATFinit = initEdges;
                bestCostGCATFinit = initCost;
            }
            done+=1.0/(8.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");



            copyEdges = copyEdges(edgesGCA);
            initCost = countCost(distMat,copyEdges);
            initEdges = copyEdges(copyEdges);
            startTime = System.nanoTime();
            while(findSwapGreedyAndSteepest(distMat,copyEdges,false,true)){};
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            avgTimeGCAFT += timeTime;
            if(timeTime > maxTimeGCAFT) maxTimeGCAFT = timeTime;
            if(timeTime < minTimeGCAFT) minTimeGCAFT = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostGCAFT += resCost;
            if(resCost>maxCostGCAFT) maxCostGCAFT = resCost;
            if(bestEdgesGCAFT == null || resCost< bestCostGCAFT){
                bestEdgesGCAFT = copyEdges(copyEdges);
                bestCostGCAFT = resCost;
                bestEdgesGCAFTinit = initEdges;
                bestCostGCAFTinit = initCost;
            }
            done+=1.0/(8.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");


            copyEdges = copyEdges(edgesGCA);
            initCost = countCost(distMat,copyEdges);
            initEdges = copyEdges(copyEdges);
            startTime = System.nanoTime();
            while(findSwapGreedyAndSteepest(distMat,copyEdges,false,false)){};
            endTime = System.nanoTime();
            timeTime = endTime - startTime;
            avgTimeGCAFF += timeTime;
            if(timeTime > maxTimeGCAFF) maxTimeGCAFF = timeTime;
            if(timeTime < minTimeGCAFF) minTimeGCAFF = timeTime;
            resCost = countCost(distMat,copyEdges);
            avgCostGCAFF += resCost;
            if(resCost>maxCostGCAFF) maxCostGCAFF = resCost;
            if(bestEdgesGCAFF == null || resCost< bestCostGCAFF){
                bestEdgesGCAFF = copyEdges(copyEdges);
                bestCostGCAFF = resCost;
                bestEdgesGCAFFinit = initEdges;
                bestCostGCAFFinit = initCost;
            }
            done+=1.0/(8.0*iterations);
            System.out.println("Ukonczono: " +done*100+"%");
        }


//        Long minCostRandom = Long.MAX_VALUE;
//        startTime = System.nanoTime();
//        endTime = System.nanoTime();
//        while(endTime-startTime<2*10e9){
//            cDistMat = new ArrayList<>(distMat);
//            RandAlg ra = new RandAlg(cDistMat);
//            ra.process(2);
//            edgesRA = ra.getEdges();
//            resCost = countCost(distMat,edgesRA);
//            if(resCost<minCostRandom) minCostRandom = resCost;
//            endTime = System.nanoTime();
//        }
//        System.out.println("min random: " + minCostRandom);

        System.out.println("RATT: " + "min: " + bestCostRATT + " avg: " + avgCostRATT/iterations + " max: " + maxCostRATT);
        System.out.println("RATF: " + "min: " + bestCostRATF + " avg: " + avgCostRATF/iterations + " max: " + maxCostRATF);
        System.out.println("RAFT: " + "min: " + bestCostRAFT + " avg: " + avgCostRAFT/iterations + " max: " + maxCostRAFT);
        System.out.println("RAFF: " + "min: " + bestCostRAFF + " avg: " + avgCostRAFF/iterations + " max: " + maxCostRAFF);

        System.out.println("GCATT: " + "min: " + bestCostGCATT + " avg: " + avgCostGCATT/iterations + " max: " + maxCostGCATT);
        System.out.println("GCATF: " + "min: " + bestCostGCATF + " avg: " + avgCostGCATF/iterations + " max: " + maxCostGCATF);
        System.out.println("GCAFT: " + "min: " + bestCostGCAFT + " avg: " + avgCostGCAFT/iterations + " max: " + maxCostGCAFT);
        System.out.println("GCAFF: " + "min: " + bestCostGCAFF + " avg: " + avgCostGCAFF/iterations + " max: " + maxCostGCAFF);

        double giga = 1000000000.0;
        System.out.println("Time RATT: " + "min: " + (double)minTimeRATT/giga + " avg: " + (double)avgTimeRATT/iterations/giga + " max: " + (double)maxTimeRATT/giga);
        System.out.println("Time RATF: " + "min: " + (double)minTimeRATF/giga + " avg: " + (double)(avgTimeRATF/iterations)/giga + " max: " + (double)maxTimeRATF/giga);
        System.out.println("Time RAFT: " + "min: " + (double)minTimeRAFT/giga + " avg: " + (double)(avgTimeRAFT/iterations)/giga + " max: " + (double)maxTimeRAFT/giga);
        System.out.println("Time RAFF: " + "min: " + (double)minTimeRAFF/giga + " avg: " + (double)(avgTimeRAFF/iterations)/giga + " max: " + (double)maxTimeRAFF/giga);

        System.out.println("Time GCATT: " + "min: " + (double)minTimeGCATT/giga + " avg: " + (double)(avgTimeGCATT/iterations)/giga + " max: " + (double)maxTimeGCATT/giga);
        System.out.println("Time GCATF: " + "min: " + (double)minTimeGCATF/giga + " avg: " + (double)(avgTimeGCATF/iterations)/giga + " max: " + (double)maxTimeGCATF/giga);
        System.out.println("Time GCAFT: " + "min: " + (double)minTimeGCAFT/giga + " avg: " + (double)(avgTimeGCAFT/iterations)/giga + " max: " + (double)maxTimeGCAFT/giga);
        System.out.println("Time GCAFF: " + "min: " + (double)minTimeGCAFF/giga + " avg: " + (double)(avgTimeGCAFF/iterations)/giga + " max: " + (double)maxTimeGCAFF/giga);

        System.out.println("Best RATT edges:" + bestEdgesRATT);
        System.out.println("Best RATF edges:" + bestEdgesRATF);
        System.out.println("Best RAFT edges:" + bestEdgesRAFT);
        System.out.println("Best RAFF edges:" + bestEdgesRAFF);

        System.out.println("Best GCATT edges:" + bestEdgesGCATT);
        System.out.println("Best GCATF edges:" + bestEdgesGCATF);
        System.out.println("Best GCAFT edges:" + bestEdgesGCAFT);
        System.out.println("Best GCAFF edges:" + bestEdgesGCAFF);


        visualizeResults(nodes,bestEdgesRATT);
        visualizeResults(nodes,bestEdgesRATF);
        visualizeResults(nodes,bestEdgesRAFT);
        visualizeResults(nodes,bestEdgesRAFF);

        visualizeResults(nodes, bestEdgesGCATT);
        visualizeResults(nodes,bestEdgesGCATF);
        visualizeResults(nodes,bestEdgesGCAFT);
        visualizeResults(nodes,bestEdgesGCAFF);
        //System.out.println(countCost(distMat,edges));
        //System.out.println(edges);

//        System.out.println("Init cost:" + bestCostRATTinit);
//        System.out.println("new cost:" + bestCostRATT);
//        visualizeResults(nodes,bestEdgesRATTinit);
//        visualizeResults(nodes,bestEdgesRATT);

//        while(findSwapSteepest(distMat,edges)){
//            //visualizeResults(nodes,edges);
//        }

//        while(findSwapGreedyAndSteepest(distMat,edgesRA,true,true)){
//            //visualizeResults(nodes,edges);
//        }
//
//
//        System.out.println();
//        //System.out.println(countCost(distMat,edges));
//        visualizeResults(nodes,edgesRA);
    }
}

package com.example;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import java.util.ArrayList;

public class VisualizeResults {
    public static void visualizeResults(ArrayList<ArrayList<Long>> nodes, ArrayList<ArrayList<Integer>> edges){
        String stylesheet = """
                node.cycle1 {
                    fill-color: red;
                }
                node.cycle2 {
                    fill-color: blue;
                }
                edge.cycle1 {
                    fill-color: red;
                }
                edge.cycle2 {
                    fill-color: blue;
                }
                node {
                    text-alignment: above;
                }
                """;
        System.setProperty("org.graphstream.ui","swing");
        Graph graph = new SingleGraph("TSP");
        graph.setAttribute("ui.stylesheet",stylesheet);

        for (int i = 0; i < nodes.size(); i++) {
            Node node = graph.addNode(String.valueOf(i));
            node.setAttribute("xy", nodes.get(i).get(0).doubleValue(), nodes.get(i).get(1).doubleValue());
            node.setAttribute("ui.label", String.valueOf(i));

        }



        for (int i = 0; i < edges.size(); i++){
            Edge edge = graph.addEdge("Edge_" + i,edges.get(i).get(0),edges.get(i).get(1));
            if(i< edges.size()/2 + 1){
                edge.setAttribute("ui.class","cycle1");
                graph.getNode(edges.get(i).get(0)).setAttribute("ui.class","cycle1");
                graph.getNode(edges.get(i).get(1)).setAttribute("ui.class","cycle1");
            }else{
                edge.setAttribute("ui.class","cycle2");
                graph.getNode(edges.get(i).get(0)).setAttribute("ui.class","cycle2");
                graph.getNode(edges.get(i).get(1)).setAttribute("ui.class","cycle2");
            }
        }
        Viewer viewer = graph.display();
        viewer.disableAutoLayout();
    }

}

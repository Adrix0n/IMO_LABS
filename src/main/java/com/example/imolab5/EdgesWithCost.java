package com.example.imolab5;

import java.util.ArrayList;

public class EdgesWithCost {
    public ArrayList<ArrayList<Integer>> edges;
    public long cost;

    public EdgesWithCost(long cost, ArrayList<ArrayList<Integer>> edges) {
        this.edges = edges;
        this.cost = cost;
    }

    public EdgesWithCost() {
        this.edges = new ArrayList<>();
        this.cost = 0;
    }
}

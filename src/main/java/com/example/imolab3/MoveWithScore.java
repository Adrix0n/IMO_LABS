package com.example.imolab3;

import java.util.ArrayList;

public class MoveWithScore {
    public ArrayList<ArrayList<Integer>> edgeList;
    public Long score;

    public MoveWithScore(ArrayList<ArrayList<Integer>> edgeList, Long score){
        this.edgeList = edgeList;
        this.score = score;
    }

    public MoveWithScore(){
        this.edgeList = new ArrayList<>();
        this.score = 0L;
    }

    @Override
    public String toString(){
        return "Lista: " + edgeList + " score: " + score;
    }

    public MoveWithScore copy() {
        MoveWithScore copy = new MoveWithScore();
        copy.score = this.score;
        for (ArrayList<Integer> edge : this.edgeList) {
            ArrayList<Integer> edgeCopy = new ArrayList<>(edge); // kopia listy
            copy.edgeList.add(edgeCopy);
        }
        return copy;
    }
}

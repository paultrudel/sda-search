package com.search.sdasearch.summarizer;

import java.util.Comparator;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Compare sentences based on their  */
/*  intersection score                */
/*                                    */
/**************************************/

public class SentenceScoreComparator implements Comparator<Sentence> {

    @Override
    public int compare(Sentence sentence1, Sentence sentence2) {
        if(sentence1.getSentenceScore() > sentence2.getSentenceScore()) {
            return -1;
        } else if(sentence1.getSentenceScore() < sentence2.getSentenceScore()) {
            return 1;
        } else {
            return 0;
        }
    }
}

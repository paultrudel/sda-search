package com.search.sdasearch.summarizer;

import java.util.Comparator;

public class SentenceNumberComparator implements Comparator<Sentence> {

    @Override
    public int compare(Sentence sentence1, Sentence sentence2) {
        if(sentence1.getSentenceNumber() > sentence2.getSentenceNumber()) {
            return -1;
        } else if(sentence1.getSentenceNumber() < sentence2.getSentenceNumber()) {
            return 1;
        } else {
            return 0;
        }
    }
}

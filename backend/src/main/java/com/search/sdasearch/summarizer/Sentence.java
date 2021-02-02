package com.search.sdasearch.summarizer;

import lombok.Data;

@Data
public class Sentence {

    private int paragraphNumber;
    private int sentenceNumber;
    private int sentenceLength;
    private double sentenceScore;
    private int numWords;
    private String sentenceContent;

    public Sentence(int sentenceNumber, String sentenceContent, int sentenceLength, int paragraphNumber) {
        this.sentenceNumber = sentenceNumber;
        this.sentenceContent = sentenceContent;
        this.sentenceLength = sentenceLength;
        this.paragraphNumber = paragraphNumber;
    }
}

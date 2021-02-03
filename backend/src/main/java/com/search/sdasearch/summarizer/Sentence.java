package com.search.sdasearch.summarizer;

import lombok.Data;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Representation of a sentence in a */
/*  body of text                      */
/*                                    */
/**************************************/

@Data // Lombok annotation to generate boilerplate code
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

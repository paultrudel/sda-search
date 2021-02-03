package com.search.sdasearch.summarizer;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Representation of a paragraph in  */
/*  a body of text                    */
/*                                    */
/**************************************/

@Data // Lombok annotation to generate boilerplate code
public class Paragraph {

    private int paragraphNumber; // The paragraph number in the body of text
    private List<Sentence> sentences; // The sentences contained in the paragraph

    public Paragraph(int paragraphNumber) {
        this.paragraphNumber = paragraphNumber;
    }

    // Add a sentence to the paragraph
    public void addSentence(Sentence sentence) {
        if(sentences == null) {
            sentences = new ArrayList<>();
        }
        sentences.add(sentence);
    }
}

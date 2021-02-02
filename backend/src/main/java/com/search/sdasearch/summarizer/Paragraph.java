package com.search.sdasearch.summarizer;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Paragraph {

    private int paragraphNumber;
    private List<Sentence> sentences;

    public Paragraph(int paragraphNumber) {
        this.paragraphNumber = paragraphNumber;
    }

    public void addSentence(Sentence sentence) {
        if(sentences == null) {
            sentences = new ArrayList<>();
        }
        sentences.add(sentence);
    }
}

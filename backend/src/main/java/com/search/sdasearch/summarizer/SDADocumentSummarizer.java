package com.search.sdasearch.summarizer;

import com.search.sdasearch.entity.DocumentParagraph;
import com.search.sdasearch.entity.SDADocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class SDADocumentSummarizer {

    private List<Sentence> sentences, documentSummary;
    private List<Paragraph> paragraphs;
    private int numParagraphs, numSentences;
    private double[][] intersectionMatrix;
    private LinkedHashMap<Sentence, Double> dictionary;
    private static final Logger logger = LoggerFactory.getLogger(SDADocumentSummarizer.class);

    public String summarize(SDADocument sdaDocument) {
        numParagraphs = 0;
        numSentences = 0;
        sentences = new ArrayList<>();
        documentSummary = new ArrayList<>();
        paragraphs = new ArrayList<>();
        dictionary = new LinkedHashMap<>();
        extractSentencesFromDocument(sdaDocument);
        groupSentencesIntoParagraphs();
        createIntersectionMatrix();
        createDictionary();
        createDocumentSummary();
        return concatenateSummary();
    }

    private void extractSentencesFromDocument(SDADocument sdaDocument) {
        int nextChar = 0, j = 0;
        for(DocumentParagraph paragraph: sdaDocument.getParagraphs()) {
            String paragraphText = paragraph.getText();
            j = 0;
            char[] temp = new char[100000];
            for(int i = 0; i < paragraphText.length(); i++) {
                nextChar = paragraphText.charAt(i);
                if((char) nextChar != '.' && (char) nextChar != '!' && (char) nextChar != '?') {
                    temp[j] = (char) nextChar;
                    j++;
                } else {
                    sentences.add(new Sentence(
                            numSentences,
                            (new String(temp)).trim(),
                            (new String(temp)).trim().length(),
                            numParagraphs)
                    );
                    numSentences++;
                    j = 0;
                    temp = new char[100000];
                }
            }
            if((char) nextChar == '.' || (char) nextChar == '!' || (char) nextChar == '?') {
                numParagraphs++;
            }
        }
    }

    private void groupSentencesIntoParagraphs() {
        int paragraphNumber = 0;
        Paragraph paragraph = new Paragraph(paragraphNumber);

        for(int i = 0; i < numSentences; i++) {
            if(sentences.get(i).getParagraphNumber() == paragraphNumber) {

            } else {
                paragraphs.add(paragraph);
                paragraphNumber++;
                paragraph = new Paragraph(paragraphNumber);
            }
            paragraph.addSentence(sentences.get(i));
        }

        paragraphs.add(paragraph);
    }

    private double countNumberOfCommonWords(Sentence sentence1, Sentence sentence2) {
        double commonWordsCount = 0;

        for(String sentence1Word: sentence1.getSentenceContent().split("\\s+")) {
            for(String sentence2Word: sentence2.getSentenceContent().split("\\s+")) {
                if(sentence1Word.compareToIgnoreCase(sentence2Word) == 0) {
                    commonWordsCount++;
                }
            }
        }

        return commonWordsCount;
    }

    private void createIntersectionMatrix() {
        intersectionMatrix = new double[numSentences][numSentences];
        for(int i = 0; i < numSentences; i++) {
            for(int j = 0; j < numSentences; j++) {
                if(i <= j) {
                    Sentence sentence1 = sentences.get(i);
                    Sentence sentence2 = sentences.get(j);
                    intersectionMatrix[i][j] =
                            countNumberOfCommonWords(sentence1, sentence2) /
                                    ((double) (sentence1.getNumWords() + sentence2.getNumWords()) / 2);
                } else {
                    intersectionMatrix[i][j] = intersectionMatrix[j][i];
                }
            }
        }
    }

    private void createDictionary() {
        for(int i = 0; i < numSentences; i++) {
            double score = 0;
            for(int j = 0; j < numSentences; j++) {
                score += intersectionMatrix[i][j];
            }
            dictionary.put(sentences.get(i), score);
            sentences.get(i).setSentenceScore(score);
        }
    }

    private void createDocumentSummary() {
        for(int i = 0; i < numParagraphs; i++) {
            int primarySet = paragraphs.get(i).getSentences().size() / 5;
            Collections.sort(paragraphs.get(i).getSentences(), new SentenceScoreComparator());
            for(int j = 0; j < primarySet; j++) {
                documentSummary.add(paragraphs.get(i).getSentences().get(j));
            }
        }

        Collections.sort(documentSummary, new SentenceNumberComparator());
    }

    private String concatenateSummary() {
        StringBuilder sb = new StringBuilder();
        for(Sentence sentence: documentSummary) {
            sb.append(sentence.getSentenceContent() + " ");
        }
        return sb.toString();
    }
}

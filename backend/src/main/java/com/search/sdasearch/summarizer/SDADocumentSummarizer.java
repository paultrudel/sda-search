package com.search.sdasearch.summarizer;

import com.search.sdasearch.entity.DocumentParagraph;
import com.search.sdasearch.entity.SDADocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Attempts to summarize the content */
/*  of a document                     */
/*                                    */
/**************************************/

public class SDADocumentSummarizer {

    private List<Sentence> sentences, documentSummary; // Sentences in the document and the collection of sentence which make up the summary
    private List<Paragraph> paragraphs; // Paragraphs in the document
    private int numParagraphs, numSentences; // Number of sentences and paragraphs
    private double[][] intersectionMatrix; // Intersection matrix where entries represent the degree to which two sentences are similar
    private LinkedHashMap<Sentence, Double> dictionary; // Maps sentences to their intersection value
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

    // Extract each sentence individually from the given document
    private void extractSentencesFromDocument(SDADocument sdaDocument) {
        int nextChar = 0, j = 0;
        
        // Loop over the paragraphs in the document
        for(DocumentParagraph paragraph: sdaDocument.getParagraphs()) {
            String paragraphText = paragraph.getText(); // Get the text found in the paragraph
            j = 0;
            char[] temp = new char[100000]; // char array to hold the content of the sentence
            
            // Loop over the text in the paragraph character by character
            for(int i = 0; i < paragraphText.length(); i++) {
                nextChar = paragraphText.charAt(i); // Get the character at index i of the paragraph text
                
                // If the character is not punctuation which ends a sentence add the character to the sentence text
                if((char) nextChar != '.' && (char) nextChar != '!' && (char) nextChar != '?') {
                    temp[j] = (char) nextChar; // append character to sentence text
                    j++;
                } else {
                    // If the character is punctuation which ends a sentence then create a new Sentence object with the text
                    sentences.add(new Sentence(
                            numSentences,
                            (new String(temp)).trim(),
                            (new String(temp)).trim().length(),
                            numParagraphs)
                    );
                    numSentences++; // Increment the number of sentences in the document
                    j = 0;
                    temp = new char[100000]; // Create a new char array for the next sentence
                }
            }
            // If the final character of the paragraph text is punctuation which ends a sentence then increment the number of sentences
            if((char) nextChar == '.' || (char) nextChar == '!' || (char) nextChar == '?') {
                numParagraphs++;
            }
        }
    }

    // Add the sentences to their respective paragprahs
    private void groupSentencesIntoParagraphs() {
        int paragraphNumber = 0;
        Paragraph paragraph = new Paragraph(paragraphNumber); // Create a new paragraph

        // Loop over all the sentences in the document
        for(int i = 0; i < numSentences; i++) {
            // Add the sentence to the paragraph with the number corresponding to the paragraph number of the sentence
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

    // Count the number words that the two given sentences have in common
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

    // Create the matrix of sentence intersection (similarity) values
    private void createIntersectionMatrix() {
        intersectionMatrix = new double[numSentences][numSentences]; // Create new N x N matrix where N is the number of sentences in the document
        
        // Loop over all the sentences twice
        for(int i = 0; i < numSentences; i++) {
            for(int j = 0; j < numSentences; j++) {
                // If index i is less than index j then compute the intersection value of sentence i and sentence j
                if(i <= j) {
                    Sentence sentence1 = sentences.get(i);
                    Sentence sentence2 = sentences.get(j);
                    intersectionMatrix[i][j] =
                            countNumberOfCommonWords(sentence1, sentence2) /
                                    ((double) (sentence1.getNumWords() + sentence2.getNumWords()) / 2);
                } else {
                    // Since the matrix is symmetric entry j,i is the same as entry i,j
                    intersectionMatrix[i][j] = intersectionMatrix[j][i];
                }
            }
        }
    }

    // Map each sentence to its intersection value
    private void createDictionary() {
        // Loop over each sentence in the document
        for(int i = 0; i < numSentences; i++) {
            double score = 0;
            
            // Compute the sentence score for sentence i by looping over each sentence in the document
            // The intersection score of a sentence is the sum of all of its itersection scores with all other sentences
            for(int j = 0; j < numSentences; j++) {
                score += intersectionMatrix[i][j];
            }
            
            dictionary.put(sentences.get(i), score); // Add the sentence to score map entry
            sentences.get(i).setSentenceScore(score); // Set the score for the sentence
        }
    }

    // Generate the summary of the document
    private void createDocumentSummary() {
        // Loop over each paragraph in the document
        for(int i = 0; i < numParagraphs; i++) {
            int primarySet = paragraphs.get(i).getSentences().size() / 5; // The number of sentences from the paragraph to be chosen
            Collections.sort(paragraphs.get(i).getSentences(), new SentenceScoreComparator()); // Sort the sentences in the paragraph by their score
            
            // Add the top sentences to the document summary
            for(int j = 0; j < primarySet; j++) {
                documentSummary.add(paragraphs.get(i).getSentences().get(j));
            }
        }

        Collections.sort(documentSummary, new SentenceNumberComparator()); // Sort the document summary by sentence order in the document
    }

    // Collection all of the sentence text together into one string
    private String concatenateSummary() {
        StringBuilder sb = new StringBuilder();
        
        // Loop over each sentence in the summary
        for(Sentence sentence: documentSummary) {
            sb.append(sentence.getSentenceContent() + " "); // Append the content of the sentence
        }
        return sb.toString(); // Return the summary
    }
}

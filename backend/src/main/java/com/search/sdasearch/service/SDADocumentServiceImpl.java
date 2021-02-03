package com.search.sdasearch.service;

import com.search.sdasearch.dao.SDADocumentRepository;
import com.search.sdasearch.entity.SDADocument;
import com.search.sdasearch.utility.SDASetup;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FeatureField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Implementation of the             */
/*  SDADocumentService                */
/*                                    */
/**************************************/

@Service // Annotate class so that it can be injected
public class SDADocumentServiceImpl implements SDADocumentService {

    private static final Logger logger = LoggerFactory.getLogger(
            SDADocumentServiceImpl.class);

    // Field names contained in the document index
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String FEATURES = "features";
    public static final String PAGERANK = "pagerank";

    private SDADocumentRepository documentRepository;

    // Inject the document repository using constructor injection
    @Autowired
    public SDADocumentServiceImpl(SDADocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    // Search the index for relevant documents using the user provided query
    @Override
    public Page<SDADocument> searchDocuments(String queryString, Pageable pageable) {
        logger.info("========= Starting search for query: {} ========", queryString);
        // If the query is an empty string then return all of the documents
        if(queryString.isEmpty()) {
            return documentRepository.findAll(pageable);
        }
        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(
                    new File(SDASetup.INDEX).toPath()
            )); // Open an index reader at the directory containing the document index
            IndexSearcher searcher = new IndexSearcher(reader); // Initialize a index searcher using the reader
            Analyzer analyzer = new StandardAnalyzer(); // Create a new document analyzer

            // Create a query to search the title field in the indexed documents
            QueryParser titleParser = new QueryParser(TITLE, analyzer);
            Query titleQuery = titleParser.parse(queryString);

            // Create a query to search the content field in the indexed documents
            QueryParser contentParser = new QueryParser(CONTENT, analyzer);
            Query contentQuery = contentParser.parse(queryString);

            // Create a query to boost documents scores using the stored PageRank scores
            Query boostQuery = FeatureField.newSaturationQuery(FEATURES, PAGERANK);

            // Create the final query which combines all of the previous queries
            Query finalQuery = new BooleanQuery.Builder()
                    .add(titleQuery, BooleanClause.Occur.MUST)
                    .add(contentQuery, BooleanClause.Occur.MUST)
                    .add(boostQuery, BooleanClause.Occur.SHOULD)
                    .build();
            
            TopDocs topDocs = searcher.search(finalQuery, 100); // Search the index and return the top 100 documents
            ScoreDoc[] hits = topDocs.scoreDocs; // Get the documents that were returned
            logger.info("Search returned {} documents", hits.length);
            return documentListToPage(getDocuments(searcher, hits), pageable); // Return the list of documents as a page
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Map the indexed documents to their corresponding SDADocument and return the list
    private List<SDADocument> getDocuments(IndexSearcher searcher, ScoreDoc[] hits) {
        List<SDADocument> documents = new ArrayList<>();

        // Loop over all over the returned documents
        for(ScoreDoc hit: hits) {
            Document indexDocument;
            try {
                indexDocument = searcher.doc(hit.doc); // Get the document from the index
                logger.info("Getting document from index with ID {}", indexDocument.get(ID));
                Long id = Long.parseLong(indexDocument.get(ID)); // Get the value of the ID field from the document
                if(id != null) {
                    logger.info("Getting document from DB with ID {}", id);
                    Optional<SDADocument> result = documentRepository.findById(id); // Use the ID to get the corresponding SDADocument
                    SDADocument sdaDocument = null;
                    if(result.isPresent()) {
                        sdaDocument = result.get(); // Get the SDaDocument
                        sdaDocument.setQueryScore(hit.score); // Set the query score assigned to the document by Lucene
                        documents.add(sdaDocument);
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        return documents; // Return the list of SDADocuments
    }

    // Convert the list of SDADocuments to a page
    private Page<SDADocument> documentListToPage(List<SDADocument> documents,
                                                 Pageable pageable) {
        logger.info("Converting returned document list to page");
        int start = (int) pageable.getOffset(); // Get the start index
        int end = (int) (Math.min((start + pageable.getPageSize()), documents.size())); // Compute the end index
        
        // Create a page of SDADocuments
        Page<SDADocument> documentsPage = new PageImpl<>(
                documents.subList(start, end), pageable, documents.size()
        );
        
        return documentsPage; // Return the page of SDADocuments
    }
}

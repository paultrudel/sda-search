package com.search.sdasearch.indexer;

import com.search.sdasearch.dao.SDADocumentDAO;
import com.search.sdasearch.entity.*;
import com.search.sdasearch.utility.CrawlGraph;
import com.search.sdasearch.utility.SDASetup;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Use Lucene to index the documents */
/*  that were crawled and stored in   */
/*  the database                      */
/*                                    */
/**************************************/

public class SDADocumentIndexer {

    // Names of the fields that will indexed for each SDADocument
    public static final String ID = "id";
    public static final String URL = "url";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String FEATURES = "features";
    public static final String PAGERANK = "pagerank";

    private SDADocumentDAO documentDAO; // Class handling data interaction
    private SDAPageRank pageRank; // Class used to compute PageRank scores for each SDADocument
    private CrawlGraph graph; // Class which holds the crawl graph
    private HashMap<Long, Double> pageRankMap; // Map of SDAdocument IDs to PageRank scores
    private double alpha = 0.1; // Alpha value to be used in PageRank calculations

    private static final Logger logger = LoggerFactory.getLogger(SDADocumentIndexer.class);

    public SDADocumentIndexer() {
        documentDAO = SDADocumentDAO.getInstance();
        pageRank = new SDAPageRank();
        graph = CrawlGraph.getInstance();
    }

    public void indexDocuments() {
        getPageRanks(); // Compute PageRank scores
        IndexWriter writer = null;
        FSDirectory directory = null;

        try {
            logger.info("Opening index directory {}", SDASetup.INDEX);
            directory = FSDirectory.open(new File(SDASetup.INDEX).toPath()); // Open directory where the index will be stored
            Analyzer analyzer = new StandardAnalyzer(); // Create a new document analyzer
            IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer); // Create new writer configuration
            writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE); // Set the writer configuration to Open
            logger.info("Setting index writer");
            writer = new IndexWriter(directory, writerConfig); // Create the index writer
            indexDocuments(writer); // Index the documents
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) {
                    writer.close();
                }
                if(directory != null) {
                    directory.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Obtain the PageRank scores
    private void getPageRanks() {
        pageRankMap = pageRank.computePageRanks(graph.getGraph(), alpha);
        savePageRanks();
    }

    // Save the PageRank scores to the corresponding SDADocument entity
    private void savePageRanks() {
        for(Map.Entry<Long, Double> pageRankEntry: pageRankMap.entrySet()) {
            SDADocument document = documentDAO.find(pageRankEntry.getKey());
            document.setPageRank(pageRankEntry.getValue().floatValue());
            documentDAO.save(document);
        }
    }

    // Index the SDADocuments
    private void indexDocuments(IndexWriter writer) {
        logger.info("Indexing documents");
        List<SDADocument> documents = documentDAO.findAllDocuments(); // Retrieve all the SDADocuments
        logger.info("Found {} documents to index", documents.size());
        // Loop through the list of SDADocuments and index each one
        for(SDADocument document: documents) {
            indexDocument(writer, document);
        }
    }

    // Create an index of the provided SDADocument
    private void indexDocument(IndexWriter writer, SDADocument sdaDocument) {
        logger.info("========= Beginning to index document {} =========",
                sdaDocument.getTitle());
        Document document = new Document(); // Create a new Lucene Index Document

        try {
            StoredField id = new StoredField(ID, sdaDocument.getId()); // Create a new field to store SDADocument ID
            document.add(id); // Add the field to the Lucene document
            logger.info("Added field id to indexed document with value {}", id);

            TextField url = new TextField(URL, sdaDocument.getUrl(), Field.Store.YES); // Create a new field to store the SDADocument URL
            document.add(url); // Add the field to the Lucene document
            logger.info("Added field url to indexed document with value {}", url);

            TextField title = new TextField(TITLE, sdaDocument.getTitle(), Field.Store.YES); // Create new field to store the SDADocument title
            document.add(title); // Add the field to the Lucene document
            logger.info("Added field title to indexed document with value {}", title);

            // Create a new field to store the SDADocument content
            TextField content = new TextField(
                    CONTENT,
                    compileSDADocumentContent(sdaDocument),
                    Field.Store.YES
            );
            document.add(content); // Add the field to the Lucene document
            logger.info("Added field content to indexed document");

            // Create a new field to store the SDADocument PageRank score
            FeatureField pagerank = new FeatureField(
                    FEATURES,
                    PAGERANK,
                    sdaDocument.getPageRank()
            );
            document.add(pagerank); // Add the field to the document
            logger.info("Added field pagerank to indexed document with value {}", pagerank);

            logger.info("Document {} added to writer", sdaDocument.getTitle());
            writer.addDocument(document); // Write the Lucene document to the index directory
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Compile all of the content found in the given SDADocument into one String
    private String compileSDADocumentContent(SDADocument document) {
        logger.info("Compiling content from sda document {}", document.getTitle());
        StringBuilder sb = new StringBuilder();

        // Retireve all of the corresponding DocumentHeadings
        List<DocumentHeading> headings =
                documentDAO.getDocumentRelation(document, DocumentHeading.class);
        
        // Retrieve all of the corresponding DocumentImages
        List<DocumentImage> images =
                documentDAO.getDocumentRelation(document, DocumentImage.class);
        
        // Retrieve all of the corresponding DocumentLinks
        List<DocumentLink> links =
                documentDAO.getDocumentRelation(document, DocumentLink.class);
        
        // Retrieve all of the corresponding DocumentParagraphs
        List<DocumentParagraph> paragraphs =
                documentDAO.getDocumentRelation(document, DocumentParagraph.class);

        // Append all of the text from the DocumentHeadings
        for(DocumentHeading heading: headings) {
            sb.append(heading.getText() + " ");
        }

        // Append all of the text from "alt" attributes in the DocumentImages
        for(DocumentImage image: images) {
            sb.append(image.getAltText() + " ");
        }

        // Append all of the text from the DocumentLinks
        for(DocumentLink link: links) {
            sb.append(link.getText() + " ");
        }

        // Append all of the text from the DocumentParapgraphs
        for(DocumentParagraph paragraph: paragraphs) {
            sb.append(paragraph.getText() + " ");
        }

        return sb.toString(); // Return the compiled SDADocument content
    }
}

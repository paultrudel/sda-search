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

public class SDADocumentIndexer {

    public static final String ID = "id";
    public static final String URL = "url";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String FEATURES = "features";
    public static final String PAGERANK = "pagerank";

    private SDADocumentDAO documentDAO;
    private SDAPageRank pageRank;
    private CrawlGraph graph;
    private HashMap<Long, Double> pageRankMap;
    private double alpha = 0.1;

    private static final Logger logger = LoggerFactory.getLogger(SDADocumentIndexer.class);

    public SDADocumentIndexer() {
        documentDAO = SDADocumentDAO.getInstance();
        pageRank = new SDAPageRank();
        graph = CrawlGraph.getInstance();
    }

    public void indexDocuments() {
        //getPageRanks(); Requires too much memory
        IndexWriter writer = null;
        FSDirectory directory = null;

        try {
            logger.info("Opening index directory {}", SDASetup.INDEX);
            directory = FSDirectory.open(new File(SDASetup.INDEX).toPath());
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
            writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            logger.info("Setting index writer");
            writer = new IndexWriter(directory, writerConfig);
            indexDocuments(writer);
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

    private void getPageRanks() {
        pageRankMap = pageRank.computePageRanks(graph.getGraph(), alpha);
        savePageRanks();
    }

    private void savePageRanks() {
        for(Map.Entry<Long, Double> pageRankEntry: pageRankMap.entrySet()) {
            SDADocument document = documentDAO.find(pageRankEntry.getKey());
            document.setPageRank(pageRankEntry.getValue().floatValue());
            documentDAO.save(document);
        }
    }

    private void indexDocuments(IndexWriter writer) {
        logger.info("Indexing documents");
        List<SDADocument> documents = documentDAO.findAllDocuments();
        logger.info("Found {} documents to index", documents.size());
        for(SDADocument document: documents) {
            indexDocument(writer, document);
        }
    }

    private void indexDocument(IndexWriter writer, SDADocument sdaDocument) {
        logger.info("========= Beginning to index document {} =========",
                sdaDocument.getTitle());
        Document document = new Document();

        try {
            StoredField id = new StoredField(ID, sdaDocument.getId());
            document.add(id);
            logger.info("Added field id to indexed document with value {}", id);

            TextField url = new TextField(URL, sdaDocument.getUrl(), Field.Store.YES);
            document.add(url);
            logger.info("Added field url to indexed document with value {}", url);

            TextField title = new TextField(TITLE, sdaDocument.getTitle(), Field.Store.YES);
            document.add(title);
            logger.info("Added field title to indexed document with value {}", title);

            TextField content = new TextField(
                    CONTENT,
                    compileSDADocumentContent(sdaDocument),
                    Field.Store.YES
            );
            document.add(content);
            logger.info("Added field content to indexed document");

//            FeatureField pagerank = new FeatureField(
//                    FEATURES,
//                    PAGERANK,
//                    sdaDocument.getPageRank()
//            );
//            document.add(pagerank);
//            logger.info("Added field pagerank to indexed document with value {}", pagerank);

            logger.info("Document {} added to writer", sdaDocument.getTitle());
            writer.addDocument(document);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String compileSDADocumentContent(SDADocument document) {
        logger.info("Compiling content from sda document {}", document.getTitle());
        StringBuilder sb = new StringBuilder();

        List<DocumentHeading> headings =
                documentDAO.getDocumentRelation(document, DocumentHeading.class);
        List<DocumentImage> images =
                documentDAO.getDocumentRelation(document, DocumentImage.class);
        List<DocumentLink> links =
                documentDAO.getDocumentRelation(document, DocumentLink.class);
        List<DocumentParagraph> paragraphs =
                documentDAO.getDocumentRelation(document, DocumentParagraph.class);

        for(DocumentHeading heading: headings) {
            sb.append(heading.getText() + " ");
        }

        for(DocumentImage image: images) {
            sb.append(image.getAltText() + " ");
        }

        for(DocumentLink link: links) {
            sb.append(link.getText() + " ");
        }

        for(DocumentParagraph paragraph: paragraphs) {
            sb.append(paragraph.getText() + " ");
        }

        return sb.toString();
    }
}

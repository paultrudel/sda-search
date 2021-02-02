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

@Service
public class SDADocumentServiceImpl implements SDADocumentService {

    private static final Logger logger = LoggerFactory.getLogger(
            SDADocumentServiceImpl.class);

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String FEATURES = "features";
    public static final String PAGERANK = "pagerank";

    private SDADocumentRepository documentRepository;

    @Autowired
    public SDADocumentServiceImpl(SDADocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public Page<SDADocument> searchDocuments(String queryString, Pageable pageable) {
        logger.info("========= Starting search for query: {} ========", queryString);
        if(queryString.isEmpty()) {
            return documentRepository.findAll(pageable);
        }
        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(
                    new File(SDASetup.INDEX).toPath()
            ));
            IndexSearcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new StandardAnalyzer();

            QueryParser titleParser = new QueryParser(TITLE, analyzer);
            Query titleQuery = titleParser.parse(queryString);

            QueryParser contentParser = new QueryParser(CONTENT, analyzer);
            Query contentQuery = contentParser.parse(queryString);

            //Query boostQuery = FeatureField.newSaturationQuery(FEATURES, PAGERANK);

            Query finalQuery = new BooleanQuery.Builder()
                    .add(titleQuery, BooleanClause.Occur.MUST)
                    .add(contentQuery, BooleanClause.Occur.MUST)
                    //.add(boostQuery, BooleanClause.Occur.SHOULD)
                    .build();

            TopDocs topDocs = searcher.search(finalQuery, 100);
            ScoreDoc[] hits = topDocs.scoreDocs;
            logger.info("Search returned {} documents", hits.length);
            return documentListToPage(getDocuments(searcher, hits), pageable);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<SDADocument> getDocuments(IndexSearcher searcher, ScoreDoc[] hits) {
        List<SDADocument> documents = new ArrayList<>();

        for(ScoreDoc hit: hits) {
            Document indexDocument;
            try {
                indexDocument = searcher.doc(hit.doc);
                logger.info("Getting document from index with ID {}", indexDocument.get(ID));
                Long id = Long.parseLong(indexDocument.get(ID));
                if(id != null) {
                    logger.info("Getting document from DB with ID {}", id);
                    Optional<SDADocument> result = documentRepository.findById(id);
                    SDADocument sdaDocument = null;
                    if(result.isPresent()) {
                        sdaDocument = result.get();
                        sdaDocument.setQueryScore(hit.score);
                        documents.add(sdaDocument);
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        return documents;
    }

    private Page<SDADocument> documentListToPage(List<SDADocument> documents,
                                                 Pageable pageable) {
        logger.info("Converting returned document list to page");
        int start = (int) pageable.getOffset();
        int end = (int) (Math.min((start + pageable.getPageSize()), documents.size()));
        Page<SDADocument> documentsPage = new PageImpl<>(
                documents.subList(start, end), pageable, documents.size()
        );
        return documentsPage;
    }
}

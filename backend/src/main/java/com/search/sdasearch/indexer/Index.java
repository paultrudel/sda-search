package com.search.sdasearch.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Index {

    private static final Logger logger = LoggerFactory.getLogger(Index.class);

    public static void main(String args[]) {
        logger.info("========= Starting to index documents =========");
        SDADocumentIndexer indexer = new SDADocumentIndexer();
        indexer.indexDocuments();
        logger.info("========= Finished indexing documents =========");
    }
}

package com.search.sdasearch.crawler;

import com.search.sdasearch.utility.SDADocumentHandler;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  User defined web crawler          */
/*                                    */
/**************************************/

public class SDACrawler extends WebCrawler {

    private SDADocumentHandler documentHandler; // Class response for handling the parsing of crawled documents

    public SDACrawler() {
        documentHandler = new SDADocumentHandler();
    }

    // Determine whether the next page in the queue should be visited
    @Override
    public boolean shouldVisit(Page refferingPage, WebURL url) {
        return true;
    }

    // Pass the visited page of to the DocumentHandler for parsing
    @Override
    public void visit(Page page) {
        logger.info("========== Visiting Page {} ==========", page.getWebURL());
        documentHandler.handlePage(page);
        logger.info("========== Finished Visiting {} ==========", page.getWebURL());
    }
}

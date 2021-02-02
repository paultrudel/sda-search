package com.search.sdasearch.crawler;

import com.search.sdasearch.utility.SDADocumentHandler;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

public class SDACrawler extends WebCrawler {

    private SDADocumentHandler documentHandler;

    public SDACrawler() {
        documentHandler = new SDADocumentHandler();
    }

    @Override
    public boolean shouldVisit(Page refferingPage, WebURL url) {
        return true;
    }

    @Override
    public void visit(Page page) {
        logger.info("========== Visiting Page {} ==========", page.getWebURL());
        documentHandler.handlePage(page);
        logger.info("========== Finished Visiting {} ==========", page.getWebURL());
    }
}

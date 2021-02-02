package com.search.sdasearch.crawler;

import com.google.common.collect.ImmutableList;
import com.search.sdasearch.utility.CrawlGraph;
import com.search.sdasearch.utility.SDASetup;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Class responsible for handling    */
/*  the web crawler configuration     */
/*                                    */
/**************************************/

public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    public static void main(String args[]) throws Exception {
        SDASetup.setup(); // Initialze the required directories

        CrawlConfig config = new CrawlConfig(); // Create new crawler configuration
        config.setCrawlStorageFolder(SDASetup.STORAGE); // Set the crawler storage folder
        config.setPolitenessDelay(1000); // Set the delay between requests sent to the same domain
        config.setMaxDepthOfCrawling(2); // Set the depth that crawler should go to from the root url
        config.setMaxPagesToFetch(10000); // Set the maximum number of web pages to be fetched
        config.setIncludeHttpsPages(true); // Include Https domains
        config.setResumableCrawling(true); // Resume the crawl in the cause of a crash or manual stoppage
        config.setIncludeBinaryContentInCrawling(false); // Do not crawl binary content (images, documents, etc.)

        PageFetcher pf = new PageFetcher(config);
        RobotstxtConfig robotsConfig = new RobotstxtConfig();
        RobotstxtServer robotsServer = new RobotstxtServer(robotsConfig, pf);

        CrawlController controller = new CrawlController(
                config, pf, robotsServer
        );


        // List of root domains from which to start the crawl
        List<String> crawlDomains = ImmutableList.of(
                "https://en.wikipedia.org/wiki/Artificial_general_intelligence",
                "https://en.wikipedia.org/wiki/Ray_Kurzweil",
                "https://en.wikipedia.org/wiki/Ben_Goertzel"
        );
        
        // Add the seed (root) domains to the controller
        for(String domain: crawlDomains) {
            controller.addSeed(domain);
        }

        // Initialize the user created crawler type
        CrawlController.WebCrawlerFactory<SDACrawler> factory = () ->
                new SDACrawler();
        int numCrawlers = 1; // Number of crawlers to run simultaneously

        logger.info("========== Crawler Started ==========");
        controller.start(factory, numCrawlers); // Start the crawl

        // Export the crawl graph when crawling is completed
        if(controller.isFinished()) {
            logger.info("========== Crawler Finished ==========");
            CrawlGraph.getInstance().exportGraph();
        }
    }
}

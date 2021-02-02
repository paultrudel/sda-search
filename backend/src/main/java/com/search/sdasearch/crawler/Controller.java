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

public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    public static void main(String args[]) throws Exception {
        SDASetup.setup();

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(SDASetup.STORAGE);
        config.setPolitenessDelay(1000);
        config.setMaxDepthOfCrawling(2);
        config.setMaxPagesToFetch(10000);
        config.setIncludeHttpsPages(true);
        config.setResumableCrawling(true);
        config.setIncludeBinaryContentInCrawling(false);

        PageFetcher pf = new PageFetcher(config);
        RobotstxtConfig robotsConfig = new RobotstxtConfig();
        RobotstxtServer robotsServer = new RobotstxtServer(robotsConfig, pf);

        CrawlController controller = new CrawlController(
                config, pf, robotsServer
        );


        List<String> crawlDomains = ImmutableList.of(
                "https://en.wikipedia.org/wiki/Artificial_general_intelligence",
                "https://en.wikipedia.org/wiki/Ray_Kurzweil",
                "https://en.wikipedia.org/wiki/Ben_Goertzel"
        );
        for(String domain: crawlDomains) {
            controller.addSeed(domain);
        }

        CrawlController.WebCrawlerFactory<SDACrawler> factory = () ->
                new SDACrawler();
        int numCrawlers = 1;

        logger.info("========== Crawler Started ==========");
        controller.start(factory, numCrawlers);

        if(controller.isFinished()) {
            logger.info("========== Crawler Finished ==========");
            CrawlGraph.getInstance().exportGraph();
        }
    }
}

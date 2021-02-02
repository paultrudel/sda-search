package com.search.sdasearch.utility;

import com.search.sdasearch.dao.*;
import com.search.sdasearch.entity.*;
import com.search.sdasearch.summarizer.SDADocumentSummarizer;
import edu.uci.ics.crawler4j.crawler.Page;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class SDADocumentHandler {

    private SDADocumentDAO documentDAO;
    private SDADocumentSummarizer documentSummarizer;
    private CrawlGraph crawlGraph;
    private Logger logger = LoggerFactory.getLogger(SDADocumentHandler.class);

    public SDADocumentHandler() {
        documentDAO = SDADocumentDAO.getInstance();
        documentSummarizer = new SDADocumentSummarizer();
        crawlGraph = CrawlGraph.getInstance();
    }

    public void handlePage(Page page) {
        logger.info("Beginning to handle page {}", page.getWebURL());
        SDADocument sdaDocument = parsePage(page);
        Long docId = documentDAO.save(sdaDocument);
        Long parentDocId = sdaDocument.getParentId();
        if (parentDocId != null) {
            crawlGraph.addEdge(parentDocId, docId);
        }
        else {
            crawlGraph.addVertex(docId);
        }
    }

    private SDADocument parsePage(Page page) {
        logger.info("Parsing page {}", page.getWebURL());
        SDADocument sdaDocument = new SDADocument();
        String url = page.getWebURL().getURL();
        String parentUrl = page.getWebURL().getParentUrl();
        try {
            Document document = Jsoup.connect(url).get();
            sdaDocument.setUrl(url);
            sdaDocument.setTitle(document.title());
            if(parentUrl != null) {
                SDADocument parentDocument = documentDAO.findDocumentByUrl(parentUrl);
                if(parentDocument != null) {
                    sdaDocument.setParentId(parentDocument.getId());
                }
            }
            parseHeadings(sdaDocument, document);
            parseImages(sdaDocument, document);
            parseLinks(sdaDocument, document);
            parseMetadata(sdaDocument, document);
            parseParagraphs(sdaDocument, document);
            String documentSummary = documentSummarizer.summarize(sdaDocument);
            if(documentSummary != null) {
                sdaDocument.setDocumentSummary(documentSummary);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sdaDocument;
    }

    private void parseHeadings(SDADocument sdaDocument, Document document) {
        logger.info("Parsing headings");
        Elements h = document.select("h1, h2, h3, h4, h5, h6");
        sdaDocument.setHeadings(new HashSet<>());
        for(Element heading: h) {
            if(heading.text().length() > 0) {
                DocumentHeading docHeading = new DocumentHeading();
                docHeading.setText(heading.text());
                sdaDocument.addHeading(docHeading);
            }
        }
    }

    private void parseImages(SDADocument sdaDocument, Document document) {
        logger.info("Parsing images");
        Elements i = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
        sdaDocument.setImages(new HashSet<>());
        for(Element image: i) {
            DocumentImage docImage = new DocumentImage();
            docImage.setLink(image.attr("src"));
            docImage.setAltText(image.attr("alt"));
            sdaDocument.addImage(docImage);
        }
    }

    private void parseLinks(SDADocument sdaDocument, Document document) {
        logger.info("Parsing links");
        Elements a = document.select("a[href]");
        sdaDocument.setLinks(new HashSet<>());
        for(Element link: a) {
            DocumentLink docLink = new DocumentLink();
            docLink.setText(link.attr("href"));
            sdaDocument.addLink(docLink);
        }
    }

    private void parseMetadata(SDADocument sdaDocument, Document document) {
        logger.info("Parsing metadata");
        Elements m = document.select("meta");
        sdaDocument.setMetadata(new HashSet<>());
        for(Element meta: m) {
            DocumentMetadata docMetadata = new DocumentMetadata();
            docMetadata.setName(meta.attr("name"));
            docMetadata.setContent(meta.attr("content"));
            sdaDocument.addMetadata(docMetadata);
        }
    }

    private void parseParagraphs(SDADocument sdaDocument, Document document) {
        logger.info("Parsing paragraphs");
        Elements p = document.select("p");
        sdaDocument.setParagraphs(new HashSet<>());
        for(Element paragraph: p) {
            if(paragraph.text().length() > 30) {
                DocumentParagraph docParagraph = new DocumentParagraph();
                docParagraph.setText(paragraph.text());
                sdaDocument.addParagraph(docParagraph);
            }
        }
    }
}

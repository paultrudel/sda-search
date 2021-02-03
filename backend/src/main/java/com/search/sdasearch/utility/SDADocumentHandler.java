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

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Handles the parsing of crawled    */
/*  documents                         */
/*                                    */
/**************************************/

public class SDADocumentHandler {

    private SDADocumentDAO documentDAO; // Handles database interaction
    private SDADocumentSummarizer documentSummarizer; // Summarizes the content of the parsed documents
    private CrawlGraph crawlGraph; // Graph of the crawled web pages
    private Logger logger = LoggerFactory.getLogger(SDADocumentHandler.class);

    public SDADocumentHandler() {
        documentDAO = SDADocumentDAO.getInstance();
        documentSummarizer = new SDADocumentSummarizer();
        crawlGraph = CrawlGraph.getInstance();
    }

    // Handle the parsing of the crawled page
    public void handlePage(Page page) {
        logger.info("Beginning to handle page {}", page.getWebURL());
        SDADocument sdaDocument = parsePage(page); // Create a new SDADocument
        Long docId = documentDAO.save(sdaDocument); // Save the SDADocument to the database and get the ID
        Long parentDocId = sdaDocument.getParentId(); // Get the parent ID of the SDADocument
        
        // If the document has a parent document add an edge in the crawl graph
        if (parentDocId != null) {
            crawlGraph.addEdge(parentDocId, docId);
        }
        //Otherwise just add a new vertex to the crawl graph
        else {
            crawlGraph.addVertex(docId);
        }
    }

    // Parse the web page
    private SDADocument parsePage(Page page) {
        logger.info("Parsing page {}", page.getWebURL());
        
        SDADocument sdaDocument = new SDADocument(); // Create a new SDADocument
        String url = page.getWebURL().getURL(); // Get the URL of the page
        String parentUrl = page.getWebURL().getParentUrl(); // Get the parent URL of the page
        
        try {
            Document document = Jsoup.connect(url).get(); // Get the web page as a JSoup document
            sdaDocument.setUrl(url); // Set the URL
            sdaDocument.setTitle(document.title()); // Set the title
            
            // If the parent URL exists attempt to find it in the database
            if(parentUrl != null) {
                SDADocument parentDocument = documentDAO.findDocumentByUrl(parentUrl); // Find the parent SDADocument in the database
                // Set the parent SDADocument if it exists
                if(parentDocument != null) {
                    sdaDocument.setParentId(parentDocument.getId());
                }
            }
            
            parseHeadings(sdaDocument, document); // Parse the document headings
            parseImages(sdaDocument, document); // Parse the document images
            parseLinks(sdaDocument, document); // Parse the document links
            parseMetadata(sdaDocument, document); // Parse the document metadata
            parseParagraphs(sdaDocument, document); // Parse the document paragraphs
            
            String documentSummary = documentSummarizer.summarize(sdaDocument); // Summarize the document content
            
            // Set the document summary if successful
            if(documentSummary != null) {
                sdaDocument.setDocumentSummary(documentSummary);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sdaDocument;
    }

    // Parse the headings in the document
    private void parseHeadings(SDADocument sdaDocument, Document document) {
        logger.info("Parsing headings");
        Elements h = document.select("h1, h2, h3, h4, h5, h6"); // Select the headings tags from the document
        sdaDocument.setHeadings(new HashSet<>());
        
        // Loop over the heading elements
        for(Element heading: h) {
            // If the heading tag is not empty add it
            if(heading.text().length() > 0) {
                DocumentHeading docHeading = new DocumentHeading(); // Create a new DocumentHeading
                docHeading.setText(heading.text()); // Get the heading text
                sdaDocument.addHeading(docHeading); // Add the DocumentHeading to the SDADocument
            }
        }
    }

    // Parse the images in the document
    private void parseImages(SDADocument sdaDocument, Document document) {
        logger.info("Parsing images");
        Elements i = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]"); // Select the image tags from the document
        sdaDocument.setImages(new HashSet<>());
        
        // Loop over the image elements
        for(Element image: i) {
            DocumentImage docImage = new DocumentImage(); // Create a new DocumentImage
            docImage.setLink(image.attr("src")); // Get the image source
            docImage.setAltText(image.attr("alt")); // Get the image alt text
            sdaDocument.addImage(docImage); // Add the DocumentImage to the SDADocument
        }
    }

    // Parse the links in the document
    private void parseLinks(SDADocument sdaDocument, Document document) {
        logger.info("Parsing links");
        Elements a = document.select("a[href]"); // Select the anchor tags from the document
        sdaDocument.setLinks(new HashSet<>());
        
        // Loop over the achor elements
        for(Element link: a) {
            DocumentLink docLink = new DocumentLink(); // Create a new DocumentLink
            docLink.setText(link.attr("href")); // Get the anchor href text
            sdaDocument.addLink(docLink); // Add the DocumentLink to the SDADocument
        }
    }

    // Parse the document metadata
    private void parseMetadata(SDADocument sdaDocument, Document document) {
        logger.info("Parsing metadata");
        Elements m = document.select("meta"); // Select the meta tags from the document
        sdaDocument.setMetadata(new HashSet<>());
        
        // Loop over the meta elements
        for(Element meta: m) {
            DocumentMetadata docMetadata = new DocumentMetadata(); // Create a new DocumentMetadata
            docMetadata.setName(meta.attr("name")); // Get the name attribute
            docMetadata.setContent(meta.attr("content")); // Get the content attribute
            sdaDocument.addMetadata(docMetadata); // Add the DocumentMetadata to the SDADocument
        }
    }

    // Parse the paragraphs in the document
    private void parseParagraphs(SDADocument sdaDocument, Document document) {
        logger.info("Parsing paragraphs");
        Elements p = document.select("p"); // Select the paragraph tags from the document
        sdaDocument.setParagraphs(new HashSet<>());
        
        // Loop over the paragraph elements
        for(Element paragraph: p) {
            // If the paragraph text is of sufficient length add it
            if(paragraph.text().length() > 30) {
                DocumentParagraph docParagraph = new DocumentParagraph(); // Create a new DocumentParagraph
                docParagraph.setText(paragraph.text()); // Get the paragraph text
                sdaDocument.addParagraph(docParagraph); // Add the DocumentParagraph to the SDADocument
            }
        }
    }
}

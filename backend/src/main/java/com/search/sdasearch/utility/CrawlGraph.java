package com.search.sdasearch.utility;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Graph of the pages visited during */
/*  crawling                          */
/*                                    */
/**************************************/

public class CrawlGraph {

    private final String PATH = SDASetup.GRAPH + "\\CrawlGraph.dot"; // Graph export directory
    private Graph<Long, DefaultEdge> G; // Graph of pages
    private Logger logger = LoggerFactory.getLogger(CrawlGraph.class);
    private static CrawlGraph instance;

    private CrawlGraph() {
        // Attempt to import the graph if it already exists
        if(new File(PATH).exists()) {
            try {
                importGraph();
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            } catch(ImportException e) {
                e.printStackTrace();
            }
        // Create a new graph otherwise
        } else {
            G = new DirectedMultigraph<>(DefaultEdge.class);
        }
    }

    public static CrawlGraph getInstance() {
        if(instance == null) {
            instance = new CrawlGraph();
        }
        return instance;
    }

    public Graph<Long, DefaultEdge> getGraph() {
        return G;
    }

    // Add vertex to the graph with the given ID
    public synchronized void addVertex(Long id) {
        logger.info("Adding vertex with ID {}", id);
        
        // If the vertex does not already exist add it
        if(!vertexExists(id)) {
            G.addVertex(id);
        }
    }

    // Add an edge to graph
    public synchronized  void addEdge(Long parent, Long child) {
        logger.info("Adding edge from {} to {}", parent, child);
        addVertex(parent); // Add the parent vertex
        addVertex(child); // Add the child vertex
        G.addEdge(parent, child); // Add the edge between the two vertices
    }

    // Check if a vertex with the given ID exists
    private boolean vertexExists(Long id) {
        return G.vertexSet().contains(id);
    }

    // Imort the graph
    private void importGraph() throws FileNotFoundException, ImportException {
        logger.info("Importing graph");
        
        // Define a vertext provider for the graph
        VertexProvider<Long> vertexProvider = (id, attributes) -> {
            return Long.parseLong(id);
        };
        
        EdgeProvider<Long, DefaultEdge> edgeProvider = (from, to, label, attributes) -> new DefaultEdge(); // Define an edge provider for the graph
        GraphImporter<Long, DefaultEdge> importer = new DOTImporter<>(vertexProvider, edgeProvider); // Create the graph importer
        G = new DirectedMultigraph<>(DefaultEdge.class); // Create a new graph
        Reader reader = new FileReader(PATH); // Open a reader to the graph directory
        importer.importGraph(G, reader); // Import the graph
    }

    // Export the graph
    public void exportGraph() throws ExportException, IOException {
        logger.info("Exporting graph");
        
        // Define a vertext provider
        ComponentNameProvider<Long> vertexIdProvider = new ComponentNameProvider<Long>() {
            @Override
            public String getName(Long id) {
                return id.toString();
            }
        };
        
        // Create the graph exporter
        GraphExporter<Long, DefaultEdge> exporter = new DOTExporter<>(
                vertexIdProvider, vertexIdProvider, null
        );
        
        Writer writer = new FileWriter(PATH); // Open a writer to the graph directory
        exporter.exportGraph(G, writer); // Export the graph
    }
}

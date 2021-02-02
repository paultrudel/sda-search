package com.search.sdasearch.utility;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class CrawlGraph {

    private final String PATH = SDASetup.GRAPH + "\\CrawlGraph.dot";
    private Graph<Long, DefaultEdge> G;
    private Logger logger = LoggerFactory.getLogger(CrawlGraph.class);
    private static CrawlGraph instance;

    private CrawlGraph() {
        if(new File(PATH).exists()) {
            try {
                importGraph();
            } catch(FileNotFoundException e) {
                e.printStackTrace();
            } catch(ImportException e) {
                e.printStackTrace();
            }
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

    public synchronized void addVertex(Long id) {
        logger.info("Adding vertex with ID {}", id);
        if(!vertexExists(id)) {
            G.addVertex(id);
        }
    }

    public synchronized  void addEdge(Long parent, Long child) {
        logger.info("Adding edge from {} to {}", parent, child);
        addVertex(parent);
        addVertex(child);
        G.addEdge(parent, child);
    }

    private boolean vertexExists(Long id) {
        return G.vertexSet().contains(id);
    }

    private void importGraph() throws FileNotFoundException, ImportException {
        logger.info("Importing graph");
        VertexProvider<Long> vertexProvider = (id, attributes) -> {
            return Long.parseLong(id);
        };
        EdgeProvider<Long, DefaultEdge> edgeProvider = (from, to, label, attributes) -> new DefaultEdge();
        GraphImporter<Long, DefaultEdge> importer = new DOTImporter<>(vertexProvider, edgeProvider);
        G = new DirectedMultigraph<>(DefaultEdge.class);
        Reader reader = new FileReader(PATH);
        importer.importGraph(G, reader);
    }

    public void exportGraph() throws ExportException, IOException {
        logger.info("Exporting graph");
        ComponentNameProvider<Long> vertexIdProvider = new ComponentNameProvider<Long>() {
            @Override
            public String getName(Long id) {
                return id.toString();
            }
        };
        GraphExporter<Long, DefaultEdge> exporter = new DOTExporter<>(
                vertexIdProvider, vertexIdProvider, null
        );
        Writer writer = new FileWriter(PATH);
        exporter.exportGraph(G, writer);
    }
}

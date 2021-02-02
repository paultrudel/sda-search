package com.search.sdasearch.indexer;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Class responsible for handling    */
/*  the PageRank calculations for the */
/*  crawled documents                 */
/*                                    */
/**************************************/

public class SDAPageRank {

    private static final Logger logger = LoggerFactory.getLogger(SDAPageRank.class);

    // Compute the PageRank scores from the given crawl graph
    public HashMap<Long, Double> computePageRanks(Graph<Long, DefaultEdge> graph, double alpha) {
        logger.info("========= Beginning to computer page rank values ========");
        int matrixSize = graph.vertexSet().size(); // Determine the size of the matrices (matrices will be N x N where N is the number of crawled documents)
        double [][] adjMatrix = buildAdjacencyMatrix(graph, matrixSize); // Construct an adjacency matrix from the given crawl graph
        double [][] transitionMatrix = computeTransitionMatrix(adjMatrix, alpha, matrixSize); // Transform the adjacency matrix into a transition probability matrix
        Matrix pageRankMatrix = computePageRankMatrix(transitionMatrix); // Compute the PageRank scores from the transition matrix
        return buildPageRankMap(graph.vertexSet(), pageRankMatrix); // Map SDADocuments to PageRank scores
    }

    // Build an adjacency matrix from the given crawl graph
    private double[][] buildAdjacencyMatrix(Graph<Long, DefaultEdge> graph, int matrixSize) {
        logger.info("Building adjacency matrix of size {} from crawl graph", matrixSize);
        double[][] adjMatrix = new double[matrixSize][matrixSize]; // Create new N x N matrix filled with O's
        List<Long> vertexList = new ArrayList<>(graph.vertexSet()); // Obtain the list of vertices
        Collections.sort(vertexList); // Sort the vertices so that the vertext at index 1 corresponds to the first SDADocument and so on...
        
        // Loop over the edge set and put a '1' at the corresponing indices in the matrix
        for(DefaultEdge edge: graph.edgeSet()) {
            Long source = graph.getEdgeSource(edge); // Get the source index of the edge
            Long target = graph.getEdgeTarget(edge); // Get the target index of the edge
            adjMatrix[vertexList.indexOf(source)][vertexList.indexOf(target)] = 1; // Put a '1' at entry (source, target) of the matrix 
        }
        return adjMatrix; // Return teh adjacency matrix
    }

    // Use the adjacency matrix to build a transition probability matrix
    private double[][] computeTransitionMatrix(double[][] adjMatrix, double alpha, int matrixSize) {
        logger.info("Computing transition probability matrix from adjacency matrix");
        double[][] transitionMatrix = new double[matrixSize][matrixSize]; // Create new N x N filled with 0's
        double[] rowSums = computeRowSums(adjMatrix, matrixSize); // Compute the sum of each row in the adjacency matrix

        // Loop over each row in the matrix
        for(int i = 0; i < matrixSize; i++) {
            double rowSum = rowSums[i]; // Get the row sum for row i
            double[] pageRankMatrixRow = new double[matrixSize]; // Create new row for the transition matrix

            // Loop over each column in the matrix
            for(int j = 0; j < matrixSize; j++) {
                double pageRankMatrixEntry; // Initialize the matrix entry at (i, j)
                
                // If the row sum for row i is 1 set the transition matrix entry to 1/N
                if(rowSum == 0) {
                    pageRankMatrixEntry = 1 / matrixSize;
                } else {
                    // If the corresponding ajacency matrix entry is 1 then set the transition entry to 1/rowSum 
                    if(adjMatrix[i][j] == 1) {
                        pageRankMatrixEntry = 1 / rowSum;
                    }
                    // Otherwise set the transition entry to 0
                    else {
                        pageRankMatrixEntry = 0;
                    }
                }
                pageRankMatrixEntry = pageRankMatrixEntry * (1 - alpha); // Multiply the transition matrix entry by 1 - alpha
                pageRankMatrixEntry += alpha / matrixSize; // Add to the transition matrix entry alpha/N
                pageRankMatrixRow[j] = pageRankMatrixEntry;
            }

            transitionMatrix[i] = pageRankMatrixRow;
        }

        return transitionMatrix;
    }

    // Compute the sums for each row in the adjacency matrix
    private double[] computeRowSums(double[][] adjMatrix, int matrixSize) {
        double[] rowSums = new double[matrixSize];
        for(int i = 0; i < matrixSize; i++) {
            double rowSum = 0;
            for(int j = 0; j < matrixSize; j++) {
                rowSum += adjMatrix[i][j];
            }
            rowSums[i] = rowSum;
        }
        return rowSums;
    }

    // Compute the PageRank scores using the transition matrix
    private Matrix computePageRankMatrix(double[][] transitionMatrix) {
        logger.info("Computing the stationary distribution of the transition matrix");
        Matrix pageRankMatrix = new Matrix(transitionMatrix);
        Matrix prevMatrix;
        // Use repeated squaring of the transition matrix to obtain the stationary distribution of the process
        do {
            prevMatrix = pageRankMatrix;
            pageRankMatrix = pageRankMatrix.times(pageRankMatrix); // Multiple the transition matrix by itself
        } while(!shouldTerminate(prevMatrix, pageRankMatrix)); // Check if the matrix has converged and stop if it has
        return pageRankMatrix; // Return the transition matrix containing the PageRank scores
    }

    // Determine if the matrix has converged and therefore repeated squaring should terminate
    private boolean shouldTerminate(Matrix prevMatrix, Matrix nextMatrix) {
        double maxDifference = 0.01; // Maximum allowable difference between any entry in the pervious matrix and the next matrix

        // Loop over each entry in the matrices and compute the different between the entries
        for(int row = 0; row < prevMatrix.getRowDimension(); row++) {
            for(int col = 0; col < prevMatrix.getColumnDimension(); col++) {
                double difference = Math.abs(
                        prevMatrix.get(row, col) - nextMatrix.get(row, col)
                );
                // Do no terminate if the difference is greater than the threshold
                if(difference > maxDifference) {
                    return false;
                }
            }
        }

        return true; // If no entries have a difference greater than the threshold then terminate
    }

    // Map the SDADocuments to PageRank scores
    private HashMap<Long, Double> buildPageRankMap(Set<Long> vertexSet, Matrix pageRankMatrix) {
        logger.info("Mapping documents to page rank scores");
        HashMap<Long, Double> pageRankMap = new HashMap<>(); // Create a new map
        List<Long> vertexList = new ArrayList<>(vertexSet); // Create a list of the vertices
        Collections.sort(vertexList); // Sort the list of vertices so that index 1 corresponds to the first SDADocument and so on... 

        // Loop over the list of vertices and insert a new entry into the map with SDADocument ID and its corresponding PageRank score
        for(int i = 0; i < vertexList.size(); i++) {
            pageRankMap.put(vertexList.get(i), pageRankMatrix.get(0, i));
        }

        return pageRankMap; // return the map of SDADocuments to PageRank scores
    }
}

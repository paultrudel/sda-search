package com.search.sdasearch.indexer;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SDAPageRank {

    private static final Logger logger = LoggerFactory.getLogger(SDAPageRank.class);

    public HashMap<Long, Double> computePageRanks(Graph<Long, DefaultEdge> graph, double alpha) {
        logger.info("========= Beginning to computer page rank values ========");
        int matrixSize = graph.vertexSet().size();
        double [][] adjMatrix = buildAdjacencyMatrix(graph, matrixSize);
        double [][] transitionMatrix = computeTransitionMatrix(adjMatrix, alpha, matrixSize);
        Matrix pageRankMatrix = computePageRankMatrix(transitionMatrix);
        return buildPageRankMap(graph.vertexSet(), pageRankMatrix);
    }

    private double[][] buildAdjacencyMatrix(Graph<Long, DefaultEdge> graph, int matrixSize) {
        logger.info("Building adjacency matrix of size {} from crawl graph", matrixSize);
        double[][] adjMatrix = new double[matrixSize][matrixSize];
        List<Long> vertexList = new ArrayList<>(graph.vertexSet());
        Collections.sort(vertexList);
        for(DefaultEdge edge: graph.edgeSet()) {
            Long source = graph.getEdgeSource(edge);
            Long target = graph.getEdgeTarget(edge);
            adjMatrix[vertexList.indexOf(source)][vertexList.indexOf(target)] = 1;
        }
        return adjMatrix;
    }

    private double[][] computeTransitionMatrix(double[][] adjMatrix, double alpha, int matrixSize) {
        logger.info("Computing transition probability matrix from adjacency matrix");
        double[][] transitionMatrix = new double[matrixSize][matrixSize];
        double[] rowSums = computeRowSums(adjMatrix, matrixSize);

        for(int i = 0; i < matrixSize; i++) {
            double rowSum = rowSums[i];
            double[] pageRankMatrixRow = new double[matrixSize];

            for(int j = 0; j < matrixSize; j++) {
                double pageRankMatrixEntry;
                if(rowSum == 0) {
                    pageRankMatrixEntry = 1 / matrixSize;
                } else {
                    if(adjMatrix[i][j] == 1) {
                        pageRankMatrixEntry = 1 / rowSum;
                    }
                    else {
                        pageRankMatrixEntry = 0;
                    }
                }
                pageRankMatrixEntry = pageRankMatrixEntry * (1 - alpha);
                pageRankMatrixEntry += alpha / matrixSize;
                pageRankMatrixRow[j] = pageRankMatrixEntry;
            }

            transitionMatrix[i] = pageRankMatrixRow;
        }

        return transitionMatrix;
    }

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

    private Matrix computePageRankMatrix(double[][] transitionMatrix) {
        logger.info("Computing the stationary distribution of the transition matrix");
        Matrix pageRankMatrix = new Matrix(transitionMatrix);
        Matrix prevMatrix;
        do {
            prevMatrix = pageRankMatrix;
            pageRankMatrix = pageRankMatrix.times(pageRankMatrix);
        } while(!shouldTerminate(prevMatrix, pageRankMatrix));
        return pageRankMatrix;
    }

    private boolean shouldTerminate(Matrix prevMatrix, Matrix nextMatrix) {
        double maxDifference = 0.01;

        for(int row = 0; row < prevMatrix.getRowDimension(); row++) {
            for(int col = 0; col < prevMatrix.getColumnDimension(); col++) {
                double difference = Math.abs(
                        prevMatrix.get(row, col) - nextMatrix.get(row, col)
                );
                if(difference > maxDifference) {
                    return false;
                }
            }
        }

        return true;
    }

    private HashMap<Long, Double> buildPageRankMap(Set<Long> vertexSet, Matrix pageRankMatrix) {
        logger.info("Mapping documents to page rank scores");
        HashMap<Long, Double> pageRankMap = new HashMap<>();
        List<Long> vertexList = new ArrayList<>(vertexSet);
        Collections.sort(vertexList);

        for(int i = 0; i < vertexList.size(); i++) {
            pageRankMap.put(vertexList.get(i), pageRankMatrix.get(0, i));
        }

        return pageRankMap;
    }
}

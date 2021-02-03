package com.search.sdasearch.service;

import com.search.sdasearch.entity.SDADocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Interface defining the methods    */
/*  available from the document       */
/*  service                           */
/*                                    */
/**************************************/

public interface SDADocumentService {

    public Page<SDADocument> searchDocuments(String query, Pageable pageable); // Query the document index with user provided string
}

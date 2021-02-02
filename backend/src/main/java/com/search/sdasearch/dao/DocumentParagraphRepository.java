package com.search.sdasearch.dao;

import com.search.sdasearch.entity.DocumentParagraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  JPA repository for the            */
/*  DocumentParagraph entity          */
/*                                    */
/**************************************/

@CrossOrigin("http://localhost:4200")
public interface DocumentParagraphRepository extends JpaRepository<DocumentParagraph, Long> {

    // Return page of DocumentParagraphs corresponding to the given document ID
    Page<DocumentParagraph> findByDocumentId(@RequestParam("id") Long id, Pageable pageable);
}

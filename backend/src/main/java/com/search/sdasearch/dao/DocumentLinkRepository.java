package com.search.sdasearch.dao;

import com.search.sdasearch.entity.DocumentLink;
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
/*  DocumentLink entity               */
/*                                    */
/**************************************/

@CrossOrigin("http://localhost:4200")
public interface DocumentLinkRepository extends JpaRepository<DocumentLink, Long> {

    // Return page of DocumentLinks corresponding to the given document ID
    Page<DocumentLink> findByDocumentId(@RequestParam("id") Long id, Pageable pageable);
}

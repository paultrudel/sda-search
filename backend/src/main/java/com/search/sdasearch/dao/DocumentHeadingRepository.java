package com.search.sdasearch.dao;

import com.search.sdasearch.entity.DocumentHeading;
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
/*  Spring Data JPA repository for    */
/*  the DocumentHeading entity        */
/*                                    */
/**************************************/

@CrossOrigin("http://localhost:4200")
public interface DocumentHeadingRepository extends JpaRepository<DocumentHeading, Long> {

    // Return page of DocumentHeadings corresponding to the given document ID
    Page<DocumentHeading> findByDocumentId(@RequestParam("id") Long id, Pageable pageable);
}

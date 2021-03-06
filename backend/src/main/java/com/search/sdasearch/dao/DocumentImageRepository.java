package com.search.sdasearch.dao;

import com.search.sdasearch.entity.DocumentImage;
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
/*  the DocumentImage entity          */
/*                                    */
/**************************************/

@CrossOrigin("http://localhost:4200")
public interface DocumentImageRepository extends JpaRepository<DocumentImage, Long> {

    // Return page of DocumentImages corresponding to the given document ID
    Page<DocumentImage> findByDocumentId(@RequestParam("id") Long id, Pageable pageable);
}

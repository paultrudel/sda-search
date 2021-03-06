package com.search.sdasearch.dao;

import com.search.sdasearch.entity.DocumentMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Spring Data JPA repository for    */
/*  the DocumentMetadata entity       */
/*                                    */
/**************************************/

@CrossOrigin("http://localhost:4200")
@RepositoryRestResource(
        collectionResourceRel = "documentMetadata",
        path = "document-metadata"
)
public interface DocumentMetadataRepository extends JpaRepository<DocumentMetadata, Long> {

    // Return page of DocumentMetadata corresponding to the given document ID
    Page<DocumentMetadata> findByDocumentId(@RequestParam("id") Long id, Pageable pageable);
}

package com.search.sdasearch.dao;

import com.search.sdasearch.entity.SDADocument;
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
/*  the SDADocument entity            */
/*                                    */
/**************************************/

@CrossOrigin("http://localhost:4200")
@RepositoryRestResource(collectionResourceRel = "documents", path = "documents")
public interface SDADocumentRepository
        extends JpaRepository<SDADocument, Long> {
}

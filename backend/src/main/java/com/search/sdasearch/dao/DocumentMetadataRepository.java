package com.search.sdasearch.dao;

import com.search.sdasearch.entity.DocumentMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin("http://localhost:4200")
@RepositoryRestResource(
        collectionResourceRel = "documentMetadata",
        path = "document-metadata"
)
public interface DocumentMetadataRepository extends JpaRepository<DocumentMetadata, Long> {

    Page<DocumentMetadata> findByDocumentId(@RequestParam("id") Long id, Pageable pageable);
}

package com.search.sdasearch.service;

import com.search.sdasearch.entity.SDADocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SDADocumentService {

    public Page<SDADocument> searchDocuments(String query, Pageable pageable);
}

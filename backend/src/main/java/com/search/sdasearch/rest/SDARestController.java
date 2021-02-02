package com.search.sdasearch.rest;

import com.search.sdasearch.entity.SDADocument;
import com.search.sdasearch.service.SDADocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:4200")
public class SDARestController {

    private SDADocumentService documentService;

    @Autowired
    public SDARestController(
            SDADocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/search")
    public Page<SDADocument> searchDocuments(@RequestParam("query") String query,
                                             @RequestParam("page") int page,
                                             @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentService.searchDocuments(query, pageable);
    }
}

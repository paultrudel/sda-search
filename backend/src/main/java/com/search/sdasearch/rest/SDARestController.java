package com.search.sdasearch.rest;

import com.search.sdasearch.entity.SDADocument;
import com.search.sdasearch.service.SDADocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Application controller to expose  */
/*  additional REST enpoints and      */
/*  handle requests                   */
/*                                    */
/**************************************/

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:4200")
public class SDARestController {

    private SDADocumentService documentService;

    // Inject the document service using constructor injection
    @Autowired
    public SDARestController(
            SDADocumentService documentService) {
        this.documentService = documentService;
    }

    // Expose endpoint at /api/search which accepts GET requests and returns a Page of SDADocuments
    @GetMapping("/search")
    public Page<SDADocument> searchDocuments(@RequestParam("query") String query,
                                             @RequestParam("page") int page,
                                             @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page, size); // Create new pageable with using the request parameters
        return documentService.searchDocuments(query, pageable); // Use the document service to fetch documents using the user provided query
    }
}

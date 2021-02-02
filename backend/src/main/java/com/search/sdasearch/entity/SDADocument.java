package com.search.sdasearch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Class which defines the           */
/*  SDADocument entity                */
/*                                    */
/**************************************/

@Entity  // Annotate class as JPA entity
@Table(name = "sda_document") // Annotate table corresponding to entity
//Lombok annotations to automatically generate boilerplate code
@Getter
@Setter
public class SDADocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Entity primary key

    @Column(name = "url")
    private String url;

    @Column(name = "title")
    private String title;

    @Column(name = "document_summary")
    private String documentSummary;

    @Column(name = "page_rank")
    private float pageRank;

    @Column(name = "parent_id")
    private Long parentId;

    @Transient
    private float queryScore;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "document")
    @JsonIgnore
    private Set<DocumentHeading> headings; // Define One-to-Many relationship between SDADocument and DocumentHeading entities

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "document")
    @JsonIgnore
    private Set<DocumentParagraph> paragraphs; // Define One-to-Many relationship between SDADocument and DocumentParagraph entities

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "document")
    @JsonIgnore
    private Set<DocumentLink> links; // Define One-to-Many relationship between SDADocument and DocumentLink entities

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "document")
    @JsonIgnore
    private Set<DocumentMetadata> metadata; // Define One-to-Many relationship between SDADocument and DocumentMetadata entities

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "document")
    @JsonIgnore
    private Set<DocumentImage> images; // Define One-to-Many relationship between SDADocument and DocumentImage entities

    // Add a DocumentHeading to SDADocument
    public void addHeading(DocumentHeading documentHeading) {
        if(headings == null) {
            headings = new HashSet<>();
        }
        headings.add(documentHeading);
        documentHeading.setDocument(this);
    }

    // Add a DocumentImage to SDADocument
    public void addImage(DocumentImage documentImage) {
        if(images == null) {
            images = new HashSet<>();
        }
        images.add(documentImage);
        documentImage.setDocument(this);
    }

    // Add a DocumentLink to SDADocument
    public void addLink(DocumentLink documentLink) {
        if(links == null) {
            links = new HashSet<>();
        }
        links.add(documentLink);
        documentLink.setDocument(this);
    }

    // Add a DocumentMetadata to SDADocument
    public void addMetadata(DocumentMetadata documentMetadata) {
        if(metadata == null) {
            metadata = new HashSet<>();
        }
        metadata.add(documentMetadata);
        documentMetadata.setDocument(this);
    }

    // Add a DocumentParagraph to SDADocument
    public void addParagraph(DocumentParagraph documentParagraph) {
        if(paragraphs == null) {
            paragraphs = new HashSet<>();
        }
        paragraphs.add(documentParagraph);
        documentParagraph.setDocument(this);
    }
}

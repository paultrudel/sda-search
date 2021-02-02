package com.search.sdasearch.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sda_document")
@Getter
@Setter
public class SDADocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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
    private Set<DocumentHeading> headings;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "document")
    @JsonIgnore
    private Set<DocumentParagraph> paragraphs;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "document")
    @JsonIgnore
    private Set<DocumentLink> links;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "document")
    @JsonIgnore
    private Set<DocumentMetadata> metadata;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "document")
    @JsonIgnore
    private Set<DocumentImage> images;

    public void addHeading(DocumentHeading documentHeading) {
        if(headings == null) {
            headings = new HashSet<>();
        }
        headings.add(documentHeading);
        documentHeading.setDocument(this);
    }

    public void addImage(DocumentImage documentImage) {
        if(images == null) {
            images = new HashSet<>();
        }
        images.add(documentImage);
        documentImage.setDocument(this);
    }

    public void addLink(DocumentLink documentLink) {
        if(links == null) {
            links = new HashSet<>();
        }
        links.add(documentLink);
        documentLink.setDocument(this);
    }

    public void addMetadata(DocumentMetadata documentMetadata) {
        if(metadata == null) {
            metadata = new HashSet<>();
        }
        metadata.add(documentMetadata);
        documentMetadata.setDocument(this);
    }

    public void addParagraph(DocumentParagraph documentParagraph) {
        if(paragraphs == null) {
            paragraphs = new HashSet<>();
        }
        paragraphs.add(documentParagraph);
        documentParagraph.setDocument(this);
    }
}

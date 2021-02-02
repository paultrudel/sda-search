package com.search.sdasearch.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "document_image")
@Data
public class DocumentImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "link")
    private String link;

    @Column(name = "alt_text")
    private String altText;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private SDADocument document;
}

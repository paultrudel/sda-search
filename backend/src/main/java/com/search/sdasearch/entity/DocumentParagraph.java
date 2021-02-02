package com.search.sdasearch.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "document_paragraph")
@Data
public class DocumentParagraph {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private SDADocument document;
}

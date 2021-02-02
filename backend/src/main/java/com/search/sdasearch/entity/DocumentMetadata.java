package com.search.sdasearch.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "document_metadata")
@Data
public class DocumentMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private SDADocument document;
}

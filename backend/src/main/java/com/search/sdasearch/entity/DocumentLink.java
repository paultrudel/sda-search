package com.search.sdasearch.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "document_link")
@Data
public class DocumentLink {

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

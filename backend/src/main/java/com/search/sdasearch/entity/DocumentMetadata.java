package com.search.sdasearch.entity;

import lombok.Data;

import javax.persistence.*;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Class which defines the           */
/*  DocumentMetadata entity           */
/*                                    */
/**************************************/

@Entity // Annotate class as JPA entity
@Table(name = "document_metadata") // Annoate table corresponding to entity
@Data // Lombok annotation to automatically generate boilerplate code
public class DocumentMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Entity primary key

    @Column(name = "name")
    private String name;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private SDADocument document; // Define Many-to-One relationship between DocumentMetadata and SDADocument entities
}

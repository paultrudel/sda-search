package com.search.sdasearch.entity;

import lombok.Data;

import javax.persistence.*;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Class which defines the           */
/*  DocumentHeading entity            */
/*                                    */
/**************************************/

@Entity // Annotate class as JPA entity
@Table(name = "document_heading") // Annotate the table corresponding to the entity
@Data // Lombok annotation to automatically generate boilerplate code
public class DocumentHeading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Entity primary key

    @Column(name = "text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private SDADocument document; // Define Many-to-One relationship between DocumentHeading and SDADocument entities

}

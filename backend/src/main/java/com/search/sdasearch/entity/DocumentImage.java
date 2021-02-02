package com.search.sdasearch.entity;

import lombok.Data;

import javax.persistence.*;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Class defining the DocumentImage  */
/*  entity                            */
/*                                    */
/**************************************/

@Entity // Annotate class as JPA entity
@Table(name = "document_image") // Annotate the table corresponding to the entity
@Data // Lombok annotation to automatically generate boilerplate code
public class DocumentImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Entity primary key

    @Column(name = "link")
    private String link;

    @Column(name = "alt_text")
    private String altText;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private SDADocument document; // Define Many-to-One relationship between DocumentImage and SDADocument entities 
}

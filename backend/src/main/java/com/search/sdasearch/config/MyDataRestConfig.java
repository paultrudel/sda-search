package com.search.sdasearch.config;

import com.search.sdasearch.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**************************************/
/*                                    */
/*  Program: SDA-Search               */
/*  Author: Paul Trudel               */
/*                                    */
/*  Class responsible for handling    */
/*  the security configuration for    */
/*  incoming requests to the server   */
/*                                    */
/**************************************/

public class MyDataRestConfig implements RepositoryRestConfigurer {

    private EntityManager entityManager;

    // Inject the EntityManager using constructor injection
    @Autowired
    public MyDataRestConfig(EntityManager theEntityManager) {
        entityManager = theEntityManager;
    }

    // Restrict the actions that can be performed on the REST endpoints exposed by Spring Data JPA
    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config,
            CorsRegistry cors) {
        HttpMethod[] unsupportedActions = {HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE};

        disableHttpMethods(SDADocument.class, config, unsupportedActions);
        disableHttpMethods(DocumentHeading.class, config, unsupportedActions);
        disableHttpMethods(DocumentImage.class, config, unsupportedActions);
        disableHttpMethods(DocumentLink.class, config, unsupportedActions);
        disableHttpMethods(DocumentMetadata.class, config, unsupportedActions);
        disableHttpMethods(DocumentParagraph.class, config, unsupportedActions);

        exposeIds(config);
    }

    // Disable the specified HTTP actions for the given JPA entity
    private void disableHttpMethods(
            Class theClass,
            RepositoryRestConfiguration config,
            HttpMethod[] unsupportedActions) {
        config.getExposureConfiguration()
                .forDomainType(theClass)
                .withItemExposure((metadata, httpMethods) -> httpMethods.disable(unsupportedActions))
                .withCollectionExposure((metadata, httpMethods) -> httpMethods.disable(unsupportedActions));
    }

    // Expose the entity IDs as a normal property 
    private void exposeIds(RepositoryRestConfiguration config) {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        List<Class> entityClasses = new ArrayList<>();

        for(EntityType entityType: entities) {
            entityClasses.add(entityType.getJavaType());
        }

        Class[] domainTypes = entityClasses.toArray(new Class[0]);
        config.exposeIdsFor(domainTypes);
    }
}

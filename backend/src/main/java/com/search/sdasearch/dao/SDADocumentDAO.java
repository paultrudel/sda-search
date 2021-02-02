package com.search.sdasearch.dao;

import com.search.sdasearch.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;

public class SDADocumentDAO {

    private SessionFactory sessionFactory;
    private Logger logger = LoggerFactory.getLogger(SDADocumentDAO.class);
    private static SDADocumentDAO instance;

    private SDADocumentDAO() {
        openConnection();
    }

    public static SDADocumentDAO getInstance() {
        if(instance == null) {
            instance = new SDADocumentDAO();
        }
        return instance;
    }

    private void openConnection() {
        logger.info("Opening connection to database");
        sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(SDADocument.class)
                .addAnnotatedClass(DocumentHeading.class)
                .addAnnotatedClass(DocumentImage.class)
                .addAnnotatedClass(DocumentLink.class)
                .addAnnotatedClass(DocumentMetadata.class)
                .addAnnotatedClass(DocumentParagraph.class)
                .buildSessionFactory();
    }

    public Long save(SDADocument sdaDocument) {
        logger.info("Saving document {}", sdaDocument.getUrl());
        Long docId = null;
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            docId = (Long) session.save(sdaDocument);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                e.printStackTrace();
            }
        } finally {
            session.close();
        }
        return docId;
    }

    public SDADocument find(Long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        SDADocument result = null;

        try {
            transaction = session.beginTransaction();
            result = session.find(SDADocument.class, id);
            transaction.commit();
        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
                e.printStackTrace();
            }
        } finally {
            session.close();
        }
        return result;
    }

    public SDADocument findDocumentByUrl(String url) {
        logger.info("Searching for document with URL {}", url);
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        SDADocument result = null;

        try {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<SDADocument> criteriaQuery = criteriaBuilder.createQuery(SDADocument.class);
            Root<SDADocument> sdaDocumentRoot = criteriaQuery.from(SDADocument.class);
            criteriaQuery.where(criteriaBuilder.equal(sdaDocumentRoot.get("url"), url));
            result = session.createQuery(criteriaQuery).uniqueResult();
            transaction.commit();
        } catch(Exception e) {
            if (transaction != null) {
                transaction.rollback();
                e.printStackTrace();
            }
        } finally {
            session.close();
        }
        return result;
    }

    public List<SDADocument> findAllDocuments() {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        List<SDADocument> result = null;

        try {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<SDADocument> criteriaQuery = criteriaBuilder.createQuery(
                    SDADocument.class
            );
            Root<SDADocument> sdaDocumentRoot = criteriaQuery.from(SDADocument.class);
            CriteriaQuery<SDADocument> allDocuments = criteriaQuery.select(sdaDocumentRoot);
            result = session.createQuery(allDocuments).getResultList();
        } catch(Exception e) {
            if(transaction != null) {
                transaction.rollback();
                e.printStackTrace();
            }
        } finally {
            session.close();
        }
        return result;
    }

    public <T> List<T> getDocumentRelation(SDADocument document, Class<T> relationClass) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        List<T> result = null;

        try {
            transaction = session.beginTransaction();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(relationClass);
            Root<T> documentHeadingRoot = criteriaQuery.from(relationClass);
            criteriaQuery.where(
                    criteriaBuilder.equal(
                            documentHeadingRoot.get("document"), document
                    )
            );
            result = session.createQuery(criteriaQuery).getResultList();
        } catch(Exception e) {
            if(transaction != null) {
                transaction.rollback();
                e.printStackTrace();
            }
        } finally {
            session.close();
        }
        return result;
    }
}

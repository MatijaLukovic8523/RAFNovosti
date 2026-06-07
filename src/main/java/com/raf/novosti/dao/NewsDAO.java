package com.raf.novosti.dao;

import com.raf.novosti.model.News;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;

public class NewsDAO extends AbstractDAO<News, Long> {
    @Inject
    public NewsDAO(EntityManagerFactory emf) {
        super(emf, News.class);
    }
}
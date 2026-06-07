package com.raf.novosti.dao;

import com.raf.novosti.model.Category;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;

public class CategoryDAO extends AbstractDAO<Category, Long> {
    @Inject
    public CategoryDAO(EntityManagerFactory emf) {
        super(emf, Category.class);
    }
}
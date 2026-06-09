package com.raf.novosti.dao;

import com.raf.novosti.model.Category;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class CategoryDAO extends AbstractDAO<Category, Long> {
    @Inject
    public CategoryDAO(EntityManagerFactory emf) {
        super(emf, Category.class);
    }

    @Override
    public void delete(Category category) {
        EntityManager em = emf.createEntityManager();
        try {
            Category managedCategory = em.find(Category.class, category.getId());
            if (managedCategory != null && managedCategory.getNews() != null && !managedCategory.getNews().isEmpty()) {
                throw new IllegalStateException("Nije dozvoljeno brisanje kategorije koja sadrži vesti.");
            }
            super.delete(category);
        } finally {
            em.close();
        }
    }
}
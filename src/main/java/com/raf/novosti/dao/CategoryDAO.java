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
            // 1. Provera da li kategorija ima vesti
            // Napomena: Moraš da učitaš svežu instancu iz baze kako bi 'news' lista bila tačna
            Category managedCategory = em.find(Category.class, category.getId());

            if (managedCategory != null && !managedCategory.getNews().isEmpty()) {
                throw new IllegalStateException("Nije dozvoljeno brisanje kategorije koja sadrži vesti.");
            }

            // 2. Ako provera prođe, pozovi originalni delete iz AbstractDAO
            super.delete(category);

        } finally {
            em.close();
        }
    }
}
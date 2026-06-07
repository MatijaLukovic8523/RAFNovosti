package com.raf.novosti.dao;

import com.raf.novosti.model.News;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class NewsDAO extends AbstractDAO<News, Long> {
    @Inject
    public NewsDAO(EntityManagerFactory emf) {
        super(emf, News.class);
    }

    public List<News> searchByText(String text, int page, int limit) {
        EntityManager em = emf.createEntityManager();
        try {
            // Dodajemo % oko teksta za LIKE pretragu
            String queryParam = "%" + text + "%";

            return em.createQuery("SELECT n FROM News n WHERE n.title LIKE :query OR n.text LIKE :query ORDER BY n.publishedAt DESC", News.class)
                    .setParameter("query", queryParam)
                    .setFirstResult((page - 1) * limit)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<News> findMostReadLast30Days() {
        EntityManager em = emf.createEntityManager();
        try {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            return em.createQuery(
                            "SELECT n FROM News n WHERE n.publishedAt >= :date ORDER BY n.visits DESC", News.class)
                    .setParameter("date", thirtyDaysAgo)
                    .setMaxResults(10)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 2. Najviše reakcija (zbir like i dislike)
    // Napomena: Ovo zahteva da Reaction entitet ima polje 'targetId' i 'targetType'
    public List<News> findMostReacted() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT n FROM News n JOIN Reaction r ON n.id = r.targetId " +
                                    "WHERE r.targetType = 'NEWS' " +
                                    "GROUP BY n.id ORDER BY count(r) DESC", News.class)
                    .setMaxResults(3)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 3. Povezane vesti (na osnovu zajedničkih tagova)
    public List<News> findRelatedNews(Long newsId, Set<Long> tagIds) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT n FROM News n JOIN n.tags t " +
                                    "WHERE t.id IN :tagIds AND n.id != :newsId " +
                                    "ORDER BY n.publishedAt DESC", News.class)
                    .setParameter("tagIds", tagIds)
                    .setParameter("newsId", newsId)
                    .setMaxResults(3)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
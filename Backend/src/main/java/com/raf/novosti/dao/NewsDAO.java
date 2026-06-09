package com.raf.novosti.dao;

import com.raf.novosti.model.News;
import com.raf.novosti.model.NewsView;
import com.raf.novosti.model.Tag;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

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

    public List<News> findByCategoryId(Long categoryId, int page, int limit) {
        EntityManager em = emf.createEntityManager();
        try {
            int offset = (page - 1) * limit;
            return em.createQuery("SELECT n FROM News n WHERE n.category.id = :categoryId ORDER BY n.publishedAt DESC", News.class)
                    .setParameter("categoryId", categoryId)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<News> findByTagId(Long tagId, int page, int limit) {
        EntityManager em = emf.createEntityManager();
        try {
            int offset = (page - 1) * limit;
            return em.createQuery("SELECT DISTINCT n FROM News n JOIN n.tags t WHERE t.id = :tagId ORDER BY n.publishedAt DESC", News.class)
                    .setParameter("tagId", tagId)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<News> findAllSorted(String sortBy, int page, int limit) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT n FROM News n " +
                    "LEFT JOIN FETCH n.comments " +
                    "LEFT JOIN FETCH n.tags ";

            if ("visits".equalsIgnoreCase(sortBy)) {
                jpql += " ORDER BY n.visits DESC";
            } else {
                jpql += " ORDER BY n.publishedAt DESC";
            }

            return em.createQuery(jpql, News.class)
                    .setFirstResult((page - 1) * limit)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public News findByIdWithTags(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            News news = em.find(News.class, id);
            if (news == null) return null;

            List<Tag> tags = em.createNativeQuery(
                            "SELECT t.* FROM tags t " +
                                    "JOIN news_tags nt ON t.id = nt.tag_id " +
                                    "WHERE nt.news_id = :newsId", Tag.class)
                    .setParameter("newsId", id)
                    .getResultList();

            news.setTags(new java.util.HashSet<>(tags));

            System.out.println("DEBUG DAO: Ručno učitano tagova: " + news.getTags().size());

            return news;
        } finally {
            em.close();
        }
    }

    public boolean hasVisited(String sessionId, Long newsId) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(v) FROM NewsView v WHERE v.sessionId = :sId AND v.news.id = :nId", Long.class)
                    .setParameter("sId", sessionId)
                    .setParameter("nId", newsId)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public void recordVisit(String sessionId, Long newsId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            News news = em.find(News.class, newsId);

            if (news != null) {

                NewsView view = new NewsView(null, news, sessionId);
                em.persist(view);

                news.setVisits(news.getVisits() + 1);
                em.merge(news);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<News> findTop3MostInteracted() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT n FROM News n JOIN Reaction r ON n.id = r.targetId " +
                                    "WHERE r.targetType = 'NEWS' " +
                                    "GROUP BY n.id " +
                                    "ORDER BY count(r) DESC", News.class)
                    .setMaxResults(3)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<News> findByAuthorId(Long authorId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT n FROM News n WHERE n.author.id = :authorId ORDER BY n.publishedAt DESC", News.class)
                    .setParameter("authorId", authorId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
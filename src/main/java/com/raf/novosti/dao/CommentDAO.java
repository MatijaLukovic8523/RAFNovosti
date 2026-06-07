package com.raf.novosti.dao;

import com.raf.novosti.model.Comment;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class CommentDAO extends AbstractDAO<Comment, Long> {
    @Inject
    public CommentDAO(EntityManagerFactory emf) {
        super(emf, Comment.class);
    }

    public List<Comment> findByNewsId(Long newsId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Comment c WHERE c.news.id = :newsId", Comment.class)
                    .setParameter("newsId", newsId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
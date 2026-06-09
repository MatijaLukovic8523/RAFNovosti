package com.raf.novosti.dao;

import com.raf.novosti.model.Reaction;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class ReactionDAO extends AbstractDAO<Reaction, Long> {

    @Inject
    public ReactionDAO(EntityManagerFactory emf) {
        super(emf, Reaction.class);
    }

    public Reaction findBySessionAndTarget(String sessionId, Long targetId, String targetType) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Reaction r WHERE r.sessionId = :sId AND r.targetId = :tId AND r.targetType = :tType", Reaction.class)
                    .setParameter("sId", sessionId)
                    .setParameter("tId", targetId)
                    .setParameter("tType", targetType)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    public Long countReactions(Long targetId, String type, String targetType) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(r) FROM Reaction r WHERE r.targetId = :tId AND r.reactionType = :rType AND r.targetType = :tType", Long.class)
                    .setParameter("tId", targetId)
                    .setParameter("rType", type)
                    .setParameter("tType", targetType)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}
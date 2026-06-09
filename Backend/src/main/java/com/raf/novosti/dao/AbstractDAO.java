package com.raf.novosti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDAO<T, ID> implements DAO<T, ID> {
    protected final EntityManagerFactory emf;
    private final Class<T> entityClass;

    public AbstractDAO(EntityManagerFactory emf, Class<T> entityClass) {
        this.emf = emf;
        this.entityClass = entityClass;
    }

    @Override
    public void save(T entity) {
        executeInTransaction(em -> em.persist(entity));
    }

    @Override
    public void update(T entity) {
        executeInTransaction(em -> em.merge(entity));
    }

    @Override
    public void delete(T entity) {
        executeInTransaction(em -> em.remove(em.contains(entity) ? entity : em.merge(entity)));
    }

    @Override
    public Optional<T> findById(ID id) {
        EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(entityClass, id));
        } finally { em.close(); }
    }

    @Override
    public List<T> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                    .getResultList();
        } finally { em.close(); }
    }

    public List<T> findAll(int page, int limit) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e ORDER BY e.id DESC", entityClass)
                    .setFirstResult((page - 1) * limit)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    protected void executeInTransaction(java.util.function.Consumer<EntityManager> action) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            action.accept(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally { em.close(); }
    }
}
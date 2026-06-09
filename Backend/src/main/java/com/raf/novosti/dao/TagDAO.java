package com.raf.novosti.dao;

import com.raf.novosti.model.Tag;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;

public class TagDAO extends AbstractDAO<Tag, Long> {
    @Inject
    public TagDAO(EntityManagerFactory emf) {
        super(emf, Tag.class);
    }
}

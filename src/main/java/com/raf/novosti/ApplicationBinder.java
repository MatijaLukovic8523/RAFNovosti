package com.raf.novosti;

import com.raf.novosti.controller.*;
import com.raf.novosti.dao.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import jakarta.inject.Singleton;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(Persistence.createEntityManagerFactory("raf_novosti_pu"))
                .to(EntityManagerFactory.class);

        bind(UserDAO.class).to(UserDAO.class).in(Singleton.class);
        bind(NewsDAO.class).to(NewsDAO.class).in(Singleton.class);
        bind(CategoryDAO.class).to(CategoryDAO.class).in(Singleton.class);
        bind(TagDAO.class).to(TagDAO.class).in(Singleton.class);
        bind(CommentDAO.class).to(CommentDAO.class).in(Singleton.class);

        bind(NewsResource.class).to(NewsResource.class);
        bind(UserResource.class).to(UserResource.class);
        bind(CategoryResource.class).to(CategoryResource.class);
        bind(AuthResource.class).to(AuthResource.class);
        bind(TagResource.class).to(TagResource.class);

    }
}
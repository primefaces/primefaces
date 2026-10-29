/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.showcase.service;

import org.primefaces.showcase.domain.Customer;
import org.primefaces.showcase.domain.CustomerEntity;

import java.util.LinkedHashSet;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * The database behind the DataTable <em>Lazy JPA</em> example: an application managed
 * {@link EntityManagerFactory} over an in-memory database, exposed as a request scoped
 * {@link EntityManager}, and filled once with the very customers {@link CustomerService} makes up for the
 * other examples.
 * <p>
 * A real application would inject a container managed {@code @PersistenceContext} instead; the persistence
 * unit is bootstrapped by hand here only so the example runs on a plain servlet container as well.
 */
@ApplicationScoped
public class CustomerEntityService {

    @Inject
    private CustomerService customerService;

    private EntityManagerFactory entityManagerFactory;

    @PostConstruct
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("showcase");

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            List<Customer> customers = customerService.getCustomers(200);
            for (int i = 0; i < customers.size(); i++) {
                entityManager.persist(toEntity((long) i + 1000, customers.get(i)));
            }
            entityManager.getTransaction().commit();
        }
        finally {
            entityManager.close();
        }
    }

    @PreDestroy
    public void destroy() {
        entityManagerFactory.close();
    }

    @Produces
    @RequestScoped
    public EntityManager produceEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public void closeEntityManager(@Disposes EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

    private static CustomerEntity toEntity(Long id, Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(id);
        entity.setName(customer.getName());
        entity.setCompany(customer.getCompany());
        entity.setCountryName(customer.getCountry().getName());
        entity.setCountryCode(customer.getCountry().getCode());
        entity.setRepresentativeName(customer.getRepresentative().getName());
        entity.setRepresentativeImage(customer.getRepresentative().getImage());
        entity.setJoinDate(customer.getDate());
        entity.setStatus(customer.getStatus());
        entity.setActivity(customer.getActivity());
        entity.setVip(customer.getVip());
        entity.setTags(customer.getTags() == null ? new LinkedHashSet<>() : new LinkedHashSet<>(customer.getTags()));
        entity.setCheckInTime(customer.getCheckInTime());
        entity.setLastContact(customer.getLastContact());
        return entity;
    }
}

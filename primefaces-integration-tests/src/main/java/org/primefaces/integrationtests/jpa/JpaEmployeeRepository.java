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
package org.primefaces.integrationtests.jpa;

import org.primefaces.integrationtests.jpa.JpaEmployee.Department;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedHashSet;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * The application managed {@link EntityManagerFactory} of the JPALazyDataModel test page, exposed as a
 * request scoped {@link EntityManager} - embedded Tomcat has no container managed JPA, so the persistence
 * unit is bootstrapped by hand.
 */
@ApplicationScoped
public class JpaEmployeeRepository {

    private EntityManagerFactory entityManagerFactory;

    @PostConstruct
    public void init() {
        entityManagerFactory = Persistence.createEntityManagerFactory("integration-tests");
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

    /**
     * Recreates the fixture from scratch:
     * <pre>
     * id name           salary active reviewDate    lastLogin  startTime  department  tags
     *  1 "Mike Master"    5000 true   today         now-10min  now-10min  ENGINEERING java, sql
     *  2 "Susan Pepper"   3000 false  yesterday     now-1h     now-1h     MARKETING   java
     *  3 null             null null   null          null       null       null        -
     *  4 "   " (blank)    7000 true   tomorrow      now+10min  now+10min  SALES       sql, xml
     *  5 "Chris Clark"    9000 false  today-1week   now-2d     null       ENGINEERING python
     *  6 "Trish Mayer"    4000 true   today+1week   now+2d     null       MARKETING   -
     *  7 "James Bush"     2000 false  today-1year   now-400d   null       SALES       -
     *  8 "Mary March"     6000 true   today+1month  now+400d   null       ENGINEERING java, xml
     *  9 "Nina Night"     1000 false  today+1year   null       null       MARKETING   -
     * </pre>
     * The dates and times are relative to "now", as the relative date/time match modes are resolved against
     * the clock at query time - and they are rewritten on every view of the page rather than once at startup,
     * so that a "last 15 minutes" filter still selects the same rows however long the application has been up.
     *
     * @param entityManager the entity manager to write the fixture with
     */
    public void reseed(EntityManager entityManager) {
        entityManager.getTransaction().begin();

        // one by one instead of a bulk delete, which would leave the rows of the @ElementCollection behind
        entityManager.createQuery("select e from JpaEmployee e", JpaEmployee.class)
                .getResultList()
                .forEach(entityManager::remove);
        entityManager.flush();

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now().withNano(0);
        LocalTime nowTime = LocalTime.now().withNano(0);

        entityManager.persist(employee(1L, "Mike Master", 5000, true, today, now.minusMinutes(10), nowTime.minusMinutes(10),
                Department.ENGINEERING, "java", "sql"));
        entityManager.persist(employee(2L, "Susan Pepper", 3000, false, today.minusDays(1), now.minusHours(1), nowTime.minusHours(1),
                Department.MARKETING, "java"));
        entityManager.persist(employee(3L, null, null, null, null, null, null,
                null));
        entityManager.persist(employee(4L, "   ", 7000, true, today.plusDays(1), now.plusMinutes(10), nowTime.plusMinutes(10),
                Department.SALES, "sql", "xml"));
        entityManager.persist(employee(5L, "Chris Clark", 9000, false, today.minusWeeks(1), now.minusDays(2), null,
                Department.ENGINEERING, "python"));
        entityManager.persist(employee(6L, "Trish Mayer", 4000, true, today.plusWeeks(1), now.plusDays(2), null,
                Department.MARKETING));
        entityManager.persist(employee(7L, "James Bush", 2000, false, today.minusYears(1), now.minusDays(400), null,
                Department.SALES));
        entityManager.persist(employee(8L, "Mary March", 6000, true, today.plusMonths(1), now.plusDays(400), null,
                Department.ENGINEERING, "java", "xml"));
        entityManager.persist(employee(9L, "Nina Night", 1000, false, today.plusYears(1), null, null,
                Department.MARKETING));

        entityManager.getTransaction().commit();
    }

    private static JpaEmployee employee(Long id, String name, Integer salary, Boolean active, LocalDate reviewDate,
            LocalDateTime lastLogin, LocalTime startTime, Department department, String... tags) {

        JpaEmployee employee = new JpaEmployee();
        employee.setId(id);
        employee.setName(name);
        employee.setSalary(salary);
        employee.setActive(active);
        employee.setReviewDate(reviewDate);
        employee.setLastLogin(lastLogin);
        employee.setStartTime(startTime);
        employee.setDepartment(department);
        employee.setTags(new LinkedHashSet<>(Arrays.asList(tags)));
        return employee;
    }
}

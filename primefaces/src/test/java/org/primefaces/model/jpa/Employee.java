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
package org.primefaces.model.jpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Entity fixture of {@code JPALazyDataModelTest}, with one field per {@code filterValueType} preset so that
 * every match mode can be filtered on the type it is meant for.
 */
@Entity
public class Employee {

    public enum Department {
        ENGINEERING,
        MARKETING,
        SALES
    }

    @Id
    private Long id;

    /**
     * The text match modes, including the "is empty"/"is null" pair (one row is {@code null}, another blank).
     */
    private String name;

    /**
     * The numeric match modes.
     */
    private Integer salary;

    /**
     * The boolean match modes; {@code Boolean} rather than {@code boolean}, so a row can be {@code null}.
     */
    private Boolean active;

    /**
     * The date match modes, on a date without a time component.
     */
    private LocalDate reviewDate;

    /**
     * The datetime match modes, including "last/next N minutes/hours".
     */
    private LocalDateTime lastLogin;

    /**
     * The time match modes, on a bare time of day - a cyclic 24h clock.
     */
    private LocalTime startTime;

    /**
     * The date match modes again, but on a {@code java.util.Date}, whose criteria literals have to be converted
     * to {@code java.util.Date} rather than compared as a {@code LocalDate}/{@code LocalDateTime}.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date legacyDate;

    @Enumerated(EnumType.STRING)
    private Department department;

    /**
     * The "array" match modes, translated to {@code MEMBER OF} predicates on a mapped collection.
     */
    @ElementCollection
    @CollectionTable(name = "employee_tag", joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "tag")
    private Set<String> tags = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Date getLegacyDate() {
        return legacyDate;
    }

    public void setLegacyDate(Date legacyDate) {
        this.legacyDate = legacyDate;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}

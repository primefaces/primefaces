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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

import lombok.Getter;
import lombok.Setter;

/**
 * Entity behind dataTable053.xhtml, with one field per {@code filterValueType} preset, so that every match
 * mode of every preset can be filtered on the type it is meant for - server side, through the criteria
 * queries of {@link org.primefaces.model.JPALazyDataModel}.
 * <p>
 * Deliberately no Lombok {@code @Data}: its generated {@code equals}/{@code hashCode} would touch
 * {@link #tags} and trigger a lazy load outside of a transaction.
 */
@Entity
@Getter
@Setter
public class JpaEmployee {

    public enum Department {
        ENGINEERING,
        MARKETING,
        SALES
    }

    @Id
    private Long id;

    /**
     * The text match modes; one row is {@code null} and another blank, so "is empty" and "is null" differ.
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
     * The time match modes, on a bare time of day.
     */
    private LocalTime startTime;

    /**
     * The enum match modes.
     */
    @Enumerated(EnumType.STRING)
    private Department department;

    /**
     * The "array" match modes, translated to {@code MEMBER OF} predicates on a mapped collection.
     */
    @ElementCollection
    @CollectionTable(name = "jpa_employee_tag", joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "tag")
    private Set<String> tags = new LinkedHashSet<>();
}

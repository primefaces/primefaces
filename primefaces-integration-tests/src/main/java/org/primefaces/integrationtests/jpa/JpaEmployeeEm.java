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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jakarta.inject.Qualifier;

/**
 * Qualifies the {@link jakarta.persistence.EntityManager} of the "integration-tests" persistence unit - the
 * JPALazyDataModel test page's own H2 database (see {@link JpaEmployeeRepository}).
 * <p>
 * Needed because the module bootstraps two persistence units by hand: this one and the "integration-test"
 * unit behind {@link EntityManagerProducer}, whose EntityManager stays the {@code @Default} one. Without a
 *  qualifier, CDI sees two candidates for every {@code @Inject EntityManager} and refuses to deploy
 * ("WELD-001409: Ambiguous dependencies for type EntityManager").
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface JpaEmployeeEm {
}

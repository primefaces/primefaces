/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.cli.migration.primeflex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PrimeFlexMigrationTest {

    private static final PrimeFlexMigration migration = new PrimeFlexMigration();

    @BeforeAll
    static void beforeAll() {
        migration.initReplaceRegEx();
    }

    @Test
    void migrateV2ToV3Col() {
        String source = "<div class=\"p-grid\"><div class=\"p-col-3\"></div><div class=\"p-col-9\"></div></div>";
        String result = migration.migrateSource(source);
        Assertions.assertEquals("<div class=\"grid\"><div class=\"col-3\"></div><div class=\"col-9\"></div></div>", result);
    }

    @Test
    void migrateV2ToV3Grid() {
        String source = "<div class=\"p-grid\"><div class=\"p-col-3 p-sm-12 p-md-6 p-lg-3 p-xl-2\"></div></div>";
        String result = migration.migrateSource(source);
        Assertions.assertEquals("<div class=\"grid\"><div class=\"col-3 sm:col-12 md:col-6 lg:col-3 xl:col-2\"></div></div>", result);
    }

    @Test
    void migrateV2ToV3Spacing() {
        String source = "<div class=\"p-m-1 p-p-1 p-m-lg-3 p-m-lg-3\"></div>";
        String result = migration.migrateSource(source);
        Assertions.assertEquals("<div class=\"m-1 p-1 lg:m-3 lg:m-3\"></div>", result);
    }

    @Test
    void noMigrationSrOnly() {
        String source = "<div class=\"p-sr-only\"></div>";
        String result = migration.migrateSource(source);
        Assertions.assertEquals("<div class=\"p-sr-only\"></div>", result);
    }

    @Test
    void migrateV2ToV3AlternativeJustifyContentClasses() {
        String source = "<div class=\"p-justify-start p-justify-end p-justify-center p-justify-between p-justify-around p-justify-even\"></div>";
        String result = migration.migrateSource(source);
        Assertions.assertEquals("<div class=\"justify-content-start justify-content-end justify-content-center justify-content-between justify-content-around justify-content-evenly\"></div>", result);
    }

    @Test
    void migrateV2ToV3SpacingCornerCase() {
        String source = "<div class=\"p-p-lg-3\"></div>";
        String result = migration.migrateSource(source);
        Assertions.assertEquals("<div class=\"lg:p-3\"></div>", result);
    }


}

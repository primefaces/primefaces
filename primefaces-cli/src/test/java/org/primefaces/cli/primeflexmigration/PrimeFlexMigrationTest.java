package org.primefaces.cli.primeflexmigration;

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
        String result = migration.migrateV2ToV3(source);
        Assertions.assertEquals("<div class=\"grid\"><div class=\"col-3\"></div><div class=\"col-9\"></div></div>", result);
    }

    @Test
    void migrateV2ToV3Grid() {
        String source = "<div class=\"p-grid\"><div class=\"p-col-3 p-sm-12 p-md-6 p-lg-3 p-xl-2\"></div></div>";
        String result = migration.migrateV2ToV3(source);
        Assertions.assertEquals("<div class=\"grid\"><div class=\"col-3 sm:col-12 md:col-6 lg:col-3 xl:col-2\"></div></div>", result);
    }


}
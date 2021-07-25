package org.primefaces.cli.primeflexmigration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PrimeFlexMigrationTest {

    private static final PrimeFlexMigration migration = new PrimeFlexMigration();

    @BeforeAll
    static void beforeAll() {
        migration.initReplaceRegEx();
    }

    @Test
    void migrateV2ToV3() {
        String source = "<div class=\"p-grid\"><div class=\"p-col-3\"></div><div class=\"p-col-9\"></div></div>";
        String result = migration.migrateV2ToV3(source);
        System.out.println("conversation-result: " + result);
    }


}
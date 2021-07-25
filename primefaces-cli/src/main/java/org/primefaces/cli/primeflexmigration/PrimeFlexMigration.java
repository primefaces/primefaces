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
package org.primefaces.cli.primeflexmigration;

import java.util.HashMap;
import java.util.Map;

public class PrimeFlexMigration {

    private final Map<String, String> replaceRegex = new HashMap<>();

    public static void main(String[] args){
        System.out.println("PrimeFlex V2 --> V3 migration tool");
        System.out.println("Supported parameters:");
        System.out.println("-directory=c:\\users\\myuser\\javadev\\myproject");
        System.out.println("-fileextensions=xhtml");

        PrimeFlexMigration migration = new PrimeFlexMigration();
        migration.initReplaceRegEx();

        // TODO: scan directory recursive and migration all files matching filexextensions
    }

    void initReplaceRegEx() {
        replaceRegex.put("p-grid", "grid");
        replaceRegex.put("p-col-([0-9]+?)", "col-$1");
        replaceRegex.put("p-col", "col");
        replaceRegex.put("p-(xl|lg|md|sm)-([0-9]+?)", "$1:col-$2");
    }

    String migrateV2ToV3(String source) {
        String result = source;

        for (Map.Entry<String, String> entry : replaceRegex.entrySet()) {
            String rv2 = entry.getKey();
            String rv3 = entry.getValue();
            result = result.replaceAll(rv2, rv3);
        }

        return result;
    }
}

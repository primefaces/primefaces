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

import java.io.BufferedWriter;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class PrimeFlexMigration {

    private final Map<String, String> replaceRegex = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("PrimeFlex V2 --> V3 migration tool");
        System.out.println("Supported parameters:");
        System.out.println("-directory=c:\\temp\\myapp");
        System.out.println("-fileextensions=xhtml");
        System.out.println("-replaceexisting=true");

        PrimeFlexMigration migration = new PrimeFlexMigration();
        migration.initReplaceRegEx();

        String directory = "c:\\temp\\myapp2";
        String[] aFileextensions = {"xhtml"};
        Boolean replaceExisting = true;

        for (String arg : args) {
            if (arg.startsWith("-")) {
                String[] argSplitted = arg.substring(1).split("=");
                if (argSplitted.length == 2) {
                    if ("directory".equals(argSplitted)) {
                        directory = argSplitted[1];
                    }
                    else if ("fileextensions".equals(argSplitted)) {
                        aFileextensions = argSplitted[1].toLowerCase().split(",");
                    }
                    else if ("replaceExisting".equals(argSplitted)) {
                        replaceExisting = Boolean.parseBoolean(argSplitted[1]);
                    }
                }
            }
        }

        Set<String> fileextensions = new HashSet<String>(Arrays.asList(aFileextensions));

        try {
            migration.migrateDirectory(Paths.get(directory), fileextensions, replaceExisting);
        }
        catch (Exception ex) {
            System.out.println("Error during migration: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    void initReplaceRegEx() {
        // see https://www.primefaces.org/primeflex/migration

        // grid
        replaceRegex.put("p-grid", "grid");
        replaceRegex.put("p-col-([0-9]+?)", "col-$1");
        replaceRegex.put("p-col", "col");
        replaceRegex.put("p-(xl|lg|md|sm)-([0-9]+?)", "$1:col-$2");

        // display
        replaceRegex.put("p-d-none", "hidden");
        replaceRegex.put("p-d-inline", "inline");
        replaceRegex.put("p-d-inline-block", "inline-block");
        replaceRegex.put("p-d-block", "block");
        replaceRegex.put("p-d-flex", "flex");
        replaceRegex.put("p-d-inline-flex", "inline-flex");

        // flexbox
        replaceRegex.put("p-flex-row", "flex-row");
        replaceRegex.put("p-flex-column", "flex-column");
        replaceRegex.put("p-flex-row-reverse", "flex-row-reverse");
        replaceRegex.put("p-flex-column-reverse", "flex-column-reverse");
        replaceRegex.put("p-order-1", "flex-order-2"); // correct ???
        replaceRegex.put("p-order-([0-9])", "flex-order-$1");
        replaceRegex.put("p-flex-nowrap", "flex-nowrap");
        replaceRegex.put("p-flex-wrap", "flex-wrap");
        replaceRegex.put("p-flex-wrap-reverse", "flex-wrap-reverse");
        replaceRegex.put("p-jc-start", "justify-content-start");
        replaceRegex.put("p-jc-end", "justify-content-end");
        replaceRegex.put("p-jc-center", "justify-content-center");
        replaceRegex.put("p-jc-between", "justify-content-between");
        replaceRegex.put("p-jc-around", "justify-content-around");
        replaceRegex.put("p-jc-evenly", "justify-content-evenly");
        replaceRegex.put("p-ai-start", "align-items-start");
        replaceRegex.put("p-ai-end", "align-items-end");
        replaceRegex.put("p-ai-center", "align-items-center");
        replaceRegex.put("p-ai-baseline", "align-items-baseline");
        replaceRegex.put("p-ai-stretch", "align-items-stretch");
        replaceRegex.put("p-as-start", "align-self-start");
        replaceRegex.put("p-as-end", "align-self-start");
        replaceRegex.put("p-as-center", "align-self-start");
        replaceRegex.put("p-as-baseline", "align-self-start");
        replaceRegex.put("p-as-stretch", "align-self-stretch");
        replaceRegex.put("p-ac-start", "align-content-start");
        replaceRegex.put("p-ac-end", "align-content-end");
        replaceRegex.put("p-ac-center", "align-content-center");
        replaceRegex.put("p-ac-around", "align-content-around");
        replaceRegex.put("p-ac-between", "align-content-between");

        // text
        replaceRegex.put("p-text-right", "text-right");
        replaceRegex.put("p-text-center", "text-center");
        replaceRegex.put("p-text-justify", "text-justify");
        replaceRegex.put("p-text-lowercase", "text-lowercase");
        replaceRegex.put("p-text-uppercase", "text-uppercase");
        replaceRegex.put("p-text-capitalize", "text-capitalize");
        replaceRegex.put("p-text-bold", "font-bold");
        replaceRegex.put("p-text-normal", "font-normal");
        replaceRegex.put("p-text-light", "font-light");
        replaceRegex.put("p-text-italic", "font-italic");

        // TODO: 2 be continued
//        replaceRegex.put("", "");
//        replaceRegex.put("", "");
//        replaceRegex.put("", "");
//        replaceRegex.put("", "");
//        replaceRegex.put("", "");
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

    void migrateDirectory(Path directory, Set<String> fileextensions, boolean replaceExisting) throws Exception {
        Files.list(directory).forEach(f -> {
            try {
                if (Files.isDirectory(f)) {
                    migrateDirectory(f, fileextensions, replaceExisting);
                }
                else if (Files.isRegularFile(f) && Files.isWritable(f)) {
                    boolean migrateFile = false;
                    String filenameLC = f.toString().toLowerCase();

                    for (String fileextension : fileextensions) {
                        if (filenameLC.endsWith("." + fileextension)) {
                            migrateFile = true;
                        }
                    }

                    if (migrateFile) {
                        List<String> contentV2 = Files.readAllLines(f);
                        List<String> contentV3 = contentV2.stream().map(l -> migrateV2ToV3(l)).collect(Collectors.toList());
                        Path tmpFile = Paths.get(f.toString() + "v3");
                        try (BufferedWriter writer = Files.newBufferedWriter(tmpFile, StandardOpenOption.CREATE)) {
                            for (String l : contentV3) {
                                writer.write(l);
                            }
                        }

                        if (replaceExisting) {
                            Files.delete(f);
                            Files.move(tmpFile, f);
                            System.out.println("migrated " + f.toString());
                        }
                        else {
                            System.out.println("migrated " + f.toString() + " to " + tmpFile.toString());
                        }
                    }
                }
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}

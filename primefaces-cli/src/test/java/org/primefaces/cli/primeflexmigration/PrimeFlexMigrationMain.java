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

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PrimeFlexMigrationMain {

    public static void main(String[] args) {
        System.out.println("PrimeFlex V2 --> V3 migration tool");
        System.out.println("");
        System.out.println("Supported parameters:");
        System.out.println("-directory=c:\\temp\\myapp");
        System.out.println("-fileextensions=xhtml");
        System.out.println("-replaceexisting=true");
        System.out.println("");

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
            System.out.println("start migrating " + directory + " and subfolders; " +
                    "fileextension: " + fileextensions.stream().collect(Collectors.joining(",")) + "; " +
                    "replaceExisting: " + replaceExisting);
            migration.migrateDirectory(Paths.get(directory), fileextensions, replaceExisting);
            System.out.println("finished migration");
        }
        catch (Exception ex) {
            System.out.println("Error during migration: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}

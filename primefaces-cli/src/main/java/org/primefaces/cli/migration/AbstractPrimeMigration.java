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
package org.primefaces.cli.migration;

import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractPrimeMigration implements Runnable {

    @CommandLine.Parameters(
            description = "Directory (including subdirectories) where files with specified fileextension(s) " +
                    "should be migrated.")
    protected String directory;

    @CommandLine.Option(names = { "-e", "--fileextension" }, defaultValue = "xhtml", split = ",",
            description = "Whitelist of fileextensions of files in the specified directory which should be converted.")
    protected String[] fileextensions = {"xhtml"};

    @CommandLine.Option(names = { "-r", "--replaceexisting" }, defaultValue = "true",
            description = "Replace existing files with converted ones? False means the migrated files are written with additional migrated-suffix.")
    protected Boolean replaceExisting = true;

    protected final Map<String, String> replaceRegex = new LinkedHashMap<>();

    @Override
    public void run() {
        Set<String> fileextensionsSet = new HashSet<>(Arrays.asList(fileextensions));

        initReplaceRegEx();

        try {
            System.out.println("Start migrating " + directory + " and subdirectories; " +
                    "fileextension: " + fileextensionsSet.stream().collect(Collectors.joining(",")) + "; " +
                    "replaceExisting: " + replaceExisting);
            migrateDirectory(Paths.get(directory), fileextensionsSet, replaceExisting);
            System.out.println("Finished migration!");
        }
        catch (Exception ex) {
            System.err.println("Error during migration: " + ex.toString());
        }
    }

    protected abstract void initReplaceRegEx();

    public String migrateSource(String source) {
        String result = source;

        for (Map.Entry<String, String> entry : replaceRegex.entrySet()) {
            String rOld = entry.getKey();
            String rNew = entry.getValue();
            result = result.replaceAll(rOld, rNew);
        }

        return result;
    }

    protected void migrateDirectory(Path directory, Set<String> fileextensions, boolean replaceExisting) throws Exception {
        Files.list(directory).forEach(f -> {
            try {
                if (Files.isDirectory(f)) {
                    migrateDirectory(f, fileextensions, replaceExisting);
                }
                else if (Files.isRegularFile(f) && Files.isWritable(f)) {
                    try {
                        boolean migrateFile = false;
                        String filenameLC = f.toString().toLowerCase();

                        for (String fileextension : fileextensions) {
                            if (filenameLC.endsWith("." + fileextension)) {
                                migrateFile = true;
                            }
                        }

                        if (migrateFile) {
                            migrateFile(f, replaceExisting);
                        }
                    }
                    catch (Exception ex) {
                        System.err.println("...error during migrating " + f.toString() + ":" + ex.toString());
                    }
                }
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    protected void migrateFile(Path f, boolean replaceExisting) throws IOException {
        List<String> contentV2 = Files.readAllLines(f);
        List<String> contentV3 = contentV2.stream().map(l -> migrateSource(l)).collect(Collectors.toList());
        Path tmpFile = Paths.get(f.toString() + ".migrated");
        try (BufferedWriter writer = Files.newBufferedWriter(tmpFile, StandardOpenOption.CREATE)) {
            int line = 0;
            for (String l : contentV3) {
                if (line > 0) {
                    writer.newLine();
                }
                writer.write(l);
                line++;
            }
            writer.newLine(); // newline at the end of the file (that´s what git expects usually)
        }

        if (replaceExisting) {
            Files.delete(f);
            Files.move(tmpFile, f);
            System.out.println("...migrated " + f.toString());
        }
        else {
            System.out.println("...migrated " + f.toString() + " to " + tmpFile.toString());
        }
    }
}

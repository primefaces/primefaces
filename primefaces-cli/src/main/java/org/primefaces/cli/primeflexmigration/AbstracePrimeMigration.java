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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstracePrimeMigration implements Runnable {

    final Map<String, String> replaceRegex = new LinkedHashMap<>();

    String migrateSource(String source) {
        String result = source;

        for (Map.Entry<String, String> entry : replaceRegex.entrySet()) {
            String rOld = entry.getKey();
            String rNew = entry.getValue();
            result = result.replaceAll(rOld, rNew);
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

    private void migrateFile(Path f, boolean replaceExisting) throws IOException {
        List<String> contentV2 = Files.readAllLines(f);
        List<String> contentV3 = contentV2.stream().map(l -> migrateSource(l)).collect(Collectors.toList());
        Path tmpFile = Paths.get(f.toString() + "v3");
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

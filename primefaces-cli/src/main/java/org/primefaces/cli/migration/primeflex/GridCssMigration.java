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

import org.primefaces.cli.migration.AbstractPrimeMigration;
import picocli.CommandLine;

@CommandLine.Command(name = "GridCssMigration", mixinStandardHelpOptions = true, version = "early WIP",
    description = "This CLI-Tool replaces Grid CSS - classes in your HTML, XHTML, ... - files with PrimeFlex 2 - CSS - classes.",
    headerHeading = "@|bold,underline Usage|@:%n%n",
    descriptionHeading = "%n@|bold,underline Description|@:%n",
    parameterListHeading = "%n@|bold,underline Parameters|@:%n",
    optionListHeading = "%n@|bold,underline Options|@:%n")
public class GridCssMigration extends AbstractPrimeMigration implements Runnable {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new GridCssMigration()).execute(args);
        System.exit(exitCode);
    }

    @Override
    protected void initReplaceRegEx() {
        // see https://www.primefaces.org/primeflex/migration

        // grid
        replaceRegex.put("ui-g-(xl|lg|md|sm)-([0-9]+?)", "p-(xl|lg|md|sm)-([0-9]+?)");
        replaceRegex.put("ui-g-([0-9]+?)", "p-col-$1");
        replaceRegex.put("ui-g-nopad", "p-grid p-nogutter");
        replaceRegex.put("ui-g", "p-grid");

        // complete?
    }
}

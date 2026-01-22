/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.showcase.view.data.tree;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class FileInfo implements Serializable {

    private final String path;
    private final String name;
    private final boolean directory;

    public FileInfo(String path, boolean directory) {
        this.directory = directory;
        if (path == null) {
            this.path = File.separator;
            this.name = File.separator;
        }
        else if (path.equals(File.separator)) {
            this.name = path;
            this.path = path;
        }
        else {
            String[] parts = path.split(File.separator.equals("\\") ? "\\\\" : File.separator);
            this.name = parts[parts.length - 1];
            this.path = path;
        }
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return directory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo fileInfo = (FileInfo) o;
        return isDirectory() == fileInfo.isDirectory() && Objects.equals(getPath(), fileInfo.getPath()) && Objects.equals(getName(), fileInfo.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath(), getName(), isDirectory());
    }

    @Override
    public String toString() {
        return path + " / " + name;
    }
}
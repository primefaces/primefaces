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
package org.primefaces.showcase.util;

import javax.faces.context.FacesContext;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.primefaces.util.LangUtils;

/**
 * FileContentMarkerUtil
 *
 * @author Sébastien Lepage / last modified by $Author$
 * @version $Revision$
 * @since 6.3
 */
public class FileContentMarkerUtil {

    private static final FileContentSettings JAVA_FILE_SETTINGS = new FileContentSettings()
            .setType("java")
            .setStartMarkers(
                    Marker.of("@Named"),
                    Marker.of("@RequestScoped"),
                    Marker.of("@ViewScoped"),
                    Marker.of("@SessionScoped"),
                    Marker.of("@FacesConverter"),
                    Marker.of("@Target"),
                    Marker.of(" class "),
                    Marker.of(" enum "),
                    Marker.of("EXCLUDE-SOURCE-END").excluded())
            .setEndMarkers(Marker.of("EXCLUDE-SOURCE-START").excluded());

    private static final FileContentSettings XHTML_FILE_SETTINGS = new FileContentSettings()
            .setType("xml")
            .setStartMarkers(
                    Marker.of("EXAMPLE-SOURCE-START").excluded(),
                    Marker.of("<ui:define name=\"implementation\">").excluded(),
                    Marker.of("<ui:define name=\"head\">").excluded())
            .setEndMarkers(
                    Marker.of("EXAMPLE-SOURCE-END").excluded(),
                    Marker.of("</ui:define>").excluded());

    private static final Pattern SC_BEAN_PATTERN = Pattern.compile("#\\{\\w*?\\s?(\\w+)[\\.\\[].*\\}");

    private static final String SC_PREFIX = "org.primefaces.showcase";

    private FileContentMarkerUtil() {

    }

    public static FileContent readFileContent(String fullPathToFile, InputStream is, boolean readBeans) {
        try {
            String fileName = fullPathToFile.substring(fullPathToFile.lastIndexOf("/") + 1);
            if (fullPathToFile.endsWith(".java")) {
                return readFileContent(fileName, is, JAVA_FILE_SETTINGS, readBeans);
            }

            if (fullPathToFile.endsWith(".xhtml")) {
                return readFileContent(fileName, is, XHTML_FILE_SETTINGS, readBeans);
            }

            throw new UnsupportedOperationException();
        }
        catch (Exception e) {
            throw new IllegalStateException("Internal error: file " + fullPathToFile + " could not be read", e);
        }
    }

    private static FileContent readFileContent(String fileName, InputStream inputStream, FileContentSettings settings, boolean readBeans) throws Exception {
        StringBuilder content = new StringBuilder();
        List<FileContent> javaFiles = new ArrayList<>();
        FacesContext facesContext = FacesContext.getCurrentInstance();

        try (InputStreamReader ir = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(ir)) {
            String line;
            boolean started = false;

            while ((line = br.readLine()) != null) {
                if (!started) {
                    Marker marker = getMatchingMarker(line, settings.getStartMarkers());
                    started = marker != null;
                    if (!started || marker.isExcluded()) {
                        continue;
                    }
                }

                // if is before first end marker
                if (started && getMatchingMarker(line, settings.getEndMarkers()) != null) {
                    started = false;
                    content.append("\n");
                    continue;
                }

                content.append("\n");
                content.append(line);

                if (readBeans && line.contains("#{")) {
                    Matcher m = SC_BEAN_PATTERN.matcher(line.trim());
                    while (m.find()) {
                        String group = m.group(1);
                        addBean(facesContext, javaFiles, group);
                    }
                }
            }
        }

        String value = content.toString().trim();
        if ("xml".equalsIgnoreCase(settings.getType())) {
            value = prettyFormat(value);
        }
        return new FileContent(fileName, value, settings.getType(), javaFiles);
    }

    private static void addBean(FacesContext facesContext, List<FileContent> javaFiles, String group) throws Exception {
        Object bean = facesContext.getApplication().evaluateExpressionGet(facesContext, "#{" + group + "}", Object.class);
        if (bean == null) {
            return;
        }

        Class<?> beanClass = LangUtils.getUnproxiedClass(bean.getClass());
        if (isEligibleFile(beanClass.getName())) {
            // special handling for member classes (like ColumnsView and ColumnsView$ColumnModel)
            String className = beanClass.getName();
            if (beanClass.isMemberClass()) {
                className = className.substring(0, className.indexOf("$"));
            }

            String javaFileName = packageToPathAccess(className);
            if (!isFileContainedIn(javaFileName, javaFiles)) {
                javaFiles.add(createFileContent(className));
            }

            for (Field field : beanClass.getDeclaredFields()) {
                addDeclaredField(javaFiles, field);
            }
        }
    }

    private static void addDeclaredField(List<FileContent> javaFiles, Field field) throws Exception {
        String typeName = field.getType().getTypeName();
        String javaFileName = packageToPathAccess(typeName);
        if (isEligibleFile(typeName)
                && !isFileContainedIn(javaFileName, javaFiles)) {
            javaFiles.add(createFileContent(typeName));
        }
    }

    private static FileContent createFileContent(String fileName) throws Exception {
        String path = createFullPath(fileName);
        InputStream is = FileContentMarkerUtil.class.getResourceAsStream(path);

        if (is == null) {
            throw new FileNotFoundException("File " + path + " could not be found");
        }

        return readFileContent(fileName.substring(fileName.lastIndexOf(".") + 1) + ".java",
                is,
                JAVA_FILE_SETTINGS,
                false);
    }

    private static Marker getMatchingMarker(String line, Marker[] markers) {
        for (Marker marker : markers) {
            if (line.contains(marker.getName())) {
                return marker;
            }
        }

        return null;
    }

    private static String prettyFormat(String value) {
        String[] chunks = value.split("(?<=\\n)");
        String pretty = "";
        for (String chunk : chunks) {
            pretty += chunk.replaceFirst("\\s{8}", "");
        }
        return pretty;
    }

    private static boolean isEligibleFile(String file) {
        return file != null && file.startsWith(SC_PREFIX);
    }

    private static String packageToPathAccess(String pckage) {
        return pckage.substring(pckage.lastIndexOf(".") + 1) + ".java";
    }

    private static boolean isFileContainedIn(String filename, List<FileContent> javaFiles) {
        return javaFiles.contains(new FileContent(filename, null, null, null));
    }

    private static String createFullPath(String filename) {
        return "/" + filename.replaceAll("\\.", "/") + ".java";
    }
}

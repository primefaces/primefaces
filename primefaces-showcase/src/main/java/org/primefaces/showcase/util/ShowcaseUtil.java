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
package org.primefaces.showcase.util;

import org.primefaces.cache.CacheProvider;
import org.primefaces.component.tabview.Tab;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.application.ProjectStage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIPanel;
import jakarta.faces.context.FacesContext;

public class ShowcaseUtil {

    private static final Logger LOGGER = Logger.getLogger(ShowcaseUtil.class.getName());

    private ShowcaseUtil() {

    }

    public static List<FileContent> getFilesContent(String fullPath, Boolean readBeans) {
        CacheProvider provider = CDI.current().select(ShowcaseCacheProvider.class).get().getCacheProvider();
        List<FileContent> files = (List<FileContent>) provider.get("contents", fullPath);

        if (files == null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            FileContent srcContent = getFileContent(fullPath, readBeans);
            if (srcContent == null) {
                LOGGER.severe("File " + fullPath + " not found");
            }
            else {
                UIComponent tabs = UIComponent.getCurrentComponent(facesContext).getFacet("static-tabs");
                if (tabs != null) {
                    attach(tabs, srcContent);
                }
                files = new ArrayList<>();
                flatFileContent(srcContent, files);
            }

            if (facesContext.isProjectStage(ProjectStage.Production)) {
                provider.put("contents", fullPath, files);
            }
        }
        return files;
    }

    public static Object getPropertyValueViaReflection(Object o, String field)
            throws ReflectiveOperationException, IllegalArgumentException, IntrospectionException {
        return new PropertyDescriptor(field, o.getClass()).getReadMethod().invoke(o);
    }

    // EXCLUDE-SOURCE-START
    private static FileContent getFileContent(String fullPath, Boolean readBeans) {
        try {
            // in case we get a request with .jsf, we should remap it to .xhtml
            // later we could remove .jsf mapping propably
            if (fullPath.endsWith(".jsf")) {
                fullPath = fullPath.substring(0, fullPath.length() - ".jsf".length()) + ".xhtml";
            }

            // Finding in WEB ...
            FacesContext fc = FacesContext.getCurrentInstance();
            InputStream is = fc.getExternalContext().getResourceAsStream(fullPath);
            if (is != null) {
                return FileContentMarkerUtil.readFileContent(fullPath, is, readBeans);
            }

            // Finding in ClassPath ...
            is = ShowcaseUtil.class.getResourceAsStream(fullPath);
            if (is != null) {
                return FileContentMarkerUtil.readFileContent(fullPath, is, readBeans);
            }
        }
        catch (Exception e) {
            throw new IllegalStateException("Internal error: file " + fullPath + " could not be read", e);
        }

        return null;
    }

    private static void attach(UIComponent component, FileContent file) {
        if (component.isRendered()) {
            if (component instanceof Tab) {
                String flatten = (String) component.getAttributes().get("flatten");
                FileContent content = getFileContent(
                        ((Tab) component).getTitle(),
                        flatten == null ? false : Boolean.valueOf(flatten));
                file.getAttached().add(content);
            }
            else if (component instanceof UIPanel) {
                for (UIComponent child : component.getChildren()) {
                    attach(child, file);
                }
            }
        }
    }

    private static void flatFileContent(FileContent source, List<FileContent> dest) {
        dest.add(new FileContent(source.getTitle(), source.getValue(), source.getType(), Collections.<FileContent>emptySet()));

        for (FileContent file : source.getAttached()) {
            flatFileContent(file, dest);
        }
    }
    // EXCLUDE-SOURCE-END
}

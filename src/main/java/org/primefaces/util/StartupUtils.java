/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import org.primefaces.config.PrimeEnvironment;

/**
 * @author Kyle Stiemann
 */
public final class StartupUtils {

    private static final Logger LOGGER = Logger.getLogger(StartupUtils.class.getName());

    private StartupUtils() {
        // prevent instantiation
    }

    public static String getBuildVersion() {

        String buildVersion = null;

        Properties buildProperties = new Properties();
        InputStream is = null;
        try {
            is = PrimeEnvironment.class.getResourceAsStream("/META-INF/maven/org.primefaces/primefaces/pom.properties");
            buildProperties.load(is);
            buildVersion = buildProperties.getProperty("version");
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "PrimeFaces version not resolvable - Could not load pom.properties.");
        }

        if (is != null) {
            try {
                is.close();
            }
            catch (IOException e) {
            }
        }

        // This should only happen if PF + the webapp is openend and started in the same netbeans instance
        // Fallback to a UID to void a empty version in the resourceUrls
        if (buildVersion == null || buildVersion.trim().isEmpty()) {
            buildVersion = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }

        return buildVersion;
    }

    public static boolean isAtLeastJsf23(ClassLoader applicationClassLoader) {
        return LangUtils.tryToLoadClassForName("javax.faces.component.UIImportConstants", applicationClassLoader) != null;
    }

    public static boolean isAtLeastJsf22(ClassLoader applicationClassLoader) {
        return LangUtils.tryToLoadClassForName("javax.faces.flow.Flow", applicationClassLoader) != null;
    }

    public static ClassLoader getApplicationClassLoader(FacesContext facesContext) {

        ClassLoader applicationClassLoader = null;
        Object context = facesContext.getExternalContext().getContext();
        if (context != null) {
            try {
                // Reflectively call getClassLoader() on the context in order to be compatible with both the Portlet 3.0
                // API and the Servlet API without depending on the Portlet API directly.
                Method getClassLoaderMethod = context.getClass().getMethod("getClassLoader");

                if (getClassLoaderMethod != null) {
                    applicationClassLoader = (ClassLoader) getClassLoaderMethod.invoke(context);
                }
            }
            catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | AbstractMethodError |
                NoSuchMethodError | UnsupportedOperationException e) {
                // Do nothing.
            }
            catch (Throwable t) {
                LOGGER.log(Level.WARNING, "An unexpected Exception or Error was thrown when calling " +
                    "facesContext.getExternalContext().getContext().getClassLoader(). Falling back to " +
                    "Thread.currentThread().getContextClassLoader() instead.", t);
            }
        }

        // If the context is unavailable or this is a Portlet 2.0 environment, the ClassLoader cannot be obtained from
        // the context, so use Thread.currentThread().getContextClassLoader() to obtain the application ClassLoader
        // instead.
        if (applicationClassLoader == null) {
            applicationClassLoader = LangUtils.getCurrentClassLoader(StartupUtils.class);
        }

        return applicationClassLoader;
    }
}

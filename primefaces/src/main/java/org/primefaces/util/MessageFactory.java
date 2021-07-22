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
package org.primefaces.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;

public class MessageFactory {

    private static final String DEFAULT_BUNDLE_BASENAME = "javax.faces.Messages";
    private static final String PRIMEFACES_BUNDLE_BASENAME = "org.primefaces.Messages";
    private static final String DEFAULT_DETAIL_SUFFIX = "_detail";

    private MessageFactory() {
        // NOOP
    }

    public static FacesMessage getFacesMessage(String messageId, FacesMessage.Severity severity, Object... params) {
        FacesMessage facesMessage = getFacesMessage(LocaleUtils.getCurrentLocale(), messageId, params);
        facesMessage.setSeverity(severity);
        return facesMessage;
    }

    public static FacesMessage getFacesMessage(Locale locale, String messageId, Object... params) {
        String summary = null;
        String detail = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application application = facesContext.getApplication();
        String userBundleName = application.getMessageBundle();
        ResourceBundle bundle = null;
        ClassLoader currentClassLoader = LangUtils.getCurrentClassLoader(application.getClass());

        // try user defined bundle first
        if (userBundleName != null) {
            try {
                bundle = getBundle(userBundleName, locale, currentClassLoader, facesContext);
                if (bundle.containsKey(messageId)) {
                    summary = bundle.getString(messageId);
                }
            }
            catch (MissingResourceException e) {
                // No Op
            }
        }

        // try primefaces bundle
        if (summary == null) {
            try {
                bundle = getBundle(PRIMEFACES_BUNDLE_BASENAME, locale, currentClassLoader, facesContext);
                if (bundle == null) {
                    throw new NullPointerException();
                }
                if (bundle.containsKey(messageId)) {
                    summary = bundle.getString(messageId);
                }
            }
            catch (MissingResourceException e) {
                // No Op
            }
        }

        // fallback to default jsf bundle
        if (summary == null) {
            try {
                bundle = getBundle(DEFAULT_BUNDLE_BASENAME, locale, currentClassLoader, facesContext);
                if (bundle == null) {
                    throw new NullPointerException();
                }
                if (bundle.containsKey(messageId)) {
                    summary = bundle.getString(messageId);
                }
            }
            catch (MissingResourceException e) {
                // No Op
            }
        }

        summary = getFormattedText(locale, summary, params);

        if (bundle != null) {
            try {
                String detailMessageId = messageId + DEFAULT_DETAIL_SUFFIX;
                if (bundle.containsKey(detailMessageId)) {
                    detail = getFormattedText(locale, bundle.getString(detailMessageId), params);
                }
            }
            catch (MissingResourceException e) {
                // NoOp
            }
        }

        return new FacesMessage(summary, detail);
    }

    public static String getMessage(String messageId, Object... params) {
        return getMessage(LocaleUtils.getCurrentLocale(), messageId, params);
    }

    public static String getMessage(Locale locale, String messageId, Object... params) {
        String summary = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application application = facesContext.getApplication();
        String userBundleName = application.getMessageBundle();
        ResourceBundle bundle = null;
        ClassLoader currentClassLoader = LangUtils.getCurrentClassLoader(application.getClass());

        // try user defined bundle first
        if (userBundleName != null) {
            try {
                bundle = getBundle(userBundleName, locale, currentClassLoader, facesContext);
                if (bundle.containsKey(messageId)) {
                    summary = bundle.getString(messageId);
                }
            }
            catch (MissingResourceException e) {
                // No Op
            }
        }

        // try primefaces bundle
        if (summary == null) {
            try {
                bundle = getBundle(PRIMEFACES_BUNDLE_BASENAME, locale, currentClassLoader, facesContext);
                if (bundle == null) {
                    throw new NullPointerException();
                }
                if (bundle.containsKey(messageId)) {
                    summary = bundle.getString(messageId);
                }
            }
            catch (MissingResourceException e) {
                // No Op
            }
        }

        // fallback to default jsf bundle
        if (summary == null) {
            try {
                bundle = getBundle(DEFAULT_BUNDLE_BASENAME, locale, currentClassLoader, facesContext);
                if (bundle == null) {
                    throw new NullPointerException();
                }
                if (bundle.containsKey(messageId)) {
                    summary = bundle.getString(messageId);
                }
            }
            catch (MissingResourceException e) {
                // No Op
            }
        }

        summary = getFormattedText(locale, summary, params);

        return summary;
    }

    public static String getFormattedText(Locale locale, String message, Object... params) {
        if ((params == null || params.length == 0) || LangUtils.isValueBlank(message)) {
            return message;
        }

        MessageFormat messageFormat = null;
        if (locale != null) {
            messageFormat = new MessageFormat(message, locale);
        }
        else {
            messageFormat = new MessageFormat(message);
        }

        return messageFormat.format(params);
    }

    public static Object getLabel(FacesContext facesContext, UIComponent component) {
        String label = (String) component.getAttributes().get("label");

        if (label == null) {
            label = component.getClientId(facesContext);
        }

        return label;
    }

    private static ResourceBundle getBundle(String baseName, Locale locale, ClassLoader classLoader,
                FacesContext facesContext) {

        ClassLoader primeFacesClassLoader = MessageFactory.class.getClassLoader();

        if (PRIMEFACES_BUNDLE_BASENAME.equals(baseName)) {
            if (!primeFacesClassLoader.equals(classLoader)) {
                return ResourceBundle.getBundle(baseName, locale, classLoader, new PrimeFacesControl(primeFacesClassLoader));
            }
        }
        else if (DEFAULT_BUNDLE_BASENAME.equals(baseName)) {

            ClassLoader jsfImplClassLoader = getJSFImplClassLoader(facesContext);

            if (!jsfImplClassLoader.equals(classLoader)) {
                return ResourceBundle.getBundle(baseName, locale, classLoader, new PrimeFacesControl(jsfImplClassLoader));
            }
        }

        return ResourceBundle.getBundle(baseName, locale, classLoader, new PrimeFacesControl(primeFacesClassLoader));
    }

    private static ClassLoader getJSFImplClassLoader(FacesContext facesContext) {
        facesContext = unwrapFacesContext(facesContext);

        Class<? extends FacesContext> facesContextImplClass = FacesContext.class;
        if (facesContext != null) {
            facesContextImplClass = facesContext.getClass();
        }

        return facesContextImplClass.getClassLoader();
    }

    private static FacesContext unwrapFacesContext(FacesContext facesContext) {
        if (!(facesContext instanceof FacesContextWrapper)) {
            return facesContext;
        }

        FacesContextWrapper wrapper = (FacesContextWrapper) facesContext;
        FacesContext unwrapped = wrapper.getWrapped();

        if (unwrapped == null || FacesContext.class.equals(unwrapped.getClass())) {
            return facesContext;
        }

        return unwrapFacesContext(unwrapped);
    }

    /**
     * Custom ResourceBundle.Control to handle loading resources as UTF-8 in Java8 and OSGI classloader issues.
     *
     */
    private static final class PrimeFacesControl extends ResourceBundle.Control {

        private final ClassLoader osgiBundleClassLoader;

        public PrimeFacesControl(ClassLoader osgiBundleClassLoader) {
            this.osgiBundleClassLoader = osgiBundleClassLoader;
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader classLoader,
                    boolean reload) throws IllegalAccessException, InstantiationException, IOException {

            ResourceBundle resourceBundle = createUnicodeBundle(baseName, locale, format, classLoader, reload);

            // If the ResourceBundle cannot be found with the default class loader (usually the thread context class
            // loader or TCCL), try to find it with the OSGi bundle's class loader. Since default i18n files are
            // included inside of the jar/bundle that provides them, default i18n files are not visible to the TCCL in
            // an OSGi environment. Instead, i18n files are only visible to the class loader of the OSGi bundle that
            // they are included in.
            if (resourceBundle == null && !osgiBundleClassLoader.equals(classLoader)) {
                resourceBundle = createUnicodeBundle(baseName, locale, format, osgiBundleClassLoader, reload);
            }

            return resourceBundle;
        }

        /**
         * For Java 8 we need to create bundle using UTF-8. This is standard in JDK9+.
         */
        private ResourceBundle createUnicodeBundle(String baseName, Locale locale, String format, final ClassLoader loader,
                    final boolean reload) throws IllegalAccessException, InstantiationException, IOException {

            // The below is a copy of the default implementation.
            String bundleName = toBundleName(baseName, locale);
            ResourceBundle bundle = null;
            if ("java.class".equals(format)) {
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends ResourceBundle> bundleClass = (Class<? extends ResourceBundle>) loader.loadClass(bundleName);

                    // If the class isn't a ResourceBundle subclass, throw a ClassCastException.
                    if (!ResourceBundle.class.isAssignableFrom(bundleClass)) {
                        throw new ClassCastException(bundleClass.getName() + " cannot be cast to ResourceBundle");
                    }

                    bundle = bundleClass.getConstructor().newInstance();
                }
                catch (IllegalArgumentException | InvocationTargetException | SecurityException
                        | NoSuchMethodException | ClassNotFoundException e) {
                    // ignore for now
                }
            }
            else if ("java.properties".equals(format)) {
                String resourceName = toResourceName(bundleName, "properties");
                if (resourceName == null) {
                    return null;
                }

                try {
                    InputStream stream = AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>) () -> {
                        InputStream is = null;
                        if (reload) {
                            URL url = loader.getResource(resourceName);
                            if (url != null) {
                                URLConnection connection = url.openConnection();
                                if (connection != null) {
                                    connection.setUseCaches(false);
                                    is = connection.getInputStream();
                                }
                            }
                        }
                        else {
                            is = loader.getResourceAsStream(resourceName);
                        }
                        return is;
                    });

                    if (stream != null) {
                        try {
                            // Only this line is changed to make it to read properties files as UTF-8.
                            bundle = new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
                        }
                        finally {
                            stream.close();
                        }
                    }
                }
                catch (PrivilegedActionException e) {
                    throw (IOException) e.getException();
                }
            }
            else {
                throw new IllegalArgumentException("unknown format: " + format);
            }
            return bundle;
        }
    }
}

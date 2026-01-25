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
package org.primefaces.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.FacesContextWrapper;

public class MessageFactory {

    private static final String DEFAULT_BUNDLE_BASENAME = "jakarta.faces.Messages";
    private static final String PRIMEFACES_BUNDLE_BASENAME = "org.primefaces.Messages";
    private static final String DEFAULT_DETAIL_SUFFIX = "_detail";

    private MessageFactory() {
        // NOOP
    }

    public static FacesMessage getFacesMessage(FacesContext context, String messageId, FacesMessage.Severity severity, Object... params) {
        FacesMessage facesMessage = getFacesMessage(context, LocaleUtils.getCurrentLocale(context), messageId, params);
        facesMessage.setSeverity(severity);
        return facesMessage;
    }

    public static FacesMessage getFacesMessage(FacesContext facesContext, Locale locale, String messageId, Object... params) {
        String summary = null;
        String detail = null;
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
                if (bundle.containsKey(messageId)) {
                    summary = bundle.getString(messageId);
                }
            }
            catch (MissingResourceException e) {
                // No Op
            }
        }

        // fallback to default Faces bundle
        if (summary == null) {
            try {
                bundle = getBundle(DEFAULT_BUNDLE_BASENAME, locale, currentClassLoader, facesContext);
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

    public static String getMessage(FacesContext facesContext, String messageId, Object... params) {
        return getMessage(facesContext, LocaleUtils.getCurrentLocale(facesContext), messageId, params);
    }

    public static String getMessage(FacesContext facesContext, Locale locale, String messageId, Object... params) {
        String summary = null;
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
                if (bundle.containsKey(messageId)) {
                    summary = bundle.getString(messageId);
                }
            }
            catch (MissingResourceException e) {
                // No Op
            }
        }

        // fallback to default Faces bundle
        if (summary == null) {
            try {
                bundle = getBundle(DEFAULT_BUNDLE_BASENAME, locale, currentClassLoader, facesContext);
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
        if ((params == null || params.length == 0) || LangUtils.isBlank(message)) {
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

    private static ResourceBundle getBundle(String baseName, Locale locale, ClassLoader classLoader,
                FacesContext facesContext) {

        ClassLoader primeFacesClassLoader = MessageFactory.class.getClassLoader();

        if (PRIMEFACES_BUNDLE_BASENAME.equals(baseName)) {
            if (!primeFacesClassLoader.equals(classLoader)) {
                return ResourceBundle.getBundle(baseName, locale, classLoader, new PrimeFacesControl(primeFacesClassLoader));
            }
        }
        else if (DEFAULT_BUNDLE_BASENAME.equals(baseName)) {

            ClassLoader facesImplClassLoader = getFacesImplClassLoader(facesContext);

            if (!facesImplClassLoader.equals(classLoader)) {
                return ResourceBundle.getBundle(baseName, locale, classLoader, new PrimeFacesControl(facesImplClassLoader));
            }
        }

        return ResourceBundle.getBundle(baseName, locale, classLoader, new PrimeFacesControl(primeFacesClassLoader));
    }

    private static ClassLoader getFacesImplClassLoader(FacesContext facesContext) {
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
     * Custom ResourceBundle.Control to handle OSGI classloader issues.
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

            ResourceBundle resourceBundle = super.newBundle(baseName, locale, format, classLoader, reload);

            // If the ResourceBundle cannot be found with the default class loader (usually the thread context class
            // loader or TCCL), try to find it with the OSGi bundle's class loader. Since default i18n files are
            // included inside of the jar/bundle that provides them, default i18n files are not visible to the TCCL in
            // an OSGi environment. Instead, i18n files are only visible to the class loader of the OSGi bundle that
            // they are included in.
            if (resourceBundle == null && !osgiBundleClassLoader.equals(classLoader)) {
                resourceBundle = super.newBundle(baseName, locale, format, osgiBundleClassLoader, reload);
            }

            return resourceBundle;
        }
    }
}

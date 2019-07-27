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
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
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
    }

    public static FacesMessage getMessage(String messageId, FacesMessage.Severity severity, Object[] params) {
        FacesMessage facesMessage = getMessage(LocaleUtils.getCurrentLocale(), messageId, params);
        facesMessage.setSeverity(severity);

        return facesMessage;
    }

    public static FacesMessage getMessage(Locale locale, String messageId, Object[] params) {
        String summary = null;
        String detail = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Application application = facesContext.getApplication();
        String userBundleName = application.getMessageBundle();
        ResourceBundle bundle = null;
        ClassLoader currentClassLoader = LangUtils.getCurrentClassLoader(application.getClass());

        //try user defined bundle first
        if (userBundleName != null) {
            try {
                bundle = getBundle(userBundleName, locale, currentClassLoader, facesContext);
                summary = bundle.getString(messageId);
            }
            catch (MissingResourceException e) {
                // No Op
            }
        }

        //try primefaces bundle
        if (summary == null) {
            try {
                bundle = getBundle(PRIMEFACES_BUNDLE_BASENAME, locale, currentClassLoader, facesContext);
                if (bundle == null) {
                    throw new NullPointerException();
                }
                summary = bundle.getString(messageId);
            }
            catch (MissingResourceException e) {
                // No Op
            }
        }

        //fallback to default jsf bundle
        if (summary == null) {
            try {
                bundle = getBundle(DEFAULT_BUNDLE_BASENAME, locale, currentClassLoader, facesContext);
                if (bundle == null) {
                    throw new NullPointerException();
                }
                summary = bundle.getString(messageId);
            }
            catch (MissingResourceException e) {
                // No Op
            }
        }

        summary = getFormattedText(locale, summary, params);

        try {
            detail = getFormattedText(locale, bundle.getString(messageId + DEFAULT_DETAIL_SUFFIX), params);
        }
        catch (MissingResourceException e) {
            // NoOp
        }

        return new FacesMessage(summary, detail);
    }

    public static String getMessage(String messageId, Object[] params) {
        FacesMessage message = getMessage(LocaleUtils.getCurrentLocale(), messageId, params);

        return message.getSummary();
    }

    public static String getFormattedText(Locale locale, String message, Object[] params) {
        MessageFormat messageFormat = null;

        if (params == null || message == null) {
            return message;
        }

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

        if (PRIMEFACES_BUNDLE_BASENAME.equals(baseName)) {

            ClassLoader primeFacesClassLoader = MessageFactory.class.getClassLoader();

            if (!primeFacesClassLoader.equals(classLoader)) {
                return ResourceBundle.getBundle(baseName, locale, classLoader,
                        new OSGiFriendlyControl(primeFacesClassLoader));
            }
        }
        else if (DEFAULT_BUNDLE_BASENAME.equals(baseName)) {

            ClassLoader jsfImplClassLoader = getJSFImplClassLoader(facesContext);

            if (!jsfImplClassLoader.equals(classLoader)) {
                return ResourceBundle.getBundle(baseName, locale, classLoader,
                        new OSGiFriendlyControl(jsfImplClassLoader));
            }
        }

        return ResourceBundle.getBundle(baseName, locale, classLoader);
    }

    private static ClassLoader getJSFImplClassLoader(FacesContext facesContext) {

        Class<? extends FacesContext> facesContextImplClass = FacesContext.class;
        facesContext = getWrappedFacesContextImpl(facesContext);

        if (facesContext != null) {
            facesContextImplClass = facesContext.getClass();
        }

        return facesContextImplClass.getClassLoader();
    }

    private static FacesContext getWrappedFacesContextImpl(FacesContext facesContext) {

        if (!(facesContext instanceof FacesContextWrapper)) {
            return facesContext;
        }

        FacesContextWrapper facesContextWrapper = (FacesContextWrapper) facesContext;
        FacesContext wrappedFacesContext = facesContextWrapper.getWrapped();

        if (wrappedFacesContext == null || FacesContext.class.equals(wrappedFacesContext.getClass())) {
            return facesContext;
        }

        return getWrappedFacesContextImpl(wrappedFacesContext);
    }

    private static final class OSGiFriendlyControl extends ResourceBundle.Control {

        private final ClassLoader osgiBundleClassLoader;

        public OSGiFriendlyControl(ClassLoader osgiBundleClassLoader) {
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

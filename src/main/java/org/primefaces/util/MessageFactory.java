/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        FacesMessage facesMessage = getMessage(getLocale(), messageId, params);
        facesMessage.setSeverity(severity);

        return facesMessage;
    }

    public static FacesMessage getMessage(Locale locale, String messageId, Object params[]) {
        String summary = null;
        String detail = null;
        Application application = FacesContext.getCurrentInstance().getApplication();
        String userBundleName = application.getMessageBundle();
        ResourceBundle bundle = null;
        ClassLoader currentClassLoader = getCurrentClassLoader(application);

        //try user defined bundle first
        if (userBundleName != null) {
            try {
                bundle = getBundle(userBundleName, locale, currentClassLoader);
                summary = bundle.getString(messageId);
            }
            catch (MissingResourceException e) {
                // No Op
            }
        }

        //try primefaces bundle
        if (summary == null) {
            try {
                bundle = getBundle(PRIMEFACES_BUNDLE_BASENAME, locale, currentClassLoader);
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
                bundle = getBundle(DEFAULT_BUNDLE_BASENAME, locale, currentClassLoader);
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

    public static String getMessage(String messageId, Object params[]) {
        FacesMessage message = getMessage(getLocale(), messageId, params);

        return message.getSummary();
    }

    public static String getFormattedText(Locale locale, String message, Object params[]) {
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

    protected static ClassLoader getCurrentClassLoader(Object clazz) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        if (loader == null) {
            loader = clazz.getClass().getClassLoader();
        }
        return loader;
    }

    protected static Locale getLocale() {
        Locale locale = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();

        if (facesContext != null && facesContext.getViewRoot() != null) {
            locale = facesContext.getViewRoot().getLocale();

            if (locale == null) {
                locale = Locale.getDefault();
            }
        }
        else {
            locale = Locale.getDefault();
        }

        return locale;
    }

    private static ResourceBundle getBundle(String baseName, Locale locale, ClassLoader classLoader) {

        if (OSGiFriendlyControl.OSGI_ENVIRONMENT_DETECTED) {

            if (PRIMEFACES_BUNDLE_BASENAME.equals(baseName)) {
                return ResourceBundle.getBundle(baseName, locale, classLoader,
                        OSGiFriendlyControls.OSGI_FRIENDLY_PRIMEFACES_CONTROL);
            }
            else if (DEFAULT_BUNDLE_BASENAME.equals(baseName)) {
                return ResourceBundle.getBundle(baseName, locale, classLoader,
                        OSGiFriendlyControls.OSGI_FRIENDLY_JSF_CONTROL);
            }
            else {
                return ResourceBundle.getBundle(baseName, locale, classLoader);
            }
        }
        else {
            return ResourceBundle.getBundle(baseName, locale, classLoader);
        }
    }

    private static final class OSGiFriendlyControl extends ResourceBundle.Control {

        private final static Logger logger = Logger.getLogger(OSGiFriendlyControl.class.getName());

        private static final boolean OSGI_ENVIRONMENT_DETECTED;

        static {

            boolean osgiEnvironmentDetected = false;

            try {

                Class<?> frameworkUtilClass = Class.forName("org.osgi.framework.FrameworkUtil");
                Method getBundleMethod = frameworkUtilClass.getMethod("getBundle", Class.class);
                Object currentBundle = getBundleMethod.invoke(null, OSGiFriendlyControl.class);

                if (currentBundle != null) {
                    osgiEnvironmentDetected = true;
                }
            }
            catch (ClassNotFoundException | NoClassDefFoundError e) {
                // Do nothing.
            }
            catch (Throwable t) {
                logger.log(Level.SEVERE, "An unexpected error occurred when attempting to detect OSGi:", t);
            }

            OSGI_ENVIRONMENT_DETECTED = osgiEnvironmentDetected;
        }

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

    private static final class OSGiFriendlyControls {

        // Class initialization is lazy and thread-safe. For more details on this pattern, see
        // http://stackoverflow.com/questions/7420504/threading-lazy-initialization-vs-static-lazy-initialization and
        // http://docs.oracle.com/javase/specs/jls/se7/html/jls-12.html#jls-12.4.2
        private static final ResourceBundle.Control OSGI_FRIENDLY_PRIMEFACES_CONTROL =
                new OSGiFriendlyControl(MessageFactory.class.getClassLoader());
        private static final ResourceBundle.Control OSGI_FRIENDLY_JSF_CONTROL =
                new OSGiFriendlyControl(getJSFImplClassLoader());

        private OSGiFriendlyControls() {
            throw new AssertionError();
        }

        private static ClassLoader getJSFImplClassLoader() {

            Class<? extends FacesContext> facesContextImplClass = FacesContext.class;
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext = getWrappedFacesContextImpl(facesContext);

            if (facesContext != null) {
                facesContextImplClass = facesContext.getClass();
            }

            return facesContextImplClass.getClassLoader();
        }

        private static FacesContext getWrappedFacesContextImpl(FacesContext facesContext) {

            if (facesContext == null || !(facesContext instanceof FacesContextWrapper)) {
                return facesContext;
            }

            FacesContextWrapper facesContextWrapper = (FacesContextWrapper) facesContext;
            FacesContext wrappedFacesContext = facesContextWrapper.getWrapped();

            if (wrappedFacesContext == null || FacesContext.class.equals(wrappedFacesContext.getClass())) {
                return facesContext;
            }

            return getWrappedFacesContextImpl(wrappedFacesContext);
        }
    }
}

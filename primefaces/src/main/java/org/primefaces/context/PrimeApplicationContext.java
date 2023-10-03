/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.primefaces.application.PropertyDescriptorResolver;
import org.primefaces.cache.CacheProvider;
import org.primefaces.cache.DefaultCacheProvider;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.export.*;
import org.primefaces.component.export.Exporter;
import org.primefaces.component.fileupload.FileUploadDecoder;
import org.primefaces.component.treetable.TreeTable;
import org.primefaces.component.treetable.export.*;
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.config.PrimeEnvironment;
import org.primefaces.metadata.transformer.MetadataTransformer;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.util.Lazy;
import org.primefaces.util.MapBuilder;
import org.primefaces.validate.bean.ClientValidationConstraint;
import org.primefaces.virusscan.VirusScannerService;
import org.primefaces.webapp.FileUploadChunksServlet;

/**
 * A {@link PrimeApplicationContext} is a contextual store for the current application.
 *
 * It can be accessed via:
 * <blockquote>
 * PrimeApplicationContext.getCurrentInstance(context)
 * </blockquote>
 */
public class PrimeApplicationContext {

    public static final String INSTANCE_KEY = PrimeApplicationContext.class.getName();

    private static final Logger LOGGER = Logger.getLogger(PrimeApplicationContext.class.getName());

    private final PrimeEnvironment environment;
    private final PrimeConfiguration config;
    private final ClassLoader applicationClassLoader;
    private final Map<Class<?>, Map<String, Object>> enumCacheMap;
    private final Map<Class<?>, Map<String, Object>> constantsCacheMap;
    private final Map<Class<? extends UIComponent>, Map<String, Class<? extends Exporter<?>>>> exporters;
    private final Map<String, ClientValidationConstraint> beanValidationClientConstraintMapping;
    private final List<MetadataTransformer> metadataTransformers;

    private final Lazy<ValidatorFactory> validatorFactory;
    private final Lazy<Validator> validator;
    private final Lazy<CacheProvider> cacheProvider;
    private final Lazy<VirusScannerService> virusScannerService;
    private FileTypeDetector fileTypeDetector;
    private FileUploadDecoder fileUploadDecoder;
    private String fileUploadResumeUrl;
    private PropertyDescriptorResolver propertyDescriptorResolver;

    public PrimeApplicationContext(FacesContext facesContext) {
        environment = new PrimeEnvironment(facesContext);
        config = new PrimeConfiguration(facesContext, environment);

        enumCacheMap = new ConcurrentHashMap<>();
        constantsCacheMap = new ConcurrentHashMap<>();
        exporters = new ConcurrentHashMap<>();
        beanValidationClientConstraintMapping = new ConcurrentHashMap<>();
        metadataTransformers = new CopyOnWriteArrayList<>();

        ClassLoader classLoader = null;
        Object context = facesContext.getExternalContext().getContext();
        if (context != null) {
            try {
                // Reflectively call getClassLoader() on the context in order to be compatible with both the Portlet 3.0
                // API and the Servlet API without depending on the Portlet API directly.
                Method getClassLoaderMethod = context.getClass().getMethod("getClassLoader");

                if (getClassLoaderMethod != null) {
                    classLoader = (ClassLoader) getClassLoaderMethod.invoke(context);
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
        if (classLoader == null) {
            classLoader = LangUtils.getContextClassLoader();
        }

        applicationClassLoader = classLoader;

        if (config.isBeanValidationEnabled()) {
            validatorFactory = new Lazy<>(Validation::buildDefaultValidatorFactory);
            validator = new Lazy<>(() -> validatorFactory.get().getValidator());
        }
        else {
            validatorFactory = null;
            validator = null;
        }

        virusScannerService = new Lazy<>(() -> new VirusScannerService(applicationClassLoader));

        cacheProvider = new Lazy<>(() -> {
            String cacheProviderConfigValue = FacesContext.getCurrentInstance().getExternalContext()
                    .getInitParameter(Constants.ContextParams.CACHE_PROVIDER);
            if (cacheProviderConfigValue == null) {
                return new DefaultCacheProvider();
            }
            else {
                try {
                    Class<? extends CacheProvider> cacheProviderClazz = LangUtils.loadClassForName(cacheProviderConfigValue);
                    return cacheProviderClazz.getConstructor().newInstance();
                }
                catch (NoSuchMethodException | ClassNotFoundException | InstantiationException
                        | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    throw new FacesException(ex);
                }
            }
        });

        resolveFileUploadDecoder();

        resolveFileUploadResumeUrl(facesContext);

        resolveFileTypeDetector();

        registerDefaultExporters();

        resolvePropertyDescriptorResolver();
    }

    private void registerDefaultExporters() {
        MapBuilder.builder(exporters)
                .put(DataTable.class, MapBuilder.<String, Class<? extends Exporter<?>>>builder()
                        .put("xls", DataTableExcelExporter.class)
                        .put("pdf", DataTablePDFExporter.class)
                        .put("csv", DataTableCSVExporter.class)
                        .put("xml", DataTableXMLExporter.class)
                        .put("xlsx", DataTableExcelXExporter.class)
                        .put("xlsxstream", DataTableExcelXStreamExporter.class)
                        .build())
                .put(TreeTable.class, MapBuilder.<String, Class<? extends Exporter<?>>>builder()
                        .put("xls", TreeTableExcelExporter.class)
                        .put("pdf", TreeTablePDFExporter.class)
                        .put("csv", TreeTableCSVExporter.class)
                        .put("xml", TreeTableXMLExporter.class)
                        .put("xlsx", TreeTableExcelXExporter.class)
                        .put("xlsxstream", TreeTableExcelXStreamExporter.class)
                        .build());
    }

    private void resolveFileTypeDetector() {
        ServiceLoader<FileTypeDetector> loader = ServiceLoader.load(FileTypeDetector.class, applicationClassLoader);

        // collect all first to avoid concurrency issues #8797
        List<FileTypeDetector> detectors = new ArrayList<>();
        for (FileTypeDetector detector : loader) {
            detectors.add(detector);
        }

        fileTypeDetector = new FileTypeDetector() {

            @Override
            public String probeContentType(Path path) throws IOException {
                for (FileTypeDetector detector : detectors) {
                    String result = detector.probeContentType(path);
                    if (result != null) {
                        return result;
                    }
                }

                // fallback to default
                return Files.probeContentType(path);
            }
        };
    }

    private void resolveFileUploadResumeUrl(FacesContext facesContext) {
        Object request = facesContext.getExternalContext().getRequest();
        if (request instanceof HttpServletRequest) {
            ServletContext servletContext = ((HttpServletRequest) request).getServletContext();
            if (servletContext == null) {
                return;
            }
            Map<String, ? extends ServletRegistration> servletRegistrations = servletContext.getServletRegistrations();
            if (servletRegistrations == null || servletRegistrations.isEmpty()) {
                return;
            }

            fileUploadResumeUrl = servletRegistrations.values().stream()
                    .filter(s -> FileUploadChunksServlet.class.getName().equals(s.getClassName()))
                    .findFirst()
                    .map(ServletRegistration::getMappings)
                    .map(Collection::stream)
                    .flatMap(Stream::findFirst)
                    .map(s -> facesContext.getExternalContext().getApplicationContextPath() + s)
                    .orElse(null);
        }
    }

    private void resolveFileUploadDecoder() {
        String uploader = config.getUploader();
        if ("auto".equals(uploader)) {
            uploader = "native"; // default since JSF 2.2+
        }

        String finalUploader = uploader;
        fileUploadDecoder = StreamSupport.stream(ServiceLoader.load(FileUploadDecoder.class, applicationClassLoader).spliterator(), false)
                .filter(d -> d.getName().equals(finalUploader))
                .findFirst()
                .orElseThrow(() -> new FacesException("FileUploaderDecoder '" + finalUploader + "' not found"));
    }

    private void resolvePropertyDescriptorResolver() {
        if (LangUtils.isBlank(config.getPropertyDescriptorResolver())) {
            propertyDescriptorResolver = new PropertyDescriptorResolver.DefaultResolver();
        }
        else {
            try {
                propertyDescriptorResolver = (PropertyDescriptorResolver)
                        Class.forName(config.getPropertyDescriptorResolver()).getConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new FacesException(e);
            }
        }
    }

    public static PrimeApplicationContext getCurrentInstance(FacesContext facesContext) {
        return getCurrentInstance(facesContext, true);
    }

    public static PrimeApplicationContext getCurrentInstance(FacesContext facesContext, boolean create) {
        if (facesContext == null || facesContext.getExternalContext() == null) {
            return null;
        }

        PrimeApplicationContext applicationContext =
                (PrimeApplicationContext) facesContext.getExternalContext().getApplicationMap().get(INSTANCE_KEY);

        if (create && applicationContext == null) {
            applicationContext = new PrimeApplicationContext(facesContext);
            setCurrentInstance(applicationContext, facesContext);
        }

        return applicationContext;
    }

    public static PrimeApplicationContext getCurrentInstance(ServletContext context) {
        return (PrimeApplicationContext) context.getAttribute(INSTANCE_KEY);
    }

    public static void setCurrentInstance(PrimeApplicationContext context, FacesContext facesContext) {
        facesContext.getExternalContext().getApplicationMap().put(INSTANCE_KEY, context);

        if (facesContext.getExternalContext().getContext() instanceof ServletContext) {
            ((ServletContext) facesContext.getExternalContext().getContext()).setAttribute(INSTANCE_KEY, context);
        }
    }

    public PrimeEnvironment getEnvironment() {
        return environment;
    }

    public PrimeConfiguration getConfig() {
        return config;
    }

    public ValidatorFactory getValidatorFactory() {
        return validatorFactory == null ? null : validatorFactory.get();
    }

    public CacheProvider getCacheProvider() {
        return cacheProvider.get();
    }

    public Map<Class<?>, Map<String, Object>> getEnumCacheMap() {
        return enumCacheMap;
    }

    public Map<Class<?>, Map<String, Object>> getConstantsCacheMap() {
        return constantsCacheMap;
    }

    public Validator getValidator() {
        return validator == null ? null : validator.get();
    }

    public VirusScannerService getVirusScannerService() {
        return virusScannerService.get();
    }

    public FileTypeDetector getFileTypeDetector() {
        return fileTypeDetector;
    }

    public void release() {
        if (environment != null) {
            if (validatorFactory != null && validatorFactory.isInitialized() && validatorFactory.get() != null) {
                validatorFactory.get().close();
            }
        }
    }

    public FileUploadDecoder getFileUploadDecoder() {
        return fileUploadDecoder;
    }

    public String getFileUploadResumeUrl() {
        return fileUploadResumeUrl;
    }

    public Map<Class<? extends UIComponent>, Map<String, Class<? extends Exporter<?>>>> getExporters() {
        return exporters;
    }

    public Map<String, ClientValidationConstraint> getBeanValidationClientConstraintMapping() {
        return beanValidationClientConstraintMapping;
    }

    public List<MetadataTransformer> getMetadataTransformers() {
        return metadataTransformers;
    }

    public PropertyDescriptorResolver getPropertyDescriptorResolver() {
        return propertyDescriptorResolver;
    }
}

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
package org.primefaces.cdk.impl.taglib;

import org.primefaces.cdk.api.FacesTagHandler;
import org.primefaces.cdk.api.Function;
import org.primefaces.cdk.api.Property;
import org.primefaces.cdk.impl.container.BehaviorInfo;
import org.primefaces.cdk.impl.container.ComponentInfo;
import org.primefaces.cdk.impl.container.FunctionInfo;
import org.primefaces.cdk.impl.container.TagHandlerInfo;
import org.primefaces.cdk.impl.container.ValidatorInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.behavior.FacesBehavior;
import jakarta.faces.validator.FacesValidator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

@Mojo(name = "generate-taglib",
        defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        threadSafe = true
)
public class TaglibMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Taglib output directory relative to project build directory
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}/META-INF")
    private File outputDirectory;

    /**
     * Taglib URI
     */
    @Parameter(required = true)
    private String uri;

    /**
     * Taglib short name (prefix)
     */
    @Parameter(required = true)
    private String shortName;

    /**
     * Taglib display name
     */
    @Parameter(required = true)
    private String displayName;

    /**
     * Perform taglib XML generation
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("PrimeFaces Taglib Generator - Starting in " + outputDirectory);
        getLog().info("Taglib Name: " + displayName);
        getLog().info("Taglib URI: " + uri);
        getLog().info("Taglib Short Name: " + shortName);

        try {
            // Create output directory if it doesn't exist
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }

            // Find all component and behavior classes
            List<Class<?>> componentClasses = findAnnotatedClasses(FacesComponent.class);
            List<Class<?>> behaviorClasses = findAnnotatedClasses(FacesBehavior.class);
            List<Class<?>> validatorClasses = findAnnotatedClasses(FacesValidator.class);
            List<Class<?>> tagHandlerClasses = findAnnotatedClasses(FacesTagHandler.class);
            List<FunctionInfo> functionInfos = findFunctionInfos();

            getLog().info("Found " + componentClasses.size() + " component classes");
            getLog().info("Found " + behaviorClasses.size() + " behavior classes");
            getLog().info("Found " + validatorClasses.size() + " validator classes");
            getLog().info("Found " + tagHandlerClasses.size() + " TagHandler classes");

            getLog().info("Processing components...");
            List<ComponentInfo> componentInfos = componentClasses.stream()
                    .map(clazz -> processComponentClass(clazz))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(ComponentInfo::getTagName))
                    .collect(Collectors.toList());

            getLog().info("Processing behaviors...");
            List<BehaviorInfo> behaviorInfos = behaviorClasses.stream()
                    .map(clazz -> processBehaviorClass(clazz))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(BehaviorInfo::getTagName))
                    .collect(Collectors.toList());

            getLog().info("Processing validators...");
            List<ValidatorInfo> validatorInfos = validatorClasses.stream()
                    .map(clazz -> processValidatorClass(clazz))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(ValidatorInfo::getTagName))
                    .collect(Collectors.toList());

            getLog().info("Processing tagHandlers...");
            List<TagHandlerInfo> tagHandlerInfos = tagHandlerClasses.stream()
                    .map(clazz -> processTagHandlerClass(clazz))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(TagHandlerInfo::getTagName))
                    .collect(Collectors.toList());

            // Generate the taglib XML
            Document document = generateTaglibXml(functionInfos, componentInfos, behaviorInfos, validatorInfos, tagHandlerInfos);

            // Save the taglib XML to file
            File taglibFile = new File(outputDirectory, shortName + ".taglib.xml");
            writeTaglibXml(document, taglibFile);

            getLog().info("Generated " + taglibFile.getAbsolutePath());
            getLog().info("Taglib contains " + componentInfos.size() + " components and " + behaviorInfos.size() + " behaviors");
        }
        catch (Exception e) {
            getLog().error("Error generating taglib XML", e);
            throw new MojoExecutionException("Error generating taglib XML", e);
        }
    }

    private List<Class<?>> findAnnotatedClasses(Class<? extends Annotation> annotation) throws MojoExecutionException {
        getLog().info("Scanning for @" + annotation.getSimpleName() + " annotated classes...");

        try {
            Collection<Class<?>> projectClasses = getAllProjectClasses();

            List<Class<?>> foundClasses = new ArrayList<>();
            for (Class<?> c : projectClasses) {
                try {
                    if (c.getAnnotation(annotation) != null) {
                        foundClasses.add(c);
                    }
                }
                catch (Throwable e) {
                    // Skip classes that can't be processed due to missing dependencies
                    getLog().warn("Skipping class " + c.getName() + " during annotation scan: " + e.getMessage());
                }
            }

            Collection<String> classNames = foundClasses.stream().map(Class::getName).collect(Collectors.toList());
            getLog().info("Found classes: " + String.join(",", classNames));

            return foundClasses;
        }
        catch (Exception e) {
            getLog().error("Error while scanning", e);
            throw new MojoExecutionException("Error while scanning", e);
        }
    }

    private List<FunctionInfo> findFunctionInfos() throws MojoExecutionException {
        getLog().info("Scanning for @" + Function.class.getName() + " annotated methods...");

        try {
            Collection<Class<?>> projectClasses = getAllProjectClasses();
            List<FunctionInfo> allFunctions = new ArrayList<>();

            for (Class<?> clazz : projectClasses) {
                try {
                    List<FunctionInfo> functions = TaglibUtils.getFunctionInfos(clazz);
                    if (!functions.isEmpty()) {
                        allFunctions.addAll(functions);
                    }
                }
                catch (Throwable e) {
                    // Skip classes that can't be processed due to missing dependencies
                    getLog().debug("Skipping class " + clazz.getName() + " during function scan: " + e.getMessage());
                }
            }

            Collection<String> classNames = allFunctions.stream().map(FunctionInfo::getName).collect(Collectors.toList());
            getLog().info("Found functions: " + String.join(",", classNames));

            return allFunctions;
        }
        catch (Exception e) {
            getLog().error("Error while scanning", e);
            throw new MojoExecutionException("Error while scanning", e);
        }
    }

    private ComponentInfo processComponentClass(Class<?> componentClass) {
        try {
            getLog().debug("Processing @FacesComponent class: " + componentClass.getName());

            ComponentInfo componentInfo = TaglibUtils.getComponentInfo(componentClass);

            getLog().info("Processing component: " + componentClass.getName()
                    + ", type: " + componentInfo.getComponentType()
                    + ", tag: " + componentInfo.getTagName());

            return componentInfo;
        }
        catch (Exception e) {
            getLog().error("Error processing component class: " + componentClass.getName(), e);
            return null;
        }
    }

    private BehaviorInfo processBehaviorClass(Class<?> behaviorClass) {
        try {
            getLog().debug("Processing @FacesBehavior class: " + behaviorClass.getName());

            BehaviorInfo behaviorInfo = TaglibUtils.getBehaviorInfo(behaviorClass);

            getLog().info("Processing behavior: " + behaviorInfo.getBehaviorClass().getName()
                    + ", id: " + behaviorInfo.getBehaviorId()
                    + ", renderer: " + behaviorInfo.getRendererType()
                    + ", tag: " + behaviorInfo.getTagName());

            return behaviorInfo;
        }
        catch (Exception e) {
            getLog().error("Error processing behavior class: " + behaviorClass.getName(), e);
            return null;
        }
    }

    private ValidatorInfo processValidatorClass(Class<?> validatorClass) {
        try {
            getLog().debug("Processing @FacesValidator class: " + validatorClass.getName());

            ValidatorInfo validatorInfo = TaglibUtils.getValidatorInfo(validatorClass);

            getLog().info("Processing validator: " + validatorInfo.getValidatorClass().getName()
                    + ", id: " + validatorInfo.getValidatorId()
                    + ", tag: " + validatorInfo.getTagName());

            return validatorInfo;
        }
        catch (Exception e) {
            getLog().error("Error processing validator class: " + validatorClass.getName(), e);
            return null;
        }
    }

    private TagHandlerInfo processTagHandlerClass(Class<?> tagHandlerClass) {
        try {
            getLog().debug("Processing @FacesTagHandler class: " + tagHandlerClass.getName());

            TagHandlerInfo behaviorInfo = TaglibUtils.getTagHandlerInfo(tagHandlerClass);

            getLog().info("Processing tagHandler: " + behaviorInfo.getClazz().getName()
                    + ", tag: " + behaviorInfo.getTagName());

            return behaviorInfo;
        }
        catch (Exception e) {
            getLog().error("Error processing tagHandler class: " + tagHandlerClass.getName(), e);
            return null;
        }
    }

    private Document generateTaglibXml(List<FunctionInfo> functionInfos, List<ComponentInfo> componentInfos, List<BehaviorInfo> behaviorInfos,
                                       List<ValidatorInfo> validatorInfos, List<TagHandlerInfo> tagHandlerInfos) {
        Document document = DocumentHelper.createDocument();
        Element faceletTaglib = document.addElement("facelet-taglib", "https://jakarta.ee/xml/ns/jakartaee")
                .addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance")
                .addAttribute("xsi:schemaLocation", "https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-facelettaglibrary_4_0.xsd")
                .addAttribute("version", "4.0");

        faceletTaglib.addElement("namespace").addText(uri);
        faceletTaglib.addElement("short-name").addText(shortName);
        faceletTaglib.addElement("display-name").addText(displayName);

        for (FunctionInfo functionInfo : functionInfos) {
            Element function = faceletTaglib.addElement("function");
            function.addElement("function-name").addText(functionInfo.getName());
            function.addElement("function-class").addText(functionInfo.getClazz());
            function.addElement("function-signature").addText(functionInfo.getSignature());
            if (functionInfo.getDescription() != null && !functionInfo.getDescription().isEmpty()) {
                function.addElement("description").addText(functionInfo.getDescription());
            }
        }

        // Add a tag for each component
        for (ComponentInfo componentInfo : componentInfos) {
            Element tag = faceletTaglib.addElement("tag");
            if (componentInfo.getDescription() != null) {
                tag.addElement("description").addCDATA(componentInfo.getDescription());
            }
            tag.addElement("tag-name").addText(componentInfo.getTagName());
            Element component = tag.addElement("component");
            component.addElement("component-type").addText(componentInfo.getComponentType());
            if (componentInfo.getRendererType() != null) {
                component.addElement("renderer-type").addText(componentInfo.getRendererType());
            }
            if (componentInfo.getHandlerClass() != null) {
                component.addElement("handler-class").addText(componentInfo.getHandlerClass().getName());
            }

            // Add attributes for each property
            writeProperties(tag, componentInfo.getProperties());
        }

        // Add a tag for each behavior
        for (BehaviorInfo behaviorInfo : behaviorInfos) {
            Element tag = faceletTaglib.addElement("tag");
            if (behaviorInfo.getDescription() != null) {
                tag.addElement("description").addCDATA(behaviorInfo.getDescription());
            }
            tag.addElement("tag-name").addText(behaviorInfo.getTagName());
            Element behavior = tag.addElement("behavior");
            behavior.addElement("behavior-id").addText(behaviorInfo.getBehaviorId());

            if (behaviorInfo.getHandlerClass() != null) {
                behavior.addElement("handler-class").addText(behaviorInfo.getHandlerClass().getName());
            }

            // Add renderer-type if present
            if (behaviorInfo.getRendererType() != null && !behaviorInfo.getRendererType().isEmpty()) {
                behavior.addElement("behavior-renderer-type").addText(behaviorInfo.getRendererType());
            }

            writeProperties(tag, behaviorInfo.getProperties());
        }

        // Add a tag for each validator
        for (ValidatorInfo validatorInfo : validatorInfos) {
            Element tag = faceletTaglib.addElement("tag");
            if (validatorInfo.getDescription() != null) {
                tag.addElement("description").addCDATA(validatorInfo.getDescription());
            }
            tag.addElement("tag-name").addText(validatorInfo.getTagName());
            Element validator = tag.addElement("validator");
            validator.addElement("validator-id").addText(validatorInfo.getValidatorId());

            if (validatorInfo.getHandlerClass() != null) {
                validator.addElement("handler-class").addText(validatorInfo.getHandlerClass().getName());
            }

            writeProperties(tag, validatorInfo.getProperties());
        }

        // Add a tag for each tagHandler
        for (TagHandlerInfo tagHandlerInfo : tagHandlerInfos) {
            Element tag = faceletTaglib.addElement("tag");
            if (tagHandlerInfo.getDescription() != null) {
                tag.addElement("description").addCDATA(tagHandlerInfo.getDescription());
            }
            tag.addElement("tag-name").addText(tagHandlerInfo.getTagName());

            tag.addElement("handler-class").addText(tagHandlerInfo.getClazz().getName());

            writeProperties(tag, tagHandlerInfo.getProperties());
        }

        return document;
    }

    void writeProperties(Element tag, Map<String, Property> properties) {
        for (Map.Entry<String, Property> propertyInfo : properties.entrySet()) {
            Property property = propertyInfo.getValue();
            if (property.internal()) {
                continue;
            }

            Element attribute = tag.addElement("attribute");
            String description = property.description() == null ? "" : property.description();
            if (property.implicitDefaultValue() != null && !property.implicitDefaultValue().isEmpty()) {
                description += "Default is " + property.implicitDefaultValue() + ".";
            }
            else if (property.defaultValue() != null && !property.defaultValue().isEmpty()) {
                description += "Default is " + property.defaultValue() + ".";
            }
            attribute.addElement("description").addCDATA(description);
            attribute.addElement("name").addText(propertyInfo.getKey());
            attribute.addElement("required").addText(String.valueOf(property.required()));
            attribute.addElement("type").addText(getAttributeType(property.type()).getName());
        }
    }

    public Class<?> getAttributeType(Class<?> type) {
        Map<Class<?>, Class<?>> primitivesToWrapper = Map.of(
                boolean.class, Boolean.class,
                int.class, Integer.class,
                long.class, Long.class,
                double.class, Double.class,
                float.class, Float.class,
                short.class, Short.class,
                byte.class, Byte.class,
                char.class, Character.class,
                void.class, Void.class
        );

        return primitivesToWrapper.getOrDefault(type, type);
    }

    @SuppressWarnings("unchecked")
    private <T> Class<? extends T> loadClass(String className) throws MojoExecutionException {
        try {
            return (Class<? extends T>) getProjectClassLoader(project).loadClass(className);
        }
        catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Could not load class " + className, e);
        }
    }

    private ClassLoader getProjectClassLoader(MavenProject project) throws MojoExecutionException {
        try {
            URL url = new File(project.getBuild().getOutputDirectory()).toURI().toURL();
            return new URLClassLoader(new URL[]{url}, this.getClass().getClassLoader());
        }
        catch (Exception e) {
            getLog().debug("Error getting class loader", e);
            return Thread.currentThread().getContextClassLoader();
        }
    }

    private Collection<Class<?>> getAllProjectClasses() {
        String b = project.getBuild().getOutputDirectory();

        try (Stream<Path> stream = Files.walk(Paths.get(b))) {
            final Collection<Class<?>> classes = new ArrayList<>();
            stream.filter(f -> f.toString().endsWith(".class"))
                    .forEach(f -> {
                        String currentFile = f.toString();
                        currentFile = currentFile.substring(b.length());
                        currentFile = currentFile.replace(".class", "");
                        currentFile = currentFile.replace("/", ".");
                        currentFile = currentFile.replace("\\", ".");
                        if (currentFile.startsWith(".")) {
                            currentFile = currentFile.substring(1);
                        }
                        try {
                            Class<?> clazz = loadClass(currentFile);
                            classes.add(clazz);
                        }
                        catch (Throwable e) {
                            getLog().warn("Skipping class " + currentFile + " due to missing dependencies: " + e.getMessage());
                        }
                    });
            return classes;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeTaglibXml(Document document, File taglibFile) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setNewLineAfterDeclaration(true);
        XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(taglibFile), StandardCharsets.UTF_8), format);
        writer.write(document);
        writer.close();
    }
}
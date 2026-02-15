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
package org.primefaces.cdk.impl.subclass;

import org.primefaces.cdk.impl.container.BehaviorEventInfo;
import org.primefaces.cdk.impl.container.FacetInfo;
import org.primefaces.cdk.impl.container.PropertyInfo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link HierarchyScanner}.
 * Uses Google's compile-testing library to spin up an in-memory javac with a
 * thin {@link CapturingProcessor} that drives the scanner and captures results.
 */
class HierarchyScannerTest {

    @Test
    void isGetterName_recognisesGetPrefix() {
        Assertions.assertTrue(HierarchyScanner.isGetterName("getValue"));
        Assertions.assertTrue(HierarchyScanner.isGetterName("getStyleClass"));
    }

    @Test
    void isGetterName_recognisesIsPrefix() {
        Assertions.assertTrue(HierarchyScanner.isGetterName("isDisabled"));
        Assertions.assertTrue(HierarchyScanner.isGetterName("isRendered"));
    }

    @Test
    void isGetterName_rejectsNonGetters() {
        Assertions.assertFalse(HierarchyScanner.isGetterName("setValue"));
        Assertions.assertFalse(HierarchyScanner.isGetterName("doSomething"));
        Assertions.assertFalse(HierarchyScanner.isGetterName("processAction"));
    }

    @Test
    void extractPropertyName_stripsGetPrefix() {
        Assertions.assertEquals("value",      HierarchyScanner.extractPropertyName("getValue"));
        Assertions.assertEquals("styleClass", HierarchyScanner.extractPropertyName("getStyleClass"));
    }

    @Test
    void extractPropertyName_stripsIsPrefix() {
        Assertions.assertEquals("disabled", HierarchyScanner.extractPropertyName("isDisabled"));
        Assertions.assertEquals("rendered", HierarchyScanner.extractPropertyName("isRendered"));
    }

    @Test
    void extractFacetName_stripsGetAndFacetSuffix() {
        Assertions.assertEquals("header", HierarchyScanner.extractFacetName("getHeaderFacet"));
        Assertions.assertEquals("footer", HierarchyScanner.extractFacetName("getFooterFacet"));
    }

    @Test
    void extractFacetName_withoutFacetSuffix_returnsPropertyName() {
        Assertions.assertEquals("content", HierarchyScanner.extractFacetName("getContent"));
    }

    private HierarchyScannerResult compile(String rootClassName, JavaFileObject... sources) {
        CapturingProcessor processor = new CapturingProcessor(rootClassName);
        Compilation compilation = Compiler.javac().withProcessors(processor).compile(sources);
        CompilationSubject.assertThat(compilation).succeeded();
        return processor.result;
    }

    /** Single class — {@code @Property} annotations are collected with correct name and type. */
    @Test
    void singleClass_propertyAnnotations_areCollected() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.example.SimpleBase",
                "package com.example;\n"
                        + "import org.primefaces.cdk.api.Property;\n"
                        + "public abstract class SimpleBase {\n"
                        + "    @Property(description = \"The label\")\n"
                        + "    public abstract String getLabel();\n"
                        + "    @Property(description = \"Is it closable?\")\n"
                        + "    public abstract boolean isClosable();\n"
                        + "}\n");

        HierarchyScannerResult result = compile("com.example.SimpleBase", source);

        Set<String> names = propertyNames(result);
        Assertions.assertTrue(names.contains("label"),    "expected property 'label'");
        Assertions.assertTrue(names.contains("closable"), "expected property 'closable'");
        Assertions.assertEquals(2, names.size(),          "expected exactly 2 properties");

        PropertyInfo label = findPropertyByName(result, "label");
        Assertions.assertNotNull(label);
        Assertions.assertEquals("The label",        label.getAnnotation().description());
        Assertions.assertEquals("java.lang.String", label.getTypeName());

        PropertyInfo closable = findPropertyByName(result, "closable");
        Assertions.assertNotNull(closable);
        Assertions.assertEquals("boolean", closable.getTypeName());
    }

    /** Parent and child compiled in the same round — inherited properties are merged. */
    @Test
    void parentChild_inheritedPropertiesAreMerged() {
        JavaFileObject parent = JavaFileObjects.forSourceString(
                "com.example.ParentBase",
                "package com.example;\n"
                        + "import org.primefaces.cdk.api.Property;\n"
                        + "public abstract class ParentBase {\n"
                        + "    @Property(description = \"Parent style\")\n"
                        + "    public abstract String getStyle();\n"
                        + "}\n");
        JavaFileObject child = JavaFileObjects.forSourceString(
                "com.example.ChildBase",
                "package com.example;\n"
                        + "import org.primefaces.cdk.api.Property;\n"
                        + "public abstract class ChildBase extends ParentBase {\n"
                        + "    @Property(description = \"Child value\")\n"
                        + "    public abstract String getValue();\n"
                        + "}\n");

        HierarchyScannerResult result = compile("com.example.ChildBase", parent, child);

        Assertions.assertTrue(propertyNames(result).contains("style"), "expected inherited 'style'");
        Assertions.assertTrue(propertyNames(result).contains("value"), "expected own 'value'");
    }

    /** Child re-declares a property with {@code hide=true} — child definition wins. */
    @Test
    void child_hideTrue_overridesParentDefinition() {
        JavaFileObject parent = JavaFileObjects.forSourceString(
                "com.example.ParentBase",
                "package com.example;\n"
                        + "import org.primefaces.cdk.api.Property;\n"
                        + "public abstract class ParentBase {\n"
                        + "    @Property(description = \"Parent widget var\")\n"
                        + "    public abstract String getWidgetVar();\n"
                        + "}\n");
        JavaFileObject child = JavaFileObjects.forSourceString(
                "com.example.ChildBase",
                "package com.example;\n"
                        + "import org.primefaces.cdk.api.Property;\n"
                        + "public abstract class ChildBase extends ParentBase {\n"
                        + "    @Property(description = \"Hidden\", hide = true)\n"
                        + "    public abstract String getWidgetVar();\n"
                        + "}\n");

        HierarchyScannerResult result = compile("com.example.ChildBase", parent, child);

        PropertyInfo widgetVar = findPropertyByName(result, "widgetVar");
        Assertions.assertNotNull(widgetVar, "widgetVar must be present");
        Assertions.assertTrue(widgetVar.getAnnotation().hide(), "child hide=true must win");
    }

    /** Interface {@code @Property} is picked up when no {@code PropertyKeys} enum is present. */
    @Test
    void annotatedInterface_propertiesCollected() {
        JavaFileObject iface = JavaFileObjects.forSourceString(
                "com.example.HasWidget",
                "package com.example;\n"
                        + "import org.primefaces.cdk.api.Property;\n"
                        + "public interface HasWidget {\n"
                        + "    @Property(description = \"The widget variable name\")\n"
                        + "    String getWidgetVar();\n"
                        + "}\n");
        JavaFileObject impl = JavaFileObjects.forSourceString(
                "com.example.WidgetBase",
                "package com.example;\n"
                        + "public abstract class WidgetBase implements HasWidget {\n"
                        + "}\n");

        HierarchyScannerResult result = compile("com.example.WidgetBase", iface, impl);

        Assertions.assertTrue(propertyNames(result).contains("widgetVar"));
    }

    /** {@code @Facet} annotations are collected and names are extracted correctly. */
    @Test
    void facetAnnotations_areCollected() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.example.PanelBase",
                "package com.example;\n"
                        + "import org.primefaces.cdk.api.Facet;\n"
                        + "public abstract class PanelBase {\n"
                        + "    @Facet(description = \"The panel header\")\n"
                        + "    public abstract Object getHeaderFacet();\n"
                        + "    @Facet(description = \"The panel footer\")\n"
                        + "    public abstract Object getFooterFacet();\n"
                        + "}\n");

        HierarchyScannerResult result = compile("com.example.PanelBase", source);

        Set<String> names = facetNames(result);
        Assertions.assertTrue(names.contains("header"), "expected facet 'header'");
        Assertions.assertTrue(names.contains("footer"), "expected facet 'footer'");
        Assertions.assertEquals(2, names.size());
    }

    /** Single {@code @FacesBehaviorEvent} is collected with correct name and flags. */
    @Test
    void behaviorEvent_singleAnnotation_isCollected() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.example.ClickBehaviorBase",
                "package com.example;\n"
                        + "import org.primefaces.cdk.api.FacesBehaviorEvent;\n"
                        + "import jakarta.faces.event.BehaviorEvent;\n"
                        + "@FacesBehaviorEvent(name = \"click\", event = BehaviorEvent.class, defaultEvent = true)\n"
                        + "public abstract class ClickBehaviorBase {\n"
                        + "}\n");

        HierarchyScannerResult result = compile("com.example.ClickBehaviorBase", source);

        List<BehaviorEventInfo> events = result.getBehaviorEvents();
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals("click", events.get(0).getName());
        Assertions.assertTrue(events.get(0).getAnnotation().defaultEvent());
    }

    /** {@code @FacesBehaviorEvents} container annotation — all repeated events are collected. */
    @Test
    void behaviorEvents_container_allEventsCollected() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.example.MultiEventBase",
                "package com.example;\n"
                        + "import org.primefaces.cdk.api.FacesBehaviorEvent;\n"
                        + "import org.primefaces.cdk.api.FacesBehaviorEvents;\n"
                        + "import jakarta.faces.event.BehaviorEvent;\n"
                        + "@FacesBehaviorEvents({\n"
                        + "    @FacesBehaviorEvent(name = \"select\",   event = BehaviorEvent.class, defaultEvent = true),\n"
                        + "    @FacesBehaviorEvent(name = \"unselect\", event = BehaviorEvent.class),\n"
                        + "    @FacesBehaviorEvent(name = \"toggle\",   event = BehaviorEvent.class, implicit = true)\n"
                        + "})\n"
                        + "public abstract class MultiEventBase {\n"
                        + "}\n");

        HierarchyScannerResult result = compile("com.example.MultiEventBase", source);

        Set<String> names = result.getBehaviorEvents().stream()
                .map(BehaviorEventInfo::getName).collect(Collectors.toSet());
        Assertions.assertTrue(names.contains("select"));
        Assertions.assertTrue(names.contains("unselect"));
        Assertions.assertTrue(names.contains("toggle"));
        Assertions.assertEquals(3, names.size());
        Assertions.assertTrue(findEventByName(result, "toggle").getAnnotation().implicit());
    }

    /** Child event with same name as parent event — child wins, no duplicate. */
    @Test
    void child_behaviorEvent_overridesParentByName() {
        JavaFileObject parent = JavaFileObjects.forSourceString(
                "com.example.ParentBehaviorBase",
                "package com.example;\n"
                        + "import org.primefaces.cdk.api.FacesBehaviorEvent;\n"
                        + "import jakarta.faces.event.BehaviorEvent;\n"
                        + "@FacesBehaviorEvent(name = \"click\", event = BehaviorEvent.class, description = \"parent click\")\n"
                        + "public abstract class ParentBehaviorBase {\n"
                        + "}\n");
        JavaFileObject child = JavaFileObjects.forSourceString(
                "com.example.ChildBehaviorBase",
                "package com.example;\n"
                        + "import org.primefaces.cdk.api.FacesBehaviorEvent;\n"
                        + "import jakarta.faces.event.BehaviorEvent;\n"
                        + "@FacesBehaviorEvent(name = \"click\", event = BehaviorEvent.class, description = \"child click\")\n"
                        + "public abstract class ChildBehaviorBase extends ParentBehaviorBase {\n"
                        + "}\n");

        HierarchyScannerResult result = compile("com.example.ChildBehaviorBase", parent, child);

        List<BehaviorEventInfo> events = result.getBehaviorEvents();
        Assertions.assertEquals(1, events.size(), "expected 1 'click' after dedup");
        Assertions.assertEquals("child click", events.get(0).getAnnotation().description());
    }

    /** {@code @Property} on a non-getter method is silently ignored — compilation still succeeds. */
    @Test
    void nonGetterPropertyAnnotation_isIgnored() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.example.BadBase",
                "package com.example;\n"
                        + "import org.primefaces.cdk.api.Property;\n"
                        + "public abstract class BadBase {\n"
                        + "    @Property(description = \"Valid\")\n"
                        + "    public abstract String getGoodProp();\n"
                        + "    @Property(description = \"Invalid placement\")\n"
                        + "    public abstract void processAction();\n"
                        + "}\n");

        CapturingProcessor processor = new CapturingProcessor("com.example.BadBase");
        Compilation compilation = Compiler.javac().withProcessors(processor).compile(source);
        CompilationSubject.assertThat(compilation).succeeded();

        Set<String> names = propertyNames(processor.result);
        Assertions.assertTrue(names.contains("goodProp"));
        Assertions.assertFalse(names.contains("processAction"));
        Assertions.assertEquals(1, names.size());
    }

    /** Diamond interface hierarchy — a property inherited via two paths appears exactly once. */
    @Test
    void diamondInterface_propertyNotDuplicated() {
        JavaFileObject base = JavaFileObjects.forSourceString(
                "com.example.HasStyle",
                "package com.example;\n"
                        + "import org.primefaces.cdk.api.Property;\n"
                        + "public interface HasStyle {\n"
                        + "    @Property(description = \"CSS style\")\n"
                        + "    String getStyle();\n"
                        + "}\n");
        JavaFileObject left  = JavaFileObjects.forSourceString("com.example.HasInput",
                "package com.example;\npublic interface HasInput extends HasStyle {}\n");
        JavaFileObject right = JavaFileObjects.forSourceString("com.example.HasOutput",
                "package com.example;\npublic interface HasOutput extends HasStyle {}\n");
        JavaFileObject leaf  = JavaFileObjects.forSourceString(
                "com.example.DiamondBase",
                "package com.example;\n"
                        + "public abstract class DiamondBase implements HasInput, HasOutput {\n"
                        + "}\n");

        HierarchyScannerResult result = compile("com.example.DiamondBase", base, left, right, leaf);

        long count = result.getProperties().stream().filter(p -> "style".equals(p.getName())).count();
        Assertions.assertEquals(1, count, "'style' must appear exactly once");
    }

    /** Abstract getter in a compiled ancestor — getter and setter must be generated. */
    @Test
    void abstractGetterInAncestor_getterIsGenerated() {
        JavaFileObject parent = JavaFileObjects.forSourceString(
                "com.example.AbstractParent",
                "package com.example;\n"
                        + "import org.primefaces.cdk.api.Property;\n"
                        + "public abstract class AbstractParent {\n"
                        + "    @Property(description = \"The for attribute\")\n"
                        + "    public abstract String getFor();\n"
                        + "}\n");
        JavaFileObject child = JavaFileObjects.forSourceString(
                "com.example.ChildBase",
                "package com.example;\n"
                        + "public abstract class ChildBase extends AbstractParent {\n"
                        + "}\n");

        HierarchyScannerResult result = compile("com.example.ChildBase", parent, child);

        PropertyInfo forProp = findPropertyByName(result, "for");
        Assertions.assertNotNull(forProp);
        Assertions.assertTrue(forProp.isGenerateGetter(), "getter must be generated for abstract ancestor");
        Assertions.assertNotNull(forProp.getGetterElement(), "getterElement must be set");
    }

    /**
     * {@code BaseClassOverrideProperty} re-declares {@code getFor()} as abstract with
     * {@code @Property(hide=true)}, breaking the concrete chain from {@code SuperBaseClass}.
     * Getter must be generated and {@code hide=true} must win.
     */
    @Test
    void threeLevelHierarchy_abstractRedeclarationWithHide_getterIsGenerated() {
        JavaFileObject base = JavaFileObjects.forSourceString(
                "org.primefaces.cdk.impl.subclass.Base",
                "package org.primefaces.cdk.impl.subclass;\n"
                        + "public abstract class Base extends BaseClassOverrideProperty {\n"
                        + "}\n");

        HierarchyScannerResult result = compile("org.primefaces.cdk.impl.subclass.Base", base);

        PropertyInfo forProp = findPropertyByName(result, "for");
        Assertions.assertNotNull(forProp);
        Assertions.assertTrue(forProp.getAnnotation().hide(), "hide=true from BaseClassOverrideProperty must win");
        Assertions.assertTrue(forProp.isGenerateGetter(),     "abstract re-declaration must generate getter");
    }

    /**
     * {@code BaseClassWithoutProperties} silently inherits the concrete {@code getFor()} from
     * {@code SuperBaseClass} and also implements {@code AdditionalPropertiesInterface} which
     * adds {@code @Property} metadata. No getter should be generated — the concrete body
     * already exists — but the interface description must be preserved.
     */
    @Test
    @Disabled
    void threeLevelHierarchy_concreteGetterSilentlyInherited_noGetterGenerated() {
        JavaFileObject base = JavaFileObjects.forSourceString(
                "org.primefaces.cdk.impl.subclass.Base",
                "package org.primefaces.cdk.impl.subclass;\n"
                        + "public abstract class Base extends BaseClassWithoutProperties {\n"
                        + "}\n");

        HierarchyScannerResult result = compile("org.primefaces.cdk.impl.subclass.Base", base);

        PropertyInfo forProp = findPropertyByName(result, "for");
        Assertions.assertNotNull(forProp);
        Assertions.assertEquals("test", forProp.getAnnotation().description(),
                "description from AdditionalPropertiesInterface must win");
        Assertions.assertFalse(forProp.isGenerateGetter(), "concrete SuperBaseClass getter must suppress generation");
        Assertions.assertFalse(forProp.isGenerateSetter(), "concrete SuperBaseClass setter must suppress generation");
    }

    private Set<String> propertyNames(HierarchyScannerResult result) {
        return result.getProperties().stream().map(PropertyInfo::getName).collect(Collectors.toSet());
    }

    private Set<String> facetNames(HierarchyScannerResult result) {
        return result.getFacets().stream().map(FacetInfo::getName).collect(Collectors.toSet());
    }

    private PropertyInfo findPropertyByName(HierarchyScannerResult result, String name) {
        return result.getProperties().stream().filter(p -> name.equals(p.getName())).findFirst().orElse(null);
    }

    private BehaviorEventInfo findEventByName(HierarchyScannerResult result, String name) {
        return result.getBehaviorEvents().stream().filter(e -> name.equals(e.getName())).findFirst().orElse(null);
    }

    @SupportedAnnotationTypes("*")
    @SupportedSourceVersion(SourceVersion.RELEASE_11)
    static class CapturingProcessor extends AbstractProcessor {

        HierarchyScannerResult result = HierarchyScannerResult.empty();
        private final String targetClassName;

        CapturingProcessor(String targetClassName) {
            this.targetClassName = targetClassName;
        }

        @Override
        public boolean process(java.util.Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
            if (roundEnv.processingOver()) {
                return false;
            }
            TypeElement target = processingEnv.getElementUtils().getTypeElement(targetClassName);
            if (target == null) {
                return false;
            }
            HierarchyScanner scanner = new HierarchyScanner(processingEnv);
            result = scanner.scan(target);
            return false;
        }
    }
}
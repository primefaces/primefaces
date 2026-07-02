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
                """
                package com.example;
                import org.primefaces.cdk.api.Property;
                public abstract class SimpleBase {
                    @Property(description = "The label")
                    public abstract String getLabel();
                    @Property(description = "Is it closable?")
                    public abstract boolean isClosable();
                }
                """);

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
                """
                package com.example;
                import org.primefaces.cdk.api.Property;
                public abstract class ParentBase {
                    @Property(description = "Parent style")
                    public abstract String getStyle();
                }
                """);
        JavaFileObject child = JavaFileObjects.forSourceString(
                "com.example.ChildBase",
                """
                package com.example;
                import org.primefaces.cdk.api.Property;
                public abstract class ChildBase extends ParentBase {
                    @Property(description = "Child value")
                    public abstract String getValue();
                }
                """);

        HierarchyScannerResult result = compile("com.example.ChildBase", parent, child);

        Assertions.assertTrue(propertyNames(result).contains("style"), "expected inherited 'style'");
        Assertions.assertTrue(propertyNames(result).contains("value"), "expected own 'value'");
    }

    /** Child re-declares a property with {@code internal=true} — child definition wins. */
    @Test
    void child_internalTrue_overridesParentDefinition() {
        JavaFileObject parent = JavaFileObjects.forSourceString(
                "com.example.ParentBase",
                """
                package com.example;
                import org.primefaces.cdk.api.Property;
                public abstract class ParentBase {
                    @Property(description = "Parent widget var")
                    public abstract String getWidgetVar();
                }
                """);
        JavaFileObject child = JavaFileObjects.forSourceString(
                "com.example.ChildBase",
                """
                package com.example;
                import org.primefaces.cdk.api.Property;
                public abstract class ChildBase extends ParentBase {
                    @Property(description = "Hidden", internal = true)
                    public abstract String getWidgetVar();
                }
                """);

        HierarchyScannerResult result = compile("com.example.ChildBase", parent, child);

        PropertyInfo widgetVar = findPropertyByName(result, "widgetVar");
        Assertions.assertNotNull(widgetVar, "widgetVar must be present");
        Assertions.assertTrue(widgetVar.getAnnotation().internal(), "child internal=true must win");
    }

    /** Interface {@code @Property} is picked up when no {@code PropertyKeys} enum is present. */
    @Test
    void annotatedInterface_propertiesCollected() {
        JavaFileObject iface = JavaFileObjects.forSourceString(
                "com.example.HasWidget",
                """
                package com.example;
                import org.primefaces.cdk.api.Property;
                public interface HasWidget {
                    @Property(description = "The widget variable name")
                    String getWidgetVar();
                }
                """);
        JavaFileObject impl = JavaFileObjects.forSourceString(
                "com.example.WidgetBase",
                """
                package com.example;
                public abstract class WidgetBase implements HasWidget {
                }
                """);

        HierarchyScannerResult result = compile("com.example.WidgetBase", iface, impl);

        Assertions.assertTrue(propertyNames(result).contains("widgetVar"));
    }

    /** {@code @Facet} annotations are collected and names are extracted correctly. */
    @Test
    void facetAnnotations_areCollected() {
        JavaFileObject source = JavaFileObjects.forSourceString(
                "com.example.PanelBase",
                """
                package com.example;
                import org.primefaces.cdk.api.Facet;
                public abstract class PanelBase {
                    @Facet(description = "The panel header")
                    public abstract Object getHeaderFacet();
                    @Facet(description = "The panel footer")
                    public abstract Object getFooterFacet();
                }
                """);

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
                """
                package com.example;
                import org.primefaces.cdk.api.FacesBehaviorEvent;
                import jakarta.faces.event.BehaviorEvent;
                @FacesBehaviorEvent(name = "click", event = BehaviorEvent.class, defaultEvent = true)
                public abstract class ClickBehaviorBase {
                }
                """);

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
                """
                package com.example;
                import org.primefaces.cdk.api.FacesBehaviorEvent;
                import org.primefaces.cdk.api.FacesBehaviorEvents;
                import jakarta.faces.event.BehaviorEvent;
                @FacesBehaviorEvents({
                    @FacesBehaviorEvent(name = "select",   event = BehaviorEvent.class, defaultEvent = true),
                    @FacesBehaviorEvent(name = "unselect", event = BehaviorEvent.class),
                    @FacesBehaviorEvent(name = "toggle",   event = BehaviorEvent.class, implicit = true)
                })
                public abstract class MultiEventBase {
                }
                """);

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
                """
                package com.example;
                import org.primefaces.cdk.api.FacesBehaviorEvent;
                import jakarta.faces.event.BehaviorEvent;
                @FacesBehaviorEvent(name = "click", event = BehaviorEvent.class, description = "parent click")
                public abstract class ParentBehaviorBase {
                }
                """);
        JavaFileObject child = JavaFileObjects.forSourceString(
                "com.example.ChildBehaviorBase",
                """
                package com.example;
                import org.primefaces.cdk.api.FacesBehaviorEvent;
                import jakarta.faces.event.BehaviorEvent;
                @FacesBehaviorEvent(name = "click", event = BehaviorEvent.class, description = "child click")
                public abstract class ChildBehaviorBase extends ParentBehaviorBase {
                }
                """);

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
                """
                package com.example;
                import org.primefaces.cdk.api.Property;
                public abstract class BadBase {
                    @Property(description = "Valid")
                    public abstract String getGoodProp();
                    @Property(description = "Invalid placement")
                    public abstract void processAction();
                }
                """);

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
                """
                package com.example;
                import org.primefaces.cdk.api.Property;
                public interface HasStyle {
                    @Property(description = "CSS style")
                    String getStyle();
                }
                """);
        JavaFileObject left  = JavaFileObjects.forSourceString("com.example.HasInput",
                "package com.example;\npublic interface HasInput extends HasStyle {}\n");
        JavaFileObject right = JavaFileObjects.forSourceString("com.example.HasOutput",
                "package com.example;\npublic interface HasOutput extends HasStyle {}\n");
        JavaFileObject leaf  = JavaFileObjects.forSourceString(
                "com.example.DiamondBase",
                """
                package com.example;
                public abstract class DiamondBase implements HasInput, HasOutput {
                }
                """);

        HierarchyScannerResult result = compile("com.example.DiamondBase", base, left, right, leaf);

        long count = result.getProperties().stream().filter(p -> "style".equals(p.getName())).count();
        Assertions.assertEquals(1, count, "'style' must appear exactly once");
    }

    /** Abstract getter in a compiled ancestor — getter and setter must be generated. */
    @Test
    void abstractGetterInAncestor_getterIsGenerated() {
        JavaFileObject parent = JavaFileObjects.forSourceString(
                "com.example.AbstractParent",
                """
                package com.example;
                import org.primefaces.cdk.api.Property;
                public abstract class AbstractParent {
                    @Property(description = "The for attribute")
                    public abstract String getFor();
                }
                """);
        JavaFileObject child = JavaFileObjects.forSourceString(
                "com.example.ChildBase",
                """
                package com.example;
                public abstract class ChildBase extends AbstractParent {
                }
                """);

        HierarchyScannerResult result = compile("com.example.ChildBase", parent, child);

        PropertyInfo forProp = findPropertyByName(result, "for");
        Assertions.assertNotNull(forProp);
        Assertions.assertFalse(forProp.isImplementedGetterExists());
    }

    /**
     * {@code BaseClassOverrideProperty} re-declares {@code getFor()} as abstract with
     * {@code @Property(internal=true)}, breaking the concrete chain from {@code SuperBaseClass}.
     * Getter must be generated and {@code internal=true} must win.
     */
    @Test
    void threeLevelHierarchy_abstractRedeclarationWithInternal_getterIsGenerated() {
        JavaFileObject base = JavaFileObjects.forSourceString(
                "org.primefaces.cdk.impl.subclass.Base",
                """
                package org.primefaces.cdk.impl.subclass;
                public abstract class Base extends BaseClassOverrideProperty {
                }
                """);

        HierarchyScannerResult result = compile("org.primefaces.cdk.impl.subclass.Base", base);

        PropertyInfo forProp = findPropertyByName(result, "for");
        Assertions.assertNotNull(forProp);
        Assertions.assertTrue(forProp.getAnnotation().internal(), "internal=true from BaseClassOverrideProperty must win");
        Assertions.assertFalse(forProp.isImplementedGetterExists(),     "abstract re-declaration must generate getter");
    }

    /**
     * {@code BaseClassWithoutProperties} silently inherits the concrete {@code getFor()} from
     * {@code SuperBaseClass} and also implements {@code AdditionalPropertiesInterface} which
     * adds {@code @Property} metadata. No getter should be generated — the concrete body
     * already exists — but the interface description must be preserved.
     */
    @Test
    void threeLevelHierarchy_concreteGetterSilentlyInherited_noGetterGenerated() {
        JavaFileObject base = JavaFileObjects.forSourceString(
                "org.primefaces.cdk.impl.subclass.Base",
                """
                package org.primefaces.cdk.impl.subclass;
                public abstract class Base extends BaseClassWithoutProperties {
                }
                """);

        HierarchyScannerResult result = compile("org.primefaces.cdk.impl.subclass.Base", base);

        PropertyInfo forProp = findPropertyByName(result, "for");
        Assertions.assertNotNull(forProp);
        Assertions.assertEquals("test", forProp.getAnnotation().description(),
                "description from AdditionalPropertiesInterface must win");
        Assertions.assertTrue(forProp.isGetterExists(), "concrete SuperBaseClass getter must suppress generation");
        Assertions.assertTrue(forProp.isSetterExists(), "concrete SuperBaseClass setter must suppress generation");
    }

    @Test
    void asdasd() {
        JavaFileObject base = JavaFileObjects.forSourceString(
                "org.primefaces.cdk.impl.subclass.Base",
                """
                package org.primefaces.cdk.impl.subclass;
                public abstract class Base extends SuperBaseClass implements AdditionalPropertiesInterface {
                }
                """);

        HierarchyScannerResult result = compile("org.primefaces.cdk.impl.subclass.Base", base);

        PropertyInfo forProp = findPropertyByName(result, "for");
        Assertions.assertNotNull(forProp);
        Assertions.assertTrue(forProp.isGetterExists(), "concrete SuperBaseClass getter must suppress generation");
        Assertions.assertTrue(forProp.isSetterExists(), "concrete SuperBaseClass setter must suppress generation");
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
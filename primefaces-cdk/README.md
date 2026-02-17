# PrimeFaces Component Development Kit (CDK)

The PrimeFaces CDK automates JSF component development through annotation-based code generation and Maven build integration.

## Overview

The CDK consists of four main parts:

1. **API Annotations** - Mark your component and behavior definitions
2. **API Base Classes** - Foundation classes for components and behaviors
3. **Annotation Processor** - Generates implementation classes at compile-time
4. **Maven Plugin** - Generates taglib XML at build-time

## Build Process

1. **Compile** - Annotation processor generates `*Impl` classes in `target/generated-sources/annotations`
2. **Process-Classes** - Maven plugin scans compiled classes
3. **Generate** - Taglib XML is written to `META-INF/*.taglib.xml`
4. **Package** - Everything is bundled into JAR

## Base Classes

The CDK provides base classes to simplify component and behavior development.


### Behavior Classes

**`PrimeClientBehavior`** - Base class for custom behaviors
```java
@FacesBehaviorBase
public abstract class AjaxBehaviorBase extends PrimeClientBehavior {

    @Property
    public abstract boolean isDisabled();
}
```
```java
@FacesBehavior(AjaxBehavior.BEHAVIOR_ID)
@FacesBehaviorHandler(AjaxBehaviorHandler.class)
@FacesBehaviorDescription("AjaxBehavior is an extension to standard f:ajax.")
public class AjaxBehavior extends AjaxBehaviorBaseImpl implements AjaxSource {
    public static final String BEHAVIOR_ID = "org.primefaces.component.AjaxBehavior";

}
```

**`PrimeClientBehaviorHandler`** - Simplifies custom behavior tag handlers
```java
public class AjaxBehaviorHandler extends PrimeClientBehaviorHandler<AjaxBehavior> {

    public AjaxBehaviorHandler(BehaviorConfig config) {
        super(config);
    }

    @Override
    public String getBehaviorId() {
        return AjaxBehavior.BEHAVIOR_ID;
    }

    @Override
    protected void init(FaceletContext ctx, AjaxBehavior behavior, String eventName, UIComponent parent) {
        
    }
```

#### Typical Behavior Structure

```
ConfirmBehaviorBase       (abstract, extends PrimeClientBehavior, @Property)
    ↓ extends
ConfirmBehaviorBaseImpl   (generated, abstract, PropertyKeys, getAllProperties())
    ↓ extends
ConfirmBehavior           (concrete, @FacesBehavior, @FacesBehaviorHandler)
```

### Component Classes

**`PrimeComponent`** - Interface that all generated components implement
```java
public interface PrimeComponent {
    PrimePropertyKeys[] getPropertyKeys();
    PrimeFacetKeys[] getFacetKeys();
    // ... other component lifecycle methods
}
```

#### Typical Component Structure

```
DataTableBase             (abstract, @Property/@Facet)
    ↓ extends
DataTableBaseImpl         (generated, abstract, implements PrimeComponent, PrimePropertyKeys, PrimeFacetKeys)
    ↓ extends  
DataTable                 (concrete, @FacesComponent)
```

### TagHandler Classes

```java
@FacesTagHandler("Input components keep their local values at state when validation fails." +
        " ResetInput is used to clear the cached values from state so that components retrieve their values from the backing bean model instead.")
public class ResetInputTagHandler extends TagHandler {

    @Property(description = "Comma or white-space separated list of component ids.", required = true)
    private final TagAttribute target;

    @Property(description = "Whether to assign null values to bound values as well.", type = Boolean.class)
    private final TagAttribute clearModel;

    public ResetInputTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        target = getRequiredAttribute("target");
        clearModel = getAttribute("clearModel");
    }
    
    ...
}
```

## API Annotations

### `@FacesComponentBase`
Marks an abstract component class for implementation generation, if no other CDK annotations are present.

```java
@FacesComponentBase
public abstract class InputTextBase extends HtmlInputText {

}
```

### `@FacesBehaviorBase`
Marks an abstract behavior class for implementation generation, if no other CDK annotations are present.

```java
@FacesBehaviorBase
public abstract class AjaxBehaviorBase extends PrimeClientBehavior {

}
```

### `@Property`
Defines a component property with StateHelper integration and generates `PropertyKeys` enum.

```java
@Property(description = "Enables pagination", defaultValue = "false", required = false)
public abstract boolean isPaginator();

@Property(description = "Number of rows")
public abstract int getRows();
```

**Features:**
- Auto-generates getter/setter implementations
- StateHelper integration for state saving
- Default values for primitives
- Scans entire class hierarchy including interfaces

### `@Facet`
Defines a component facet and generates `PrimeFacetKeys` enum.

```java
@Facet(description = "Header content")
public abstract UIComponent getHeaderFacet();

@Facet(description = "Footer content")
public abstract UIComponent getFooterFacet();
```

### `@FacesBehaviorEvent` / `@FacesBehaviorEvents`
Defines client behavior events for components and generates `PrimeClientBehaviorEventKeys` enum.

```java
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "toggle", event = ToggleEvent.class),
    @FacesBehaviorEvent(name = "close", event = CloseEvent.class)
})
public abstract class PanelBase extends UIPanel 
        implements Widget, ClientBehaviorHolder {
    // ...
}
```

### `@FacesBehaviorHandler`
Specifies a custom tag handler for a behavior.

```java
@FacesBehavior(AjaxBehavior.BEHAVIOR_ID)
@FacesBehaviorHandler(AjaxBehaviorHandler.class)
public class AjaxBehavior extends AjaxBehaviorBaseImpl implements AjaxSource {
    public static final String BEHAVIOR_ID = "org.primefaces.component.AjaxBehavior";
    
}
```

### `@FacesComponentHandler`
Specifies a custom tag handler for a component.

```java
@FacesComponent(value = Tree.COMPONENT_TYPE, namespace = Tree.COMPONENT_FAMILY)
@FacesComponentDescription("Tree is is used for displaying hierarchical data and creating site navigations.")
@FacesComponentHandler(TreeHandler.class)
public class Tree extends TreeBaseImpl {

}
```

### `@Function`
Exposes static methods as EL functions.

```java
@Function
public static String closestWidgetVar(UIComponent component) {
    return SearchExpressionUtils.resolveWidgetVar(component);
}

@Function(name = "getVar")  // Custom EL name
public static String getWidgetVar(UIComponent component) {
    return ((Widget) component).resolveWidgetVar();
}
```

**Usage in XHTML:**
```xml
<h:outputText value="#{p:closestWidgetVar(component)}" />
<h:outputText value="#{p:getVar(component)}" />
```

## Annotation Processor

Generates the implementation classes at compile-time like mentioned above.

### Features
- **Hierarchy scanning** - Collects `@Property`, `@Facet`, `@FacetBehaviorEvent`, `@FacesBehaviorHandler` from superclasses and interfaces
- **Deduplication** - Prevents duplicate properties in inheritance tree
- **PrimeComponent integration** - Automatically implements methods for components
- **PrimeClientBehavior integration** - Automatically implements methods for components
- **Same package generation** - `*Impl` generated alongside source class
- **PrimePropertyKeys enum** - Type-safe property access
- **PrimeFacetKeys enum** - Type-safe facet access
- **PrimeClientBehaviorEventKeys enum** - Type-safe facet access


Usage:

````xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.primefaces</groupId>
                <artifactId>primefaces-cdk-impl</artifactId>
                <version>${primefaces.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
````


## Maven Plugin

Generates JSF taglib XML from compiled classes.

### Configuration
```xml
<plugin>
    <groupId>org.primefaces.next</groupId>
    <artifactId>primefaces-next-cdk-impl</artifactId>
    <version>${primefaces.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>generate-taglib</goal>
            </goals>
            <phase>compile</phase>
            <configuration>
                <uri>primefaces</uri>
                <shortName>p</shortName>
                <displayName>PrimeFaces</displayName>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Generated Taglib XML

```xml
<facelet-taglib>
    <displayName>PrimeFaces</displayName>
    <namespace>primefaces</namespace>
    <short-name>p</short-name>

    <!-- Component Tag -->
    <tag>
        <tag-name>dataTable</tag-name>
        <component>
            <component-type>org.primefaces.next.DataTable</component-type>
            <renderer-type>org.primefaces.next.DataTableRenderer</renderer-type>
        </component>
        <attribute>
            <description>Number of rows per page</description>
            <name>rows</name>
            <required>false</required>
            <type>int</type>
        </attribute>
    </tag>
    
    <!-- Behavior Tag -->
    <tag>
        <tag-name>confirm</tag-name>
        <behavior>
            <behavior-id>org.primefaces.component.ConfirmBehavior</behavior-id>
            <handler-class>org.primefaces.component.ConfirmBehaviorHandler</handler-class>
        </behavior>
    </tag>
    
    <!-- EL Function -->
    <function>
        <function-name>closestWidgetVar</function-name>
        <function-class>org.primefaces.expression.SearchExpressionUtils</function-class>
        <function-signature>java.lang.String closestWidgetVar(jakarta.faces.component.UIComponent)</function-signature>
    </function>
</facelet-taglib>
```

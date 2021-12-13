# Global Attributes

You may have a need to set the same attribute on hundreds of components in a large application.

The perfect example is needing the same date pattern on all of your Calendar/DatePicker 
components but not wanting to repeat yourself on every single instance on every single XHTML page.

```xml
<p:calendar pattern="dd-MMM-yyyy"/>
```

This can be accomplished by using a custom JSF Application wrapper.


## Application Wrapper

First you must create a custom application wrapper like the following:

```java
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.UICalendar;
import org.primefaces.component.layout.Layout;

public class DemoApplication extends ApplicationWrapper {

    private static final Set<String> BLACKLISTED_COMPONENT_TYPES = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(Layout.COMPONENT_TYPE)));

    private final Application wrapped;

    public DemoApplication(Application wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Application getWrapped() {
        return wrapped;
    }

    @Override
    public UIComponent createComponent(FacesContext context, String componentType, String rendererType) {
        // prevent certain components from being used
        if (BLACKLISTED_COMPONENT_TYPES.contains(componentType)) {
            throw new IllegalArgumentException(
                    componentType + " is deprecated and you should not be using this component.");
        }

        final UIComponent component = super.createComponent(context, componentType, rendererType);

        // set a global date pattern by default
        if (component instanceof UICalendar) {
            final UICalendar calendar = (UICalendar) component;
            calendar.setPattern("dd-MMM-yyyy");
        }

        return component;
    }
}

```

## Application Factory

Next you must implement a factory to produce your application:

```java
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

public class DemoApplicationFactory extends ApplicationFactory {

    private final ApplicationFactory wrapped;

    public DemoApplicationFactory(ApplicationFactory wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Application getApplication() {
        return new DemoApplication(wrapped.getApplication());
    }

    @Override
    public void setApplication(Application application) {
        wrapped.setApplication(application);
    }
}
```

Finally you must enable it with faces-config.xml:

```xml
<factory>
      <application-factory>org.your.DemoApplicationFactory</application-factory>
</factory>
```
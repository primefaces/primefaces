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

public class DemoApplication extends ApplicationWrapper {

    public DemoApplication(Application wrapped) {
        super(wrapped);
    }

    @Override
    public UIComponent createComponent(FacesContext context, String componentType, String rendererType) {
        UIComponent component = super.createComponent(context, componentType, rendererType);

        // set a global date pattern by default
        if (component instanceof UICalendar) {
            UICalendar calendar = (UICalendar) component;
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

    public DemoApplicationFactory(ApplicationFactory wrapped) {
        super(wrapped);
    }

    @Override
    public Application getApplication() {
        return new DemoApplication(getWrapped().getApplication());
    }

    @Override
    public void setApplication(Application application) {
        getWrapped().setApplication(application);
    }
}
```

Finally you must enable it with faces-config.xml:

```xml
<factory>
      <application-factory>org.your.DemoApplicationFactory</application-factory>
</factory>
```

!> Global attributes will ignore backing bean EL expression values. [`See GitHub 9694`](https://github.com/primefaces/primefaces/issues/9694) Hard coded values in XHTML will work properly but not EL expressions.
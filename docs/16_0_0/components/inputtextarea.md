# InputTextarea

InputTextarea is an extension to standard inputTextarea with autoComplete, autoResize, remaining
characters counter and theming features.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.InputTextarea.html)

## Getting Started with InputTextarea
InputTextarea usage is same as standard inputTextarea;

```xhtml
<p:inputTextarea value="#{bean.propertyName}" />
```
## AutoResize
While textarea is being typed, if content height exceeds the allocated space, textarea can grow
automatically. Use autoResize option to turn on/off this feature.

```xhtml
<p:inputTextarea value="#{bean.propertyName}" autoResize="true|false"/>
```

## Remaining Characters
InputTextarea can limit the maximum allowed characters with maxLength option and display the
remaining characters count as well.

```xhtml
<p:inputTextarea value="#{bean.propertyName}" counter="display" maxlength="20" counterTemplate="{0} characters remaining" />
<h:outputText id="display" />
```

## AutoComplete
InputTextarea supports ajax autocomplete functionality as well. You need to provide a
completeMethod to provide the suggestions to use this feature. In sample below, completeArea
method will be invoked with the query being the four characters before the caret.

```java
public class AutoCompleteBean {
    public List<String> completeArea(String query) {
        List<String> results = new ArrayList<String>();
        if(query.equals("PrimeFaces")) {
            results.add("PrimeFaces Rocks!!!");
            results.add("PrimeFaces has 100+ components.");
            results.add("PrimeFaces is lightweight.");
            results.add("PrimeFaces is easy to use.");
            results.add("PrimeFaces is developed with passion!");
        }
        else {
            for(int i = 0; i < 10; i++) {
                results.add(query + i);
            }
        }
        return results;
    }
}
```

```xhtml
<p:inputTextarea rows="10" cols="50" minQueryLength="4" completeMethod="#{autoCompleteBean.completeArea}" />
```

## Client Side API
Widget: _PrimeFaces.widget.InputTextarea_


| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| disable() | - | void | Disables the input field |
| enable() | - | void | Enables the input field |

## Skinning
InputTextarea renders a textarea element which _style_ and _styleClass_ options apply. Following is the
list of structural style classes;

| Class | Applies |
| --- | --- |
.ui-inputtextarea | Textarea element.
.ui-inputfield | Textarea element.
.ui-autocomplete-panel | Overlay for suggestions.
.ui-autocomplete-items | Suggestions container.
.ui-autocomplete-item | Each suggestion.

As skinning style classes are global, see the main theming section for more information.

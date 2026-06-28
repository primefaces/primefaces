# Effect

Effect component is based on the jQuery UI effects library.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Effect-1.html)

## Getting started with Effect
Effect component needs a trigger and target which is effect’s parent by default. In example below
clicking outputText (trigger) will run the pulsate effect on outputText(target) itself.

```xhtml
<h:outputText value="#{bean.value}">
    <p:effect type="pulsate" event="click" />
</h:outputText>
```
## Effect Target
There may be cases where you want to display an effect on another target on the same page while
keeping the parent as the trigger. Use _for_ option to specify a target.

```xhtml
<h:outputLink id="lnk" value="#">
    <h:outputText value="Show the Barca Temple" />
    <p:effect type="appear" event="click" for="img" />
</h:outputLink>
<p:graphicImage id="img" value="/ui/barca/campnou.jpg" style="display:none"/>
```
With this setting, outputLink becomes the trigger for the effect on graphicImage. When the link is
clicked, initially hidden graphicImage comes up with a fade effect.

**Note**: It’s important for components that have the effect component as a child to have an
assigned id because some components do not render their clientId’s if you don’t give them an id
explicitly.

## List of Effects
Following is the list of effects;

- blind
- clip
- drop
- explode
- fold
- puff
- slide
- scale
- bounce
- highlight
- pulsate
- shake
- size
- transfer


## Effect Configuration
Each effect has different parameters for animation like colors, duration and more. In order to change
the configuration of the animation, provide these parameters with the f:param tag.

```xhtml
<h:outputText value="#{bean.value}">
    <p:effect type="scale" event="mouseover">
        <f:param name="percent" value="90"/>
    </p:effect>
</h:outputText>
```
It’s important to provide string options with single quotes.

```xhtml
<h:outputText value="#{bean.value}">
    <p:effect type="blind" event="click">
        <f:param name="direction" value="'horizontal'" />
    </p:effect>
</h:outputText>
```
For the full list of configuration parameters for each effect, please see the jquery documentation;

http://docs.jquery.com/UI/Effects

## Effect on Load
Effects can also be applied to any Jakarta Faces component when page is loaded for the first time or after an
ajax request is completed by using _load_ as the event name. Following example animates messages
with pulsate effect after ajax request completes.

```xhtml
<p:messages id="messages">
    <p:effect type="pulsate" event="load" delay=" 500 ">
        <f:param name="mode" value="'show'" />
    </p:effect>
</p:messages>
<p:commandButton value="Save" action="#{bean.action}" update="messages"/>
```
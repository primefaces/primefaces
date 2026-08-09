# Tooltip

Tooltip goes beyond the legacy HTML title attribute by providing custom effects, events, HTML content
and advance theme support.

## Getting started with the Tooltip
Tooltip can be used by attaching it to a target component. Tooltip value can also be retrieved from
target’s title, so following are same;

```xhtml
<h:inputSecret id="pwd" value="#{myBean.password}" />
<p:tooltip for="pwd" value="Only numbers"/>
```
```xhtml
<h:inputSecret id="pwd" value="#{myBean.password}" title="Only numbers"/>
<p:tooltip for="pwd"/>
```
## Global Tooltip
Global tooltip binds to elements with title attributes. Ajax updates are supported as well, meaning if
target component is updated with ajax, tooltip can still bind. As global tooltips are more efficient
since only one instance of tooltip is used across all tooltip targets, it is suggested to be used instead
of explicit tooltips unless you have a custom case e.g. different options, custom content.

```xhtml
<p:tooltip />
<p:inputText id="focus" title="Tooltip for an input"/>
<h:outputLink id="fade" value="#" title="Tooltip for a link">
    <h:outputText value="Fade Effect" />
</h:outputLink>
<p:commandButton value="Up" title="Up" />
```

## IE10 Issue
Due to a bug, IE10 always displays the title text in a native popup when the element receives focus
via tabbing and two tooltips might be displayed at once. Solution is to use passthrough data-tooltip
attribute instead of title.

```xhtml
<html xmlns="http://www.w3.org/1999/xhtml"
    xmlns:h="jakarta.faces.html"
    xmlns:pt="jakarta.faces.passthrough"
    xmlns:p="http://primefaces.org/ui">
    <h:head></h:head>
    <h:body>
        <p:inputText pt:data-tooltip="Title here"/>
        <p:inputText title="Works fine except tabbed on IE10"/>
    </h:body>
</html>
```
## Position
Tooltip has four possible position, default is _right_ and other valid values are _left_ , _top_ and _bottom_.

## Events and Effects
A tooltip is shown on mouseover event and hidden when mouse is out by default. If you need to
change this behavior use the showEvent and hideEvent feature. Tooltip below is displayed when the
input gets the focus and hidden with onblur.

```xhtml
<h:inputSecret id="pwd" value="#{myBean.password}" />
<p:tooltip for="pwd" value="Password must contain only numbers" showEvent="focus" hideEvent="blur" showEffect="blind" hideEffect="explode" />
```
Available options for effects are; _blind, bounce, clip, drop, explode, fold, highlight, puff, pulsate,
scale, shake, size_ and _slide_.

## HTML Content
Another powerful feature of tooltip is the ability to display custom content as a tooltip.

```xhtml
<h:outputLink id="lnk" value="#">
    <h:outputText value="PrimeFaces Home" />
</h:outputLink>
<p:tooltip for="lnk">
    <p:graphicImage value="/images/prime_logo.png" />
    <h:outputText value="Visit PrimeFaces Home" />
</p:tooltip>
```

## Global Selector
Problem scenario:
When using tooltip inside a Column the tooltip value is displayed in the ColumnToggle and Export.

```xhtml
<p:dataTable id="cars" var="car" value="#{dtBasicView.cars}">
	<p:column>
   		<h:outputText value="#{car.id}" />
    	<f:facet name="header">
			<p:outputLabel value="ID" id="outputTextID"/>
			<p:tooltip value="Tooltip for ID" for="outputTextID"></p:tooltip>
		</f:facet>
	</p:column>
```

Solution:
To only display the text without the tooltip value use globalTooltip with globalSelector attribute.

```xhtml
<p:tooltip globalSelector="a,:input,:button,.header" />
<p:dataTable id="cars" var="car" value="#{dtBasicView.cars}">
	<p:column>
    	<h:outputText value="#{car.id}" />
        <f:facet name="header">
        	<p:outputLabel value="ID" id="outputTextID" styleClass="header" pt:data-tooltip="Tooltip for ID"/>
         </f:facet>
   </p:column>
```


## Skinning
Tooltip has only _.ui-tooltip_ as a style class and is styled with global skinning selectors, see main
skinning section for more information.


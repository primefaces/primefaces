${tag.description}

image::${tag.tagName}.jpg[]

[cols="1,2", options="header"]
.Info
|===
| Key | Value

| Tag
| ${tag.tagName}

<#if component??>
    | Component Class
    | ${component.componentClass}

    | Component Type
    | ${component.componentType}
</#if>

<#if renderer??>
    | Component Family
    | ${renderer.componentFamily}

    | Renderer Type
    | ${renderer.rendererType}

    | Renderer Class
    | ${renderer.rendererClass}
</#if>

<#if behavior??>
    | Behavior Id
    | ${behavior.behaviorId}

    | Behavior Class
    | ${behavior.handlerClass}
</#if>
|===

[cols="1,1,1,2", options="header"]
.Attributes
|===
| Name | Type | Default | Description

<#list tag.attribute as attr>
    | ${attr.name}
    | ${attr.type}
    | ${(attr.defaultValue)!"null"}
    | ${attr.description}
</#list>
|===
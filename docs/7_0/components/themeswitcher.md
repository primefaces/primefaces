# ThemeSwitcher

ThemeSwitcher enables switching PrimeFaces themes on the fly with no page refresh.

## Info

| Name | Value |
| --- | --- |
| Tag | themeSwitcher
| Component Class | org.primefaces.component.themeswitcher.ThemeSwitcher
| Component Type | org.primefaces.component.ThemeSwitcher
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.ThemeSwitcherRenderer
| Renderer Class | org.primefaces.component.themeswitcher.ThemeSwitcherRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
effect | fade | String | Name of the animation.
effectSpeed | normal | String | Duration of the toggle animation, valid values are "slow", "normal" and "fast".
disabled | false | Boolean | Disables the component.
label | null | String | User presentable name.
onchange | null | String | Client side callback to execute on theme change.
style | null | String | Inline style of the component.
styleClass | null | String | Style class of the component.
var | null | String | Variable name to refer to each item.
height | null | Integer | Height of the panel.
tabindex | null | Integer | Position of the element in the tabbing order.

## Getting Started with the ThemeSwitcher
ThemeSwitcher usage is very similar to selectOneMenu.

```xhtml
<p:themeSwitcher style="width:150px">
    <f:selectItem itemLabel="Choose Theme" itemValue="" />
    <f:selectItems value="#{bean.themes}" />
</p:themeSwitcher>
```
## Stateful ThemeSwitcher
By default, themeswitcher just changes the theme on the fly with no page refresh, in case you’d like
to get notified when a user changes the theme (e.g. to update user preferences), you can use an ajax
behavior.

```xhtml
<p:themeSwitcher value="#{bean.theme}" effect="fade">
    <f:selectItem itemLabel="Choose Theme" itemValue="" />
    <f:selectItems value="#{themeSwitcherBean.themes}" />
    <p:ajax listener="#{bean.saveTheme}" />
</p:themeSwitcher>
```
## Advanced ThemeSwitcher
ThemeSwitcher supports displaying custom content so that you can show theme previews.

```xhtml
<p:themeSwitcher>
    <f:selectItem itemLabel="Choose Theme" itemValue="" />
    <f:selectItems value="#{themeSwitcherBean.advancedThemes}" var="theme" itemLabel="#{theme.name}" itemValue="#{theme}"/>
    <p:column>
        <p:graphicImage value="/images/themes/#{t.image}"/>
    </p:column>
    <p:column>
        #{t.name}
    </p:column>
</p:themeSwitcher>
```

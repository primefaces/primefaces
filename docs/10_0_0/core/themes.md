# Themes

PrimeFaces provides a complete skinning/theming architecture allowing you to build your own theme or use a pre-built
theme to completely change the look and feel of web pages.

## Configuration

To activate your theme simply use the `primefaces.THEME`  web.xml context parameter.

```xml
<context-param>
    <param-name>primefaces.THEME</param-name>
    <param-value>saga</param-value>
</context-param>
```


## Built-In Themes

PrimeFaces comes built-in with themes ready to use of out the box. They include:

- arya
- luna-amber
- luna-blue
- luna-green
- luna-pink
- nova-colored
- nova-dark
- nova-light
- omega
- saga (DEFAULT)
- vela


## Premium Themes

PrimeTek offers a full range of professionally built premium themes ready to use out of the box.
Premium themes come with an amazing looking responsive template with a set of XHTML files including dashboard, login, error,
 404 along with icons and images to kick-start applications quickly. 
Each premium layout offers a compatible theme with a similar design. Although theme can be used standalone without the layout,
 we suggest using the theme of a layout for a unified look in your application.

See the [Theme Gallery](https://www.primefaces.org/themes/) for more information.

## Designer

PrimeTek also offers a [Theme Designer](https://www.primefaces.org/designer/primefaces) tool. 
Designer API is a SASS based theme engine to create PrimeFaces themes easily featuring over 500 variables, 
a demo application and a base sample theme. Whether you have your own style guide or just need a custom theme, 
Designer API is the right tool to design and bring them to existence.

## Legacy Themes

PrimeFaces originally supported JQuery ThemeRoller themes but these are not really supported anymore and newer components like ToggleSwitch may not work at all.
However, many older PrimeFaces applications still may use these themes. JQuery has an [on-line tool](https://jqueryui.com/themeroller/) to build your own theme.
PrimeFaces also has a JAR of pre-built ThemeRoller themes you can find on [GitHub](https://github.com/primefaces/themes).
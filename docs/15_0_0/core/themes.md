# Themes

PrimeFaces provides a complete skinning/theming architecture allowing you to build your own theme or use a pre-built
theme to completely change the look and feel of web pages.

## Configuration

To activate your theme simply use the `primefaces.THEME`  web.xml context parameter.

```xml
<context-param>
    <param-name>primefaces.THEME</param-name>
    <param-value>saga-blue</param-value>
</context-param>
```


## Theme

PrimeFaces per default only comes with the saga-blue theme.


## Additional Themes

The primefaces-themes module contains all other themes:

- primefaces-arya-blue
- primefaces-bootstrap4-blue-dark
- primefaces-bootstrap4-blue-light
- primefaces-bootstrap4-dark-common
- primefaces-bootstrap4-light-common
- primefaces-bootstrap4-purple-dark
- primefaces-bootstrap4-purple-light
- primefaces-luna-amber
- primefaces-luna-blue
- primefaces-luna-common
- primefaces-luna-green
- primefaces-luna-pink
- primefaces-material-compact-deeppurple-dark
- primefaces-material-compact-deeppurple-light
- primefaces-material-compact-indigo-dark
- primefaces-material-compact-indigo-light
- primefaces-material-dark-common
- primefaces-material-deeppurple-dark
- primefaces-material-deeppurple-light
- primefaces-material-indigo-dark
- primefaces-material-indigo-light
- primefaces-material-light-common
- primefaces-nova-colored
- primefaces-nova-common
- primefaces-nova-dark
- primefaces-nova-light
- primefaces-saga-blue
- primefaces-vela-blue

You can include it via:

```xml
<dependency>
    <groupId>org.primefaces</groupId>
    <artifactId>primefaces-themes</artifactId>
    <version></version>
</dependency>
```

## Designer

PrimeTek also offers a [Theme Designer](https://www.primefaces.org/designer/primefaces) tool. 
Designer API is a SASS based theme engine to create PrimeFaces themes easily featuring over 500 variables, 
a demo application and a base sample theme. Whether you have your own style guide or just need a custom theme, 
Designer API is the right tool to design and bring them to existence.

## Legacy Themes

PrimeFaces originally supported JQuery ThemeRoller themes but these are not really supported anymore and newer components like ToggleSwitch may not work at all.
However, many older PrimeFaces applications still may use these themes. JQuery has an [on-line tool](https://jqueryui.com/themeroller/) to build your own theme.
PrimeFaces also has a JAR of pre-built ThemeRoller themes you can find on [GitHub](https://github.com/primefaces/themes).

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
    <version>...</version>
</dependency>
```

Themes are always prefixed with `primefaces-`. So for example `primefaces-material-indigo-light` is the `material-indigo-light` theme.
```xml
<context-param>
    <param-name>primefaces.THEME</param-name>
    <param-value>material-indigo-light</param-value>
</context-param>
```

### Creating your own Theme
You can fork and customize theme sources in the [`primefaces-themes`](https://github.com/primefaces/primefaces/tree/master/primefaces-themes) module. There you'll find two folders:
* `theme-base` contains base styling for the layout of all components
* `themes` defines `.scss` variables (e.g. colors, fonts, additional styles, etc.) that will be used by `theme-base`.

See [PrimeFaces Theme template](https://github.com/jungm/primefaces-theme-template) for a community-built template to create your own theme that does not require you to fork the entire base theme.

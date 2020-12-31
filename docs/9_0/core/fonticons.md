# Font Icons

Icon fonts are fonts that contain symbols and glyphs instead of letters or numbers.
They are popular for web designers since you can style them with CSS the same way as regular text.
Also, since they are vectors they are easily scale-able.
They are small, so they load quickly and they are supported in all browsers.

PrimeFaces provides support for both PrimeIcons and FontAwesome web fonts.

## PrimeIcons

PrimeIcons is PrimeTek's web font used in PrimeFaces, PrimeNG, PrimeVue, and PrimeReact.
It comes built into PrimeFaces JAR and they are always added to the view as newer Themes are based on it.

## FontAwesome 4.7 (built-in)

PrimeFaces comes bundled with FontAwesome 4.7 which is a highly customizable scalable vector iconset with 479 icons.
FontAwesome is disabled by default and a global parameter is required to turn it on.

```xml
<context-param>
    <param-name>primefaces.FONT_AWESOME</param-name>
    <param-value>true</param-value>
</context-param>
```

## FontAwesome 5.X (or higher)
FontAwesome 4 is older and PrimeFaces has decided not to upgrade it.  If you would prefer FontAwesome 5 or higher
which has 8000+ icons it is possible.  Make sure the above primefaces.FONT_AWESOME is set to FALSE.

Include FontAwesome webjar in your pom.xml
```xml
<dependency>
     <groupId>org.webjars</groupId>
     <artifactId>font-awesome</artifactId>
     <version>5.12.0</version>
</dependency>
```

Then include these two CSS stylesheets to enable it:
```xml
<h:outputStylesheet library="webjars" name="font-awesome/5.12.0/css/all.min-jsf.css" />
<h:outputStylesheet library="webjars" name="font-awesome/5.12.0/css/v4-shims.min-jsf.css" />
```
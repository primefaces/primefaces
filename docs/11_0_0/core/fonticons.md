# Font Icons

Icon fonts are fonts that contain symbols and glyphs instead of letters or numbers.
They are popular for web designers since you can style them with CSS the same way as regular text.
Also, since they are vectors they are easily scale-able.
They are small, so they load quickly and they are supported in all browsers.

PrimeFaces provides support for both PrimeIcons and FontAwesome web fonts.

## PrimeIcons

PrimeIcons is PrimeTek's web font used in PrimeFaces, PrimeNG, PrimeVue, and PrimeReact.
It comes built into PrimeFaces JAR and they are always added to the view as newer Themes are based on it.

## FontAwesome
PrimeFaces supports both FontAwesome 4.x and 5.x, you just need to manually include it via your pom.xml:

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
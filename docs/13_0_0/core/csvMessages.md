# Client Side Validation -  Messages

Validation errors are displayed as the same way in server side validation, texts are retrieved from a
client side bundle and message components are required for the displays.

## I18N
Default language is English for the CSV messages and for other languages or to customize the
default messages, PrimeFaces Locales bundle needs to be present at the page if you'd like to provide
translations.  
For more info on PrimeFaces Locales, visit
[https://github.com/primefaces/primefaces/wiki/Locales](https://github.com/primefaces/primefaces/wiki/Locales).

## Rendering
PrimeFaces message components have client side renderers for CSV support, these are _p:message_, _p:messages_ and _p:growl_.  
Component options like _showSummary_, _showDetail_, _globalOnly_, mode are all implemented by client side renderer for compatibility.

## MyFaces vs Mojarra
Bean Validation messages between implementations have a slight difference regarding labels,
Mojarra do not the label of the field but MyFaces does. For example;

Mojarra:
```js
javax.faces.validator.BeanValidator.MESSAGE={0}
```

MyFaces:
```js
javax.faces.validator.BeanValidator.MESSAGE={1}: {0}
```

Default CSV messages follow the convention of Mojarra.  
However if you prefer to display the label along with the message, you can override it by adding {1} to the message.

Default:
```js
PrimeFaces.locales['en_US'].messages['javax.faces.validator.BeanValidator.MESSAGE'] = '{0}';
```

With Label:
```js
PrimeFaces.locales['en_US'].messages['javax.faces.validator.BeanValidator.MESSAGE'] = '{1}: {0}';
```

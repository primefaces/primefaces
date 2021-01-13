# Client Side Validation Messages

Validation errors are displayed as the same way in server side validation, texts are retrieved from a
client side bundle and message components are required for the displays.

## I18N
Default language is English for the CSV messages and for other languages or to customize the
default messages, PrimeFaces Locales bundle needs to be present at the page if you'd like to provide
translations. For more info on PrimeFaces Locales, visit
[https://github.com/primefaces/primefaces/wiki/Locales](https://github.com/primefaces/primefaces/wiki/Locales).


## Rendering
PrimeFaces message components have client side renderers for CSV support, these are p:message,
p:messages and p:growl. Component options like showSummary, showDetail, globalOnly, mode are
all implemented by client side renderer for compatibility.

## MyFaces vs Mojarra
Bean Validation messages between implementations have a slight difference regarding labels,
Mojarra do not the label of the field but MyFaces does. For example;

```js
Mojarra:
javax.faces.validator.BeanValidator.MESSAGE={0}
MyFaces:
javax.faces.validator.BeanValidator.MESSAGE={1}: {0}
```
Default CSV messages follow the convention of Mojarra however if you prefer to display the label
along with the message, override can be done by adding {1} to the message;

```js
Default:
PrimeFaces.locales['en_US'].messages['javax.faces.validator.BeanValidator.MESSAGE'] =
'{0}';
With Label:
PrimeFaces.locales['en_US'].messages['javax.faces.validator.BeanValidator.MESSAGE'] =
'{1}: {0}';
```

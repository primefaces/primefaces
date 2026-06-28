# Rating

Rating component features a star based rating system.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.Rating-1.html)

## Getting Started with Rating
Rating is an input component that takes an integer variable as its value.

```java
public class RatingBean {
    private Integer rating;
    //getter-setter
}
```
```xhtml
<p:rating value="#{ratingBean.rating}" />
```
## Number of Stars
Default number of stars is 5, if you need less or more stars use the stars attribute. Following rating
consists of 10 stars.

```xhtml
<p:rating value="#{ratingBean.rating}" stars="10"/>
```

## Display Value Only
In cases where you only want to use the rating component to display the rating value and disallow
user interaction, set _readonly_ to true. Using _disabled_ attribute does the same but adds disabled
visual styles.

## Client Side Callbacks
_onRate_ is called when a star is selected with _value_ as the only parameter.

```xhtml
<p:rating value="#{ratingBean.rating}" onRate="alert('You rated: ' + value)" />
```
## Client Side API
Widget: _PrimeFaces.widget.Rating_

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
getValue() | - | Number | Returns the current value
setValue(value) | value: Value to set | void | Updates rating value with provided one.
disable() | - | void | Disables component.
enable() | - | void | Enables component.
reset() | - | void | Clears the rating.
isDisabled() | - | boolean | Is the rating disabled?
isReadOnly() | - | boolean | Is the rating read only?

## Skinning
Rating resides in a main container which _style_ and _styleClass_ attributes apply. Following is the list
of structural style classes;

| Class | Applies |
| --- | --- |
.ui-rating | Main container element.
.ui-rating-cancel | Cancel icon
.ui-rating | Default star
.ui-rating-on | Active star

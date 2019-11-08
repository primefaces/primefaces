# Collector

Collector is a simple utility to manage collections declaratively.

## Info

| Name | Value |
| --- | --- |
| Tag | collector
| ActionListener Class | org.primefaces.component.collector.Collector

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| value | null | Object | Value to be used in collection operation
| addTo | null | java.util.Collection | Reference to the Collection instance
| removeFrom | null | java.util.Collection | Reference to the Collection instance
| unique | true | Boolean | When enabled, rejects duplicate items on addition.

## Getting started with Collector
Collector requires a collection and a value to work with. It’s important to override equals and
hashCode methods of the value object to make collector work.

```java
public class BookBean {
    private Book book = new Book();
    private List<Book> books;

    public BookBean() {
        books = new ArrayList<Book>();
    }
    //getters and setters
}
```
```xhtml
<p:commandButton value="Add">
    <p:collector value="#{bookBean.book}" addTo="#{bookBean.books}" />
</p:commandButton>

<p:commandLink value="Remove">
    <p:collector value="#{bookBean.book}" removeFrom="#{bookBean.books}" />
</p:commandLink>
```

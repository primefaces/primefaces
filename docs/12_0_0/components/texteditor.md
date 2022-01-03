# TextEditor

Editor is an input component with rich text editing capabilities based on Quill.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.TextEditor-1.html)

## Info

| Name | Value |
| --- | --- |
| Tag | textEditor
| Component Class | org.primefaces.component.texteditor.TextEditor
| Component Type | org.primefaces.component.TextEditor
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.TextEditorRenderer
| Renderer Class | org.primefaces.component.texteditor.TextEditorRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component.
rendered | true | Boolean | Boolean value to specify the rendering of the component.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
value | null | Object | Value of the component than can be either an EL expression of a literal text.
converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id.
immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
required | false | Boolean | Marks component as required.
validator | null | MethodExpr | A method expression that refers to a method validating the input.
valueChangeListener | null | MethodExpr | A method expression that refers to a method for handling a valuchangeevent.
requiredMessage | null | String | Message to be displayed when required field validation fails.
converterMessage | null | String | Message to be displayed when conversion fails.
validatorMessage | null | String | Message to be displayed when validation fails.
widgetVar | null | String | Name of the client side widget.
height | null | Integer | Height of the editor.
readonly | false | Boolean | Whether to instantiate the editor to read-only mode.
disabled | false | Boolean | Disables the editor.
style | null | String | Inline style of the editor container.
styleClass | null | String | Style class of the editor container.
placeholder | null | String | Placeholder text to show when editor is empty
toolbarVisible | true | Boolean | Whether the toolbar of the editor is visible.
secure | true | Boolean | Secure the component with the OWASP HTML Sanitizer library on the classpath.
allowFormatting | true | Boolean | Whether to allow formatting to be included.
allowBlocks | true | Boolean | Whether to allow blocks to be included.
allowStyles | true | Boolean | Whether to allow styles to be included.
allowLinks | true | Boolean | Whether to allow links to be included.
allowImages | true | Boolean | Whether to allow images to be included.
formats | null | List | Define a list of formats to allow in the editor. By default all formats are allowed.

## Getting started with the Editor
!> TextEditor requires the [`owasp-java-html-sanitizer`](https://search.maven.org/artifact/com.googlecode.owasp-java-html-sanitizer/owasp-java-html-sanitizer) dependency added to your pom.xml to prevent XSS.

Rich Text entered using the Editor is passed to the server using `value` expression.

```java
public class Bean {
    private String text;
    //getter and setter
}
```

```xhtml
<p:textEditor value="#{bean.text}" />
```

## Custom Toolbar
Toolbar of editor is easy to customize using the `toolbar` facet:

```xhtml
<p:textEditor ...>
    <f:facet name="toolbar">
        <span class="ql-formats">
            <select class="ql-font" />
            <select class="ql-size" />
        </span>
        <span class="ql-formats">
            <button class="ql-bold" />
            <button class="ql-italic" />
            <button class="ql-underline" />
            <button class="ql-strike" />
        </span>
        <span class="ql-formats">
            <select class="ql-color" />
            <select class="ql-background" />
        </span>
        <span class="ql-formats">
            <button class="ql-script" value="sub" />
            <button class="ql-script" value="super" />
        </span>
        <span class="ql-formats">
            <button class="ql-header" value="1" />
            <button class="ql-header" value="2" />
            <button class="ql-blockquote" />
            <button class="ql-code-block" />
        </span>
        <span class="ql-formats">
            <button class="ql-list" value="ordered" />
            <button class="ql-list" value="bullet" />
            <button class="ql-indent" value="-1" />
            <button class="ql-indent" value="+1" />
        </span>
        <span class="ql-formats">
            <button class="ql-direction" value="rtl" />
            <select class="ql-align" />
        </span>
        <span class="ql-formats">
            <button class="ql-link" />
            <button class="ql-image" />
            <button class="ql-video" />
            <button class="ql-formula" />
        </span>
        <span class="ql-formats">
            <button class="ql-clean" />
        </span>
    </f:facet>
</p:textEditor>
```

## Client Side API
Widget: _PrimeFaces.widget.Editor_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- |
enable() | - | void | Enables the component |
disable() | - | void | Disables the component |
getEditorValue() | - | void | Initializes a lazy editor, subsequent calls do not reinit the editor.
clear() | - | void | Clears the text in editor.

## Skinning
Refer to [QuillJS documentation](https://quilljs.com/guides/how-to-customize-quill/) for styling.

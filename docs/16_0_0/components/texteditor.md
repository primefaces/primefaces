# TextEditor

Editor is an input component with rich text editing capabilities based on Quill.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.TextEditor-1.html)

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
setValue(value) | value: the new value | void | Sets the value of the TextEditor |
getEditorValue() | - | void | Initializes a lazy editor, subsequent calls do not reinit the editor.
clear() | - | void | Clears the text in editor.

## Skinning
Refer to [QuillJS documentation](https://quilljs.com/guides/how-to-customize-quill/) for styling.

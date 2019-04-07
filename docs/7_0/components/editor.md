# Editor

Editor is an input component with rich text editing capabilities. (Deprecated: Use TextEditor instead)

## Info

| Name | Value |
| --- | --- |
| Tag | editor
| Component Class | org.primefaces.component.editor.Editor
| Component Type | org.primefaces.component.Editor
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.EditorRenderer
| Renderer Class | org.primefaces.component.editor.EditorRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component.
| rendered | true | Boolean | Boolean value to specify the rendering of the component.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
| value | null | Object | Value of the component than can be either an EL expression of a literal text.
| converter | null | Converter/String | An el expression or a literal text that defines a converter for the component. When it’s an EL expression, it’s resolved to a converter instance. In case it’s a static text, it must refer to a converter id.
| immediate | false | Boolean | When set true, process validations logic is executed at apply request values phase for this component.
| required | false | Boolean | Marks component as required.
| validator | null | MethodExpr | A method expression that refers to a method validationg the input.
| valueChangeListener | null | MethodExpr | A method expression that refers to a method for handling a valuchangeevent.
| requiredMessage | null | String | Message to be displayed when required field validation fails.
| converterMessage | null | String | Message to be displayed when conversion fails.
| validatorMessage | null | String | Message to be displayed when validation fails.
| widgetVar | null | String | Name of the client side widget.
| controls | null | String | List of controls to customize toolbar.
| height | null | Integer | Height of the editor.
| width | null | Integer | Width of the editor.
| disabled | false | Boolean | Disables editor.
| style | null | String | Inline style of the editor container.
| styleClass | null | String | Style class of the editor container.
| onchange | null | String | Client side callback to execute when editor data changes.
| maxlength | null | Integer | Maximum length of the raw input.

## Getting started with the Editor
Rich Text entered using the Editor is passed to the server using _value_ expression.

```java
public class Bean {
    private String text;
    //getter and setter
}
```
```xhtml
<p:editor value="#{bean.text}" />
```
## Custom Toolbar
Toolbar of editor is easy to customize using _controls_ option;

```xhtml
<p:editor value="#{bean.text}" controls="bold italic underline strikethrough" />
```

Here is the full list of all available controls;

- bold
- italic
- underline
- strikethrough
- subscript
- superscript
- font
- size
- style
- color
- highlight
- bullets
- numbering
- alignleft
- center
- alignright
    - justify
    - undo
    - redo
    - rule
    - image
    - link
    - unlink
    - cut
    - copy
    - paste
    - pastetext
    - print
    - source
    - outdent
    - indent
    - removeFormat

## Client Side API
Widget: _PrimeFaces.widget.Editor_

| Method | Params | Return Type | Description | 
| --- | --- | --- | --- | 
| init() | - | void | Initializes a lazy editor, subsequent calls do not reinit the editor.
| saveHTML() | - | void | Saves html text in iframe back to the textarea.
| clear() | - | void | Clears the text in editor.
| enable() | - | void | Enables editing.
| disable() | - | void | Disables editing.
| focus() | - | void | Adds cursor focus to edit area.
| selectAll() | - | void | Selects all text in editor.
| getSelectedHTML() | - | String | Returns selected text as HTML.
| getSelectedText() | - | String | Returns selected text in plain format.

## Skinning
Following is the list of structural style classes.

| Class | Applies | 
| --- | --- | 
| .ui-editor | Main container.
| .ui-editor-toolbar | Toolbar of editor.
| .ui-editor-group | Button groups.
| .ui-editor-button | Each button.
| .ui-editor-divider | Divider to separate buttons.
| .ui-editor-disabled | Disabled editor controls.
| .ui-editor-list | Dropdown lists.
| .ui-editor-color | Color picker.
| .ui-editor-popup | Popup overlays.
| .ui-editor-prompt | Overlays to provide input.
| .ui-editor-message | Overlays displaying a message.

Editor is not integrated with ThemeRoller as there is only one icon set for the controls.

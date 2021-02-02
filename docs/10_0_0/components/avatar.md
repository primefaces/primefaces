# Avatar

Avatar represents people using icons, labels and images.

## Info

| Name | Value |
| --- | --- |
| Tag | avatar
| Component Class | org.primefaces.component.avatar.Avatar
| Component Type | org.primefaces.component.Avatar
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.AvatarRenderer
| Renderer Class | org.primefaces.component.avatar.AvatarRenderer

## Attributes

| Name | Default | Type | Description |
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
| label | null | String | Defines the text to display.
| icon | null | String | Defines the icon to display.
| size | null | String | Size of the element, valid options are "large" and "xlarge".
| shape | square | String | Shape of the element, valid options are "square" and "circle".
| style | null | String | Style of the avatar.
| styleClass | null | String | StyleClass of the avatar.
| dynamicColor | false | Boolean | Dynamically assign contrasting foreground and background colors based on the label. Default is false.

## Getting Started
Avatar has three built-in display modes; "label", "icon" and custom content.

```xhtml
<p:avatar label="P"/>
<p:avatar icon="pi pi-search"/>
```

## Images
```xhtml
<p:avatar>
   <p:graphicImage name="user.png" />
</p:avatar>
```

## Sizes
```size``` property defines the size of the Avatar with "large" and "xlarge" as possible values.

```xhtml
<p:avatar label="P" size="large"/>
```

## AvatarGroup
A set of Avatars can be displayed together using the ```p:avatarGroup``` component.

```xhtml
<p:avatarGroup>
    <p:avatar label="P"/>
    <p:avatar icon="pi pi-search"/>
    <p:avatar image="user.png"/>
    <p:avatar label="+2"/>
</p:avatarGroup>
```

## Shape
Avatar comes in two different styles specified with the ```shape``` property, "square" is the default and "circle" is the alternative.

```xhtml
<p:avatar label="P" shape="circle"/>
```

## Badge
A badge can be added to an Avatar.

```xhtml
<p:badge value="4">
    <p:avatar icon="pi pi-user" size="xlarge"/>
</p:badge>
```

## Templating
Content can easily be customized with the default slot instead of using the built-in modes.

```xhtml
<p:avatar>
    Content
</p:avatar>
```

## Skinning of Avatar
Avatar resides in a main container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Name | Element |
| --- | --- |
|.ui-avatar | Container element.
|.ui-avatar-circle | Container element with a circle shape.
|.ui-avatar-text | Text of the Avatar.
|.ui-avatar-icon | Icon of the Avatar.
|.ui-avatar-lg | Container element with a large size.
|.ui-avatar-xl | Container element with an xlarge size.

## Skinning of AvatarGroup

| Name | Element |
| --- | --- |
|.ui-avatar-group | Container element.
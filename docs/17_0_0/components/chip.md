# Chip

Chip represents entities using icons, labels and images.

## Getting Started
Chip can display labels, icons and images.

```xhtml
<p:chip value="2"></p:chip>
<p:chip value="2">
    <i class="pi pi-bell"/>
</p:chip>
```

## Removable
Setting ```removable``` property is "true", displays an icon to close the chip, the optional ```remove``` event is 
available to get notified when a chip is hidden.

```xhtml
<p:chip label="text" removable="true"></p:chip>
```

The remove icon is rendered as `<span class="p-icon p-close-icon p-chip-remove-icon">` and shows
the icon token `--p-chip-remove-icon` (default `--p-icon-clear`, see [Theming](/core/theming.md?id=icons)); the former
`removeIcon` attribute is gone.

## Templating
Content can easily be customized with the default slot instead of using the built-in modes.

```xhtml
<p:chip>
    Content
</p:chip>
```

## Skinning of Chip
Chip resides in a main container element which _style_ and _styleClass_ options apply. As skinning
style classes are global, see the main theming section for more information. Following is the list of
structural style classes;

| Name | Element |
| --- | --- |
|.p-chip | Container element.
|.p-chip-image | Container element in image mode.
|.p-chip-text | Text of the chip.
|.p-chip-remove-icon | Remove icon.

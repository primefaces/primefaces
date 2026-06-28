# HotKey

HotKey is a generic key binding component that can bind any formation of keys to javascript event
handlers or ajax calls.

## Getting Started with HotKey
HotKey is used in two ways, either on client side with the event handler or with ajax support.
Simplest example would be;

```xhtml
<p:hotkey bind="a" handler="alert('Pressed a');" />
```

When this hotkey is on page, pressing the `a` key will alert the ‘Pressed a’ text.

## Bind values
A string key name (combination) is allowed. Combinations are separated with the `+` character. For example:

```xhtml
<p:hotkey bind="ctrl+s" handler="alert('Pressed ctrl+s');" />
<p:hotkey bind="ctrl+shift+s" handler="alert('Pressed ctrl+shift+s')" />
```

## Key names
The following key names are supported:
`a .. z`,
`0 .. 9`,
`meta`,
`backspace`,
`tab`,
`return`,
`shift`,
`ctrl`,
`alt`,
`pause`,
`capslock`,
`esc`,
`space`,
`pageup`,
`pagedown`,
`end`,
`home`,
`left`,
`up`,
`right`,
`down`,
`insert`,
`del`,
`;`,
`=`,
`*`,
`+`,
`-`,
`.`,
`/`,
`f1 .. f12`,
`numlock`,
`scroll`,
`-`,
`;`,
`=`,
`,`,
`-`,
`.`,
`/`,
`` ` ``,
`[`,
`\`,
`]`,
`'`.

## Integration
Here’s an example demonstrating how to integrate hotkeys with a client side API. Using left and
right keys will switch the images displayed via the `p:imageSwitch` component.

```xhtml
<p:hotkey bind="left" handler="PF('switcher').previous();" />
<p:hotkey bind="right" handler="PF('switcher').next();" />
<p:imageSwitch widgetVar="switcher">
    //content
</p:imageSwitch>
```

## Ajax Support
Ajax is a built-in feature of HotKeys meaning you can do ajax calls with key combinations.
Following form can be submitted with the `ctrl+shift+s` combination.

```xhtml
<h:form>
    <p:hotkey bind="ctrl+shift+s" update="display" />
    <h:panelGrid columns="2">
        <h:outputLabel for="name" value="Name:" />
        <h:inputText id="name" value="#{bean.name}" />
    </h:panelGrid>
    <h:outputText id="display" value="Hello: #{bean.firstname}" />
</h:form>
```

> :warning: Hotkey will not be triggered if there is a focused input on page.

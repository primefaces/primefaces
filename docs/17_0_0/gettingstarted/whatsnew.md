# What is new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 17.0.0

### PrimeFaces

Look into [migration guide](https://primefaces.github.io/primefaces/17_0_0/#/../migrationguide/17_0_0) for more enhancements and changes.

* Theming
    * New [token based theming](/core/theming.md) with the themes Aura, Lara, Nora, Metal and Glass: pure CSS design tokens, light and dark mode, primary color, density and icons customizable with CSS variables. The themes are part of the `primefaces` jar and replace the SASS based themes (`primefaces-themes`).
    * Works together with [Tailwind CSS](/core/tailwind.md) v4: the utilities use the tokens of the theme.
* Tag
    * Added `outlined` property to display the tag with an outlined style.
* New components
    * [InputOtp](/components/inputotp.md): input for one-time passwords and PINs, one input per character.
    * [InputPhone](/components/inputphone.md): input for international phone numbers with a country selector, the value is the number in the E.164 format, validated in the browser (also with client side validation) and, with Google's libphonenumber, on the server.

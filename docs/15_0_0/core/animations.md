# Animations

Many of the PrimeFaces components use animations to create dynamic interactions such as fading or sliding.

Animations in newer components are hardcoded per component via CSS transitions.
In theorie they could be changed by overwriting PrimeFaces internal CSS but we don't offer a documentation for it, as it would be very much effort to provide this for every component.

Some older components are still based on JQuery animations and have attributes to configure it (e.g. `effect` and `effectDuration`):
```xml
<p:dialog showEffect="fade" hideEffect="fade" />
```

## Accessibility considerations: prefers-reduced-motion

PrimeFaces respects user accessibility preferences, particularly the `prefers-reduced-motion` CSS media query.
This global setting is used to detect whether a user has requested reduced motion animations in their system settings.
When prefers-reduced-motion is enabled, PrimeFaces will automatically disable animations to ensure a more comfortable experience for users with motion sensitivity.

## disable / enable animations 

Its also possible to override the `prefers-reduced-motion` setting via JS:

```js
PrimeFaces.utils.disableAnimations();
PrimeFaces.utils.enableAnimations();
```

This does however only work for component animations and will not influence e.g. CSS transitions of PrimeIcons.

## Inline load animation

Some components support an inline load animation. To avoid flickering on short loading times, a minimum animation
duration is defined. It's 500 milliseconds by default, but you can customize it. For example

```js
PrimeFaces.ajax.minLoadAnimation = 250;
```

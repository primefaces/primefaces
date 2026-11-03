import { createThemeStore, type ThemeStore } from "./src/theme.js";

declare global {
    interface Window {
        /**
         * The theme settings of the user (theme configurator), see `primefaces.THEME_CONFIGURATOR`.
         */
        PrimeFacesTheme: ThemeStore;
    }
}

// before the first paint; only once, also when the script is included twice
if (!window.PrimeFacesTheme) {
    const store = createThemeStore(document.documentElement);
    window.PrimeFacesTheme = store;
    store.apply(store.load());
}

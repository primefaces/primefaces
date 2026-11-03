import { setup } from "./src/themeconfigurator.js";

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", setup);
}
else {
    setup();
}

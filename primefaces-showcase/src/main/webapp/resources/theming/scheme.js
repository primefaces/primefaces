/*
 * data-dark on <html>: the effective color scheme for the showcase layout (code highlighting). Included by the templates
 * with data-p-no-move, after the theme.js of PrimeFaces that applies the stored color scheme, so it is set before the
 * first paint; updated on changes of the configurator (p-theme-change) and of the system setting.
 */
(function () {
    var root = document.documentElement;
    var media = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)');

    function sync() {
        var scheme = root.style.colorScheme;
        var dark = scheme ? scheme === 'dark' : !!(media && media.matches);
        root.setAttribute('data-dark', dark ? 'true' : 'false');
    }

    sync();
    if (media) {
        media.addEventListener('change', sync);
    }
    document.addEventListener('p-theme-change', sync);
})();

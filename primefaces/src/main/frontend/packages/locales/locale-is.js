import { is } from "primelocale/js/is.js";

if (window.PrimeFaces) {
    /** Icelandic (from PrimeLocale)*/
    PrimeFaces.locales["is"] = is;

    // custom PF labels
    PrimeFaces.locales["is"] = $.extend(true, {}, PrimeFaces.locales["is"], {
      aria: {
        movedBottom: "Fært neðst: {count}",
        movedDown: "Fært niður: {count}",
        movedTop: "Fært efst: {count}",
        movedToSource: "Fært í uppruna: {count}",
        movedToTarget: "Fært í markmið: {count}",
        movedUp: "Fært upp: {count}",
      },
    });
}
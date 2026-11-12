import { is } from "primelocale/js/is.js";

if (window.PrimeFaces) {
    /** Icelandic (from PrimeLocale)*/
    PrimeFaces.locales["is"] = is;

    // custom PF labels
    PrimeFaces.locales["is"] = $.extend(true, {}, PrimeFaces.locales["is"], {
      aria: {
        movedBottom: "Fært neðst",
        movedDown: "Fært niður",
        movedTop: "Fært efst",
        movedToSource: "Fært í uppruna",
        movedToTarget: "Fært í markmið",
        movedUp: "Fært upp",
      },
    });
}
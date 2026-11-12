import { et } from "primelocale/js/et.js";

if (window.PrimeFaces) {
    /** Estonian (from PrimeLocale)*/
    PrimeFaces.locales["et"] = et;

    // custom PF labels
    PrimeFaces.locales["et"] = $.extend(true, {}, PrimeFaces.locales["et"], {
      aria: {
        movedBottom: "Liigutatud lõppu",
        movedDown: "Liigutatud alla",
        movedTop: "Liigutatud algusesse",
        movedToSource: "Liigutatud allikasse",
        movedToTarget: "Liigutatud sihtkohta",
        movedUp: "Liigutatud üles",
      },
    });
}
import { et } from "primelocale/js/et.js";

if (window.PrimeFaces) {
    /** Estonian (from PrimeLocale)*/
    PrimeFaces.locales["et"] = et;

    // custom PF labels
    PrimeFaces.locales["et"] = $.extend(true, {}, PrimeFaces.locales["et"], {
      aria: {
        movedBottom: "Liigutatud lõppu: {count}",
        movedDown: "Liigutatud alla: {count}",
        movedTop: "Liigutatud algusesse: {count}",
        movedToSource: "Liigutatud allikasse: {count}",
        movedToTarget: "Liigutatud sihtkohta: {count}",
        movedUp: "Liigutatud üles: {count}",
      },
    });
}
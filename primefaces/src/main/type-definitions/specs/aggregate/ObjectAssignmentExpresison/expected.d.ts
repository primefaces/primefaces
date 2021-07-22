declare namespace PrimeFaces.ObjectAssignmentExpresison {
    /**
     * iface foo
     */
    export interface Foo {
        /**
         * foo prop x
         */
        x: number;
    }
}

declare namespace PrimeFaces.ObjectAssignmentExpresison.Other {
    /**
     * iface bar
     */
    export interface Bar2 {
        /**
         * bar prop x
         */
        x: number;
    }
}

declare namespace PrimeFaces.ObjectAssignmentExpresison {
    /**
     * class hoge
     */
    export class Hoge {
        /**
         * hoge prop x
         */
        x: number;
    }
}

declare namespace PrimeFaces.ObjectAssignmentExpresison.Other {
    /**
     * class hogera
     */
    export class Hogera2 {
        /**
         * hogera prop x
         */
        x: number;
    }
}

/**
 * namespace bazbar
 */
declare namespace PrimeFaces.ObjectAssignmentExpresison.Bazbar {
    /**
     * bazbar prop x
     */
    export let x: number;
}
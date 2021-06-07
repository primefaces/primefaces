declare namespace PrimeFaces.widget {
    /**
     * Tests the ability to create an interface for sub props
     */
    export class PropSubCreateIface {
        /**
         * prop bar
         */
        bar: Prop.Sub.CreateIface.Bar;
    }
}
declare namespace Prop.Sub.CreateIface {
    /**
     * iface bar
     */
    export interface Bar {
        /**
         * prop bar-x
         */
        x: string;
        /**
         * prop bar-y
         */
        y: number;
        /**
         * prop bar-z
         */
        z: Prop.Sub.CreateIface.Z;
    }
}
declare namespace Prop.Sub.CreateIface {
    /**
     * iface z
     */
    export interface Z {
        /**
         * prop bar-z-a1
         */
        a1: boolean;
        /**
         * prop bar-z-a2
         */
        a2: boolean;
    }
}
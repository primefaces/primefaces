declare namespace PrimeFaces.widget {
    /**
     * Tests the ability to create a class for sub props
     */
    export class PropSubCreateClass {
        /**
         * prop bar
         */
        bar: Prop.Sub.CreateClass.Bar;
    }
}
declare namespace Prop.Sub.CreateClass {
    /**
     * class bar
     */
    export class Bar {
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
        z: Prop.Sub.CreateClass.Z;
    }
}
declare namespace Prop.Sub.CreateClass {
    /**
     * class z
     */
    export class Z {
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
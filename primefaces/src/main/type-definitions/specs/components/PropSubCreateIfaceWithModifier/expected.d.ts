declare namespace PrimeFaces.widget {
    /**
     * Tests the ability to create an interface for sub props with modifiers
     */
    export class PropSubCreateIfaceWithModifier {
        /**
         * prop bar
         */
        bar: Prop.Sub.CreateIfaceMod.IBar;
    }
}
declare namespace Prop.Sub.CreateIfaceMod {
    /**
     * class bar
     * @typeparam T bar type T
     */
    export interface Bar<T> extends Prop.Sub.CreateIfaceMod.BarIface1, Prop.Sub.CreateIfaceMod.BarIface2 {
        /**
         * prop bar-x
         */
        x: T;
        /**
         * prop bar-y
         */
        y: number;
    }
}
declare namespace PrimeFaces.widget {
    /**
     * Tests the ability to create a class for sub props with modifiers
     */
    export class PropSubCreateClassWithModifier {
        /**
         * prop bar
         */
        bar: Prop.Sub.CreateClassMod.IBar;
    }
}
declare namespace Prop.Sub.CreateClassMod {
    /**
     * class bar
     * @typeparam T bar type T
     */
    export abstract class Bar<T> extends Prop.Sub.CreateClassMod.BarExtend implements Prop.Sub.CreateClassMod.BarIface1, Prop.Sub.CreateClassMod.BarIface2 {
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
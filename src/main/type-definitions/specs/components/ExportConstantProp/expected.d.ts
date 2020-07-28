declare namespace PrimeFaces.widget {
    /**
     * Tests export a property as an additional const
     */
    export class ExportConstantProp {
        /**
         * prop hoge
         */
        hoge: number;
        /**
         * prop bar
         */
        bar: string;
    }
}

declare namespace PrimeFaces.widget {
    /**
     * const hoge
     */
    export const exportConstantPropHoge: number;
}

declare namespace PrimeFaces.widget {
    /**
     * const bar
     */
    export const exportConstantPropBar: string;
}
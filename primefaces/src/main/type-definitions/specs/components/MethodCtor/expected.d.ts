declare namespace PrimeFaces.widget {
    /**
     * Tests the constructor modifier on a method
     */
    export class MethodCtor {
        /**
         * ctor bar
         * @typeparam S bar template
         * @param arg1 bar arg1
         * @param arg2 bar arg2
         */
        constructor<S extends number>(arg1: number, arg2: Record<string, S>);
        /**
         * ctor foo
         * @typeparam T foo template
         * @param arg1 foo arg1
         * @param arg2 foo arg2
         */
        constructor<T extends string>(arg1: number, arg2: T[]);
    }
}
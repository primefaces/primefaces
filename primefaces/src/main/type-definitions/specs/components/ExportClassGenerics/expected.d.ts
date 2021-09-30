declare namespace PrimeFaces.widget {
    /**
     * Tests generics on a class
     * @typeparam T1 type T1
     * @typeparam T2 type T2
     * @typeparam T3 Defaults to `T1`. type T3
     * @typeparam T4 Defaults to `T3`. type T4
     */
    export class ExportClassGenerics<T1, T2 extends keyof T1, T3 = T1, T4 extends T3 = T3> {
    }
}
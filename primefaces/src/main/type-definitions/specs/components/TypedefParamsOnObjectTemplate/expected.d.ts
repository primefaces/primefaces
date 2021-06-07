declare namespace Test.TypedefParamsOnObjectTemplate.Extern {
    /**
     * type foobar
     * @typeparam T template T
     */
    export type Foobar<T> =
    /**
     * @param x param x
     * @return foobar return
     */
    (x: T[]) => T;
}
declare namespace PrimeFaces.widget {
    /**
     * Tests external typedef on an object
     */
    export class TypedefParamsOnObjectTemplate {
    }
}
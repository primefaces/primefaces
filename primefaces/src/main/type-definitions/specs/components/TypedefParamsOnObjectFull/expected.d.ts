declare namespace Test.TypedefParamsOnObjectFull.Extern {
    /**
     * type foobar
     * @typeparam R template R
     */
    export type Foobar<R extends string> =
    /**
     * @typeparam T template T
     * @param param2 param pattern 2
     * @param x param x
     * @param y param y
     * @param z1 param z1
     * @param z2 param z2
     * @yield foobar yield
     * @return foobar return
     */
    <T>(this: string, x: number, y: T[], [z1, z2]: [number, boolean]) => AsyncGenerator<RegExp, string>;
}
declare namespace PrimeFaces.widget {
    /**
     * Tests external typedef on an object
     */
    export class TypedefParamsOnObjectFull {
    }
}
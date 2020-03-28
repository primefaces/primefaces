declare namespace Test.TypedefParamsOnObjectSimple.Extern {
    /**
     * type foobar
     */
    export type Foobar =
    /**
     * @param x param x
     * @param y param y
     * @return foobar return
     */
    (x: number, y: boolean) => string;
}
declare namespace PrimeFaces.widget {
    /**
     * Tests external typedef on an object
     */
    export class TypedefParamsOnObjectSimple {
    }
}
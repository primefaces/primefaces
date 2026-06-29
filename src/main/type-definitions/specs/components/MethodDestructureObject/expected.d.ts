declare namespace PrimeFaces.widget {
    /**
     * Typedef obj
     */
    export type DesObj = {r: boolean, s: boolean};
    /**
     * Tests destructuring objects in methods
     */
    export class MethodDestructureObject {
        /**
         * method bar
         * @param param1 bar param1 
         * @param param3 bar param3 
         * @param x bar param x
         * @param a bar param a
         * @param b bar param b
         * @param c1 bar param c1
         * @param c2 bar param c2
         * @param y bar param y
         * @param r bar param r
         * @param s bar param s
         */
        bar(x: number, {a, b2: b, c: {c1, c2}}: {a: number, b2: string, c: {c1: boolean, c2: boolean}}, y: RegExp, {r, s}: DesObj): void;

        /**
         * method foo
         * @param param1 foo param1 
         * @param param3 foo param3 
         * @param x foo param x
         * @param a foo param a
         * @param b foo param b
         * @param c1 foo param c1
         * @param c2 foo param c2
         * @param y foo param y
         * @param r foo param r
         * @param s foo param s
         */
        foo(x: number, {a, b2: b, c: {c1, c2}}: {a: number, b2: string, c: {c1: boolean, c2: boolean}}, y: RegExp, {r, s}: DesObj): void;
    }
}
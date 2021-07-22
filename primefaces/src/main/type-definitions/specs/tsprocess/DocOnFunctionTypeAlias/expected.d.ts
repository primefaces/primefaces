declare namespace DocOnFunctionTypeAlias {
    /**
     * Type fn
     */
    export type Fn =
    /**
     * @typeparam T type T
     * @param param x
     * @param param y
     * @return retval
     */
    <T>(x: number, y: string) => T;
}
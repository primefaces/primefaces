/**
 * Tests initializers in destructured parameter
 */
({
    /**
     * method foo
     * @pattern {number[]} 0 foo pattern 0
     * @pattern {Record<string, boolean>} 1 foo pattern 1
     * @param x foo param x
     * @param y foo param y
     */
    foo([x=42], {y = true}) {},
    /**
     * method bar
     * @pattern {number[]} 0 bar pattern 0
     * @pattern {Record<string, string>} 1 bar pattern 1
     * @param [x=42] bar param x
     * @param [y=true] bar param y
     */
    bar([x], {y}) {}
})
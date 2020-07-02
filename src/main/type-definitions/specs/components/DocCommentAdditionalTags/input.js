/**
 * Tests the ability to add arbitrary additional tags
 * 
 * @foo . tag foo
 * 
 * @prop {number} x prop x
 * @foox x tag foox
 * 
 * @method y method y
 * @fooy y tag fooy
 */
({
    /**
     * prop bar
     * 
     * @type {number}
     * @bar . tag bar
     */
    bar: 0,
    /**
     * method baz
     * 
     * @baz tag baz
     */
    baz(){}
})
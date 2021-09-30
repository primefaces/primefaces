declare namespace ValidateDefinitivePropsForcedSuccess {
    /**
     * @validateforcedprops json ["ccc"]
     * @validatedefinitiveprops json {}
     */
     class FooClass {
        ccc: boolean;
    }
    /**
     * @validateforcedprops json ["aaa", "bbb"]
     * @validatedefinitiveprops json {}
     */
    class BarClass extends FooClass {
        aaa: string;
        bbb: number;
    }
}
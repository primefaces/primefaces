declare namespace ValidateDefinitivePropsSuperfluous {
    /**
     * @validatedefinitiveprops json {}
     */
    class BaseClass {
        base: boolean;
    }
    /**
     * @validatedefinitiveprops json {}
     */
    class FooClass extends BaseClass {
        foo: number;
    }
    /**
     * @validatedefinitiveprops json {}
     */
    class BarClass extends FooClass {
        bar: string;
    }
}
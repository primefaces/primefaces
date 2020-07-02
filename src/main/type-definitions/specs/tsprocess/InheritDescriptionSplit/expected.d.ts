declare namespace InheritDescriptionSplit {
    /**
     * interface foo 1
     */
    interface Foo {
        /**
         * method foo 1
         */
        foo(): string;
    }
    /**
     * interface foo 2
     */
    interface Foo {
        /**
         * method foo 2
         */
        foo(): string;
    }

    /**
     * interface bar
     * 
     * (from super type Baz) interface baz
     * 
     * (from super type Foo) interface foo 1
     * 
     * (from super type Foo) interface foo 2
     */
    interface Bar extends Foo, Baz {
        /**
         * method bar
         * 
         * (from super type Baz) method baz
         * 
         * (from super type Foo) method foo 1
         * 
         * (from super type Foo) method foo 2
         */
        foo(): string;
    }

    /**
     * interface baz
     */
    interface Baz {
        /**
         * method baz
         */
        foo(): string;
    }
}
declare namespace InheritReturn {
    interface Foo {
        /**
         * @return Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam laoreet pretium diam quis
         * ullamcorper. In vestibulum elit sed mauris dapibus, sit amet feugiat elit egestas. Nulla commodo nibh vel
         * odio.
         */
        foo(): string;
        /**
         * @return Foo#bar retval
         */
        bar(): string;
        baz(): string;
    }
    interface Bar extends Foo {
        /**
         * @return Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam laoreet pretium diam quis
         * ullamcorper. In vestibulum elit sed mauris dapibus, sit amet feugiat elit egestas. Nulla commodo nibh vel
         * odio.
         */
        foo(): string;
        /**
         * @return Bar#bar retval
         * 
         * (from super type Foo) Foo#bar retval
         */
        bar(): string;
        /**
         * method Bar#baz
         */
        baz(): string;
    }
}
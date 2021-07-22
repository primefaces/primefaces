declare namespace InheritParam {
    interface Foo {
        /**
         * @param x Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam laoreet pretium diam quis
         * ullamcorper. In vestibulum elit sed mauris dapibus, sit amet feugiat elit egestas. Nulla commodo nibh vel
         * odio.
         */
        foo(x): void;
        /**
         * @param y Foo#bar param y
         */
        bar(y): void;
        baz(): void;
    }
    interface Bar extends Foo {
        /**
         * @param x Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam laoreet pretium diam quis
         * ullamcorper. In vestibulum elit sed mauris dapibus, sit amet feugiat elit egestas. Nulla commodo nibh vel
         * odio.
         */
        foo(x): void;
        /**
         * @param y Bar#bar param y
         * 
         * (from super type Foo) Foo#bar param y
         */
        bar(y): void;
        /**
         * method Bar#baz
         */
        baz(): void;
    }
}
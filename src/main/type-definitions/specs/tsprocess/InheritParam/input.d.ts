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
         * @inheritdoc
         */
        foo(x): void;
        /**
         * @inheritdoc
         * @param y Bar#bar param y
         */
        bar(y): void;
        /**
         * method Bar#baz
         * @inheritdoc
         */
        baz(): void;
    }
}
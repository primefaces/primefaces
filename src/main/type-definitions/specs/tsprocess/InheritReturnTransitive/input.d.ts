declare namespace InheritReturn {
    interface AbstractBase {
        /**
         * @return AbstractBase#hoge retval
         */
        hoge(): number;
    }
    interface Base extends AbstractBase {
        /**
         * @return Base#hoge retval
         */
        hoge(): number;
    }
    interface Foo extends Base {
    }
    interface Bar extends Foo {
        /**
         * @inheritdoc
         */
        hoge(): number;
    }
}
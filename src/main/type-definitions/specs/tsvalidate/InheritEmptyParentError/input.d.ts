declare namespace InheritEmptyParentError {
    interface C1 {
        /**
         * @return
         */
        foo(): number;
    }
    interface C2 extends C1 {
        /**
         * @override
         * @inheritdoc
         */
        foo(): number;
    }
}
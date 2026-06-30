declare namespace InheritDescriptionComplex {
    /**
     * c1
     * @inheritdoc
     */
    interface C1 extends C3, C2 {
        /**
         * c1 foo
         * @inheritdoc
         */
        foo(): void;
    }
    /**
     * c2
     * @inheritdoc
     */
    interface C2 extends C4 {
        /**
         * c2 foo
         * @inheritdoc
         */
        foo(): void;
    }
    /**
     * c3
     * @inheritdoc
     */
    interface C3 extends C5 {
        /**
         * c3 foo
         * @inheritdoc
         */
        foo(): void;
    }
    /**
     * c4
     */
    interface C4 {
        /**
         * c4 foo
         */
        foo(): void;
    }
    /**
     * c5
     * @inheritdoc
     */
    interface C5 extends C6 {
        /**
         * c5 foo
         * @inheritdoc
         */
        foo(): void;
    }
    /**
     * c6
     */
    interface C6 {
        /**
         * c6 foo
         */
        foo(): void;
    }
}
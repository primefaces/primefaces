declare namespace InheritCircular {
    /**
     * @inheritdoc
     */
    interface C1 extends C3 {
    }
    /**
     * @inheritdoc
     */
    interface C2 extends C1 {
    }
    /**
     * @inheritdoc
     */
    interface C3 extends C2 {
    }
}
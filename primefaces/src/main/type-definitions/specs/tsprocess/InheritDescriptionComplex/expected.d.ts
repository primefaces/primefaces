declare namespace InheritDescriptionComplex {
    /**
     * c1
     * 
     * (from super type C2) c2
     * 
     * (from super type C4) c4
     * 
     * (from super type C3) c3
     * 
     * (from super type C5) c5
     * 
     * (from super type C6) c6
     */
    interface C1 extends C3, C2 {
        /**
         * c1 foo
         * 
         * (from super type C2) c2 foo
         * 
         * (from super type C4) c4 foo
         * 
         * (from super type C3) c3 foo
         * 
         * (from super type C5) c5 foo
         * 
         * (from super type C6) c6 foo
         */
        foo(): void;
    }
    /**
     * c2
     * 
     * (from super type C4) c4
     */
    interface C2 extends C4 {
        /**
         * c2 foo
         * 
         * (from super type C4) c4 foo
         */
        foo(): void;
    }
    /**
     * c3
     * 
     * (from super type C5) c5
     * 
     * (from super type C6) c6
     */
    interface C3 extends C5 {
        /**
         * c3 foo
         * 
         * (from super type C5) c5 foo
         * 
         * (from super type C6) c6 foo
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
     * 
     * (from super type C6) c6
     */
    interface C5 extends C6 {
        /**
         * c5 foo
         * 
         * (from super type C6) c6 foo
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
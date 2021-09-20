declare namespace PrimeFaces.widget {
    /**
     * Tests that private methods are recognized.
     */
    export class ClassFieldsPrivateMethod {
        private _zing(): void;
        met1(): void;
    }
}
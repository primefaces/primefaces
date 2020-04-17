declare namespace InheritDocClassSuccess {
    /**
     * foocfg
     */
    interface FooCfg {
        foo: string;
    }
    /**
     * @inheritdoc
     */
    interface BarCfg extends FooCfg {
        bar: boolean;
    }
    /**
     * @inheritdoc
     */
    interface BazCfg extends BarCfg {
        baz: number;
    }
    /**
     * foo
     */
    interface Foo<T extends FooCfg = FooCfg> {
        cfg: T;
    }
    /**
     * @inheritdoc
     */
    interface Bar<T extends BarCfg = BarCfg> extends Foo<T> {
        hoge(): string;
    }
    /**
     * @inheritdoc
     */
    interface Baz extends Bar<BazCfg> {
        hogera(): boolean;
    }
}
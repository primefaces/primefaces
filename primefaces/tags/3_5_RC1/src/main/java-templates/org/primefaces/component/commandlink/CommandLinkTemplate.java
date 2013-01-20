
    public final static String STYLE_CLASS = "ui-commandlink ui-widget";
    public final static String DISABLED_STYLE_CLASS = "ui-commandlink ui-widget ui-state-disabled";

    public boolean isPartialSubmitSet() {
        return (getStateHelper().get(PropertyKeys.partialSubmit) != null) || (this.getValueExpression("partialSubmit") != null);
    }
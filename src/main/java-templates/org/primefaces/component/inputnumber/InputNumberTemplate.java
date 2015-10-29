
    public final static String STYLE_CLASS = "ui-inputnumber ui-widget";

    @Override
	public String getInputClientId() {
		return getClientId() + "_input";
	}

    @Override
    public String getValidatableInputClientId() {
        return getClientId() + "_hinput";
    }

    @Override
    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }

    @Override
    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    } 

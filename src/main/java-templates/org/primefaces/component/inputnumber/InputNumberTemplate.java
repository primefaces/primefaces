import java.text.DecimalFormatSymbols;
import java.util.Locale;

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

    public String getDecimalSeparator() {
        return (String) getStateHelper().eval("decimalSeparator", getCalculatedDecimalSepartor());
    }

    public void setDecimalSeparator(final String decimalSeparator) {
        getStateHelper().put("decimalSeparator", decimalSeparator);
    }

    public String getThousandSeparator() {
        return (String) getStateHelper().eval("thousandSeparator", getCalculatedThousandSeparator());
    }

    public void setThousandSeparator(final String thousandSeparator) {
        getStateHelper().put("thousandSeparator", thousandSeparator);
    }

    private String getCalculatedDecimalSepartor(){
        String decimalSeparator = (String) getStateHelper().eval("decimalSeparator", null);
        if (decimalSeparator==null){
            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale);
            decimalSeparator = Character.toString(decimalFormatSymbols.getDecimalSeparator());
        }
        return decimalSeparator;
    }

    private String getCalculatedThousandSeparator(){
        String thousandSeparator = (String) getStateHelper().eval("thousandSeparator", null);
        if (thousandSeparator==null){
            Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale);
            thousandSeparator =  Character.toString(decimalFormatSymbols.getGroupingSeparator());
        }
        return thousandSeparator;
    }


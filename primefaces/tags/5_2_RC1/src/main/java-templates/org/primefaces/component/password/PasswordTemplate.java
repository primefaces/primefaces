import javax.faces.component.UIComponent;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.MessageFactory;


    public final static String STYLE_CLASS = "ui-inputfield ui-password ui-widget ui-state-default ui-corner-all";
    public final static String MOBILE_STYLE_CLASS = "ui-input-text ui-body-inherit ui-corner-all ui-shadow-inset ui-input-has-clear";
    public final static String MOBILE_CLEAR_ICON_CLASS = "ui-input-clear ui-btn ui-icon-delete ui-btn-icon-notext ui-corner-all ui-input-clear-hidden";
    public final static String INVALID_MATCH_KEY = "primefaces.password.INVALID_MATCH";

    @Override
	protected void validateValue(FacesContext context, Object value) {
		super.validateValue(context, value);
        String match = this.getMatch();
        Object submittedValue = this.getSubmittedValue();

        if(isValid() && match != null) {
        	Password matchWith = (Password) SearchExpressionFacade.resolveComponent(context, this, match);

            if(submittedValue != null && !submittedValue.equals(matchWith.getSubmittedValue())) {
                this.setValid(false);
                matchWith.setValid(false);

                String validatorMessage = getValidatorMessage();
                FacesMessage msg = null;

                if(validatorMessage != null) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessage, validatorMessage);
                }
                else {
                    Object[] params = new Object[2];
                    params[0] = MessageFactory.getLabel(context, this);
                    params[1] = MessageFactory.getLabel(context, matchWith);

                    msg = MessageFactory.getMessage(Password.INVALID_MATCH_KEY, FacesMessage.SEVERITY_ERROR, params);
                }

                context.addMessage(getClientId(context), msg);
            }
        }
	}
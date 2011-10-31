import javax.faces.component.UIComponent;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import org.primefaces.util.MessageFactory;

    public final static String STYLE_CLASS = "ui-inputfield ui-password ui-widget ui-state-default ui-corner-all";
    public final static String PLAIN_STYLE_CLASS = "ui-password";

    public final static String INVALID_MATCH_KEY = "primefaces.password.INVALID_MATCH";

    @Override
	protected void validateValue(FacesContext context, Object value) {
		super.validateValue(context, value);
        String match = this.getMatch();

        if(isValid() && match != null) {
            Password matchWith = (Password) this.findComponent(match);
            if(matchWith == null) {
                throw new FacesException("Cannot find component " + match + " in view.");
            }

            Object matchValue = matchWith.getValue();

            if(value != null && !value.equals(matchValue)) {
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
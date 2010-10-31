
    public final static String PUBLIC_KEY = "primefaces.PUBLIC_CAPTCHA_KEY";
    public final static String PRIVATE_KEY = "primefaces.PRIVATE_CAPTCHA_KEY";
    public final static String INVALID_MESSAGE_ID = "primefaces.captcha.INVALID";

	protected void validateValue(FacesContext facesContext, Object value) {
		if(!hasCaptchaValidator()) {
			addValidator(new org.primefaces.component.captcha.CaptchaValidator());
		}
		
		super.validateValue(facesContext, value);
	}
	
	private boolean hasCaptchaValidator() {
		for(javax.faces.validator.Validator validator : getValidators()) {
			if(validator instanceof org.primefaces.component.captcha.CaptchaValidator)
				return true;
		}
		
		return false;
	}
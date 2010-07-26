	
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
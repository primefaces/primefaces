import java.util.Map;
import org.primefaces.util.Constants;

	/**
	 * Process only tabview if it's a tabswitch request
	 */
	public void processDecodes(FacesContext facesContext) {
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		String ajaxSourceValue = params.get(Constants.PARTIAL_SOURCE_PARAM);
		
		//TabSwitch Request
		if(ajaxSourceValue != null && ajaxSourceValue.equals(getClientId(facesContext))) {
			 decode(facesContext);
			 facesContext.renderResponse();
		} else {
			super.processDecodes(facesContext);
		}
	}
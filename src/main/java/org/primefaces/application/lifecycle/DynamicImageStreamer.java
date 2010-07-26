package org.primefaces.application.lifecycle;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.StreamedContent;

public class DynamicImageStreamer implements PhaseListener {

	public final static String DYNAMICIMAGE_PARAM = "primefacesDynamicImage";
	public final static String CONTENTTYPE_PARAM = "contentType";
	
	private Logger logger = Logger.getLogger(DynamicImageStreamer.class.getName());

	public void afterPhase(PhaseEvent phaseEvent) {
		FacesContext facesContext = phaseEvent.getFacesContext();
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		
		if(params.containsKey(DYNAMICIMAGE_PARAM)) {
			ELContext elContext = facesContext.getELContext();
			String expression = params.get(DYNAMICIMAGE_PARAM);
			ValueExpression ve = facesContext.getApplication().getExpressionFactory().createValueExpression(elContext, "#{" + expression + "}", StreamedContent.class);
			StreamedContent content = (StreamedContent) ve.getValue(elContext);
			
			if(content != null) {
				if(logger.isLoggable(Level.FINE))
					logger.log(Level.FINE, "Streaming image: {0}", ve.getExpressionString());
			
				HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
			
				try {
					response.setContentType(content.getContentType());
					
					byte[] buffer = new byte[2048];
			
					int length;
					while ((length = (content.getStream().read(buffer))) >= 0) {
						response.getOutputStream().write(buffer, 0, length);
					}
					
					response.setStatus(200);
					content.getStream().close();
					response.getOutputStream().flush();
					facesContext.responseComplete();
				}catch (IOException e) {
					logger.log(Level.WARNING, "Exception in streaming image {0}", ve.getExpressionString());
				}
			}
		}
	}
	
	public void beforePhase(PhaseEvent phaseEvent) {
		//Nothing to do here
	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}
}
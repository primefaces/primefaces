import java.util.Map;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.FlowEvent;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;

    public final static String STEP_STATUS_CLASS = "ui-wizard-step-titles ui-helper-reset ui-helper-clearfix";
	public final static String STEP_CLASS = "ui-wizard-step-title ui-state-default ui-corner-all";
    public final static String ACTIVE_STEP_CLASS = "ui-wizard-step-title ui-state-default ui-state-highlight ui-corner-all";
	public final static String BACK_BUTTON_CLASS = "ui-wizard-nav-back";
	public final static String NEXT_BUTTON_CLASS = "ui-wizard-nav-next";
	
	private Tab current;

	public void processDecodes(FacesContext context) {
        this.decode(context);

		if(!isBackRequest(context)) {
			getStepToProcess().processDecodes(context);
		}
    }
	
	public void processValidators(FacesContext context) {
        if(!isBackRequest(context)) {
			current.processValidators(context);
		}
    }
	
	public void processUpdates(FacesContext context) {
		if(!isBackRequest(context)) {
			current.processUpdates(context);
		}
	}
	
	public Tab getStepToProcess() {
		if(current == null) {
			String currentStepId = getStep();
			
			for(UIComponent child : getChildren()) {
				if(child.getId().equals(currentStepId)) {
					current = (Tab) child;
					
					break;
				}
			}
		}
		
		return current;
	}
	
	public boolean isWizardRequest(FacesContext context) {
		return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_wizardRequest");
	}
	
	public boolean isBackRequest(FacesContext context) {
		return isWizardRequest(context) && context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_backRequest");
	}

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);

        if(event instanceof FlowEvent) {
            FlowEvent flowEvent = (FlowEvent) event;
            FacesContext context = getFacesContext();
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = this.getClientId(context);
            MethodExpression me = this.getFlowListener();

            if(me != null) {
                String step = (String) me.invoke(context.getELContext(), new Object[]{event});

                this.setStep(step);
            }
            else {
                this.setStep(flowEvent.getNewStep());
            }
        }
    }
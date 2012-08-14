import javax.faces.component.UIComponent;
import org.primefaces.component.layout.LayoutUnit;
import java.util.Map;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.util.Constants;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.ResizeEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;

    public final static String UNIT_CLASS = "ui-layout-unit ui-widget ui-widget-content ui-corner-all";
    public final static String UNIT_HEADER_CLASS = "ui-layout-unit-header ui-widget-header ui-corner-all";
    public final static String UNIT_CONTENT_CLASS = "ui-layout-unit-content ui-widget-content";
    public final static String UNIT_FOOTER_CLASS = "ui-layout-unit-footer ui-widget-header ui-corner-all";
    public final static String UNIT_HEADER_TITLE_CLASS = "ui-layout-unit-header-title";
    public final static String UNIT_FOOTER_TITLE_CLASS = "ui-layout-unit-footer-title";
    public final static String UNIT_HEADER_ICON_CLASS = "ui-layout-unit-header-icon ui-state-default ui-corner-all";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("toggle","close", "resize"));

	protected LayoutUnit getLayoutUnitByPosition(String name) {
		for(UIComponent child : getChildren()) {
			if(child instanceof LayoutUnit) {
				LayoutUnit layoutUnit = (LayoutUnit) child;
				
				if(layoutUnit.getPosition().equalsIgnoreCase(name))
					return layoutUnit;
			}
		}
		
		return null;
	}
	
    public boolean isNested() {
        return this.getParent() instanceof LayoutUnit;
    }

    public boolean isElementLayout() {
        return !isNested() && !isFullPage();
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public void processDecodes(FacesContext context) {
        if(isSelfRequest(context)) {
            this.decode(context);
        }
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if(!isSelfRequest(context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(!isSelfRequest(context)) {
            super.processUpdates(context);
        }
    }

    private boolean isSelfRequest(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
        String clientId = this.getClientId(context);

        if(isSelfRequest(context)) {

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            FacesEvent wrapperEvent = null;

            if(eventName.equals("toggle")) {
                boolean collapsed = Boolean.valueOf(params.get(clientId + "_collapsed"));
                LayoutUnit unit = getLayoutUnitByPosition(params.get(clientId + "_unit"));
                Visibility visibility = collapsed ? Visibility.HIDDEN : Visibility.VISIBLE;
                unit.setCollapsed(collapsed);
                
                wrapperEvent = new ToggleEvent(unit, behaviorEvent.getBehavior(), visibility);
            }
            else if(eventName.equals("close")) {
                LayoutUnit unit = getLayoutUnitByPosition(params.get(clientId + "_unit"));
                unit.setVisible(false);

                wrapperEvent = new CloseEvent(unit, behaviorEvent.getBehavior());
            }
            else if(eventName.equals("resize")) {
                LayoutUnit unit = getLayoutUnitByPosition(params.get(clientId + "_unit"));
                String position = unit.getPosition();
                int width = Integer.valueOf(params.get(clientId + "_width"));
                int height = Integer.valueOf(params.get(clientId + "_height"));

                if(position.equals("west") || position.equals("east")) {
                    unit.setSize(String.valueOf(width));
                } else if(position.equals("north") || position.equals("south")) {
                    unit.setSize(String.valueOf(height));
                }

                wrapperEvent = new ResizeEvent(unit, behaviorEvent.getBehavior(), width, height);
            }

            wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }
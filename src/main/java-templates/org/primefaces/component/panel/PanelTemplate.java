import org.primefaces.component.menu.Menu;
import javax.faces.component.UIComponent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.CloseEvent;
import org.primefaces.model.Visibility;
import org.primefaces.util.Constants;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import org.primefaces.component.panel.Panel;
import javax.faces.event.BehaviorEvent;

	public static final String PANEL_CLASS = "ui-panel ui-widget ui-widget-content ui-corner-all";
	public static final String PANEL_TITLEBAR_CLASS = "ui-panel-titlebar ui-widget-header ui-helper-clearfix ui-corner-all";
	public static final String PANEL_TITLE_CLASS = "ui-panel-title";
	public static final String PANEL_TITLE_ICON_CLASS = "ui-panel-titlebar-icon ui-corner-all ui-state-default";
	public static final String PANEL_CONTENT_CLASS = "ui-panel-content ui-widget-content";
	public static final String PANEL_FOOTER_CLASS = "ui-panel-footer ui-widget-content";

    public static final String MOBILE_CLASS = "ui-panel-m ui-corner-all";
    public static final String MOBILE_TITLE_CLASS = "ui-panel-m-titlebar ui-bar ui-bar-inherit";
    public static final String MOBILE_CONTENT_CLASS = "ui-panel-m-content ui-body ui-body-inherit";
    public static final String MOBILE_TOGGLEICON_EXPANDED_CLASS = "ui-panel-m-titlebar-icon ui-btn ui-shadow ui-corner-all ui-icon-minus ui-btn-icon-notext ui-btn-right";
    public static final String MOBILE_TOGGLEICON_COLLAPSED_CLASS = "ui-panel-m-titlebar-icon ui-btn ui-shadow ui-corner-all ui-icon-plus ui-btn-icon-notext ui-btn-right";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = Collections.unmodifiableMap(new HashMap<String, Class<? extends BehaviorEvent>>() {{
        put("toggle", ToggleEvent.class);
        put("close", CloseEvent.class);
    }});

    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
         return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }
	
	private Menu optionsMenu;
	
	public Menu  getOptionsMenu() {
		if(optionsMenu == null) {
			UIComponent optionsFacet = getFacet("options");
			if(optionsFacet != null) {
                if(optionsFacet instanceof Menu)
                    optionsMenu = (Menu) optionsFacet;
                else
                    optionsMenu = (Menu) optionsFacet.getChildren().get(0);
            }

		}

		return optionsMenu;
	}

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
        String clientId = this.getClientId(context);
        
        if(isSelfRequest(context)) {
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            
            if(eventName.equals("toggle")) {
                boolean collapsed = Boolean.valueOf(params.get(clientId + "_collapsed"));
                Visibility visibility = collapsed ? Visibility.HIDDEN : Visibility.VISIBLE;

                super.queueEvent(new ToggleEvent(this, behaviorEvent.getBehavior(), visibility));

            } else if(eventName.equals("close")) {
                super.queueEvent(new CloseEvent(this, behaviorEvent.getBehavior()));
            }
        }
        else {
            super.queueEvent(event);
        }
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
        
        FacesContext facesContext = getFacesContext();
        ELContext eLContext = facesContext.getELContext();
        
        ValueExpression collapsedVE = this.getValueExpression(PropertyKeys.collapsed.toString());
        if(collapsedVE != null && !collapsedVE.isReadOnly(eLContext)) {
            collapsedVE.setValue(eLContext, this.isCollapsed());
            getStateHelper().put(Panel.PropertyKeys.collapsed, null);
        }
    }

    private boolean isSelfRequest(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }
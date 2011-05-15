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
import javax.faces.event.PhaseId;

	public static final String PANEL_CLASS = "ui-panel ui-widget ui-widget-content ui-corner-all";
	public static final String PANEL_TITLEBAR_CLASS = "ui-panel-titlebar ui-widget-header ui-corner-all";
	public static final String PANEL_TITLE_CLASS = "ui-panel-title";
	public static final String PANEL_TITLE_ICON_CLASS = "ui-panel-titlebar-icon ui-corner-all ui-state-default";
	public static final String PANEL_CONTENT_CLASS = "ui-panel-content ui-widget-content";
	public static final String PANEL_FOOTER_CLASS = "ui-panel-footer ui-widget-content";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("toggle","close"));
	
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
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
        String source = context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM);
        String clientId = this.getClientId(context);
        
        if(clientId.equals(source)) {
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            
            if(eventName.equals("toggle")) {
                boolean collapsed = Boolean.valueOf(params.get(clientId + "_collapsed"));
                Visibility visibility = collapsed ? Visibility.HIDDEN : Visibility.VISIBLE;

                ToggleEvent toggleEvent = new ToggleEvent(this, behaviorEvent.getBehavior(), visibility);
                super.queueEvent(toggleEvent);

            } else if(eventName.equals("close")) {
                CloseEvent closeEvent = new CloseEvent(this, behaviorEvent.getBehavior());
                super.queueEvent(closeEvent);
                
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import javax.el.ValueExpression;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;

    public final static String CONTAINER_CLASS = "ui-accordion ui-widget ui-helper-reset ui-hidden-container";
    public final static String ACTIVE_TAB_HEADER_CLASS = "ui-accordion-header ui-helper-reset ui-state-default ui-state-active ui-corner-top";
    public final static String TAB_HEADER_CLASS = "ui-accordion-header ui-helper-reset ui-state-default ui-corner-all";
    public final static String TAB_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-e";
    public final static String TAB_HEADER_ICON_RTL_CLASS = "ui-icon ui-icon-triangle-1-w";
    public final static String ACTIVE_TAB_HEADER_ICON_CLASS = "ui-icon ui-icon-triangle-1-s";
    public final static String ACTIVE_TAB_CONTENT_CLASS = "ui-accordion-content ui-helper-reset ui-widget-content";
    public final static String INACTIVE_TAB_CONTENT_CLASS = "ui-accordion-content ui-helper-reset ui-widget-content ui-helper-hidden";

    public final static String MOBILE_CONTAINER_CLASS = "ui-accordion ui-collapsible-set ui-corner-all";
    public final static String MOBILE_INACTIVE_TAB_CONTAINER_CLASS = "ui-collapsible ui-collapsible-inset ui-corner-all ui-collapsible-themed-content ui-collapsible-collapsed";
    public final static String MOBILE_ACTIVE_TAB_CONTAINER_CLASS = "ui-collapsible ui-collapsible-inset ui-corner-all ui-collapsible-themed-content";
    public final static String MOBILE_ACTIVE_TAB_HEADER_CLASS = "ui-collapsible-heading";
    public final static String MOBILE_INACTIVE_TAB_HEADER_CLASS = "ui-collapsible-heading ui-collapsible-heading-collapsed";
    public final static String MOBILE_ACTIVE_TAB_CONTENT_CLASS = "ui-collapsible-content ui-body-inherit";
    public final static String MOBILE_INACTIVE_TAB_CONTENT_CLASS = "ui-collapsible-content ui-body-inherit ui-collapsible-content-collapsed";
    public final static String MOBILE_ACTIVE_ICON_CLASS = "ui-collapsible-heading-toggle ui-btn ui-btn-icon-left ui-icon-minus";
    public final static String MOBILE_INACTIVE_ICON_CLASS = "ui-collapsible-heading-toggle ui-btn ui-btn-icon-left ui-icon-plus";   

    private final static String DEFAULT_EVENT = "tabChange";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("tabChange","tabClose"));

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }

    public boolean isContentLoadRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_contentLoad");
    }

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }

	public Tab findTab(String tabClientId) {
        for(UIComponent component : getChildren()) {
            if(component.getClientId().equals(tabClientId))
                return (Tab) component;
        }

        return null;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);
            boolean repeating = this.isRepeating();
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("tabChange")) {
                String tabClientId = params.get(clientId + "_newTab");
                TabChangeEvent changeEvent = new TabChangeEvent(this, behaviorEvent.getBehavior(), findTab(tabClientId));

                if(repeating) {
                    int index = Integer.parseInt(params.get(clientId + "_tabindex"));
                    setIndex(index);
                    changeEvent.setData(this.getIndexData());
                    changeEvent.setTab((Tab) getChildren().get(0));
                }

                changeEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(changeEvent);

                if(repeating) {
                    setIndex(-1);
                }
            }
            else if(eventName.equals("tabClose")) {
                String tabClientId = params.get(clientId + "_tabId");
                TabCloseEvent closeEvent = new TabCloseEvent(this, behaviorEvent.getBehavior(), findTab(tabClientId));

                if(repeating) {
                    int index = Integer.parseInt(params.get(clientId + "_tabindex"));
                    setIndex(index);
                    closeEvent.setData(this.getIndexData());
                    closeEvent.setTab((Tab) getChildren().get(0));
                }

                closeEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(closeEvent);

                if(repeating) {
                    setIndex(-1);
                }
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(!isRendered()) {
            return;
        }

        super.processUpdates(context);

        ValueExpression expr = this.getValueExpression("activeIndex");
        if(expr != null) {
            expr.setValue(getFacesContext().getELContext(), getActiveIndex());
            resetActiveIndex();
        }
    }

    protected void resetActiveIndex() {
		getStateHelper().remove(PropertyKeys.activeIndex);
    }

    public boolean isRTL() {
        return this.getDir().equalsIgnoreCase("rtl");
    }
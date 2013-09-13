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
import javax.faces.context.FacesContext;

    public static final String CONTAINER_CLASS = "ui-tabs ui-widget ui-widget-content ui-corner-all ui-hidden-container";
    public static final String NAVIGATOR_CLASS = "ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all";
    public static final String INACTIVE_TAB_HEADER_CLASS = "ui-state-default";
    public static final String ACTIVE_TAB_HEADER_CLASS = "ui-state-default ui-tabs-selected ui-state-active";
    public static final String PANELS_CLASS = "ui-tabs-panels";
    public static final String ACTIVE_TAB_CONTENT_CLASS = "ui-tabs-panel ui-widget-content ui-corner-bottom";
    public static final String INACTIVE_TAB_CONTENT_CLASS = "ui-tabs-panel ui-widget-content ui-corner-bottom ui-helper-hidden";
    public static final String NAVIGATOR_SCROLLER_CLASS = "ui-tabs-navscroller";
    public static final String NAVIGATOR_LEFT_CLASS = "ui-tabs-navscroller-btn ui-tabs-navscroller-btn-left ui-state-default ui-corner-right";
    public static final String NAVIGATOR_RIGHT_CLASS = "ui-tabs-navscroller-btn ui-tabs-navscroller-btn-right ui-state-default ui-corner-left";
    public static final String NAVIGATOR_LEFT_ICON_CLASS = "ui-icon ui-icon-carat-1-w";
    public static final String NAVIGATOR_RIGHT_ICON_CLASS = "ui-icon ui-icon-carat-1-e";
    public static final String SCROLLABLE_TABS_CLASS = "ui-tabs-scrollable";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("tabChange","tabClose"));

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

    List<Tab> loadedTabs;
    public List<Tab> getLoadedTabs() {
        if(loadedTabs == null) {
            loadedTabs = new ArrayList<Tab>();

            for(UIComponent component : getChildren()) {
                if(component instanceof Tab) {
                    Tab tab =  (Tab) component;
                    
                    if(tab.isLoaded())
                        loadedTabs.add(tab);
                }
            }
        }

        return loadedTabs;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("tabChange")) {
                String tabClientId = params.get(clientId + "_newTab");
                TabChangeEvent changeEvent = new TabChangeEvent(this, behaviorEvent.getBehavior(), findTab(tabClientId));

                if(this.getVar() != null) {
                    int tabindex = Integer.parseInt(params.get(clientId + "_tabindex"));
                    setRowIndex(tabindex);
                    changeEvent.setData(this.getRowData());
                    changeEvent.setTab((Tab) getChildren().get(0));
                    setRowIndex(-1);
                }

                changeEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(changeEvent);
            }
            else if(eventName.equals("tabClose")) {
                String tabClientId = params.get(clientId + "_closeTab");
                TabCloseEvent closeEvent = new TabCloseEvent(this, behaviorEvent.getBehavior(), findTab(tabClientId));

                if(this.getVar() != null) {
                    int tabindex = Integer.parseInt(params.get(clientId + "_tabindex"));
                    setRowIndex(tabindex);
                    closeEvent.setData(this.getRowData());
                    closeEvent.setTab((Tab) getChildren().get(0));
                    setRowIndex(-1);
                }

                closeEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(closeEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void processDecodes(FacesContext context) {
        if(!isRendered()) {
            return;
        }

        //only process loaded tabs on dynamic case without tab model
        if(isDynamic() && getVar() == null) {
        	pushComponentToEL(context, null);
            for(Tab tab : getLoadedTabs()) {
                tab.processDecodes(context);
            }
            this.decode(context);
            popComponentFromEL(context);
        }
        else {
            if(this.getVar() == null) {
            	pushComponentToEL(context, null);
            	ComponentUtils.processDecodesOfFacetsAndChilds(this, context);
                this.decode(context);
                popComponentFromEL(context);
            }
            else {
                super.processDecodes(context);
            }
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if(!isRendered()) {
            return;
        }

        //only process loaded tabs on dynamic case without tab model
        if(isDynamic() && getVar() == null) {
            for(Tab tab : getLoadedTabs()) {
                tab.processValidators(context);
            }
        }
        else {
            if(this.getVar() == null) {
            	ComponentUtils.processValidatorsOfFacetsAndChilds(this, context);
            }
            else {
                super.processValidators(context);
            }
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(!isRendered()) {
            return;
        }

        ValueExpression expr = this.getValueExpression("activeIndex");
        if(expr != null) {
            expr.setValue(getFacesContext().getELContext(), getActiveIndex());
            resetActiveIndex();
        }

        //only process loaded tabs on dynamic case without tab model
        if(isDynamic() && getVar() == null) {
            for(Tab tab : getLoadedTabs()) {
                tab.processUpdates(context);
            }
        }
        else {
            if(this.getVar() == null) {
            	ComponentUtils.processUpdatesOfFacetsAndChilds(this, context);
            }
            else {
                super.processUpdates(context);
            }  
        }
    }

    protected void resetActiveIndex() {
		getStateHelper().remove(PropertyKeys.activeIndex);
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public boolean visitTree(VisitContext context,  VisitCallback callback) {
    
        if(this.getVar() == null) {
            if (!isVisitable(context))
                return false;

            FacesContext facesContext = context.getFacesContext();
            pushComponentToEL(facesContext, null);

            try {
                VisitResult result = context.invokeVisitCallback(this, callback);

                if (result == VisitResult.COMPLETE)
                  return true;

                if (result == VisitResult.ACCEPT) {
                    Iterator<UIComponent> kids = this.getFacetsAndChildren();

                    while(kids.hasNext()) {
                        boolean done = kids.next().visitTree(context, callback);

                        if (done)
                            return true;
                    }
                }
            }
            finally {
                popComponentFromEL(facesContext);
            }

            return false;
        }
        else {
            return super.visitTree(context, callback);
        }
    }

    public boolean isRTL() {
        return this.getDir().equalsIgnoreCase("rtl");
    }

    @Override
    public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback) throws FacesException {
        if(this.getVar() == null) {
            if (null == context || null == clientId || null == callback) {
                throw new NullPointerException();
            }

            boolean found = false;
            if (clientId.equals(this.getClientId(context))) {
                try {
                    this.pushComponentToEL(context, this);
                    callback.invokeContextCallback(context, this);
                    return true;
                } catch (Exception e) {
                    throw new FacesException(e);
                } finally {
                    this.popComponentFromEL(context);
                }
            } else {
                Iterator<UIComponent> itr = this.getFacetsAndChildren();

                while (itr.hasNext() && !found) {
                    found = itr.next().invokeOnComponent(context, clientId,
                            callback);
                }
            }
            return found;
        }
        else {
            return super.invokeOnComponent(context, clientId, callback);
        }
    }
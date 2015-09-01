import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.MoveEvent;
import org.primefaces.util.Constants;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import javax.el.ELContext;

    public static final String CONTAINER_CLASS = "ui-dialog ui-widget ui-widget-content ui-corner-all ui-shadow ui-hidden-container";
    public static final String TITLE_BAR_CLASS = "ui-dialog-titlebar ui-widget-header ui-helper-clearfix ui-corner-top";
    public static final String TITLE_CLASS = "ui-dialog-title";
    public static final String TITLE_BAR_CLOSE_CLASS = "ui-dialog-titlebar-icon ui-dialog-titlebar-close ui-corner-all";
    public static final String CLOSE_ICON_CLASS = "ui-icon ui-icon-closethick";
    public static final String TITLE_BAR_MINIMIZE_CLASS = "ui-dialog-titlebar-icon ui-dialog-titlebar-minimize ui-corner-all";
    public static final String MINIMIZE_ICON_CLASS = "ui-icon ui-icon-minus";
    public static final String TITLE_BAR_MAXIMIZE_CLASS = "ui-dialog-titlebar-icon ui-dialog-titlebar-maximize ui-corner-all";
    public static final String MAXIMIZE_ICON_CLASS = "ui-icon ui-icon-extlink";
    public static final String CONTENT_CLASS = "ui-dialog-content ui-widget-content";
    public static final String FOOTER_CLASS = "ui-dialog-footer ui-widget-content";

    public static final String MOBILE_CONTAINER_CLASS = "ui-popup-container ui-popup-hidden ui-popup-truncate";
    public static final String MOBILE_POPUP_CLASS = "ui-popup ui-body-inherit ui-overlay-shadow ui-corner-all";
    public static final String MOBILE_MASK_CLASS = "ui-popup-screen ui-overlay-b ui-screen-hidden";
    public static final String MOBILE_TITLE_BAR_CLASS = "ui-header ui-bar-inherit";
    public static final String MOBILE_TITLE_CLASS = "ui-title";
    public static final String MOBILE_CONTENT_CLASS = "ui-content";
    public static final String MOBILE_CLOSE_ICON_CLASS = "ui-btn ui-corner-all ui-icon-delete ui-btn-icon-notext ui-btn-left";

    private final static String DEFAULT_EVENT = "close";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("close","minimize","maximize","move","restoreMinimize","restoreMaximize"));

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            AjaxBehaviorEvent ajaxBehaviorEvent = (AjaxBehaviorEvent) event;
            String clientId = getClientId(context);

            if(eventName.equals("close")) {
                setVisible(false);
                CloseEvent closeEvent = new CloseEvent(this, ((AjaxBehaviorEvent) event).getBehavior());
                closeEvent.setPhaseId(ajaxBehaviorEvent.getPhaseId());
                super.queueEvent(closeEvent);
            }
            else if(eventName.equals("move")) {
                int top = Double.valueOf(params.get(clientId + "_top")).intValue();
                int left = Double.valueOf(params.get(clientId + "_left")).intValue();
                MoveEvent moveEvent = new MoveEvent(this, ((AjaxBehaviorEvent) event).getBehavior(), top, left);
                moveEvent.setPhaseId(ajaxBehaviorEvent.getPhaseId());
                super.queueEvent(moveEvent);
            }
            else {
                //minimize and maximize
                super.queueEvent(event);
            }
        } else {
            super.queueEvent(event);
        }
    }

    @Override
    public void processDecodes(FacesContext context) {
        if(isRequestSource(context)) {
            this.decode(context);
        }
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if(!isRequestSource(context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(!isRequestSource(context)) {
            super.processUpdates(context);
        }
        else {
            ValueExpression visibleVE = this.getValueExpression("visible");
            if(visibleVE != null) {
                FacesContext facesContext = getFacesContext();
                ELContext eLContext = facesContext.getELContext();

                if(!visibleVE.isReadOnly(eLContext)) {
                    visibleVE.setValue(eLContext, this.isVisible());
                    this.getStateHelper().put(PropertyKeys.visible, null);
                }
            }
        }
    }

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }

    public boolean isContentLoadRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_contentLoad");
    }

    public boolean isRTL() {
        return this.getDir().equalsIgnoreCase("rtl");
    }

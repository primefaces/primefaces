import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.util.Constants;
import org.primefaces.event.diagram.ConnectEvent;
import org.primefaces.event.diagram.DisconnectEvent;
import org.primefaces.event.diagram.ConnectionChangeEvent;
import java.util.Map;

    public static final String CONTAINER_CLASS = "ui-diagram ui-widget";
    public static final String ELEMENT_CLASS = "ui-diagram-element";
    public static final String DRAGGABLE_ELEMENT_CLASS = "ui-diagram-draggable";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("connect","disconnect", "connectionChange"));

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    public boolean isConnectRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_connect");
    }

    public boolean isDisconnectRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_disconnect");
    }

    public boolean isConnectionChangeRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_connectionChange");
    }

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            DiagramModel model = (DiagramModel) this.getValue();

            if(model != null) {
                if(eventName.equals("connect") || eventName.equals("disconnect")) {
                    FacesEvent facesEvent = null;
                    Element sourceElement = model.findElement(params.get(clientId + "_sourceId"));
                    Element targetElement = model.findElement(params.get(clientId + "_targetId"));
                    EndPoint sourceEndPoint = model.findEndPoint(sourceElement, params.get(clientId + "_sourceEndPointId"));
                    EndPoint targetEndPoint = model.findEndPoint(targetElement, params.get(clientId + "_targetEndPointId"));

                    if(eventName.equals("connect"))
                        facesEvent = new ConnectEvent(this, behaviorEvent.getBehavior(), sourceElement, targetElement, sourceEndPoint, targetEndPoint);
                    else if(eventName.equals("disconnect"))
                        facesEvent = new DisconnectEvent(this, behaviorEvent.getBehavior(), sourceElement, targetElement, sourceEndPoint, targetEndPoint);

                    facesEvent.setPhaseId(behaviorEvent.getPhaseId());
                    super.queueEvent(facesEvent);
                }
                else if(eventName.equals("connectionChange")) {
                    Element originalSourceElement = model.findElement(params.get(clientId + "_originalSourceId"));
                    Element newSourceElement = model.findElement(params.get(clientId + "_newSourceId"));
                    Element originalTargetElement = model.findElement(params.get(clientId + "_originalTargetId"));
                    Element newTargetElement = model.findElement(params.get(clientId + "_newTargetId"));
                    EndPoint originalSourceEndPoint = model.findEndPoint(originalSourceElement, params.get(clientId + "_originalSourceEndPointId"));
                    EndPoint newSourceEndPoint = model.findEndPoint(newSourceElement, params.get(clientId + "_newSourceEndPointId"));
                    EndPoint originalTargetEndPoint = model.findEndPoint(originalTargetElement, params.get(clientId + "_originalTargetEndPointId"));
                    EndPoint newTargetEndPoint = model.findEndPoint(newTargetElement, params.get(clientId + "_targetEndPointId"));

                    ConnectionChangeEvent connectionChangeEvent = new ConnectionChangeEvent(this, behaviorEvent.getBehavior(), 
                            originalSourceElement, newSourceElement, originalTargetElement, newTargetElement,
                            originalSourceEndPoint, newSourceEndPoint, originalTargetEndPoint, newTargetEndPoint);

                    connectionChangeEvent.setPhaseId(behaviorEvent.getPhaseId());
                    super.queueEvent(connectionChangeEvent);
                }
            }
        }
        else {
            super.queueEvent(event);
        }
    }
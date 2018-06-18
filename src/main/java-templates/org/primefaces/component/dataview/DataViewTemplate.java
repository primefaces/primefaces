import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.util.Constants;
import java.util.HashMap;
import javax.faces.component.UIComponent;
import javax.faces.event.BehaviorEvent;

        public static final String DATAVIEW_CLASS = "ui-dataview ui-widget";
        public static final String LIST_LAYOUT_CLASS = "ui-dataview-list";
        public static final String GRID_LAYOUT_CLASS = "ui-dataview-grid";
        public static final String HEADER_CLASS = "ui-dataview-header ui-widget-header ui-helper-clearfix ui-corner-top";
        public static final String FOOTER_CLASS = "ui-dataview-footer ui-widget-header ui-corner-bottom";
        public static final String CONTENT_CLASS = "ui-dataview-content ui-widget-content";
        public static final String BUTTON_CONTAINER_CLASS = "ui-dataview-layout-options ui-selectonebutton ui-buttonset";
        public static final String BUTTON_CLASS = "ui-button ui-button-icon-only ui-state-default";
        public static final String LIST_LAYOUT_CONTAINER_CLASS = "ui-dataview-list-container";
        public static final String ROW_CLASS = "ui-dataview-row";
        public static final String GRID_LAYOUT_ROW_CLASS = "ui-dataview-row ui-g";
        public static final String GRID_LAYOUT_COLUMN_CLASS = "ui-dataview-column";

        private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = Collections.unmodifiableMap(new HashMap<String, Class<? extends BehaviorEvent>>() {{
            put("page", PageEvent.class);
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

        public boolean isLayoutRequest(FacesContext context) {
            return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_layout");
        }

        public boolean isPaginationRequest(FacesContext context) {
            return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_pagination");
        }

        @Override
        public void queueEvent(FacesEvent event) {
            FacesContext context = getFacesContext();

            if(ComponentUtils.isRequestSource(this, context) && event instanceof AjaxBehaviorEvent) {
                setRowIndex(-1);
                Map<String,String> params = context.getExternalContext().getRequestParameterMap();
                String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

                if(eventName.equals("page")) {
                    AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
                    String clientId = this.getClientId(context);
                    int rows = this.getRowsToRender();
                    int first = Integer.parseInt(params.get(clientId + "_first"));
                    int page = rows > 0 ? (int) (first / rows) : 0;

                    PageEvent pageEvent = new PageEvent(this, behaviorEvent.getBehavior(), page);
                    pageEvent.setPhaseId(behaviorEvent.getPhaseId());

                    super.queueEvent(pageEvent);
                }
            }
            else {
                super.queueEvent(event);
            }
        }

        private DataViewGridItem gridItem = null;
        private DataViewListItem listItem = null;

        public DataViewGridItem getGridItem() {
            return gridItem;
        }

        public DataViewListItem getListItem() {
            return listItem;
        }

        public void findViewItems() {
            for (UIComponent kid : this.getChildren()) {
                if (kid.isRendered()) {
                    if(kid instanceof DataViewListItem) {
                        listItem = (DataViewListItem) kid;
                    }
                    else if (kid instanceof DataViewGridItem) {
                        gridItem = (DataViewGridItem) kid;
                    }
                }
            }
        }




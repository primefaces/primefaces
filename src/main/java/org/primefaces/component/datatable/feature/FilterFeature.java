/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.datatable.feature;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import org.primefaces.component.api.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.row.Row;
import org.primefaces.context.RequestContext;
import org.primefaces.event.data.PostFilterEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.filter.*;
import org.primefaces.util.Constants;

public class FilterFeature implements DataTableFeature {
    
    private final static Logger logger = Logger.getLogger(DataTable.class.getName());
    
    private final static String STARTS_WITH_MATCH_MODE = "startsWith";
    private final static String ENDS_WITH_MATCH_MODE = "endsWith";
    private final static String CONTAINS_MATCH_MODE = "contains";
    private final static String EXACT_MATCH_MODE = "exact";
    private final static String LESS_THAN_MODE = "lt";
    private final static String LESS_THAN_EQUALS_MODE = "lte";
    private final static String GREATER_THAN_MODE = "gt";
    private final static String GREATER_THAN_EQUALS_MODE = "gte";
    private final static String EQUALS_MODE = "equals";
    private final static String IN_MODE = "in";
    private final static String GLOBAL_MODE = "global";
  
    final static Map<String,FilterConstraint> FILTER_CONSTRAINTS;
    
    static {
        FILTER_CONSTRAINTS = new HashMap<String,FilterConstraint>();
        FILTER_CONSTRAINTS.put(STARTS_WITH_MATCH_MODE, new StartsWithFilterConstraint());
        FILTER_CONSTRAINTS.put(ENDS_WITH_MATCH_MODE, new EndsWithFilterConstraint());
        FILTER_CONSTRAINTS.put(CONTAINS_MATCH_MODE, new ContainsFilterConstraint());
        FILTER_CONSTRAINTS.put(EXACT_MATCH_MODE, new ExactFilterConstraint());
        FILTER_CONSTRAINTS.put(LESS_THAN_MODE, new LessThanFilterConstraint());
        FILTER_CONSTRAINTS.put(LESS_THAN_EQUALS_MODE, new LessThanEqualsFilterConstraint());
        FILTER_CONSTRAINTS.put(GREATER_THAN_MODE, new GreaterThanFilterConstraint());
        FILTER_CONSTRAINTS.put(GREATER_THAN_EQUALS_MODE, new GreaterThanEqualsFilterConstraint());
        FILTER_CONSTRAINTS.put(EQUALS_MODE, new EqualsFilterConstraint());
        FILTER_CONSTRAINTS.put(IN_MODE, new InFilterConstraint());
        FILTER_CONSTRAINTS.put(GLOBAL_MODE, new GlobalFilterConstraint());
    }
    
    private boolean isFilterRequest(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_filtering");
    }
    
    public boolean shouldDecode(FacesContext context, DataTable table) {
        return false;
    }
    
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return isFilterRequest(context, table);
    }

    public void decode(FacesContext context, DataTable table) {
        String globalFilterParam = table.getClientId(context) + UINamingContainer.getSeparatorChar(context) + "globalFilter";
        List<FilterMeta> filterMetadata = this.populateFilterMetaData(context, table);
        Map<String,Object> filterParameterMap = this.populateFilterParameterMap(context, table, filterMetadata, globalFilterParam);
        table.setFilters(filterParameterMap);
        table.setFilterMetadata(filterMetadata);
    }
            
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        
        //reset state
        String clientId = table.getClientId(context);
        table.updateFilteredValue(context, null);
        table.setValue(null);
        table.setFirst(0);
        table.setRowIndex(-1);
        
        //update rows with rpp value
        String rppValue = params.get(clientId + "_rppDD");
        if(rppValue != null) {
            table.setRows(Integer.parseInt(rppValue));
        }
        
        if(table.isLazy()) {
            table.loadLazyData();
        }
        else {
            String globalFilterParam = clientId + UINamingContainer.getSeparatorChar(context) + "globalFilter";
            filter(context, table, table.getFilterMetadata(), globalFilterParam);
                                  
            //sort new filtered data to restore sort state
            boolean sorted = (table.getValueExpression("sortBy") != null || table.getSortBy() != null);
            if(sorted) {
                SortFeature sortFeature = (SortFeature) table.getFeature(DataTableFeatureKey.SORT);
                
                if(table.isMultiSort())
                    sortFeature.multiSort(context, table);
                else
                    sortFeature.singleSort(context, table);
            }
        }
        
        context.getApplication().publishEvent(context, PostFilterEvent.class, table);
                        
        renderer.encodeTbody(context, table, true);
    }
    
    public void filter(FacesContext context, DataTable table, List<FilterMeta> filterMetadata, String globalFilterParam) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        List filteredData = new ArrayList();
        Locale filterLocale = table.resolveDataLocale();
        boolean hasGlobalFilter = globalFilterParam != null ? params.containsKey(globalFilterParam) : false;
        String globalFilterValue = hasGlobalFilter ? params.get(globalFilterParam): null;
        GlobalFilterConstraint globalFilterConstraint = (GlobalFilterConstraint) FILTER_CONSTRAINTS.get(GLOBAL_MODE);
        ELContext elContext = context.getELContext();
        
        for(int i = 0; i < table.getRowCount(); i++) {
            table.setRowIndex(i);
            boolean localMatch = true;
            boolean globalMatch = false;

            for(FilterMeta filterMeta : filterMetadata) {
                Object filterValue = filterMeta.getFilterValue();
                UIColumn column = filterMeta.getColumn();
                MethodExpression filterFunction = column.getFilterFunction();
                ValueExpression filterByVE = filterMeta.getFilterByVE();
                
                if(column instanceof DynamicColumn) {
                    ((DynamicColumn) column).applyStatelessModel();
                }
                
                Object columnValue = filterByVE.getValue(elContext);
                FilterConstraint filterConstraint = this.getFilterConstraint(column);

                if(hasGlobalFilter && !globalMatch) {
                    globalMatch = globalFilterConstraint.applies(columnValue, globalFilterValue, filterLocale);
                }

                if(filterFunction != null) {
                    localMatch = (Boolean) filterFunction.invoke(elContext, new Object[]{columnValue, filterValue, filterLocale});
                }
                else if(!filterConstraint.applies(columnValue, filterValue, filterLocale)) {
                    localMatch = false;
                }
                
                if(!localMatch) {
                    break;
                }
            }

            boolean matches = localMatch;
            if(hasGlobalFilter) {
                matches = localMatch && globalMatch;
            }

            if(matches) {
                filteredData.add(table.getRowData());
            }
        }

        //Metadata for callback
        if(table.isPaginator()) {
            RequestContext requestContext = RequestContext.getCurrentInstance();

            if(requestContext != null) {
                requestContext.addCallbackParam("totalRecords", filteredData.size());
            }
        }

        //save filtered data
        table.updateFilteredValue(context, filteredData);
        
        //update value
        table.updateValue(table.getFilteredValue());

        table.setRowIndex(-1);  //reset datamodel
    }
        
    private Map<String,Object> populateFilterParameterMap(FacesContext context, DataTable table, List<FilterMeta> filterMetadata, String globalFilterParam) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap(); 
        Map<String,Object> filterParameterMap = new HashMap<String, Object>();

        for(FilterMeta filterMeta : filterMetadata) {
            Object filterValue = filterMeta.getFilterValue();
            UIColumn column = filterMeta.getColumn();
            
            if(filterValue != null && !filterValue.toString().trim().equals(Constants.EMPTY_STRING)) {
                String filterField = null;
                ValueExpression filterByVE = column.getValueExpression("filterBy");
                
                if(column.isDynamic()) {
                    ((DynamicColumn) column).applyStatelessModel();
                    Object filterByProperty = column.getFilterBy();
                    String field = column.getField();
                    if(field == null)
                        filterField = (filterByProperty == null) ? table.resolveDynamicField(filterByVE) : filterByProperty.toString();
                    else
                        filterField = field;
                }
                else {
                    String field = column.getField();
                    if(field == null)
                        filterField = (filterByVE == null) ? (String) column.getFilterBy(): table.resolveStaticField(filterByVE);
                    else
                        filterField = field;
                }

                filterParameterMap.put(filterField, filterValue);
            }
        }

        if(params.containsKey(globalFilterParam)) {
            filterParameterMap.put("globalFilter", params.get(globalFilterParam));
        }
        
        return filterParameterMap;
    }
    
    public List<FilterMeta> populateFilterMetaData(FacesContext context, DataTable table) {
        List<FilterMeta> filterMetadata = new ArrayList<FilterMeta>();
        String separator = String.valueOf(UINamingContainer.getSeparatorChar(context));
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        boolean hasFrozenColumns = table.getFrozenColumns() > 0;
                        
        if(!hasFrozenColumns) {
            ColumnGroup headerGroup = getColumnGroup(table, "header");
            
            if(headerGroup != null)
                populateFilterMetaDataInColumnGroup(context, filterMetadata, headerGroup, params, separator);
            else
                populateFilterMetaDataWithoutColumnGroups(context, table, filterMetadata, params, separator);
        }
        else {
            ColumnGroup frozenHeaderGroup = getColumnGroup(table, "frozenHeader");
            ColumnGroup scrollableHeaderGroup = getColumnGroup(table, "scrollableHeader");
            
            if(frozenHeaderGroup != null) {
                populateFilterMetaDataInColumnGroup(context, filterMetadata, frozenHeaderGroup, params, separator);
                populateFilterMetaDataInColumnGroup(context, filterMetadata, scrollableHeaderGroup, params, separator);
            }
            else {
                populateFilterMetaDataWithoutColumnGroups(context, table, filterMetadata, params, separator);
            }
        }

        return filterMetadata;
    }
    
    private void populateFilterMetaDataInColumnGroup(FacesContext context, List<FilterMeta> filterMetadata, ColumnGroup group, Map<String,String> params, String separator) {
        if(group == null) {
            return;
        }
       
       for(UIComponent child : group.getChildren()) {
            Row headerRow = (Row) child;

            if(headerRow.isRendered()) {
                for(UIComponent headerRowChild : headerRow.getChildren()) {
                    if(headerRowChild instanceof Column) {
                        Column column = (Column) headerRowChild;
                        if(column.isRendered()) {
                            ValueExpression columnFilterByVE = column.getValueExpression("filterBy");
                            if(columnFilterByVE != null) {
                                ValueExpression filterByVE = columnFilterByVE;
                                UIComponent filterFacet = column.getFacet("filter");
                                Object filterValue = (filterFacet == null) ? params.get(column.getClientId(context) + separator + "filter") : ((ValueHolder) filterFacet).getLocalValue(); 

                                filterMetadata.add(new FilterMeta(column, filterByVE, filterValue));
                            }
                        }
                    }
                    else if(headerRowChild instanceof Columns) {
                        Columns uiColumns = (Columns) headerRowChild;
                        List<DynamicColumn> dynamicColumns = uiColumns.getDynamicColumns();

                        for(DynamicColumn dynaColumn : dynamicColumns) {
                            dynaColumn.applyStatelessModel();
                            if(dynaColumn.isRendered()) {
                                ValueExpression columnFilterByVE = dynaColumn.getValueExpression("filterBy");
                                if(columnFilterByVE != null) {
                                    String filterId = dynaColumn.getContainerClientId(context) + separator + "filter";
                                    UIComponent filterFacet = dynaColumn.getFacet("filter"); 
                                    Object filterValue = (filterFacet == null) ? params.get(filterId) : ((ValueHolder) filterFacet).getLocalValue();

                                    filterMetadata.add(new FilterMeta(dynaColumn, columnFilterByVE, filterValue));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
   
   private void populateFilterMetaDataWithoutColumnGroups(FacesContext context, DataTable table, List<FilterMeta> filterMetadata, Map<String,String> params, String separator) {
       for(UIColumn column : table.getColumns()) {
            ValueExpression columnFilterByVE = column.getValueExpression("filterBy");

            if (columnFilterByVE != null) {
                UIComponent filterFacet = column.getFacet("filter");                    
                ValueExpression filterByVE = columnFilterByVE;
                Object filterValue = null;
                String filterId = null;

                if(column instanceof Column) {
                    filterId = column.getClientId(context) + separator + "filter";
                }
                else if(column instanceof DynamicColumn) {
                    DynamicColumn dynamicColumn = (DynamicColumn) column;
                    dynamicColumn.applyStatelessModel();
                    filterId = dynamicColumn.getContainerClientId(context) + separator + "filter";
                    dynamicColumn.cleanStatelessModel();
                }

                if(filterFacet == null)
                    filterValue = params.get(filterId);
                else
                    filterValue = ((ValueHolder) filterFacet).getLocalValue();

                filterMetadata.add(new FilterMeta(column, filterByVE, filterValue));
            }
        }
   }
    
   private ColumnGroup getColumnGroup(DataTable table, String target) {
        for(UIComponent child : table.getChildren()) {
            if(child instanceof ColumnGroup) {
                ColumnGroup colGroup = (ColumnGroup) child;
                String type = colGroup.getType();

                if(type != null && type.equals(target)) {
                    return colGroup;
                }

            }
        }

        return null;
    }
   
    public FilterConstraint getFilterConstraint(UIColumn column) {
        String filterMatchMode = column.getFilterMatchMode();
        FilterConstraint filterConstraint  = FILTER_CONSTRAINTS.get(filterMatchMode);
        
        if(filterConstraint == null) { 
            throw new FacesException("Illegal filter match mode:" + filterMatchMode);
        }

        return filterConstraint;
    }
}
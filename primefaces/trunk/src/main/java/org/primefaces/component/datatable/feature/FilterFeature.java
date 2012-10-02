/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import org.primefaces.component.api.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datatable.DataTableRenderer;
import org.primefaces.component.row.Row;
import org.primefaces.context.RequestContext;
import org.primefaces.model.filter.*;
import org.primefaces.util.ComponentUtils;

public class FilterFeature implements DataTableFeature {
    
    private final static Logger logger = Logger.getLogger(DataTable.class.getName());
    
    private final static String STARTS_WITH_MATCH_MODE = "startsWith";
    private final static String ENDS_WITH_MATCH_MODE = "endsWith";
    private final static String CONTAINS_MATCH_MODE = "contains";
    private final static String EXACT_MATCH_MODE = "exact";
    
    private boolean isFilterRequest(FacesContext context, DataTable table) {
        return context.getExternalContext().getRequestParameterMap().containsKey(table.getClientId(context) + "_filtering");
    }

    public boolean shouldDecode(FacesContext context, DataTable table) {
        return isFilterRequest(context, table);
    }
    
    public boolean shouldEncode(FacesContext context, DataTable table) {
        return isFilterRequest(context, table);
    }

    public void decode(FacesContext context, DataTable table) {
        //reset state
        updateFilteredValue(context, table, null);
        table.setFirst(0);
        table.setRowIndex(-1);
        
        String globalFilterParam = table.getClientId(context) + UINamingContainer.getSeparatorChar(context) + "globalFilter";
        Map<String,UIColumn> columnFilterMap = this.populateColumnFilterMap(context, table);
        Map<String,String> filterParameterMap = this.populateFilterParameterMap(context, table, columnFilterMap, globalFilterParam);

        table.setFilters(filterParameterMap);
        
        if(!table.isLazy()) {
            filter(context, table, columnFilterMap, globalFilterParam);
        }
    }
    
    protected void filter(FacesContext context, DataTable table, Map<String,UIColumn> filterMap, String globalFilterParam) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        List filteredData = new ArrayList();
        boolean hasGlobalFilter = params.containsKey(globalFilterParam);
        String globalFilter = hasGlobalFilter ? params.get(globalFilterParam).toLowerCase() : null;

        for(int i = 0; i < table.getRowCount(); i++) {
            table.setRowIndex(i);
            boolean localMatch = true;
            boolean globalMatch = false;

            for(String filterParamName : filterMap.keySet()) {
                UIColumn column = filterMap.get(filterParamName);
                String filterParamValue = params.containsKey(filterParamName) ? params.get(filterParamName).toLowerCase() : null; 

                if(column instanceof DynamicColumn) {
                    ((DynamicColumn) column).applyModel();
                }

                String columnValue = String.valueOf(column.getValueExpression("filterBy").getValue(context.getELContext()));
                FilterConstraint filterConstraint = this.getFilterConstraint(column);

                if(hasGlobalFilter && !globalMatch) {
                    if(columnValue != null && columnValue.toLowerCase().contains(globalFilter))
                        globalMatch = true;
                }

                if(ComponentUtils.isValueBlank(filterParamValue)) {
                    localMatch = true;
                }
                else if(columnValue == null || !filterConstraint.applies(columnValue.toLowerCase(), filterParamValue)) {
                    localMatch = false;
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
        updateFilteredValue(context, table, filteredData);

        table.setRowIndex(-1);  //reset datamodel
    }
        
    public void encode(FacesContext context, DataTableRenderer renderer, DataTable table) throws IOException {
        if(table.isLazy()) {
            table.loadLazyData();
        }
        
        renderer.encodeTbody(context, table, true);
    }
    
    public void updateFilteredValue(FacesContext context, DataTable table, List<?> value) {
        table.setSelectableDataModelWrapper(null);
        ValueExpression ve = table.getValueExpression("filteredValue");
        
        if(ve != null) {
            ve.setValue(context.getELContext(), value);
        }
        else {
            if(value != null) {
                logger.log(Level.WARNING, "DataTable {0} has filtering enabled but no filteredValue model reference is defined"
                    + ", for backward compatibility falling back to page viewstate method to keep filteredValue."
                    + " It is highly suggested to use filtering with a filteredValue model reference as viewstate method is deprecated and will be removed in future."
                    , new Object[]{table.getClientId(context)});
            
            }
            
            table.setFilteredValue(value);
        }
    }
    
    public Map<String,String> populateFilterParameterMap(FacesContext context, DataTable table, Map<String,UIColumn> filterColumnMap, String globalFilterParam) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap(); 
        Map<String,String> filterParameterMap = new HashMap<String, String>();

        for(String filterName : filterColumnMap.keySet()) {
            UIColumn column = filterColumnMap.get(filterName);
            String filterValue = params.get(filterName);

            if(!ComponentUtils.isValueBlank(filterValue)) {
                String filterField = null;

                if(column instanceof DynamicColumn) {
                    ((DynamicColumn) column).applyModel();

                    filterField = table.resolveDynamicField(column.getValueExpression("filterBy"));
                }
                else {
                    filterField = table.resolveStaticField(column.getValueExpression("filterBy"));
                }

                filterParameterMap.put(filterField, filterValue);
            }
        }

        if(params.containsKey(globalFilterParam)) {
            filterParameterMap.put("globalFilter", params.get(globalFilterParam));
        }
        
        return filterParameterMap;
    }
    
    public Map<String,UIColumn> populateColumnFilterMap(FacesContext context, DataTable table) {
        Map filterMap = new HashMap<String,UIColumn>();
        String separator = String.valueOf(UINamingContainer.getSeparatorChar(context));

        ColumnGroup group = getColumnGroup(table, "header");
        if(group != null) {
            for(UIComponent child : group.getChildren()) {
                Row headerRow = (Row) child;

                if(headerRow.isRendered()) {
                    for(UIComponent headerRowChild : headerRow.getChildren()) {
                        Column column= (Column) headerRowChild;

                        if(column.isRendered() && column.getValueExpression("filterBy") != null) {
                            String filterId = column.getClientId(FacesContext.getCurrentInstance()) + separator + "filter";
                            filterMap.put(filterId, column);
                        }
                    }
                }
            }
        } 
        else {
            
            for(UIColumn column : table.getColumns()) {
                    
                if(column.getValueExpression("filterBy") != null) {
                    if(column instanceof Column) {
                        String filterId = column.getClientId(FacesContext.getCurrentInstance()) + separator + "filter";
                        filterMap.put(filterId, column);
                    }
                    else if(column instanceof DynamicColumn) {
                        DynamicColumn dynamicColumn = (DynamicColumn) column;
                        dynamicColumn.applyModel();

                        String filterId = dynamicColumn.getContainerClientId(FacesContext.getCurrentInstance()) + separator + "filter";
                        filterMap.put(filterId, dynamicColumn);
                    }
                }
            }
        }

      return filterMap;
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
        FilterConstraint filterConstraint  = null;

        if(filterMatchMode.equals(STARTS_WITH_MATCH_MODE)) {
            filterConstraint = new StartsWithFilterConstraint();
        } else if(filterMatchMode.equals(ENDS_WITH_MATCH_MODE)) {
            filterConstraint = new EndsWithFilterConstraint();
        } else if(filterMatchMode.equals(CONTAINS_MATCH_MODE)) {
            filterConstraint = new ContainsFilterConstraint();
        } else if(filterMatchMode.equals(EXACT_MATCH_MODE)) {
            filterConstraint = new ExactFilterConstraint();
        } else {
            throw new FacesException("Illegal filter match mode:" + filterMatchMode);
        }

        return filterConstraint;
    }
}
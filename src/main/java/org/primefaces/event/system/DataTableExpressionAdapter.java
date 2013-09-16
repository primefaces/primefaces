/*
 * Copyright 2009-2013 PrimeTek.
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
package org.primefaces.event.system;

import java.util.List;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import org.primefaces.component.column.Column;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.datatable.DataTable;

/**
 * DataTableExpressionAdapter converts old sortBy-filterBy syntax of 3.x to new syntax of 4.x for backward compatibility
 */
public class DataTableExpressionAdapter implements SystemEventListener {
        
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        DataTable dt = (DataTable) event.getSource();
        if(!dt.isExpressionsAdapted()) {
            FacesContext context = FacesContext.getCurrentInstance();
            String var = dt.getVar();
            int columnsCount = dt.getChildCount();
            
            if(columnsCount > 0) {
                List<UIComponent> children = dt.getChildren();
                for(int i = 0; i < columnsCount; i++) {
                    UIComponent child = children.get(i);
                    
                    if(child instanceof Column) {
                        String sortByProperty = convertStaticExpression(var, child.getValueExpression("sortBy"));
                        String filterByProperty = convertStaticExpression(var, child.getValueExpression("filterBy"));
                        
                        if(sortByProperty != null)
                            ((Column) child).setSortBy(sortByProperty);
                        
                        if(filterByProperty != null)
                            ((Column) child).setFilterBy(filterByProperty);
                    }
                    else if(child instanceof Columns) {
                        String sortByExpression = convertDynamicExpression(var, child.getValueExpression("sortBy"));
                        String filterByExpression = convertDynamicExpression(var, child.getValueExpression("filterBy"));
                        
                        if(sortByExpression != null)
                            ((Columns) child).setValueExpression("sortBy", createValueExpression(context, sortByExpression));
                        
                        if(filterByExpression != null)
                            ((Columns) child).setValueExpression("filterBy", createValueExpression(context, filterByExpression));
                    }
                }
            }
            
            dt.setExpressionsAdapted();
        }
    }
    
    private String convertStaticExpression(String var, ValueExpression ve) {
        if(ve != null) {
            String expressionString = ve.getExpressionString();
            int varIndex = expressionString.indexOf("#{" + var + '.');
            if(varIndex == 0) {
                return expressionString.substring(3 + var.length(), expressionString.length() - 1);
            }
        }
        
        return null;
    }
    
    private String convertDynamicExpression(String var, ValueExpression ve) {
        if(ve != null) {
            String expressionString = ve.getExpressionString();
            int varIndex = expressionString.indexOf("#{" + var + '[');
            if(varIndex == 0) {
                return expressionString.substring(3 + var.length(), expressionString.length() - 2);
            }
        }
        
        return null;
    }
    
    private ValueExpression createValueExpression(FacesContext context, String expression) {
        ELContext elContext = context.getELContext();
        return context.getApplication().getExpressionFactory().createValueExpression(elContext, "#{" + expression + "}", Object.class);
    }

    public boolean isListenerForSource(Object source) {
        return true;
    }    
}
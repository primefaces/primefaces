/*
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.column;

import javax.faces.component.UIColumn;
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.faces.render.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import java.util.List;
import java.util.ArrayList;
import org.primefaces.util.ComponentUtils;
import java.util.List;
import org.primefaces.model.filter.*;
import org.primefaces.component.celleditor.CellEditor;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import org.primefaces.model.menu.MenuModel;

@ResourceDependencies({

})
public class Column extends ColumnBase implements org.primefaces.component.api.UIColumn,org.primefaces.model.menu.MenuColumn {



    public CellEditor getCellEditor() {
    	
    	CellEditor cellEditor = null;
    	
	    for(UIComponent child : getChildren()) {
	        if(child instanceof CellEditor && ((CellEditor)child).isRendered()) {
	            cellEditor = (CellEditor) child;
	        	break;
	        }
	    }
	
	    return cellEditor;
    }

    public boolean isDynamic() {
        return false;
    }

    public String getColumnKey() {
        return this.getClientId();
    }

    public List getElements() {
        return getChildren();
    }

    public int getElementsCount() {
        return getChildCount();
    }
    
    public void renderChildren(FacesContext context) throws IOException {
        int childCount = this.getChildCount();
        if(childCount > 0) {
            for(int i = 0; i < childCount; i++) {
                UIComponent child = this.getChildren().get(i);
                child.encodeAll(context);
            }
        }
    }
}
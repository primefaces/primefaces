/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2005-2007 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.primefaces.component.repeat;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.ContextCallback;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;
import javax.faces.event.PostValidateEvent;
import javax.faces.event.PreValidateEvent;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.ResultSetDataModel;
import javax.faces.model.ScalarDataModel;
import javax.faces.render.Renderer;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import static javax.faces.component.UINamingContainer.getSeparatorChar;
import org.primefaces.component.api.UITabPanel;


public class UIRepeat extends UINamingContainer {

    public static final String COMPONENT_TYPE = "org.primefaces.component.UIRepeat";

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    private final static DataModel EMPTY_MODEL = new ListDataModel<Object>(Collections.emptyList());

    // our data
    private Object value;

    private transient DataModel model;

    // variables
    private String var;

    private String varStatus;

    private int index = -1;

    private Integer begin;
    private Integer end;
    private Integer step;
    private Integer size;
    private Boolean isNested = null;
    
    private Map<String, SavedState> initialChildState;
    private String initialClientId;

    public UIRepeat() {
        this.setRendererType(null);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Integer getEnd() {

        if (this.end != null) {
            return end;
        }
        ValueExpression ve = this.getValueExpression("end");
        if (ve != null) {
            return (Integer) ve.getValue(getFacesContext().getELContext());
        }
        return null;

    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getSize() {

        if (this.size != null) {
            return size;
        }
        ValueExpression ve = this.getValueExpression("size");
        if (ve != null) {
            return (Integer) ve.getValue(getFacesContext().getELContext());
        }
        return null;

    }

    public void setOffset(Integer offset) {
        this.begin = offset;
    }

    public Integer getOffset() {

        if (this.begin != null) {
            return this.begin;
        }
        ValueExpression ve = this.getValueExpression("offset");
        if (ve != null) {
            return (Integer) ve.getValue(getFacesContext().getELContext());
        }
        return null;

    }


    public void setBegin(Integer begin) {
        this.begin = begin;
    }

    public Integer getBegin() {

        if (this.begin != null) {
            return this.begin;
        }
        ValueExpression ve = this.getValueExpression("begin");
        if (ve != null) {
            return (Integer) ve.getValue(getFacesContext().getELContext());
        }
        return null;

    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Integer getStep() {

        if (this.step != null) {
            return this.step;
        }
        ValueExpression ve = this.getValueExpression("step");
        if (ve != null) {
            return (Integer) ve.getValue(getFacesContext().getELContext());
        }
        return null;

    }


    public String getVar() {
        return this.var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getVarStatus() {
        return varStatus;
    }

    public void setVarStatus(String varStatus) {
        this.varStatus = varStatus;
    }

    private void resetDataModel() {
        if (this.isNestedInIterator()) {
            this.setDataModel(null);
        }
    }

    private void setDataModel(DataModel model) {
        //noinspection unchecked
        this.model = model;
    }

    private DataModel getDataModel() {
        if (this.model == null) {
            Object val = this.getValue();
            if (val == null) {
                this.model = EMPTY_MODEL;
            } else if (val instanceof DataModel) {
                //noinspection unchecked
                this.model = (DataModel<Object>) val;
            } else if (val instanceof List) {
                //noinspection unchecked
                this.model = new ListDataModel<Object>((List<Object>) val);
            } else if (Object[].class.isAssignableFrom(val.getClass())) {
                this.model = new ArrayDataModel<Object>((Object[]) val);
            } else if (val instanceof ResultSet) {
                this.model = new ResultSetDataModel((ResultSet) val);
            } else {
                this.model = new ScalarDataModel<Object>(val);
            }
        }
        return this.model;
    }

    public Object getValue() {
        if (this.value == null) {
            ValueExpression ve = this.getValueExpression("value");
            if (ve != null) {
                return ve.getValue(getFacesContext().getELContext());
            }
        }
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    private transient StringBuffer buffer;

    private StringBuffer getBuffer() {
        if (this.buffer == null) {
            this.buffer = new StringBuffer();
        }
        this.buffer.setLength(0);
        return this.buffer;
    }

    public String getClientId(FacesContext faces) {
        String id = super.getClientId(faces);
        if (this.index >= 0) {
            id = this.getBuffer().append(id).append(
                    getSeparatorChar(faces)).append(this.index)
                    .toString();
        }
        return id;
    }

    private transient Object origValueOfVar;
    private transient Object origValueOfVarStatus;

    private void captureOrigValue(FacesContext ctx) {
        if (this.var != null || this.varStatus != null) {
            Map<String,Object> attrs = ctx.getExternalContext().getRequestMap();
            if (this.var != null) {
                this.origValueOfVar = attrs.get(this.var);
            }
            if (this.varStatus != null) {
                this.origValueOfVarStatus = attrs.get(this.varStatus);
            }
        }
    }

    private void restoreOrigValue(FacesContext ctx) {
        if (this.var != null || this.varStatus != null) {
            Map<String,Object> attrs = ctx.getExternalContext().getRequestMap();
            if (this.var != null) {
                if (this.origValueOfVar != null) {
                    attrs.put(this.var, this.origValueOfVar);
                } else {
                    attrs.remove(this.var);
                }
            }
            if (this.varStatus != null) {
                if (this.origValueOfVarStatus != null) {
                    attrs.put(this.varStatus, this.origValueOfVarStatus);
                } else {
                    attrs.remove(this.varStatus);
                }
            }
        }
    }

    private Map<String,SavedState> childState;

    private Map<String,SavedState> getChildState() {
        if (this.childState == null) {
            this.childState = new HashMap<String,SavedState>();
        }
        return this.childState;
    }
    
    private void clearChildState() {
    	this.childState = null;
    }

    private void saveChildState(FacesContext ctx) {
        if (this.getChildCount() > 0) {

            for (UIComponent uiComponent : this.getChildren()) {
                this.saveChildState(ctx, uiComponent);
            }
        }
    }

    private void removeChildState(FacesContext ctx) {
        if (this.getChildCount() > 0) {

            for (UIComponent uiComponent : this.getChildren()) {
                this.removeChildState(ctx, uiComponent);
            }
            
            if (this.childState != null) {
                this.childState.remove(this.getClientId(ctx));
            }
        }
    }

    private void removeChildState(FacesContext faces, UIComponent c) {
        String id = c.getId();
        c.setId(id);        
        
        Iterator itr = c.getFacetsAndChildren();
        while (itr.hasNext()) {
            removeChildState(faces, (UIComponent) itr.next());
        }
        if (this.childState != null) {
            this.childState.remove(c.getClientId(faces));
        }
    }
    
    private void saveChildState(FacesContext faces, UIComponent c) {

        if (c instanceof EditableValueHolder && !c.isTransient()) {
            String clientId = c.getClientId(faces);
            SavedState ss = this.getChildState().get(clientId);
            if (ss == null) {
                ss = new SavedState();
                this.getChildState().put(clientId, ss);
            }
            ss.populate((EditableValueHolder) c);
        }

        // continue hack
        Iterator itr = c.getFacetsAndChildren();
        while (itr.hasNext()) {
            saveChildState(faces, (UIComponent) itr.next());
        }
    }

    private void restoreChildState(FacesContext ctx) {
        if (this.getChildCount() > 0) {

            for (UIComponent uiComponent : this.getChildren()) {
                this.restoreChildState(ctx, uiComponent);
            }
        }
    }

    private void restoreChildState(FacesContext faces, UIComponent c) {
        // reset id
        String id = c.getId();
        c.setId(id);

        // hack
        if (c instanceof EditableValueHolder) {
            EditableValueHolder evh = (EditableValueHolder) c;
            String clientId = c.getClientId(faces);
            SavedState ss = this.getChildState().get(clientId);
            if (ss != null) {
                ss.apply(evh);
            } else {
                String childId = clientId.substring(initialClientId.length() + 1);
                childId = childId.substring(childId.indexOf(getSeparatorChar(faces)) + 1);
                childId = initialClientId + getSeparatorChar(faces) + childId;
                if (initialChildState.containsKey(childId)) {
                    SavedState initialState = initialChildState.get(childId);
                    initialState.apply(evh);
                } else {
                    NullState.apply(evh);
                }
            }
        }

        // continue hack
        Iterator itr = c.getFacetsAndChildren();
        while (itr.hasNext()) {
            restoreChildState(faces, (UIComponent) itr.next());
        }
    }

    private boolean keepSaved(FacesContext context) {

        return (hasErrorMessages(context) || isNestedInIterator());

    }

    private boolean hasErrorMessages(FacesContext context) {

        FacesMessage.Severity sev = context.getMaximumSeverity();
        return (sev != null && (FacesMessage.SEVERITY_ERROR.compareTo(sev) <= 0));
        
    }

    
    private boolean isNestedInIterator() {
        if (isNested == null) {
            UIComponent parent = this;
            while (null != (parent = parent.getParent())) {
                if (parent instanceof javax.faces.component.UIData || parent.getClass().getName().endsWith("UIRepeat") 
                        ||(parent instanceof UITabPanel && ((UITabPanel) parent).isRepeating())) {
                    isNested = Boolean.TRUE;
                    break;
                }
            }
            if (isNested == null) {
                isNested = Boolean.FALSE;
            }
            return isNested;
        } else {
            return isNested;
        }
    }

    /**
     * Save the initial child state.
     * 
     * <p>
     *  In order to be able to restore each row to a pristine condition if NO
     *  state was necessary to be saved for a given row we need to store the
     *  initial state (a.k.a the state of the skeleton) so we can restore the
     *  skeleton as if it was just created by the page markup.
     * </p>
     * 
     * @param facesContext the Faces context. 
     */
    private void saveInitialChildState(FacesContext facesContext) {
        index = -1;
        initialChildState = new ConcurrentHashMap<String, SavedState>();
        initialClientId = getClientId(facesContext);
        if (getChildCount() > 0) {
            for (UIComponent child : getChildren()) {
                saveInitialChildState(facesContext, child);
            }
        }
    }

    /**
     * Recursively create the initial state for the given component.
     * 
     * @param facesContext the Faces context.
     * @param component the UI component to save the state for.
     * @see #saveInitialChildState(javax.faces.context.FacesContext) 
     */
    private void saveInitialChildState(FacesContext facesContext, UIComponent component) {
        if (component instanceof EditableValueHolder && !component.isTransient()) {
            String clientId = component.getClientId(facesContext);
            SavedState state = new SavedState();
            initialChildState.put(clientId, state);
            state.populate((EditableValueHolder) component);
        }

        Iterator<UIComponent> iterator = component.getFacetsAndChildren();
        while (iterator.hasNext()) {
            saveChildState(facesContext, iterator.next());
        }
    }

    private void setIndex(FacesContext ctx, int index) {

        DataModel localModel = getDataModel();
        
        if (index == -1 && initialChildState == null) {
            saveInitialChildState(ctx);
        }
        
        // save child state
        if (this.index != -1 && localModel.isRowAvailable()) {
            this.saveChildState(ctx);
        } else if (this.index >= 0 && this.childState != null) {
            this.removeChildState(ctx);
        }

        this.index = index;
        localModel.setRowIndex(index);

        if (this.index != -1 && this.var != null && localModel.isRowAvailable()) {
            Map<String,Object> attrs = ctx.getExternalContext().getRequestMap();
            attrs.put(var, localModel.getRowData());
        }

        // restore child state
        if (this.index != -1 && localModel.isRowAvailable()) {
            this.restoreChildState(ctx);
        }
    }

    private void updateIterationStatus(FacesContext ctx, IterationStatus status) {
        if (this.varStatus != null) {
            Map<String,Object> attrs = ctx.getExternalContext().getRequestMap();
            attrs.put(varStatus, status);
        }
    }

    private boolean isIndexAvailable() {
        return this.getDataModel().isRowAvailable();
    }

    public void process(FacesContext faces, PhaseId phase) {

        // stop if not rendered
        if (!this.isRendered())
            return;

        // clear datamodel
        this.resetDataModel();

        // We must clear the child state if we just entered the Render Phase, and there are no error messages
        if (PhaseId.RENDER_RESPONSE.equals(phase) && !hasErrorMessages(faces)) {
        	this.clearChildState();
        }

        // reset index
        this.captureOrigValue(faces);        
        this.setIndex(faces, -1);

        try {
            // has children
            if (this.getChildCount() > 0) {
                Iterator itr;
                UIComponent c;

                Integer begin = this.getBegin();
                Integer step = this.getStep();
                Integer end = this.getEnd();
                Integer offset = this.getOffset();

                if (null != offset && offset > 0) {
                    begin = offset;
                }

                Integer size = this.getSize();
                if (null != size) {
                    end = size;
                }

                // grab renderer
                String rendererType = getRendererType();
                Renderer renderer = null;
                if (rendererType != null) {
                    renderer = getRenderer(faces);
                }

                int rowCount = getDataModel().getRowCount();
                int i = ((begin != null) ? begin : 0);
                int e = ((end != null) ? end : rowCount);
                int s = ((step != null) ? step : 1);
                validateIterationControlValues(rowCount, i, e);
                if (null != size && size > 0) {
                    e = size - 1;
                }

                this.setIndex(faces, i);
                this.updateIterationStatus(faces, new IterationStatus(true, (i + s > e || rowCount == 1), i, begin, end, step));
                while (i <= e && this.isIndexAvailable()) {

                    if (PhaseId.RENDER_RESPONSE.equals(phase)
                            && renderer != null) {
                        renderer.encodeChildren(faces, this);
                    } else {
                        itr = this.getChildren().iterator();
                        while (itr.hasNext()) {
                            c = (UIComponent) itr.next();
                            if (PhaseId.APPLY_REQUEST_VALUES.equals(phase)) {
                                c.processDecodes(faces);
                            } else if (PhaseId.PROCESS_VALIDATIONS
                                    .equals(phase)) {
                                c.processValidators(faces);
                            } else if (PhaseId.UPDATE_MODEL_VALUES
                                    .equals(phase)) {
                                c.processUpdates(faces);
                            } else if (PhaseId.RENDER_RESPONSE.equals(phase)) {
                                c.encodeAll(faces);
                            }
                        }
                    }
                    i += s;
                    this.setIndex(faces, i);
                    this.updateIterationStatus(faces, new IterationStatus(false, i + s >= e, i, begin, end, step));
                }
            }
        } catch (IOException e) {
            throw new FacesException(e);
        } finally {
            this.setIndex(faces, -1);
            this.restoreOrigValue(faces);
        }

        /*
         * Once rendering is done we need to make sure the child components
         * are not still having client ids that use an index.
         */
        if (PhaseId.RENDER_RESPONSE.equals(phase)) {
            resetClientIds(this);
        }
    }
    
    private void resetClientIds(UIComponent component) {
        Iterator<UIComponent> iterator = component.getFacetsAndChildren();
        while(iterator.hasNext()) {
            UIComponent child = iterator.next();
            resetClientIds(child);
            child.setId(child.getId());
        }
    }

     public boolean invokeOnComponent(FacesContext faces, String clientId,
            ContextCallback callback) throws FacesException {
        String id = super.getClientId(faces);
        if (clientId.equals(id)) {
            this.pushComponentToEL(faces, this);
            try {
                callback.invokeContextCallback(faces, this);
            }
            finally {
                this.popComponentFromEL(faces);
            }
            return true;
        } else if (clientId.startsWith(id)) {
            int prevIndex = this.index;
            int idxStart = clientId.indexOf(getSeparatorChar(faces), id
                    .length());
            if (idxStart != -1
                    && Character.isDigit(clientId.charAt(idxStart + 1))) {
                int idxEnd = clientId.indexOf(getSeparatorChar(faces),
                        idxStart+1);
                if (idxEnd != -1) {
                    int newIndex = Integer.parseInt(clientId.substring(
                            idxStart+1, idxEnd));
                    boolean found = false;
                    try {
                        this.captureOrigValue(faces);
                        this.setIndex(faces,newIndex);
                        if (this.isIndexAvailable()) {
                            found = super.invokeOnComponent(faces, clientId,
                                    callback);
                        }
                    } finally {
                        this.setIndex(faces, prevIndex);
                        this.restoreOrigValue(faces);
                    }
                    return found;
                }
            } else {
                return super.invokeOnComponent(faces, clientId, callback);
            }
        }
        return false;
    }

    @Override
    public boolean visitTree(VisitContext context, VisitCallback callback) {
        // First check to see whether we are visitable.  If not
        // short-circuit out of this subtree, though allow the
        // visit to proceed through to other subtrees.
        if (!isVisitable(context)) {
            return false;
        }
        
        FacesContext facesContext = context.getFacesContext();
        boolean visitRows = requiresRowIteration(context);

        int oldRowIndex = -1;
        if (visitRows) {
            oldRowIndex = getDataModel().getRowIndex();
            setIndex(facesContext, -1);
        }

        this.setDataModel(null);

        // Push ourselves to EL
        pushComponentToEL(facesContext, null);

        try {

            // Visit ourselves.  Note that we delegate to the
            // VisitContext to actually perform the visit.
            VisitResult result = context.invokeVisitCallback(this, callback);

            // If the visit is complete, short-circuit out and end the visit
            if (result == VisitResult.COMPLETE) {
                return true;
            }

            // Visit children, short-circuiting as necessary
            if ((result == VisitResult.ACCEPT) && doVisitChildren(context)) {

                // And finally, visit rows
                if (!visitRows) {
                    // visit rows without model access
                    for (UIComponent kid : getChildren()) {
                        if (kid.visitTree(context, callback)) {
                            return true;
                        }
                    }
                } else {
                    if (visitChildren(context, callback)) {
                        return true;
                    }
                }
            }
        }
        finally {
            // Clean up - pop EL and restore old row index
            popComponentFromEL(facesContext);
            if (visitRows) {
                setIndex(facesContext, oldRowIndex);
            }
        }

        // Return false to allow the visit to continue
        return false;
    }

    private boolean requiresRowIteration(VisitContext ctx) {

        return !ctx.getHints().contains(VisitHint.SKIP_ITERATION);

    }

    // Tests whether we need to visit our children as part of
    // a tree visit
    private boolean doVisitChildren(VisitContext context) {

        // Just need to check whether there are any ids under this
        // subtree.  Make sure row index is cleared out since
        // getSubtreeIdsToVisit() needs our row-less client id.
        //
        // We only need to position if row iteration is actually needed.
        //
        if (requiresRowIteration(context)) {
            setIndex(context.getFacesContext(), -1);
        }
        Collection<String> idsToVisit = context.getSubtreeIdsToVisit(this);
        assert(idsToVisit != null);

        // All ids or non-empty collection means we need to visit our children.
        return (!idsToVisit.isEmpty());

    }


    private void validateIterationControlValues(int rowCount,
                                                int begin,
                                                int end) {

        if (rowCount == 0) {
            return;
        }
        // PENDING i18n
        if (begin > rowCount) {
            throw new FacesException("Iteration start index is greater than the number of available rows.");
        }
        if (begin > end) {
            throw new FacesException("Iteration start index is greater than the end index.");
        }
        if (end > rowCount) {
            throw new FacesException("Iteration end index is greater than the number of available rows.");
        }
    }


    private boolean visitChildren(VisitContext context, VisitCallback callback) {

        Integer begin = this.getBegin();
        Integer end = this.getEnd();
        Integer step = this.getStep();

        int rowCount = getDataModel().getRowCount();
        int i = ((begin != null) ? begin : 0);
        int e = ((end != null) ? end : rowCount);
        int s = ((step != null) ? step : 1);
        validateIterationControlValues(rowCount, i, e);
        FacesContext faces = context.getFacesContext();
        this.setIndex(faces, i);
        this.updateIterationStatus(faces,
                                   new IterationStatus(true,
                                                       (i + s > e || rowCount == 1),
                                                       i,
                                                       begin,
                                                       end,
                                                       step));
        while (i < e && this.isIndexAvailable()) {

            this.setIndex(faces, i);
            this.updateIterationStatus(faces,
                                       new IterationStatus(false,
                                                           i + s >= e,
                                                           i,
                                                           begin,
                                                           end,
                                                           step));
            for (UIComponent kid : getChildren()) {
                if (kid.visitTree(context, callback)) {
                    return true;
                }
            }
            i += s;
        }


        return false;
    }


    public void processDecodes(FacesContext faces) {
        if (!this.isRendered())
            return;
        this.setDataModel(null);
        if (!this.keepSaved(faces)) this.childState = null;
        this.process(faces, PhaseId.APPLY_REQUEST_VALUES);
        this.decode(faces);
    }

    public void processUpdates(FacesContext faces) {
        if (!this.isRendered()) return;
        this.resetDataModel();
        this.process(faces, PhaseId.UPDATE_MODEL_VALUES);
    }

    public void processValidators(FacesContext faces) {
        if (!this.isRendered()) return;
        this.resetDataModel();
        Application app = faces.getApplication();
        app.publishEvent(faces, PreValidateEvent.class, this);
        this.process(faces, PhaseId.PROCESS_VALIDATIONS);
        app.publishEvent(faces, PostValidateEvent.class, this);
    }

    private final static SavedState NullState = new SavedState();

    // from RI
    private final static class SavedState implements Serializable {

        private Object submittedValue;

        private static final long serialVersionUID = 2920252657338389849L;

        Object getSubmittedValue() {
            return (this.submittedValue);
        }

        void setSubmittedValue(Object submittedValue) {
            this.submittedValue = submittedValue;
        }

        private boolean valid = true;

        boolean isValid() {
            return (this.valid);
        }

        void setValid(boolean valid) {
            this.valid = valid;
        }

        private Object value;

        Object getValue() {
            return (this.value);
        }

        public void setValue(Object value) {
            this.value = value;
        }

        private boolean localValueSet;

        boolean isLocalValueSet() {
            return (this.localValueSet);
        }

        public void setLocalValueSet(boolean localValueSet) {
            this.localValueSet = localValueSet;
        }

        public String toString() {
            return ("submittedValue: " + submittedValue + " value: " + value
                    + " localValueSet: " + localValueSet);
        }

        public void populate(EditableValueHolder evh) {
            this.value = evh.getLocalValue();
            this.valid = evh.isValid();
            this.submittedValue = evh.getSubmittedValue();
            this.localValueSet = evh.isLocalValueSet();
        }

        public void apply(EditableValueHolder evh) {
            evh.setValue(this.value);
            evh.setValid(this.valid);
            evh.setSubmittedValue(this.submittedValue);
            evh.setLocalValueSet(this.localValueSet);
        }

    }

    private static final class IndexedEvent extends FacesEvent {

        private static final long serialVersionUID = 1L;
        
        private final FacesEvent target;

        private final int index;

        public IndexedEvent(UIRepeat owner, FacesEvent target, int index) {
            super(owner);
            this.target = target;
            this.index = index;
        }

        public PhaseId getPhaseId() {
            return (this.target.getPhaseId());
        }

        public void setPhaseId(PhaseId phaseId) {
            this.target.setPhaseId(phaseId);
        }

        public boolean isAppropriateListener(FacesListener listener) {
            return this.target.isAppropriateListener(listener);
        }

        public void processListener(FacesListener listener) {
            UIRepeat owner = (UIRepeat) this.getComponent();
            int prevIndex = owner.index;
            FacesContext ctx = FacesContext.getCurrentInstance();
            try {
                owner.setIndex(ctx,this.index);
                if (owner.isIndexAvailable()) {
                    this.target.processListener(listener);
                }
            } finally {
                owner.setIndex(ctx,prevIndex);
            }
        }

        public int getIndex() {
            return index;
        }

        public FacesEvent getTarget() {
            return target;
        }

    }

    public void broadcast(FacesEvent event) throws AbortProcessingException {
        if (event instanceof IndexedEvent) {
            IndexedEvent idxEvent = (IndexedEvent) event;
            this.resetDataModel();
            int prevIndex = this.index;
            FacesContext ctx = FacesContext.getCurrentInstance();
            FacesEvent target = idxEvent.getTarget();
            UIComponent source = target.getComponent();
            UIComponent compositeParent = null;
            try {
                int rowCount = getDataModel().getRowCount();
                int idx = idxEvent.getIndex();
                this.setIndex(ctx, idx);
                Integer begin = this.getBegin();
                Integer end = this.getEnd();
                Integer step = this.getStep();
                int b = ((begin != null) ? begin : 0);
                int e = ((end != null) ? end : rowCount);
                int s = ((step != null) ? step : 1);
                this.updateIterationStatus(ctx,
                                           new IterationStatus(idx == b,
                                                               (idx + s >= e || rowCount == 1),
                                                               idx,
                                                               begin,
                                                               end,
                                                               step));
                if (this.isIndexAvailable()) {
                    if (!UIComponent.isCompositeComponent(source)) {
                        compositeParent = UIComponent
                              .getCompositeComponentParent(source);
                    }
                    if (compositeParent != null) {
                        compositeParent.pushComponentToEL(ctx, null);
                    }
                    source.pushComponentToEL(ctx, null);
                    source.broadcast(target);

                }
            } finally {
                source.popComponentFromEL(ctx);
                if (compositeParent != null) {
                    compositeParent.popComponentFromEL(ctx);
                }
                this.updateIterationStatus(ctx, null);
                this.setIndex(ctx, prevIndex);
            }
        } else {
            super.broadcast(event);
        }
    }

    public void queueEvent(FacesEvent event) {
        super.queueEvent(new IndexedEvent(this, event, this.index));
    }

    public void restoreState(FacesContext faces, Object object) {
        if (faces == null) {
            throw new NullPointerException();
        }
        if (object == null) {
            return;
        }
        Object[] state = (Object[]) object;
        super.restoreState(faces, state[0]);
        //noinspection unchecked
        this.childState = (Map<String,SavedState>) state[1];
        this.begin = (Integer) state[2];
        this.end = (Integer) state[3];
        this.step = (Integer) state[4];
        this.var = (String) state[5];
        this.varStatus = (String) state[6];
        this.value = state[7];
    }

    public Object saveState(FacesContext faces) {
        resetClientIds(this);
        
        if (faces == null) {
            throw new NullPointerException();
        }
        Object[] state = new Object[8];
        state[0] = super.saveState(faces);
        state[1] = this.childState;
        state[2] = this.begin;
        state[3] = this.end;
        state[4] = this.step;
        state[5] = this.var;
        state[6] = this.varStatus;
        state[7] = this.value;
        return state;
    }

    public void encodeChildren(FacesContext faces) throws IOException {
        if (!isRendered()) {
            return;
        }
        this.setDataModel(null);
        if (!this.keepSaved(faces)) {
            this.childState = null;
        }
        this.process(faces, PhaseId.RENDER_RESPONSE);
    }

    public boolean getRendersChildren() {
        if (getRendererType() != null) {
            Renderer renderer = getRenderer(getFacesContext());
            if (renderer != null) {
                return renderer.getRendersChildren();
            }
        }
        return true;
    }
}


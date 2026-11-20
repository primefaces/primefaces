/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.context;

import org.primefaces.PrimeFaces;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.mock.FacesContextMock;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import jakarta.el.ValueExpression;
import jakarta.faces.FactoryFinder;
import jakarta.faces.application.ProjectStage;
import jakarta.faces.component.UIColumn;
import jakarta.faces.component.UIData;
import jakarta.faces.component.UIForm;
import jakarta.faces.component.UIPanel;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.PartialViewContext;
import jakarta.faces.event.PhaseId;

import com.sun.faces.component.visit.VisitContextFactoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrimePartialViewContextTest {

    private final Logger logger = Logger.getLogger(PrimePartialViewContext.class.getName());
    private FacesContext context;
    private MockedStatic<FacesContext> currentContext;
    private MockedStatic<FactoryFinder> factories;
    private PartialViewContext wrapped;
    private PrimePartialViewContext partialContext;
    private List<String> renderIds;
    private Handler logHandler;
    private UIForm form;
    private Tab tab;

    @BeforeEach
    void setup() {
        context = spy(new FacesContextMock());
        currentContext = mockStatic(FacesContext.class);
        currentContext.when(FacesContext::getCurrentInstance).thenReturn(context);
        factories = mockStatic(FactoryFinder.class);
        factories.when(() -> FactoryFinder.getFactory(FactoryFinder.VISIT_CONTEXT_FACTORY)).thenReturn(new VisitContextFactoryImpl());
        wrapped = mock(PartialViewContext.class);
        renderIds = new ArrayList<>();
        when(wrapped.getRenderIds()).thenReturn(renderIds);
        partialContext = new PrimePartialViewContext(wrapped);
        doReturn(partialContext).when(context).getPartialViewContext();

        UIViewRoot view = new UIViewRoot();
        view.setId("view");
        context.setViewRoot(view);
        form = new UIForm();
        form.setId("form");
        view.getChildren().add(form);
        TabView tabs = new TabView();
        tabs.setId("tabs");
        form.getChildren().add(tabs);
        tab = new Tab();
        tab.setId("second");
        tabs.getChildren().add(tab);
        OutputPanel content = new OutputPanel();
        content.setId("secondContent");
        tab.getChildren().add(content);

        logHandler = mock(Handler.class);
        logger.addHandler(logHandler);
    }

    @AfterEach
    void cleanup() {
        logger.removeHandler(logHandler);
        factories.close();
        currentContext.close();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void warnsForTabWithoutRenderer(String rendererType) {
        tab.setRendererType(rendererType);
        renderIds.add("form:tabs:second");

        partialContext.processPartial(PhaseId.RENDER_RESPONSE);

        assertWarning("form:tabs:second");
        assertEquals(Arrays.asList("form:tabs:second"), renderIds);
        verify(wrapped).processPartial(PhaseId.RENDER_RESPONSE);
        verify(context, never()).validationFailed();
        verify(context, never()).renderResponse();
    }

    @Test
    void warnsForProgrammaticUpdate() {
        PrimeFaces.current().ajax().update(tab);

        partialContext.processPartial(PhaseId.RENDER_RESPONSE);

        assertWarning("form:tabs:second");
    }

    @ParameterizedTest
    @ValueSource(strings = {"form:tabs", "form:tabs:secondContent"})
    void doesNotWarnForComponentsWithRenderers(String clientId) {
        renderIds.add(clientId);

        partialContext.processPartial(PhaseId.RENDER_RESPONSE);

        verifyNoInteractions(logHandler);
        verify(wrapped).processPartial(PhaseId.RENDER_RESPONSE);
    }

    @ParameterizedTest
    @EnumSource(value = ProjectStage.class, names = "Development", mode = EnumSource.Mode.EXCLUDE)
    void doesNotCheckRenderTargetsOutsideDevelopment(ProjectStage stage) {
        doReturn(stage == ProjectStage.Development).when(context).isProjectStage(ProjectStage.Development);
        renderIds.add("form:tabs:second");

        partialContext.processPartial(PhaseId.RENDER_RESPONSE);

        verifyNoInteractions(logHandler);
        verify(wrapped, never()).getRenderIds();
        verify(wrapped).processPartial(PhaseId.RENDER_RESPONSE);
    }

    @Test
    void doesNotCheckExecuteTargets() {
        when(wrapped.getExecuteIds()).thenReturn(Arrays.asList("form:tabs:second"));

        for (PhaseId phase : Arrays.asList(PhaseId.APPLY_REQUEST_VALUES, PhaseId.PROCESS_VALIDATIONS, PhaseId.UPDATE_MODEL_VALUES)) {
            partialContext.processPartial(phase);
            verify(wrapped).processPartial(phase);
        }

        verify(wrapped, never()).getRenderIds();
        partialContext.processPartial(PhaseId.RENDER_RESPONSE);
        verifyNoInteractions(logHandler);
    }

    @Test
    void doesNotCheckIndividualTargetsWhenRenderingAll() {
        when(wrapped.isRenderAll()).thenReturn(true);
        renderIds.add("form:tabs:second");

        partialContext.processPartial(PhaseId.RENDER_RESPONSE);

        verifyNoInteractions(logHandler);
        verify(wrapped).processPartial(PhaseId.RENDER_RESPONSE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"@all", "@none", "form:missing"})
    void ignoresSpecialOrUnresolvedTargets(String clientId) {
        renderIds.add(clientId);

        partialContext.processPartial(PhaseId.RENDER_RESPONSE);

        verifyNoInteractions(logHandler);
        verify(wrapped).processPartial(PhaseId.RENDER_RESPONSE);
    }

    @Test
    void resolvesRowSpecificClientIdsAndRestoresRowIndex() {
        UIData table = new UIData();
        table.setId("rows");
        table.setValue(Arrays.asList("first", "second"));
        form.getChildren().add(table);
        UIColumn column = new UIColumn();
        table.getChildren().add(column);
        Tab rowTab = new Tab();
        rowTab.setId("tab");
        column.getChildren().add(rowTab);
        renderIds.add("form:rows:1:tab");

        partialContext.processPartial(PhaseId.RENDER_RESPONSE);

        assertWarning("form:rows:1:tab");
        assertEquals(-1, table.getRowIndex());
    }

    @Test
    void skipsHiddenRowsWithTheSameClientId() {
        ExternalContext externalContext = spy(context.getExternalContext());
        Map<String, Object> requestMap = new HashMap<>();
        doReturn(requestMap).when(externalContext).getRequestMap();
        doReturn(externalContext).when(context).getExternalContext();
        UIPanel hiddenSearch = new UIPanel();
        hiddenSearch.setId("hiddenSearch");
        hiddenSearch.setRendered(false);
        form.getChildren().add(hiddenSearch);
        UIData hiddenTable = new UIData();
        hiddenTable.setId("rows");
        ValueExpression hiddenValue = mock(ValueExpression.class);
        hiddenTable.setValueExpression("value", hiddenValue);
        hiddenSearch.getChildren().add(hiddenTable);

        UIData table = new UIData();
        table.setId("rows");
        table.setVar("row");
        table.setValue(Arrays.asList("first", "second"));
        form.getChildren().add(table);
        UIColumn column = new UIColumn();
        table.getChildren().add(column);
        Tab rowTab = new Tab();
        rowTab.setId("tab");
        column.getChildren().add(rowTab);
        renderIds.add("form:rows:1:tab");
        table.setRowIndex(0);

        partialContext.processPartial(PhaseId.RENDER_RESPONSE);

        verify(hiddenValue, never()).getValue(any());
        assertWarning("form:rows:1:tab");
        assertEquals(0, table.getRowIndex());
        assertEquals("first", requestMap.get("row"));
        verify(wrapped).processPartial(PhaseId.RENDER_RESPONSE);
    }

    @Test
    void doesNotWarnForChildrenRenderedByAnUpdatedParent() {
        renderIds.add("form:tabs");
        renderIds.add("form:tabs:second");

        partialContext.processPartial(PhaseId.RENDER_RESPONSE);

        verifyNoInteractions(logHandler);
        verify(wrapped).processPartial(PhaseId.RENDER_RESPONSE);
    }

    private void assertWarning(String clientId) {
        ArgumentCaptor<LogRecord> record = ArgumentCaptor.forClass(LogRecord.class);
        verify(logHandler).publish(record.capture());
        assertEquals(Level.WARNING, record.getValue().getLevel());
        assertEquals("Can not update component \"org.primefaces.component.tabview.Tab\" with id \"" + clientId
                        + "\" without an attached renderer.",
                MessageFormat.format(record.getValue().getMessage(), record.getValue().getParameters()));
    }
}

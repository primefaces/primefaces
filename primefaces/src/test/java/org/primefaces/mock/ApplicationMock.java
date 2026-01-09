/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.mock;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import jakarta.el.ELException;
import jakarta.el.ExpressionFactory;
import jakarta.faces.FacesException;
import jakarta.faces.application.Application;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.application.StateManager;
import jakarta.faces.application.ViewHandler;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.search.SearchExpressionHandler;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.event.ActionListener;
import jakarta.faces.event.SystemEvent;
import jakarta.faces.validator.Validator;

import org.apache.el.ExpressionFactoryImpl;

@SuppressWarnings("deprecation")
public class ApplicationMock extends Application {

    private ExpressionFactory expressionFactory = new ExpressionFactoryImpl();
    private SearchExpressionHandler searchExpressionHandler = new SearchExpressionHandlerMock();

    @Override
    public ActionListener getActionListener() {
        return null;
    }

    @Override
    public void setActionListener(ActionListener listener) {

    }

    @Override
    public Locale getDefaultLocale() {
        return null;
    }

    @Override
    public void setDefaultLocale(Locale locale) {

    }

    @Override
    public String getDefaultRenderKitId() {
        return null;
    }

    @Override
    public void setDefaultRenderKitId(String renderKitId) {

    }

    @Override
    public String getMessageBundle() {
        return null;
    }

    @Override
    public void setMessageBundle(String bundle) {

    }

    @Override
    public NavigationHandler getNavigationHandler() {
        return null;
    }

    @Override
    public void setNavigationHandler(NavigationHandler handler) {

    }

    @Override
    public ViewHandler getViewHandler() {
        return null;
    }

    @Override
    public void setViewHandler(ViewHandler handler) {

    }

    @Override
    public StateManager getStateManager() {
        return null;
    }

    @Override
    public void setStateManager(StateManager manager) {

    }

    @Override
    public void addComponent(String componentType, String componentClass) {

    }

    @Override
    public UIComponent createComponent(String componentType) throws FacesException {
        return null;
    }

    @Override
    public Iterator<String> getComponentTypes() {
        return null;
    }

    @Override
    public void addConverter(String converterId, String converterClass) {

    }

    @Override
    public void addConverter(Class<?> targetClass, String converterClass) {

    }

    @Override
    public Converter createConverter(String converterId) {
        return null;
    }

    @Override
    public Converter createConverter(Class<?> targetClass) {
        return null;
    }

    @Override
    public Iterator<String> getConverterIds() {
        return null;
    }

    @Override
    public Iterator<Class<?>> getConverterTypes() {
        return null;
    }

    @Override
    public Iterator<Locale> getSupportedLocales() {
        return null;
    }

    @Override
    public void setSupportedLocales(Collection<Locale> locales) {

    }

    @Override
    public void addValidator(String validatorId, String validatorClass) {

    }

    @Override
    public Validator createValidator(String validatorId) throws FacesException {
        return null;
    }

    @Override
    public Iterator<String> getValidatorIds() {
        return null;
    }

    @Override
    public void publishEvent(FacesContext context,
            Class<? extends SystemEvent> systemEventClass,
            Object source) {

    }

    @Override
    public ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    @Override
    public <T> T evaluateExpressionGet(FacesContext context, String expression, Class<? extends T> expectedType) throws ELException {
        return expressionFactory.createValueExpression(expression, expectedType).getValue(context.getELContext());
    }

    @Override
    public SearchExpressionHandler getSearchExpressionHandler() {
        return searchExpressionHandler;
    }
}

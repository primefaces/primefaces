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
package org.primefaces.component.password;

import javax.faces.component.html.HtmlInputText;
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
import javax.faces.component.UIComponent;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.MessageFactory;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;


public abstract class PasswordBase extends HtmlInputText implements org.primefaces.component.api.Widget {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Password";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.PasswordRenderer";

	public enum PropertyKeys {

		placeholder
		,widgetVar
		,feedback
		,inline
		,promptLabel
		,weakLabel
		,goodLabel
		,strongLabel
		,redisplay
		,match;
	}

	public PasswordBase() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getPlaceholder() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.placeholder, null);
	}
	public void setPlaceholder(java.lang.String _placeholder) {
		getStateHelper().put(PropertyKeys.placeholder, _placeholder);
	}

	public java.lang.String getWidgetVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}
	public void setWidgetVar(java.lang.String _widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
	}

	public boolean isFeedback() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.feedback, false);
	}
	public void setFeedback(boolean _feedback) {
		getStateHelper().put(PropertyKeys.feedback, _feedback);
	}

	public boolean isInline() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.inline, false);
	}
	public void setInline(boolean _inline) {
		getStateHelper().put(PropertyKeys.inline, _inline);
	}

	public java.lang.String getPromptLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.promptLabel, "Please enter a password");
	}
	public void setPromptLabel(java.lang.String _promptLabel) {
		getStateHelper().put(PropertyKeys.promptLabel, _promptLabel);
	}

	public java.lang.String getWeakLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.weakLabel, "Weak");
	}
	public void setWeakLabel(java.lang.String _weakLabel) {
		getStateHelper().put(PropertyKeys.weakLabel, _weakLabel);
	}

	public java.lang.String getGoodLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.goodLabel, "Good");
	}
	public void setGoodLabel(java.lang.String _goodLabel) {
		getStateHelper().put(PropertyKeys.goodLabel, _goodLabel);
	}

	public java.lang.String getStrongLabel() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.strongLabel, "Strong");
	}
	public void setStrongLabel(java.lang.String _strongLabel) {
		getStateHelper().put(PropertyKeys.strongLabel, _strongLabel);
	}

	public boolean isRedisplay() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.redisplay, false);
	}
	public void setRedisplay(boolean _redisplay) {
		getStateHelper().put(PropertyKeys.redisplay, _redisplay);
	}

	public java.lang.String getMatch() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.match, null);
	}
	public void setMatch(java.lang.String _match) {
		getStateHelper().put(PropertyKeys.match, _match);
	}

	public String resolveWidgetVar() {
		return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
	}
}
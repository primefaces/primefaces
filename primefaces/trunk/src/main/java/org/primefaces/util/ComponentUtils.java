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
package org.primefaces.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.application.ResourceHandler;
import javax.faces.component.*;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.Widget;
import org.primefaces.config.ConfigContainer;
import org.primefaces.context.RequestContext;
import org.primefaces.expression.SearchExpressionFacade;

public class ComponentUtils {

    public static final EnumSet<VisitHint> VISIT_HINTS_SKIP_UNRENDERED = EnumSet.of(VisitHint.SKIP_UNRENDERED);

    public static final String SKIP_ITERATION_HINT = "javax.faces.visit.SKIP_ITERATION";

    private static final String SB_ESCAPE_TEXT = ComponentUtils.class.getName() + "#escapeText";

    /**
     * Algorithm works as follows;
     * - If it's an input component, submitted value is checked first since it'd be the value to be used in case validation errors
     * terminates jsf lifecycle
     * - Finally the value of the component is retrieved from backing bean and if there's a converter, converted value is returned
     *
     * @param context			FacesContext instance
     * @param component			UIComponent instance whose value will be returned
     * @return					End text
     */
    public static String getValueToRender(FacesContext context, UIComponent component) {
        if(component instanceof ValueHolder) {

            if(component instanceof EditableValueHolder) {
                EditableValueHolder input = (EditableValueHolder) component;
                Object submittedValue = input.getSubmittedValue();
                ConfigContainer config = RequestContext.getCurrentInstance().getApplicationContext().getConfig();

                if(config.isInterpretEmptyStringAsNull() && submittedValue == null && context.isValidationFailed() && !input.isValid()) {
                    return null;
                }
                else if(submittedValue != null) {
                    return submittedValue.toString();
                }
            }

            ValueHolder valueHolder = (ValueHolder) component;
            Object value = valueHolder.getValue();

            //format the value as string
            if(value != null) {
                Converter converter = valueHolder.getConverter();
                if(converter == null) {
                    Class valueType = value.getClass();
                    if(valueType == String.class && !RequestContext.getCurrentInstance().getApplicationContext().getConfig().isStringConverterAvailable()) {
                        return (String) value;
                    }

                    converter = context.getApplication().createConverter(valueType);
                }

                if(converter != null)
                    return converter.getAsString(context, component, value);
                else
                    return value.toString();    //Use toString as a fallback if there is no explicit or implicit converter
            }
            else {
                //component is a value holder but has no value
                return null;
            }
        }

        //component it not a value holder
        return null;
    }

    /**
     * Finds appropriate converter for a given value holder
     *
     * @param context			FacesContext instance
     * @param component			ValueHolder instance to look converter for
     * @return					Converter
     */
    public static Converter getConverter(FacesContext context, UIComponent component) {
    	if (!(component instanceof ValueHolder)) {
    		return null;
    	}

    	Converter converter = ((ValueHolder) component).getConverter();
    	if (converter != null) {
    		return converter;
    	}

    	ValueExpression valueExpression = component.getValueExpression("value");
    	if (valueExpression == null) {
    		return null;
    	}

    	Class<?> converterType = valueExpression.getType(context.getELContext());
    	if (converterType == null || converterType == Object.class) {
    		// no conversion is needed
    		return null;
    	}

        if (converterType == String.class
        		&& !RequestContext.getCurrentInstance().getApplicationContext().getConfig().isStringConverterAvailable()) {
        	return null;
        }

    	return context.getApplication().createConverter(converterType);
    }

    // used by p:component
    public static String findComponentClientId(String id) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent component = findComponent(facesContext.getViewRoot(), id);

        return component.getClientId(facesContext);
    }

    private static UIComponent findComponent(UIComponent base, String id) {
        if (id.equals(base.getId()))
          return base;

        UIComponent kid = null;
        UIComponent result = null;
        Iterator<UIComponent> kids = base.getFacetsAndChildren();
        while (kids.hasNext() && (result == null)) {
          kid = (UIComponent) kids.next();
          if (id.equals(kid.getId())) {
            result = kid;
            break;
          }
          result = findComponent(kid, id);
          if (result != null) {
            break;
          }
        }
        return result;
    }

    public static UIComponent findParentForm(FacesContext context, UIComponent component) {
        UIComponent parent = component.getParent();

        while(parent != null) {
            if(parent instanceof UIForm) {
                return parent;
            }

            parent = parent.getParent();
        }

        return null;
    }

    public static UniqueIdVendor findParentUniqueIdVendor(UIComponent component) {
        UIComponent parent = component.getParent();

        while(parent != null) {
            if(parent instanceof UniqueIdVendor) {
                return (UniqueIdVendor) parent;
            }

            parent = parent.getParent();
        }

        return null;
    }

    public static UIComponent findParentNamingContainer(UIComponent component) {
        UIComponent parent = component.getParent();

        while(parent != null) {
            if(parent instanceof NamingContainer) {
                return (UIComponent) parent;
            }

            parent = parent.getParent();
        }

        return null;
    }

    public static String escapeJQueryId(String id) {
        return "#" + id.replaceAll(":", "\\\\\\\\:");
    }

    public static String resolveWidgetVar(String expression) {
        return resolveWidgetVar(expression, FacesContext.getCurrentInstance().getViewRoot());
    }

    public static String resolveWidgetVar(String expression, UIComponent component) {
    	UIComponent resolvedComponent = SearchExpressionFacade.resolveComponent(
    			FacesContext.getCurrentInstance(),
    			component,
    			expression);

        if(resolvedComponent instanceof Widget) {
            return "PF('" + ((Widget) resolvedComponent).resolveWidgetVar() + "')";
        } else {
            throw new FacesException("Component with clientId " + resolvedComponent.getClientId() + " is not a Widget");
        }
    }

    /**
     * Implementation from Apache Commons Lang
     */
    public static Locale toLocale(String str) {
        if(str == null) {
            return null;
        }
        int len = str.length();
        if(len != 2 && len != 5 && len < 7) {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        char ch0 = str.charAt(0);
        char ch1 = str.charAt(1);
        if(ch0 < 'a' || ch0 > 'z' || ch1 < 'a' || ch1 > 'z') {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        if(len == 2) {
            return new Locale(str, "");
        } else {
            if(str.charAt(2) != '_') {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            char ch3 = str.charAt(3);
            if(ch3 == '_') {
                return new Locale(str.substring(0, 2), "", str.substring(4));
            }
            char ch4 = str.charAt(4);
            if(ch3 < 'A' || ch3 > 'Z' || ch4 < 'A' || ch4 > 'Z') {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            if(len == 5) {
                return new Locale(str.substring(0, 2), str.substring(3, 5));
            } else {
                if(str.charAt(5) != '_') {
                    throw new IllegalArgumentException("Invalid locale format: " + str);
                }
                return new Locale(str.substring(0, 2), str.substring(3, 5), str.substring(6));
            }
        }
    }

    public static boolean isValueBlank(String value) {
		if(value == null)
			return true;

		return value.trim().equals("");
    }

    public static boolean isRTL(FacesContext context, RTLAware component) {
        boolean globalValue = RequestContext.getCurrentInstance().isRTL();

        return globalValue||component.isRTL();
    }

    public static void processDecodesOfFacetsAndChilds(UIComponent component, FacesContext context) {
    	if (component.getFacetCount() > 0) {
    		for (UIComponent facet : component.getFacets().values()) {
    			facet.processDecodes(context);
    		}
    	}

    	if (component.getChildCount() > 0) {
	    	for (int i = 0, childCount = component.getChildCount(); i < childCount; i++) {
	    		UIComponent child = component.getChildren().get(i);
	    		child.processDecodes(context);
	    	}
    	}
    }

    public static void processValidatorsOfFacetsAndChilds(UIComponent component, FacesContext context) {
    	if (component.getFacetCount() > 0) {
    		for (UIComponent facet : component.getFacets().values()) {
    			facet.processValidators(context);
    		}
    	}

    	if (component.getChildCount() > 0) {
	    	for (int i = 0, childCount = component.getChildCount(); i < childCount; i++) {
	    		UIComponent child = component.getChildren().get(i);
	    		child.processValidators(context);
	    	}
    	}
    }

    public static void processUpdatesOfFacetsAndChilds(UIComponent component, FacesContext context) {
    	if (component.getFacetCount() > 0) {
    		for (UIComponent facet : component.getFacets().values()) {
    			facet.processUpdates(context);
    		}
    	}

    	if (component.getChildCount() > 0) {
	    	for (int i = 0, childCount = component.getChildCount(); i < childCount; i++) {
	    		UIComponent child = component.getChildren().get(i);
	    		child.processUpdates(context);
	    	}
    	}
    }

    public static NavigationCase findNavigationCase(FacesContext context, String outcome) {
        ConfigurableNavigationHandler navHandler = (ConfigurableNavigationHandler) context.getApplication().getNavigationHandler();
        String outcomeValue = (outcome == null) ? context.getViewRoot().getViewId() : outcome;

        return navHandler.getNavigationCase(context, null, outcomeValue);
    }

    public static Map<String, List<String>> getUIParams(UIComponent component) {
        List<UIComponent> children = component.getChildren();
        Map<String, List<String>> params = null;

        if(children != null && children.size() > 0) {
            params = new LinkedHashMap<String, List<String>>();

            for(UIComponent child : children) {
                if(child.isRendered() && (child instanceof UIParameter)) {
                    UIParameter uiParam = (UIParameter) child;

                    if(!uiParam.isDisable()) {
                        List<String> paramValues = params.get(uiParam.getName());
                        if(paramValues == null) {
                            paramValues = new ArrayList<String>();
                            params.put(uiParam.getName(), paramValues);
                        }

                        paramValues.add(String.valueOf(uiParam.getValue()));
                    }
                }
            }
        }

        return params;
    }

    public static String getResourceURL(FacesContext context, String value) {
        if (isValueBlank(value)) {
            return Constants.EMPTY_STRING;
        }
        else if (value.contains(ResourceHandler.RESOURCE_IDENTIFIER)) {
            return value;
        }
        else {
            String url = context.getApplication().getViewHandler().getResourceURL(context, value);

            return context.getExternalContext().encodeResourceURL(url);
        }
    }

    public static boolean isSkipIteration(VisitContext visitContext) {
        if (RequestContext.getCurrentInstance().getApplicationContext().getConfig().isAtLeastJSF21()) {
            return visitContext.getHints().contains(VisitHint.SKIP_ITERATION);
        }
        else {
            Boolean skipIterationHint = (Boolean) visitContext.getFacesContext().getAttributes().get(SKIP_ITERATION_HINT);
            return skipIterationHint != null && skipIterationHint.booleanValue() == true;
        }
    }

    public static String resolveWidgetVar(FacesContext context, Widget widget) {
        UIComponent component = (UICommand) widget;
        String userWidgetVar = (String) component.getAttributes().get("widgetVar");

        if (userWidgetVar != null) {
            return userWidgetVar;
        }
        else {
            return "widget_" + component.getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
        }
    }

    /**
     * Duplicate code from json-simple project under apache license
     * http://code.google.com/p/json-simple/source/browse/trunk/src/org/json/simple/JSONValue.java
     */
    public static String escapeText(String text) {
        if(text == null) {
            return null;
        }

        StringBuilder sb = SharedStringBuilder.get(SB_ESCAPE_TEXT);

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }

        return sb.toString();
    }
}
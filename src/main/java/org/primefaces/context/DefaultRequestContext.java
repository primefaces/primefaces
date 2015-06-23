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
package org.primefaces.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.AjaxRequestBuilder;
import org.primefaces.util.CSVBuilder;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.StringEncrypter;
import org.primefaces.util.WidgetBuilder;
import org.primefaces.visit.ResetInputVisitCallback;

public class DefaultRequestContext extends RequestContext {

    private final static String ATTRIBUTES_KEY = "ATTRIBUTES";
    private final static String CALLBACK_PARAMS_KEY = "CALLBACK_PARAMS";
    private final static String EXECUTE_SCRIPT_KEY = "EXECUTE_SCRIPT";
    private final static String APPLICATION_CONTEXT_KEY = DefaultApplicationContext.class.getName();

    private Map<Object, Object> attributes;
    private WidgetBuilder widgetBuilder;
    private AjaxRequestBuilder ajaxRequestBuilder;
    private CSVBuilder csvBuilder;
    private FacesContext context;
    private StringEncrypter encrypter;
    private ApplicationContext applicationContext;
    private Boolean ignoreAutoUpdate;
    private Boolean rtl;

    public DefaultRequestContext(FacesContext context) {
    	this.context = context;
    	this.attributes = new HashMap<Object, Object>();
    }

    @Override
    public boolean isAjaxRequest() {
        return context.getPartialViewContext().isAjaxRequest();
    }

    @Override
    public void addCallbackParam(String name, Object value) {
        getCallbackParams().put(name, value);
    }

    @Override
    public void execute(String script) {
        getScriptsToExecute().add(script);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCallbackParams() {
        if(attributes.get(CALLBACK_PARAMS_KEY) == null) {
            attributes.put(CALLBACK_PARAMS_KEY, new HashMap<String, Object>());
        }
        return (Map<String, Object>) attributes.get(CALLBACK_PARAMS_KEY);
    }

    @Override
	@SuppressWarnings("unchecked")
    public List<String> getScriptsToExecute() {
        if(attributes.get(EXECUTE_SCRIPT_KEY) == null) {
            attributes.put(EXECUTE_SCRIPT_KEY, new ArrayList<String>());
        }
        return (List<String>) attributes.get(EXECUTE_SCRIPT_KEY);
    }

    @Override
	public WidgetBuilder getWidgetBuilder() {
    	if (this.widgetBuilder == null) {
    		this.widgetBuilder = new WidgetBuilder(context);
    	}

        return widgetBuilder;
    }

	@Override
	public AjaxRequestBuilder getAjaxRequestBuilder() {
		if (this.ajaxRequestBuilder == null) {
			this.ajaxRequestBuilder = new AjaxRequestBuilder(context);
		}

		return ajaxRequestBuilder;
	}

    @Override
	public CSVBuilder getCSVBuilder() {
		if (this.csvBuilder == null) {
			this.csvBuilder = new CSVBuilder(context);
		}

		return csvBuilder;
	}

    @Override
    public void scrollTo(String clientId) {
        this.execute("PrimeFaces.scrollTo('" + clientId +  "');");
    }

    @Override
    public void update(String clientId) {
        // call SEF to validate if a component with the clientId exists
        if (context.isProjectStage(ProjectStage.Development)) {
            SearchExpressionFacade.resolveClientId(context, context.getViewRoot(), clientId);
        }

    	context.getPartialViewContext().getRenderIds().add(clientId);
    }

    @Override
    public void update(Collection<String> clientIds) {

        // call SEF to validate if a component with the clientId exists
        if (context.isProjectStage(ProjectStage.Development)) {
            if (clientIds != null) {
                for (String clientId : clientIds) {
                    SearchExpressionFacade.resolveClientId(context, context.getViewRoot(), clientId);
                }
            }
        }

    	context.getPartialViewContext().getRenderIds().addAll(clientIds);
    }

    @Override
    public void reset(Collection<String> expressions) {
        VisitContext visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);

        for(String expression : expressions) {
        	reset(visitContext, expression);
        }
    }

    @Override
    public void reset(String expressions) {
        VisitContext visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);

        reset(visitContext, expressions);
    }

    private void reset(VisitContext visitContext, String expressions) {
        UIViewRoot root = context.getViewRoot();

        List<UIComponent> components = SearchExpressionFacade.resolveComponents(context, root, expressions);
        for (UIComponent component : components) {
            component.visitTree(visitContext, ResetInputVisitCallback.INSTANCE);
        }
    }

    @Override
    public void openDialog(String outcome) {
        this.getAttributes().put(Constants.DIALOG_FRAMEWORK.OUTCOME, outcome);
    }

    @Override
    public void openDialog(String outcome, Map<String,Object> options, Map<String,List<String>> params) {
        this.getAttributes().put(Constants.DIALOG_FRAMEWORK.OUTCOME, outcome);

        if(options != null)
            this.getAttributes().put(Constants.DIALOG_FRAMEWORK.OPTIONS, options);

        if(params != null)
            this.getAttributes().put(Constants.DIALOG_FRAMEWORK.PARAMS, params);
    }

    @Override
    public void closeDialog(Object data) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String pfdlgcid = params.get(Constants.DIALOG_FRAMEWORK.CONVERSATION_PARAM);

        if(data != null) {
            Map<String,Object> session = context.getExternalContext().getSessionMap();
            session.put(pfdlgcid, data);
        }

        this.execute("parent.PrimeFaces.closeDialog({pfdlgcid:'" + pfdlgcid + "'});");
    }

    @Override
    public void showMessageInDialog(FacesMessage message) {

        String summary = ComponentUtils.escapeText(message.getSummary());
        summary = ComponentUtils.replaceNewLineWithHtml(summary);

        String detail = ComponentUtils.escapeText(message.getDetail());
        detail = ComponentUtils.replaceNewLineWithHtml(detail);

        this.execute("PrimeFaces.showMessageInDialog({severity:\"" + message.getSeverity()
                + "\",summary:\"" + summary
                + "\",detail:\"" + detail + "\"});");
    }

    @Override
    public void release() {
        setCurrentInstance(null, context);

        attributes = null;
        widgetBuilder = null;
        ajaxRequestBuilder = null;
        context = null;
        applicationContext = null;
        encrypter = null;
    }

    @Override
    public Map<Object, Object> getAttributes() {
        if(attributes.get(ATTRIBUTES_KEY) == null) {
            attributes.put(ATTRIBUTES_KEY, new HashMap<Object, Object>());
        }
        return (Map<Object, Object>) attributes.get(ATTRIBUTES_KEY);
    }

	@Override
	public ApplicationContext getApplicationContext() {
		if (this.applicationContext == null) {
	    	// get applicationContext from application map
	    	this.applicationContext = (ApplicationContext) context.getExternalContext().getApplicationMap().get(APPLICATION_CONTEXT_KEY);
	    	if (this.applicationContext == null) {
	    		this.applicationContext = new DefaultApplicationContext(context);
				context.getExternalContext().getApplicationMap().put(APPLICATION_CONTEXT_KEY, this.applicationContext);
	    	}
		}

		return applicationContext;
	}

	@Override
	public StringEncrypter getEncrypter() {
		// lazy init, it's not required for all pages
    	if (encrypter == null) {
	    	// we can't store it in the ApplicationMap, as Cipher isn't thread safe
	    	encrypter = new StringEncrypter(getApplicationContext().getConfig().getSecretKey());
    	}

		return encrypter;
	}

    @Override
    public boolean isSecure() {
        Object request = context.getExternalContext().getRequest();

        if(request instanceof HttpServletRequest) {
            return ((HttpServletRequest) request).isSecure();
        }
        else {
            try {
                Method method = request.getClass().getDeclaredMethod("isSecure", new Class[0]);
                return (Boolean) method.invoke(request, null);
            } catch(Exception e) {
                return false;
            }
        }
    }

    @Override
    public boolean isIgnoreAutoUpdate() {
        if (ignoreAutoUpdate == null) {
            Object ignoreAutoUpdateObject = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.IGNORE_AUTO_UPDATE_PARAM);
            ignoreAutoUpdate = (null != ignoreAutoUpdateObject && "true".equals(ignoreAutoUpdateObject)) ? true : false;
        }

        return ignoreAutoUpdate;
    }

	@Override
	public boolean isRTL()
	{
		if (rtl == null) {
			String param = context.getExternalContext().getInitParameter(Constants.ContextParams.DIRECTION);
	        if (param == null) {
	        	rtl = false;
	        } else {
				ELContext elContext = context.getELContext();
		        ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
		        ValueExpression expression = expressionFactory.createValueExpression(elContext, param, String.class);
		        String expressionValue = (String) expression.getValue(elContext);

		        rtl = (expressionValue == null) ? false : expressionValue.equalsIgnoreCase("rtl");
	        }
		}

		return rtl;
	}
}

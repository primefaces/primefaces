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
package org.primefaces.component.importenum;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;
import org.primefaces.context.RequestContext;

/**
 * {@link TagHandler} for the <code>ImportEnum</code> component.
 */
public class ImportEnumTagHandler extends TagHandler {

	private static final String DEFAULT_ALL_SUFFIX = "ALL_VALUES";

	private final TagAttribute typeTagAttribute;
	private final TagAttribute varTagAttribute;
	private final TagAttribute allSuffixTagAttribute;

	public ImportEnumTagHandler(TagConfig config) {
		super(config);

		typeTagAttribute = super.getRequiredAttribute("type");
		varTagAttribute = super.getAttribute("var");
		allSuffixTagAttribute = super.getAttribute("allSuffix");
	}

    @Override
	public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
		Class<?> type = getClassFromAttribute(typeTagAttribute, ctx);
		Map<String, Object> enumValues = getEnumValues(facesContext, type,
				allSuffixTagAttribute == null ? null : allSuffixTagAttribute.getValue(ctx));

		// Create alias/var expression
		String var;
		if (varTagAttribute == null) {
			var = type.getSimpleName(); // fall back to class name
		}
        else {
			var = varTagAttribute.getValue(ctx);
		}

		if (var.charAt(0) != '#') {
			StringBuilder varBuilder = new StringBuilder();
			varBuilder.append("#{").append(var).append("}");

			var = varBuilder.toString();
		}

		// Assign enum values to alias/var expression
		ELContext elContext = facesContext.getELContext();
		ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();

		ValueExpression aliasValueExpression = expressionFactory.createValueExpression(elContext, var, Map.class);
		aliasValueExpression.setValue(elContext, enumValues);
	}

	/**
	 * Gets the {@link Class} from the {@link TagAttribute}.
	 * 
	 * @param attribute The {@link TagAttribute}.
	 * @param ctx The {@link FaceletContext}.
	 * @return The {@link Class}.
	 */
	protected Class<?> getClassFromAttribute(TagAttribute attribute, FaceletContext ctx) {
		String type = attribute.getValue(ctx);

		try {
			return Class.forName(type, true, Thread.currentThread().getContextClassLoader());
		}
        catch (ClassNotFoundException e) {
			throw new FacesException("Class " + type + " not found.", e);
		}
	}

	/**
	 * Get all enum values of the given {@link Class}.
	 * 
     * @param facesContext The {@link FacesContext}.
	 * @param type The enum class.
     * @param allSuffix The suffix to access a array with all enum values.
	 * @return A {@link Map} with the enum values.
	 */
	protected Map<String, Object> getEnumValues(FacesContext facesContext, Class<?> type, String allSuffix) {
		
        if (type.isEnum()) {

            boolean cacheEnabled = facesContext.isProjectStage(ProjectStage.Production);
			Map<Class<?>, Map<String, Object>> cache =
                    RequestContext.getCurrentInstance().getApplicationContext().getEnumCacheMap();

			Map<String, Object> enums;

			if (cacheEnabled && cache.containsKey(type)) {
				enums = cache.get(type);
			}
            else {
				enums = new EnumHashMap<String, Object>(type);

				for (Object value : type.getEnumConstants()) {
					Enum<?> currentEnum = (Enum<?>) value;
					enums.put(currentEnum.name(), currentEnum);
				}

                if (allSuffix != null) {
                    enums.put(allSuffix, type.getEnumConstants());
                }

                enums.put(DEFAULT_ALL_SUFFIX, type.getEnumConstants());

                enums = Collections.unmodifiableMap(enums);
                
                if (cacheEnabled) {
                    cache.put(type, enums);
                }
			}

			return enums;
		}
        else {
			throw new FacesException("Class '" + type + "' is not an enum.");
		}
	}
}

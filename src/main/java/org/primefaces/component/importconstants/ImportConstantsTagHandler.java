/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.importconstants;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.util.LangUtils;

/**
 * {@link TagHandler} for the <code>ImportConstants</code> component.
 */
public class ImportConstantsTagHandler extends TagHandler {

    private final TagAttribute typeTagAttribute;
    private final TagAttribute varTagAttribute;

    public ImportConstantsTagHandler(TagConfig config) {
        super(config);

        typeTagAttribute = super.getRequiredAttribute("type");
        varTagAttribute = super.getAttribute("var");
    }

    @Override
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Class<?> type = getClassFromAttribute(typeTagAttribute, ctx);
        Map<String, Object> constants = getConstants(facesContext, type);

        // Create alias/var expression
        String var;
        if (varTagAttribute == null) {
            var = type.getSimpleName(); // fall back to class name
        }
        else {
            var = varTagAttribute.getValue(ctx);
        }

        ctx.setAttribute(var, constants);
    }

    /**
     * Gets the {@link Class} from the {@link TagAttribute}.
     *
     * @param attribute The {@link TagAttribute}.
     * @param ctx       The {@link FaceletContext}.
     * @return The {@link Class}.
     */
    protected Class<?> getClassFromAttribute(TagAttribute attribute, FaceletContext ctx) {
        String type = attribute.getValue(ctx);

        try {
            return LangUtils.loadClassForName(type);
        }
        catch (ClassNotFoundException e) {
            throw new FacesException("Class " + type + " not found.", e);
        }
    }

    /**
     * Get all constants of the given {@link Class}.
     *
     * @param facesContext The {@link FacesContext}.
     * @param type         The class which includes the constants.
     * @return A {@link Map} with the constants.
     */
    protected Map<String, Object> getConstants(FacesContext facesContext, Class<?> type) {
        boolean cacheEnabled = facesContext.isProjectStage(ProjectStage.Production);
        Map<Class<?>, Map<String, Object>> cache
                = PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getConstantsCacheMap();

        Map<String, Object> constants;

        if (cacheEnabled && cache.containsKey(type)) {
            constants = cache.get(type);
        }
        else {
            constants = Collections.unmodifiableMap(collectConstants(type));

            if (cacheEnabled) {
                cache.put(type, constants);
            }
        }

        return constants;
    }

    /**
     * Collects all constants of the given {@link Class}.
     *
     * @param type The class which includes the constants.
     * @return A {@link Map} with the found constants.
     */
    protected Map<String, Object> collectConstants(Class<?> type) {
        Map<String, Object> constants = new ConstantsHashMap<>(type);

        // Go through all the fields, and put static ones in a map.
        Field[] fields = type.getDeclaredFields();

        for (Field field : fields) {
            // Check to see if this is public static final. If not, it's not a constant.
            int modifiers = field.getModifiers();
            if (!Modifier.isFinal(modifiers) || !Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
                continue;
            }

            try {
                Object value = field.get(null); // null for static fields.
                constants.put(field.getName(), value);
            }
            catch (Exception e) {
                throw new FacesException("Could not get value of " + field.getName() + " in " + type.getName() + ".", e);
            }
        }

        return constants;
    }
}

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
package org.primefaces.component.importconstants;

import org.primefaces.cdk.api.FacesTagHandler;
import org.primefaces.cdk.api.Property;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.util.LangUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.application.ProjectStage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.TagAttribute;
import jakarta.faces.view.facelets.TagConfig;
import jakarta.faces.view.facelets.TagHandler;

/**
 * {@link TagHandler} for the <code>ImportConstants</code> component.
 */
@FacesTagHandler("Utility tag to import constants.")
public class ImportConstantsTagHandler extends TagHandler {

    @Property(description = "The constants class.", required = true, type = String.class)
    private final TagAttribute type;

    @Property(description = "The EL variable which can be used to obtain the constants.",
            defaultValue = "Name of the class without package.",
            type = String.class)
    private final TagAttribute var;

    public ImportConstantsTagHandler(TagConfig config) {
        super(config);

        type = super.getRequiredAttribute("type");
        var = super.getAttribute("var");
    }

    @Override
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Class<?> type = getClassFromAttribute(this.type, ctx);
        Map<String, Object> constants = getConstants(facesContext, type);

        // Create alias/var expression
        String var;
        if (this.var == null) {
            var = type.getSimpleName(); // fall back to class name
        }
        else {
            var = this.var.getValue(ctx);
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
    protected static Map<String, Object> collectConstants(Class<?> type) {
        Map<String, Object> constants = new ConstantsHashMap<>(type);

        // Go through all the fields, and put static ones in a map.
        Field[] fields = type.getFields();

        for (Field field : fields) {
            // already collected in a class with higher prio
            if (constants.containsKey(field.getName())) {
                continue;
            }

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

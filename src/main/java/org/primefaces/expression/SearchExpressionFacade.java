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
package org.primefaces.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.component.ContextCallback;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import org.primefaces.util.ComponentTraversalUtils;

import org.primefaces.util.LangUtils;
import org.primefaces.util.SharedStringBuilder;

/**
 * Simple facade for the whole Search Expression module.
 */
public class SearchExpressionFacade {

    public static final char[] EXPRESSION_SEPARATORS = new char[]{',', ' '};

    private static final Logger LOGGER = Logger.getLogger(SearchExpressionFacade.class.getName());

    private static final String SHARED_EXPRESSION_BUFFER_KEY = SearchExpressionFacade.class.getName() + ".SHARED_EXPRESSION_BUFFER";
    private static final String SHARED_SPLIT_BUFFER_KEY = SearchExpressionFacade.class.getName() + ".SHARED_SPLIT_BUFFER_KEY";
    private static final String SHARED_CLIENT_ID_EXPRESSION_BUFFER_KEY = SearchExpressionFacade.class.getName() + ".SHARED_CLIENT_ID_EXPRESSION_BUFFER_KEY";

    private SearchExpressionFacade() {
    }

    /**
     * Resolves a list of {@link UIComponent}s for the given expression or expressions.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expressions The search expressions.
     * @return A {@link List} with resolved {@link UIComponent}s.
     */
    public static List<UIComponent> resolveComponents(FacesContext context, UIComponent source, String expressions) {
        return resolveComponents(context, source, expressions, SearchExpressionHint.NONE);
    }

    /**
     * Resolves a list of {@link UIComponent}s for the given expression or expressions.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expressions The search expressions.
     * @param hints The hints.
     * @return A {@link List} with resolved {@link UIComponent}s.
     */
    public static List<UIComponent> resolveComponents(FacesContext context, UIComponent source, String expressions, int hints) {

        ArrayList<UIComponent> components = new ArrayList<>();

        if (!LangUtils.isValueBlank(expressions)) {
            String[] splittedExpressions = splitExpressions(context, source, expressions);

            if (splittedExpressions != null && splittedExpressions.length > 0) {

                final char separatorChar = UINamingContainer.getSeparatorChar(context);

                for (String splittedExpression : splittedExpressions) {
                    String expression = splittedExpression.trim();

                    if (LangUtils.isValueBlank(expression)) {
                        continue;
                    }

                    // if it contains a keyword and it's not a nested expression (e.g. @parent:@parent), we don't need to loop
                    if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX) && expression.indexOf(separatorChar) != -1) {
                        components.addAll(resolveComponentsByExpressionChain(context, source, expression, separatorChar, hints));
                    }
                    else {
                        // it's a keyword and not nested, just ask our resolvers
                        if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {
                            SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(expression);

                            if (resolver instanceof MultiSearchExpressionResolver) {
                                ((MultiSearchExpressionResolver) resolver).resolveComponents(context, source, source, expression, components, hints);
                            }
                            else {
                                UIComponent component = resolver.resolveComponent(context, source, source, expression, hints);

                                if (component == null) {
                                    if (!SearchExpressionUtils.isHintSet(hints, SearchExpressionHint.IGNORE_NO_RESULT)) {
                                        cannotFindComponent(context, source, expression);
                                    }
                                }
                                else {
                                    components.add(component);
                                }
                            }
                        }
                        // default ID case
                        else {
                            ResolveComponentCallback callback = new ResolveComponentCallback();
                            resolveComponentById(source, expression, separatorChar, context, callback);
                            UIComponent component = callback.getComponent();

                            if (component == null) {
                                if (!SearchExpressionUtils.isHintSet(hints, SearchExpressionHint.IGNORE_NO_RESULT)) {
                                    cannotFindComponent(context, source, expression);
                                }
                            }
                            else {
                                components.add(component);
                            }
                        }
                    }
                }
            }
        }

        return components;
    }

    /**
     * Resolves a list of {@link UIComponent} clientIds and/or passtrough expressions for the given expression or expressions.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expressions The search expressions.
     * @return A {@link List} with resolved clientIds and/or passtrough expression (like PFS, widgetVar).
     */
    public static String resolveClientIds(FacesContext context, UIComponent source, String expressions) {

        return resolveClientIds(context, source, expressions, SearchExpressionHint.NONE);
    }

    /**
     * Resolves a list of {@link UIComponent} clientIds and/or passtrough expressions for the given expression or expressions.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expressions The search expressions.
     * @param hints The hints.
     * @return A {@link List} with resolved clientIds and/or passtrough expression (like PFS, widgetVar).
     */
    public static String resolveClientIds(FacesContext context, UIComponent source, String expressions, int hints) {

        if (LangUtils.isValueBlank(expressions)) {
            if (SearchExpressionUtils.isHintSet(hints, SearchExpressionHint.PARENT_FALLBACK)) {
                return source.getParent().getClientId(context);
            }

            return null;
        }

        String[] splittedExpressions = splitExpressions(context, source, expressions);

        if (splittedExpressions != null && splittedExpressions.length > 0) {

            final char separatorChar = UINamingContainer.getSeparatorChar(context);

            StringBuilder expressionsBuffer = SharedStringBuilder.get(context, SHARED_EXPRESSION_BUFFER_KEY);

            for (int i = 0; i < splittedExpressions.length; i++) {
                String expression = splittedExpressions[i].trim();

                if (LangUtils.isValueBlank(expression)) {
                    continue;
                }

                validateExpression(context, source, expression, separatorChar);

                if (isPassTroughExpression(expression)) {
                    if (expressionsBuffer.length() > 0) {
                        expressionsBuffer.append(" ");
                    }
                    expressionsBuffer.append(expression);
                }
                else {
                    // if it contains a keyword and it's not a nested expression (e.g. @parent:@parent), we don't need to loop
                    if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX) && expression.indexOf(separatorChar) != -1) {
                        String clientIds = resolveClientIdsByExpressionChain(context, source, expression, separatorChar, hints);
                        if (!LangUtils.isValueBlank(clientIds)) {
                            if (expressionsBuffer.length() > 0) {
                                expressionsBuffer.append(" ");
                            }
                            expressionsBuffer.append(clientIds);
                        }
                    }
                    else {
                        // it's a keyword and not nested, just ask our resolvers
                        if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {
                            SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(expression);

                            if (resolver instanceof ClientIdSearchExpressionResolver) {
                                String clientIds = ((ClientIdSearchExpressionResolver) resolver).resolveClientIds(context, source, source, expression, hints);
                                if (!LangUtils.isValueBlank(clientIds)) {
                                    if (expressionsBuffer.length() > 0) {
                                        expressionsBuffer.append(" ");
                                    }
                                    expressionsBuffer.append(clientIds);
                                }
                            }
                            else if (resolver instanceof MultiSearchExpressionResolver) {
                                ArrayList<UIComponent> result = new ArrayList<>();
                                ((MultiSearchExpressionResolver) resolver).resolveComponents(context, source, source, expression, result, hints);
                                for (int j = 0; j < result.size(); j++) {
                                    UIComponent component = result.get(j);
                                    validateRenderer(context, source, component, expression, hints);
                                    if (expressionsBuffer.length() > 0) {
                                        expressionsBuffer.append(" ");
                                    }
                                    expressionsBuffer.append(component.getClientId());
                                }
                            }
                            else {
                                UIComponent component = resolver.resolveComponent(context, source, source, expression, hints);

                                if (component == null) {
                                    if (!SearchExpressionUtils.isHintSet(hints, SearchExpressionHint.IGNORE_NO_RESULT)) {
                                        cannotFindComponent(context, source, expression);
                                    }
                                }
                                else {
                                    validateRenderer(context, source, component, expression, hints);
                                    if (expressionsBuffer.length() > 0) {
                                        expressionsBuffer.append(" ");
                                    }
                                    expressionsBuffer.append(component.getClientId(context));
                                }
                            }
                        }
                        // default ID case
                        else {
                            ResolveClientIdCallback callback = new ResolveClientIdCallback(source, hints, expression);
                            resolveComponentById(source, expression, separatorChar, context, callback);

                            if (callback.getClientId() == null && !SearchExpressionUtils.isHintSet(hints, SearchExpressionHint.IGNORE_NO_RESULT)) {
                                cannotFindComponent(context, source, expression);
                            }

                            if (callback.getClientId() != null) {
                                if (expressionsBuffer.length() > 0) {
                                    expressionsBuffer.append(" ");
                                }
                                expressionsBuffer.append(callback.getClientId());
                            }
                        }
                    }
                }
            }

            String clientIds = expressionsBuffer.toString();
            if (!LangUtils.isValueBlank(clientIds)) {
                return clientIds;
            }
        }

        return null;
    }

    protected static void validateRenderer(FacesContext context, UIComponent source, UIComponent component, String expression, int hints) {
        if (SearchExpressionUtils.isHintSet(hints, SearchExpressionHint.VALIDATE_RENDERER) && context.isProjectStage(ProjectStage.Development)) {
            if (LangUtils.isValueBlank(component.getRendererType())) {
                LOGGER.warning("Can not update component \"" + component.getClass().getName()
                        + "\" with id \"" + component.getClientId(context)
                        + "\" without an attached renderer. Expression \"" + expression
                        + "\" referenced from \"" + source.getClientId(context) + "\"");
            }
        }
    }

    /**
     * Resolves a {@link UIComponent} clientId and/or passtrough expression for the given expression.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @return A resolved clientId and/or passtrough expression (like PFS, widgetVar).
     */
    public static String resolveClientId(FacesContext context, UIComponent source, String expression) {
        return resolveClientId(context, source, expression, SearchExpressionHint.NONE);
    }

    /**
     * Resolves a {@link UIComponent} clientId and/or passtrough expression for the given expression.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @param hints The hints.
     * @return A resolved clientId and/or passtrough expression (like PFS, widgetVar).
     */
    public static String resolveClientId(FacesContext context, UIComponent source, String expression, int hints) {
        if (LangUtils.isValueBlank(expression)) {
            return null;
        }

        final char separatorChar = UINamingContainer.getSeparatorChar(context);

        expression = expression.trim();

        validateExpression(context, source, expression, separatorChar);

        if (isPassTroughExpression(expression)) {
            return expression;
        }

        UIComponent component;

        // if it contains a keyword and it's not a nested expression (e.g. @parent:@parent), we don't need to loop
        if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX) && expression.indexOf(separatorChar) != -1) {
            component = resolveComponentByExpressionChain(context, source, expression, separatorChar, hints);
        }
        // it's a keyword and not nested, just ask our resolvers
        else if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {
            SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(expression);

            if (SearchExpressionUtils.isHintSet(hints, SearchExpressionHint.RESOLVE_CLIENT_SIDE)
                    && resolver instanceof ClientIdSearchExpressionResolver) {
                return ((ClientIdSearchExpressionResolver) resolver).resolveClientIds(context, source, source, expression, hints);
            }
            else {
                component = resolver.resolveComponent(context, source, source, expression, hints);
            }
        }
        // default ID case
        else {
            ResolveClientIdCallback callback = new ResolveClientIdCallback(source, hints, expression);
            resolveComponentById(source, expression, separatorChar, context, callback);

            if (callback.getClientId() == null && !SearchExpressionUtils.isHintSet(hints, SearchExpressionHint.IGNORE_NO_RESULT)) {
                cannotFindComponent(context, source, expression);
            }

            return callback.getClientId();
        }

        if (component == null) {
            if (SearchExpressionUtils.isHintSet(hints, SearchExpressionHint.IGNORE_NO_RESULT)) {
                return null;
            }
            else {
                cannotFindComponent(context, source, expression);
            }
        }

        validateRenderer(context, source, component, expression, hints);

        return component.getClientId(context);
    }

    static class ResolveClientIdCallback implements ContextCallback {

        private final UIComponent source;
        private final int hints;
        private final String expression;

        private String clientId;

        ResolveClientIdCallback(UIComponent source, int hints, String expression) {
            this.source = source;
            this.hints = hints;
            this.expression = expression;
        }

        @Override
        public void invokeContextCallback(FacesContext context, UIComponent target) {
            clientId = target.getClientId(context);
            validateRenderer(context, source, target, expression, hints);
        }

        public String getClientId() {
            return clientId;
        }
    }

    static class ResolveComponentCallback implements ContextCallback {

        private UIComponent component;

        @Override
        public void invokeContextCallback(FacesContext context, UIComponent target) {
            component = target;
        }

        public UIComponent getComponent() {
            return component;
        }
    }

    /**
     * Resolves a {@link UIComponent} for the given expression.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @return A resolved {@link UIComponent} or <code>null</code>.
     */
    public static UIComponent resolveComponent(FacesContext context, UIComponent source, String expression) {

        return resolveComponent(context, source, expression, SearchExpressionHint.NONE);
    }

    /**
     * Resolves a {@link UIComponent} for the given expression.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @param hints The hints.
     * @return A resolved {@link UIComponent} or <code>null</code>.
     */
    public static UIComponent resolveComponent(FacesContext context, UIComponent source, String expression, int hints) {

        if (LangUtils.isValueBlank(expression)) {
            if (SearchExpressionUtils.isHintSet(hints, SearchExpressionHint.PARENT_FALLBACK)) {
                return source.getParent();
            }

            return null;
        }

        final char separatorChar = UINamingContainer.getSeparatorChar(context);

        expression = expression.trim();

        validateExpression(context, source, expression, separatorChar);

        if (expression.equals(SearchExpressionConstants.NONE_KEYWORD)) {
            return null;
        }

        if (LangUtils.isValueBlank(expression)) {
            return null;
        }

        UIComponent component;

        // if it contains a keyword and it's not a nested expression (e.g. @parent:@parent), we don't need to loop
        if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX) && expression.indexOf(separatorChar) != -1) {
            component = resolveComponentByExpressionChain(context, source, expression, separatorChar, hints);
        }
        // it's a keyword and not nested, just ask our resolvers
        else if (expression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {
            SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(expression);
            component = resolver.resolveComponent(context, source, source, expression, hints);
        }
        // default ID case
        else {
            ResolveComponentCallback callback = new ResolveComponentCallback();
            resolveComponentById(source, expression, separatorChar, context, callback);
            component = callback.getComponent();
        }

        if (component == null && !SearchExpressionUtils.isHintSet(hints, SearchExpressionHint.IGNORE_NO_RESULT)) {
            cannotFindComponent(context, source, expression);
        }

        return component;
    }

    private static UIComponent resolveComponentByExpressionChain(FacesContext context, UIComponent source, String expression,
            char separatorChar, int hints) {

        boolean startsWithSeperator = expression.charAt(0) == separatorChar;

        // check if the first subExpression starts with ":",
        // this will be re-added later to the first expression (only if it's a ID expression),
        // to check if we need a absolute or relative search
        if (startsWithSeperator) {
            expression = expression.substring(1);
        }

        UIComponent last = source;

        String[] subExpressions = split(context, expression, separatorChar);
        if (subExpressions != null && subExpressions.length > 0) {
            for (int j = 0; j < subExpressions.length; j++) {

                String subExpression = subExpressions[j].trim();

                if (LangUtils.isValueBlank(subExpression)) {
                    continue;
                }

                // re-add the separator string here
                // the impl will decide to search absolute or relative then
                if (startsWithSeperator
                        && j == 0
                        && !subExpression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {
                    subExpression = separatorChar + subExpression;
                }

                SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(subExpression);
                UIComponent temp = resolver.resolveComponent(context, source, last, subExpression, hints);

                if (temp == null) {
                    if (!SearchExpressionUtils.isHintSet(hints, SearchExpressionHint.IGNORE_NO_RESULT)) {
                        throw new FacesException("Cannot find component for subexpression \"" + subExpression
                                + "\" from component with id \"" + last.getClientId(context)
                                + "\" in full expression \"" + expression
                                + "\" referenced from \"" + source.getClientId(context) + "\".");
                    }

                    return null;
                }

                last = temp;
            }
        }

        return last;
    }

    private static void resolveComponentById(UIComponent source, String expression, char seperatorChar, FacesContext context,
            ContextCallback callback) {

        ComponentTraversalUtils.firstById(expression, source, seperatorChar, context, callback);
    }

    private static ArrayList<UIComponent> resolveComponentsByExpressionChain(FacesContext context, UIComponent source, String expression, char separatorChar,
            int hints) {

        boolean startsWithSeperator = expression.charAt(0) == separatorChar;

        // check if the first subExpression starts with ":",
        // this will be re-added later to the first expression (only if it's a ID expression),
        // to check if we need a absolute or relative search
        if (startsWithSeperator) {
            expression = expression.substring(1);
        }

        ArrayList<UIComponent> lastComponents = new ArrayList<>();
        lastComponents.add(source);

        String[] subExpressions = split(context, expression, separatorChar);
        if (subExpressions != null && subExpressions.length > 0) {

            ArrayList<UIComponent> tempComponents = new ArrayList<>();

            for (int i = 0; i < subExpressions.length; i++) {

                String subExpression = subExpressions[i].trim();

                if (LangUtils.isValueBlank(subExpression)) {
                    continue;
                }

                // re-add the separator string here
                // the impl will decide to search absolute or relative then
                if (startsWithSeperator
                        && i == 0
                        && !subExpression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {
                    subExpression = separatorChar + subExpression;
                }

                SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(subExpression);

                tempComponents.clear();

                for (int j = 0; j < lastComponents.size(); j++) {
                    UIComponent last = lastComponents.get(j);

                    if (resolver instanceof MultiSearchExpressionResolver) {
                        ((MultiSearchExpressionResolver) resolver).resolveComponents(context, source, last, subExpression, tempComponents, hints);
                    }
                    else {
                        UIComponent temp = resolver.resolveComponent(context, source, last, subExpression, hints);

                        if (temp == null) {
                            if (!SearchExpressionUtils.isHintSet(hints, SearchExpressionHint.IGNORE_NO_RESULT)) {
                                throw new FacesException("Cannot find component for subexpression \"" + subExpression
                                        + "\" from component with id \"" + last.getClientId(context)
                                        + "\" in full expression \"" + expression
                                        + "\" referenced from \"" + source.getClientId(context) + "\".");
                            }
                        }
                        else {
                            tempComponents.add(temp);
                        }
                    }
                }

                lastComponents.clear();
                lastComponents.addAll(tempComponents);
                tempComponents.clear();
            }
        }

        return lastComponents;
    }

    private static String resolveClientIdsByExpressionChain(FacesContext context, UIComponent source, String expression, char separatorChar,
            int hints) {

        boolean startsWithSeperator = expression.charAt(0) == separatorChar;

        // check if the first subExpression starts with ":",
        // this will be re-added later to the first expression (only if it's a ID expression),
        // to check if we need a absolute or relative search
        if (startsWithSeperator) {
            expression = expression.substring(1);
        }

        ArrayList<UIComponent> lastComponents = new ArrayList<>();
        lastComponents.add(source);

        StringBuilder clientIdsBuilder = null;

        String[] subExpressions = split(context, expression, separatorChar);
        if (subExpressions != null && subExpressions.length > 0) {

            ArrayList<UIComponent> tempComponents = new ArrayList<>();

            for (int i = 0; i < subExpressions.length; i++) {

                String subExpression = subExpressions[i].trim();

                if (LangUtils.isValueBlank(subExpression)) {
                    continue;
                }

                // re-add the separator string here
                // the impl will decide to search absolute or relative then
                if (startsWithSeperator
                        && i == 0
                        && !subExpression.contains(SearchExpressionConstants.KEYWORD_PREFIX)) {
                    subExpression = separatorChar + subExpression;
                }

                SearchExpressionResolver resolver = SearchExpressionResolverFactory.findResolver(subExpression);

                tempComponents.clear();

                for (int j = 0; j < lastComponents.size(); j++) {
                    UIComponent last = lastComponents.get(j);

                    // if it's the last expression and the resolver is a ClientIdSearchExpressionResolver, we can call it
                    if (i == subExpressions.length - 1 && resolver instanceof ClientIdSearchExpressionResolver) {
                        String result = ((ClientIdSearchExpressionResolver) resolver).resolveClientIds(context, source, last, subExpression, hints);

                        if (!LangUtils.isValueBlank(result)) {

                            if (clientIdsBuilder == null) {
                                clientIdsBuilder = SharedStringBuilder.get(SHARED_CLIENT_ID_EXPRESSION_BUFFER_KEY);
                            }
                            else if (clientIdsBuilder.length() > 0) {
                                clientIdsBuilder.append(" ");
                            }

                            clientIdsBuilder.append(result);
                        }
                    }
                    else if (resolver instanceof MultiSearchExpressionResolver) {
                        ((MultiSearchExpressionResolver) resolver).resolveComponents(context, source, last, subExpression, tempComponents, hints);
                    }
                    else {
                        UIComponent temp = resolver.resolveComponent(context, source, last, subExpression, hints);

                        if (temp == null) {
                            if (!SearchExpressionUtils.isHintSet(hints, SearchExpressionHint.IGNORE_NO_RESULT)) {
                                throw new FacesException("Cannot find component for subexpression \"" + subExpression
                                        + "\" from component with id \"" + last.getClientId(context)
                                        + "\" in full expression \"" + expression
                                        + "\" referenced from \"" + source.getClientId(context) + "\".");
                            }
                        }
                        else {
                            tempComponents.add(temp);
                        }
                    }
                }

                lastComponents.clear();
                lastComponents.addAll(tempComponents);
                tempComponents.clear();
            }
        }

        // already initialized -> last resolver was a ClientIdExpressionResolver
        if (clientIdsBuilder == null) {
            clientIdsBuilder = SharedStringBuilder.get(SHARED_CLIENT_ID_EXPRESSION_BUFFER_KEY);

            for (int i = 0; i < lastComponents.size(); i++) {
                UIComponent result = lastComponents.get(i);
                if (clientIdsBuilder.length() > 0) {
                    clientIdsBuilder.append(" ");
                }
                clientIdsBuilder.append(result.getClientId(context));
            }

        }

        return clientIdsBuilder.toString();
    }

    protected static void cannotFindComponent(FacesContext context, UIComponent source, String expression) {
        throw new ComponentNotFoundException("Cannot find component for expression \""
                + expression + "\" referenced from \""
                + source.getClientId(context) + "\".");
    }

    protected static String[] splitExpressions(FacesContext context, UIComponent source, String expressions) {

        // split expressions by blank or comma (and ignore blank and commas inside brackets)
        String[] splittedExpressions = split(context, expressions, EXPRESSION_SEPARATORS);

        if (splittedExpressions != null) {

            validateExpressions(context, source, expressions, splittedExpressions);
        }

        return splittedExpressions;
    }

    /**
     * Validates the given search expression. We only validate it, for performance reasons, if the current {@link ProjectStage} is
     * {@link ProjectStage#Development}.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expression The search expression.
     * @param separatorChar The separator as char.
     */
    protected static void validateExpression(FacesContext context, UIComponent source,
            String expression, char separatorChar) {

        if (context.isProjectStage(ProjectStage.Development)) {

            // checks the whole expression doesn't start with ":@"
            // keywords are always related to the current component, not absolute or relative
            if (expression.startsWith(separatorChar + SearchExpressionConstants.KEYWORD_PREFIX)) {
                throw new FacesException("A expression should not start with the separater char and a keyword. "
                        + "Expression: \"" + expression + "\" referenced from \"" + source.getClientId(context) + "\"");
            }

            // Pattern to split expressions by the separator but not inside parenthesis
            String[] subExpressions = split(context, expression, separatorChar);

            if (subExpressions != null) {
                // checks for unnestable subexpressions (like @all or @none)
                if (subExpressions.length > 1) {
                    for (String subExpression : subExpressions) {
                        subExpression = subExpression.trim();

                        if (!isNestable(subExpression)) {
                            throw new FacesException("Subexpression \"" + subExpression
                                    + "\" in full expression \"" + expression
                                    + "\" from \"" + source.getClientId(context) + "\" can not be nested.");
                        }
                    }
                }
            }
        }
    }

    /**
     * Validates the given search expressions. We only validate it, for performance reasons, if the current {@link ProjectStage} is
     * {@link ProjectStage#Development}.
     *
     * @param context The {@link FacesContext}.
     * @param source The source component. E.g. a button.
     * @param expressions The search expression.
     * @param splittedExpressions The already splitted expressions.
     */
    protected static void validateExpressions(FacesContext context, UIComponent source, String expressions, String[] splittedExpressions) {

        if (context.isProjectStage(ProjectStage.Development)) {
            if (splittedExpressions.length > 1) {
                if (expressions.contains(SearchExpressionConstants.NONE_KEYWORD)
                        || expressions.contains(SearchExpressionConstants.ALL_KEYWORD)) {

                    throw new FacesException("It's not possible to use @none or @all combined with other expressions."
                            + " Expressions: \"" + expressions
                            + "\" referenced from \"" + source.getClientId(context) + "\"");
                }
            }
        }
    }

    /**
     * Splits the given string by the given separator, but ignoring separators inside parentheses.
     *
     * @param context The current {@link FacesContext}.
     * @param value The string value.
     * @param separators The separators.
     * @return The splitted string.
     */
    public static String[] split(FacesContext context, String value, char... separators) {

        if (LangUtils.isValueBlank(value)) {
            return null;
        }

        List<String> tokens = new ArrayList<>(5);
        StringBuilder buffer = SharedStringBuilder.get(context, SHARED_SPLIT_BUFFER_KEY);

        int parenthesesCounter = 0;

        int length = value.length();
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c == '(') {
                parenthesesCounter++;
            }

            if (c == ')') {
                parenthesesCounter--;
            }

            if (parenthesesCounter == 0) {
                boolean isSeparator = false;
                for (char separator : separators) {
                    if (c == separator) {
                        isSeparator = true;
                    }
                }

                if (isSeparator) {
                    // lets add token inside buffer to our tokens
                    tokens.add(buffer.toString());
                    // now we need to clear buffer
                    buffer.delete(0, buffer.length());
                }
                else {
                    buffer.append(c);
                }
            }
            else {
                buffer.append(c);
            }
        }

        // lets not forget about part after the separator
        tokens.add(buffer.toString());

        return tokens.toArray(new String[tokens.size()]);
    }

    /**
     * Checks if the given expression must not be resolved by a {@link SearchExpressionResolver}, before rendering it to the client. e.g. @all or
     * @none.
     *
     * @param expression The search expression.
     * @return <code>true</code> if it should just be rendered without manipulation or resolving.
     */
    protected static boolean isPassTroughExpression(String expression) {
        return expression.contains(SearchExpressionConstants.PFS_PREFIX);
    }

    /**
     * Checks if the given expression can be nested. e.g. @form:@parent This should not be possible e.g. with @none or @all.
     *
     * @param expression The search expression.
     * @return <code>true</code> if it's nestable.
     */
    protected static boolean isNestable(String expression) {
        return !(expression.contains(SearchExpressionConstants.ALL_KEYWORD)
                || expression.contains(SearchExpressionConstants.NONE_KEYWORD)
                || expression.contains(SearchExpressionConstants.PFS_PREFIX));
    }

}

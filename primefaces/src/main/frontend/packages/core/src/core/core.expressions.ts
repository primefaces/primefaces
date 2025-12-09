import { core } from "./core.js";
import { utils } from "./core.utils.js";

/**
 * The class providing the entry point for functions related to search expressions. 
 */
export class SearchExpressionFacade {
    /**
     * Takes a search expression that may contain multiple components, separated by commas or whitespaces. Resolves
     * each search expression to the component it refers to and returns a JQuery object with the DOM elements of
     * the resolved components.
     * @param source the source element where to start the search (e.g. required for @form).
     * @param expressions A search expression with one or multiple components to resolve.
     * @return A list with the resolved components.
     */
    resolveComponentsAsSelector(source: JQuery, expressions: string | HTMLElement | JQuery | undefined | null): JQuery {

        if (utils.isJQuery(expressions)) {
            return expressions;
        }

        if (expressions instanceof HTMLElement) {
            return $(expressions);
        }

        var splittedExpressions = this.splitExpressions(expressions);
        var elements = $();

        for (const splittedExpression of splittedExpressions) {
            var expression = core.trim(splittedExpression);
            if (expression.length > 0) {

                // skip unresolvable keywords
                if (expression == '@none' || expression == '@all' || expression.indexOf("@obs(") == 0) {
                    continue;
                }

                // just a id
                if (expression.indexOf("@") == -1) {
                    const cleanedExpression = expression.startsWith("#") ? expression.substring(1) : expression;
                    const element = document.getElementById(cleanedExpression);
                    if (element !== null) {
                        elements = elements.add($(element));
                    }
                }
                // @widget
                else if (expression.indexOf("@widgetVar(") == 0) {
                    var widgetVar = expression.substring("@widgetVar(".length, expression.length - 1);
                    var widget = core.widgets[widgetVar];

                    if (widget) {
                        const element = document.getElementById(widget.getId());
                        if (element !== null) {
                            elements = elements.add($(element));
                        }
                    } else {
                        core.widgetNotAvailable(widgetVar);
                    }
                }
                // PFS
                else if (expression.indexOf("@(") == 0) {
                    //converts pfs to jq selector e.g. @(div.mystyle :input) to div.mystyle :input
                    elements = elements.add(
                            $(expression.substring(2, expression.length - 1)));
                }
                else if (expression == '@form') {
                    const form = source.closest('form')[0];
                    if (form === undefined) {
                        core.error("Could not resolve @form for source '" + source.attr('id') + "'");
                    }
                    else {
                        elements = elements.add(form);
                    }
                }
            }
        }

        return elements;
    }

    /**
     * Takes a search expression that may contain multiple components, separated by commas or whitespaces. Resolves
     * each search expression to the component it refers to and returns a list of IDs of the resolved components.
     *
     * @param source the source element where to start the search (e.g. required for @form).
     * @param expressions A search expression with one or multiple components to resolve.
     * @return A list of IDs with the resolved components.
     */
    resolveComponents(source: JQuery, expressions: string): string[] {
        const splittedExpressions = this.splitExpressions(expressions);
        const ids: string[] = [];

        for (const splittedExpression of splittedExpressions) {
            var expression = core.trim(splittedExpression);
            if (expression.length > 0) {

                // just a id or passtrough keywords
                if (expression.indexOf("@") == -1 || expression == '@none'
                        || expression == '@all' || expression.indexOf("@obs(") == 0) {
                    if (!core.inArray(ids, expression)) {
                        ids.push(expression);
                    }
                }
                // @widget
                else if (expression.indexOf("@widgetVar(") == 0) {
                    var widgetVar = expression.substring(11, expression.length - 1),
                    widget = core.widgets[widgetVar];

                    if (widget) {
                        if (!core.inArray(ids, widget.id)) {
                            if (Array.isArray(widget.id)) {
                                ids.push(...widget.id);
                            } else {
                                ids.push(widget.id);
                            }
                        }
                    } else {
                        core.widgetNotAvailable(widgetVar);
                    }
                }
                // PFS
                else if (expression.indexOf("@(") == 0) {
                    //converts pfs to jq selector e.g. @(div.mystyle :input) to div.mystyle :input
                    var elements = $(expression.substring(2, expression.length - 1));

                    for (const element of elements) {
                        var $element = $(element);
                        var clientId = $element.data(core.CLIENT_ID_DATA) || $element.attr('id');

                        if (clientId && !core.inArray(ids, clientId)) {
                            ids.push(clientId);
                        }
                    }
                }
                else if (expression == '@form') {
                    var form = source.closest('form');
                    if (form.length == 0) {
                        core.error("Could not resolve @form for source '" + source.attr('id') + "'");
                    }
                    else {
                        var formClientId = form.data(core.CLIENT_ID_DATA) || form.attr('id');
                        if (!core.inArray(ids, formClientId)) {
                            ids.push(formClientId);
                        }
                    }
                }
            }
        }

        return ids;
    }

    /**
     * Splits the given search expression into its components. The components of a search expression are separated
     * by either a command or a whitespace.
     * 
     * When the expression is empty, returns an empty array.
     *
     * @example
     * ```js
     * splitExpressions("") // => [""]
     * splitExpressions("form") // => ["form"]
     * splitExpressions("form,input") // => ["form", "input"]
     * splitExpressions("form input") // => ["form", "input"]
     * splitExpressions("form,@child(1,2)") // => ["form", "child(1,2)"]
     * ```
     * @param expression A search expression to split.
     * @return The individual components of the given search expression.
     */
    splitExpressions(expression: string | undefined | null): string[] {
        if (!expression) {
            return [];
        }

        const expressions: string[] = [];
        let buffer = '';
        let parenthesesCounter = 0;
        
        for (const c of expression) {

            if (c === '(') {
                parenthesesCounter++;
            }

            if (c === ')') {
                parenthesesCounter--;
            }

            if ((c === ' ' || c === ',') && parenthesesCounter === 0) {
                // lets add token inside buffer to our tokens
                expressions.push(buffer);
                // now we need to clear buffer
                buffer = '';
            } else {
                buffer += c;
            }
        }

        // lets not forget about part after the separator
        expressions.push(buffer);

        return expressions;
    }
}

/**
 * The class with functionality related to working with search expressions.
 */
export class Expressions {
    /**
     * The object providing the entry point for functions related to search expressions. 
     */
    SearchExpressionFacade: SearchExpressionFacade = new SearchExpressionFacade();
}

/**
 * The object with functionality related to working with search expressions.
 */
export const expressions: Expressions = new Expressions();

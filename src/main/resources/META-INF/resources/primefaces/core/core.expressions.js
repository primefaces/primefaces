if (!PrimeFaces.expressions) {

    PrimeFaces.expressions = {};

    PrimeFaces.expressions.SearchExpressionFacade = {

        resolveComponentsAsSelector: function(expressions) {

            var splittedExpressions = PrimeFaces.expressions.SearchExpressionFacade.splitExpressions(expressions);
            var elements = $();

            if (splittedExpressions) {
                for (var i = 0; i < splittedExpressions.length; ++i) {
                    var expression =  $.trim(splittedExpressions[i]);
                    if (expression.length > 0) {

                        // skip unresolvable keywords
                        if (expression == '@none' || expression == '@all') {
                            continue;
                        }

                        // just a id
                        if (expression.indexOf("@") == -1) {
                            elements = elements.add(
                                    $(document.getElementById(expression)));
                        }
                        // @widget
                        else if (expression.indexOf("@widgetVar(") == 0) {
                            var widgetVar = expression.substring(11, expression.length - 1);
                            var widget = PrimeFaces.widgets[widgetVar];

                            if (widget) {
                                elements = elements.add(
                                        $(document.getElementById(widget.id)));
                            } else {
                                PrimeFaces.widgetNotAvailable(widgetVar);
                            }
                        }
                        // PFS
                        else if (expression.indexOf("@(") == 0) {
                            //converts pfs to jq selector e.g. @(div.mystyle :input) to div.mystyle :input
                            elements = elements.add(
                                    $(expression.substring(2, expression.length - 1)));
                        }
                    }
                }
            }

            return elements;
        },

        resolveComponents: function(expressions) {
            var splittedExpressions = PrimeFaces.expressions.SearchExpressionFacade.splitExpressions(expressions),
            ids = [];

            if (splittedExpressions) {
                for (var i = 0; i < splittedExpressions.length; ++i) {
                    var expression =  $.trim(splittedExpressions[i]);
                    if (expression.length > 0) {

                        // just a id or passtrough keywords
                        if (expression.indexOf("@") == -1 || expression == '@none' || expression == '@all') {
                            if (!PrimeFaces.inArray(ids, expression)) {
                                ids.push(expression);
                            }
                        }
                        // @widget
                        else if (expression.indexOf("@widgetVar(") == 0) {
                            var widgetVar = expression.substring(11, expression.length - 1),
                            widget = PrimeFaces.widgets[widgetVar];

                            if (widget) {
                                if (!PrimeFaces.inArray(ids, widget.id)) {
                                    ids.push(widget.id);
                                }
                            } else {
                                PrimeFaces.widgetNotAvailable(widgetVar);
                            }
                        }
                        // PFS
                        else if (expression.indexOf("@(") == 0) {
                            //converts pfs to jq selector e.g. @(div.mystyle :input) to div.mystyle :input
                            var elements = $(expression.substring(2, expression.length - 1));

                            for (var j = 0; j < elements.length; j++) {
                                var element = $(elements[j]),
                                clientId = element.data(PrimeFaces.CLIENT_ID_DATA) || element.attr('id');

                                if (clientId && !PrimeFaces.inArray(ids, clientId)) {
                                    ids.push(clientId);
                                }
                            }
                        }
                    }
                }
            }

            return ids;
        },

        splitExpressions: function(expression) {

            var expressions = [];
            var buffer = '';

            var parenthesesCounter = 0;

            for (var i = 0; i < expression.length; i++) {
                var c = expression[i];

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
    };
}
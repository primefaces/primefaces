// see #7395
// we always add validation/beanvalidation.js on each page, also if no PrimeFaces component is available
// so... just check if primefaces.js was rendered
if (window.PrimeFaces) {

    /**
     * When an element is invalid due to a validation error, the user needs to be informed. This highlighter is
     * responsible for changing the visual state of an element so that the user notices the invalid element.
     * @interface {PrimeFaces.Highlighter}
     */
    PrimeFaces.validator.Highlighter = {

        /**
         * When an element is invalid due to a validation error, the user needs to be informed. This method highlights
         * the label for the given element by adding an appropriate CSS class.
         * @param {string} forElement ID of an element with a label to highlight.
         */
        highlightLabel: function(forElement) {
            var label = $("label[for='" + forElement.attr('id') + "']");
            if (label.hasClass('ui-outputlabel')) {
                label.addClass('ui-state-error');
            }
        },

        /**
         * When an element is invalid due to a validation error, the user needs to be informed. This method removes the
         * highlighting on a label for the given element by removing the appropriate CSS class.
         * @param {string} forElement ID of an element with a label to unhighlight.
         */
        unhighlightLabel: function(forElement) {
            var label = $("label[for='" + forElement.attr('id') + "']");
            if (label.hasClass('ui-outputlabel')) {
                label.removeClass('ui-state-error');
            }
        },

        /**
         * A map between a widget type and the corresponding highlight handler for that type.
         * @type {Record<string, PrimeFaces.validation.Highlighter>}
         */
        types : {

            'default': {

                highlight: function(element) {
                    element.addClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.highlightLabel(element);
                },

                unhighlight: function(element) {
                    element.removeClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.unhighlightLabel(element);
                }
            },

            'booleanchkbox': {

                highlight: function(element) {
                    element.parent().next().addClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.highlightLabel(element);
                },

                unhighlight: function(element) {
                    element.parent().next().removeClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.unhighlightLabel(element);
                }

            },

            'manychkbox': {

                highlight: function(element) {
                    var custom = element.hasClass('ui-chkbox-clone'),
                    chkboxes;
                    
                    if(custom) {
                        var groupedInputs = $('input[name="' + $.escapeSelector(element.attr('name')) + '"].ui-chkbox-clone');
                        chkboxes = groupedInputs.parent().next();
                    }
                    else {
                        var container = element.closest('.ui-selectmanycheckbox');
                        chkboxes = container.find('div.ui-chkbox-box');
                    }

                    for(var i = 0; i < chkboxes.length; i++) {
                        chkboxes.eq(i).addClass('ui-state-error');
                    }
                },

                unhighlight: function(element) {
                    var custom = element.hasClass('ui-chkbox-clone'),
                    chkboxes;
                    
                    if(custom) {
                        var groupedInputs = $('input[name="' + element.attr('name') + '"].ui-chkbox-clone');
                        chkboxes = groupedInputs.parent().next();
                    }
                    else {
                        var container = element.closest('.ui-selectmanycheckbox');
                        chkboxes = container.find('div.ui-chkbox-box');
                    }

                    for(var i = 0; i < chkboxes.length; i++) {
                        chkboxes.eq(i).removeClass('ui-state-error');
                    }
                }

            },

            'listbox': {

                highlight: function(element) {
                    element.closest('.ui-inputfield').addClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.highlightLabel(element.closest('.ui-inputfield'));
                },

                unhighlight: function(element) {
                    element.closest('.ui-inputfield').removeClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.unhighlightLabel(element.closest('.ui-inputfield'));
                }

            },

            'onemenu': {

                highlight: function(element) {
                    element.parent().siblings('.ui-selectonemenu-trigger').addClass('ui-state-error').parent().addClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.highlightLabel(this.getFocusElement(element));
                },

                unhighlight: function(element) {
                    element.parent().siblings('.ui-selectonemenu-trigger').removeClass('ui-state-error').parent().removeClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.unhighlightLabel(this.getFocusElement(element));
                },

                getFocusElement: function(element) {
                    return element.closest('.ui-selectonemenu').find('.ui-helper-hidden-accessible > input');
                }
            },

            'spinner': {

                highlight: function(element) {
                    element.parent().addClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.highlightLabel(element.parent());
                },

                unhighlight: function(element) {
                    element.parent().removeClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.unhighlightLabel(element.parent());
                }

            },

            'oneradio': {

                highlight: function(element) {
                    var container = element.closest('.ui-selectoneradio'),
                    radios = container.find('div.ui-radiobutton-box');

                    for(var i = 0; i < radios.length; i++) {
                        radios.eq(i).addClass('ui-state-error');
                    }
                    PrimeFaces.validator.Highlighter.highlightLabel(container);
                },

                unhighlight: function(element) {
                    var container = element.closest('.ui-selectoneradio'),
                    radios = container.find('div.ui-radiobutton-box');

                    for(var i = 0; i < radios.length; i++) {
                        radios.eq(i).removeClass('ui-state-error');
                    }
                    PrimeFaces.validator.Highlighter.unhighlightLabel(container);
                }

            },

            'booleanbutton': {

                highlight: function(element) {
                    element.parent().parent().addClass('ui-state-error');
                },

                unhighlight: function(element) {
                    element.parent().parent().removeClass('ui-state-error');
                }

            },

            'inputnumber': {

                highlight: function(element) {
                    var orginalInput = element.prev('input');
                    orginalInput.addClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.highlightLabel(orginalInput);

                    // see #3706
                    orginalInput.parent().addClass('ui-state-error');
                },

                unhighlight: function(element) {
                    var orginalInput = element.prev('input');
                    orginalInput.removeClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.unhighlightLabel(orginalInput);

                    // see #3706
                    orginalInput.parent().removeClass('ui-state-error');
                }

            }
        }
    };
}
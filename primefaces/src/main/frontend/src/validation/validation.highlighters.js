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
         * @param {JQuery} forElement Element with a label to highlight.
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
         * @param {JQuery} forElement Element with a label to unhighlight.
         */
        unhighlightLabel: function(forElement) {
            var label = $("label[for='" + forElement.attr('id') + "']");
            if (label.hasClass('ui-outputlabel')) {
                label.removeClass('ui-state-error');
            }
        },

        /**
         * Applies ui-state-XXX - css-classes to an element (component).
         * @param {JQuery} element Element to which apply the css-classes.
         * @param {boolean} valid Is the input of the element valid?
         */
        applyStateCssClasses: function(element, valid) {
            if (valid) {
                element.removeClass('ui-state-error');
                element.removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid');
            }
            else {
                element.addClass('ui-state-error');
                element.addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid');
            }
        },

        /**
         * A map between a widget type and the corresponding highlight handler for that type.
         * @type {Record<string, PrimeFaces.validation.Highlighter>}
         */
        types : {

            'default': {

                highlight: function(element) {
                    PrimeFaces.validator.Highlighter.highlightLabel(element);
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(element, false);
                },

                unhighlight: function(element) {
                    PrimeFaces.validator.Highlighter.unhighlightLabel(element);
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(element, true);
                }
            },

            'booleanchkbox': {

                highlight: function(element) {
                    PrimeFaces.validator.Highlighter.highlightLabel(element);
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(element.parent().next(), false);
                },

                unhighlight: function(element) {
                    PrimeFaces.validator.Highlighter.unhighlightLabel(element);
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(element.parent().next(), true);
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
                        PrimeFaces.validator.Highlighter.applyStateCssClasses(chkboxes.eq(i), false);
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
                        PrimeFaces.validator.Highlighter.applyStateCssClasses(chkboxes.eq(i), true);
                    }
                }

            },

            'listbox': {

                highlight: function(element) {
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(element.closest('.ui-inputfield'), false);
                    PrimeFaces.validator.Highlighter.highlightLabel(element.closest('.ui-inputfield'));
                },

                unhighlight: function(element) {
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(element.closest('.ui-inputfield'), true);
                    PrimeFaces.validator.Highlighter.unhighlightLabel(element.closest('.ui-inputfield'));
                }

            },

            'onemenu': {

                highlight: function(element) {
                    var siblings = element.parent().siblings('.ui-selectonemenu-trigger');
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(siblings, false);
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(siblings.parent(), false);
                    PrimeFaces.validator.Highlighter.highlightLabel(this.getFocusElement(element));
                },

                unhighlight: function(element) {
                    var siblings = element.parent().siblings('.ui-selectonemenu-trigger');
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(siblings, true);
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(siblings.parent(), true);
                    PrimeFaces.validator.Highlighter.unhighlightLabel(this.getFocusElement(element));
                },

                getFocusElement: function(element) {
                    return element.closest('.ui-selectonemenu').find('.ui-helper-hidden-accessible > input');
                }
            },

            'spinner': {

                highlight: function(element) {
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(element.parent(), false);
                    PrimeFaces.validator.Highlighter.highlightLabel(element.parent());
                },

                unhighlight: function(element) {
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(element.parent(), true);
                    PrimeFaces.validator.Highlighter.unhighlightLabel(element.parent());
                }

            },

            'oneradio': {

                highlight: function(element) {
                    var container = element.closest('.ui-selectoneradio'),
                    radios = container.find('div.ui-radiobutton-box');

                    for(var i = 0; i < radios.length; i++) {
                        PrimeFaces.validator.Highlighter.applyStateCssClasses(radios.eq(i), false);
                    }
                    PrimeFaces.validator.Highlighter.highlightLabel(container);
                },

                unhighlight: function(element) {
                    var container = element.closest('.ui-selectoneradio'),
                    radios = container.find('div.ui-radiobutton-box');

                    for(var i = 0; i < radios.length; i++) {
                        PrimeFaces.validator.Highlighter.applyStateCssClasses(radios.eq(i), true);
                    }
                    PrimeFaces.validator.Highlighter.unhighlightLabel(container);
                }

            },

            'booleanbutton': {

                highlight: function(element) {
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(element.parent().parent(), false);
                },

                unhighlight: function(element) {
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(element.parent().parent(), true);
                }

            },
            
            'toggleswitch': {

                highlight: function(element) {
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(element.parent().next(), false);
                },

                unhighlight: function(element) {
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(element.parent().next(), true);
                }

            },

            'inputnumber': {

                highlight: function(element) {
                    var orginalInput = element.prev('input');
                        PrimeFaces.validator.Highlighter.highlightLabel(orginalInput);

                        // see #3706
                        orginalInput.parent().addClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(orginalInput, false);
                    // orginalInput.parent().addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid'); // makes visual no sense
                },

                unhighlight: function(element) {
                    var orginalInput = element.prev('input');
                    PrimeFaces.validator.Highlighter.unhighlightLabel(orginalInput);

                    // see #3706
                    orginalInput.parent().removeClass('ui-state-error');
                    PrimeFaces.validator.Highlighter.applyStateCssClasses(orginalInput, true);
                    // orginalInput.parent().removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid'); // makes visual no sense
                }

            }
        }
    };
}
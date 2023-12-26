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

                highlight: function(element, csvStateOnly) {
                    // TODO: we may pass element to a (private) function together with csvStateOnly and do addClass/removeClass there

                    if (!csvStateOnly) {
                        element.addClass('ui-state-error');
                        PrimeFaces.validator.Highlighter.highlightLabel(element);
                    }
                    element.addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid');
                },

                unhighlight: function(element, csvStateOnly) {
                    // TODO: we may pass element to a (private) function together with csvStateOnly and do addClass/removeClass there

                    if (!csvStateOnly) {
                        element.removeClass('ui-state-error');
                        PrimeFaces.validator.Highlighter.unhighlightLabel(element);
                    }
                    element.removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid');
                }
            },

            'booleanchkbox': {

                highlight: function(element, csvStateOnly) {
                    var elt2AddState = element.parent().next();
                    if (!csvStateOnly) {
                        elt2AddState.addClass('ui-state-error');
                        PrimeFaces.validator.Highlighter.highlightLabel(element);
                    }
                    elt2AddState.addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid');
                },

                unhighlight: function(element, csvStateOnly) {
                    var elt2AddState = element.parent().next();
                    if (!csvStateOnly) {
                        elt2AddState.removeClass('ui-state-error');
                        PrimeFaces.validator.Highlighter.unhighlightLabel(element);
                    }
                    elt2AddState.removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid');
                }

            },

            'manychkbox': {

                highlight: function(element, csvStateOnly) {
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
                        if (!csvStateOnly) chkboxes.eq(i).addClass('ui-state-error');
                        chkboxes.eq(i).addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid');
                    }
                },

                unhighlight: function(element, csvStateOnly) {
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
                        if (!csvStateOnly) chkboxes.eq(i).removeClass('ui-state-error');
                        chkboxes.eq(i).removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid');
                    }
                }

            },

            'listbox': {

                highlight: function(element, csvStateOnly) {
                    var elt2AddState = element.closest('.ui-inputfield'); 
                    if (!csvStateOnly) {
                        elt2AddState.addClass('ui-state-error');
                        PrimeFaces.validator.Highlighter.highlightLabel(element.closest('.ui-inputfield'));
                    }
                    elt2AddState.addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid');
                },

                unhighlight: function(element, csvStateOnly) {
                    var elt2AddState = element.closest('.ui-inputfield');
                    if (!csvStateOnly) {
                        elt2AddState.removeClass('ui-state-error');
                        PrimeFaces.validator.Highlighter.unhighlightLabel(element.closest('.ui-inputfield'));
                    }
                    elt2AddState.removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid');
                }

            },

            'onemenu': {

                highlight: function(element, csvStateOnly) {
                    var siblings = element.parent().siblings('.ui-selectonemenu-trigger');
                    if (!csvStateOnly) {
                        siblings.addClass('ui-state-error').parent().addClass('ui-state-error');
                        PrimeFaces.validator.Highlighter.highlightLabel(this.getFocusElement(element));
                    }
                    siblings.addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid').parent().addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid');
                },

                unhighlight: function(element, csvStateOnly) {
                    var siblings = element.parent().siblings('.ui-selectonemenu-trigger');
                    if (!csvStateOnly) {
                        siblings.removeClass('ui-state-error').parent().removeClass('ui-state-error');
                        PrimeFaces.validator.Highlighter.unhighlightLabel(this.getFocusElement(element));
                    }
                    siblings.removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid').parent().removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid');
                },

                getFocusElement: function(element) {
                    return element.closest('.ui-selectonemenu').find('.ui-helper-hidden-accessible > input');
                }
            },

            'spinner': {

                highlight: function(element, csvStateOnly) {
                    var elt2AddState = element.parent();
                    if (!csvStateOnly) {
                        elt2AddState.addClass('ui-state-error');
                        PrimeFaces.validator.Highlighter.highlightLabel(element.parent());
                    }
                    elt2AddState.addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid');
                },

                unhighlight: function(element, csvStateOnly) {
                    var elt2AddState = element.parent();
                    if (!csvStateOnly) {
                        elt2AddState.removeClass('ui-state-error');
                        PrimeFaces.validator.Highlighter.unhighlightLabel(element.parent());
                    }
                    elt2AddState.removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid');
                }

            },

            'oneradio': {

                highlight: function(element, csvStateOnly) {
                    var container = element.closest('.ui-selectoneradio'),
                    radios = container.find('div.ui-radiobutton-box');

                    for(var i = 0; i < radios.length; i++) {
                        if (!csvStateOnly) radios.eq(i).addClass('ui-state-error');
                        radios.eq(i).addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid');
                    }
                    if (!csvStateOnly) PrimeFaces.validator.Highlighter.highlightLabel(container);
                },

                unhighlight: function(element, csvStateOnly) {
                    var container = element.closest('.ui-selectoneradio'),
                    radios = container.find('div.ui-radiobutton-box');

                    for(var i = 0; i < radios.length; i++) {
                        if (!csvStateOnly) radios.eq(i).removeClass('ui-state-error');
                        radios.eq(i).removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid');                        
                    }
                    if (!csvStateOnly) PrimeFaces.validator.Highlighter.unhighlightLabel(container);
                }

            },

            'booleanbutton': {

                highlight: function(element, csvStateOnly) {
                    var elt2AddState = element.parent().parent();
                    if (!csvStateOnly) elt2AddState.addClass('ui-state-error');
                    elt2AddState.addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid');
                },

                unhighlight: function(element, csvStateOnly) {
                    var elt2AddState = element.parent().parent();
                    if (!csvStateOnly) elt2AddState.removeClass('ui-state-error');
                    radios.eq(i).removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid');
                }

            },
            
            'toggleswitch': {

                highlight: function(element, csvStateOnly) {
                    var elt2AddState = element.parent().next();
                    if (!csvStateOnly) elt2AddState.addClass('ui-state-error');
                    elt2AddState.addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid');
                },

                unhighlight: function(element, csvStateOnly) {
                    var elt2AddState = element.parent().next();
                    if (!csvStateOnly) elt2AddState.removeClass('ui-state-error');
                    radios.eq(i).removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid');
                }

            },

            'inputnumber': {

                highlight: function(element, csvStateOnly) {
                    var orginalInput = element.prev('input');
                    if (!csvStateOnly) {
                        orginalInput.addClass('ui-state-error');
                        PrimeFaces.validator.Highlighter.highlightLabel(orginalInput);

                        // see #3706
                        orginalInput.parent().addClass('ui-state-error');
                    }
                    orginalInput.addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid');
                    // orginalInput.parent().addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid'); // makes visual no sense
                },

                unhighlight: function(element, csvStateOnly) {
                    var orginalInput = element.prev('input');
                    if (!csvStateOnly) {
                        orginalInput.removeClass('ui-state-error');
                        PrimeFaces.validator.Highlighter.unhighlightLabel(orginalInput);

                        // see #3706
                        orginalInput.parent().removeClass('ui-state-error');
                    }
                    orginalInput.removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid');
                    // orginalInput.parent().removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid'); // makes visual no sense
                }

            }
        }
    };
}
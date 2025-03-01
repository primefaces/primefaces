/**
 * When an element is invalid due to a validation error, the user needs to be informed. This highlighter is
 * responsible for changing the visual state of an element so that the user notices the invalid element.
 */
export class ValidationHighlighter {
    /**
     * A map between a widget type and the corresponding highlight handler for that type.
     */
    types: Record<string, PrimeType.validation.Highlighter> = {};

    /**
     * When an element is invalid due to a validation error, the user needs to be informed. This method highlights
     * the label for the given element by adding an appropriate CSS class.
     * @param forElement Element with a label to highlight.
     */
    highlightLabel(forElement: JQuery): void {
        var label = $("label[for='" + forElement.attr('id') + "']");
        if (label.hasClass('ui-outputlabel')) {
            label.addClass('ui-state-error');
        }
    }

    /**
     * When an element is invalid due to a validation error, the user needs to be informed. This method removes the
     * highlighting on a label for the given element by removing the appropriate CSS class.
     * @param forElement Element with a label to unhighlight.
     */
    unhighlightLabel(forElement: JQuery): void {
        var label = $("label[for='" + forElement.attr('id') + "']");
        if (label.hasClass('ui-outputlabel')) {
            label.removeClass('ui-state-error');
        }
    }

    /**
     * Applies ui-state-XXX - css-classes to an element (component).
     * @param element Element to which apply the css-classes.
     * @param valid Is the input of the element valid?
     */
    applyStateCssClasses(element: JQuery, valid: boolean): void {
        if (valid) {
            element.removeClass('ui-state-error');
            element.removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid');
        }
        else {
            element.addClass('ui-state-error');
            element.addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid');
        }
    }
};

class DefaultHighlighter implements PrimeType.validation.Highlighter {
    highlight(element: JQuery): void {
        validationHighlighter.highlightLabel(element);
        validationHighlighter.applyStateCssClasses(element, false);
    }

    unhighlight(element: JQuery): void {
        validationHighlighter.unhighlightLabel(element);
        validationHighlighter.applyStateCssClasses(element, true);
    }
}

class BooleanCheckBoxHighlighter implements PrimeType.validation.Highlighter {
    highlight(element: JQuery): void {
        validationHighlighter.highlightLabel(element);
        validationHighlighter.applyStateCssClasses(element.parent().next(), false);
    }

    unhighlight(element: JQuery): void {
        validationHighlighter.unhighlightLabel(element);
        validationHighlighter.applyStateCssClasses(element.parent().next(), true);
    }
}

class ManyCheckBoxHighlighter implements PrimeType.validation.Highlighter {
    highlight(element: JQuery): void {
        const custom = element.hasClass('ui-chkbox-clone');
        let chkboxes: JQuery;
        
        if(custom) {
            const groupedInputs = $('input[name="' + CSS.escape(element.attr('name') ?? "") + '"].ui-chkbox-clone');
            chkboxes = groupedInputs.parent().next();
        }
        else {
            const container = element.closest('.ui-selectmanycheckbox');
            chkboxes = container.find('div.ui-chkbox-box');
        }

        for(let i = 0; i < chkboxes.length; i++) {
            validationHighlighter.applyStateCssClasses(chkboxes.eq(i), false);
        }
    }

    unhighlight(element: JQuery): void {
        const custom = element.hasClass('ui-chkbox-clone');
        let chkboxes: JQuery;
        
        if(custom) {
            const groupedInputs = $('input[name="' + element.attr('name') + '"].ui-chkbox-clone');
            chkboxes = groupedInputs.parent().next();
        }
        else {
            const container = element.closest('.ui-selectmanycheckbox');
            chkboxes = container.find('div.ui-chkbox-box');
        }

        for(let i = 0; i < chkboxes.length; i++) {
            validationHighlighter.applyStateCssClasses(chkboxes.eq(i), true);
        }
    }
}

class ListBoxHighlighter implements PrimeType.validation.Highlighter {
    highlight(element: JQuery): void {
        validationHighlighter.applyStateCssClasses(element.closest('.ui-inputfield'), false);
        validationHighlighter.highlightLabel(element.closest('.ui-inputfield'));
    }

    unhighlight(element: JQuery): void {
        validationHighlighter.applyStateCssClasses(element.closest('.ui-inputfield'), true);
        validationHighlighter.unhighlightLabel(element.closest('.ui-inputfield'));
    }
}

class OneMenuHighlighter implements PrimeType.validation.Highlighter {
    highlight(element: JQuery): void {
        var siblings = element.parent().siblings('.ui-selectonemenu-trigger');
        validationHighlighter.applyStateCssClasses(siblings, false);
        validationHighlighter.applyStateCssClasses(siblings.parent(), false);
        validationHighlighter.highlightLabel(this.getFocusElement(element));
    }

    unhighlight(element: JQuery): void {
        var siblings = element.parent().siblings('.ui-selectonemenu-trigger');
        validationHighlighter.applyStateCssClasses(siblings, true);
        validationHighlighter.applyStateCssClasses(siblings.parent(), true);
        validationHighlighter.unhighlightLabel(this.getFocusElement(element));
    }

    private getFocusElement(element: JQuery): JQuery {
        return element.closest('.ui-selectonemenu').find('.ui-helper-hidden-accessible > input');
    }
}

class SpinnerHighlighter implements PrimeType.validation.Highlighter {
    highlight(element: JQuery): void {
        validationHighlighter.applyStateCssClasses(element.parent(), false);
        validationHighlighter.highlightLabel(element.parent());
    }

    unhighlight(element: JQuery): void {
        validationHighlighter.applyStateCssClasses(element.parent(), true);
        validationHighlighter.unhighlightLabel(element.parent());
    }
}

class OneRadioHighlighter implements PrimeType.validation.Highlighter {
    highlight(element: JQuery): void {
        var container = element.closest('.ui-selectoneradio'),
        radios = container.find('div.ui-radiobutton-box');

        for(var i = 0; i < radios.length; i++) {
            validationHighlighter.applyStateCssClasses(radios.eq(i), false);
        }
        validationHighlighter.highlightLabel(container);
    }

    unhighlight(element: JQuery): void {
        var container = element.closest('.ui-selectoneradio'),
        radios = container.find('div.ui-radiobutton-box');

        for(var i = 0; i < radios.length; i++) {
            validationHighlighter.applyStateCssClasses(radios.eq(i), true);
        }
        validationHighlighter.unhighlightLabel(container);
    }
}

class BooleanButtonHighlighter implements PrimeType.validation.Highlighter {
    highlight(element: JQuery): void {
        validationHighlighter.applyStateCssClasses(element.parent().parent(), false);
    }

    unhighlight(element: JQuery): void {
        validationHighlighter.applyStateCssClasses(element.parent().parent(), true);
    }
}

class ToggleSwitchHighlighter implements PrimeType.validation.Highlighter {
    highlight(element: JQuery): void {
        validationHighlighter.applyStateCssClasses(element.parent().next(), false);
    }

    unhighlight(element: JQuery): void {
        validationHighlighter.applyStateCssClasses(element.parent().next(), true);
    }
}

class InputNumberHighlighter implements PrimeType.validation.Highlighter {
    highlight(element: JQuery): void {
        const orginalInput = element.prev('input');
        validationHighlighter.highlightLabel(orginalInput);

        // see #3706
        orginalInput.parent().addClass('ui-state-error');
        validationHighlighter.applyStateCssClasses(orginalInput, false);
        // orginalInput.parent().addClass('ui-state-csv-invalid').removeClass('ui-state-csv-valid'); // makes visual no sense
    }

    unhighlight(element: JQuery): void {
        const orginalInput = element.prev('input');
        validationHighlighter.unhighlightLabel(orginalInput);

        // see #3706
        orginalInput.parent().removeClass('ui-state-error');
        validationHighlighter.applyStateCssClasses(orginalInput, true);
        // orginalInput.parent().removeClass('ui-state-csv-invalid').addClass('ui-state-csv-valid'); // makes visual no sense
    }
}

export function registerCommonHighlighters(): void {
    validationHighlighter.types["default"] = new DefaultHighlighter();
    validationHighlighter.types["booleanchkbox"] = new BooleanCheckBoxHighlighter();
    validationHighlighter.types["manychkbox"] = new ManyCheckBoxHighlighter();
    validationHighlighter.types["listbox"] = new ListBoxHighlighter();
    validationHighlighter.types["onemenu"] = new OneMenuHighlighter();
    validationHighlighter.types["spinner"] = new SpinnerHighlighter();
    validationHighlighter.types["oneradio"] = new OneRadioHighlighter();
    validationHighlighter.types["booleanbutton"] = new BooleanButtonHighlighter();
    validationHighlighter.types["toggleswitch"] = new ToggleSwitchHighlighter();
    validationHighlighter.types["inputnumber"] = new InputNumberHighlighter();
}

/**
 * When an element is invalid due to a validation error, the user needs to be informed. This highlighter is
 * responsible for changing the visual state of an element so that the user notices the invalid element.
 */
export const validationHighlighter: ValidationHighlighter = new ValidationHighlighter();
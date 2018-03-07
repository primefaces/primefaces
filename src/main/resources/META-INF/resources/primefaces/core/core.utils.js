if (!PrimeFaces.utils) {

    PrimeFaces.utils = {

        resolveDynamicOverlayContainer: function(widget) {
            return widget.cfg.appendTo
                ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(widget.cfg.appendTo)
                : $(document.body);
        },

        /**
         * Removes the overlay from the appendTo overlay container.
         */
        removeDynamicOverlay: function(widget, element, id, appendTo) {
            // if the id contains a ':'
            appendTo.children(PrimeFaces.escapeClientId(id)).not(element).remove();

            // if the id does NOT contain a ':'
            appendTo.children("[id='" + id + "']").not(element).remove();
        },

        appendDynamicOverlay: function(widget, element, id, appendTo) {
            var elementParent = element.parent();

            // skip when the parent currently is already the same
            // this likely happens when the dialog is updated directly instead of a container
            // as our ajax update mechanism just updates by id
            if (!elementParent.is(appendTo)
                    && !appendTo.is(element)) {

                PrimeFaces.utils.removeDynamicOverlay(widget, element, id, appendTo);

                element.appendTo(appendTo);
            }
        },

        addModal: function(id, zIndex) {
            var modalId = id + '_modal';

            var modalOverlay = $('<div id="' + modalId + '" class="ui-widget-overlay ui-dialog-mask"></div>');
            modalOverlay.appendTo($(document.body));
            modalOverlay.css('z-index' , zIndex);

            return modalOverlay;
        },

        removeModal: function(id) {
            var modalId = id + '_modal';

            // if the id contains a ':'
            $(PrimeFaces.escapeClientId(modalId)).remove();

            // if the id does NOT contain a ':'
            $(document.body).children("[id='" + modalId + "']").remove();
        },

        isModalActive: function(id) {
            var modalId = id + '_modal';

            return $(PrimeFaces.escapeClientId(modalId)).length === 1
                || $(document.body).children("[id='" + modalId + "']").length === 1;
        }

    };

}
/**
 * PrimeFaces OutputPanel Widget
 */
PrimeFaces.widget.OutputPanel = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.loadContent();
    },
            
    loadContent: function() {
        var options = {
            source: this.id,
            process: this.id,
            update: this.id,
            ignoreAutoUpdate: true
        },
        $this = this;

        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id === $this.id) {
                    $this.jq.html(content);
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }

            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        };
        
        options.params = [{name: this.id + '_load', value: true}];
        
        PrimeFaces.ajax.AjaxRequest(options);
    }
});
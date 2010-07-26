PrimeFaces.widget.ProgressBar = function(id, cfg) {
	PrimeFaces.widget.ProgressBar.superclass.constructor.call(this, cfg);
	this.id = id;
	this.cfg = cfg;
	
	if(this.cfg.effect) {
		this.setupAnimation();
	}

	this.render(id);
	
	if(this.cfg.hasCompleteProcess) {
		this.on('complete', this.handleComplete);
	}
}

YAHOO.lang.extend(PrimeFaces.widget.ProgressBar, YAHOO.widget.ProgressBar,
{
	setupAnimation : function() {
		this.set('anim', true);
		var anim = this.get('anim');
		anim.method = this.cfg.effect;
		anim.duration = this.cfg.effectDuration;
	},
	
	start : function() {
		var scope = this;
		
		if(this.isAjax()) {

			this.progressPoll = setInterval(function() {
				var params = {};
				params[PrimeFaces.PARTIAL_PROCESS_PARAM] = scope.id;
				
				PrimeFaces.ajax.AjaxRequest(scope.cfg.actionURL, {
					formId:scope.cfg.formId,
					global:false,
					oncomplete:function(xhr, status, args) {
							var value = args[scope.id + '_value'];
							
							scope.set('value', value);
						}
					},
					params);
			}, this.cfg.interval);
			
		} else {
			
			this.progressPoll = setInterval(function(){
				scope.set('value', scope.get('value') + scope.cfg.step);
			}, scope.cfg.interval);
			
		}
	},
	
	cancel : function() {
		clearInterval(this.progressPoll);
		var scope = this;
		
		if(this.isAjax()) {
			var params = {};
			
			params[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.id;
			params[this.id + '_cancel'] = true;
			
			PrimeFaces.ajax.AjaxRequest(scope.cfg.actionURL, {
				formId:this.cfg.formId,
				global:false,
				oncomplete:function(xhr, status, args) {
						scope.set('value', scope.get('minValue'));
					}
				},
				params);	
			
		} else {
			scope.set('value', scope.get('minValue'));
		}
	},
	
	handleComplete : function(value) {
		if(value == this.get('maxValue')) {
			clearInterval(this.progressPoll);
			
			var completeParams = {};
			completeParams[this.id + '_complete'] = true;
			completeParams[PrimeFaces.PARTIAL_PROCESS_PARAM] = this.id;
			
			if(this.cfg.oncompleteUpdate) {
				completeParams[PrimeFaces.PARTIAL_UPDATE_PARAM] = this.cfg.oncompleteUpdate;
			}

			PrimeFaces.ajax.AjaxRequest(this.cfg.actionURL, {formId:this.cfg.formId, global:false}, completeParams);
		}
	},
	
	isAjax : function() {
		return this.cfg.ajax;
	}
});
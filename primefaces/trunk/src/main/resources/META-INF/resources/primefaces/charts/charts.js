/**
 * PrimeFaces PieChart Widget
 */
PrimeFaces.widget.PieChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = this.id.replace(/:/g,"\\:");

    //renderer options
    var rendererCfg = {
        diameter : this.cfg.diameter,
        sliceMargin : this.cfg.sliceMargin,
        fill: this.cfg.fill
    }

    //renderer configuration
    this.cfg.seriesDefaults = {
        renderer: $.jqplot.PieRenderer,
        rendererOptions: rendererCfg
    };

    //events
    PrimeFaces.widget.ChartUtils.bindItemSelectListener(this);

    //render chart
    this.plot = $.jqplot(this.jqId, this.cfg.data, this.cfg);
}

/**
 * PrimeFaces LineChart Widget
 */
PrimeFaces.widget.LineChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = this.id.replace(/:/g,"\\:");

    //axes
    this.cfg.axes = {
        xaxis:{
            min:this.cfg.minX,
            max:this.cfg.maxX
        },
        yaxis:{
            min:this.cfg.minY,
            max:this.cfg.maxY
        }
    };

    if(this.cfg.categories) {
        this.cfg.axes.xaxis.renderer = $.jqplot.CategoryAxisRenderer;
        this.cfg.axes.xaxis.ticks = this.cfg.categories;
    }

    //events
    PrimeFaces.widget.ChartUtils.bindItemSelectListener(this);
    
    //render chart
    this.plot = $.jqplot(this.jqId, this.cfg.data, this.cfg);
}

/**
 * PrimeFaces LineChart Widget
 */
PrimeFaces.widget.BarChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = this.id.replace(/:/g,"\\:");

    var rendererCfg = {
    	barDirection:this.cfg.orientation,
    	barPadding: this.cfg.barPadding,
    	barMargin: this.cfg.barMargin
    };

    //renderer configuration
    this.cfg.seriesDefaults = {
        renderer: $.jqplot.BarRenderer,
        rendererOptions: rendererCfg
    };

    var categoryAxis = {
        renderer:$.jqplot.CategoryAxisRenderer,
        ticks: this.cfg.categories
    };
    
    //axes
    this.cfg.axes = {};

    if(this.cfg.orientation == 'vertical') {
    	this.cfg.axes.xaxis = categoryAxis;
    }
    else {
    	this.cfg.axes.yaxis = categoryAxis;
    }

    //events
    PrimeFaces.widget.ChartUtils.bindItemSelectListener(this);

    //render chart
    this.plot = $.jqplot(this.jqId, this.cfg.data, this.cfg);
}

PrimeFaces.widget.ChartUtils = {

    bindItemSelectListener : function(chart) {
        $('#' + chart.jqId).bind("jqplotClick", function(ev, gridpos, datapos, neighbor) {
            if(neighbor && chart.cfg.behaviors) {
                var itemSelectCallback = chart.cfg.behaviors['itemSelect'];
                if(itemSelectCallback) {
                    itemSelectCallback.call(chart, {itemIndex:neighbor.pointIndex, seriesIndex:neighbor.seriesIndex});
                }
            }
        });
    }
}
/**
 * PrimeFaces PieChart Widget
 */
PrimeFaces.widget.PieChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = this.id.replace(/:/g,"\\:");
    var o = this.cfg.plotOptions;

    //renderer options
    var rendererCfg = {
        diameter : o.diameter,
        sliceMargin : o.sliceMargin,
        fill: o.fill
    }

    //renderer configuration
    this.cfg.plotOptions.seriesDefaults = {
        renderer: $.jqplot.PieRenderer,
        rendererOptions: rendererCfg
    };

    //events
    PrimeFaces.widget.ChartUtils.bindItemSelectListener(this);

    //highlighter
    if(this.cfg.tooltip)
      PrimeFaces.widget.ChartUtils.bindHighlighter(this);
    this.cfg.plotOptions.highlighter = {show:false}; //default highlighter 

    //render chart
    this.plot = $.jqplot(this.jqId, this.cfg.plotOptions.data, this.cfg.plotOptions);
}

/**
 * PrimeFaces LineChart Widget
 */
PrimeFaces.widget.LineChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = this.id.replace(/:/g,"\\:");
    var o = this.cfg.plotOptions;
    //axes
    this.cfg.plotOptions.axes = {
        xaxis:{
            min:o.minX,
            max:o.maxX
        },
        yaxis:{
            min:o.minY,
            max:o.maxY
        }
    };

    if(o.categories) {
        this.cfg.plotOptions.axes.xaxis.renderer = $.jqplot.CategoryAxisRenderer;
        this.cfg.plotOptions.axes.xaxis.ticks = o.categories;
    }

    if(o.breakOnNull) {
        this.cfg.plotOptions.seriesDefaults = {
            breakOnNull: true
        }
    }

    this.cfg.plotOptions.highlighter = { show : this.cfg.tooltip, formatString : '%s, %s', showTooltip : true};

    //events
    PrimeFaces.widget.ChartUtils.bindItemSelectListener(this);
    
    //render chart
    this.plot = $.jqplot(this.jqId, this.cfg.plotOptions.data, this.cfg.plotOptions);
}

/**
 * PrimeFaces LineChart Widget
 */
PrimeFaces.widget.BarChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = this.id.replace(/:/g,"\\:");
    var o = this.cfg.plotOptions;

    var rendererCfg = {
    	barDirection:o.orientation,
    	barPadding: o.barPadding,
    	barMargin: o.barMargin
    };

    //renderer configuration
    this.cfg.plotOptions.seriesDefaults = {
        renderer: $.jqplot.BarRenderer,
        rendererOptions: rendererCfg
    };

    if(o.breakOnNull) {
        this.cfg.plotOptions.seriesDefaults.breakOnNull = true;
    }

    //axes
    var categoryAxis = {
        renderer:$.jqplot.CategoryAxisRenderer,
        ticks: o.categories
    },
    valueAxis = {
        min: o.min,
        max: o.max
    }

    this.cfg.plotOptions.axes = {};

    if(o.orientation == 'vertical') {
    	this.cfg.plotOptions.axes.xaxis = categoryAxis;
        this.cfg.plotOptions.axes.yaxis = valueAxis;
    }
    else {
    	this.cfg.plotOptions.axes.yaxis = categoryAxis;
        this.cfg.plotOptions.axes.xaxis = valueAxis;
    }

    //events
    PrimeFaces.widget.ChartUtils.bindItemSelectListener(this);
    
    //highlighter
    if(this.cfg.tooltip)
      PrimeFaces.widget.ChartUtils.bindHighlighter(this);
    this.cfg.plotOptions.highlighter = {show:false}; //default highlighter off
    
    //render chart
    this.plot = $.jqplot(this.jqId, this.cfg.plotOptions.data, this.cfg.plotOptions);
}

PrimeFaces.widget.ChartUtils = {

    bindItemSelectListener : function(chart) {
        $('#' + chart.jqId).bind("jqplotClick", function(ev, gridpos, datapos, neighbor) {
            if(neighbor && chart.cfg.behaviors) {
                var itemSelectCallback = chart.cfg.behaviors['itemSelect'];
                if(itemSelectCallback) {
                    var ext = {
                        params: {
                            itemIndex:neighbor.pointIndex, 
                            seriesIndex:neighbor.seriesIndex
                        }
                    };
                    
                    itemSelectCallback.call(chart, ev, ext);
                }
            }
        });
    },
    
    bindHighlighter : function(chart){
      var _self = $('#' + chart.jqId);
      _self.append($('<div class="ui-chart-tooltip" style="position:absolute;display:none;background:#E5DACA;padding:4px; z-index:1000;"></div>').css({opacity : 0.8}));
      var tooltip = _self.find('.ui-chart-tooltip');
      _self.bind('jqplotDataHighlight',
        function (ev, seriesIndex, pointIndex, data) {
          tooltip.html(data[0] + ", " +data[1]).css({display : 'block'});
        }
      ).bind('jqplotDataUnhighlight',
        function (ev, seriesIndex, pointIndex, data) {
          tooltip.css({display : 'none'});
      }).bind('jqplotMouseMove', 
        function(ev, gridpos, datapos, neighbor, plot){
          if (neighbor != null)
            tooltip.css({left:(gridpos.x ), top:(gridpos.y - 5)});
      });
    }
}
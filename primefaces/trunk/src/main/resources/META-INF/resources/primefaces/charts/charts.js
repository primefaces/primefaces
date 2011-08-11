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
        fill: this.cfg.fill,
        shadow : this.cfg.shadow,
        showDataLabels : this.cfg.showDataLabels,
        dataLabels : this.cfg.dataFormat || "percent"
    }

    //renderer configuration
    this.cfg.seriesDefaults = {
        renderer: $.jqplot.PieRenderer,
        rendererOptions: rendererCfg
    };

    //events
    PrimeFaces.widget.ChartUtils.bindItemSelectListener(this);

    //highlighter
    PrimeFaces.widget.ChartUtils.bindHighlighter(this);
    this.cfg.highlighter = {show:false}; //default highlighter 
    
    if(this.cfg.resizable)
        PrimeFaces.widget.ChartUtils.makeResizable(this);

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
    this.cfg.seriesDefaults = {};
    
    //axes
    this.cfg.axes = this.cfg.axes || {};
    this.cfg.axes.xaxis = this.cfg.axes.xaxis || {};
    this.cfg.axes.yaxis = this.cfg.axes.yaxis || {};
    
    this.cfg.axes.xaxis.min = this.cfg.minX;
    this.cfg.axes.xaxis.max = this.cfg.maxX;

    this.cfg.axes.yaxis.min = this.cfg.minY;
    this.cfg.axes.yaxis.max = this.cfg.maxY;

   

    if(this.cfg.categories) {
        this.cfg.axes.xaxis.renderer = $.jqplot.CategoryAxisRenderer;
        this.cfg.axes.xaxis.ticks = this.cfg.categories;
    }

    if(this.cfg.breakOnNull) {
        this.seriesDefaults.breakOnNull =  true;
    }

    if(this.cfg.fillToZero){
        this.cfg.seriesDefaults.fillToZero = true;
        this.cfg.seriesDefaults.fill = true;
    }
    else if(this.cfg.fill){
        this.cfg.seriesDefaults.fill = true;
    }
    
    this.cfg.highlighter = { show : true, formatString : '%s, %s', showTooltip : true};

    //events
    PrimeFaces.widget.ChartUtils.bindItemSelectListener(this);
    
    if(this.cfg.resizable)
        PrimeFaces.widget.ChartUtils.makeResizable(this);
    
    //render chart
    this.plot = $.jqplot(this.jqId, this.cfg.data, this.cfg);
}

/**
 * PrimeFaces BarChart Widget
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

    if(this.cfg.breakOnNull) {
        this.cfg.seriesDefaults.breakOnNull = true;
    }

    //axes
    var categoryAxis = {
        renderer:$.jqplot.CategoryAxisRenderer,
        ticks: this.cfg.categories
    },
    valueAxis = {
        min: this.cfg.min,
        max: this.cfg.max
    }

    this.cfg.axes = this.cfg.axes || {};
    this.cfg.axes.xaxis = this.cfg.axes.xaxis || {};
    this.cfg.axes.yaxis = this.cfg.axes.yaxis || {};
    
    if(this.cfg.orientation == 'vertical') {
    	this.cfg.axes.xaxis.renderer = categoryAxis.renderer;
    	this.cfg.axes.xaxis.ticks = categoryAxis.ticks;
        this.cfg.axes.yaxis.min = valueAxis.min;
        this.cfg.axes.yaxis.max = valueAxis.max;
    }
    else {
    	this.cfg.axes.yaxis.renderer = categoryAxis.renderer;
    	this.cfg.axes.yaxis.ticks = categoryAxis.ticks;
        this.cfg.axes.xaxis.min = valueAxis.min;
        this.cfg.axes.xaxis.max = valueAxis.max;
    }

    //events
    PrimeFaces.widget.ChartUtils.bindItemSelectListener(this);
    
    //highlighter
    PrimeFaces.widget.ChartUtils.bindHighlighter(this);
    this.cfg.highlighter = {show:false}; //default highlighter off
    
    if(this.cfg.resizable)
        PrimeFaces.widget.ChartUtils.makeResizable(this);
    
    //render chart
    this.plot = $.jqplot(this.jqId, this.cfg.data, this.cfg);
}

/**
 * PrimeFaces DonutChart Widget
 */
PrimeFaces.widget.DonutChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = this.id.replace(/:/g,"\\:");

    //renderer options
    var rendererCfg = {
        diameter : this.cfg.diameter,
        sliceMargin : this.cfg.sliceMargin,
        fill: this.cfg.fill,
        shadow : this.cfg.shadow,
        showDataLabels : this.cfg.showDataLabels,
        dataLabels : this.cfg.dataFormat || "percent"
    }

    //renderer configuration
    this.cfg.seriesDefaults = {
        renderer: $.jqplot.DonutRenderer,
        rendererOptions: rendererCfg
    };

    //events
    PrimeFaces.widget.ChartUtils.bindItemSelectListener(this);

    //highlighter
    PrimeFaces.widget.ChartUtils.bindHighlighter(this);
    this.cfg.highlighter = {show:false}; //default highlighter 

    if(this.cfg.resizable)
        PrimeFaces.widget.ChartUtils.makeResizable(this);

    //render chart
    this.plot = $.jqplot(this.jqId, this.cfg.data, this.cfg);
}

/**
 * PrimeFaces BubbleChart Widget
 */
PrimeFaces.widget.BubbleChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = this.id.replace(/:/g,"\\:");

    //default values
    this.cfg.seriesDefaults.rendererOptions.bubbleAlpha = this.cfg.seriesDefaults.rendererOptions.bubbleAlpha || 0.7;
    this.cfg.seriesDefaults.rendererOptions.highlightAlpha = 0.8;
    this.cfg.highlighter = {show:false};
    this.cfg.seriesDefaults.shadow = this.cfg.shadow;
    
    //events
    PrimeFaces.widget.ChartUtils.bindItemSelectListener(this);
    
    if(this.cfg.resizable)
        PrimeFaces.widget.ChartUtils.makeResizable(this);
    
    //render chart
    this.plot = $.jqplot(this.jqId, this.cfg.data, this.cfg);
}

/**
 * PrimeFaces OhlcChart Widget
 */
PrimeFaces.widget.OhlcChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = this.id.replace(/:/g,"\\:");
    
    this.cfg.highlighter = {
          tooltipAxes: 'xy',
          yvalues: 4,
          formatString:'<table class="jqplot-highlighter"> \
          <tr><td>value:</td><td>%s</td></tr> \
          <tr><td>open:</td><td>%s</td></tr> \
          <tr><td>hi:</td><td>%s</td></tr> \
          <tr><td>low:</td><td>%s</td></tr> \
          <tr><td>close:</td><td>%s</td></tr></table>'
   }
   
   if(this.cfg.resizable)
        PrimeFaces.widget.ChartUtils.makeResizable(this);

    //render chart
    this.plot = $.jqplot(this.jqId, this.cfg.data, this.cfg);
}

/**
 * PrimeFaces MeterGaugeChart Widget
 */
PrimeFaces.widget.MeterGaugeChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;
    this.jqId = this.id.replace(/:/g,"\\:");

    if(this.cfg.seriesColors)
        this.cfg.seriesDefaults.rendererOptions.intervalColors = this.cfg.seriesColors;
    
    if(this.cfg.resizable)
        PrimeFaces.widget.ChartUtils.makeResizable(this);
    
    //render chart
    this.plot = $.jqplot(this.jqId, this.cfg.data, this.cfg);
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
    
    makeResizable : function(chart){
        var jq = $(PrimeFaces.escapeClientId(chart.id)),
        rs = $("<div class='ui-widget-content'></div>");
        
        rs.insertBefore(jq);
        jq.appendTo(rs);
        
        rs.resizable({helper:'ui-resizable-helper'}).bind('resizestop', function(event, ui) {
            jq.height(rs.height());
            jq.width(rs.width());
            chart.plot.replot({resetAxes:true});
        });
    },
    
    bindHighlighter : function(chart){
      var _self = chart;
      var panel = $('#' + chart.jqId);
      panel.append($('<div class="ui-chart-tooltip" style="position:absolute;overflow:hidden;white-space:nowrap;display:none;background:#E5DACA;padding:4px; z-index:1000;"></div>').css({opacity : 0.8}));
      var tooltip = panel.find('.ui-chart-tooltip');
      panel.bind('jqplotDataHighlight',
        function (ev, seriesIndex, pointIndex, data) {
          var text = _self.cfg.categories ? _self.cfg.categories[pointIndex] + ' : ' : '';
          text += (_self.cfg.series ? _self.cfg.series[seriesIndex].label + " : " : '') + data[0] + " : " +data[1];
          tooltip.html(text).css({display : 'block'});
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
PrimeFaces.widget.BaseChart = PrimeFaces.widget.DeferredWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.canvas = this.jq.children('canvas');
        this.ctx = this.canvas[0].getContext('2d');
        
        if(this.cfg.extender) {
            this.cfg.extender.call(this);
        }
        
        this.renderDeferred(); 
    },
    
    _render: function() {
        this.chart = new Chart(this.ctx, this.cfg.config); 
        
        this.bindItemSelect();
    },
    
    bindItemSelect: function() {
        var $this = this;
        
        this.canvas.on('click', function(evt){   
            var activePoints = $this.chart.getElementAtEvent(evt);
            if(activePoints.length && $this.cfg.behaviors) {
                var itemSelectCallback = $this.cfg.behaviors['itemSelect'];
                if(itemSelectCallback) {
                    var ext = {
                        params: [
                            {name: 'itemIndex', value: activePoints[0]["_index"]}
                            ,{name: 'dataSetIndex', value: activePoints[0]["_datasetIndex"]}
                        ]
                    };

                    itemSelectCallback.call($this, ext);
                }
            }
        });
    },
    
    /**
     * Return this chart as an <img src="data:url" />
     */
    exportAsImage: function() {
        var img = new Image();
        img.src = this.chart.toBase64Image();
        return img;
    }
});

/**
 * PrimeFaces LineChart Widget
 */
PrimeFaces.widget.LineChart = PrimeFaces.widget.BaseChart.extend({});

/**
 * PrimeFaces BarChart Widget
 */
PrimeFaces.widget.BarChart = PrimeFaces.widget.BaseChart.extend({});

/**
 * PrimeFaces PieChart Widget
 */
PrimeFaces.widget.PieChart = PrimeFaces.widget.BaseChart.extend({});

/**
 * PrimeFaces DonutChart Widget
 */
PrimeFaces.widget.DonutChart = PrimeFaces.widget.BaseChart.extend({});

/**
 * PrimeFaces PolarAreaChart Widget
 */
PrimeFaces.widget.PolarAreaChart = PrimeFaces.widget.BaseChart.extend({});

/**
 * PrimeFaces RadarChart Widget
 */
PrimeFaces.widget.RadarChart = PrimeFaces.widget.BaseChart.extend({});

/**
 * PrimeFaces BubbleChart Widget
 */
PrimeFaces.widget.BubbleChart = PrimeFaces.widget.BaseChart.extend({});

/**
 * PrimeFaces ScatterChart Widget
 */
PrimeFaces.widget.ScatterChart = PrimeFaces.widget.BaseChart.extend({});
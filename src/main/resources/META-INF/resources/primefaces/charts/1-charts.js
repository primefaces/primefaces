
/**
 * Configurators for specific chart types
 */
PrimeFaces.widget.ChartUtils = {

    CONFIGURATORS: {

        pie: {

            configure: function(chart) {
                chart.cfg.seriesDefaults = {
                    shadow : chart.cfg.shadow,
                    renderer: $.jqplot.PieRenderer,
                    rendererOptions: {
                        fill: chart.cfg.fill,
                        diameter : chart.cfg.diameter,
                        sliceMargin : chart.cfg.sliceMargin,
                        showDataLabels : chart.cfg.showDataLabels,
                        dataLabels : chart.cfg.dataFormat||'percent',
                        highlightMouseOver: chart.cfg.highlightMouseOver,
                        dataLabelFormatString: chart.cfg.dataLabelFormatString,
                        dataLabelThreshold: chart.cfg.dataLabelThreshold||3
                    }
                };

                if(chart.cfg.datatip) {
                    chart.cfg.highlighter.useAxesFormatters = false;
                }
            }

        },

        line: {

            configure: function(chart) {
                chart.cfg.axesDefaults = {
                    labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
                    tickRenderer: $.jqplot.CanvasAxisTickRenderer,
                    tickOptions: {enableFontSupport: true}
                };

                chart.cfg.seriesDefaults = {
                    shadow: chart.cfg.shadow,
                    breakOnNull: chart.cfg.breakOnNull,
                    pointLabels: {
                        show: chart.cfg.showPointLabels ? true: false
                    },
                    rendererOptions: {
                        highlightMouseOver: chart.cfg.highlightMouseOver
                    }
                };

                if(chart.cfg.stackSeries && chart.cfg.axes.xaxis.renderer !== $.jqplot.DateAxisRenderer) {
                    PrimeFaces.widget.ChartUtils.transformStackedData(chart);
                }
            }

        },

        bar: {

            configure: function(chart) {
                chart.cfg.axesDefaults = {
                    labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
                    tickRenderer: $.jqplot.CanvasAxisTickRenderer,
                    tickOptions: {enableFontSupport: true}
                };

                chart.cfg.seriesDefaults = {
                    shadow : chart.cfg.shadow,
                    renderer: $.jqplot.BarRenderer,
                    rendererOptions: {
                        barDirection: chart.cfg.orientation,
                        barPadding: chart.cfg.barPadding,
                        barMargin: chart.cfg.barMargin,
                        barWidth: chart.cfg.barWidth,
                        highlightMouseOver: chart.cfg.highlightMouseOver
                    },
                    fillToZero: true,
                    pointLabels: {
                        show: chart.cfg.showPointLabels ? true: false
                    }
                };

                if(chart.cfg.orientation === 'horizontal') {
                    chart.cfg.axes.yaxis.ticks = chart.cfg.ticks;
                }
                else {
                    if(chart.cfg.axes.xaxis.renderer === $.jqplot.DateAxisRenderer) {
                        PrimeFaces.widget.ChartUtils.transformDateData(chart);
                    }
                    else {
                        chart.cfg.axes.xaxis.ticks = chart.cfg.ticks;
                    }

                    if(chart.cfg.dataRenderMode === 'key') {
                        chart.cfg.data = [chart.cfg.data];
                        chart.cfg.axes = {
                            xaxis: {
                               renderer: $.jqplot.CategoryAxisRenderer
                            }
                        };
                    }
                }
            }

        },

        ohlc: {
            configure: function(chart) {
                chart.cfg.axesDefaults = {
                    labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
                    tickRenderer: $.jqplot.CanvasAxisTickRenderer,
                    tickOptions: {enableFontSupport: true}
                };

                chart.cfg.seriesDefaults = {
                    shadow : chart.cfg.shadow,
                    renderer: $.jqplot.OHLCRenderer,
                    rendererOptions: {
                        candleStick: chart.cfg.candleStick,
                        highlightMouseOver: chart.cfg.highlightMouseOver
                    }
                };
            }
        },

        donut: {

            configure: function(chart) {
                chart.cfg.seriesDefaults = {
                    shadow : chart.cfg.shadow,
                    renderer: $.jqplot.DonutRenderer,
                    rendererOptions: {
                        fill: chart.cfg.fill,
                        diameter : chart.cfg.diameter,
                        sliceMargin : chart.cfg.sliceMargin,
                        showDataLabels : chart.cfg.showDataLabels,
                        dataLabels : chart.cfg.dataFormat||'percent',
                        highlightMouseOver: chart.cfg.highlightMouseOver,
                        dataLabelFormatString: chart.cfg.dataLabelFormatString,
                        dataLabelThreshold: chart.cfg.dataLabelThreshold||3
                    }
                };

                if(chart.cfg.datatip) {
                    chart.cfg.highlighter.useAxesFormatters = false;
                }
            }

        },

        bubble: {

            configure: function(chart) {
                chart.cfg.axesDefaults = {
                    labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
                    tickRenderer: $.jqplot.CanvasAxisTickRenderer,
                    tickOptions: {enableFontSupport: true}
                };

                chart.cfg.seriesDefaults = {
                    shadow : chart.cfg.shadow,
                    renderer: $.jqplot.BubbleRenderer,
                    rendererOptions: {
                        showLabels: chart.cfg.showLabels,
                        bubbleGradients: chart.cfg.bubbleGradients,
                        bubbleAlpha: chart.cfg.bubbleAlpha,
                        highlightMouseOver: chart.cfg.highlightMouseOver
                    }
                };
            }
        },

        metergauge: {

            configure: function(chart) {
                chart.cfg.seriesDefaults = {
                    shadow : chart.cfg.shadow,
                    renderer: $.jqplot.MeterGaugeRenderer,
                    rendererOptions: {
                        intervals: chart.cfg.intervals,
                        intervalColors: chart.cfg.seriesColors,
                        label: chart.cfg.gaugeLabel,
                        labelPosition: chart.cfg.gaugeLabelPosition,
                        showTickLabels: chart.cfg.showTickLabels,
                        ticks: chart.cfg.ticks,
                        labelHeightAdjust: chart.cfg.labelHeightAdjust,
                        intervalInnerRadius: chart.cfg.intervalInnerRadius,
                        intervalOuterRadius: chart.cfg.intervalOuterRadius,
                        min: chart.cfg.min,
                        max: chart.cfg.max
                    }
                };

                chart.cfg.replotMode = 'reinit';
            }
        }
    },

    transformStackedData: function(chart) {
        var data = chart.cfg.data,
        ticks = [];

        for(var i = 0; i < data.length; i++) {
            var series = data[i];
            for(var j = 0; j < series.length; j++) {
                ticks[j] = series[j][0];
                series[j] = series[j][1];
            }
        }

        chart.cfg.axes.xaxis.ticks = ticks;
    },

    transformDateData: function(chart) {
        var data = chart.cfg.data,
        ticks = chart.cfg.ticks;

        for(var i = 0; i < data.length; i++) {
            for(var j = 0; j < data[i].length; j++) {
                var newData = new Array(2);
                newData[0] = ticks[j];
                newData[1] = data[i][j];
                data[i][j] = newData;
            }
        }

        chart.cfg.data = data;
    }
};

/**
 * PrimeFaces Chart Widget
 */
PrimeFaces.widget.Chart = PrimeFaces.widget.DeferredWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        this.jqpid = this.id.replace(/:/g,"\\:");

        this.configure();

        if(this.cfg.extender) {
            this.cfg.extender.call(this);
        }

        this.renderDeferred();
    },

    //@Override
    refresh: function(cfg) {
        if(this.plot) {
            this.plot.destroy();
        }

        this._super(cfg);
    },

    //@Override
    destroy: function() {
        this._super();

        if (this.plot) {
            this.plot.destroy();
        }
    },

    //@Override
    _render: function() {
        this._draw();
    },

    _draw: function() {
        this.bindItemSelect();
        this.plot = $.jqplot(this.jqpid, this.cfg.data, this.cfg);
        this.adjustLegendTable();

        if(this.cfg.responsive) {
            this.makeResponsive();
        }
    },

    makeResponsive: function() {
        var $this = this;

        this.cfg.resetAxesOnResize = (this.cfg.resetAxesOnResize === false) ? false : true;

        PrimeFaces.utils.registerResizeHandler(this, 'resize.' + this.id + '_align', $this.jq, function() {
            var replotOptions = {
                resetAxes: $this.cfg.resetAxesOnResize
            };

            if($this.cfg.replotMode) {
                replotOptions.replotMode = $this.cfg.replotMode;
            }

            $this.plot.replot(replotOptions);
        });
    },

    adjustLegendTable: function() {
        var tableLegend = this.jq.find('table.jqplot-table-legend'),
            tr = tableLegend.find('tr.jqplot-table-legend');

        if(tr.length > 1) {
            var trFirst = tableLegend.find('tr.jqplot-table-legend:first'),
                trLast = tableLegend.find('tr.jqplot-table-legend:last'),
                length = trFirst.children('td').length - trLast.children('td').length;

            for(var i = 0; i < length; i++) {
                trLast.append('<td></td>');
            }
        }
    },

    configure: function() {
        //legend config
        if(this.cfg.legendPosition) {
            this.cfg.legend = {
                renderer: this.cfg.type === 'pie' ? $.jqplot.EnhancedPieLegendRenderer : $.jqplot.EnhancedLegendRenderer,
                show: true,
                escapeHtml: this.cfg.escapeHtml,
                location: this.cfg.legendPosition,
                placement: this.cfg.legendPlacement,
                rendererOptions: {
                    numberRows: this.cfg.legendRows||0,
                    numberColumns: this.cfg.legendCols||0
                }
            };
        }

        //zoom
        if(this.cfg.zoom) {
            this.cfg.cursor = {
                show: true,
                zoom: true,
                showTooltip: false
            };
        }
        else {
            this.cfg.cursor = {
                show: false
            };
        }

        //highlighter
        if(this.cfg.datatip) {
            this.cfg.highlighter = {
                show: true,
                formatString: this.cfg.datatipFormat,
                tooltipContentEditor: this.cfg.datatipEditor
            };
        }
        else {
            this.cfg.highlighter = {
                show: false
            };
        }

        PrimeFaces.widget.ChartUtils.CONFIGURATORS[this.cfg.type].configure(this);
    },

    exportAsImage: function() {
        return this.jq.jqplotToImageElem();
    },

    bindItemSelect: function() {
        var $this = this;

        $(this.jqId).bind("jqplotClick", function(ev, gridpos, datapos, neighbor) {
            if(neighbor && $this.hasBehavior('itemSelect')) {
                var ext = {
                    params: [
                        {name: 'itemIndex', value: neighbor.pointIndex}
                        ,{name: 'seriesIndex', value: neighbor.seriesIndex}
                    ]
                };

                $this.callBehavior('itemSelect', ext);
            }
        });
    },

    resetZoom: function() {
        this.plot.resetZoom();
    }
});

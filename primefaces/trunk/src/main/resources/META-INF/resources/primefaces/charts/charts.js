PrimeFaces.widget.ChartExtensions = {

    itemSelectHandler : function(event) {
		var options = {
            source: this.id,
            process: this.id,
            formId: this.cfg.id
        };

        if(this.cfg.update) {
            options.update = this.cfg.update;
        }

        if(this.cfg.oncomplete) {
            options.oncomplete = this.cfg.oncomplete;
        }

        var params = {};
        params[this.id + '_ajaxItemSelect'] = true;
        params[this.id + '_itemIndex'] = event.index;
		params[this.id + '_seriesIndex'] = event.seriesIndex;

		PrimeFaces.ajax.AjaxRequest(this.cfg.url, options, params);
	}

    ,createLocalDataSource : function(data, schema) {
        var datasource = new YAHOO.util.DataSource(data);
        datasource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
        datasource.responseSchema = {
            fields: schema
        };

        return datasource;
    }

    ,setupAxises : function(xAxis, yAxis) {
        this.cfg.xAxis = xAxis;
        this.cfg.yAxis = yAxis;

        if(this.cfg.minX) this.cfg.xAxis.minimum = this.cfg.minX;
        if(this.cfg.maxX) this.cfg.xAxis.maximum = this.cfg.maxX;
        if(this.cfg.minY) this.cfg.yAxis.minimum = this.cfg.minY;
        if(this.cfg.maxY) this.cfg.yAxis.maximum = this.cfg.maxY;
        if(this.cfg.titleX) this.cfg.xAxis.title = this.cfg.titleX;
        if(this.cfg.titleY) this.cfg.yAxis.title = this.cfg.titleY;
        if(this.cfg.labelFunctionX) this.cfg.xAxis.labelFunction = this.cfg.labelFunctionX;
        if(this.cfg.labelFunctionY) this.cfg.yAxis.labelFunction = this.cfg.labelFunctionY;
    }
};

PrimeFaces.widget.PieChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;

    var datasource = this.createLocalDataSource(this.cfg.data, [this.cfg.categoryField, this.cfg.dataField]);
    
    PrimeFaces.widget.PieChart.superclass.constructor.call(this, this.id, datasource, this.cfg);

    if(this.cfg.ajaxItemSelect) {
        this.subscribe('itemClickEvent', this.itemSelectHandler, this, true);
    }
}

PrimeFaces.widget.LineChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;

    var datasource = this.createLocalDataSource(this.cfg.data, this.cfg.fields);

    this.setupAxises(new YAHOO.widget.CategoryAxis(), new YAHOO.widget.NumericAxis());

    PrimeFaces.widget.LineChart.superclass.constructor.call(this, this.id, datasource, this.cfg);

    if(this.cfg.ajaxItemSelect) {
        this.subscribe('itemClickEvent', this.itemSelectHandler, this, true);
    }
}

PrimeFaces.widget.ColumnChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;

    var datasource = this.createLocalDataSource(this.cfg.data, this.cfg.fields);

    this.setupAxises(new YAHOO.widget.CategoryAxis(), new YAHOO.widget.NumericAxis());

    PrimeFaces.widget.ColumnChart.superclass.constructor.call(this, this.id, datasource, this.cfg);

    if(this.cfg.ajaxItemSelect) {
        this.subscribe('itemClickEvent', this.itemSelectHandler, this, true);
    }
}

PrimeFaces.widget.StackedColumnChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;

    var datasource = this.createLocalDataSource(this.cfg.data, this.cfg.fields);

    var numericAxis = new YAHOO.widget.NumericAxis();
    numericAxis.stackingEnabled = true;
    this.setupAxises(new YAHOO.widget.CategoryAxis(), numericAxis);

    PrimeFaces.widget.StackedColumnChart.superclass.constructor.call(this, this.id, datasource, this.cfg);

    if(this.cfg.ajaxItemSelect) {
        this.subscribe('itemClickEvent', this.itemSelectHandler, this, true);
    }
}

PrimeFaces.widget.BarChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;

    var datasource = this.createLocalDataSource(this.cfg.data, this.cfg.fields);
    
    this.setupAxises(new YAHOO.widget.NumericAxis(), new YAHOO.widget.CategoryAxis());

    PrimeFaces.widget.BarChart.superclass.constructor.call(this, this.id, datasource, this.cfg);

    if(this.cfg.ajaxItemSelect) {
        this.subscribe('itemClickEvent', this.itemSelectHandler, this, true);
    }
}

PrimeFaces.widget.StackedBarChart = function(id, cfg) {
    this.id = id;
    this.cfg = cfg;

    var datasource = this.createLocalDataSource(this.cfg.data, this.cfg.fields);

    var numericAxis = new YAHOO.widget.NumericAxis();
    numericAxis.stackingEnabled = true;
    this.setupAxises(numericAxis, new YAHOO.widget.CategoryAxis());

    PrimeFaces.widget.StackedBarChart.superclass.constructor.call(this, this.id, datasource, this.cfg);

    if(this.cfg.ajaxItemSelect) {
        this.subscribe('itemClickEvent', this.itemSelectHandler, this, true);
    }
}

YAHOO.lang.extend(PrimeFaces.widget.PieChart, YAHOO.widget.PieChart, PrimeFaces.widget.ChartExtensions);

YAHOO.lang.extend(PrimeFaces.widget.LineChart, YAHOO.widget.LineChart, PrimeFaces.widget.ChartExtensions);

YAHOO.lang.extend(PrimeFaces.widget.ColumnChart, YAHOO.widget.ColumnChart, PrimeFaces.widget.ChartExtensions);

YAHOO.lang.extend(PrimeFaces.widget.StackedColumnChart, YAHOO.widget.StackedColumnChart, PrimeFaces.widget.ChartExtensions);

YAHOO.lang.extend(PrimeFaces.widget.BarChart, YAHOO.widget.BarChart, PrimeFaces.widget.ChartExtensions);

YAHOO.lang.extend(PrimeFaces.widget.StackedBarChart, YAHOO.widget.StackedBarChart, PrimeFaces.widget.ChartExtensions);
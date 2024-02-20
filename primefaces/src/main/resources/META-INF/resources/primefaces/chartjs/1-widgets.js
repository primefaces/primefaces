/**
 * __PrimeFaces LineChart Widget__
 *
 * A line chart is a way of plotting data points on a line. Often, it is used to show trend data, or the comparison of
 * two data sets.
 *
 * @interface {PrimeFaces.widget.LineChartCfg} cfg The configuration for the {@link  LineChart| LineChart widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.ChartCfg} cfg
 */
PrimeFaces.widget.LineChart = PrimeFaces.widget.Chart.extend({});

/**
 * __PrimeFaces BarChart Widget__
 *
 * A bar chart provides a way of showing data values represented as vertical
 * bars. It is sometimes used to show trend data, and the comparison of multiple
 * data sets side by side.
 *
 * @interface {PrimeFaces.widget.BarChartCfg} cfg The configuration for the {@link  BarChart| BarChart widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.ChartCfg} cfg
 */
PrimeFaces.widget.BarChart = PrimeFaces.widget.Chart.extend({});

/**
 * __PrimeFaces PieChart Widget__
 *
 * Pie chart is divided into segments, the arc of each segment shows the proportional value of each piece of data.
 *
 * @interface {PrimeFaces.widget.PieChartCfg} cfg The configuration for the {@link  PieChart| PieChart widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.ChartCfg} cfg
 */
PrimeFaces.widget.PieChart = PrimeFaces.widget.Chart.extend({});

/**
 * __PrimeFaces DonutChart Widget__
 *
 * A Donut Chart is a variation of a Pie Chart but with a space in the center.
 *
 * @interface {PrimeFaces.widget.DonutChartCfg} cfg The configuration for the {@link  DonutChart| DonutChart widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.ChartCfg} cfg
 */
PrimeFaces.widget.DonutChart = PrimeFaces.widget.Chart.extend({});

/**
 * __PrimeFaces PolarAreaChart Widget__
 *
 * Polar area charts are similar to pie charts, but each segment has the same angle - the radius of the segment differs
 * depending on the value.
 *
 * @interface {PrimeFaces.widget.PolarAreaChartCfg} cfg The configuration for the
 * {@link  PolarAreaChart| PolarAreaChart widget}. You can access this configuration via
 * {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this configuration is usually meant to be
 * read-only and should not be modified.
 * @extends {PrimeFaces.widget.ChartCfg} cfg
 */
PrimeFaces.widget.PolarAreaChart = PrimeFaces.widget.Chart.extend({});

/**
 * __PrimeFaces RadarChart Widget__
 *
 * A radar chart is a way of showing multiple data points and the variation between them.
 *
 * @interface {PrimeFaces.widget.RadarChartCfg} cfg The configuration for the {@link  RadarChart| RadarChart widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.ChartCfg} cfg
 */
PrimeFaces.widget.RadarChart = PrimeFaces.widget.Chart.extend({});

/**
 * __PrimeFaces BubbleChart Widget__
 *
 * A bubble chart is used to display three dimensions of data at the same time. The location of the bubble is determined
 * by the first two dimensions and the corresponding horizontal and vertical axes. The third dimension is represented by
 * the size of the individual bubbles.
 *
 * @interface {PrimeFaces.widget.BubbleChartCfg} cfg The configuration for the {@link  BubbleChart| BubbleChart widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.ChartCfg} cfg
 */
PrimeFaces.widget.BubbleChart = PrimeFaces.widget.Chart.extend({});

/**
 * __PrimeFaces ScatterChart Widget__
 *
 * @interface {PrimeFaces.widget.ScatterChartCfg} cfg The configuration for the
 * {@link  ScatterChart| ScatterChart widget}. You can access this configuration via
 * {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this configuration is usually meant to be
 * read-only and should not be modified.
 * @extends {PrimeFaces.widget.ChartCfg} cfg
 */
PrimeFaces.widget.ScatterChart = PrimeFaces.widget.Chart.extend({});
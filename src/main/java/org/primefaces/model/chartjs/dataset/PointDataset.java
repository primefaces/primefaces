package org.primefaces.model.chartjs.dataset;

import org.primefaces.model.chartjs.color.Color;
import org.primefaces.model.chartjs.enums.BorderCapStyle;
import org.primefaces.model.chartjs.enums.BorderJoinStyle;
import org.primefaces.model.chartjs.enums.PointStyle;
import org.primefaces.model.chartjs.objects.OptionalArray;

import java.util.ArrayList;
import java.util.List;

public abstract class PointDataset<T extends Dataset<T, O>, O> extends Dataset<T, O> {

	/**
	 * @see #setLabel(String)
	 */
	private String label;

	/**
	 * @see #setFill(Boolean)
	 */
	private Boolean fill;

	/**
	 * @see #setLineTension(Float)
	 */
	private Float lineTension;

	/**
	 * @see #setBackgroundColor(Color)
	 */
	private Color backgroundColor;

	/**
	 * @see #setBorderWidth(Integer)
	 */
	private Integer borderWidth;

	/**
	 * @see #setBorderColor(Color)
	 */
	private Color borderColor;

	/**
	 * @see #setBorderCapStyle(BorderCapStyle)
	 */
	private BorderCapStyle borderCapStyle;

	/**
	 * @see #setBorderDash(List)
	 */
	private final List<Integer> borderDash = new ArrayList<Integer>();

	/**
	 * @see #setBorderDashOffset(Float)
	 */
	private Float borderDashOffset;

	/**
	 * @see #setBorderJoinStyle(BorderJoinStyle)
	 */
	private BorderJoinStyle borderJoinStyle;

	/**
	 * @see #setPointBorderColor(List)
	 */
	private final List<Color> pointBorderColor = new OptionalArray<Color>();

	/**
	 * @see #setPointBackgroundColor(List)
	 */
	private final List<Color> pointBackgroundColor = new OptionalArray<Color>();

	/**
	 * @see #setPointBorderWidth(List)
	 */
	private final List<Integer> pointBorderWidth = new OptionalArray<Integer>();

	/**
	 * @see #setPointRadius(List)
	 */
	private final List<Integer> pointRadius = new OptionalArray<Integer>();

	/**
	 * @see #setPointHoverRadius(List)
	 */
	private final List<Integer> pointHoverRadius = new OptionalArray<Integer>();

	/**
	 * @see #setPointHitRadius(List)
	 */
	private final List<Integer> pointHitRadius = new OptionalArray<Integer>();

	/**
	 * @see #setPointHoverBackgroundColor(List)
	 */
	private final List<Color> pointHoverBackgroundColor = new OptionalArray<Color>();

	/**
	 * @see #setPointHoverBorderColor(List)
	 */
	private final List<Color> pointHoverBorderColor = new OptionalArray<Color>();

	/**
	 * @see #setPointHoverBorderWidth(List)
	 */
	private final List<Integer> pointHoverBorderWidth = new OptionalArray<Integer>();

	/**
	 * @see #setPointStyle(List)
	 */
	private final List<PointStyle> pointStyle = new OptionalArray<PointStyle>();

	/**
	 * @see #setLabel(String)
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * The label for the dataset which appears in the legend and tooltips
	 */
	@SuppressWarnings("unchecked")
	public T setLabel(String label) {
		this.label = label;
		return (T) this;
	}

	/**
	 * @see #setFill(Boolean)
	 */
	public Boolean getFill() {
		return fill;
	}

	/**
	 * If true, fill the area under the line
	 */
	@SuppressWarnings("unchecked")
	public T setFill(Boolean fill) {
		this.fill = fill;
		return (T) this;
	}

	/**
	 * @see #setLineTension(Float)
	 */
	public Float getLineTension() {
		return lineTension;
	}

	/**
	 * Bezier curve tension of the line. Set to 0 to draw straightlines.
	 */
	@SuppressWarnings("unchecked")
	public T setLineTension(Float lineTension) {
		this.lineTension = lineTension;
		return (T) this;
	}

	/**
	 * @see #setBackgroundColor(Color)
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * The fill color under the line.
	 */
	@SuppressWarnings("unchecked")
	public T setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return (T) this;
	}

	/**
	 * @see #setBorderWidth(Integer)
	 */
	public Integer getBorderWidth() {
		return borderWidth;
	}

	/**
	 * The width of the line in pixels
	 */
	@SuppressWarnings("unchecked")
	public T setBorderWidth(Integer borderWidth) {
		this.borderWidth = borderWidth;
		return (T) this;
	}

	/**
	 * @see #setBorderColor(Color)
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * The color of the line.
	 */
	@SuppressWarnings("unchecked")
	public T setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		return (T) this;
	}

	/**
	 * @see #setBorderCapStyle(BorderCapStyle)
	 */
	public BorderCapStyle getBorderCapStyle() {
		return borderCapStyle;
	}

	/**
	 * Default line cap style.
	 * <ul>
	 * <li>{@code butt} The ends of lines are squared off at the endpoints.
	 * <li>{@code round} The ends of lines are rounded.
	 * <li>{@code square} The ends of lines are squared off by adding a box with
	 * an equal width and half the height of the line's thickness.
	 * </ul>
	 */
	@SuppressWarnings("unchecked")
	public T setBorderCapStyle(BorderCapStyle borderCapStyle) {
		this.borderCapStyle = borderCapStyle;
		return (T) this;
	}

	/**
	 * @see #setBorderDash(List)
	 */
	public List<Integer> getBorderDash() {
		return borderDash;
	}

	/**
	 * @see #setBorderDash(List)
	 */
	@SuppressWarnings("unchecked")
	public T addBorderDash(Integer borderDash) {
		this.borderDash.add(borderDash);
		return (T) this;
	}

	/**
	 * Default line dash. A list of numbers that specifies distances to
	 * alternately draw a line and a gap (in coordinate space units). If the
	 * number of elements in the array is odd, the elements of the array get
	 * copied and concatenated. For example, [5, 15, 25] will become [5, 15, 25,
	 * 5, 15, 25].
	 */
	@SuppressWarnings("unchecked")
	public T setBorderDash(List<Integer> borderDash) {
		this.borderDash.clear();
		if (borderDash != null) {
			this.borderDash.addAll(borderDash);
		}
		return (T) this;
	}

	/**
	 * @see #setBorderDashOffset(Float)
	 */
	public Float getBorderDashOffset() {
		return borderDashOffset;
	}

	/**
	 * Default line dash offset. A float specifying the amount of the offset.
	 * Initially 0.0.
	 */
	@SuppressWarnings("unchecked")
	public T setBorderDashOffset(Float borderDashOffset) {
		this.borderDashOffset = borderDashOffset;
		return (T) this;
	}

	/**
	 * @see #setBorderJoinStyle(BorderJoinStyle)
	 */
	public BorderJoinStyle getBorderJoinStyle() {
		return borderJoinStyle;
	}

	/**
	 * <p>
	 * Default line join style.
	 * </p>
	 * <ul>
	 * <li>{@code round} Rounds off the corners of a shape by filling an
	 * additional sector of disc centered at the common endpoint of connected
	 * segments. The radius for these rounded corners is equal to the line
	 * width.
	 * <li>{@code bevel} Fills an additional triangular area between the common
	 * endpoint of connected segments, and the separate outside rectangular
	 * corners of each segment.
	 * <li>{@code miter} Connected segments are joined by extending their
	 * outside edges to connect at a single point, with the effect of filling an
	 * additional lozenge-shaped area. This setting is effected by the
	 * miterLimit property.
	 * </ul>
	 */
	@SuppressWarnings("unchecked")
	public T setBorderJoinStyle(BorderJoinStyle borderJoinStyle) {
		this.borderJoinStyle = borderJoinStyle;
		return (T) this;
	}

	/**
	 * @see #setPointBorderColor(List)
	 */
	public List<Color> getPointBorderColor() {
		return pointBorderColor;
	}

	/**
	 * @see #setPointBorderColor(List)
	 */
	@SuppressWarnings("unchecked")
	public T addPointBorderColor(Color pointBorderColor) {
		this.pointBorderColor.add(pointBorderColor);
		return (T) this;
	}

	/**
	 * The border color for points.
	 */
	@SuppressWarnings("unchecked")
	public T setPointBorderColor(List<Color> pointBorderColor) {
		this.pointBorderColor.clear();
		if (pointBorderColor != null) {
			this.pointBorderColor.addAll(pointBorderColor);
		}
		return (T) this;
	}

	/**
	 * @see #setPointBackgroundColor(List)
	 */
	public List<Color> getPointBackgroundColor() {
		return pointBackgroundColor;
	}

	/**
	 * @see #setPointBackgroundColor(List)
	 */
	@SuppressWarnings("unchecked")
	public T addPointBackgroundColor(Color pointBackgroundColor) {
		this.pointBackgroundColor.add(pointBackgroundColor);
		return (T) this;
	}

	/**
	 * The fill color for points
	 */
	@SuppressWarnings("unchecked")
	public T setPointBackgroundColor(List<Color> pointBackgroundColor) {
		this.pointBackgroundColor.clear();
		if (pointBackgroundColor != null) {
			this.pointBackgroundColor.addAll(pointBackgroundColor);
		}
		return (T) this;
	}

	/**
	 * @see #setPointBorderWidth(List)
	 */
	public List<Integer> getPointBorderWidth() {
		return pointBorderWidth;
	}

	/**
	 * @see #setPointBorderWidth(List)
	 */
	@SuppressWarnings("unchecked")
	public T addPointBorderWidth(Integer pointBorderWidth) {
		this.pointBorderWidth.add(pointBorderWidth);
		return (T) this;
	}

	/**
	 * The width of the point border in pixels
	 */
	@SuppressWarnings("unchecked")
	public T setPointBorderWidth(List<Integer> pointBorderWidth) {
		this.pointBorderWidth.clear();
		if (pointBorderWidth != null) {
			this.pointBorderWidth.addAll(pointBorderWidth);
		}
		return (T) this;
	}

	/**
	 * @see #setPointRadius(List)
	 */
	public List<Integer> getPointRadius() {
		return pointRadius;
	}

	/**
	 * @see #setPointRadius(List)
	 */
	@SuppressWarnings("unchecked")
	public T addPointRadius(Integer pointRadius) {
		this.pointRadius.add(pointRadius);
		return (T) this;
	}

	/**
	 * The radius of the point shape. If set to 0, nothing is rendered.
	 */
	@SuppressWarnings("unchecked")
	public T setPointRadius(List<Integer> pointRadius) {
		this.pointRadius.clear();
		if (pointRadius != null) {
			this.pointRadius.addAll(pointRadius);
		}
		return (T) this;
	}

	/**
	 * @see #setPointHoverRadius(List)
	 */
	public List<Integer> getPointHoverRadius() {
		return pointHoverRadius;
	}

	/**
	 * @see #setPointHoverRadius(List)
	 */
	@SuppressWarnings("unchecked")
	public T addPointHoverRadius(Integer pointHoverRadius) {
		this.pointHoverRadius.add(pointHoverRadius);
		return (T) this;
	}

	/**
	 * The radius of the point when hovered
	 */
	@SuppressWarnings("unchecked")
	public T setPointHoverRadius(List<Integer> pointHoverRadius) {
		this.pointHoverRadius.clear();
		if (pointHoverRadius != null) {
			this.pointHoverRadius.addAll(pointHoverRadius);
		}
		return (T) this;
	}

	/**
	 * @see #setPointHitRadius(List)
	 */
	public List<Integer> getPointHitRadius() {
		return pointHitRadius;
	}

	/**
	 * @see #setPointHitRadius(List)
	 */
	@SuppressWarnings("unchecked")
	public T addPointHitRadius(Integer pointHitRadius) {
		this.pointHitRadius.add(pointHitRadius);
		return (T) this;
	}

	/**
	 * The pixel size of the non-displayed point that reacts to mouse events
	 */
	@SuppressWarnings("unchecked")
	public T setPointHitRadius(List<Integer> pointHitRadius) {
		this.pointHitRadius.clear();
		if (pointHitRadius != null) {
			this.pointHitRadius.addAll(pointHitRadius);
		}
		return (T) this;
	}

	/**
	 * @see #setPointHoverBackgroundColor(List)
	 */
	public List<Color> getPointHoverBackgroundColor() {
		return pointHoverBackgroundColor;
	}

	/**
	 * @see #setPointHoverBackgroundColor(List)
	 */
	@SuppressWarnings("unchecked")
	public T addPointHoverBackgroundColor(Color pointHoverBackgroundColor) {
		this.pointHoverBackgroundColor.add(pointHoverBackgroundColor);
		return (T) this;
	}

	/**
	 * Point background color when hovered
	 */
	@SuppressWarnings("unchecked")
	public T setPointHoverBackgroundColor(List<Color> pointHoverBackgroundColor) {
		this.pointHoverBackgroundColor.clear();
		if (pointHoverBackgroundColor != null) {
			this.pointHoverBackgroundColor.addAll(pointHoverBackgroundColor);
		}
		return (T) this;
	}

	/**
	 * @see #setPointHoverBorderColor(List)
	 */
	public List<Color> getPointHoverBorderColor() {
		return pointHoverBorderColor;
	}

	/**
	 * @see #setPointHoverBorderColor(List)
	 */
	@SuppressWarnings("unchecked")
	public T addPointHoverBorderColor(Color pointHoverBorderColor) {
		this.pointHoverBorderColor.add(pointHoverBorderColor);
		return (T) this;
	}

	/**
	 * Point border color when hovered
	 */
	@SuppressWarnings("unchecked")
	public T setPointHoverBorderColor(List<Color> pointHoverBorderColor) {
		this.pointHoverBorderColor.clear();
		if (pointHoverBorderColor != null) {
			this.pointHoverBorderColor.addAll(pointHoverBorderColor);
		}
		return (T) this;
	}

	/**
	 * @see #setPointHoverBorderWidth(List)
	 */
	public List<Integer> getPointHoverBorderWidth() {
		return pointHoverBorderWidth;
	}

	/**
	 * @see #setPointHoverBorderWidth(List)
	 */
	@SuppressWarnings("unchecked")
	public T addPointHoverBorderWidth(Integer pointHoverBorderWidth) {
		this.pointHoverBorderWidth.add(pointHoverBorderWidth);
		return (T) this;
	}

	/**
	 * Border width of point when hovered
	 */
	@SuppressWarnings("unchecked")
	public T setPointHoverBorderWidth(List<Integer> pointHoverBorderWidth) {
		this.pointHoverBorderWidth.clear();
		if (pointHoverBorderWidth != null) {
			this.pointHoverBorderWidth.addAll(pointHoverBorderWidth);
		}
		return (T) this;
	}

	/**
	 * @see #setPointStyle(List)
	 */
	public List<PointStyle> getPointStyle() {
		return pointStyle;
	}

	/**
	 * @see #setPointStyle(List)
	 */
	@SuppressWarnings("unchecked")
	public T addPointStyle(PointStyle pointStyle) {
		this.pointStyle.add(pointStyle);
		return (T) this;
	}

	/**
	 * The style of point. Options are 'circle', 'triangle', 'rect', 'rectRot',
	 * 'cross', 'crossRot', 'star', 'line', and 'dash'. If the option is an
	 * image, that image is drawn on the canvas using drawImage.
	 */
	@SuppressWarnings("unchecked")
	public T setPointStyle(List<PointStyle> pointStyle) {
		this.pointStyle.clear();
		if (pointStyle != null) {
			this.pointStyle.addAll(pointStyle);
		}
		return (T) this;
	}

}

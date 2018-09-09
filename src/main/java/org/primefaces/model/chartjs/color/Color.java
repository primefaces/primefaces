/*
	Copyright 2017 Marceau Dewilde <m@ceau.be>

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package org.primefaces.model.chartjs.color;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Random;

/**
 * Immutable RGBa color model.
 */
public class Color {

	private static final Random RANDOMIZER = new Random(System.nanoTime());

	public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color RED = new Color(255, 0, 0);
	public static final Color LIME = new Color(0, 255, 0);
	public static final Color BLUE = new Color(0, 0, 255);
	public static final Color YELLOW = new Color(255, 255, 0);
	public static final Color CYAN = new Color(0, 255, 255);
	public static final Color MAGENTA = new Color(255, 0, 255);
	public static final Color SILVER = new Color(192, 192, 192);
	public static final Color GRAY = new Color(128, 128, 128);
	public static final Color MAROON = new Color(128, 0, 0);
	public static final Color OLIVE = new Color(128, 128, 0);
	public static final Color GREEN = new Color(0, 128, 0);
	public static final Color PURPLE = new Color(128, 0, 128);
	public static final Color TEAL = new Color(0, 128, 128);
	public static final Color NAVY = new Color(0, 0, 128);
	public static final Color DARK_RED = new Color(139, 0, 0);
	public static final Color BROWN = new Color(165, 42, 42);
	public static final Color CRIMSON = new Color(220, 20, 60);
	public static final Color DARK_SALMON = new Color(233, 150, 122);
	public static final Color SALMON = new Color(250, 128, 114);
	public static final Color LIGHT_SALMON = new Color(255, 160, 122);
	public static final Color ORANGE_RED = new Color(255, 69, 0);
	public static final Color DARK_ORANGE = new Color(255, 140, 0);
	public static final Color ORANGE = new Color(255, 165, 0);
	public static final Color GOLD = new Color(255, 215, 0);
	public static final Color DARK_KHAKI = new Color(189, 183, 107);
	public static final Color KHAKI = new Color(240, 230, 140);
	public static final Color YELLOW_GREEN = new Color(154, 205, 50);
	public static final Color DARK_OLIVE_GREEN = new Color(85, 107, 47);
	public static final Color GREEN_YELLOW = new Color(173, 255, 47);
	public static final Color DARK_GREEN = new Color(0, 100, 0);
	public static final Color DARK_CYAN = new Color(0, 139, 139);
	public static final Color LIGHT_CYAN = new Color(224, 255, 255);
	public static final Color AQUA = new Color(0, 255, 255);
	public static final Color DARK_TURQUOISE = new Color(0, 206, 209);
	public static final Color TURQUOISE = new Color(64, 224, 208);
	public static final Color MEDIUM_TURQUOISE = new Color(72, 209, 204);
	public static final Color PALE_TURQUOISE = new Color(175, 238, 238);
	public static final Color AQUA_MARINE = new Color(127, 255, 212);
	public static final Color LIGHT_BLUE = new Color(173, 216, 230);
	public static final Color SKY_BLUE = new Color(135, 206, 235);
	public static final Color LIGHT_SKY_BLUE = new Color(135, 206, 250);
	public static final Color MIDNIGHT_BLUE = new Color(25, 25, 112);
	public static final Color DARK_BLUE = new Color(0, 0, 139);
	public static final Color MEDIUM_BLUE = new Color(0, 0, 205);
	public static final Color BLUE_VIOLET = new Color(138, 43, 226);
	public static final Color VIOLET = new Color(238, 130, 238);
	public static final Color DEEP_PINK = new Color(255, 20, 147);
	public static final Color HOT_PINK = new Color(255, 105, 180);
	public static final Color LIGHT_PINK = new Color(255, 182, 193);
	public static final Color PINK = new Color(255, 192, 203);
	public static final Color LIGHT_YELLOW = new Color(255, 255, 224);
	public static final Color CHOCOLATE = new Color(210, 105, 30);
	public static final Color TAN = new Color(210, 180, 140);
	public static final Color LINEN = new Color(250, 240, 230);
	public static final Color LAVENDER = new Color(230, 230, 250);
	public static final Color AZURE = new Color(240, 255, 255);
	public static final Color DIM_GRAY = new Color(105, 105, 105);
	public static final Color DARK_GRAY = new Color(169, 169, 169);
	public static final Color LIGHT_GRAY = new Color(211, 211, 211);

	private final int r;
	private final int g;
	private final int b;
	private final double alpha;

	/**
	 * Constructs a new Color instance
	 * 
	 * @param r
	 *            value for Red color channel. Value between 0 and 255
	 *            (inclusive).
	 * @param g
	 *            value for Green color channel. Value between 0 and 255
	 *            (inclusive).
	 * @param b
	 *            value for Blue color channel. Value between 0 and 255
	 *            (inclusive).
	 * @param alpha
	 *            value for alpha transparency. Value between 0 and 1
	 *            (inclusive), with 0 fully transparent and 1 fully opaque.
	 */
	public Color(int r, int g, int b, double alpha) {
		if (!Color.isChannelWithinBounds(r) || !Color.isChannelWithinBounds(g) || !Color.isChannelWithinBounds(b) || !Color.isAlphaWithinBounds(alpha)) {
			throw new IllegalArgumentException("at least one argument is not within bounds");
		}
		this.r = r;
		this.g = g;
		this.b = b;
		this.alpha = alpha;
	}

	/**
	 * Constructs a new Color instance with alpha set fully opaque
	 * 
	 * @param r
	 *            value for Red color channel. Value between 0 and 255
	 *            (inclusive).
	 * @param g
	 *            value for Green color channel. Value between 0 and 255
	 *            (inclusive).
	 * @param b
	 *            value for Blue color channel. Value between 0 and 255
	 *            (inclusive).
	 */
	public Color(int r, int g, int b) {
		this(r, g, b, 1);
	}

	/**
	 * Constructs a new Color instance with the RGB values of the Color argument
	 * and the alpha transparency of the double argument.
	 */
	public Color(Color color, double alpha) {
		if (color == null) {
			throw new IllegalArgumentException("Color argument may not be null");
		}
		if (!Color.isAlphaWithinBounds(alpha)) {
			throw new IllegalArgumentException("alpha double argument is not within allowed bounds: allowed values are between 0 and 1 (inclusive), but value passed is " + alpha);
		}
		r = color.getR();
		g = color.getG();
		b = color.getB();
		this.alpha = alpha;
	}

	/**
	 * Constructs and returns a new fully random Color instance.
	 * 
	 * @return Color
	 */
	public static Color random() {
		int r = RANDOMIZER.nextInt(256);
		int g = RANDOMIZER.nextInt(256);
		int b = RANDOMIZER.nextInt(256);
		double a = RANDOMIZER.nextDouble();
		return new Color(r, g, b, a);
	}

	/**
	 * <p>
	 * Verify that argument is valid value for the R, G or B channel.
	 * </p>
	 * 
	 * <p>
	 * Any integer between 0 and 255 (inclusive) is valid.
	 * </p>
	 * 
	 * @param channel
	 * @return true if argument is valid R, G or B value
	 */
	public static boolean isChannelWithinBounds(int channel) {
		return channel >= 0 && channel <= 255;
	}

	/**
	 * <p>
	 * Verify that argument is valid value for the alpha channel.
	 * </p>
	 * 
	 * <p>
	 * Any double between 0.0d and 1.0d (inclusive) is valid.
	 * </p>
	 * 
	 * @param alpha
	 * @return true if argument is valid alpha value
	 */
	public static boolean isAlphaWithinBounds(double alpha) {
		return Double.compare(0.0d, alpha) <= 0 && Double.compare(1.0d, alpha) >= 0;
	}

	/**
	 * @return red channel value, between 0 and 255 (inclusive)
	 */
	public int getR() {
		return r;
	}

	/**
	 * @return green channel value, between 0 and 255 (inclusive)
	 */
	public int getG() {
		return g;
	}

	/**
	 * @return blue channel value, between 0 and 255 (inclusive)
	 */
	public int getB() {
		return b;
	}

	/**
	 * @return alpha channel value, between 0.0d and 1.0d (inclusive)
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * @return serialized version of this {@code Color}, as used for JSON.
	 */
	public String rgba() {
		return "rgba(" + r + "," + g + "," + b + "," + String.format(Locale.US, "%.3f", alpha) + ")";
	}

	@Override
	public String toString() {
		return rgba();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(alpha);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + b;
		result = prime * result + g;
		result = prime * result + r;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Color other = (Color) obj;
		if (Double.doubleToLongBits(alpha) != Double.doubleToLongBits(other.alpha))
			return false;
		if (b != other.b)
			return false;
		if (g != other.g)
			return false;
		if (r != other.r)
			return false;
		return true;
	}

	public static class ColorSerializer implements JsonSerializer {

		@Override
		public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(((Color)src).rgba());
		}
	}
}

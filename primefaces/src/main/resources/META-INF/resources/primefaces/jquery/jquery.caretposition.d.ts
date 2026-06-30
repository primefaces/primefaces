/**
 * Namespace for the caretposition JQueryUI plugin.
 * 
 * Contains some additional types and interfaces required for the typings.
 */
declare namespace JQueryCaretposition {
	/**
	 * Represents a position of an element.
	 */
	export interface CaretPosition {
		/**
		 * Position from the top in pixels.
		 */
		top: number;
		/**
		 * Position from the left in pixels.
		 */
		left: number;
	}
}

interface JQuery {
	/**
	 * Finds the computed value of a CSS style property.
	 * @param styleName Name of a style.
	 * @return The value of the given style.
	 */
	getComputedStyle(styleName: string): string;
	/**
	 * Copies the given CSS style property from this element to the target element.
	 * @param target Target element to which to copy the style.
	 * @param styleName Name of the CSS style property.
	 */
	cloneStyle(target: JQuery | HTMLElement, styleName: string): void;
	/**
	 * Copies all CSS style properties from this element to the target element
	 * @param target Target element to which to copy the CSS style properties.
	 */
	cloneAllStyle(target: JQuery | HTMLElement): void;
	/**
	 * Finds the position of the cursor for an INPUT or TEXTAREA element.
	 * @return The current position of the cursor, in characters from the first character of the first line.
	 */
	getCursorPosition(): number;
	/**
	 * Finds the position of the cursor for an INPUT or TEXTAREA element in pixels, relative to the top left corner
	 * of the INPUT or TEXTAREA element.
	 * @return The current position of the cursor in pixels, relative to the top left of the element.
	 */
	getCaretPosition(): JQueryCaretposition.CaretPosition;
}
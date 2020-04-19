/**
 * Namespace for the rangy JQueryUI plugin.
 * 
 * Contains some additional types and interfaces required for the typings.
 * 
 * The rangy JQueryUI plugin adds a few methods to jQuery instances:
 * 
 * - {@link JQuery.collapseSelection}
 * - {@link JQuery.deleteSelectedText}
 * - {@link JQuery.deleteText}
 * - {@link JQuery.extractSelectedText}
 * - {@link JQuery.getSelection}
 * - {@link JQuery.insertText}
 * - {@link JQuery.replaceSelectedText}
 * - {@link JQuery.setSelection}
 * - {@link JQuery.surroundSelectedText}
 */
declare namespace JQueryRangy {
    /**
     * Represents a range of text of an element. When no text is selected, the `length` may be `0`.
     */
    export interface Range {
        /**
         * Index of the character that marks the beginning of the range.
         */
        start: number,
        /**
         * Index of the character that marks the beginning of the range, exclusive. This is one character after the
         * last selected character.
         */
        end: number,
        /**
         * Length of the range (`end - start`)
         */
        length: number,
        /**
         * The selected text.
         */
        text: string;
    }
}

interface JQuery {
    /**
     * Returns the selected text of an INPUT or TEXTAREA element.
     * @param element 
     * @return The selected range, or `undefined` if this DOM element is not supported.
     */
    getSelection(): JQueryRangy.Range | undefined;
    /**
     * Selects the text at given range of an INPUT or TEXTAREA element.
     * @param startOffset 0-based index pointing to the start of the range to select (inclusive).
     * @param endOffset 0-based index pointing to the end of the range to select (exclusive).
     * @return this for chaining.
     */
    setSelection(startOffset: number, endOffset: number): this;
    /**
     * Unselects all selected text of an INPUT or TEXTAREA element.
     * @return this for chaining.
     */
    collapseSelection(): this;
    /**
     * Deletes the currently selected text of an INPUT or TEXTAREA element.
     * @return this for chaining.
     */
    deleteSelectedText(): this;
    /**
     * Deletes the text at the given range of an INPUT or TEXTAREA element, optionally moving the cursor to the start
     * of the range.
     * @param startOffset 0-based index pointing to the start of the range to select (inclusive).
     * @param endOffset 0-based index pointing to the end of the range to select (exclusive).
     * @param moveSelection `true` to position the cursor at the start of the given range, or `false` otherwise.
     * @return this for chaining.
     */
    deleteText(startOffset: number, endOffset: number, moveSelection?: boolean): this;
    /**
     * Removes and returns the currently selected text of an INPUT or TEXTAREA element.
     * @return The text that was selected. Empty string if no text is selected. `undefined` if this DOM element is not
     * supported.
     */
    extractSelectedText(): string | undefined;
    /**
     * Inserts the text at the given position of an INPUT or TEXTAREA element, optionally moving the cursor to the end
     * if the inserted text.
     * @param text Text to insert.
     * @param index 0-based index where to insert the text.
     * @param moveSelection `true` to move the cursor to the end of the inserted text, or `false` otherwise.
     * @return this for chaining.
     */
    insertText(text: string, index: number, moveSelection?: boolean): this;
    /**
     * Replaces the currently selected text of an INPUT or TEXTAREA element with the given text. When no text is
     * selected, insert the given text at the current cursor position.
     * @param text Text to replace the current selection.
     * @return this for chaining.
     */
    replaceSelectedText(text: string): this;
    /**
     * For an INPUT or TEXTAREA element: Inserts the given prefix at the beginning of the currently selected text, and
     * the given suffix at the end of the currently selected text. When no text is selected, inserts the given prefix
     * and suffix at the current position of the cursor. 
     * @param prefix Text to insert at the beginning of the currently selected text.
     * @param suffix Text to insert at the end of the currently selected text.
     * @return this for chaining.
     */
    surroundSelectedText(prefix: string, suffix: string): this;
}
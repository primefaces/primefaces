/**
 * Autosize is a small, stand-alone script to automatically adjust textarea height to fit text. Attaches autosize to the
 * given element or elements.
 * 
 * See http://www.jacklmoore.com/autosize
 * 
 * @param element The TEXTAREA element to which to attach autosize.
 */
declare function autosize<TElement extends autosize.ElementOrElements>(element: TElement): TElement;

/**
 * Autosize is a small, stand-alone script to automatically adjust textarea height to fit text. This contains a few more
 * utility methods for updating or removing autosize.
 * 
 * See http://www.jacklmoore.com/autosize
 */
declare namespace autosize {
	/**
	 * An element or a list of elements accepted by the autosize functions.
	 */
	type ElementOrElements = Element | ArrayLike<Element>;
    /**
     * Removes Autosize and reverts any changes it made to the TEXTAREA element.
     * @param element The TEXTAREA element to which autosize is attached.
     */
	export function destroy<TElement extends ElementOrElements>(element: TElement): TElement;
    /**
     * Triggers the height adjustment for an assigned textarea element.
	 * 
     * Autosize will automatically adjust the textarea height on keyboard and window resize events.
	 * 
     * There is no efficient way for Autosize to monitor for when another script has changed the TEXTAREA value or for
	 * changes in layout that impact the textarea element.
	 * @param element The TEXTAREA element to which autosize is attaached.
     */
	export function update<TElement extends ElementOrElements>(element: TElement): TElement;
}

// Types for jquery.browser, which is not an NPM package

import "jquery";

/**
 * Namespace for the browser JQueryUI plugin.
 * 
 * Contains some additional types and interfaces required for the typings.
 */
declare namespace JQueryBrowser {
	/**
	 * Browser detection result interface
	 */
	export interface BrowserDetection {
		// Browser flags
		chrome?: boolean;
		iemobile?: boolean;
		mozilla?: boolean;
		msedge?: boolean;
		msie?: boolean;
		opera?: boolean;
		opr?: boolean;
		rv?: boolean;
		safari?: boolean;
		webkit?: boolean;
		
		// Platform flags
		"windows phone"?: boolean;
		android?: boolean;
		bb?: boolean;
		blackberry?: boolean;
		cros?: boolean;
		ios?: boolean;
		ipad?: boolean;
		iphone?: boolean;
		ipod?: boolean;
		kindle?: boolean;
		linux?: boolean;
		mac?: boolean;
		playbook?: boolean;
		silk?: boolean;
		win?: boolean;
		
		// Classification flags
		mobile?: boolean;
		desktop?: boolean;
		
		// Metadata
		name: string;
		version: string;
		versionNumber: number;
		platform: string;
		
		// Function to match arbitrary user agents
		uaMatch: (ua?: string) => BrowserDetection;
	}
}

declare global {
	const jQBrowser: JQueryBrowser.BrowserDetection;

	interface JQueryStatic {
		browser: JQueryBrowser.BrowserDetection;
	}

	interface Window {
		jQBrowser: JQueryBrowser.BrowserDetection;
		jQuery?: JQueryStatic;
	}
}

// Expose jqBrowser to the global scope
//
// We include jquery.browser in the jquery bundle because it is needed both
// by `jquery-plugins.ts` and `core.ts`. We cannot include it in `jquery-plugins.ts`,
// because there are situations when only core.ts is loaded, and vice versa.
// Note: jquery.browser.js already sets window.jQBrowser, so this is just for type safety
Object.assign(window, { jQBrowser: window.jQBrowser });
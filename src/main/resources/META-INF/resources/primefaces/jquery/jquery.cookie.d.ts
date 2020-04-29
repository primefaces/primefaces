/**
 * Namespace for the cookie JQueryUI plugin, available as `$.fn.cookie` and `$.fn.removeCookie`.
 * 
 * Contains some additional types and interfaces required for the typings.
 */
declare namespace JQueryCookie {
	/**
	 * Represents the properties of a cookie, other that its name and value.
	 */
	export interface Options {
		/**
		 * The date when the cookie expires. When a number is given, it is interpreted as the number of days the cookie
		 * is valid from the current date. Default to no expiration date.
		 */
		expires: number | Date;
		/**
		 * Path of the cookie. Default to the path of current page.
		 */
		path: string;
		/**
		 * Domain of the cookie. Defaults to the domain of the current page.
		 */
		domain: string;
		/**
		 * `true` if the cookie should be secure, or `false` otherwise. Default to `false`.
		 */
		secure: boolean;
	}
}

interface JQueryStatic {
	/**
	 * Sets a cookie with the given value.
	 * @param key Name of the cookie to set.
	 * @param options Option of the cookie to set.
	 * @return The literal text of the cookie that was set, eg. `key=value; expires=Wed, 01 Jan 2000 12:00:00 GMT`.
	 */
	cookie(key: string, value: string, options?: Partial<JQueryCookie.Options>): string;
	/**
	 * Removes the given cookie. You should pass in the same options you used when setting the cookie.
	 * @param key Name of the cookie to remove.
	 * @param options Option of the cookie to remove.
	 * @return `true` if the cookie does not exist anymore, `false` otherwise.
	 */
	removeCookie(key: string, options?: Partial<JQueryCookie.Options>): boolean;
}
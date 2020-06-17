/**
 * Namespace for the cookie Javascript Cookie, available as `Cookies.set` and `Cookies.get`.
 * 
 * Contains some additional types and interfaces required for the typings.
 * https://github.com/js-cookie/js-cookie
 */
declare namespace CookieJs {
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
		/**
		 * A String, with possible values lax or strict, prevents the browser from sending cookie along with cross-site requests. Default: not set, i.e. include cookie in any request.
		 */
		sameSite: string;
	}
}
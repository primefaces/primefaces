/**
 * Namespace for the autoNumeric JQuery plugin.
 * 
 * autoNumeric is a standalone Javascript library that provides live as-you-type formatting for international numbers
 * and currencies.
 */
declare namespace JQueryAutoNumeric {
  /**
   * An object with properties. The value of each property is either the value directly, or a function that returns the
   * value.
   * 
   * For example, it is possible to pass options to autoNumeric likes this:
   * 
   * ```javascript
   * $.autoNumeric({
   *   // Decimal separator
   *   aDec: ",",
   *   // Unit
   *   aSign: function() {
   *     return " kg";
   *   }
   * });
   * ```
   *
   */
  type ValueOrSupplier<T> = {
      [P in keyof T]: T[P] | ((this: Window) => T[P]);
  };
  
  /**
   * The kind of brackets used for negative values when the input does not have focus.  
   */
  type BracketMode = "(,)" | "[,]" | "{,}" | "<,>";
  
  /**
   * Whether the input is allowed to be empty or must at least contain a zero or unit sign. 
   */
  type EmptyMode = "empty" | "zero" | "sign";
  
  /**
   * How to group digits, in groups of 2 (hundreds), 3 (thousands), or 4 (ten-thousands). 
   */
  type GroupMode = 2 | 3 | 4;
  
  /**
   * The round mode used for rounding numbers. 
   */
  type RoundMode = "S" | "A" | "s" | "a" | "B" | "U" | "D" | "C" | "F" | "CHF";
  
  /**
   * Whether the unit sign is placed before or after the number. 
   */
  type SignMode = "p" | "s";
  
  /**
   * Whether leading zeroes are allowed and how they should be treated. 
   */
  type ZeroMode = "allow" | "deny" | "keep";
  
  /**
   * Options that may be passed to auto numeric during initialization. 
   */
  interface InitOptions {
      /**
       * Controls the decimal character. Default is a period (`.`).
       */
      aDec: string;
      
      /**
       * 
       * Controls if default (initial) values are formatted on page ready (load).
       * - true: Initial values are formatted on page ready (this is the default behavior)
       * - false: Initial values are not formatted on page ready. Formatting starts once the user starts typing.
       */
      aForm: boolean;
      
      /**
       * Controls padding of the decimal places.
       * true: always pads the decimal with zeros (default)
       * false: no padding
       */

       aPad: boolean;
      /**
       * Controls the thousand separator character. Default is comma (`,`).
       */
       aSep: string;

      /**
       * Displays the desired currency or unit symbol (examples: `€` or `EUR`). Default is `undefined`.
       * 
       * If not set, no unit symbol is shown.
       */
       aSign: string;

      /**
       * This was developed to accommodate for different keyboard layouts.
       * `altDec` allows you to declare an alternative key to enter the
       * decimal separator assigned in aDec
       */
       altDec: string;

      /**
       * Controls the digital grouping and the placement of the thousand separator, eg.
       * 
       * - `2`: Groups of hundreds, eg. `13,24,98`
       * - `3`: Groups of thousands, eg. `132,498` (default)
       * - `4`: Groups of ten-thousands, eg. `13,2498`
       */
       dGroup: GroupMode;

      /**
       * Controls leading zeros behavior.
       * - `allow`: Allows leading zero to be entered. They are removed on focusout event (default).
       * - `deny`: Leading zeros not allowed.
       * - `keep`: Leading zeros allowed and will be retained on the focusout event.
       */
       lZero: ZeroMode;

      /**
       * Overrides the decimal places that that are set via the `vMin` / `vMax` settings.
       * Default is `undefined`.
       */
       mDec: number;
      
       /**
       * Sets the rounding method used. Available methods are:
       * - `S`: Round-Half-Up Symmetric (default)
       * - `A`: Round-Half-Up Asymmetric
       * - `s`: Round-Half-Down Symmetric
       * - `a`: Round-Half-Down Asymmetric
       * - `B`: Round-Half-Even "Bankers Rounding"
       * - `U`: Round Up "Round-Away-From-Zero"
       * - `D`: Round Down "Round-Toward-Zero" - same as truncate
       * - `C`: Round to Ceiling "Toward Positive Infinity"
       * - `F`: Round to Floor "Toward Negative Infinity"
       * - `CHF`: Rounding for Swiss currency "to the nearest .00 or .05"
       */
       mRound: RoundMode;
      
       /**
       * Controls if negative values are displayed with brackets when the input does not have focus.
       * Default is `undefined`.
       */
      nBracket: BracketMode;
      
      /**
       * Controls the placement of the currency or unit symbol (prefix or suffix).
       * - `p`: prefix to the left (default).
       * - `s`: suffix to the right.
       */
      pSign: SignMode;
      
      /**
       * Controls the maximum allowed value. Default is `9999999999999.99`.
       */
      vMax: number;
      
      /**
       * Controls the minimum allowed value. Default is `-9999999999999.99`.
       */
      vMin: number;
      
      /**
       * Controls input display behavior.
       * - `empty`: Allows input to be empty (default) 
       * - `zero`: Input field will have at least a zero value.
       * - `sign`: The currency symbol is always present.
       */
      wEmpty: EmptyMode;
  }
}

interface JQuery {
  /**
   * Initializes autoNumeric. Must be run before other methods can be called. This method should only be used with text
   * input fields.
   * 
   * autoNumeric is a JavaScript library for entering and formatting numbers in a language and country dependent way.
   * 
   * First you need to select an input field with jQuery. Then you can pass the desired options to autoNumeric as
   * follows:
   * 
   * ```javascript
   * $("input").autoNumeric("init", {
   *   aDec: '.', // Use a period as a decimal separator
   *   aSep: '', // No separator for thousands
   *   aSign: ' kg', // Unit kg
   *   pSign: 's', // Place the unit after the number
   *   vMin: 1, // Must enter at least a value of 1
   *   vMax: 100, // Must enter a value less than 100
   *   mDec: 3, // At most 3 digits after the period
   *   aPad: false // May be less than 3 decimal digits
   *   // more options...
   * });
   * ```
   *
   * Now the user can enter a number like `3,99` in the input field. This is then displayed as `3,99 kg`. autoNumeric
   * ensures that only valid number can be entered.
   * 
   * Some common options include:
   * 
   * - aDec: Decimal separator
   * - aSep: Separator for thousand (such as `12,658`)
   * - aSign: Unit to show before or after the number, such as ` EUR` or ` ¥`.
   * - pSign: Whether the unit should be place at the beginning (value `s`) or at the end (value `p`)
   * - vMin: Smallest allowed numerical value
   * - vMax: Largets allowed numerical value
   * - mDec: Maximum number of digits after the period
   * - aPad: When set to `true`, always pad the decimal digits with `0`s (such as `2,3400`)
   * 
   * @param options Optional settings for autoNumeric.
   * @return This JQuery instance for chaining.
   */
  autoNumeric(options?: Partial<JQueryAutoNumeric.ValueOrSupplier<JQueryAutoNumeric.InitOptions>>): this;
  
  /**
   * Stops autoNumeric.
   * @return This JQuery instance for chaining.
   */
  autoNumeric(method: "destroy"): this;

  /**
   * Retrieves the actual numerical value, irrespective of the current formatting options.
   * @return The current value, formatted as `nnnn.nn` with the period as the decimal point.
   */
  autoNumeric(method: "get"): string;

  /**
   * This basically uses jQuery's `serializeArray method which returns a JavaScript array of objects, ready to be
   * encoded as a JSON string.
   * @return The values, always formatted as 'nnnn.nn' with the period as the decimal point.
   */
   autoNumeric(method: "getArray"): {name: string; value: string}[];

  /**
   * @return The autoNumeric settings for this field. You may find this helpful when developing a page.
   */
   autoNumeric(method: "getSettings"): JQueryAutoNumeric.InitOptions;

  /**
   * This basically uses jQuery's .serialize() method which creates a text string (URL-encoded notation) from a set of
   * form elements that is ready for submission. The extra step taken here is the string is split and iterated through
   * and the formatted values are replaced with unformatted values. The string is then joined back together and
   * returned.
   * @return The values, always formatted as 'nnnn.nn' with the period as the decimal point.
   */
  autoNumeric(method: "getString"): string;

  /**
   * Initializes autoNumeric Must be run before other methods can be called.
   * @param options Optional settings for autoNumeric.
   * @return This JQuery instance for chaining.
   */
  autoNumeric(method: "init", options?: Partial<JQueryAutoNumeric.ValueOrSupplier<JQueryAutoNumeric.InitOptions>>): this;
  
  /**
   * Sets the given number on this field, and formats it according to the current options.
   * @param number The new number to be set on this field.
   * @return This JQuery instance for chaining.
   */
  autoNumeric(method: "set", number: string | number): this;
  
  /**
   * Updates autoNumeric's settings.
   * @param options New options that are to be set.
   * @return This JQuery instance for chaining.
   */
  autoNumeric(method: "update", options: Partial<JQueryAutoNumeric.ValueOrSupplier<JQueryAutoNumeric.InitOptions>>): this;
}
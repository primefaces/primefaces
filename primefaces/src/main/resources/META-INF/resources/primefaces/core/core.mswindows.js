
if (PrimeFaces.env.mswindows && !PrimeFaces.mswindows) {
   /**
    * JS to address encoding differences between MS Word/Outlook and UTF-8. Only loaded when OS is Microsoft Windows.
    * 
    * @namespace
    */
   PrimeFaces.mswindows = {

      /**
       * A Map containing character code mappings for Windows-1252 encoding.
       * The keys are character codes, and the values are their corresponding replacements.
       * This map is used to replace incompatible Windows-1252 characters with their closest UTF-8 equivalents.
       * @type {Map<number, string>}
       */
      Windows1252Map: new Map([
         [8364, ""], [8218, ","], [402, "f"], [8222, "\""], [8230, "."], [8224, "+"], [8225, "+"],
         [710, "^"], [8240, ""], [352, "S"], [8249, "<"], [338, ""], [381, "Z"], [8216, "'"],
         [8217, "'"], [8220, "\""], [8221, "\""], [8226, "."], [8211, "-"], [8212, "-"], [732, "~"],
         [8482, ""], [353, "s"], [8250, ">"], [339, ""], [382, "z"], [376, "Y"]
      ]),

      /**
       * Decodes a given string to avoid incompatible Windows-1252 characters.
       * @param {string} input - The input string to be decoded.
       * @returns {string} The decoded string with incompatible Windows-1252 characters replaced.
       */
      decodeWindows1252Text: function (input) {
         let decodedText = "";
         let replacedContent = false;

         for (const char of input) {
            const charCode = char.charCodeAt(0);
            const replacement = this.Windows1252Map.get(charCode);

            if (replacement !== undefined) {
               decodedText += replacement;
               replacedContent = true;
            } else {
               decodedText += char;
            }
         }

         PrimeFaces.debug(`Converted "${input}" into "${decodedText}"`);

         if (replacedContent) {
            PrimeFaces.warn("Pasted text contains a special character. Please review and revise.");
         }

         return decodedText;
      },

      /**
       * Handles paste events for PrimeFaces input components.
       * This function decodes the pasted text to avoid incompatible Windows-1252 characters,
       * and updates the input value accordingly.
       * 
       * @param {ClipboardEvent} e - The paste event object.
       */
      handlePaste: function (e) {
         const target = e.target;
         const isInputText = target.classList.contains("ui-inputtext") || target.classList.contains("ui-inputtextarea");
         const hasClipboardData = e.clipboardData && e.clipboardData.getData;

         if (target && isInputText && !target.disabled && hasClipboardData) {
            e.preventDefault();

            const clipboardText = e.clipboardData.getData("text/plain");
            const decodedClipboardText = this.decodeWindows1252Text(clipboardText);

            const selectionStart = target.selectionStart;
            const selectionEnd = target.selectionEnd;
            const currentValue = target.value;

            const beforeSelection = currentValue.substring(0, selectionStart);
            const afterSelection = currentValue.substring(selectionEnd);

            target.value = beforeSelection + decodedClipboardText + afterSelection;

            // Update cursor position
            const newCursorPosition = selectionStart + decodedClipboardText.length;
            target.setSelectionRange(newCursorPosition, newCursorPosition);

            // Trigger change event if applicable
            if (typeof target.onchange === 'function') {
               target.onchange();
            }
         }
      }
   };

   PrimeFaces.debug(`Microsoft Windows detected, adding Windows-1252 paste event listener`);
   document.addEventListener("paste", PrimeFaces.mswindows.handlePaste.bind(PrimeFaces.mswindows));
}
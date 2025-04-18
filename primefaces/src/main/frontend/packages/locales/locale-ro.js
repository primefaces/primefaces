import { ro } from "primelocale/js/ro.js";

if (window.PrimeFaces) {
  /** Romanian */
  PrimeFaces.locales["ro"] = ro;

  // custom PF labels
  PrimeFaces.locales["ro"] = $.extend(true, {}, PrimeFaces.locales["ro"], {
    isRTL: false,
    yearSuffix: "",
    timeOnlyTitle: "Doar timp",
    timeText: "Timp",
    hourText: "Ora",
    minuteText: "Minut",
    secondText: "Secunde",
    millisecondText: "Milisecunde",
    month: "Luna",
    week: "Săptămâna",
    day: "Zi",
    allDayText: "Toată ziua",
    moreLinkText: "Mai mult...",
    noEventsText: "Fără Evenimente",
    aria: {
      "datatable.sort.ASC": "activare pentru a sorta coloana crescător",
      "datatable.sort.DESC": "activare pentru a sorta coloana descrescător",
      "datatable.sort.NONE": "activare pentru a elimina sortarea pe coloană",
      "colorpicker.OPEN": "Deschideți selectorul de culori",
      "colorpicker.CLOSE": "Închideți selectorul de culori",
      "colorpicker.CLEAR": "Ștergeți culoarea selectată",
      "colorpicker.MARKER": "Saturație: {s}. Luminozitate: {v}.",
      "colorpicker.HUESLIDER": "Slider de nuanță",
      "colorpicker.ALPHASLIDER": "Slider de opacitate",
      "colorpicker.INPUT": "Câmp pentru valoarea culorii",
      "colorpicker.FORMAT": "Format de culoare",
      "colorpicker.SWATCH": "Eșantion de culoare",
      "colorpicker.INSTRUCTION": "Selector de saturație și luminozitate. Folosiți tastele săgeți sus, jos, stânga și dreapta pentru a selecta.",
      "spinner.INCREASE": "Măriți valoarea",
      "spinner.DECREASE": "Reduceți valoarea",
      "switch.ON": "Pe",
      "switch.OFF": "Off",
      "messages.ERROR": "Eroare",
      "messages.FATAL": "Fatal",
      "messages.INFO": "Infromație",
      "messages.WARN": "Atenție",
    },
    messages: {
      "jakarta.faces.component.UIInput.REQUIRED": "{0}: Eroare de validare: Valoarea este obligatorie.",
      "jakarta.faces.converter.IntegerConverter.INTEGER": "{2}: '{0}' trebuie să fie un număr format din una sau mai multe cifre.",
      "jakarta.faces.converter.IntegerConverter.INTEGER_detail": "{2}: '{0}' trebuie să fie un număr între -2147483648 și 2147483647. Exemplu: {1}.",
      "jakarta.faces.converter.DoubleConverter.DOUBLE": "{2}: '{0}' trebuie să fie un număr format din una sau mai multe cifre.",
      "jakarta.faces.converter.DoubleConverter.DOUBLE_detail": "{2}: '{0}' trebuie să fie un număr între 4.9E-324 și 1.7976931348623157E308. Exemplu: {1}.",
      "jakarta.faces.converter.BigDecimalConverter.DECIMAL": "{2}: '{0}' trebuie să fie un număr zecimal cu semn.",
      "jakarta.faces.converter.BigDecimalConverter.DECIMAL_detail": "{2}: '{0}' trebuie să fie un număr zecimal cu semn format din zero sau mai multe cifre, care poate fi urmat de un punct zecimal și o fracție. Exemplu: {1}.",
      "jakarta.faces.converter.BigIntegerConverter.BIGINTEGER": "{2}: '{0}' trebuie să fie un număr format din una sau mai multe cifre.",
      "jakarta.faces.converter.BigIntegerConverter.BIGINTEGER_detail": "{2}: '{0}' trebuie să fie un număr format din una sau mai multe cifre. Exemplu: {1}.",
      "jakarta.faces.converter.ByteConverter.BYTE": "{2}: '{0}' trebuie să fie un număr între 0 și 255.",
      "jakarta.faces.converter.ByteConverter.BYTE_detail": "{2}: '{0}' trebuie să fie un număr între 0 și 255. Exemplu: {1}.",
      "jakarta.faces.converter.CharacterConverter.CHARACTER": "{1}: '{0}' trebuie să fie un caracter valid.",
      "jakarta.faces.converter.CharacterConverter.CHARACTER_detail": "{1}: '{0}' trebuie să fie un caracter ASCII valid.",
      "jakarta.faces.converter.ShortConverter.SHORT": "{2}: '{0}' trebuie să fie un număr format din una sau mai multe cifre.",
      "jakarta.faces.converter.ShortConverter.SHORT_detail": "{2}: '{0}' trebuie să fie un număr între -32768 și 32767. Exemplu: {1}.",
      "jakarta.faces.converter.BooleanConverter.BOOLEAN": "{1}: '{0}' trebuie să fie adevărat sau false.",
      "jakarta.faces.converter.BooleanConverter.BOOLEAN_detail": "{1}: '{0}' trebuie să fie adevărat sau false. Orice altă valoare diferită de adevărat va trece în fals.",
      "jakarta.faces.validator.LongRangeValidator.MAXIMUM": "{1}: Eroare de validare: Valoarea este mai mare decât maximul admisibil de '{0}'.",
      "jakarta.faces.validator.LongRangeValidator.MINIMUM": "{1}: Eroare de validare: Valoarea este mai mică decât minimul admisibil de '{0}'.",
      "jakarta.faces.validator.LongRangeValidator.NOT_IN_RANGE": "{2}: Eroare de validare: Atributul specificat nu se încadrează între valorile așteptate de {0} și {1}.",
      "jakarta.faces.validator.LongRangeValidator.TYPE": "{0}: Eroare de validare: Tipul valorii este greșit.",
      "jakarta.faces.validator.DoubleRangeValidator.MAXIMUM": "{1}: Eroare de validare: Valoarea este mai mare decât maximul admisibil de '{0}'.",
      "jakarta.faces.validator.DoubleRangeValidator.MINIMUM": "{1}: Eroare de validare: Valoarea este mai mică decât minimul admisibil de '{0}'.",
      "jakarta.faces.validator.DoubleRangeValidator.NOT_IN_RANGE": "{2}: Eroare de validare: Atributul specificat nu se încadrează între valorile așteptate de {0} și {1}.",
      "jakarta.faces.validator.DoubleRangeValidator.TYPE": "{0}: Eroare de validare: Tipul valorii este greșit.",
      "jakarta.faces.converter.FloatConverter.FLOAT": "{2}: '{0}' trebuie să fie un număr format din una sau mai multe cifre.",
      "jakarta.faces.converter.FloatConverter.FLOAT_detail": "{2}: '{0}' trebuie să fie un număr între 1.4E-45 și 3.4028235E38. Exemplu: {1}.",
      "jakarta.faces.converter.DateTimeConverter.DATE": "{2}: '{0}' nu a putut fi înțeles ca o dată.",
      "jakarta.faces.converter.DateTimeConverter.DATE_detail": "{2}: '{0}' nu a putut fi înțeles ca o dată. Exemplu: {1}.",
      "jakarta.faces.converter.DateTimeConverter.TIME": "{2}: '{0}' nu a putut fi înțeles ca un timp.",
      "jakarta.faces.converter.DateTimeConverter.TIME_detail": "{2}: '{0}' nu a putut fi înțeles ca un timp. Exemplu: {1}.",
      "jakarta.faces.converter.DateTimeConverter.DATETIME": "{2}: '{0}' nu a putut fi înțeles ca o dată și timp.",
      "jakarta.faces.converter.DateTimeConverter.DATETIME_detail": "{2}: '{0}' nu a putut fi înțeles ca o dată și timp. Exemplu: {1}.",
      "jakarta.faces.converter.DateTimeConverter.PATTERN_TYPE": "{1}: Trebuie specificat un atribut 'pattern' sau 'type' pentru a converti valoarea '{0}'.",
      "jakarta.faces.converter.NumberConverter.CURRENCY": "{2}: '{0}' nu a putut fi înțeles ca o valută.",
      "jakarta.faces.converter.NumberConverter.CURRENCY_detail": "{2}: '{0}' nu a putut fi înțeles ca o valută. Exemplu: {1}.",
      "jakarta.faces.converter.NumberConverter.PERCENT": "{2}: '{0}' nu a putut fi înțeles ca un procent.",
      "jakarta.faces.converter.NumberConverter.PERCENT_detail": "{2}: '{0}' nu a putut fi înțeles ca un procent. Exemplu: {1}.",
      "jakarta.faces.converter.NumberConverter.NUMBER": "{2}: '{0}' nu poate fi înțeles ca un număr.",
      "jakarta.faces.converter.NumberConverter.NUMBER_detail": "{2}: '{0}' nu poate fi înțeles ca un număr. Exemplu: {1}.",
      "jakarta.faces.converter.NumberConverter.PATTERN": "{2}: '{0}' nu poate fi înțeles ca un șablon numeric.",
      "jakarta.faces.converter.NumberConverter.PATTERN_detail": "{2}: '{0}' nu poate fi înțeles ca un șablon numeric. Exemplu: {1}.",
      "jakarta.faces.validator.LengthValidator.MINIMUM": "{1}: Eroare de validare: Lungimea este mai mică decât minimul admisibil de '{0}'.",
      "jakarta.faces.validator.LengthValidator.MAXIMUM": "{1}: Eroare de validare: Lungimea este mai mare decât maximul admisibil de '{0}'.",
      "jakarta.faces.validator.RegexValidator.PATTERN_NOT_SET": "Modelul Regex trebuie setată.",
      "jakarta.faces.validator.RegexValidator.PATTERN_NOT_SET_detail": "Modelul Regex trebuie setată pentru o valoare ce nu poate fi goală.",
      "jakarta.faces.validator.RegexValidator.NOT_MATCHED": "Modelul Regex nu se potrivește.",
      "jakarta.faces.validator.RegexValidator.NOT_MATCHED_detail": "Modelul regex '{0}' nu se potrivește.",
      "jakarta.faces.validator.RegexValidator.MATCH_EXCEPTION": "Eroare la expresia regulată.",
      "jakarta.faces.validator.RegexValidator.MATCH_EXCEPTION_detail": "Eroare la expresia regulată, '{0}'.",
      "primefaces.FileValidator.FILE_LIMIT": "Numărul maxim de fișiere depășit.",
      "primefaces.FileValidator.FILE_LIMIT_detail": "Număr maxim: {0}.",
      "primefaces.FileValidator.ALLOW_TYPES": "Tip de fișier nevalid.",
      "primefaces.FileValidator.ALLOW_TYPES_detail": "Tip de fișier nevalid: „{0}”. Tipuri permise: „{1}”.",
      "primefaces.FileValidator.SIZE_LIMIT": "Dimensiunea fișierului nevalidă.",
      "primefaces.FileValidator.SIZE_LIMIT_detail": "Fișierul „{0}” nu trebuie să fie mai mare de {1}.",
      //optional for bean validation integration in client side validation
      "jakarta.faces.validator.BeanValidator.MESSAGE": "{1}: {0}",
      "jakarta.validation.constraints.AssertFalse.message": "trebuie să fie fals",
      "jakarta.validation.constraints.AssertTrue.message": "trebuie să fie adevărat",
      "jakarta.validation.constraints.DecimalMax.message": "trebuie să fie mai mic sau egal cu {0}",
      "jakarta.validation.constraints.DecimalMin.message": "trebuie să fie mai mare sau egal cu {0}",
      "jakarta.validation.constraints.Digits.message": "valoare numerică în afara limitelor (&lt;{0} cifre&gt;.&lt;{1} cifre&gt; așteptate)",
      "jakarta.validation.constraints.Email.message": "trebuie să fie o adresă de e-mail bine formată",
      "jakarta.validation.constraints.Future.message": "trebuie să fie o dată viitoare",
      "jakarta.validation.constraints.FutureOrPresent.message": "trebuie să fie o dată în prezent sau în viitor",
      "jakarta.validation.constraints.Max.message": "trebuie să fie mai mic sau egal cu {0}",
      "jakarta.validation.constraints.Min.message": "trebuie să fie mai mare sau egal cu {0}",
      "jakarta.validation.constraints.Negative.message": "trebuie să fie mai mică de 0",
      "jakarta.validation.constraints.NegativeOrZero.message": "trebuie să fie mai mică sau egală cu 0",
      "jakarta.validation.constraints.NotBlank.message": "nu trebuie să fie goală",
      "jakarta.validation.constraints.NotEmpty.message": "nu trebuie să fie goale",
      "jakarta.validation.constraints.NotNull.message": "nu trebuie să fie nulă",
      "jakarta.validation.constraints.Null.message": "trebuie să fie nulă",
      "jakarta.validation.constraints.Past.message": "trebuie să fie o întâlnire trecută",
      "jakarta.validation.constraints.PastOrPresent.message": "trebuie să fie o dată în trecut sau în prezent",
      "jakarta.validation.constraints.Pattern.message": "trebuie să se potrivească cu „{0}”",
      "jakarta.validation.constraints.Positive.message": "trebuie să fie mai mare decât 0",
      "jakarta.validation.constraints.PositiveOrZero.message": "trebuie să fie mai mare sau egal cu 0",
      "jakarta.validation.constraints.Size.message": "dimensiunea trebuie să fie între {0} și {1}",
    },
  });
}

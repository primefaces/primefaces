import { es } from "primelocale/js/es.js";
if (window.PrimeFaces) {
  /** Spanish */
  PrimeFaces.locales["es"] = es;

  // custom PF labels
  PrimeFaces.locales["es"] = $.extend(true, {}, PrimeFaces.locales["es"], {
    allDayText: "Todo el día",
    day: "Día",
    hourText: "Hora",
    isRTL: false,
    list: "Agenda",
    millisecondText: "Milisegundo",
    minuteText: "Minuto",
    month: "Mes",
    moreLinkText: "Más...",
    noEventsText: "No hay eventos",
    secondText: "Segundo",
    timeOnlyTitle: "Sólo hora",
    timeText: "Tiempo",
    unexpectedError: "Error inesperado",
    week: "Semana",
    weekNumberTitle: "S",
    year: "Año",
    yearSuffix: "",
    aria: {
      "colorpicker.ALPHASLIDER": "Deslizador de opacidad",
      "colorpicker.CLEAR": "Limpiar el color seleccionado",
      "colorpicker.CLOSE": "Cerrar el selector de color",
      "colorpicker.FORMAT": "Formato del color",
      "colorpicker.HUESLIDER": "Deslizador de tono",
      "colorpicker.INPUT": "Entrada del valor para el color",
      "colorpicker.INSTRUCTION": "Selector de saturación y brillo. Utilice las teclas de flecha arriba, abajo, izquierda y derecha para seleccionar.",
      "colorpicker.MARKER": "Saturación: {s}. Brillo: {v}.",
      "colorpicker.OPEN": "Abrir selector de color",
      "colorpicker.SWATCH": "Muestra de color",
      "datatable.sort.ASC": "ordenar columna ascendentemente",
      "datatable.sort.DESC": "ordenar columna descendentemente",
      "datatable.sort.NONE": "eliminar la ordenación de la columna",
      "messages.ERROR": "Error",
      "messages.FATAL": "Fatal",
      "messages.INFO": "Información",
      "messages.WARN": "Advertencia",
      "spinner.DECREASE": "Disminuir valor",
      "spinner.INCREASE": "Aumentar valor",
      "switch.OFF": "Desactivar",
      "switch.ON": "Activar",
    },
    messages: {
      "jakarta.faces.component.UIInput.REQUIRED": "{0}: Error de validación: se necesita un valor.",
      "jakarta.faces.converter.BigDecimalConverter.DECIMAL": "{2}: '{0}' debe ser un número decimal positivo o negativo.",
      "jakarta.faces.converter.BigDecimalConverter.DECIMAL_detail": "{2}: '{0}' debe ser un número decimal positivo o negativo formado por cero o más dígitos, que pueden incluir a continuación una coma decimal y una fracción.  Ejemplo: {1}",
      "jakarta.faces.converter.BigIntegerConverter.BIGINTEGER": "{2}: '{0}' debe ser un número formado por uno o más dígitos.",
      "jakarta.faces.converter.BigIntegerConverter.BIGINTEGER_detail": "{2}: '{0}' debe ser un número formado por uno o más dígitos. Ejemplo: {1}",
      "jakarta.faces.converter.BooleanConverter.BOOLEAN": "{1}: '{0}' debe ser 'verdadero' o 'falso'",
      "jakarta.faces.converter.BooleanConverter.BOOLEAN_detail": "{1}: '{0}' debe ser 'verdadero' o 'falso'.  Cualquier valor diferente a 'verdadero' se evaluará como 'falso'.",
      "jakarta.faces.converter.ByteConverter.BYTE": "{2}: '{0}' debe ser un número entre 0 y 255.",
      "jakarta.faces.converter.ByteConverter.BYTE_detail": "{2}: '{0}' debe ser un número entre 0 y 255.  Ejemplo: {1}",
      "jakarta.faces.converter.CharacterConverter.CHARACTER": "{1}: '{0}' debe ser un carácter válido.",
      "jakarta.faces.converter.CharacterConverter.CHARACTER_detail": "{1}: '{0}' debe ser un carácter ASCII válido.",
      "jakarta.faces.converter.DateTimeConverter.DATE": "{2}: '{0}' no se ha podido reconocer como fecha.",
      "jakarta.faces.converter.DateTimeConverter.DATE_detail": "{2}: '{0}' no se ha podido reconocer como fecha. Ejemplo: {1}",
      "jakarta.faces.converter.DateTimeConverter.DATETIME": "{2}: '{0}' no se ha podido reconocer como fecha y hora.",
      "jakarta.faces.converter.DateTimeConverter.DATETIME_detail": "{2}: '{0}' no se ha podido reconocer como fecha y hora. Ejemplo: {1}",
      "jakarta.faces.converter.DateTimeConverter.PATTERN_TYPE": "{1}: debe especificarse el atributo 'patrón' o 'tipo' para convertir el valor '{0}'",
      "jakarta.faces.converter.DateTimeConverter.TIME": "{2}: '{0}' no se ha podido reconocer como hora.",
      "jakarta.faces.converter.DateTimeConverter.TIME_detail": "{2}: '{0}' no se ha podido reconocer como hora. Ejemplo: {1}",
      "jakarta.faces.converter.DoubleConverter.DOUBLE": "{2}: '{0}' debe ser un número formado por uno o más dígitos.",
      "jakarta.faces.converter.DoubleConverter.DOUBLE_detail": "{2}: '{0}' debe ser un número entre 4.9E-324 y 1.7976931348623157E308  Ejemplo: {1}",
      "jakarta.faces.converter.FloatConverter.FLOAT": "{2}: '{0}' debe ser un número formado por uno o más dígitos.",
      "jakarta.faces.converter.FloatConverter.FLOAT_detail": "{2}: '{0}' debe ser un número entre 1.4E-45 y 3.4028235E38  Ejemplo: {1}",
      "jakarta.faces.converter.IntegerConverter.INTEGER": "{2}: '{0}' debe ser un número formado por uno o más dígitos.",
      "jakarta.faces.converter.IntegerConverter.INTEGER_detail": "{2}: '{0}' debe ser un número entre -2147483648 y 2147483647. Ejemplo: {1}",
      "jakarta.faces.converter.NumberConverter.CURRENCY": "{2}: '{0}' no se ha podido reconocer como un valor de divisa.",
      "jakarta.faces.converter.NumberConverter.CURRENCY_detail": "{2}: '{0}' no se ha podido reconocer como un valor de divisa. Ejemplo: {1}",
      "jakarta.faces.converter.NumberConverter.NUMBER": "{2}: '{0}' no es un número.",
      "jakarta.faces.converter.NumberConverter.NUMBER_detail": "{2}: '{0}' no es un número. Ejemplo: {1}",
      "jakarta.faces.converter.NumberConverter.PATTERN": "{2}: '{0}' no es un patrón numérico.",
      "jakarta.faces.converter.NumberConverter.PATTERN_detail": "{2}: '{0}' no es un patrón numérico. Ejemplo: {1}",
      "jakarta.faces.converter.NumberConverter.PERCENT": "{2}: '{0}' no se ha podido reconocer como porcentaje.",
      "jakarta.faces.converter.NumberConverter.PERCENT_detail": "{2}: '{0}' no se ha podido reconocer como porcentaje. Ejemplo: {1}",
      "jakarta.faces.converter.ShortConverter.SHORT": "{2}: '{0}' debe ser un número formado por uno o más dígitos.",
      "jakarta.faces.converter.ShortConverter.SHORT_detail": "{2}: '{0}' debe ser un número entre -32768 y 32767 Ejemplo: {1}",
      "jakarta.faces.validator.BeanValidator.MESSAGE": "{1}: {0}",
      "jakarta.faces.validator.DoubleRangeValidator.MAXIMUM": "{1}: Error de validación: el valor es superior al máximo permitido de '{0}'",
      "jakarta.faces.validator.DoubleRangeValidator.MINIMUM": "{1}: Error de validación: el valor es inferior al mínimo permitido de '{0}'",
      "jakarta.faces.validator.DoubleRangeValidator.NOT_IN_RANGE": "{2}: Error de validación: el atributo especificado no está entre los valores esperados {0} y {1}",
      "jakarta.faces.validator.DoubleRangeValidator.TYPE": "{0}: Error de validación: el valor no tiene el tipo correcto.",
      "jakarta.faces.validator.LengthValidator.MAXIMUM": "{1}: Error de validación: La longitud es superior al máximo permitido de '{0}'",
      "jakarta.faces.validator.LengthValidator.MINIMUM": "{1}: Error de validación: La longitud es inferior al mínimo permitido de '{0}'",
      "jakarta.faces.validator.LongRangeValidator.MAXIMUM": "{1}: Error de validación: el valor es superior al máximo permitido de '{0}'",
      "jakarta.faces.validator.LongRangeValidator.MINIMUM": "{1}: Error de validación: el valor es inferior al mínimo permitido de '{0}'",
      "jakarta.faces.validator.LongRangeValidator.NOT_IN_RANGE": "{2}: Error de validación: el atributo especificado no está entre los valores esperados {0} y {1}.",
      "jakarta.faces.validator.LongRangeValidator.TYPE": "{0}: Error de validación: el valor no tiene el tipo correcto.",
      "jakarta.faces.validator.RegexValidator.MATCH_EXCEPTION": "Error en la expresión regular.",
      "jakarta.faces.validator.RegexValidator.MATCH_EXCEPTION_detail": "Error en la expresión regular, '{0}'",
      "jakarta.faces.validator.RegexValidator.NOT_MATCHED": "No hay coincidencias para la expresión regular",
      "jakarta.faces.validator.RegexValidator.NOT_MATCHED_detail": "No hay coincidencias para la expresión regular '{0}'",
      "jakarta.faces.validator.RegexValidator.PATTERN_NOT_SET": "El patrón de la expresión regular no debe estar vacío.",
      "jakarta.faces.validator.RegexValidator.PATTERN_NOT_SET_detail": "El patrón de la expresión regular no debe estar vacío.",
      "jakarta.validation.constraints.AssertFalse.message": "debe ser falso",
      "jakarta.validation.constraints.AssertTrue.message": "debe ser verdadero",
      "jakarta.validation.constraints.DecimalMax.message": "debe ser menor o igual que {0}",
      "jakarta.validation.constraints.DecimalMin.message": "debe ser mayor o igual que {0}",
      "jakarta.validation.constraints.Digits.message": "valor numérico fuera de los límites (se esperaba (<{0} digitos>.<{1} digitos>)",
      "jakarta.validation.constraints.Email.message": "debe ser una dirección de correo electrónico bien formada",
      "jakarta.validation.constraints.Future.message": "debe ser una fecha posterior",
      "jakarta.validation.constraints.FutureOrPresent.message": "debe ser una fecha en el presente o en el futuro",
      "jakarta.validation.constraints.Max.message": "debe ser menor o igual que {0}",
      "jakarta.validation.constraints.Min.message": "debe ser mayor o igual que {0}",
      "jakarta.validation.constraints.Negative.message": "debe ser menor que 0",
      "jakarta.validation.constraints.NegativeOrZero.message": "debe ser menor o igual a 0",
      "jakarta.validation.constraints.NotBlank.message": "no debe ser nulo",
      "jakarta.validation.constraints.NotEmpty.message": "no debe ser nulo",
      "jakarta.validation.constraints.NotNull.message": "no debe ser nulo",
      "jakarta.validation.constraints.Null.message": "debe ser nulo",
      "jakarta.validation.constraints.Past.message": "debe ser una fecha anterior",
      "jakarta.validation.constraints.PastOrPresent.message": "debe ser una fecha en el pasado o en el presente",
      "jakarta.validation.constraints.Pattern.message": "debe coincidir con '{0}'",
      "jakarta.validation.constraints.Positive.message": "debe ser mayor que 0",
      "jakarta.validation.constraints.PositiveOrZero.message": "debe ser mayor o igual a 0",
      "jakarta.validation.constraints.Size.message": "el tamaño debe estar entre {0} y {1}",
      "primefaces.FileValidator.ALLOW_TYPES": "Tipo de archivo no válido.",
      "primefaces.FileValidator.ALLOW_TYPES_detail": "Tipo de archivo no válido: '{0}'. Tipos permitidos: '{1}'.",
      "primefaces.FileValidator.FILE_LIMIT": "Se excedió el número máximo de archivos.",
      "primefaces.FileValidator.FILE_LIMIT_detail": "Número máximo: {0}.",
      "primefaces.FileValidator.SIZE_LIMIT": "Tamaño de archivo no válido.",
      "primefaces.FileValidator.SIZE_LIMIT_detail": "El archivo '{0}' no debe ser mayor que {1}.",
    },
  });
}

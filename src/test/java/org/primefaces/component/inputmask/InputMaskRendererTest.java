package org.primefaces.component.inputmask;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class InputMaskRendererTest {

    @Test
    void translateMaskIntoRegex_Optional() {
        // Arrange
        InputMaskRenderer comp = new InputMaskRenderer();
        StringBuilder sb = new StringBuilder();
        String mask = "9[999]";

        // Act
        Pattern result = comp.translateMaskIntoRegex(sb, mask);

        // Assert
        assertEquals("[0-9][0-9]?[0-9]?[0-9]?", result.pattern());
    }

}

/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.avatar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import javax.faces.context.FacesContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AvatarRendererTest {

    // system under test
    private AvatarRenderer sut;

    // mocks (Depended On Component)
    private Avatar avatar;
    private FacesContext context;

    // parameterized data set
    private static Stream<Arguments> data() {
        return Stream.of(
                    Arguments.of(null, null),
                    Arguments.of("G", "G"),
                    Arguments.of("BD", "BD"),
                    Arguments.of("Wolfgang Amadeus Mozart", "WM"),
                    Arguments.of("PrimeFaces Rocks", "PR"),
                    Arguments.of("ŞPrimeFaces Rocks", "ŞR"),
                    Arguments.of("PrimeFaces ŞRocks", "PŞ"),
                    Arguments.of("ŞPrimeFaces ŞRocks", "ŞŞ"),
                    Arguments.of("Àlbert Ñunes", "ÀÑ"));
    }

    @BeforeEach
    public void setup() {
        // system under test
        sut = new AvatarRenderer();

        // mocks
        avatar = mock(Avatar.class);
        context = mock(FacesContext.class);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testCalculateLabel(String input, String expected) {
        // Arrange
        when(avatar.getLabel()).thenReturn(input);

        // Act
        String result = sut.calculateLabel(context, avatar);

        // Assert
        assertEquals(expected, result);
        verify(avatar).getLabel();
    }
}

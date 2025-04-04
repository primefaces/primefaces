/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.util;

import org.primefaces.mock.CollectingResponseWriter;
import org.primefaces.mock.FacesContextMock;

import jakarta.faces.context.FacesContext;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StyleBuilderTest {

    protected StyleBuilder getStyleBuilder() {
        CollectingResponseWriter writer = new CollectingResponseWriter();
        FacesContext context = new FacesContextMock(writer);
        return new StyleBuilder(context);
    }

    @Test
    void add() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add("font-weight: bold");

        // Assert
        assertEquals("font-weight: bold", builder.build());
    }

    @Test
    void addTrue() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add(true, "font-weight: bold");

        // Assert
        assertEquals("font-weight: bold", builder.build());
    }

    @Test
    void addFalse() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add(false, "font-weight: bold");

        // Assert
        assertEquals("", builder.build());
    }

    @Test
    void attribute() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add("width", "10px");

        // Assert
        assertEquals("width:10px", builder.build());
    }

    @Test
    void nullAttribute() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add("width", null);

        // Assert
        assertEquals("", builder.build());
    }

    @Test
    void addTrueStyle() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add(true, "height", "11px", "22px");

        // Assert
        assertEquals("height:11px", builder.build());
    }

    @Test
    void addFalseStyle() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add(false, "height", "11px", "22px");

        // Assert
        assertEquals("height:22px", builder.build());
    }

    @Test
    void complex() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add("default", "none")
                .add("user", "some")
                .add("height:33px")
                .add(false, "width", "8px", "100vh")
                .add(true, "visibility", "visible", "hidden");

        // Assert
        assertEquals("default:none;user:some;height:33px;width:100vh;visibility:visible", builder.build());
    }

}

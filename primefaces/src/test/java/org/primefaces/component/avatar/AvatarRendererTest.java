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
import static org.mockito.Mockito.when;

import java.util.HashMap;

import javax.faces.context.FacesContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AvatarRendererTest {

    private AvatarRenderer renderer;
    private Avatar avatar;
    private FacesContext context;

    @BeforeEach
    public void setup() {
        renderer = new AvatarRenderer();
        avatar = mock(Avatar.class);
        context = mock(FacesContext.class);
        when(context.getAttributes()).thenReturn(new HashMap<>());
    }

    @Test
    void testCalculateLabelSingleLetter() {
        // Arrange
        when(avatar.getLabel()).thenReturn("G");

        // Act
        String result = renderer.calculateLabel(context, avatar);

        // Assert
        assertEquals("G", result);
    }

    @Test
    void testCalculateLabelAlreadyInitials() {
        // Arrange
        when(avatar.getLabel()).thenReturn("BD");

        // Act
        String result = renderer.calculateLabel(context, avatar);

        // Assert
        assertEquals("BD", result);
    }

    @Test
    void testCalculateLabelThreeWords() {
        // Arrange
        when(avatar.getLabel()).thenReturn("Wolfgang Amadeus Mozart");

        // Act
        String result = renderer.calculateLabel(context, avatar);

        // Assert
        assertEquals("WM", result);
    }

    @Test
    void testCalculateLabelIssue7900X1() {
        // Arrange
        when(avatar.getLabel()).thenReturn("PrimeFaces Rocks");

        // Act
        String result = renderer.calculateLabel(context, avatar);

        // Assert
        assertEquals("PR", result);
    }

    @Test
    void testCalculateLabelIssue7900X2() {
        // Arrange
        when(avatar.getLabel()).thenReturn("ŞPrimeFaces Rocks");

        // Act
        String result = renderer.calculateLabel(context, avatar);

        // Assert
        assertEquals("ŞR", result);
    }

    @Test
    void testCalculateLabelIssue7900X3() {
        // Arrange
        when(avatar.getLabel()).thenReturn("PrimeFaces ŞRocks");

        // Act
        String result = renderer.calculateLabel(context, avatar);

        // Assert
        assertEquals("PŞ", result);
    }

    @Test
    void testCalculateLabelIssue7900X4() {
        // Arrange
        when(avatar.getLabel()).thenReturn("ŞPrimeFaces ŞRocks");

        // Act
        String result = renderer.calculateLabel(context, avatar);

        // Assert
        assertEquals("ŞŞ", result);
    }

    @Test
    void testCalculateLabelIssue7900Unicode() {
        // Arrange
        when(avatar.getLabel()).thenReturn("Àlbert Ñunes");

        // Act
        String result = renderer.calculateLabel(context, avatar);

        // Assert
        assertEquals("ÀÑ", result);
    }

}

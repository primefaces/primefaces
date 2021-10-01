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

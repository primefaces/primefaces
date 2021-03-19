package org.primefaces.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.primefaces.mock.CollectingResponseWriter;
import org.primefaces.mock.FacesContextMock;
import javax.faces.context.FacesContext;

class StyleBuilderTest {

    protected StyleBuilder getStyleBuilder() {
        CollectingResponseWriter writer = new CollectingResponseWriter();
        FacesContext context = new FacesContextMock(writer);
        return new StyleBuilder(context);
    }

    @Test
    void testAdd() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add("font-weight: bold");

        // Assert
        assertEquals("font-weight: bold", builder.build());
    }

    @Test
    void testAddTrue() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add(true, "font-weight: bold");

        // Assert
        assertEquals("font-weight: bold", builder.build());
    }

    @Test
    void testAddFalse() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add(false, "font-weight: bold");

        // Assert
        assertEquals("", builder.build());
    }

    @Test
    void testDefaultStyle() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add("default:none", "width:10px");

        // Assert
        assertEquals("default:none;width:10px", builder.build());
    }

    @Test
    void testAddTrueStyle() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add(true, "height", "11px", "22px");

        // Assert
        assertEquals("height:11px", builder.build());
    }

    @Test
    void testAddFalseStyle() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add(false, "height", "11px", "22px");

        // Assert
        assertEquals("height:22px", builder.build());
    }

    @Test
    void testComplex() {
        // Arrange
        StyleBuilder builder = getStyleBuilder();

        // Act
        builder.add("default:none", "user:some")
                    .add("height:33px")
                    .add(false, "width", "8px", "100vh")
                    .add(true, "visibility", "visible", "hidden");

        // Assert
        assertEquals("default:none;user:some;height:33px;width:100vh;visibility:visible", builder.build());
    }

}

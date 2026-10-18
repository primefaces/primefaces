/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.integrationtests.theming;

import org.primefaces.selenium.PrimeSelenium;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Visual regression of the token themes: a fixed set of components (theming012.xhtml) is captured per preset and color
 * scheme and compared with a reference image.
 * <p>
 * The references live in {@code src/test/resources/theming-baselines/<os>/<preset>-<scheme>.png} (font rendering differs
 * between operating systems). A missing reference is created from the current rendering and the test is skipped: review
 * the image and commit it. On a difference the actual and a diff image are written to {@code target/theming-screenshots}.
 * To accept an intended change, delete the reference and run the test again.
 */
class Theming012Test extends AbstractThemingTest {

    /** a pixel differs when one channel differs by more than this (antialiasing noise stays below) */
    private static final int CHANNEL_TOLERANCE = 24;

    /** share of differing pixels that still passes */
    private static final double MAX_DIFF_RATIO = 0.002;

    @ParameterizedTest(name = "{0} {1}")
    @Order(1)
    @DisplayName("Theming: visual regression per preset and color scheme")
    @CsvSource({
        "aura, light", "aura, dark",
        "lara, light", "lara, dark",
        "nora, light", "nora, dark",
        "metal, light", "metal, dark",
        "glass, light", "glass, dark"
    })
    void snapshot(String preset, String scheme) throws IOException {
        // Arrange
        PrimeSelenium.goTo("theming/theming012.xhtml?theme=" + preset);
        WebElement snapshot = PrimeSelenium.getWebDriver().findElement(By.id("snapshot"));
        setColorScheme(scheme);
        PrimeSelenium.executeScript("document.activeElement && document.activeElement.blur();");
        PrimeSelenium.wait(200);

        // Act
        BufferedImage actual = ImageIO.read(new ByteArrayInputStream(snapshot.getScreenshotAs(OutputType.BYTES)));

        // Assert
        String name = preset + "-" + scheme + ".png";
        Path baseline = Paths.get("src", "test", "resources", "theming-baselines", os(), name);
        if (!Files.exists(baseline)) {
            Files.createDirectories(baseline.getParent());
            ImageIO.write(actual, "png", baseline.toFile());
            Assumptions.abort("reference created, review and commit it: " + baseline.toAbsolutePath());
        }
        BufferedImage expected = ImageIO.read(baseline.toFile());
        Path out = Paths.get("target", "theming-screenshots");
        if (expected.getWidth() != actual.getWidth() || expected.getHeight() != actual.getHeight()) {
            write(out, name, actual, null);
            assertEquals(expected.getWidth() + "x" + expected.getHeight(), actual.getWidth() + "x" + actual.getHeight(),
                    name + ": size changed, actual written to " + out.toAbsolutePath());
        }
        BufferedImage diff = new BufferedImage(actual.getWidth(), actual.getHeight(), BufferedImage.TYPE_INT_RGB);
        long differing = 0;
        for (int y = 0; y < actual.getHeight(); y++) {
            for (int x = 0; x < actual.getWidth(); x++) {
                int a = actual.getRGB(x, y);
                int e = expected.getRGB(x, y);
                boolean differs = channel(a, e, 16) || channel(a, e, 8) || channel(a, e, 0);
                if (differs) {
                    differing++;
                }
                diff.setRGB(x, y, differs ? 0xff0000 : (e & 0xfefefe) >> 1 | 0x808080);
            }
        }
        double ratio = (double) differing / (actual.getWidth() * actual.getHeight());
        if (ratio > MAX_DIFF_RATIO) {
            write(out, name, actual, diff);
        }
        assertTrue(ratio <= MAX_DIFF_RATIO, String.format(Locale.ROOT,
                "%s differs from the reference in %.3f%% of the pixels (max %.3f%%), actual and diff written to %s",
                name, ratio * 100, MAX_DIFF_RATIO * 100, out.toAbsolutePath()));
        assertNoJavascriptErrors();
    }

    private static boolean channel(int a, int b, int shift) {
        return Math.abs(((a >> shift) & 0xff) - ((b >> shift) & 0xff)) > CHANNEL_TOLERANCE;
    }

    private static void write(Path dir, String name, BufferedImage actual, BufferedImage diff) throws IOException {
        Files.createDirectories(dir);
        ImageIO.write(actual, "png", dir.resolve(name.replace(".png", "-actual.png")).toFile());
        if (diff != null) {
            ImageIO.write(diff, "png", dir.resolve(name.replace(".png", "-diff.png")).toFile());
        }
    }

    private static String os() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        return os.contains("win") ? "windows" : os.contains("mac") ? "mac" : "linux";
    }
}

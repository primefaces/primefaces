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
package org.primefaces.renderkit;

import org.primefaces.component.api.IterationCleanupAware;
import org.primefaces.mock.FacesContextMock;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIOutput;
import jakarta.faces.component.UIPanel;
import jakarta.faces.context.FacesContext;
import jakarta.faces.render.Renderer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrimeRendererWrapperTest {

    /**
     * A component which counts how often it was cleaned up, so that a test can tell that it happened exactly once.
     */
    private static class CleanupAwareComponent extends UIPanel implements IterationCleanupAware {

        private int cleanups;

        @Override
        public void cleanupIterationState(FacesContext context) {
            cleanups++;
        }
    }

    /**
     * A component whose own cleanup blows up, the way a table's does when the value expression which already failed
     * the render fails again while the cleanup walks its children.
     */
    private static class ThrowingCleanupComponent extends UIPanel implements IterationCleanupAware {

        @Override
        public void cleanupIterationState(FacesContext context) {
            throw new IllegalStateException("cleanup blew up");
        }
    }

    private static class CountingRenderer extends CoreRenderer<UIComponent> {

        private int decodings;
        private int encodings;

        @Override
        public void decode(FacesContext context, UIComponent component) {
            decodings++;
        }

        @Override
        public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
            encodings++;
        }
    }

    private static class ThrowingRenderer extends CoreRenderer<UIComponent> {

        @Override
        public void decode(FacesContext context, UIComponent component) {
            throw new IllegalStateException("boom");
        }

        @Override
        public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
            throw new IOException("boom");
        }
    }

    /**
     * A decode can leave iteration state behind too. The instant selection branch of the Tree and TreeTable selection
     * decode stands the component on the node which was checked and does not reset it.
     */
    @Test
    void decodeDecodesAndThenCleansUp() {
        FacesContext context = new FacesContextMock();
        CleanupAwareComponent component = new CleanupAwareComponent();
        CountingRenderer renderer = new CountingRenderer();

        Renderer wrapper = new PrimeRendererWrapper(renderer);
        wrapper.decode(context, component);

        assertEquals(1, renderer.decodings);
        assertEquals(1, component.cleanups);
    }

    @Test
    void decodeCleansUpWhenTheRendererThrew() {
        FacesContext context = new FacesContextMock();
        CleanupAwareComponent component = new CleanupAwareComponent();

        Renderer wrapper = new PrimeRendererWrapper(new ThrowingRenderer());
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> wrapper.decode(context, component));

        assertEquals("boom", thrown.getMessage());
        assertEquals(1, component.cleanups);
    }

    @Test
    void encodeEndEncodesAndThenCleansUp() throws Exception {
        FacesContext context = new FacesContextMock();
        CleanupAwareComponent component = new CleanupAwareComponent();
        CountingRenderer renderer = new CountingRenderer();

        Renderer wrapper = new PrimeRendererWrapper(renderer);
        wrapper.encodeEnd(context, component);

        assertEquals(1, renderer.encodings);
        assertEquals(1, component.cleanups);
    }

    /**
     * The state a renderer leaves behind is at its worst when the render blew up halfway, so the cleanup has to run
     * even then, and the original exception has to survive it.
     */
    @Test
    void encodeEndCleansUpWhenTheRendererThrew() {
        FacesContext context = new FacesContextMock();
        CleanupAwareComponent component = new CleanupAwareComponent();

        Renderer wrapper = new PrimeRendererWrapper(new ThrowingRenderer());
        IOException thrown = assertThrows(IOException.class, () -> wrapper.encodeEnd(context, component));

        assertEquals("boom", thrown.getMessage());
        assertEquals(1, component.cleanups);
    }

    /**
     * The render is the interesting one to debug, so its exception has to reach the user. A cleanup which fails in its
     * own right rides along as a suppressed exception instead of replacing it.
     */
    @Test
    void encodeEndKeepsTheRenderExceptionWhenTheCleanupBlowsUpToo() {
        FacesContext context = new FacesContextMock();

        Renderer wrapper = new PrimeRendererWrapper(new ThrowingRenderer());
        IOException thrown = assertThrows(IOException.class, () -> wrapper.encodeEnd(context, new ThrowingCleanupComponent()));

        assertEquals("boom", thrown.getMessage());
        assertEquals(1, thrown.getSuppressed().length);
        assertEquals("cleanup blew up", thrown.getSuppressed()[0].getMessage());
    }

    @Test
    void encodeEndReportsTheCleanupFailureWhenTheRenderItselfWasFine() {
        FacesContext context = new FacesContextMock();

        Renderer wrapper = new PrimeRendererWrapper(new CountingRenderer());
        IllegalStateException thrown =
                assertThrows(IllegalStateException.class, () -> wrapper.encodeEnd(context, new ThrowingCleanupComponent()));

        assertEquals("cleanup blew up", thrown.getMessage());
    }

    @Test
    void decodeKeepsTheDecodeExceptionWhenTheCleanupBlowsUpToo() {
        FacesContext context = new FacesContextMock();

        Renderer wrapper = new PrimeRendererWrapper(new ThrowingRenderer());
        IllegalStateException thrown =
                assertThrows(IllegalStateException.class, () -> wrapper.decode(context, new ThrowingCleanupComponent()));

        assertEquals("boom", thrown.getMessage());
        assertEquals(1, thrown.getSuppressed().length);
        assertEquals("cleanup blew up", thrown.getSuppressed()[0].getMessage());
    }

    @Test
    void encodeEndOfAComponentWhichNeedsNoCleanupJustDelegates() throws Exception {
        FacesContext context = new FacesContextMock();
        CountingRenderer renderer = new CountingRenderer();

        Renderer wrapper = new PrimeRendererWrapper(renderer);
        wrapper.encodeEnd(context, new UIOutput());

        assertEquals(1, renderer.encodings);
    }
}

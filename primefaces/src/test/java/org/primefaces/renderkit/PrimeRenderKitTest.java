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

import org.primefaces.mock.RendererMock;

import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.ResponseStream;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.RenderKit;
import jakarta.faces.render.Renderer;
import jakarta.faces.render.ResponseStateManager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class PrimeRenderKitTest {

    private static class RenderKitStub extends RenderKit {

        private final Map<String, Renderer> renderers = new HashMap<>();

        @Override
        public void addRenderer(String family, String rendererType, Renderer renderer) {
            renderers.put(family + rendererType, renderer);
        }

        @Override
        public Renderer getRenderer(String family, String rendererType) {
            return renderers.get(family + rendererType);
        }

        @Override
        public ResponseStateManager getResponseStateManager() {
            return null;
        }

        @Override
        public ResponseWriter createResponseWriter(Writer writer, String contentTypeList, String characterEncoding) {
            return null;
        }

        @Override
        public ResponseStream createResponseStream(OutputStream out) {
            return null;
        }
    }

    @Test
    void wrapsAPrimeFacesRenderer() {
        RenderKitStub wrapped = new RenderKitStub();
        Renderer renderer = new CoreRenderer<UIComponent>() { };
        wrapped.addRenderer("family", "type", renderer);

        PrimeRenderKit renderKit = new PrimeRenderKit(wrapped);

        assertInstanceOf(PrimeRendererWrapper.class, renderKit.getRenderer("family", "type"));
        assertSame(renderer, ((PrimeRendererWrapper) renderKit.getRenderer("family", "type")).getWrapped());
    }

    /**
     * Renderers of the Faces implementation and of other component libraries are handed out untouched, so that code
     * which casts them to their own type keeps working.
     */
    @Test
    void handsOutAForeignRendererUntouched() {
        RenderKitStub wrapped = new RenderKitStub();
        Renderer renderer = new RendererMock();
        wrapped.addRenderer("family", "type", renderer);

        PrimeRenderKit renderKit = new PrimeRenderKit(wrapped);

        assertSame(renderer, renderKit.getRenderer("family", "type"));
    }

    @Test
    void handsOutTheSameWrapperEveryTime() {
        RenderKitStub wrapped = new RenderKitStub();
        wrapped.addRenderer("family", "type", new CoreRenderer<UIComponent>() { });

        PrimeRenderKit renderKit = new PrimeRenderKit(wrapped);

        assertSame(renderKit.getRenderer("family", "type"), renderKit.getRenderer("family", "type"));
    }

    @Test
    void wrapsTheReplacementAfterAddRenderer() {
        RenderKitStub wrapped = new RenderKitStub();
        wrapped.addRenderer("family", "type", new CoreRenderer<UIComponent>() { });

        PrimeRenderKit renderKit = new PrimeRenderKit(wrapped);
        renderKit.getRenderer("family", "type");

        Renderer replacement = new CoreRenderer<UIComponent>() { };
        renderKit.addRenderer("family", "type", replacement);

        assertSame(replacement, ((PrimeRendererWrapper) renderKit.getRenderer("family", "type")).getWrapped());
    }

    @Test
    void handsOutNullForAnUnknownRenderer() {
        PrimeRenderKit renderKit = new PrimeRenderKit(new RenderKitStub());

        assertNull(renderKit.getRenderer("family", "type"));
    }
}

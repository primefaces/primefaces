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
package org.primefaces.selenium.internal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.primefaces.selenium.spi.PrimeSeleniumAdapter;

public class ConfigProvider {

    private static ConfigProvider configProvider = null;

    private int guiTimeout = 2;
    private int ajaxTimeout = 10;
    private int httpTimeout = 10;
    private int documentLoadTimeout = 15;

    private boolean disableAnimations = true;
    private PrimeSeleniumAdapter adapter;
    private List<String> onloadScripts;

    public ConfigProvider() {
        try {
            InputStream config = getClass().getResourceAsStream("/primefaces-selenium/config.properties");
            if (config != null) {
                Properties properties = new Properties();
                properties.load(config);

                String guiTimeout = properties.getProperty("guiTimeout");
                if (guiTimeout != null && !guiTimeout.trim().isEmpty()) {
                    this.guiTimeout = Integer.parseInt(guiTimeout);
                }

                String ajaxTimeout = properties.getProperty("ajaxTimeout");
                if (ajaxTimeout != null && !ajaxTimeout.trim().isEmpty()) {
                    this.ajaxTimeout = Integer.parseInt(ajaxTimeout);
                }

                String httpTimeout = properties.getProperty("httpTimeout");
                if (httpTimeout != null && !httpTimeout.trim().isEmpty()) {
                    this.httpTimeout = Integer.parseInt(httpTimeout);
                }

                String documentLoadTimeout = properties.getProperty("documentLoadTimeout");
                if (documentLoadTimeout != null && !documentLoadTimeout.trim().isEmpty()) {
                    this.documentLoadTimeout = Integer.parseInt(documentLoadTimeout);
                }

                String disableAnimations = properties.getProperty("disableAnimations");
                if (disableAnimations != null && !disableAnimations.trim().isEmpty()) {
                    this.disableAnimations = Boolean.parseBoolean(disableAnimations);
                }

                String adapter = properties.getProperty("adapter");
                if (adapter != null && !adapter.trim().isEmpty()) {
                    this.adapter = (PrimeSeleniumAdapter) Class.forName(adapter).getDeclaredConstructor().newInstance();
                }
            }

            if (adapter == null) {
                throw new RuntimeException("No 'adapter' set via config.properties!");
            }

            buildOnloadScripts();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void buildOnloadScripts() throws Exception {
        onloadScripts = new ArrayList<>();
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream("/primefaces-selenium/onload.js"),
                    StandardCharsets.UTF_8))) {
            onloadScripts.add(buffer.lines().collect(Collectors.joining("\n")));
        }

        if (disableAnimations) {
            onloadScripts.add("if (window.PrimeFaces) { $(function() { PrimeFaces.utils.disableAnimations(); }); }");
        }

        adapter.registerOnloadScripts(onloadScripts);
    }

    public int getGuiTimeout() {
        return guiTimeout;
    }

    public int getAjaxTimeout() {
        return ajaxTimeout;
    }

    public int getHttpTimeout() {
        return httpTimeout;
    }

    public int getDocumentLoadTimeout() {
        return documentLoadTimeout;
    }

    public boolean isDisableAnimations() {
        return disableAnimations;
    }

    public PrimeSeleniumAdapter getAdapter() {
        return adapter;
    }

    public List<String> getOnloadScripts() {
        return onloadScripts;
    }

    public static synchronized ConfigProvider getInstance() {
        if (configProvider == null) {
            configProvider = new ConfigProvider();
        }

        return configProvider;
    }
}

/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.selenium.spi.DeploymentAdapter;
import org.primefaces.selenium.spi.OnloadScriptsAdapter;
import org.primefaces.selenium.spi.WebDriverAdapter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfigProvider {

    public static final String DEPLOYMENT_ADAPTER = "deployment.adapter";
    public static final String DEPLOYMENT_BASEURL = "deployment.baseUrl";

    private static ConfigProvider configProvider = null;

    private static final Pattern PROPERTY_ENV_PLACEHOLDER = Pattern.compile("\\$\\{(.*?)}");

    private int timeoutGui = 2;
    private int timeoutAjax = 10;
    private int timeoutHttp = 10;
    private int timeoutDocumentLoad = 15;
    private int timeoutFileUpload = 20;

    private String scrollElementIntoView;

    private boolean disableAnimations = true;

    private WebDriverAdapter webdriverAdapter;
    private String webdriverBrowser;
    private boolean webdriverHeadless = false;
    private String webdriverVersion;
    private Level webdriverLogLevel = Level.WARNING;

    private String deploymentBaseUrl;
    private DeploymentAdapter deploymentAdapter;

    private OnloadScriptsAdapter onloadScriptsAdapter;

    private List<String> onloadScripts;

    private String screenshotDirectory;

    public ConfigProvider() {
        try {
            InputStream config = getClass().getResourceAsStream("/primefaces-selenium/config.properties");
            if (config != null) {
                Properties properties = new Properties();
                properties.load(config);

                String timeoutGui = getAndResolveProperty(properties, "timeout.gui");
                if (timeoutGui != null && !timeoutGui.trim().isEmpty()) {
                    this.timeoutGui = Integer.parseInt(timeoutGui);
                }

                String timeoutAjax = getAndResolveProperty(properties, "timeout.ajax");
                if (timeoutAjax != null && !timeoutAjax.trim().isEmpty()) {
                    this.timeoutAjax = Integer.parseInt(timeoutAjax);
                }

                String timeoutHttp = getAndResolveProperty(properties, "timeout.http");
                if (timeoutHttp != null && !timeoutHttp.trim().isEmpty()) {
                    this.timeoutHttp = Integer.parseInt(timeoutHttp);
                }

                String timeoutDocumentLoad = getAndResolveProperty(properties, "timeout.documentLoad");
                if (timeoutDocumentLoad != null && !timeoutDocumentLoad.trim().isEmpty()) {
                    this.timeoutDocumentLoad = Integer.parseInt(timeoutDocumentLoad);
                }

                String timeoutFileUpload = getAndResolveProperty(properties, "timeout.fileUpload");
                if (timeoutFileUpload != null && !timeoutFileUpload.trim().isEmpty()) {
                    this.timeoutFileUpload = Integer.parseInt(timeoutFileUpload);
                }

                String disableAnimations = getAndResolveProperty(properties, "disableAnimations");
                if (disableAnimations != null && !disableAnimations.trim().isEmpty()) {
                    this.disableAnimations = Boolean.parseBoolean(disableAnimations);
                }

                String scrollElementIntoView = getAndResolveProperty(properties, "scrollElementIntoView");
                if (scrollElementIntoView != null && !scrollElementIntoView.trim().isEmpty()) {
                    this.scrollElementIntoView = scrollElementIntoView;
                }

                String deploymentBaseUrl = getAndResolveProperty(properties, DEPLOYMENT_BASEURL);
                if (deploymentBaseUrl != null && !deploymentBaseUrl.trim().isEmpty()) {
                    this.deploymentBaseUrl = deploymentBaseUrl;
                }

                String deploymentAdapter = getAndResolveProperty(properties, DEPLOYMENT_ADAPTER);
                if (deploymentAdapter != null && !deploymentAdapter.trim().isEmpty()) {
                    this.deploymentAdapter = (DeploymentAdapter) Class.forName(deploymentAdapter).getDeclaredConstructor().newInstance();
                }

                String webdriverAdapter = getAndResolveProperty(properties, "webdriver.adapter");
                if (webdriverAdapter != null && !webdriverAdapter.trim().isEmpty()) {
                    this.webdriverAdapter = (WebDriverAdapter) Class.forName(webdriverAdapter).getDeclaredConstructor().newInstance();
                }

                String webdriverBrowser = getAndResolveProperty(properties, "webdriver.browser");
                if (webdriverBrowser != null && !webdriverBrowser.trim().isEmpty()) {
                    this.webdriverBrowser = webdriverBrowser;
                }

                String webdriverHeadless = getAndResolveProperty(properties, "webdriver.headless");
                if (webdriverHeadless != null && !webdriverHeadless.trim().isEmpty()) {
                    this.webdriverHeadless = Boolean.parseBoolean(webdriverHeadless);
                }

                String webdriverVersion = getAndResolveProperty(properties, "webdriver.version");
                if (webdriverVersion != null && !webdriverVersion.trim().isEmpty()) {
                    this.webdriverVersion = webdriverVersion;
                }

                String webdriverLogLevel = getAndResolveProperty(properties, "webdriver.logLevel");
                if (webdriverLogLevel != null && !webdriverLogLevel.trim().isEmpty()) {
                    this.webdriverLogLevel = Level.parse(webdriverLogLevel);
                }

                String onloadScriptsAdapter = getAndResolveProperty(properties, "onloadScripts.adapter");
                if (onloadScriptsAdapter != null && !onloadScriptsAdapter.trim().isEmpty()) {
                    this.onloadScriptsAdapter = (OnloadScriptsAdapter) Class.forName(onloadScriptsAdapter).getDeclaredConstructor().newInstance();
                }

                String screenshotDirectory = getAndResolveProperty(properties, "screenshotDirectory");
                if (screenshotDirectory != null && !screenshotDirectory.trim().isEmpty() && !"${SCREENSHOT_DIRECTORY}".equalsIgnoreCase(screenshotDirectory)) {
                    this.screenshotDirectory = screenshotDirectory;
                }
            }

            if (webdriverAdapter == null) {
                webdriverAdapter = new DefaultWebDriverAdapter();
            }

            if (deploymentBaseUrl == null && deploymentAdapter == null) {
                throw new RuntimeException("Please either provide a " + DEPLOYMENT_BASEURL + " for remote testing or "
                        + DEPLOYMENT_ADAPTER + " via config.properties!");
            }

            buildOnloadScripts();

            webdriverAdapter.initialize(this);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getAndResolveProperty(Properties configFileProperties, String propertyKey) {
        String val = configFileProperties.getProperty(propertyKey);
        String effectiveKey = propertyKey;
        if (val != null) {
            Matcher matcher = PROPERTY_ENV_PLACEHOLDER.matcher(val);
            if (matcher.matches()) {
                effectiveKey = matcher.group(1);
            }
        }
        String valueCandidate = System.getProperty(effectiveKey);
        if (valueCandidate != null && !valueCandidate.trim().isEmpty()) {
            return valueCandidate;
        }
        valueCandidate = System.getenv(effectiveKey);
        if (valueCandidate != null && !valueCandidate.trim().isEmpty()) {
            return valueCandidate;
        }
        return val;
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
            // CHECKSTYLE:OFF
            onloadScripts.add("document.head.insertAdjacentHTML('beforeend', '<style>*, *:before, *:after { -webkit-transition: none !important; -moz-transition: none !important; -ms-transition: none !important; -o-transition: none !important; transition: none !important; }</style>');");
            // CHECKSTYLE:ON
        }

        if (scrollElementIntoView != null) {
            onloadScripts.add("if (window.PrimeFaces) { $(function() { PrimeFaces.hideOverlaysOnViewportChange = false; }); }");
        }

        if (onloadScriptsAdapter != null) {
            onloadScriptsAdapter.registerOnloadScripts(onloadScripts);
        }
    }

    public int getTimeoutGui() {
        return timeoutGui;
    }

    public int getTimeoutAjax() {
        return timeoutAjax;
    }

    public void setTimeoutAjax(int timeoutAjax) {
        this.timeoutAjax = timeoutAjax;
    }

    public int getTimeoutHttp() {
        return timeoutHttp;
    }

    public int getTimeoutDocumentLoad() {
        return timeoutDocumentLoad;
    }

    public int getTimeoutFileUpload() {
        return timeoutFileUpload;
    }

    public String getScrollElementIntoView() {
        return scrollElementIntoView;
    }

    public boolean isDisableAnimations() {
        return disableAnimations;
    }

    public String getDeploymentBaseUrl() {
        return deploymentBaseUrl;
    }

    public DeploymentAdapter getDeploymentAdapter() {
        return deploymentAdapter;
    }

    public List<String> getOnloadScripts() {
        return onloadScripts;
    }

    public WebDriverAdapter getWebdriverAdapter() {
        return webdriverAdapter;
    }

    public OnloadScriptsAdapter getOnloadScriptsAdapter() {
        return onloadScriptsAdapter;
    }

    public String getWebdriverBrowser() {
        return webdriverBrowser;
    }

    public boolean isWebdriverHeadless() {
        return webdriverHeadless;
    }

    public String getWebdriverVersion() {
        return webdriverVersion;
    }

    public Level getWebdriverLogLevel() {
        return webdriverLogLevel;
    }

    public String getScreenshotDirectory() {
        return screenshotDirectory;
    }

    public static synchronized ConfigProvider getInstance() {
        if (configProvider == null) {
            configProvider = new ConfigProvider();
        }

        return configProvider;
    }

}

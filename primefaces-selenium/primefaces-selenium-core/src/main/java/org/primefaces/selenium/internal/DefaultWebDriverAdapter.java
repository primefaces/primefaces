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

import org.primefaces.selenium.spi.WebDriverAdapter;

import java.util.HashMap;
import java.util.Map;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

public class DefaultWebDriverAdapter implements WebDriverAdapter {

    @Override
    public void initialize(ConfigProvider configProvider) {
        WebDriverManager webDriverManager = null;
        if ("firefox".equals(configProvider.getWebdriverBrowser())) {
            if (!System.getProperties().contains("webdriver.gecko.driver")) {
                webDriverManager = WebDriverManager.firefoxdriver();
            }
        }
        else if ("chrome".equals(configProvider.getWebdriverBrowser())) {
            if (!System.getProperties().contains("webdriver.chrome.driver")) {
                webDriverManager = WebDriverManager.chromedriver();

                // uncomment if you need to clear cache getting Chrome mismatch errors
                //webDriverManager.clearDriverCache();
                //webDriverManager.clearResolutionCache();
            }
        }
        else if ("safari".equals(configProvider.getWebdriverBrowser())) {
            if (!System.getProperties().contains("webdriver.safari.driver")) {
                webDriverManager = WebDriverManager.safaridriver();
            }
        }

        if (webDriverManager != null) {
            if (configProvider.getWebdriverVersion() != null) {
                webDriverManager = webDriverManager.driverVersion(configProvider.getWebdriverVersion());
            }

            webDriverManager.setup();
        }
    }

    @Override
    public WebDriver createWebDriver() {
        ConfigProvider config = ConfigProvider.getInstance();
        if (config.getWebdriverBrowser() == null) {
            throw new RuntimeException("No webdriver.browser configured; Please either configure it or implement WebDriverAdapter#getWebDriver!");
        }

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, config.getWebdriverLogLevel());

        switch (config.getWebdriverBrowser()) {
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions().configureFromEnv();
                firefoxOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                if (config.isWebdriverHeadless()) {
                    firefoxOptions.addArguments("-headless");
                }
                firefoxOptions.setLogLevel(FirefoxDriverLogLevel.fromLevel(config.getWebdriverLogLevel()));
                firefoxOptions.addPreference("browser.helperApps.neverAsk.openFile", "application/octet-stream");
                return new FirefoxDriver(firefoxOptions);
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                if (config.isWebdriverHeadless()) {
                    chromeOptions.addArguments("--headless=new");
                    // Chrome 128 /129 - see https://stackoverflow.com/questions/78996364/chrome-129-headless-shows-blank-window,
                    // https://issues.chromium.org/issues/359921643
                    // Should be fixed for Chrome 130
                }
                chromeOptions.setCapability(ChromeOptions.LOGGING_PREFS, logPrefs);

                // Chrome 111 workaround: https://github.com/SeleniumHQ/selenium/issues/11750
                chromeOptions.addArguments("--remote-allow-origins=*");

                Map<String, Object> chromePrefs = new HashMap<>();
                chromePrefs.put("download.prompt_for_download", false);
                chromePrefs.put("download.directory_upgrade", true);
                chromePrefs.put("safebrowsing.enabled", true);
                chromePrefs.put("profile.default_content_settings.popups", 0);
                chromePrefs.put("download.default_directory", System.getProperty("java.io.tmpdir"));
                chromeOptions.setExperimentalOption("prefs", chromePrefs);
                return new ChromeDriver(chromeOptions);
            case "safari":
                SafariOptions safariOptions = new SafariOptions();
                /*
                 * Safari does not support headless as of september 2020
                 * https://github.com/SeleniumHQ/selenium/issues/5985
                 * https://discussions.apple.com/thread/251837694
                 */
                //safariOptions.setHeadless(config.isDriverHeadless());
                safariOptions.setCapability("safari:diagnose", "true");
                return new SafariDriver(safariOptions);
        }

        throw new RuntimeException("Current supported browsers are: safari, firefox, chrome");
    }

}

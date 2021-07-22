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
package org.primefaces.integrationtests;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Random;

import org.apache.tomee.embedded.Configuration;
import org.apache.tomee.embedded.Container;
import org.openqa.selenium.WebDriver;
import org.primefaces.selenium.spi.PrimeSeleniumAdapter;

public abstract class PrimeFacesSeleniumTomEEAdapter implements PrimeSeleniumAdapter {

    protected static final String HEADLESS_MODE_SYSPROP_NAME = "webdriver.headless";

    protected static final String HEADLESS_MODE_SYSPROP_VAL_DEFAULT = "false";

    private Container container;

    @Override
    public abstract WebDriver createWebDriver();

    @Override
    public void startup() throws Exception {
        Configuration config = new Configuration();
        config.setHttpPort(createRandomPort());
        config.setQuickSession(true);
        config.setStopPort(createRandomPort());
        config.setDir("target/tomee");

        Properties properties = new Properties();
        properties.put("org.apache.tomee.loader.TomEEJarScanner.scanClassPath", "false");
        properties.put("org.apache.tomee.loader.TomEEJarScanner.scanBootstrapClassPath", "false");
        config.setProperties(properties);

        File targetDir = new File("target/");
        String[] warFiles = targetDir.list((dir, name) -> name.endsWith(".war"));
        if (warFiles == null || warFiles.length == 0) {
            throw new RuntimeException("No WAR found in target; please build before ;)");
        }
        String warName = warFiles[0];
        File warFile = new File(targetDir, warName);

        container = new Container(config);
        container.deploy(
                    "ROOT",
                    warFile,
                    true);

        Thread.sleep(1000);

        // trigger jsf lazy startup
        URL url = new URL(getBaseUrl());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.disconnect();

        Thread.sleep(2000);
    }

    @Override
    public String getBaseUrl() {
        return "http://localhost:" + container.getConfiguration().getHttpPort() + "/";
    }

    @Override
    public void shutdown() {
        container.close();
    }

    private static int createRandomPort() {
        Random random = new Random();
        return random.nextInt((9000 - 8000) + 1) + 8000;
    }

    public Container getContainer() {
        return container;
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(System.getProperty(HEADLESS_MODE_SYSPROP_NAME, HEADLESS_MODE_SYSPROP_VAL_DEFAULT));
    }

}

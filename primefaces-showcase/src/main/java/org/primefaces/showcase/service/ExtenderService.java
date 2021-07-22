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
package org.primefaces.showcase.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.primefaces.component.schedule.Schedule;

/**
 * Provides the examples for the [@code extender} options of various components,
 * such as the {@link Schedule}.
 */
@Named
@ApplicationScoped
public class ExtenderService {

    public Map<String, ExtenderExample> createExtenderExamples() {
        Properties properties = new Properties();

        try (InputStream inStream = ExtenderService.class.getResourceAsStream("/schedule-extender-examples.properties")) {
            properties.load(inStream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, ExtenderExample> extenderExamples = new HashMap<>();

        for (String key : properties.stringPropertyNames()) {
            if (key != null && key.endsWith(".name")) {
                String baseKey = key.substring(0, key.length() - 5);
                ExtenderExample example = new ExtenderExample(baseKey, properties);
                if (example.getName() != null && example.getValue() != null && !example.getName().trim().isEmpty()
                        && !example.getValue().trim().isEmpty()) {
                    extenderExamples.put(baseKey, example);
                }
            }
        }

        return extenderExamples;
    }

    public static class ExtenderExample {
        private String details;
        private String html;
        private String key;
        private String link;
        private String name;
        private String value;

        public ExtenderExample(String key, Properties properties) {
            this.key = key;
            this.details = properties.getProperty(key + ".details");
            this.html = properties.getProperty(key + ".html");
            this.link = properties.getProperty(key + ".link");
            this.name = properties.getProperty(key + ".name");
            this.value = properties.getProperty(key + ".value");
        }

        public String getDetails() {
            return details;
        }

        public String getHtml() {
            return html;
        }

        public String getKey() {
            return key;
        }

        public String getLink() {
            return link;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}

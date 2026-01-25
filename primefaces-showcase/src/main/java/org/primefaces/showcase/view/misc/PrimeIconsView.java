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
package org.primefaces.showcase.view.misc;

import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONException;
import org.primefaces.shaded.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class PrimeIconsView implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Icon> iconsPrevious;
    private List<Icon> icons;

    @PostConstruct
    public void init() {
        iconsPrevious = getIcons("https://raw.githubusercontent.com/primefaces/primeicons/6.0.1/selection.json");
        icons = getIcons("https://raw.githubusercontent.com/primefaces/primeicons/7.0.0/selection.json");
        icons.sort(Comparator.comparing(Icon::getName));
    }

    private List<Icon> getIcons(String url) {
        List<Icon> result = new ArrayList<>();
        try {
            JSONObject json = readJsonFromUrl(url);
            JSONArray iconsArray = json.getJSONArray("icons");
            for (int i = 0; i < iconsArray.length(); i++) {
                JSONObject properties = iconsArray.optJSONObject(i).getJSONObject("properties");
                result.add(new Icon(properties.getString("name"), properties.getInt("code")));
            }
        }
        catch (IOException | JSONException ex) {
            Logger.getLogger(PrimeIconsView.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    public List<Icon> getIcons() {
        return icons;
    }

    public boolean isNew(Icon icon) {
        return !iconsPrevious.contains(icon);
    }

    public static class Icon {

        private String name;
        private int key;

        public Icon(String name, int key) {
            this.name = name;
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Icon other = (Icon) obj;
            return this.key == other.key;
        }
    }
}

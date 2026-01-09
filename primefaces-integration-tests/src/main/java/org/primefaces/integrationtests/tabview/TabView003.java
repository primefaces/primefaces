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
package org.primefaces.integrationtests.tabview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
@Data
public class TabView003 implements Serializable {

    private int setterCalled;
    private List<ViewModel> data;

    @PostConstruct
    public void init() {
        setterCalled = 0;
        data = new ArrayList<>();
        data.add(new ViewModel("Tab A", "Text A", true));
        data.add(new ViewModel("Tab B", "Text B", true));
        data.add(new ViewModel("Tab C", "Text C", true));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ViewModel implements Serializable {

        private String title;
        private String text;
        private Boolean sob;

        public void setSob(Boolean sob) {
            this.sob = sob;

            TabView003 controller = CDI.current().select(TabView003.class).get();
            controller.setSetterCalled(controller.getSetterCalled() + 1);
        }
    }

}

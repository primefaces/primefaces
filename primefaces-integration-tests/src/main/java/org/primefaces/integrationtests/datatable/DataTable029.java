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
package org.primefaces.integrationtests.datatable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class DataTable029 implements Serializable {

    private static final long serialVersionUID = -9070796086139839567L;

    public static class Data implements Serializable {

        private String text;
        private int num;

        public Data() {

        }

        public Data(String text, int num) {
            this.text = text;
            this.num = num;
        }

        public String getText() {
            return text;
        }

        public int getNum() {
            return num;
        }
    }

    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    @PostConstruct
    public void refresh() {
        List<Data> newData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            // Generate random letter and number to test sorting
            newData.add(new Data("Data " + ((char) ((int) 'A' + ThreadLocalRandom.current().nextInt(26))),
                    ThreadLocalRandom.current().nextInt(100)));
        }

        this.data = newData;
    }

}

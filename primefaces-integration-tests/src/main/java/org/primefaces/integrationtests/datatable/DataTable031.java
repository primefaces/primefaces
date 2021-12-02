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
package org.primefaces.integrationtests.datatable;

import lombok.Data;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
@Data
public class DataTable031 implements Serializable {

    private List<String> listStringData;
    private int viewState;

    private List<String> getData1() {
        List<String> t = new ArrayList<>();
        t.add("Data 1 1");
        t.add("Data 1 2");
        t.add("Data 1 3");
        t.add("Data 1 4");

        return t;
    }

    private List<String> getData2() {
        List<String> t = new ArrayList<>();
        t.add("Data 2 1");
        t.add("Data 2 2");
        t.add("Data 2 3");
        t.add("Data 2 4");

        return t;
    }

    public void toggleList() {
        if (getViewState() == 0) {
            setListStringData(getData1());
            setViewState(1);
        }
        else {
            setListStringData(getData2());
            setViewState(0);
        }
    }
}

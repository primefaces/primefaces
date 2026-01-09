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
package org.primefaces.integrationtests.selectcheckboxmenu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Named
@ViewScoped
@Data
public class SelectChecboxMenu002 implements Serializable {

    private static final long serialVersionUID = -7518459955779385834L;

    private List<TestObject> list;
    private List<TestObject> selection;

    @PostConstruct
    public void init() {
        list = new ArrayList<>(Arrays.asList(
                new TestObject("1", "", "Michael Jackson", 1982),
                new TestObject("2", "Back in Black", "AC/DC", 1980),
                new TestObject("3", "The Bodyguard", "Whitney Houston", 1992),
                new TestObject("4", "The Dark Side of the Moon", "Pink Floyd", 1973)
        ));
        selection = new ArrayList<>();
    }

    public List<SelectItem> getSelectItems() {

        List<SelectItem> selectItemsList = new ArrayList<>(list.size());

        for (final TestObject item : list) {

            SelectItem selectItem = new SelectItem(item.getId(), item.getName());
            selectItemsList.add(selectItem);
        }

        return selectItemsList;
    }

    @Data
    @EqualsAndHashCode
    public static class TestObject implements Serializable {
        private String id;
        private String name;
        private String artist;
        private Integer released;

        public TestObject(String id, String name, String artist, Integer released) {
            this.id = id;
            this.name = name;
            this.artist = artist;
            this.released = released;
        }

    }

}

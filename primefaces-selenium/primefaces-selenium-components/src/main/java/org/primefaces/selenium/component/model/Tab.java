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
package org.primefaces.selenium.component.model;

import java.util.Objects;
import java.util.StringJoiner;

import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:tab}.
 */
public class Tab {

    private String title;
    private Integer index;
    private WebElement header;
    private WebElement content;

    public Tab(String title, Integer index, WebElement header, WebElement content) {
        this.title = title;
        this.index = index;
        this.header = header;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public WebElement getHeader() {
        return header;
    }

    public void setHeader(WebElement header) {
        this.header = header;
    }

    public WebElement getContent() {
        return content;
    }

    public void setContent(WebElement content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tab)) {
            return false;
        }
        Tab tab = (Tab) o;
        return Objects.equals(getTitle(), tab.getTitle()) &&
                    Objects.equals(getIndex(), tab.getIndex());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getIndex());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Tab.class.getSimpleName() + "[", "]")
                    .add("title='" + title + "'")
                    .add("index=" + index)
                    .toString();
    }

}

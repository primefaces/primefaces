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
package org.primefaces.showcase.domain;

import java.io.Serializable;

public class Sale implements Serializable {

    private String manufacturer;
    private int lastYearSale;
    private int thisYearSale;
    private int lastYearProfit;
    private int thisYearProfit;

    public Sale() {
    }

    public Sale(String manufacturer, int lastYearSale, int thisYearSale, int lastYearProfit, int thisYearProfit) {
        this.manufacturer = manufacturer;
        this.lastYearSale = lastYearSale;
        this.thisYearSale = thisYearSale;
        this.lastYearProfit = lastYearProfit;
        this.thisYearProfit = thisYearProfit;
    }

    public int getLastYearProfit() {
        return lastYearProfit;
    }

    public void setLastYearProfit(int lastYearProfit) {
        this.lastYearProfit = lastYearProfit;
    }

    public int getLastYearSale() {
        return lastYearSale;
    }

    public void setLastYearSale(int lastYearSale) {
        this.lastYearSale = lastYearSale;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getThisYearProfit() {
        return thisYearProfit;
    }

    public void setThisYearProfit(int thisYearProfit) {
        this.thisYearProfit = thisYearProfit;
    }

    public int getThisYearSale() {
        return thisYearSale;
    }

    public void setThisYearSale(int thisYearSale) {
        this.thisYearSale = thisYearSale;
    }
}

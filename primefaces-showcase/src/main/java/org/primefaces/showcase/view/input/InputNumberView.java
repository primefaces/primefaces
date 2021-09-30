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
package org.primefaces.showcase.view.input;

import javax.faces.view.ViewScoped;

import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;

@Named
@ViewScoped
public class InputNumberView implements Serializable {

    private Double input1 = Double.valueOf(0);
    private Double input2 = Double.valueOf(0);
    private Double input3 = Double.valueOf(0);
    private Double input4 = Double.valueOf(0);
    private Double input5 = Double.valueOf(0);
    private Double input6 = Double.valueOf(0);
    private Double input7 = null;
    private BigDecimal input8 = BigDecimal.valueOf(0);

    public InputNumberView() {
        input1 = 0d;
        input2 = 0d;
        input3 = 0d;
        input4 = 0d;
        input5 = 251.31;
        input6 = 60d;
        input8 = new BigDecimal("1234.000000001");
    }

    public Double getInput1() {
        return input1;
    }

    public void setInput1(Double input1) {
        this.input1 = input1;
    }

    public Double getInput2() {
        return input2;
    }

    public void setInput2(Double input2) {
        this.input2 = input2;
    }

    public Double getInput3() {
        return input3;
    }

    public void setInput3(Double input3) {
        this.input3 = input3;
    }

    public Double getInput4() {
        return input4;
    }

    public void setInput4(Double input4) {
        this.input4 = input4;
    }

    public Double getInput5() {
        return input5;
    }

    public void setInput5(Double input5) {
        this.input5 = input5;
    }

    public Double getInput6() {
        return input6;
    }

    public void setInput6(Double input6) {
        this.input6 = input6;
    }

    public Double getInput7() {
        return input7;
    }

    public void setInput7(Double input7) {
        this.input7 = input7;
    }

    public BigDecimal getInput8() {
        return input8;
    }

    public void setInput8(BigDecimal input8) {
        this.input8 = input8;
    }
}

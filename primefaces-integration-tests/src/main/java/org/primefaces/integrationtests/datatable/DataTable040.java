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

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
@Data
public class DataTable040 implements Serializable {

    private List<Car> cars;
    private List<Car> cars2;

    @PostConstruct
    public void init() {
        cars = new ArrayList<>();
        cars.add(new Car(1, "Entry_1", 1000d));
        cars.add(new Car(2, "Entry_2", 1500d));
        cars.add(new Car(3, "Entry_3", 2000d));
        cars.add(new Car(4, "Entry_4", 2500d));
        cars.add(new Car(5, "Entry_5", 3000d));

        cars2 = new ArrayList<>();
        cars2.add(new Car(1, "Entry_1", 1000d));
        cars2.add(new Car(2, "Entry_2", 1500d));
        cars2.add(new Car(3, "Entry_3", 2000d));
        cars2.add(new Car(4, "Entry_4", 2500d));
        cars2.add(new Car(5, "Entry_5", 3000d));
    }

    @Getter
    @Setter
    public static class Car implements Serializable {

        private Integer number;
        private String name;
        private Double price;

        public Car() {
        }

        public Car(Integer number , String name, Double price) {
            this.number = number;
            this.name = name;
            this.price = price;
        }
    }
}
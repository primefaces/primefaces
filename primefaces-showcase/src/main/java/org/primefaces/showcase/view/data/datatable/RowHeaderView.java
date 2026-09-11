/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.showcase.view.data.datatable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

/**
 * Examples from https://www.w3.org/WAI/tutorials/tables/two-headers/
 * Demonstrates DataTable's rowHeader column attribute: Example 1 uses it on the first column
 * (time slots), Example 2 uses it on an offset column (Name, after ID) to show that any column
 * can act as the row header.
 */
@Named("dtRowHeaderView")
@ViewScoped
public class RowHeaderView implements Serializable {

    private List<DeliverySlot> slots;
    private List<HolidayRecord> holidays;

    @PostConstruct
    public void init() {
        slots = new ArrayList<>();
        slots.add(new DeliverySlot("09:00 – 11:00", "Closed", "Open", "Open", "Closed", "Closed"));
        slots.add(new DeliverySlot("11:00 – 13:00", "Open", "Open", "Closed", "Closed", "Closed"));
        slots.add(new DeliverySlot("13:00 – 15:00", "Open", "Open", "Open", "Closed", "Closed"));
        slots.add(new DeliverySlot("15:00 – 17:00", "Closed", "Closed", "Closed", "Open", "Open"));

        holidays = new ArrayList<>();
        holidays.add(new HolidayRecord(215, "Abel", 5, 2, 0, 0, 0, 3));
        holidays.add(new HolidayRecord(231, "Annette", 0, 5, 3, 0, 0, 6));
        holidays.add(new HolidayRecord(173, "Bernard", 2, 0, 0, 5, 0, 0));
        holidays.add(new HolidayRecord(141, "Gerald", 0, 10, 0, 0, 0, 8));
        holidays.add(new HolidayRecord(99, "Michael", 8, 8, 8, 8, 0, 4));
    }

    public List<DeliverySlot> getSlots() {
        return slots;
    }

    public List<HolidayRecord> getHolidays() {
        return holidays;
    }

    public static class DeliverySlot implements Serializable {

        private String time;
        private String monday;
        private String tuesday;
        private String wednesday;
        private String thursday;
        private String friday;

        public DeliverySlot() {
        }

        public DeliverySlot(String time, String monday, String tuesday, String wednesday, String thursday, String friday) {
            this.time = time;
            this.monday = monday;
            this.tuesday = tuesday;
            this.wednesday = wednesday;
            this.thursday = thursday;
            this.friday = friday;
        }

        public String getTime() {
            return time;
        }

        public String getMonday() {
            return monday;
        }

        public String getTuesday() {
            return tuesday;
        }

        public String getWednesday() {
            return wednesday;
        }

        public String getThursday() {
            return thursday;
        }

        public String getFriday() {
            return friday;
        }
    }

    public static class HolidayRecord implements Serializable {

        private int id;
        private String name;
        private int july;
        private int august;
        private int september;
        private int october;
        private int november;
        private int december;

        public HolidayRecord() {
        }

        public HolidayRecord(int id, String name, int july, int august, int september, int october, int november, int december) {
            this.id = id;
            this.name = name;
            this.july = july;
            this.august = august;
            this.september = september;
            this.october = october;
            this.november = november;
            this.december = december;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getJuly() {
            return july;
        }

        public int getAugust() {
            return august;
        }

        public int getSeptember() {
            return september;
        }

        public int getOctober() {
            return october;
        }

        public int getNovember() {
            return november;
        }

        public int getDecember() {
            return december;
        }
    }
}

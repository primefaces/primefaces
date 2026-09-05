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
package org.primefaces.integrationtests.timeline;

import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineGroup;
import org.primefaces.model.timeline.TimelineModel;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class Timeline004 implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private TimelineModel<String, String> model;

    @PostConstruct
    public void init() {
        model = new TimelineModel<>();

        model.addGroup(new TimelineGroup<>("releases", "Releases"));
        model.add(TimelineEvent.<String>builder().data("PrimeFaces 14").group("releases").startDate(LocalDate.of(2024, 4, 1)).build());
        model.add(TimelineEvent.<String>builder().data("PrimeFaces 15").group("releases").startDate(LocalDate.of(2025, 4, 1)).build());
        model.add(TimelineEvent.<String>builder().data("PrimeFaces 16").group("releases").startDate(LocalDate.of(2026, 4, 1)).build());
    }
}

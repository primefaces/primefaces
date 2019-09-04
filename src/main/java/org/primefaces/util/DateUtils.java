/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtils {

    /*
    Only used by Timeline-Component
    TODO: Maybe we should move this to CalendarUtils.
     */

    private DateUtils() {
    }

    /**
     * Convert ISO-String (@see <a href="https://developer.mozilla.org/de/docs/Web/JavaScript/Reference/Global_Objects/Date/toISOString">https://developer.mozilla.org/de/docs/Web/JavaScript/Reference/Global_Objects/Date/toISOString</a>)
     * to LocalDateTime.
     * @param isoDateString
     * @return
     */
    public static LocalDateTime toLocalDateTime(ZoneId zoneId, String isoDateString) {
        if (isoDateString == null) {
            return null;
        }

        Instant instant = Instant.parse(isoDateString);
        LocalDateTime result = LocalDateTime.ofInstant(instant, zoneId);
        return result;
    }
}

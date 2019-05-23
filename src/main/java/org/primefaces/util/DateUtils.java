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

import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    private DateUtils() {
    }

    // convert from local date to UTC
    public static Date toUtcDate(TimeZone browserTZ, TimeZone targetTZ, String localDate) {
        if (localDate == null) {
            return null;
        }

        return toUtcDate(browserTZ, targetTZ, Long.valueOf(localDate));
    }

    // convert from local date to UTC
    public static Date toUtcDate(TimeZone browserTZ, TimeZone targetTZ, long localDate) {
        return toUtcDate(browserTZ, targetTZ, new Date(localDate));
    }

    // convert from local date to UTC
    public static Date toUtcDate(TimeZone browserTZ, TimeZone targetTZ, Date localDate) {
        if (localDate == null) {
            return null;
        }

        long local = localDate.getTime();
        int targetOffsetFromUTC = targetTZ.getOffset(local);
        int browserOffsetFromUTC = browserTZ.getOffset(local);

        return new Date(local - targetOffsetFromUTC + browserOffsetFromUTC);
    }

    // convert from UTC to local date
    public static long toLocalDate(TimeZone browserTZ, TimeZone targetTZ, Date utcDate) {
        long utc = utcDate.getTime();
        int targetOffsetFromUTC = targetTZ.getOffset(utc);
        int browserOffsetFromUTC = browserTZ.getOffset(utc);

        return utc + targetOffsetFromUTC - browserOffsetFromUTC;
    }
}

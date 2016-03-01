/*
 * Copyright 2009-2016 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.util;

import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

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

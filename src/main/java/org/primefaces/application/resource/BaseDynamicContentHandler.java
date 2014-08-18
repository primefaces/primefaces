/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.application.resource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.faces.context.ExternalContext;

public abstract class BaseDynamicContentHandler implements DynamicContentHandler {
    
    public void handleCache(ExternalContext externalContext, boolean cache) {
        if(cache) {
            DateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            externalContext.setResponseHeader("Cache-Control", "max-age=29030400");
            externalContext.setResponseHeader("Expires", httpDateFormat.format(calendar.getTime()));
        }
        else {
            externalContext.setResponseHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            externalContext.setResponseHeader("Pragma", "no-cache");
            externalContext.setResponseHeader("Expires", "Mon, 8 Aug 1980 10:00:00 GMT");
        }
    }
}

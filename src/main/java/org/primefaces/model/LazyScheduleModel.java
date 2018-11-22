/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.model;

import java.util.Date;

public class LazyScheduleModel extends DefaultScheduleModel {

    private static final long serialVersionUID = 1L;

    /**
     * Method to be used when implementing lazy loading, implementers should override to fetch events that belong to a particular period
     *
     * @param start Start date of period
     * @param end   End date of period
     */
    public void loadEvents(Date start, Date end) {
    }
}

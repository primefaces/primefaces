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
package org.primefaces.push;

public final class Status {

    public enum STATUS {
        /**
         * Connection is open
         */
        OPEN,
        /**
         * Connection closed by the application
         */
        CLOSED_BY_APPLICATION,
        /**
         * Connection closed by a timeout
         */
        CLOSED_BY_TIMEOUT,
        /**
         * Connection closed by the client
         */
        CLOSED_BY_CLIENT,
        /**
         * Connection closed abruptly
         */
        UNEXPECTED_CLOSE}
    private STATUS status;

    public Status(STATUS status) {
        this.status = status;
    }

    /**
     * Return the current {@link #Status(org.primefaces.push.Status.STATUS)}
     * @return {@link #Status(org.primefaces.push.Status.STATUS)}
     */
    public STATUS status() {
        return status;
    }

    /**
     * Set the current {@link #Status(org.primefaces.push.Status.STATUS)}
     * @param status {@link #Status(org.primefaces.push.Status.STATUS)}
     * @return this
     */
    public Status status(STATUS status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return "Status{" +
                "status=" + status +
                '}';
    }
}

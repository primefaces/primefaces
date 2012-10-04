/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.webapp;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import org.primefaces.util.Constants;

/**
 *
 * Displays PrimeFaces version information on startup
 */
public class PostConstructApplicationEventListener implements SystemEventListener {

    private final static Logger logger = Logger.getLogger(PostConstructApplicationEventListener.class.getName());

    public boolean isListenerForSource(Object source) {
        return true;
    }

    public void processEvent(SystemEvent event) throws AbortProcessingException {
        logger.log(Level.INFO, "Running on PrimeFaces {0}", Constants.VERSION);
    }
}

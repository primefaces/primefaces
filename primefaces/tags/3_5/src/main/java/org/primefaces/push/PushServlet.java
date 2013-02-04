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
package org.primefaces.push;

import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereServlet;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PushServlet extends AtmosphereServlet {

    private final Logger logger = Logger.getLogger(PushServlet.class.getName());
    public final static String RULES = "org.primefaces.push.rules";

    @Override
    public void init(final ServletConfig sc) throws ServletException {

        // Shareable ThreadPool amongst all created Broadcasters.
        // This behavior can be changed using a PushRule.
        framework().addInitParameter(ApplicationConfig.BROADCASTER_SHARABLE_THREAD_POOLS, "true");

        PushContext c = PushContextFactory.getDefault().getPushContext();
        if (PushContextImpl.class.isAssignableFrom(c.getClass())) {
            framework().asyncSupportListener(PushContextImpl.class.cast(c));
        }

        super.init(sc);

        framework.interceptor(new AtmosphereResourceLifecycleInterceptor())
                .addAtmosphereHandler("/*", new PrimeAtmosphereHandler(configureRules(sc)))
                .initAtmosphereHandler(sc);
    }

    List<PushRule> configureRules(ServletConfig sc) {
        List<PushRule> rules = new ArrayList<PushRule>();

        String s = sc.getInitParameter(RULES);

        if (s != null) {
            String[] r = s.split(",");
            for (String rule : r) {
                try {
                    rules.add(loadRule(rule));
                    logger.log(Level.INFO, "PushRule " + rule + " loaded");
                } catch (Throwable t) {
                    logger.log(Level.WARNING, "Unable to load PushRule " + rule, t);
                }
            }
        }

        if (rules.isEmpty()) {
            rules.add(new DefaultPushRule());
        }

        return rules;
    }

    PushRule loadRule(String ruleName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        try {
            return (PushRule) Thread.currentThread().getContextClassLoader().loadClass(ruleName).newInstance();
        } catch (Throwable t) {
            return (PushRule) getClass().getClassLoader().loadClass(ruleName).newInstance();
        }
    }


}

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

import org.atmosphere.cpr.AtmosphereServlet;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;

public class PushServlet extends AtmosphereServlet {

    @Override
    public void init(final ServletConfig sc) throws ServletException {
        super.init(sc);

        framework.interceptor(new AtmosphereResourceLifecycleInterceptor())
                .addAtmosphereHandler("/*", new PrimeAtmosphereHandler(configureRules(sc)))
                .initAtmosphereHandler(sc);
    }

    public List<Rule> configureRules(ServletConfig sc) {
        List<Rule> rules = new ArrayList<Rule>();

        String s = sc.getInitParameter("org.primefaces.atmosphere.rules");
        if (s != null) {
            String[] r = s.split(",");
            for (String rule : r) {
                try {
                    rules.add(loadRule(rule));
                } catch (Throwable t) {
                    logger.warn("Unable to load Rule {}", rule, t);
                }
            }
        }

        if (rules.isEmpty()) {
            rules.add(new PrimeAtmosphereHandler.DefaultRule());
        }

        return rules;
    }

    Rule loadRule(String ruleName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        try {
            return (Rule) Thread.currentThread().getContextClassLoader().loadClass(ruleName).newInstance();
        } catch (Throwable t) {
            return (Rule) getClass().getClassLoader().loadClass(ruleName).newInstance();
        }
    }


}

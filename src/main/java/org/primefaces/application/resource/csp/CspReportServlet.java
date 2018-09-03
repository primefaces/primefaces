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
package org.primefaces.application.resource.csp;

import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.primefaces.util.BoundedInputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Built-in default endpoint browsers may call via POST if Content-Security-Policy (CSP) violations occur. We just do some logging.
 * To provide your own endpoint just change {@link CspHeader.ContextParams#CONTENT_SECURITY_POLICY_REPORT_URI} accordingly.
 * Data must be valid JSON as specified in <a href="https://w3c.github.io/webappsec-csp/2/#violation-reports">Content Security Policy Level 2 - Reporting</a>.
 */
@WebServlet(CspReportServlet.URL)
public class CspReportServlet extends HttpServlet {

    public static final String URL = "/csp-report";

    private static final Logger LOG = Logger.getLogger(CspReportServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jsonStr = toString(request.getInputStream());
        try {
            JSONObject json = new JSONObject(jsonStr);
            LOG.warning("CSP violation reported by browser: " + json.toString());
        }
        catch (JSONException ex) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("CSP violation reported by browser seems to be invalid JSON: " + jsonStr);
            }
        }
    }

    /**
     * Read up to 10 KiB of POST data to mitigate DoS
     * @param input the request body input stream
     * @return JSON string reported by browsers
     */
    private static String toString(ServletInputStream input) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new BoundedInputStream(input, 10240)))) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            return json.toString();
        }
    }

}

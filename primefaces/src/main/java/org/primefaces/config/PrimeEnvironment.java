/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.config;

import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.validation.Validation;

import org.primefaces.util.LangUtils;

public class PrimeEnvironment {

    private static final Logger LOGGER = Logger.getLogger(PrimeEnvironment.class.getName());

    private final boolean beanValidationAvailable;

    private final boolean atLeastEl22;

    private final boolean atLeastJsf40;

    private final boolean mojarra;

    private final boolean atLeastBv11;

    private final boolean atLeastServlet30;

    private final String buildVersion;

    private final boolean htmlSanitizerAvailable;

    public PrimeEnvironment(FacesContext context) {
        atLeastEl22 = LangUtils.isClassAvailable("javax.el.ValueReference");

        atLeastJsf40 = LangUtils.isClassAvailable("jakarta.faces.lifecycle.ClientWindowScoped");

        atLeastBv11 = LangUtils.isClassAvailable("javax.validation.executable.ExecutableValidator");

        atLeastServlet30 = LangUtils.isClassAvailable("javax.servlet.SessionCookieConfig");

        beanValidationAvailable = resolveBeanValidationAvailable();

        buildVersion = resolveBuildVersion();

        htmlSanitizerAvailable = LangUtils.isClassAvailable("org.owasp.html.PolicyFactory");

        if (context == null || context.getExternalContext() == null) {
            mojarra = false;
        }
        else {
            mojarra = context.getExternalContext().getApplicationMap().containsKey("com.sun.faces.ApplicationAssociate");
        }
    }

    protected boolean resolveBeanValidationAvailable() {
        boolean beanValidationAvailable = LangUtils.isClassAvailable("javax.validation.Validation");

        if (beanValidationAvailable) {
            // Trial-error approach to check for Bean Validation impl existence.
            // If any Exception occurs here, we assume that Bean Validation is not available.
            // The cause may be anything, i.e. NoClassDef, config error...
            try {
                Validation.buildDefaultValidatorFactory().getValidator();
            }
            catch (Throwable t) {
                LOGGER.log(Level.FINE, "BV not available - Could not build default ValidatorFactory.");
                beanValidationAvailable = false;
            }
        }

        return beanValidationAvailable;
    }

    protected String resolveBuildVersion() {
        String buildVersion = null;

        try (InputStream is = getClass().getResourceAsStream("/META-INF/maven/org.primefaces/primefaces/pom.properties")) {
            Properties buildProperties = new Properties();
            buildProperties.load(is);
            buildVersion = buildProperties.getProperty("version");
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "PrimeFaces version not resolvable - Could not load pom.properties.");
        }

        // This should only happen if PF + the webapp is openend and started in the same netbeans instance
        // Fallback to a UID to void a empty version in the resourceUrls
        if (LangUtils.isBlank(buildVersion)) {
            buildVersion = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }

        return buildVersion;
    }

    public boolean isBeanValidationAvailable() {
        return beanValidationAvailable;
    }

    public boolean isAtLeastEl22() {
        return atLeastEl22;
    }

    public boolean isAtLeastJsf40() {
        return atLeastJsf40;
    }

    public boolean isAtLeastServlet30() {
        return atLeastServlet30;
    }

    public boolean isMojarra() {
        return mojarra;
    }

    public boolean isAtLeastBv11() {
        return atLeastBv11;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public boolean isHtmlSanitizerAvailable() {
        return htmlSanitizerAvailable;
    }
}

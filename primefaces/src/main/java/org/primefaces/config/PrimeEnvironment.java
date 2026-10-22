/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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

import org.primefaces.util.LangUtils;

import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.context.FacesContext;
import jakarta.validation.Validation;

public class PrimeEnvironment {

    private static final Logger LOGGER = Logger.getLogger(PrimeEnvironment.class.getName());

    private final boolean beanValidationAvailable;

    private final boolean mojarra;

    private final String buildVersion;

    private final String resourceVersion;

    private final boolean htmlSanitizerAvailable;

    public PrimeEnvironment(FacesContext context) {
        beanValidationAvailable = resolveBeanValidationAvailable();

        buildVersion = resolveBuildVersion();
        resourceVersion = resolveResourceVersion(buildVersion);

        htmlSanitizerAvailable = LangUtils.isClassAvailable("org.owasp.html.PolicyFactory");

        if (context == null || context.getExternalContext() == null) {
            mojarra = false;
        }
        else {
            mojarra = context.getExternalContext().getApplicationMap().containsKey("com.sun.faces.ApplicationAssociate");
        }
    }

    protected boolean resolveBeanValidationAvailable() {
        boolean beanValidationAvailable = LangUtils.isClassAvailable("jakarta.validation.Validation");

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

    protected String resolveResourceVersion(String buildVersion) {
        // A SNAPSHOT keeps its version across builds, so the resource URLs would not change after a redeploy and the
        // browser would serve the old CSS/JS from its cache. Append the startup time to bust the cache on every start.
        if (buildVersion.endsWith("-SNAPSHOT")) {
            return buildVersion + "." + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);
        }

        return buildVersion;
    }

    public boolean isBeanValidationAvailable() {
        return beanValidationAvailable;
    }

    public boolean isMojarra() {
        return mojarra;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    /**
     * The version appended to the resource URLs ({@code &v=}): the build version, for a SNAPSHOT plus the startup time.
     */
    public String getResourceVersion() {
        return resourceVersion;
    }

    public boolean isHtmlSanitizerAvailable() {
        return htmlSanitizerAvailable;
    }
}

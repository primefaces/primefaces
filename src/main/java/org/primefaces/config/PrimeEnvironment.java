/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import java.io.IOException;
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

    private final boolean atLeastJsf23;
    private final boolean atLeastJsf22;
    private final boolean atLeastJsf21;

    private final boolean mojarra;

    private final boolean atLeastBv11;

    private final String buildVersion;

    private final boolean htmlSanitizerAvailable;

    public PrimeEnvironment(FacesContext context) {
        atLeastEl22 = LangUtils.tryToLoadClassForName("javax.el.ValueReference") != null;

        atLeastJsf23 = LangUtils.tryToLoadClassForName("javax.faces.component.UIImportConstants") != null;
        atLeastJsf22 = LangUtils.tryToLoadClassForName("javax.faces.flow.Flow") != null;
        atLeastJsf21 = LangUtils.tryToLoadClassForName("javax.faces.component.TransientStateHolder") != null;

        atLeastBv11 = LangUtils.tryToLoadClassForName("javax.validation.executable.ExecutableValidator") != null;

        beanValidationAvailable = resolveBeanValidationAvailable();

        buildVersion = resolveBuildVersion();

        htmlSanitizerAvailable = LangUtils.tryToLoadClassForName("org.owasp.html.PolicyFactory") != null;

        if (context == null) {
            this.mojarra = false;
        }
        else {
            this.mojarra = context.getExternalContext().getApplicationMap().containsKey("com.sun.faces.ApplicationAssociate");
        }
    }

    protected boolean resolveBeanValidationAvailable() {
        boolean beanValidationAvailable = LangUtils.tryToLoadClassForName("javax.validation.Validation") != null;

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

        InputStream is = null;
        try {
            is = getClass().getResourceAsStream("/META-INF/maven/org.primefaces/primefaces/pom.properties");

            Properties buildProperties = new Properties();
            buildProperties.load(is);
            buildVersion = buildProperties.getProperty("version");
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, "PrimeFaces version not resolvable - Could not load pom.properties.");
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                }
            }
        }

        // This should only happen if PF + the webapp is openend and started in the same netbeans instance
        // Fallback to a UID to void a empty version in the resourceUrls
        if (LangUtils.isValueBlank(buildVersion)) {
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

    public boolean isAtLeastJsf23() {
        return atLeastJsf23;
    }

    public boolean isAtLeastJsf22() {
        return atLeastJsf22;
    }

    public boolean isAtLeastJsf21() {
        return atLeastJsf21;
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

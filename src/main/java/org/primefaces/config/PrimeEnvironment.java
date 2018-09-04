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
package org.primefaces.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.Validation;
import org.primefaces.util.LangUtils;

public class PrimeEnvironment {

    private static final Logger LOG = Logger.getLogger(PrimeEnvironment.class.getName());

    private boolean beanValidationAvailable = false;

    private boolean atLeastEl22 = false;

    private boolean atLeastJsf23 = false;
    private boolean atLeastJsf22 = false;
    private boolean atLeastJsf21 = false;

    private boolean atLeastBv11 = false;

    private String buildVersion = null;

    public PrimeEnvironment() {
        atLeastEl22 = LangUtils.tryToLoadClassForName("javax.el.ValueReference") != null;

        atLeastJsf23 = LangUtils.tryToLoadClassForName("javax.faces.component.UIImportConstants") != null;
        atLeastJsf22 = LangUtils.tryToLoadClassForName("javax.faces.flow.Flow") != null;
        atLeastJsf21 = LangUtils.tryToLoadClassForName("javax.faces.component.TransientStateHolder") != null;

        atLeastBv11 = LangUtils.tryToLoadClassForName("javax.validation.executable.ExecutableValidator") != null;

        beanValidationAvailable = checkIfBeanValidationIsAvailable();

        buildVersion = resolveBuildVersion();
        // This should only happen if PF + the webapp is openend and started in the same netbeans instance
        // Fallback to a UID to void a empty version in the resourceUrls
        if (buildVersion == null || buildVersion.trim().isEmpty()) {
            buildVersion = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }
    }

    protected boolean checkIfBeanValidationIsAvailable() {
        boolean available = LangUtils.tryToLoadClassForName("javax.validation.Validation") != null;

        if (available) {
            // Trial-error approach to check for Bean Validation impl existence.
            // If any Exception occurs here, we assume that Bean Validation is not available.
            // The cause may be anything, i.e. NoClassDef, config error...
            try {
                Validation.buildDefaultValidatorFactory().getValidator();
            }
            catch (Throwable t) {
                LOG.log(Level.FINE, "BV not available - Could not build default ValidatorFactory.");
                available = false;
            }
        }

        return available;
    }

    protected String resolveBuildVersion() {

        Properties buildProperties = new Properties();
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream("/META-INF/maven/org.primefaces/primefaces/pom.properties");
            buildProperties.load(is);
            return buildProperties.getProperty("version");
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, "PrimeFaces version not resolvable - Could not load pom.properties.");
        }

        if (is != null) {
            try {
                is.close();
            }
            catch (IOException e) {
            }
        }

        return null;
    }

    public boolean isBeanValidationAvailable() {
        return beanValidationAvailable;
    }

    public void setBeanValidationAvailable(boolean beanValidationAvailable) {
        this.beanValidationAvailable = beanValidationAvailable;
    }

    public boolean isAtLeastEl22() {
        return atLeastEl22;
    }

    public void setAtLeastEl22(boolean atLeastEl22) {
        this.atLeastEl22 = atLeastEl22;
    }

    public boolean isAtLeastJsf23() {
        return atLeastJsf23;
    }

    public void setAtLeastJsf23(boolean atLeastJsf23) {
        this.atLeastJsf23 = atLeastJsf23;
    }

    public boolean isAtLeastJsf22() {
        return atLeastJsf22;
    }

    public void setAtLeastJsf22(boolean atLeastJsf22) {
        this.atLeastJsf22 = atLeastJsf22;
    }

    public boolean isAtLeastJsf21() {
        return atLeastJsf21;
    }

    public void setAtLeastJsf21(boolean atLeastJsf21) {
        this.atLeastJsf21 = atLeastJsf21;
    }

    public boolean isAtLeastBv11() {
        return atLeastBv11;
    }

    public void setAtLeastBv11(boolean atLeastBv11) {
        this.atLeastBv11 = atLeastBv11;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }
}

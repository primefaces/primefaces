/**
 * Copyright 2009-2017 PrimeTek.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;

/**
 * Detects the version of the JSF implementation.
 */
public class JsfVersionDetector {

    private static final Pattern PATTERN = Pattern.compile("([0-9]+\\.[0-9]+).*");
    private static final String VERSION_2_1 = "2.1";
    private static final String VERSION_2_2 = "2.2";
    private static final String VERSION_2_3 = "2.3";

    private String declaredVersion;
    private String heuristicVersion;
    private boolean jsf23Supported;
    private boolean jsf22Supported;
    private boolean jsf21Supported;


    /**
     * Constructor - initializes all fields.
     */
    public JsfVersionDetector() {
        this.declaredVersion = FacesContext.class.getPackage().getImplementationVersion();
        this.heuristicVersion = detectHeuristicVersion();
        final String version = getMajorAndMinorVersion(this.declaredVersion, this.heuristicVersion);
        if (version == null) {
            return;
        }
        this.jsf21Supported = VERSION_2_1.compareTo(version) <= 0;
        this.jsf22Supported = VERSION_2_2.compareTo(version) <= 0;
        this.jsf23Supported = VERSION_2_3.compareTo(version) <= 0;
    }


    private static String getMajorAndMinorVersion(final String declared, final String heuristic) {
        final String version = declared == null ? heuristic : declared;
        if (version == null) {
            return null;
        }
        final Matcher matcher = PATTERN.matcher(version);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }


    private String detectHeuristicVersion() {
        if (detectJSF23()) {
            return VERSION_2_3;
        }
        if (detectJSF22()) {
            return VERSION_2_2;
        }
        if (detectJSF21()) {
            return VERSION_2_1;
        }
        // not detected -> other
        return null;
    }


    private boolean detectJSF23() {
        return checkJsfVersion("javax.faces.component.UIImportConstants");
    }


    private boolean detectJSF22() {
        return checkJsfVersion("javax.faces.flow.Flow");
    }


    private boolean detectJSF21() {
        return checkJsfVersion("javax.faces.view.facelets.FaceletCache");
    }


    private boolean checkJsfVersion(final String knownClass) {
        try {
            Class.forName(knownClass);
            return true;
        }
        catch (final ClassNotFoundException ex) {
            // ignore
        }
        return false;
    }


    /**
     * @return only major.minor part of the version. Can be null.
     */
    public String getJsfVersion() {
        return getMajorAndMinorVersion(this.declaredVersion, this.heuristicVersion);
    }


    /**
     * @return version found in javax.faces.context package metadata. Can be null.
     */
    public String getJsfDeclaredVersion() {
        return this.declaredVersion;
    }


    /**
     * @return version guessed from implemented classes.
     */
    public String getJsfHeuristicVersion() {
        return this.heuristicVersion;
    }


    /**
     * @return true if the version is JSF 2.3 or newer.
     */
    public boolean isJsf23Supported() {
        return this.jsf23Supported;
    }


    /**
     * @return true if the version is JSF 2.2 or newer.
     */
    public boolean isJsf22Supported() {
        return this.jsf22Supported;
    }


    /**
     * @return true if the version is JSF 2.1 or newer.
     */
    public boolean isJsf21Supported() {
        return this.jsf21Supported;
    }


    /**
     * Reports all detected informations.
     */
    @Override
    public String toString() {
        return new StringBuilder().append("JsfVersionDetector(") //
                .append("package metadata: ").append(declaredVersion) //
                .append(", heuristic: ").append(this.heuristicVersion) //
                .append(", supposed JSF API version: ").append(getJsfVersion()) //
                .append(", supports[2.1, 2.2, 2.3]: ").append('[') //
                .append(this.jsf21Supported).append(", ").append(this.jsf22Supported).append(", ") //
                .append(this.jsf23Supported).append(']') //
                .append(')').toString();
    }
}

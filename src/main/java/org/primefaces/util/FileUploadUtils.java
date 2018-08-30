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
package org.primefaces.util;

import java.io.File;
import java.util.regex.Pattern;
import javax.faces.FacesException;

/**
 * Utilities for FileUpload components.
 */
public class FileUploadUtils {
    /* Restrict character set of filename */
    private static final Pattern INVALID_FILENAME_PATTERN = Pattern.compile("([\\\\/:*?<>|])");
    /**
     * Check the filename according to pattern
     * If the filename does not match the pattern([\\\\/:*?<>|]), it throws an exception.
     *
     * @param filename The name of file
     * @return
     */
    public static String getValidFilename(String filename) {
        if (filename != null) {
            String[] parts = filename.split(Pattern.quote(File.separator));
            for (String part : parts) {
                if (INVALID_FILENAME_PATTERN.matcher(part).find()) {
                    throw new FacesException("Invalid filename: " + filename);
                }
            }
        }
        return filename;
    }
}

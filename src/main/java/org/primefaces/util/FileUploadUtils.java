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
import java.io.IOException;
import java.util.regex.Pattern;
import javax.faces.FacesException;
import org.apache.commons.io.FilenameUtils;
import org.owasp.esapi.SafeFile;
import org.owasp.esapi.errors.ValidationException;

 /**
 * Utilities for FileUpload components.
 */
public class FileUploadUtils {

    private static final Pattern INVALID_FILENAME_PATTERN = Pattern.compile("([\\/:*?\"<>|])");

    public static String getValidFilename(String filename) {
        if (LangUtils.isValueBlank(filename)) {
            throw new FacesException("Filename is required.");
        }

        if (isSystemWindows()) {
            if (!filename.contains("\\\\")) {
                String[] parts = filename.substring(FilenameUtils.getPrefixLength(filename)).split(Pattern.quote(File.separator));
                for (String part : parts) {
                    if (INVALID_FILENAME_PATTERN.matcher(part).find()) {
                        throw new FacesException("Invalid filename: " + filename);
                    }
                }
            }
            else {
                throw new FacesException("Invalid filename: " + filename);
            }
        }

        String name = FilenameUtils.getName(filename);
        String extension = FilenameUtils.EXTENSION_SEPARATOR_STR + FilenameUtils.getExtension(filename);

        if (extension.isEmpty()) {
            throw new FacesException("File must have an extension");
        }
        else if (name.isEmpty() || extension.equals(name)) {
            throw new FacesException("Filename can not be the empty string");
        }

        return name;
    }

    public static String getValidFilePath(String filePath) throws ValidationException {
        if (filePath == null || filePath.trim().equals("")) {
            throw new FacesException("Path can not be the empty string or null");
        }

        try {
            SafeFile file = new SafeFile(filePath);
            File parentFile = file.getParentFile();

            if (!file.exists()) {
                throw new ValidationException("Invalid directory", "Invalid directory, \"" + file + "\" does not exist.");
            }
            if (!parentFile.exists()) {
                throw new ValidationException("Invalid directory", "Invalid directory, specified parent does not exist.");
            }
            if (!parentFile.isDirectory()) {
                throw new ValidationException("Invalid directory", "Invalid directory, specified parent is not a directory.");
            }
            if (!file.getCanonicalPath().startsWith(parentFile.getCanonicalPath())) {
                throw new ValidationException("Invalid directory", "Invalid directory, \"" + file + "\" does not inside specified parent.");
            }

            if (!file.getCanonicalPath().equals(filePath)) {
                throw new ValidationException("Invalid directory", "Invalid directory name does not match the canonical path");
            }
        }
        catch (IOException ex) {
            throw new ValidationException("Invalid directory", "Failure to validate directory path");
        }

        return filePath;
    }

    public static boolean isSystemWindows() {
        return File.separatorChar == '\\';
    }
}
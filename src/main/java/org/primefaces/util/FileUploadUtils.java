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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.faces.FacesException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.owasp.esapi.SafeFile;
import org.owasp.esapi.errors.ValidationException;
import org.primefaces.component.fileupload.FileUpload;

/**
 * Utilities for FileUpload components.
 */
public class FileUploadUtils {

    private static final Logger LOGGER = Logger.getLogger(FileUploadUtils.class.getName());

    private static final Pattern INVALID_FILENAME_PATTERN = Pattern.compile("([\\/:*?\"<>|])");

    private FileUploadUtils() {
    }

    public static String getValidFilename(String filename) {
        if (LangUtils.isValueBlank(filename)) {
            return null;
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

        if (extension.equals(FilenameUtils.EXTENSION_SEPARATOR_STR)) {
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

    /**
     * Check if an uploaded file meets all specifications regarding its filename and content type. It evaluates {@link FileUpload#getAllowTypes}
     * as well as {@link FileUpload#getAccept} and uses the installed {@link java.nio.file.spi.FileTypeDetector} implementation.
     * For most reliable content type checking it's recommended to plug in Apache Tika as an implementation.
     * @param fileUpload the fileUpload component
     * @param fileName the name of the uploaded file
     * @param inputStream the input stream to receive the file's content from
     * @return <code>true</code>, if all validations regarding filename and content type passed, <code>false</code> else
     */
    public static boolean isValidType(FileUpload fileUpload, String fileName, InputStream inputStream) {
        try {
            boolean validType = isValidFileName(fileUpload, fileName) && isValidFileContent(fileUpload, fileName, inputStream);
            if (validType) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine(String.format("The uploaded file %s meets the filename and content type specifications", fileName));
                }
            }
            return validType;
        }
        catch (IOException | ScriptException ex) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, String.format("The type of the uploaded file %s could not be validated", fileName), ex);
            }
            return false;
        }
    }

    private static boolean isValidFileName(FileUpload fileUpload, String fileName) throws ScriptException {
        String fileNameRegex = fileUpload.getAllowTypes();
        if (!LangUtils.isValueBlank(fileNameRegex)) {
            //We use rhino or nashorn javascript engine bundled with java to re-evaluate javascript regex that cannot be easily translated to java regex
            //TODO If at some day nashorn will not be bundled with java (http://openjdk.java.net/jeps/335), we have to put some notes in the user guide
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            String evalJs = String.format("%s.test(\"%s\")", fileNameRegex, EscapeUtils.forJavaScriptAttribute(fileName));
            if (!Boolean.TRUE.equals(engine.eval(evalJs))) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine(String.format("The uploaded filename %s does not match the specified regex %s", fileName, fileNameRegex));
                }
                return false;
            }
        }
        return true;
    }

    private static boolean isValidFileContent(FileUpload fileUpload, String fileName, InputStream inputStream) throws IOException {
        if (!fileUpload.isValidateContentType()) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Content type checking is disabled");
            }
            return true;
        }
        if (LangUtils.isValueBlank(fileUpload.getAccept())) {
            //Short circuit
            return true;
        }
        String tempFilePrefix = UUID.randomUUID().toString();
        boolean tika = false;
        try {
            Class.forName("org.apache.tika.filetypedetector.TikaFileTypeDetector");
            tika = true;
        }
        catch (Exception ex) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Could not find Apache Tika in classpath which is recommended for reliable content type checking");
            }
        }
        //If Tika is in place, we drop the original file extension to avoid short circuit detection by just looking at the file extension
        String tempFileSuffix = tika ? null : "." + FilenameUtils.getExtension(fileName);
        Path tempFile = Files.createTempFile(tempFilePrefix, tempFileSuffix);
        try {
            InputStream in = new PushbackInputStream(new BufferedInputStream(inputStream));
            try (OutputStream out = new FileOutputStream(tempFile.toFile())) {
                IOUtils.copyLarge(in, out);
            }
            String contentType = Files.probeContentType(tempFile);
            if (contentType == null) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.warning(String.format("Could not determine content type of uploaded file %s, consider plugging in an adequate " +
                            "FileTypeDetector implementation", fileName));
                }
                return false;
            }
            //Comma-separated values: file_extension|audio/*|video/*|image/*|media_type (see https://www.w3schools.com/tags/att_input_accept.asp)
            String[] accepts = fileUpload.getAccept().split(",");
            boolean accepted = false;
            for (String accept : accepts) {
                accept = accept.trim().toLowerCase();
                if (accept.startsWith(".") && fileName.toLowerCase().endsWith(accept)) {
                    accepted = true;
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine(String.format("The file extension %s of the uploaded file %s is accepted", accept, fileName));
                    }
                    break;
                }
                //Now we have a media type that may contain wildcards
                if (FilenameUtils.wildcardMatch(contentType.toLowerCase(), accept)) {
                    accepted = true;
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine(String.format("The content type %s of the uploaded file %s is accepted by %s", contentType, fileName, accept));
                    }
                    break;
                }
            }
            if (!accepted) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine(String.format("The uploaded file %s with content type %s does not match the accept specification %s", fileName, contentType,
                            fileUpload.getAccept()));
                }
                return false;
            }
        }
        finally {
            try {
                Files.delete(tempFile);
            }
            catch (Exception ex) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, String.format("Could not delete temporary file %s, will try to delete on JVM exit",
                            tempFile.toAbsolutePath()), ex);
                    try {
                        tempFile.toFile().deleteOnExit();
                    }
                    catch (Exception ex1) {
                        if (LOGGER.isLoggable(Level.WARNING)) {
                            LOGGER.log(Level.WARNING, String.format("Could not register temporary file %s for deletion on JVM exit",
                                    tempFile.toAbsolutePath()), ex1);
                        }
                    }
                }
            }
        }
        return true;
    }

}
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
package org.primefaces.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.shaded.owasp.SafeFile;
import org.primefaces.shaded.owasp.ValidationException;
import org.primefaces.virusscan.VirusException;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

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
        String extension = FilenameUtils.getExtension(filename);
        if (name.isEmpty() && extension.isEmpty()) {
            throw new FacesException("Filename can not be the empty string");
        }

        return name;
    }

    public static String getValidFilePath(String filePath) throws ValidationException {
        if (LangUtils.isValueBlank(filePath)) {
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
     * @param uploadedFile the details of the uploaded file
     * @return <code>true</code>, if all validations regarding filename and content type passed, <code>false</code> else
     */
    public static boolean isValidType(PrimeApplicationContext context, FileUpload fileUpload, UploadedFile uploadedFile) {
        String fileName = uploadedFile.getFileName();
        try (InputStream input = uploadedFile.getInputStream()) {
            boolean validType = isValidFileName(fileUpload, uploadedFile)
                        && isValidFileContent(context, fileUpload, fileName, input);
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

    private static boolean isValidFileName(FileUpload fileUpload, UploadedFile uploadedFile) throws ScriptException {
        String allowTypesRegex = fileUpload.getAllowTypes();
        if (!LangUtils.isValueBlank(allowTypesRegex)) {
            //We use rhino or nashorn javascript engine bundled with java to re-evaluate javascript regex that cannot be easily translated to java regex
            //TODO If at some day nashorn will not be bundled with java (http://openjdk.java.net/jeps/335), we have to put some notes in the user guide
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");

            if (engine == null) {

                // Attempt to use the default extension loader to obtain the engine for environments where the
                // JavaScript ScriptEngine isn't available via the Thread.currentThread().getContextClassLoader()
                // (such as Liferay).
                engine = new ScriptEngineManager(null).getEngineByName("javascript");
            }

            if (engine == null) {
                throw new ScriptException(new NullPointerException(
                    "JavaScript ScriptEngine not available via the context ClassLoader or the extension ClassLoader."));
            }

            String fileName = EscapeUtils.forJavaScriptAttribute(uploadedFile.getFileName());
            String contentType = EscapeUtils.forJavaScriptAttribute(uploadedFile.getContentType());

            String evalJs = String.format("%s.test(\"%s\") || %s.test(\"%s\")", allowTypesRegex, contentType, allowTypesRegex, fileName);
            if (!Boolean.TRUE.equals(engine.eval(evalJs))) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.warning(String.format("The uploaded filename %s does not match the specified regex %s", fileName, allowTypesRegex));
                }
                return false;
            }
        }
        return true;
    }

    private static boolean isValidFileContent(PrimeApplicationContext context, FileUpload fileUpload, String fileName, InputStream stream) throws IOException {
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

        boolean tika = context.getEnvironment().isTikaAvailable();
        if (!tika && LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning("Could not find Apache Tika in classpath which is recommended for reliable content type checking");
        }

        //If Tika is in place, we drop the original file extension to avoid short circuit detection by just looking at the file extension
        String tempFileSuffix = tika ? null : "." + FilenameUtils.getExtension(fileName);
        String tempFilePrefix = UUID.randomUUID().toString();
        Path tempFile = Files.createTempFile(tempFilePrefix, tempFileSuffix);

        try {
            try (InputStream in = new PushbackInputStream(new BufferedInputStream(stream))) {
                try (OutputStream out = new FileOutputStream(tempFile.toFile())) {
                    IOUtils.copyLarge(in, out);
                }
            }
            String contentType = null;
            if (context.getFileTypeDetector() != null) {
                contentType = context.getFileTypeDetector().probeContentType(tempFile);
            }
            else {
                // use default Java fallback
                contentType = Files.probeContentType(tempFile);
            }

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

    public static void performVirusScan(FacesContext facesContext, UploadedFile file) throws VirusException {
        PrimeApplicationContext.getCurrentInstance(facesContext).getVirusScannerService().performVirusScan(file);
    }

    public static void tryValidateFile(FacesContext context, FileUpload fileUpload, UploadedFile uploadedFile) throws ValidatorException {
        Long sizeLimit = fileUpload.getSizeLimit();
        PrimeApplicationContext appContext = PrimeApplicationContext.getCurrentInstance(context);

        if (sizeLimit != null && uploadedFile.getSize() > sizeLimit) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, fileUpload.getInvalidSizeMessage(), ""));
        }

        if (!isValidType(appContext, fileUpload, uploadedFile)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, fileUpload.getInvalidFileMessage(), ""));
        }

        if (fileUpload.isVirusScan()) {
            performVirusScan(context, uploadedFile);
        }
    }

    public static void tryValidateFiles(FacesContext context, FileUpload fileUpload, List<UploadedFile> files) {
        long totalPartSize = 0;
        Long sizeLimit = fileUpload.getSizeLimit();
        for (UploadedFile file : files) {
            totalPartSize += file.getSize();
            tryValidateFile(context, fileUpload, file);
        }

        if (sizeLimit != null && totalPartSize > sizeLimit) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, fileUpload.getInvalidFileMessage(), ""));
        }
    }

    /**
     * OWASP prevent directory path traversal of "../../image.png".
     *
     * @see https://www.owasp.org/index.php/Path_Traversal
     * @param relativePath the relative path to check for path traversal
     * @return the relative path
     * @throws FacesException if any error is detected
     */
    public static String checkPathTraversal(String relativePath) {
        File file = new File(relativePath);

        if (file.isAbsolute()) {
            throw new FacesException("Path traversal attempt - absolute path not allowed.");
        }

        try  {
            String pathUsingCanonical = file.getCanonicalPath();
            String pathUsingAbsolute = file.getAbsolutePath();

            // Require the absolute path and canonicalized path match.
            // This is done to avoid directory traversal
            // attacks, e.g. "1/../2/"
            if (!pathUsingCanonical.equals(pathUsingAbsolute))  {
                throw new FacesException("Path traversal attempt for path " + relativePath);
            }
        }
        catch (IOException ex) {
            throw new FacesException("Path traversal - unexpected exception.", ex);
        }
        return relativePath;
    }
}

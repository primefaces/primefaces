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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.file.MultipleUploadedFile;
import org.primefaces.model.file.SingleUploadedFile;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFileWrapper;
import org.primefaces.shaded.owasp.SafeFile;
import org.primefaces.shaded.owasp.ValidationException;
import org.primefaces.virusscan.VirusException;

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
    public static boolean isValidType(FileUpload fileUpload, SingleUploadedFile uploadedFile) {
        String fileName = uploadedFile.getFileName();
        try {
            boolean validType = isValidFileName(fileUpload, uploadedFile) && isValidFileContent(fileUpload, fileName, uploadedFile.getInputStream());
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

    private static boolean isValidFileName(FileUpload fileUpload, SingleUploadedFile uploadedFile) throws ScriptException {
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

        boolean tika = LangUtils.tryToLoadClassForName("org.apache.tika.filetypedetector.TikaFileTypeDetector") != null;
        if (!tika && LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning("Could not find Apache Tika in classpath which is recommended for reliable content type checking");
        }

        //If Tika is in place, we drop the original file extension to avoid short circuit detection by just looking at the file extension
        String tempFileSuffix = tika ? null : "." + FilenameUtils.getExtension(fileName);
        String tempFilePrefix = UUID.randomUUID().toString();
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

    public static void performVirusScan(FacesContext facesContext, FileUpload fileUpload, InputStream inputStream) throws VirusException {
        if (fileUpload.isPerformVirusScan()) {
            PrimeApplicationContext.getCurrentInstance(facesContext).getVirusScannerService().performVirusScan(inputStream);
        }
    }

    public static boolean isValidFile(FacesContext context, FileUpload fileUpload, SingleUploadedFile uploadedFile) throws IOException {
        Long sizeLimit = fileUpload.getSizeLimit();
        boolean valid = (sizeLimit == null || uploadedFile.getSize() <= sizeLimit)
                && FileUploadUtils.isValidType(fileUpload, uploadedFile);
        if (valid) {
            try {
                FileUploadUtils.performVirusScan(context, fileUpload, uploadedFile.getInputStream());
            }
            catch (VirusException ex) {
                return false;
            }
        }
        return valid;
    }

    public static boolean areValidFiles(FacesContext context, FileUpload fileUpload, List<SingleUploadedFile> files) throws IOException {
        long totalPartSize = 0;
        Long sizeLimit = fileUpload.getSizeLimit();
        for (SingleUploadedFile file : files) {
            totalPartSize += file.getSize();
            if (!isValidFile(context, fileUpload, file)) {
                return false;
            }
        }

        return sizeLimit == null || totalPartSize <= sizeLimit;
    }

    /**
     * As FileUpload comes with different subtypes of {@link UploadedFile}
     * {@link FileUploadUtils#consume(UploadedFile, Consumer)} makes completely
     * these transparent by avoiding painful casting (e.g FileUploadUtils.consume(uploadedFile, f -> doSth(f));
     *
     * @param file uploaded file
     * @param consumer operation to execute on every single file
     */
    public static void consume(UploadedFile file, Consumer<SingleUploadedFile> consumer) {
        if (file instanceof SingleUploadedFile) {
            consumer.accept((SingleUploadedFile) file);
        }
        else if (file instanceof MultipleUploadedFile) {
            ((MultipleUploadedFile) file).getFiles().stream().forEach(consumer);
        }
        else if (file instanceof UploadedFileWrapper) {
            consume(((UploadedFileWrapper) file).getWrapped(), consumer);
        }
        else {
            throw new IllegalArgumentException("Unsupported UploadedFile type: " + file.getClass().getName());
        }
    }
}
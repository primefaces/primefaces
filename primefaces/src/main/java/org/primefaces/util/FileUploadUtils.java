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
package org.primefaces.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.component.fileupload.FileUploadChunkDecoder;
import org.primefaces.component.fileupload.FileUploadDecoder;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.virusscan.VirusException;

/**
 * Utilities for FileUpload components.
 */
public class FileUploadUtils {

    private static final Logger LOGGER = Logger.getLogger(FileUploadUtils.class.getName());

    // https://owasp.org/www-community/OWASP_Validation_Regex_Repository
    private static final Pattern INVALID_FILENAME_WINDOWS =
            Pattern.compile("^(?!^(PRN|AUX|CLOCK\\$|NUL|CON|COM\\d|LPT\\d|\\..*)(\\..+)?$)[^\\x00-\\x1f\\\\?*:\\\";|/<>]+$");
    private static final Pattern INVALID_FILENAME_LINUX = Pattern.compile(".*[/\0:*?\\\"<>|].*");
    private static final Pattern PERCENTS_PAT = Pattern.compile("(%)([0-9a-fA-F])([0-9a-fA-F])");

    private FileUploadUtils() {
    }


    public static String requireValidFilename(String filename) {
        if (LangUtils.isBlank(filename)) {
            throw validationError("Filename cannot be empty or null");
        }

        if (isSystemWindows()) {
            if (!INVALID_FILENAME_WINDOWS.matcher(filename).find()) {
                throw validationError("Invalid filename: " + filename);
            }
        }
        else if (INVALID_FILENAME_LINUX.matcher(filename).find()) {
            throw validationError("Invalid filename: " + filename);
        }

        if (PERCENTS_PAT.matcher(filename).find()) {
            throw validationError("Invalid filename: " + filename);
        }

        int ch = containsUnprintableCharacters(filename);
        if (ch != -1) {
            throw validationError("Invalid filename: (" + filename + ") contains unprintable character: " + ch);
        }

        return FilenameUtils.getName(filename);
    }

    public static String requireValidFilePath(String filePath, boolean mustExist) {
        if (LangUtils.isBlank(filePath)) {
            throw validationError("Path can not be the empty string or null");
        }

        int ch = containsUnprintableCharacters(filePath);
        if (ch != -1) {
            throw validationError("Invalid path: (" + filePath + ") contains unprintable character: " + ch);
        }

        if (PERCENTS_PAT.matcher(filePath).find()) {
            throw validationError("Invalid path: " + filePath);
        }

        try {
            File file = new File(filePath);
            File parentFile = file.getParentFile();

            if (mustExist && !file.exists()) {
                throw validationError("Invalid file, \"" + file + "\" does not exist.");
            }
            if (mustExist && !parentFile.exists()) {
                throw validationError("Invalid directory, file parent directory does not exist.");
            }
            if (mustExist && !parentFile.isDirectory()) {
                throw validationError("Invalid directory, file parent is not a directory.");
            }
            if (!file.getCanonicalFile().toPath().startsWith(parentFile.getCanonicalFile().toPath())) {
                throw validationError("Invalid directory, \"" + file + "\" does not reside inside specified parent.");
            }
            if (!file.getCanonicalPath().equals(filePath)) {
                throw validationError("Invalid directory, name does not match the canonical path." );
            }
        }
        catch (IOException ex) {
            throw validationError("Failure to validate directory path: " + ex.getMessage());
        }

        return filePath;
    }

    public static ValidatorException validationError(String message) {
        return new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "File Error", message));
    }

    static boolean isSystemWindows() {
        return File.separatorChar == '\\';
    }

    public static int containsUnprintableCharacters(String s) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ((ch) < 32 || (ch) > 126) {
                return ch;
            }
        }
        return -1;
    }

    /**
     * Check if an uploaded file meets all specifications regarding its filename and content type. It evaluates {@link FileUpload#getAllowTypes}
     * as well as {@link FileUpload#getAccept} and uses the installed {@link java.nio.file.spi.FileTypeDetector} implementation.
     * For most reliable content type checking it's recommended to plug in Apache Tika as an implementation.
     *
     * @param context the {@link PrimeApplicationContext}
     * @param fileUpload the {@link FileUpload} component
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
        catch (IOException ex) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, String.format("The type of the uploaded file %s could not be validated", fileName), ex);
            }
            return false;
        }
    }

    private static boolean isValidFileName(FileUpload fileUpload, UploadedFile uploadedFile) {
        String javascriptRegex = fileUpload.getAllowTypes();
        if (LangUtils.isBlank(javascriptRegex)) {
            return true;
        }

        String javaRegex = convertJavaScriptRegex(javascriptRegex);
        if (LangUtils.isBlank(javaRegex)) {
            return true;
        }

        String fileName = EscapeUtils.forJavaScriptAttribute(uploadedFile.getFileName());
        String contentType = EscapeUtils.forJavaScriptAttribute(uploadedFile.getContentType());

        final Pattern allowTypesPattern = Pattern.compile(javaRegex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        final Matcher fileNameMatcher = allowTypesPattern.matcher(fileName);
        final Matcher contentTypeMatcher = allowTypesPattern.matcher(contentType);
        boolean isValid = fileNameMatcher.find() || contentTypeMatcher.find();
        if (!isValid) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning(String.format("The uploaded filename %s does not match the specified regex %s", fileName, javaRegex));
            }
            return false;
        }

        return true;
    }

    /**
     * Converts a JavaScript regular expression like '/(\.|\/)(gif|jpe?g|png)$/i'
     * to the Java usable format '(\\.|\\/)(gif|jpe?g|png)$'
     * @param jsRegex the client side JavaScript regex
     * @return the Java converted version of the regex
     */
    protected static String convertJavaScriptRegex(String jsRegex) {
        int start = 0;
        int end = jsRegex.length() - 1;
        if (jsRegex.charAt(0) == '/') {
            start = 1;
        }
        char endChar = jsRegex.charAt(end);
        if (endChar != '/' && endChar == 'i' || endChar == 'g') {
            end = end - 1;
        }
        return LangUtils.substring(jsRegex, start, end);
    }

    private static boolean isValidFileContent(PrimeApplicationContext context, FileUpload fileUpload, String fileName, InputStream stream) throws IOException {
        if (!fileUpload.isValidateContentType()) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Content type checking is disabled");
            }
            return true;
        }
        if (LangUtils.isBlank(fileUpload.getAccept())) {
            //Short circuit
            return true;
        }

        String tempFilePrefix = UUID.randomUUID().toString();
        Path tempFile = Files.createTempFile(tempFilePrefix, null);

        try {
            try (InputStream in = new PushbackInputStream(new BufferedInputStream(stream))) {
                try (OutputStream out = new FileOutputStream(tempFile.toFile())) {
                    IOUtils.copyLarge(in, out);
                }
            }


            String contentType = context.getFileTypeDetector().probeContentType(tempFile);

            // We want to force the FileTypeDetector impl to try to detect the file type by its content,
            // this is why we intentionally hide the original file name/extension at first because both
            // tika and mime-types go the easy way of looking up the file extension if possible.
            // If this first attempt failed we try again, but now while preserving the original file name/extension
            // to e.g. make the JDK default FileTypeDetector work
            if (contentType == null) {
                String fileExtension = FilenameUtils.getExtension(fileName);

                if (!fileExtension.isEmpty()) {
                    Path newTempFile = Files.createTempFile(tempFilePrefix, "." + fileExtension);
                    tempFile = Files.move(tempFile, newTempFile, StandardCopyOption.REPLACE_EXISTING);
                    contentType = context.getFileTypeDetector().probeContentType(tempFile);
                }
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

    public static List<Path> listChunks(Path path) {
        try (Stream<Path> walk = Files.walk(path)) {
            return walk
                    .filter(p -> p.toFile().isFile() && p.getFileName().toString().matches("\\d+"))
                    .sorted(Comparator.comparing(p -> Long.parseLong(p.getFileName().toString())))
                    .collect(Collectors.toList());
        }
        catch (IOException | SecurityException e) {
            throw new FacesException(e);
        }
    }

    public static <T extends HttpServletRequest> List<Path> listChunks(T request) {
        Path chunkDir = getChunkDir(request);
        if (!chunkDir.toFile().exists()) {
            return Collections.emptyList();
        }

        return listChunks(chunkDir);
    }

    public static <T extends HttpServletRequest> FileUploadChunkDecoder<T> getFileUploadChunkDecoder(T request) {
        PrimeApplicationContext pfContext = PrimeApplicationContext.getCurrentInstance(request.getServletContext());
        FileUploadDecoder decoder = pfContext.getFileUploadDecoder();
        if (!(decoder instanceof FileUploadChunkDecoder)) {
            throw new FacesException("Chunk decoder not supported");
        }

        return (FileUploadChunkDecoder<T>) decoder;
    }

    public static <T extends HttpServletRequest> Path getChunkDir(T request) {
        FileUploadChunkDecoder<T> chunkDecoder = getFileUploadChunkDecoder(request);
        String fileKey = chunkDecoder.generateFileInfoKey(request);
        return Paths.get(chunkDecoder.getUploadDirectory(request), fileKey);
    }

    public static <T extends HttpServletRequest> String getWebkitRelativePath(T request) {
        return request.getParameter("X-File-Webkit-Relative-Path");
    }
}

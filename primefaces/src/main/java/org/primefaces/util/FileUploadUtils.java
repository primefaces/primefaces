/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.component.fileupload.FileUploadChunkDecoder;
import org.primefaces.component.fileupload.FileUploadDecoder;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.virusscan.VirusException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.faces.FacesException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;

/**
 * Utilities for FileUpload components.
 */
public class FileUploadUtils {

    private static final Logger LOGGER = Logger.getLogger(FileUploadUtils.class.getName());

    // Message keys
    private static final String FILENAME_EMPTY = "primefaces.FileValidator.FILENAME_EMPTY";
    private static final String FILENAME_INVALID_CHAR = "primefaces.FileValidator.FILENAME_INVALID_CHAR";
    private static final String FILENAME_INVALID_WINDOWS = "primefaces.FileValidator.FILENAME_INVALID_WINDOWS";
    private static final String FILENAME_INVALID_LINUX = "primefaces.FileValidator.FILENAME_INVALID_LINUX";

    // https://owasp.org/www-community/OWASP_Validation_Regex_Repository
    private static final Pattern INVALID_FILENAME_WINDOWS =
            Pattern.compile("^(?!^(PRN|AUX|CLOCK\\$|NUL|CON|COM\\d|LPT\\d|\\..*)(\\..+)?$)[^\\x00-\\x1f\\\\?*:\\\";|/<>]+$");
    private static final Pattern INVALID_FILENAME_LINUX = Pattern.compile(".*[/\0:*?\\\"<>|].*");
    private static final Pattern ENCODED_CHARS_PAT = Pattern.compile("(%)([0-9a-fA-F])([0-9a-fA-F])");

    // pattern to format allowedTypes for FileUpload - formats like /.*\.(xls|xlsx|csv|txt)/
    private static final Pattern ALLOW_TYPES_PATTERN =  Pattern.compile("\\/\\.\\*\\\\.\\(?(.*?)\\)?\\$?\\/");
    // pattern to format allowedTypes for FileUpload - formats like /(\.|\/)(gif|jpeg|jpg|png)$/
    private static final Pattern ALLOW_TYPES_PATTERN_2 = Pattern.compile("\\/\\(\\.|\\/\\)\\(?(.*?)\\)?\\$?\\/");

    private FileUploadUtils() {
        // private constructor to prevent instantiation
    }

    /**
     * Checks file name for validity based on Windows or Linux rules.
     * @param filename the name of the file to check
     * @return the extracted file name
     */
    public static String requireValidFilename(FacesContext context, String filename) {
        if (LangUtils.isBlank(filename)) {
            throw validationError(MessageFactory.getMessage(context, FILENAME_EMPTY));
        }

        // use java.nio Paths to detect a bad character
        Character ch = containsInvalidCharacters(filename);
        if (ch != null) {
            throw validationError(MessageFactory.getMessage(context, FILENAME_INVALID_CHAR, filename, ch));
        }

        // Windows and Linux have different allowed characters
        if (isSystemWindows()) {
            if (!INVALID_FILENAME_WINDOWS.matcher(filename).find()) {
                throw validationError(MessageFactory.getMessage(context, FILENAME_INVALID_WINDOWS, filename));
            }
        }
        else if (INVALID_FILENAME_LINUX.matcher(filename).find()) {
            throw validationError(MessageFactory.getMessage(context, FILENAME_INVALID_LINUX, filename));
        }

        // URL encoded characters like %20 are invalid
        Matcher encodedMatcher = ENCODED_CHARS_PAT.matcher(filename);
        if (encodedMatcher.find() ) {
            throw validationError(MessageFactory.getMessage(context, FILENAME_INVALID_CHAR, filename, encodedMatcher.group()));
        }

        return FilenameUtils.getName(filename);
    }

    /**
     * Check the file path to ensure its valid and prevent directory traversal security issues.
     *
     * @param filePath the file path to test
     * @param mustExist true if the directory must exist on disk, false if not
     * @return the updated file path
     */
    public static String requireValidFilePath(String filePath, boolean mustExist) {
        if (LangUtils.isBlank(filePath)) {
            throw validationError("Path can not be the empty string or null");
        }

        Character ch = containsInvalidCharacters(filePath);
        if (ch != null) {
            throw validationError("Invalid path: (" + filePath + ") contains invalid character: " + ch);
        }

        filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);

        // standardize Windows paths from d:/test to d:\\test
        if (isSystemWindows()) {
            filePath = filePath.replace("/", "\\");
        }

        try {
            File file = new File(filePath);
            File parentFile = file.getParentFile();

            if (parentFile == null) {
                throw validationError("Invalid directory, \"" + filePath + "\" is not valid.");
            }
            if (mustExist && !file.exists()) {
                throw validationError("Invalid file, \"" + file + "\" does not exist.");
            }
            if (mustExist && !parentFile.exists()) {
                throw validationError("Invalid directory, file parent directory does not exist.");
            }
            if (mustExist && !parentFile.isDirectory()) {
                throw validationError("Invalid directory, file parent is not a directory.");
            }
            // CVE-2022-31159 Partial Path Traversal Vulnerability
            if (!file.getCanonicalFile().toPath().startsWith(parentFile.getCanonicalFile().toPath())) {
                throw validationError("Invalid directory, \"" + file + "\" does not reside inside specified parent.");
            }
            // SONATYPE-2018-0608 prevent path traversal like "..\..\test"
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

    public static Character containsInvalidCharacters(String s) {
        try {
            Paths.get(s);
        }
        catch (InvalidPathException e) {
            if (e.getInput() != null && !e.getInput().isEmpty() && e.getIndex() >= 0) {
                return s.toCharArray()[e.getIndex()];
            }
        }
        return null;
    }

    /**
     * Check if an uploaded file meets all specifications regarding its filename and content type.
     * It evaluates allowTypes as well as accept and uses the installed {@link java.nio.file.spi.FileTypeDetector} implementation.
     * For most reliable content type checking it's recommended to plug in Apache Tika as an implementation.
     *
     * @param context the {@link PrimeApplicationContext}
     * @param uploadedFile the details of the uploaded file
     * @param allowTypes the Javascript regex
     * @param accept the accept types
     * @return <code>true</code>, if all validations regarding filename and content type passed, <code>false</code> else
     */
    public static boolean isValidType(PrimeApplicationContext context,
                                      UploadedFile uploadedFile, String allowTypes, String accept) {
        String fileName = uploadedFile.getFileName();
        try (InputStream input = uploadedFile.getInputStream()) {
            boolean validType = isValidFileName(uploadedFile, allowTypes)
                        && isValidFileContent(context, accept, fileName, input);
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

    private static boolean isValidFileName(UploadedFile uploadedFile, String javascriptRegex) {
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
     * Converts a JavaScript regular expression like '/(\.|\/)(gif|jpeg|jpg|png)$/i'
     * to the Java usable format '(\\.|\\/)(gif|jpeg|jpg|png)$'
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
        if (endChar == 'i' || endChar == 'g') {
            end = end - 1;
        }
        return LangUtils.substring(jsRegex, start, end);
    }

    private static boolean isValidFileContent(PrimeApplicationContext primeAppContext, String allowedContentTypes,
                                              String fileName, InputStream stream) throws IOException {
        if (LangUtils.isBlank(allowedContentTypes)) {
            return true;
        }

        String prefix = FilenameUtils.removeExtension(fileName);
        Path tempFile = Files.createTempFile(prefix, Constants.EMPTY_STRING);
        String contentType;

        try (InputStream in = new PushbackInputStream(new BufferedInputStream(stream));
             OutputStream out = Files.newOutputStream(tempFile)) {
            IOUtils.copyLarge(in, out);

            contentType = primeAppContext.getFileTypeDetector().probeContentType(tempFile);

            // We want to force the FileTypeDetector impl to try to detect the file type by its content,
            // this is why we intentionally hide the original file name/extension at first because both
            // tika and mime-types go the easy way of looking up the file extension if possible.
            // If this first attempt failed we try again, but now while preserving the original file name/extension
            // to e.g. make the JDK default FileTypeDetector work
            if (contentType == null) {
                String extension = FilenameUtils.getExtension(fileName);
                if (!extension.isEmpty()) {
                    String newFileName = tempFile.getFileName().toString() + "." + extension;
                    tempFile = Files.move(tempFile, tempFile.resolveSibling(newFileName));
                    contentType = primeAppContext.getFileTypeDetector().probeContentType(tempFile);
                }
            }
        }
        finally {
            deleteFile(tempFile);
        }

        if (contentType == null) {
            LOGGER.log(Level.WARNING, () -> String.format("Could not determine content type of uploaded file %s", fileName));
            return false;
        }

        //Comma-separated values: file_extension|audio/*|video/*|image/*|media_type (see https://www.w3schools.com/tags/att_input_accept.asp)
        final String filenameLC = fileName.toLowerCase();
        final String contentTypeLC = contentType.toLowerCase();

        boolean accepted = Stream.of(allowedContentTypes.split(","))
                .map(String::trim)
                .anyMatch(allowedContentType -> {
                    // try with extension first
                    if (allowedContentType.startsWith(".") && filenameLC.endsWith(allowedContentType)) {
                        LOGGER.log(Level.FINE, () -> String.format("File extension %s of the uploaded file %s is accepted", allowedContentType, fileName));
                        return true;
                    }
                    // or try with IANA media types
                    if (FilenameUtils.wildcardMatch(contentTypeLC, allowedContentType)) {
                        LOGGER.log(Level.FINE,
                                () -> String.format("Content type %s of the uploaded file %s is accepted by %s", contentTypeLC, fileName, allowedContentType));
                        return true;
                    }

                    return false;
                });

        if (!accepted) {
            LOGGER.log(Level.FINE,
                    () -> String.format("Uploaded file %s with content type %s does not match the accept specification %s",
                            fileName, contentTypeLC, allowedContentTypes));
            return false;
        }

        return true;
    }

    private static void deleteFile(Path tempFile) {
        try {
            Files.delete(tempFile);
        }
        catch (Exception ex) {
            LOGGER.log(Level.WARNING, ex,
                    () -> String.format("Could not delete temporary file %s, will try to delete on JVM exit", tempFile.toAbsolutePath()));
            try {
                tempFile.toFile().deleteOnExit();
            }
            catch (Exception ex1) {
                LOGGER.log(Level.WARNING, ex1,
                        () -> String.format("Could not register temporary file %s for deletion on JVM exit", tempFile.toAbsolutePath()));
            }
        }
    }

    public static void performVirusScan(FacesContext facesContext, UploadedFile file) throws VirusException {
        PrimeApplicationContext.getCurrentInstance(facesContext).getVirusScannerService().performVirusScan(file);
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

    /**
     * Formats the allowTypes regex pattern in a more human-friendly format.
     * @param allowTypes The allowTypes regex pattern to format
     * @return The allowTypes formatted in a more human-friendly format.
     */
    public static String formatAllowTypes(String allowTypes) {
        // emtpy or null
        if (LangUtils.isBlank(allowTypes)) {
            return allowTypes;
        }

        // not a correct regex pattern
        if (!allowTypes.startsWith("/")) {
            return allowTypes;
        }

        // pattern to format allowedTypes for FileUpload - formats like /.*\.(xls|xlsx|csv|txt)/
        Matcher matcher1 = ALLOW_TYPES_PATTERN.matcher(allowTypes);
        if (matcher1.find()) {
            return "." + matcher1.group(1).replace("|", ", .");
        }

        // pattern to format allowedTypes for FileUpload - formats like /(\.|\/)(gif|jpeg|jpg|png)$/
        Matcher matcher2 = ALLOW_TYPES_PATTERN_2.matcher(allowTypes);
        if (matcher2.find()) {
            return "." + matcher2.group(1).replace("|", ", .");
        }

        // rest return unchanged
        return allowTypes;
    }

    /**
     * Formats the given data size in a more human-friendly format, e.g., `1.5 MB` etc.
     * @param bytes File size in bytes to format
     * @param locale The locale to use for number formatting
     * @return The given file size, formatted in a more human-friendly format. Returns empty string if bytes is null,
     *         or "N/A" if bytes is 0.
     */
    public static String formatBytes(Long bytes, Locale locale) {
        if (bytes == null) {
            return "";
        }

        if (bytes == 0) {
            return "N/A";
        }

        String[] sizes = new String[] {"B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        int i = (int) Math.floor(Math.log(bytes) / Math.log(1024));
        if (i == 0) {
            return bytes + " " + sizes[i];
        }
        else {
            DecimalFormat decimalFormat = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(locale));
            return decimalFormat.format((bytes / Math.pow(1024, i))) + " " + sizes[i];
        }
    }

    /**
     * Extracts file extensions from an HTML accept attribute and converts them to a JavaScript-compatible
     * regular expression pattern. Only processes dot-prefixed extensions (e.g., ".pdf", ".doc") and
     * ignores MIME types (e.g., "image/*", "text/plain").
     * @param accept Raw accept attribute from the component (e.g., ".pdf,.doc,image/*")
     * @return a regex pattern matching the file extensions (e.g., "/.*\.(pdf|doc)/"), or null if no valid extensions found
     */
    public static String extractAllowTypes(String accept) {
        if (LangUtils.isBlank(accept)) return null;
        return Arrays.stream(accept.split(","))
                .map(String::trim)
                .filter(part -> part.startsWith("."))
                .map(part -> part.substring(1).trim())
                .filter(ext -> !ext.isEmpty())
                .filter(ext -> ext.matches("[a-zA-Z0-9]{1,10}"))
                .distinct()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> list.isEmpty() ? null : "/.*\\.(" + String.join("|", list) + ")/"
                ));
    }

}

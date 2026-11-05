/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.fileupload;

import org.primefaces.model.file.NIOUploadedFile;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFileWrapper;
import org.primefaces.model.file.UploadedFiles;
import org.primefaces.model.file.UploadedFilesWrapper;
import org.primefaces.util.FileUploadUtils;
import org.primefaces.util.LangUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.FacesException;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;

public abstract class AbstractFileUploadDecoder<T extends HttpServletRequest> implements FileUploadDecoder, FileUploadChunkDecoder<T> {

    private static final Logger LOGGER = Logger.getLogger(AbstractFileUploadDecoder.class.getName());

    @Override
    public void decode(FacesContext context, FileUpload fileUpload) {
        T request = getRequest(context);

        try {
            String inputToDecodeId = resolveInputToDecodeId(context, fileUpload);
            if (fileUpload.getMode().equals("simple")) {
                decodeSimple(fileUpload, request, inputToDecodeId);
            }
            else {
                decodeAdvanced(fileUpload, request, inputToDecodeId);
            }
        }
        catch (IOException | ServletException e) {
            throw new FacesException(e);
        }
    }

    protected void decodeSimple(FileUpload fileUpload, T request, String inputToDecodeId) throws IOException, ServletException {
        if (fileUpload.isMultiple()) {
            List<UploadedFile> files = createUploadedFiles(request, fileUpload, inputToDecodeId);

            if (!files.isEmpty()) {
                UploadedFiles uploadedFiles = new UploadedFiles(files);
                fileUpload.setSubmittedValue(new UploadedFilesWrapper(uploadedFiles));
            }
            else {
                fileUpload.setSubmittedValue("");
            }
        }
        else {
            UploadedFile uploadedFile = createUploadedFile(request, fileUpload, inputToDecodeId);
            if (uploadedFile != null) {
                fileUpload.setSubmittedValue(new UploadedFileWrapper(uploadedFile));
            }
            else {
                fileUpload.setSubmittedValue("");
            }
        }
    }

    protected void decodeAdvanced(FileUpload fileUpload, T request, String inputToDecodeId) throws IOException, ServletException {
        UploadedFile uploadedFile = createUploadedFile(request, fileUpload, inputToDecodeId);
        if (uploadedFile != null) {
            if (isChunkedUpload(request)) {
                decodeContentRange(fileUpload, request, uploadedFile);
            }
            else {
                fileUpload.setSubmittedValue(new UploadedFileWrapper(uploadedFile));
            }
        }
    }

    protected String resolveInputToDecodeId(FacesContext context, FileUpload fileUpload) {
        String clientId = fileUpload.getClientId(context);
        String mode = fileUpload.getMode();
        if ("advanced".equals(mode) || "simple".equals(mode) && !fileUpload.isSkinSimple()) {
            return clientId;
        }
        else {
            return clientId + "_input";
        }
    }

    protected abstract List<UploadedFile> createUploadedFiles(T request, FileUpload fileUpload, String inputToDecodeId) throws IOException, ServletException;

    protected abstract UploadedFile createUploadedFile(T request, FileUpload fileUpload, String inputToDecodeId) throws IOException, ServletException;

    protected abstract T getRequest(FacesContext ctxt);

    @Override
    public void decodeContentRange(FileUpload fileUpload, T request, UploadedFile chunk) throws IOException {
        ContentRange contentRange = ContentRange.of(getContentRange(request), fileUpload.getMaxChunkSize());

        Path chunksDir = FileUploadUtils.getChunkDir(request);

        writeChunk(chunk, chunksDir, contentRange);

        if (contentRange.isLastChunk()) {
            UploadedFile uploadedFile = processLastChunk(request, chunk, chunksDir, contentRange, fileUpload.getSizeLimit());
            request.setAttribute(MULTIPARTS, uploadedFile);
            fileUpload.setSubmittedValue(new UploadedFileWrapper(uploadedFile));
        }
    }

    @Override
    public long decodeUploadedBytes(T request) {
        long uploadedBytes = 0;
        for (Path chunk : FileUploadUtils.listChunks(request)) {
            try {
                uploadedBytes += Files.size(chunk);
            }
            catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                break;
            }

        }
        return uploadedBytes;
    }

    @Override
    public void deleteChunks(T request) throws IOException {
        Path chunkDir = FileUploadUtils.getChunkDir(request);
        List<Path> chunks = FileUploadUtils.listChunks(chunkDir);
        deleteChunkFolder(chunkDir, chunks);
    }

    protected void writeChunk(UploadedFile uploadedFile, Path path, ContentRange contentRange) throws IOException {
        if (!path.toFile().exists()) {
            Files.createDirectory(path);
        }

        String chunkname = String.valueOf(contentRange.getPacket());
        Path chunkFile = Path.of(path.toFile().getAbsolutePath(), chunkname);
        try (InputStream is = uploadedFile.getInputStream()) {
            Files.copy(is, chunkFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    protected UploadedFile processLastChunk(T request, UploadedFile chunk, Path chunksDir, ContentRange contentRange, Long sizeLimit) throws IOException {
        String fileKey = generateFileInfoKey(request);
        List<Path> chunks = FileUploadUtils.listChunks(chunksDir);

        Path uploadDir = Path.of(getUploadDirectory(request)).toAbsolutePath().normalize();
        Path whole = resolveMergedFile(uploadDir, fileKey, chunk.getFileName());
        // path traversal protection: the merged file must reside directly inside the upload directory
        if (whole == null || !whole.startsWith(uploadDir) || !uploadDir.equals(whole.getParent())) {
            deleteChunkFolder(chunksDir, chunks);
            throw new IOException("Invalid file name for chunked upload");
        }

        Files.deleteIfExists(whole);
        Files.createFile(whole);

        for (Path p : chunks) {
            Files.write(whole, Files.readAllBytes(p), StandardOpenOption.APPEND);
        }

        deleteChunkFolder(chunksDir, chunks);

        if (Files.size(whole) != contentRange.getChunkTotalFileSize()) {
            Files.delete(whole);
            throw new IOException("Merged file does not meet expected size: " + contentRange.getChunkTotalFileSize());
        }

        return new NIOUploadedFile(whole, chunk.getFileName(), chunk.getContentType(), sizeLimit, FileUploadUtils.getWebkitRelativePath(request));
    }

    /**
     * Resolves the normalized merged file inside the upload directory, using only the name part of the given file name.
     *
     * @return the merged file or {@code null} if the file name is invalid
     */
    protected Path resolveMergedFile(Path uploadDir, String fileKey, String fileName) {
        try {
            String name = FilenameUtils.getName(fileName);
            if (LangUtils.isBlank(name) || ".".equals(name) || "..".equals(name)) {
                return null;
            }

            return uploadDir.resolve("[" + fileKey + "]" + name).normalize();
        }
        catch (IllegalArgumentException e) {
            // e.g. null character (FilenameUtils) or invalid characters for the file system (InvalidPathException)
            return null;
        }
    }

    protected String getContentRange(HttpServletRequest request) {
        return request.getHeader("Content-Range");
    }

    protected void deleteChunkFolder(Path chunksDir, List<Path> chunks) throws IOException {
        for (Path p : chunks) {
            Files.delete(p);
        }

        Files.delete(chunksDir);
    }

    protected boolean isChunkedUpload(T request) {
        return getContentRange(request) != null;
    }
}

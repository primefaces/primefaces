/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.primefaces.model.file.*;
import org.primefaces.util.FileUploadUtils;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            UploadedFile uploadedFile = processLastChunk(request, chunk, chunksDir, contentRange);
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
        if (Files.notExists(path)) {
            Files.createDirectory(path);
        }

        String chunkname = String.valueOf(contentRange.getPacket());
        Path chunkFile = Paths.get(path.toFile().getAbsolutePath(), chunkname);
        try (InputStream is = uploadedFile.getInputStream()) {
            Files.copy(is, chunkFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    protected UploadedFile processLastChunk(T request, UploadedFile chunk, Path chunksDir, ContentRange contentRange) throws IOException {
        String fileKey = generateFileInfoKey(request);
        Path whole = Paths.get(getUploadDirectory(request), "[" + fileKey +  "]" + chunk.getFileName());
        Files.deleteIfExists(whole);
        Files.createFile(whole);

        List<Path> chunks = FileUploadUtils.listChunks(chunksDir);
        for (Path p : chunks) {
            Files.write(whole, Files.readAllBytes(p), StandardOpenOption.APPEND);
        }

        deleteChunkFolder(chunksDir, chunks);

        if (Files.size(whole) != contentRange.getChunkTotalFileSize()) {
            Files.delete(whole);
            throw new IOException("Merged file does not meet expected size: " + contentRange.getChunkTotalFileSize());
        }

        return new NIOUploadedFile(whole, chunk.getFileName(), chunk.getContentType());
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

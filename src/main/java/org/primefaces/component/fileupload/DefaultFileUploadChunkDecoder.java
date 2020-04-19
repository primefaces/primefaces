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
package org.primefaces.component.fileupload;

import org.primefaces.model.file.NIOUploadedFile;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFileWrapper;
import org.primefaces.util.FileUploadUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;

public class DefaultFileUploadChunkDecoder implements FileUploadChunkDecoder {

    @Override
    public void decodeContentRange(FileUpload fileUpload, HttpServletRequest request, UploadedFile uploadedFile) throws IOException {
        ContentRange contentRange = ContentRange.of(getContentRange(request));

        String tmpDir =  getUploadDirectory();
        String basename = generateFileInfoKey(request);
        Path chunksDir = Paths.get(tmpDir, basename);

        writeChunk(fileUpload, uploadedFile, chunksDir, contentRange);

        if (contentRange.isLastChunk()) {
            UploadedFile file = processLastChunk(fileUpload, uploadedFile, chunksDir, contentRange);
            fileUpload.setSubmittedValue(new UploadedFileWrapper(file));
        }
    }

    protected void writeChunk(FileUpload fileUpload, UploadedFile uploadedFile, Path path, ContentRange contentRange) throws IOException {
        if (Files.notExists(path)) {
            Files.createDirectory(path);
        }

        long packet = contentRange.getChunkRangeBegin() / fileUpload.getMaxChunkSize();
        String chunkname = String.valueOf(packet);
        Path chunkFile = Paths.get(path.toFile().getAbsolutePath(), chunkname);
        try (InputStream is = uploadedFile.getInputStream()) {
            Files.copy(is, chunkFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    protected UploadedFile processLastChunk(FileUpload fileUpload, UploadedFile chunk, Path chunksDir, ContentRange contentRange) throws IOException {
        Path wholePath = Paths.get(getUploadDirectory(), chunk.getFileName());
        Files.deleteIfExists(wholePath);
        Path whole = Files.createFile(wholePath);

        List<Path> chunks = FileUploadUtils.listChunks(chunksDir);
        for (Path p : chunks) {
            Files.write(whole, Files.readAllBytes(p), StandardOpenOption.APPEND);
        }

        deleteChunkFolder(chunksDir, chunks);

        if (Files.size(whole) != contentRange.getChunkTotalFileSize()) {
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

}

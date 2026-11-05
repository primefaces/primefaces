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

import org.primefaces.model.file.UploadedFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import jakarta.faces.context.FacesContext;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbstractFileUploadDecoderTest {

    private static final String FILE_KEY = "42";

    @TempDir
    Path tempDir;

    private Path uploadDir;
    private Path chunksDir;
    private HttpServletRequest request;
    private TestDecoder decoder;

    @BeforeEach
    void setup() throws IOException {
        uploadDir = Files.createDirectory(tempDir.resolve("upload"));
        chunksDir = Files.createDirectory(uploadDir.resolve(FILE_KEY));
        request = mock(HttpServletRequest.class);
        decoder = new TestDecoder(uploadDir.toString());

        Files.writeString(chunksDir.resolve("0"), "Hello ");
        Files.writeString(chunksDir.resolve("1"), "World");
    }

    @Test
    void processLastChunkMergesChunks() throws IOException {
        UploadedFile result = decoder.processLastChunk(request, chunk("test.txt"), chunksDir, range(), null);

        Path merged = uploadDir.resolve("[" + FILE_KEY + "]test.txt");
        assertTrue(Files.exists(merged));
        assertEquals("Hello World", Files.readString(merged));
        assertEquals("test.txt", result.getFileName());
        assertArrayEquals("Hello World".getBytes(StandardCharsets.UTF_8), result.getContent());
        assertNoChunksLeft();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "../evil.txt",
        "..\\evil.txt",
        "../../evil.txt",
        "..\\..\\evil.txt",
        "sub/../../evil.txt",
        "/evil.txt",
        "C:\\evil.txt"
    })
    void processLastChunkStripsPathFromFileName(String fileName) throws IOException {
        decoder.processLastChunk(request, chunk(fileName), chunksDir, range(), null);

        assertTrue(Files.exists(uploadDir.resolve("[" + FILE_KEY + "]evil.txt")));
        assertFalse(Files.exists(tempDir.resolve("evil.txt")));
        assertFalse(Files.exists(tempDir.resolve("[" + FILE_KEY + "]evil.txt")));
        assertFalse(Files.exists(uploadDir.resolve("evil.txt")));
        assertNoChunksLeft();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "..", ".", "sub/..", "sub\\..", "sub/"})
    void processLastChunkRejectsInvalidFileName(String fileName) throws IOException {
        Path outside = tempDir.resolve("keep.txt");
        Files.writeString(outside, "keep");

        assertThrows(IOException.class, () -> decoder.processLastChunk(request, chunk(fileName), chunksDir, range(), null));

        assertNoChunksLeft();
        assertEquals("keep", Files.readString(outside));
        try (Stream<Path> files = Files.list(uploadDir)) {
            assertEquals(0, files.count(), "no merged file must be created");
        }
    }

    @Test
    void processLastChunkRejectsNullFileName() {
        assertThrows(IOException.class, () -> decoder.processLastChunk(request, chunk(null), chunksDir, range(), null));
    }

    @Test
    void resolveMergedFileStaysInUploadDirectory() {
        Path dir = uploadDir.toAbsolutePath().normalize();

        assertEquals(dir.resolve("[1]a.txt"), decoder.resolveMergedFile(uploadDir.toString(), "1", "a.txt"));
        assertEquals(dir.resolve("[1]a.txt"), decoder.resolveMergedFile(uploadDir.toString(), "1", "../../a.txt"));
        assertEquals(dir.resolve("[1]a.txt"), decoder.resolveMergedFile(uploadDir.toString(), "1", "..\\..\\a.txt"));
        assertNull(decoder.resolveMergedFile(uploadDir.toString(), "1", ".."));
        assertNull(decoder.resolveMergedFile(uploadDir.toString(), "1", null));
    }

    @Test
    void uploadDirectoryFallsBackToJavaTmpDir() {
        ServletContext servletContext = mock(ServletContext.class);
        when(request.getServletContext()).thenReturn(servletContext);

        String dir = new TestDecoder(null).getUploadDirectory(request);

        assertEquals(Path.of(System.getProperty("java.io.tmpdir")).toAbsolutePath(), Path.of(dir));
    }

    private void assertNoChunksLeft() {
        assertFalse(Files.exists(chunksDir), "chunk folder must be deleted");
    }

    private static UploadedFile chunk(String fileName) {
        UploadedFile chunk = mock(UploadedFile.class);
        when(chunk.getFileName()).thenReturn(fileName);
        when(chunk.getContentType()).thenReturn("text/plain");
        return chunk;
    }

    private static ContentRange range() {
        return ContentRange.of("bytes 6-10/11", 6);
    }

    private static final class TestDecoder extends AbstractFileUploadDecoder<HttpServletRequest> {

        private final String uploadDirectory;

        TestDecoder(String uploadDirectory) {
            this.uploadDirectory = uploadDirectory;
        }

        @Override
        public String getName() {
            return "test";
        }

        @Override
        public String generateFileInfoKey(HttpServletRequest request) {
            return FILE_KEY;
        }

        @Override
        public String getUploadDirectory(HttpServletRequest request) {
            return uploadDirectory == null ? super.getUploadDirectory(request) : uploadDirectory;
        }

        @Override
        protected List<UploadedFile> createUploadedFiles(HttpServletRequest request, FileUpload fileUpload, String inputToDecodeId) {
            return List.of();
        }

        @Override
        protected UploadedFile createUploadedFile(HttpServletRequest request, FileUpload fileUpload, String inputToDecodeId) {
            return null;
        }

        @Override
        protected HttpServletRequest getRequest(FacesContext ctxt) {
            return null;
        }
    }
}

package org.primefaces.model.file;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.util.FileUploadUtils;

import javax.faces.FacesException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NIOUploadedFile implements UploadedFile {

    private Path file;
    private String filename;
    private byte[] content;
    private String contentType;

    public NIOUploadedFile() {
        // NOOP
    }

    public NIOUploadedFile(Path file, String filename, String contentType) {
        this.file = file;
        this.filename = filename;
        this.contentType = contentType;
    }

    @Override
    public String getFileName() {
        return filename;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(file);
    }

    @Override
    public byte[] getContent() {
        if (content == null) {
            try {
                content = Files.readAllBytes(file);
            } catch (IOException e) {
                throw new FacesException(e);
            }
        }
        return content;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public long getSize() {
        try {
            return Files.size(file);
        } catch (IOException e) {
            throw new FacesException(e);
        }
    }

    @Override
    public void write(String filePath) throws Exception {
        String validFileName = FileUploadUtils.getValidFilename(FilenameUtils.getName(getFileName()));
        Files.copy(file, Paths.get(validFileName));
    }
}

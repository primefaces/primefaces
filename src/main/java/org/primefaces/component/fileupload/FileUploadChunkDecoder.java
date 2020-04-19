package org.primefaces.component.fileupload;

import org.primefaces.model.file.UploadedFile;

import javax.faces.FacesException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface FileUploadChunkDecoder {

    default String generateFileInfoKey(HttpServletRequest request) {
        String fileInfo = request.getParameter("X-File-Id");
        if (fileInfo == null) {
            throw new FacesException("Missing X-File-Id header");
        }

        return String.valueOf(fileInfo.hashCode());
    }

    default String getUploadDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    void decodeContentRange(FileUpload fileUpload, HttpServletRequest request, UploadedFile uploadedFile) throws IOException;
}

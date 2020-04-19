package org.primefaces.webapp;

import org.json.JSONException;
import org.json.JSONObject;
import org.primefaces.util.FileUploadUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUploadResumeServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(FileUploadResumeServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        long uploadedBytes = FileUploadUtils.listChunks(req).stream()
                .mapToLong(this::getSize)
                .sum();

        printUploadedBytes(resp, uploadedBytes);
    }

    protected long getSize(Path p) {
        try {
            return Files.size(p);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return 0;
    }

    protected void printUploadedBytes(HttpServletResponse response, long uploadedBytes) {
        try (PrintWriter w = response.getWriter()) {
            response.setContentType("application/json");
            JSONObject json = new JSONObject();
            json.put("uploadedBytes", uploadedBytes);
            w.print(json.toString());
        } catch (IOException | JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}

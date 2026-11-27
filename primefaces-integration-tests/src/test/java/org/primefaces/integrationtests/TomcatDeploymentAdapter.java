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
package org.primefaces.integrationtests;

import org.primefaces.selenium.spi.DeploymentAdapter;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.scan.StandardJarScanner;

public class TomcatDeploymentAdapter implements DeploymentAdapter {

    private Tomcat tomcat;
    private Path tempDir;

    @Override
    public void startup() throws Exception {
        tempDir = Files.createTempDirectory(UUID.randomUUID().toString());

        tomcat = new Tomcat();
        tomcat.setBaseDir(tempDir.toString());
        tomcat.setPort(createRandomPort());
        tomcat.getConnector().setMaxPartCount(20); // increase multipart limit
        tomcat.getConnector().setMaxPartHeaderSize(5 * 1024); // increase multipart header size limit
        tomcat.getHost().setAppBase(".");

        File webapp = new File("target/primefaces-integration-tests/");
        Context context = tomcat.addWebapp("", webapp.getAbsolutePath());
        context.setParentClassLoader(new WebappParentClassLoader(getClass().getClassLoader(), webapp));
        StandardJarScanner jarScanner = new StandardJarScanner();
        jarScanner.setScanClassPath(false);
        jarScanner.setScanBootstrapClassPath(false);
        context.setJarScanner(jarScanner);

        tomcat.start();

        Thread.sleep(1000);

        // trigger Jakarta Faces impl lazy startup
        URL url = new URL(getBaseUrl());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.disconnect();

        Thread.sleep(2000);
    }

    @Override
    public String getBaseUrl() {
        return "http://localhost:" + tomcat.getConnector().getPort() + "/";
    }

    @Override
    public void shutdown() throws LifecycleException {
        tomcat.getServer().stop();
        tomcat.getServer().destroy();

        try {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static int createRandomPort() {
        Random random = new Random();
        return random.nextInt((9000 - 8000) + 1) + 8000;
    }

    public Tomcat getTomcat() {
        return tomcat;
    }

    /**
     * The test classpath contains the JARs and classes of the webapp a second time (WEB-INF/lib and WEB-INF/classes).
     * Hides their resources from the webapp, otherwise e.g. the META-INF/faces-config.xml of PrimeFaces is loaded twice.
     */
    private static class WebappParentClassLoader extends ClassLoader {

        private final Set<String> webappLibs;

        WebappParentClassLoader(ClassLoader parent, File webapp) {
            super(parent);
            String[] libs = new File(webapp, "WEB-INF/lib").list();
            webappLibs = libs == null ? Set.of() : Set.of(libs);
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            List<URL> resources = Collections.list(super.getResources(name));
            resources.removeIf(this::isProvidedByWebapp);
            return Collections.enumeration(resources);
        }

        private boolean isProvidedByWebapp(URL url) {
            String location = url.toString();
            if ("jar".equals(url.getProtocol())) {
                String jar = location.substring(0, location.indexOf("!/"));
                return webappLibs.contains(jar.substring(jar.lastIndexOf('/') + 1));
            }
            return location.contains("/target/classes/");
        }
    }

}

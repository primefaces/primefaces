/**
 * This file is part of the Open Web Application Security Project (OWASP) Java File IO Security project. For details, please see
 * <a href="https://www.owasp.org/index.php/OWASP_Java_File_I_O_Security_Project">https://www.owasp.org/index.php/OWASP_Java_File_I_O_Security_Project</a>.
 *
 * Copyright (c) 2014 - The OWASP Foundation
 *
 * This API is published by OWASP under the Apache 2.0 license.
 * You should read and accept the LICENSE before you use, modify, and/or redistribute this software.
 *
 * @author Arshan Dabirsiaghi <a href="http://www.aspectsecurity.com">Aspect Security</a>
 * @author August Detlefsen <a href="http://www.codemagi.com">CodeMagi</a> - Java File IO Security Project lead
 * @created 2014
 */
package org.primefaces.util;

import java.io.File;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ValidationException;

/**
 * Extension to java.io.File to prevent against null byte injections
 * and other unforeseen problems resulting from unprintable characters causing problems in path lookups.
 * This does _not_ prevent against directory traversal attacks.
 */
public class SafeFile extends File {

    private static final long serialVersionUID = 1L;
    private static final Pattern PERCENTS_PAT = Pattern.compile("(%)([0-9a-fA-F])([0-9a-fA-F])");
    private static final Pattern FILE_BLACKLIST_PAT = Pattern.compile("([\\\\/:*?<>|])");
    private static final Pattern DIR_BLACKLIST_PAT = Pattern.compile("([*?<>|])");

    public SafeFile(String path) throws ValidationException {
        super(path);
        doDirCheck(this.getParent());
        doFileCheck(this.getName());
    }

    public SafeFile(String parent, String child) throws ValidationException {
        super(parent, child);
        doDirCheck(this.getParent());
        doFileCheck(this.getName());
    }

    public SafeFile(File parent, String child) throws ValidationException {
        super(parent, child);
        doDirCheck(this.getParent());
        doFileCheck(this.getName());
    }

    public SafeFile(URI uri) throws ValidationException {
        super(uri);
        doDirCheck(this.getParent());
        doFileCheck(this.getName());
    }

    private void doDirCheck(String path) throws ValidationException {
        if (path == null) return;
        Matcher m1 = DIR_BLACKLIST_PAT.matcher(path);
        if (m1.find()) {
            throw new ValidationException("Invalid directory", new Throwable("Directory path (" + path + ") contains illegal character: " + m1.group()));
        }

        Matcher m2 = PERCENTS_PAT.matcher(path);
        if (m2.find()) {
            throw new ValidationException("Invalid directory", new Throwable("Directory path (" + path + ") contains encoded characters: " + m2.group()));
        }

        int ch = containsUnprintableCharacters(path);
        if (ch != -1) {
            throw new ValidationException("Invalid directory", new Throwable("Directory path (" + path + ") contains unprintable character: " + ch));
        }
    }

    private void doFileCheck(String path) throws ValidationException {
        Matcher m1 = FILE_BLACKLIST_PAT.matcher(path);
        if (m1.find()) {
            throw new ValidationException("Invalid directory", new Throwable("Directory path (" + path + ") contains illegal character: " + m1.group()));
        }

        Matcher m2 = PERCENTS_PAT.matcher(path);
        if (m2.find()) {
            throw new ValidationException("Invalid file", new Throwable("File path (" + path + ") contains encoded characters: " + m2.group()));
        }

        int ch = containsUnprintableCharacters(path);
        if (ch != -1) {
            throw new ValidationException("Invalid file", new Throwable("File path (" + path + ") contains unprintable character: " + ch));
        }
    }

    private int containsUnprintableCharacters(String s) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (((int) ch) < 32 || ((int) ch) > 126) {
                return (int) ch;
            }
        }
        return -1;
    }
}
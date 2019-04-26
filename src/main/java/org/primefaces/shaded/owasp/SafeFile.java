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
package org.primefaces.shaded.owasp;

import java.io.File;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Extension to java.io.File to prevent against null byte injections and
 * other unforeseen problems resulting from unprintable characters
 * causing problems in path lookups. This does _not_ prevent against
 * directory traversal attacks.
 *
 * OWASP Enterprise Security API (ESAPI)
 *
 * This file is part of the Open Web Application Security Project (OWASP)
 * Enterprise Security API (ESAPI) project. For details, please see
 * <a href="http://www.owasp.org/index.php/ESAPI">http://www.owasp.org/index.php/ESAPI</a>.
 *
 * Copyright (c) 2008 - The OWASP Foundation
 *
 * The ESAPI is published by OWASP under the BSD license. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 *
 * @author Arshan Dabirsiaghi <a href="http://www.aspectsecurity.com">Aspect Security</a>
 * @see https://github.com/ESAPI/esapi-java-legacy
 * @created 2008
 */
public class SafeFile extends File {

    private static final long serialVersionUID = 1L;
    private static final Pattern PERCENTS_PAT = Pattern.compile("(%)([0-9a-fA-F])([0-9a-fA-F])");
    private static final Pattern FILE_BLACKLIST_PAT = Pattern.compile("([\\\\/:*?<>|^])");
    private static final Pattern DIR_BLACKLIST_PAT = Pattern.compile("([*?<>|^])");

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
        Matcher m1 = DIR_BLACKLIST_PAT.matcher( path );
        if ( null != m1 && m1.find() ) {
            throw new ValidationException( "Invalid directory", "Directory path (" + path + ") contains illegal character: " + m1.group() );
        }

        Matcher m2 = PERCENTS_PAT.matcher( path );
        if (null != m2 &&  m2.find() ) {
            throw new ValidationException( "Invalid directory", "Directory path (" + path + ") contains encoded characters: " + m2.group() );
        }

        int ch = containsUnprintableCharacters(path);
        if (ch != -1) {
            throw new ValidationException("Invalid directory", "Directory path (" + path + ") contains unprintable character: " + ch);
        }
    }

    private void doFileCheck(String path) throws ValidationException {
        Matcher m1 = FILE_BLACKLIST_PAT.matcher( path );
        if ( m1.find() ) {
            throw new ValidationException( "Invalid directory", "Directory path (" + path + ") contains illegal character: " + m1.group() );
        }

        Matcher m2 = PERCENTS_PAT.matcher( path );
        if ( m2.find() ) {
            throw new ValidationException( "Invalid file", "File path (" + path + ") contains encoded characters: " + m2.group() );
        }

        int ch = containsUnprintableCharacters(path);
        if (ch != -1) {
            throw new ValidationException("Invalid file", "File path (" + path + ") contains unprintable character: " + ch);
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

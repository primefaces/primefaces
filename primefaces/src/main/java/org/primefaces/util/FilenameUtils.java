/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.util;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * General file name and file path manipulation utilities.
 * <p>
 * When dealing with file names you can hit problems when moving from a Windows based development machine to a Unix based production machine. This class aims to
 * help avoid those problems.
 * </p>
 * <p>
 * <b>NOTE</b>: You may be able to avoid using this class entirely simply by using JDK {@link java.io.File File} objects and the two argument constructor
 * {@link java.io.File#File(java.io.File, String) File(File,String)}.
 * </p>
 * <p>
 * Most methods on this class are designed to work the same on both Unix and Windows. Those that don't include 'System', 'Unix' or 'Windows' in their name.
 * </p>
 * <p>
 * Most methods recognize both separators (forward and back), and both sets of prefixes. See the Javadoc of each method for details.
 * </p>
 * <p>
 * This class defines six components within a file name (example C:\dev\project\file.txt):
 * </p>
 * <ul>
 * <li>the prefix - C:\</li>
 * <li>the path - dev\project\</li>
 * <li>the full path - C:\dev\project\</li>
 * <li>the name - file.txt</li>
 * <li>the base name - file</li>
 * <li>the extension - txt</li>
 * </ul>
 * <p>
 * Note that this class works best if directory file names end with a separator. If you omit the last separator, it is impossible to determine if the file name
 * corresponds to a file or a directory. As a result, we have chosen to say it corresponds to a file.
 * </p>
 * <p>
 * This class only supports Unix and Windows style names. Prefixes are matched as follows:
 * </p>
 *
 * <pre>
 * Windows:
 * a\b\c.txt           --&gt; ""          --&gt; relative
 * \a\b\c.txt          --&gt; "\"         --&gt; current drive absolute
 * C:a\b\c.txt         --&gt; "C:"        --&gt; drive relative
 * C:\a\b\c.txt        --&gt; "C:\"       --&gt; absolute
 * \\server\a\b\c.txt  --&gt; "\\server\" --&gt; UNC
 *
 * Unix:
 * a/b/c.txt           --&gt; ""          --&gt; relative
 * /a/b/c.txt          --&gt; "/"         --&gt; absolute
 * ~/a/b/c.txt         --&gt; "~/"        --&gt; current user
 * ~                   --&gt; "~/"        --&gt; current user (slash added)
 * ~user/a/b/c.txt     --&gt; "~user/"    --&gt; named user
 * ~user               --&gt; "~user/"    --&gt; named user (slash added)
 * </pre>
 * <p>
 * Both prefix styles are matched always, irrespective of the machine that you are currently running on.
 * </p>
 * <p>
 * Provenance: Excalibur, Alexandria, Tomcat, Commons-Utils.
 * </p>
 *
 * @see https://github.com/apache/commons-io/blob/rel/commons-io-2.15.1/src/main/java/org/apache/commons/io/FilenameUtils.java
 */
public class FilenameUtils {

    /**
     * The extension separator character.
     */
    public static final char EXTENSION_SEPARATOR = '.';

    /**
     * The extension separator String.
     */
    public static final String EXTENSION_SEPARATOR_STR = Character.toString(EXTENSION_SEPARATOR);

    private static final String[] EMPTY_STRING_ARRAY = {};

    private static final String EMPTY_STRING = "";

    private static final int NOT_FOUND = -1;

    /**
     * The Unix separator character.
     */
    private static final char UNIX_NAME_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_NAME_SEPARATOR = '\\';

    /**
     * The system separator character.
     */
    private static final char SYSTEM_NAME_SEPARATOR = File.separatorChar;

    /**
     * The separator character that is the opposite of the system separator.
     */
    private static final char OTHER_SEPARATOR = flipSeparator(SYSTEM_NAME_SEPARATOR);

    private static final Pattern IPV4_PATTERN = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");

    private static final int IPV4_MAX_OCTET_VALUE = 255;

    private static final int IPV6_MAX_HEX_GROUPS = 8;

    private static final int IPV6_MAX_HEX_DIGITS_PER_GROUP = 4;

    private static final int MAX_UNSIGNED_SHORT = 0xffff;

    private static final int BASE_16 = 16;

    private static final Pattern REG_NAME_PART_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9-]*$");

    /**
     * Instances should NOT be constructed in standard programming.
     */
    private FilenameUtils() {
    }

    /**
     * Concatenates a fileName to a base path using normal command line style rules.
     * <p>
     * The effect is equivalent to resultant directory after changing directory to the first argument, followed by changing directory to the second argument.
     * </p>
     * <p>
     * The first argument is the base path, the second is the path to concatenate. The returned path is always normalized via {@link #normalize(String)}, thus
     * {@code ..} is handled.
     * </p>
     * <p>
     * If {@code pathToAdd} is absolute (has an absolute prefix), then it will be normalized and returned. Otherwise, the paths will be joined, normalized and
     * returned.
     * </p>
     * <p>
     * The output will be the same on both Unix and Windows except for the separator character.
     * </p>
     *
     * <pre>
     * /foo/      + bar        --&gt;  /foo/bar
     * /foo       + bar        --&gt;  /foo/bar
     * /foo       + /bar       --&gt;  /bar
     * /foo       + C:/bar     --&gt;  C:/bar
     * /foo       + C:bar      --&gt;  C:bar [1]
     * /foo/a/    + ../bar     --&gt;  /foo/bar
     * /foo/      + ../../bar  --&gt;  null
     * /foo/      + /bar       --&gt;  /bar
     * /foo/..    + /bar       --&gt;  /bar
     * /foo       + bar/c.txt  --&gt;  /foo/bar/c.txt
     * /foo/c.txt + bar        --&gt;  /foo/c.txt/bar [2]
     * </pre>
     * <p>
     * [1] Note that the Windows relative drive prefix is unreliable when used with this method.
     * </p>
     * <p>
     * [2] Note that the first parameter must be a path. If it ends with a name, then the name will be built into the concatenated path. If this might be a
     * problem, use {@link #getFullPath(String)} on the base path argument.
     * </p>
     *
     * @param basePath the base path to attach to, always treated as a path
     * @param fullFileNameToAdd the fileName (or path) to attach to the base
     * @return the concatenated path, or null if invalid
     * @throws IllegalArgumentException if the result path contains the null character ({@code U+0000})
     */
    public static String concat(final String basePath, final String fullFileNameToAdd) {
        final int prefix = getPrefixLength(fullFileNameToAdd);
        if (prefix < 0) {
            return null;
        }
        if (prefix > 0) {
            return normalize(fullFileNameToAdd);
        }
        if (basePath == null) {
            return null;
        }
        final int len = basePath.length();
        if (len == 0) {
            return normalize(fullFileNameToAdd);
        }
        final char ch = basePath.charAt(len - 1);
        if (isSeparator(ch)) {
            return normalize(basePath + fullFileNameToAdd);
        }
        return normalize(basePath + '/' + fullFileNameToAdd);
    }

    /**
     * Determines whether the {@code parent} directory contains the {@code child} element (a file or directory).
     * <p>
     * The files names are expected to be normalized.
     * </p>
     *
     * Edge cases:
     * <ul>
     * <li>A {@code directory} must not be null: if null, throw IllegalArgumentException</li>
     * <li>A directory does not contain itself: return false</li>
     * <li>A null child file is not contained in any parent: return false</li>
     * </ul>
     *
     * @param canonicalParent the file to consider as the parent.
     * @param canonicalChild the file to consider as the child.
     * @return true is the candidate leaf is under by the specified composite. False otherwise.
     * @see FileUtils#directoryContains(File, File)
     */
    public static boolean directoryContains(final String canonicalParent, final String canonicalChild) {
        if (isEmpty(canonicalParent) || isEmpty(canonicalChild)) {
            return false;
        }

        if (IOCase.SYSTEM.checkEquals(canonicalParent, canonicalChild)) {
            return false;
        }

        final char separator = toSeparator(canonicalParent.charAt(0) == UNIX_NAME_SEPARATOR);
        final String parentWithEndSeparator = canonicalParent.charAt(canonicalParent.length() - 1) == separator ? canonicalParent : canonicalParent + separator;

        return IOCase.SYSTEM.checkStartsWith(canonicalChild, parentWithEndSeparator);
    }

    /**
     * Does the work of getting the path.
     *
     * @param fileName the fileName
     * @param includeSeparator true to include the end separator
     * @return the path
     * @throws IllegalArgumentException if the result path contains the null character ({@code U+0000})
     */
    private static String doGetFullPath(final String fileName, final boolean includeSeparator) {
        if (fileName == null) {
            return null;
        }
        final int prefix = getPrefixLength(fileName);
        if (prefix < 0) {
            return null;
        }
        if (prefix >= fileName.length()) {
            if (includeSeparator) {
                return getPrefix(fileName); // add end slash if necessary
            }
            return fileName;
        }
        final int index = indexOfLastSeparator(fileName);
        if (index < 0) {
            return fileName.substring(0, prefix);
        }
        int end = index + (includeSeparator ? 1 : 0);
        if (end == 0) {
            end++;
        }
        return fileName.substring(0, end);
    }

    /**
     * Does the work of getting the path.
     *
     * @param fileName the fileName
     * @param separatorAdd 0 to omit the end separator, 1 to return it
     * @return the path
     * @throws IllegalArgumentException if the result path contains the null character ({@code U+0000})
     */
    private static String doGetPath(final String fileName, final int separatorAdd) {
        if (fileName == null) {
            return null;
        }
        final int prefix = getPrefixLength(fileName);
        if (prefix < 0) {
            return null;
        }
        final int index = indexOfLastSeparator(fileName);
        final int endIndex = index + separatorAdd;
        if (prefix >= fileName.length() || index < 0 || prefix >= endIndex) {
            return EMPTY_STRING;
        }
        return requireNonNullChars(fileName.substring(prefix, endIndex));
    }

    /**
     * Internal method to perform the normalization.
     *
     * @param fileName the fileName
     * @param separator The separator character to use
     * @param keepSeparator true to keep the final separator
     * @return the normalized fileName
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    private static String doNormalize(final String fileName, final char separator, final boolean keepSeparator) {
        if (fileName == null) {
            return null;
        }

        requireNonNullChars(fileName);

        int size = fileName.length();
        if (size == 0) {
            return fileName;
        }
        final int prefix = getPrefixLength(fileName);
        if (prefix < 0) {
            return null;
        }

        final char[] array = new char[size + 2]; // +1 for possible extra slash, +2 for arraycopy
        fileName.getChars(0, fileName.length(), array, 0);

        // fix separators throughout
        final char otherSeparator = flipSeparator(separator);
        for (int i = 0; i < array.length; i++) {
            if (array[i] == otherSeparator) {
                array[i] = separator;
            }
        }

        // add extra separator on the end to simplify code below
        boolean lastIsDirectory = true;
        if (array[size - 1] != separator) {
            array[size++] = separator;
            lastIsDirectory = false;
        }

        // adjoining slashes
        // If we get here, prefix can only be 0 or greater, size 1 or greater
        // If prefix is 0, set loop start to 1 to prevent index errors
        for (int i = prefix != 0 ? prefix : 1; i < size; i++) {
            if (array[i] == separator && array[i - 1] == separator) {
                System.arraycopy(array, i, array, i - 1, size - i);
                size--;
                i--;
            }
        }

        // dot slash
        for (int i = prefix + 1; i < size; i++) {
            if (array[i] == separator && array[i - 1] == '.' && (i == prefix + 1 || array[i - 2] == separator)) {
                if (i == size - 1) {
                    lastIsDirectory = true;
                }
                System.arraycopy(array, i + 1, array, i - 1, size - i);
                size -= 2;
                i--;
            }
        }

        // double dot slash
        outer: for (int i = prefix + 2; i < size; i++) {
            if (array[i] == separator && array[i - 1] == '.' && array[i - 2] == '.' && (i == prefix + 2 || array[i - 3] == separator)) {
                if (i == prefix + 2) {
                    return null;
                }
                if (i == size - 1) {
                    lastIsDirectory = true;
                }
                int j;
                for (j = i - 4; j >= prefix; j--) {
                    if (array[j] == separator) {
                        // remove b/../ from a/b/../c
                        System.arraycopy(array, i + 1, array, j + 1, size - i);
                        size -= i - j;
                        i = j + 1;
                        continue outer;
                    }
                }
                // remove a/../ from a/../c
                System.arraycopy(array, i + 1, array, prefix, size - i);
                size -= i + 1 - prefix;
                i = prefix + 1;
            }
        }

        if (size <= 0) { // should never be less than 0
            return EMPTY_STRING;
        }
        if (size <= prefix) { // should never be less than prefix
            return new String(array, 0, size);
        }
        if (lastIsDirectory && keepSeparator) {
            return new String(array, 0, size); // keep trailing separator
        }
        return new String(array, 0, size - 1); // lose trailing separator
    }

    /**
     * Checks whether two fileNames are equal exactly.
     * <p>
     * No processing is performed on the fileNames other than comparison, thus this is merely a null-safe case-sensitive equals.
     * </p>
     *
     * @param fileName1 the first fileName to query, may be null
     * @param fileName2 the second fileName to query, may be null
     * @return true if the fileNames are equal, null equals null
     * @see IOCase#SENSITIVE
     */
    public static boolean equals(final String fileName1, final String fileName2) {
        return equals(fileName1, fileName2, false, IOCase.SENSITIVE);
    }

    /**
     * Checks whether two fileNames are equal, optionally normalizing and providing control over the case-sensitivity.
     *
     * @param fileName1 the first fileName to query, may be null
     * @param fileName2 the second fileName to query, may be null
     * @param normalize whether to normalize the fileNames
     * @param ioCase what case sensitivity rule to use, null means case-sensitive
     * @return true if the fileNames are equal, null equals null
     */
    public static boolean equals(String fileName1, String fileName2, final boolean normalize, final IOCase ioCase) {

        if (fileName1 == null || fileName2 == null) {
            return fileName1 == null && fileName2 == null;
        }
        if (normalize) {
            fileName1 = normalize(fileName1);
            if (fileName1 == null) {
                return false;
            }
            fileName2 = normalize(fileName2);
            if (fileName2 == null) {
                return false;
            }
        }
        return IOCase.value(ioCase, IOCase.SENSITIVE).checkEquals(fileName1, fileName2);
    }

    /**
     * Checks whether two fileNames are equal after both have been normalized.
     * <p>
     * Both fileNames are first passed to {@link #normalize(String)}. The check is then performed in a case-sensitive manner.
     * </p>
     *
     * @param fileName1 the first fileName to query, may be null
     * @param fileName2 the second fileName to query, may be null
     * @return true if the fileNames are equal, null equals null
     * @see IOCase#SENSITIVE
     */
    public static boolean equalsNormalized(final String fileName1, final String fileName2) {
        return equals(fileName1, fileName2, true, IOCase.SENSITIVE);
    }

    /**
     * Checks whether two fileNames are equal after both have been normalized and using the case rules of the system.
     * <p>
     * Both fileNames are first passed to {@link #normalize(String)}. The check is then performed case-sensitive on Unix and case-insensitive on Windows.
     * </p>
     *
     * @param fileName1 the first fileName to query, may be null
     * @param fileName2 the second fileName to query, may be null
     * @return true if the fileNames are equal, null equals null
     * @see IOCase#SYSTEM
     */
    public static boolean equalsNormalizedOnSystem(final String fileName1, final String fileName2) {
        return equals(fileName1, fileName2, true, IOCase.SYSTEM);
    }

    /**
     * Checks whether two fileNames are equal using the case rules of the system.
     * <p>
     * No processing is performed on the fileNames other than comparison. The check is case-sensitive on Unix and case-insensitive on Windows.
     * </p>
     *
     * @param fileName1 the first fileName to query, may be null
     * @param fileName2 the second fileName to query, may be null
     * @return true if the fileNames are equal, null equals null
     * @see IOCase#SYSTEM
     */
    public static boolean equalsOnSystem(final String fileName1, final String fileName2) {
        return equals(fileName1, fileName2, false, IOCase.SYSTEM);
    }

    /**
     * Flips the Windows name separator to Linux and vice-versa.
     *
     * @param ch The Windows or Linux name separator.
     * @return The Windows or Linux name separator.
     */
    static char flipSeparator(final char ch) {
        if (ch == UNIX_NAME_SEPARATOR) {
            return WINDOWS_NAME_SEPARATOR;
        }
        if (ch == WINDOWS_NAME_SEPARATOR) {
            return UNIX_NAME_SEPARATOR;
        }
        throw new IllegalArgumentException(String.valueOf(ch));
    }

    /**
     * Special handling for NTFS ADS: Don't accept colon in the fileName.
     *
     * @param fileName a file name
     * @return ADS offsets.
     */
    private static int getAdsCriticalOffset(final String fileName) {
        // Step 1: Remove leading path segments.
        final int offset1 = fileName.lastIndexOf(SYSTEM_NAME_SEPARATOR);
        final int offset2 = fileName.lastIndexOf(OTHER_SEPARATOR);
        if (offset1 == -1) {
            if (offset2 == -1) {
                return 0;
            }
            return offset2 + 1;
        }
        if (offset2 == -1) {
            return offset1 + 1;
        }
        return Math.max(offset1, offset2) + 1;
    }

    /**
     * Gets the base name, minus the full path and extension, from a full fileName.
     * <p>
     * This method will handle a file in either Unix or Windows format. The text after the last forward or backslash and before the last dot is returned.
     * </p>
     *
     * <pre>
     * a/b/c.txt --&gt; c
     * a.txt     --&gt; a
     * a/b/c     --&gt; c
     * a/b/c/    --&gt; ""
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * </p>
     *
     * @param fileName the fileName to query, null returns null
     * @return the name of the file without the path, or an empty string if none exists
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static String getBaseName(final String fileName) {
        return removeExtension(getName(fileName));
    }

    /**
     * Gets the extension of a fileName.
     * <p>
     * This method returns the textual part of the fileName after the last dot. There must be no directory separator after the dot.
     * </p>
     *
     * <pre>
     * foo.txt      --&gt; "txt"
     * a/b/c.jpg    --&gt; "jpg"
     * a/b.txt/c    --&gt; ""
     * a/b/c        --&gt; ""
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on, with the exception of a possible {@link IllegalArgumentException} on
     * Windows (see below).
     * </p>
     * <p>
     * <b>Note:</b> This method used to have a hidden problem for names like "foo.exe:bar.txt". In this case, the name wouldn't be the name of a file, but the
     * identifier of an alternate data stream (bar.txt) on the file foo.exe. The method used to return ".txt" here, which would be misleading. Commons IO 2.7,
     * and later versions, are throwing an {@link IllegalArgumentException} for names like this.
     * </p>
     *
     * @param fileName the fileName to retrieve the extension of.
     * @return the extension of the file or an empty string if none exists or {@code null} if the fileName is {@code null}.
     * @throws IllegalArgumentException <b>Windows only:</b> The fileName parameter is, in fact, the identifier of an Alternate Data Stream, for example
     * "foo.exe:bar.txt".
     */
    public static String getExtension(final String fileName) throws IllegalArgumentException {
        if (fileName == null) {
            return null;
        }
        final int index = indexOfExtension(fileName);
        if (index == NOT_FOUND) {
            return EMPTY_STRING;
        }
        return fileName.substring(index + 1);
    }

    /**
     * Gets the full path from a full fileName, which is the prefix + path.
     * <p>
     * This method will handle a file in either Unix or Windows format. The method is entirely text based, and returns the text before and including the last
     * forward or backslash.
     * </p>
     *
     * <pre>
     * C:\a\b\c.txt --&gt; C:\a\b\
     * ~/a/b/c.txt  --&gt; ~/a/b/
     * a.txt        --&gt; ""
     * a/b/c        --&gt; a/b/
     * a/b/c/       --&gt; a/b/c/
     * C:           --&gt; C:
     * C:\          --&gt; C:\
     * ~            --&gt; ~/
     * ~/           --&gt; ~/
     * ~user        --&gt; ~user/
     * ~user/       --&gt; ~user/
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * </p>
     *
     * @param fileName the fileName to query, null returns null
     * @return the path of the file, an empty string if none exists, null if invalid
     * @throws IllegalArgumentException if the result path contains the null character ({@code U+0000})
     */
    public static String getFullPath(final String fileName) {
        return doGetFullPath(fileName, true);
    }

    /**
     * Gets the full path from a full fileName, which is the prefix + path, and also excluding the final directory separator.
     * <p>
     * This method will handle a file in either Unix or Windows format. The method is entirely text based, and returns the text before the last forward or
     * backslash.
     * </p>
     *
     * <pre>
     * C:\a\b\c.txt --&gt; C:\a\b
     * ~/a/b/c.txt  --&gt; ~/a/b
     * a.txt        --&gt; ""
     * a/b/c        --&gt; a/b
     * a/b/c/       --&gt; a/b/c
     * C:           --&gt; C:
     * C:\          --&gt; C:\
     * ~            --&gt; ~
     * ~/           --&gt; ~
     * ~user        --&gt; ~user
     * ~user/       --&gt; ~user
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * </p>
     *
     * @param fileName the fileName to query, null returns null
     * @return the path of the file, an empty string if none exists, null if invalid
     * @throws IllegalArgumentException if the result path contains the null character ({@code U+0000})
     */
    public static String getFullPathNoEndSeparator(final String fileName) {
        return doGetFullPath(fileName, false);
    }

    /**
     * Gets the name minus the path from a full fileName.
     * <p>
     * This method will handle a file in either Unix or Windows format. The text after the last forward or backslash is returned.
     * </p>
     *
     * <pre>
     * a/b/c.txt --&gt; c.txt
     * a.txt     --&gt; a.txt
     * a/b/c     --&gt; c
     * a/b/c/    --&gt; ""
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * </p>
     *
     * @param fileName the fileName to query, null returns null
     * @return the name of the file without the path, or an empty string if none exists
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static String getName(final String fileName) {
        if (fileName == null) {
            return null;
        }
        return requireNonNullChars(fileName).substring(indexOfLastSeparator(fileName) + 1);
    }

    /**
     * Gets the path from a full fileName, which excludes the prefix.
     * <p>
     * This method will handle a file in either Unix or Windows format. The method is entirely text based, and returns the text before and including the last
     * forward or backslash.
     * </p>
     *
     * <pre>
     * C:\a\b\c.txt --&gt; a\b\
     * ~/a/b/c.txt  --&gt; a/b/
     * a.txt        --&gt; ""
     * a/b/c        --&gt; a/b/
     * a/b/c/       --&gt; a/b/c/
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * </p>
     * <p>
     * This method drops the prefix from the result. See {@link #getFullPath(String)} for the method that retains the prefix.
     * </p>
     *
     * @param fileName the fileName to query, null returns null
     * @return the path of the file, an empty string if none exists, null if invalid
     * @throws IllegalArgumentException if the result path contains the null character ({@code U+0000})
     */
    public static String getPath(final String fileName) {
        return doGetPath(fileName, 1);
    }

    /**
     * Gets the path from a full fileName, which excludes the prefix, and also excluding the final directory separator.
     * <p>
     * This method will handle a file in either Unix or Windows format. The method is entirely text based, and returns the text before the last forward or
     * backslash.
     * </p>
     *
     * <pre>
     * C:\a\b\c.txt --&gt; a\b
     * ~/a/b/c.txt  --&gt; a/b
     * a.txt        --&gt; ""
     * a/b/c        --&gt; a/b
     * a/b/c/       --&gt; a/b/c
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * </p>
     * <p>
     * This method drops the prefix from the result. See {@link #getFullPathNoEndSeparator(String)} for the method that retains the prefix.
     * </p>
     *
     * @param fileName the fileName to query, null returns null
     * @return the path of the file, an empty string if none exists, null if invalid
     * @throws IllegalArgumentException if the result path contains the null character ({@code U+0000})
     */
    public static String getPathNoEndSeparator(final String fileName) {
        return doGetPath(fileName, 0);
    }

    /**
     * Gets the prefix from a full fileName, such as {@code C:/} or {@code ~/}.
     * <p>
     * This method will handle a file in either Unix or Windows format. The prefix includes the first slash in the full fileName where applicable.
     * </p>
     *
     * <pre>
     * Windows:
     * a\b\c.txt           --&gt; ""          --&gt; relative
     * \a\b\c.txt          --&gt; "\"         --&gt; current drive absolute
     * C:a\b\c.txt         --&gt; "C:"        --&gt; drive relative
     * C:\a\b\c.txt        --&gt; "C:\"       --&gt; absolute
     * \\server\a\b\c.txt  --&gt; "\\server\" --&gt; UNC
     *
     * Unix:
     * a/b/c.txt           --&gt; ""          --&gt; relative
     * /a/b/c.txt          --&gt; "/"         --&gt; absolute
     * ~/a/b/c.txt         --&gt; "~/"        --&gt; current user
     * ~                   --&gt; "~/"        --&gt; current user (slash added)
     * ~user/a/b/c.txt     --&gt; "~user/"    --&gt; named user
     * ~user               --&gt; "~user/"    --&gt; named user (slash added)
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on. ie. both Unix and Windows prefixes are matched regardless.
     * </p>
     *
     * @param fileName the fileName to query, null returns null
     * @return the prefix of the file, null if invalid
     * @throws IllegalArgumentException if the result contains the null character ({@code U+0000})
     */
    public static String getPrefix(final String fileName) {
        if (fileName == null) {
            return null;
        }
        final int len = getPrefixLength(fileName);
        if (len < 0) {
            return null;
        }
        if (len > fileName.length()) {
            requireNonNullChars(fileName);
            return fileName + UNIX_NAME_SEPARATOR;
        }
        return requireNonNullChars(fileName.substring(0, len));
    }

    /**
     * Returns the length of the fileName prefix, such as {@code C:/} or {@code ~/}.
     * <p>
     * This method will handle a file in either Unix or Windows format.
     * </p>
     * <p>
     * The prefix length includes the first slash in the full fileName if applicable. Thus, it is possible that the length returned is greater than the length
     * of the input string.
     * </p>
     *
     * <pre>
     * Windows:
     * a\b\c.txt           --&gt; 0           --&gt; relative
     * \a\b\c.txt          --&gt; 1           --&gt; current drive absolute
     * C:a\b\c.txt         --&gt; 2           --&gt; drive relative
     * C:\a\b\c.txt        --&gt; 3           --&gt; absolute
     * \\server\a\b\c.txt  --&gt; 9           --&gt; UNC
     * \\\a\b\c.txt        --&gt; -1          --&gt; error
     *
     * Unix:
     * a/b/c.txt           --&gt; 0           --&gt; relative
     * /a/b/c.txt          --&gt; 1           --&gt; absolute
     * ~/a/b/c.txt         --&gt; 2           --&gt; current user
     * ~                   --&gt; 2           --&gt; current user (slash added)
     * ~user/a/b/c.txt     --&gt; 6           --&gt; named user
     * ~user               --&gt; 6           --&gt; named user (slash added)
     * //server/a/b/c.txt  --&gt; 9
     * ///a/b/c.txt        --&gt; -1          --&gt; error
     * C:                  --&gt; 0           --&gt; valid file name as only null character and / are reserved characters
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on. ie. both Unix and Windows prefixes are matched regardless.
     * </p>
     * <p>
     * Note that a leading // (or \\) is used to indicate a UNC name on Windows. These must be followed by a server name, so double-slashes are not collapsed to
     * a single slash at the start of the fileName.
     * </p>
     *
     * @param fileName the fileName to find the prefix in, null returns -1
     * @return the length of the prefix, -1 if invalid or null
     */
    public static int getPrefixLength(final String fileName) {
        if (fileName == null) {
            return NOT_FOUND;
        }
        final int len = fileName.length();
        if (len == 0) {
            return 0;
        }
        char ch0 = fileName.charAt(0);
        if (ch0 == ':') {
            return NOT_FOUND;
        }
        if (len == 1) {
            if (ch0 == '~') {
                return 2; // return a length greater than the input
            }
            return isSeparator(ch0) ? 1 : 0;
        }
        if (ch0 == '~') {
            int posUnix = fileName.indexOf(UNIX_NAME_SEPARATOR, 1);
            int posWin = fileName.indexOf(WINDOWS_NAME_SEPARATOR, 1);
            if (posUnix == NOT_FOUND && posWin == NOT_FOUND) {
                return len + 1; // return a length greater than the input
            }
            posUnix = posUnix == NOT_FOUND ? posWin : posUnix;
            posWin = posWin == NOT_FOUND ? posUnix : posWin;
            return Math.min(posUnix, posWin) + 1;
        }
        final char ch1 = fileName.charAt(1);
        if (ch1 == ':') {
            ch0 = Character.toUpperCase(ch0);
            if (ch0 >= 'A' && ch0 <= 'Z') {
                if (len == 2 && !FileSystem.getCurrent().supportsDriveLetter()) {
                    return 0;
                }
                if (len == 2 || !isSeparator(fileName.charAt(2))) {
                    return 2;
                }
                return 3;
            }
            if (ch0 == UNIX_NAME_SEPARATOR) {
                return 1;
            }
            return NOT_FOUND;

        }
        if (!isSeparator(ch0) || !isSeparator(ch1)) {
            return isSeparator(ch0) ? 1 : 0;
        }
        int posUnix = fileName.indexOf(UNIX_NAME_SEPARATOR, 2);
        int posWin = fileName.indexOf(WINDOWS_NAME_SEPARATOR, 2);
        if (posUnix == NOT_FOUND && posWin == NOT_FOUND || posUnix == 2 || posWin == 2) {
            return NOT_FOUND;
        }
        posUnix = posUnix == NOT_FOUND ? posWin : posUnix;
        posWin = posWin == NOT_FOUND ? posUnix : posWin;
        final int pos = Math.min(posUnix, posWin) + 1;
        final String hostnamePart = fileName.substring(2, pos - 1);
        return isValidHostName(hostnamePart) ? pos : NOT_FOUND;
    }

    /**
     * Returns the index of the last extension separator character, which is a dot.
     * <p>
     * This method also checks that there is no directory separator after the last dot. To do this it uses {@link #indexOfLastSeparator(String)} which will
     * handle a file in either Unix or Windows format.
     * </p>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on, with the exception of a possible {@link IllegalArgumentException} on
     * Windows (see below).
     * </p>
     * <b>Note:</b> This method used to have a hidden problem for names like "foo.exe:bar.txt". In this case, the name wouldn't be the name of a file, but the
     * identifier of an alternate data stream (bar.txt) on the file foo.exe. The method used to return ".txt" here, which would be misleading. Commons IO 2.7,
     * and later versions, are throwing an {@link IllegalArgumentException} for names like this.
     *
     * @param fileName the fileName to find the last extension separator in, null returns -1
     * @return the index of the last extension separator character, or -1 if there is no such character
     * @throws IllegalArgumentException <b>Windows only:</b> The fileName parameter is, in fact, the identifier of an Alternate Data Stream, for example
     * "foo.exe:bar.txt".
     */
    public static int indexOfExtension(final String fileName) throws IllegalArgumentException {
        if (fileName == null) {
            return NOT_FOUND;
        }
        if (isSystemWindows()) {
            // Special handling for NTFS ADS: Don't accept colon in the fileName.
            final int offset = fileName.indexOf(':', getAdsCriticalOffset(fileName));
            if (offset != -1) {
                throw new IllegalArgumentException("NTFS ADS separator (':') in file name is forbidden.");
            }
        }
        final int extensionPos = fileName.lastIndexOf(EXTENSION_SEPARATOR);
        final int lastSeparator = indexOfLastSeparator(fileName);
        return lastSeparator > extensionPos ? NOT_FOUND : extensionPos;
    }

    /**
     * Returns the index of the last directory separator character.
     * <p>
     * This method will handle a file in either Unix or Windows format. The position of the last forward or backslash is returned.
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param fileName the fileName to find the last path separator in, null returns -1
     * @return the index of the last separator character, or -1 if there is no such character
     */
    public static int indexOfLastSeparator(final String fileName) {
        if (fileName == null) {
            return NOT_FOUND;
        }
        final int lastUnixPos = fileName.lastIndexOf(UNIX_NAME_SEPARATOR);
        final int lastWindowsPos = fileName.lastIndexOf(WINDOWS_NAME_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    private static boolean isEmpty(final String string) {
        return string == null || string.isEmpty();
    }

    /**
     * Checks whether the extension of the fileName is one of those specified.
     * <p>
     * This method obtains the extension as the textual part of the fileName after the last dot. There must be no directory separator after the dot. The
     * extension check is case-sensitive on all platforms.
     *
     * @param fileName the fileName to query, null returns false
     * @param extensions the extensions to check for, null checks for no extension
     * @return true if the fileName is one of the extensions
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static boolean isExtension(final String fileName, final Collection<String> extensions) {
        if (fileName == null) {
            return false;
        }
        requireNonNullChars(fileName);

        if (extensions == null || extensions.isEmpty()) {
            return indexOfExtension(fileName) == NOT_FOUND;
        }
        return extensions.contains(getExtension(fileName));
    }

    /**
     * Checks whether the extension of the fileName is that specified.
     * <p>
     * This method obtains the extension as the textual part of the fileName after the last dot. There must be no directory separator after the dot. The
     * extension check is case-sensitive on all platforms.
     *
     * @param fileName the fileName to query, null returns false
     * @param extension the extension to check for, null or empty checks for no extension
     * @return true if the fileName has the specified extension
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static boolean isExtension(final String fileName, final String extension) {
        if (fileName == null) {
            return false;
        }
        requireNonNullChars(fileName);

        if (isEmpty(extension)) {
            return indexOfExtension(fileName) == NOT_FOUND;
        }
        return getExtension(fileName).equals(extension);
    }

    /**
     * Checks whether the extension of the fileName is one of those specified.
     * <p>
     * This method obtains the extension as the textual part of the fileName after the last dot. There must be no directory separator after the dot. The
     * extension check is case-sensitive on all platforms.
     *
     * @param fileName the fileName to query, null returns false
     * @param extensions the extensions to check for, null checks for no extension
     * @return true if the fileName is one of the extensions
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static boolean isExtension(final String fileName, final String... extensions) {
        if (fileName == null) {
            return false;
        }
        requireNonNullChars(fileName);

        if (extensions == null || extensions.length == 0) {
            return indexOfExtension(fileName) == NOT_FOUND;
        }
        final String fileExt = getExtension(fileName);
        return Stream.of(extensions).anyMatch(fileExt::equals);
    }

    /**
     * Checks whether a given string represents a valid IPv4 address.
     *
     * @param name the name to validate
     * @return true if the given name is a valid IPv4 address
     */
    // mostly copied from org.apache.commons.validator.routines.InetAddressValidator#isValidInet4Address
    private static boolean isIPv4Address(final String name) {
        final Matcher m = IPV4_PATTERN.matcher(name);
        if (!m.matches() || m.groupCount() != 4) {
            return false;
        }

        // verify that address subgroups are legal
        for (int i = 1; i <= 4; i++) {
            final String ipSegment = m.group(i);
            final int iIpSegment = Integer.parseInt(ipSegment);
            if (iIpSegment > IPV4_MAX_OCTET_VALUE) {
                return false;
            }

            if (ipSegment.length() > 1 && ipSegment.startsWith("0")) {
                return false;
            }

        }

        return true;
    }

    // copied from org.apache.commons.validator.routines.InetAddressValidator#isValidInet6Address
    /**
     * Checks whether a given string represents a valid IPv6 address.
     *
     * @param inet6Address the name to validate
     * @return true if the given name is a valid IPv6 address
     */
    private static boolean isIPv6Address(final String inet6Address) {
        final boolean containsCompressedZeroes = inet6Address.contains("::");
        if (containsCompressedZeroes && inet6Address.indexOf("::") != inet6Address.lastIndexOf("::")) {
            return false;
        }
        if (inet6Address.startsWith(":") && !inet6Address.startsWith("::") || inet6Address.endsWith(":") && !inet6Address.endsWith("::")) {
            return false;
        }
        String[] octets = inet6Address.split(":");
        if (containsCompressedZeroes) {
            final List<String> octetList = new ArrayList<>(Arrays.asList(octets));
            if (inet6Address.endsWith("::")) {
                // String.split() drops ending empty segments
                octetList.add("");
            }
            else if (inet6Address.startsWith("::") && !octetList.isEmpty()) {
                octetList.remove(0);
            }
            octets = octetList.toArray(EMPTY_STRING_ARRAY);
        }
        if (octets.length > IPV6_MAX_HEX_GROUPS) {
            return false;
        }
        int validOctets = 0;
        int emptyOctets = 0; // consecutive empty chunks
        for (int index = 0; index < octets.length; index++) {
            final String octet = octets[index];
            if (octet.isEmpty()) {
                emptyOctets++;
                if (emptyOctets > 1) {
                    return false;
                }
            }
            else {
                emptyOctets = 0;
                // Is last chunk an IPv4 address?
                if (index == octets.length - 1 && octet.contains(".")) {
                    if (!isIPv4Address(octet)) {
                        return false;
                    }
                    validOctets += 2;
                    continue;
                }
                if (octet.length() > IPV6_MAX_HEX_DIGITS_PER_GROUP) {
                    return false;
                }
                final int octetInt;
                try {
                    octetInt = Integer.parseInt(octet, BASE_16);
                }
                catch (final NumberFormatException e) {
                    return false;
                }
                if (octetInt < 0 || octetInt > MAX_UNSIGNED_SHORT) {
                    return false;
                }
            }
            validOctets++;
        }
        return validOctets <= IPV6_MAX_HEX_GROUPS && (validOctets >= IPV6_MAX_HEX_GROUPS || containsCompressedZeroes);
    }

    /**
     * Checks whether a given string is a valid host name according to RFC 3986 - not accepting IP addresses.
     *
     * @see "https://tools.ietf.org/html/rfc3986#section-3.2.2"
     * @param name the hostname to validate
     * @return true if the given name is a valid host name
     */
    private static boolean isRFC3986HostName(final String name) {
        final String[] parts = name.split("\\.", -1);
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) {
                // trailing dot is legal, otherwise we've hit a .. sequence
                return i == parts.length - 1;
            }
            if (!REG_NAME_PART_PATTERN.matcher(parts[i]).matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the character is a separator.
     *
     * @param ch the character to check
     * @return true if it is a separator character
     */
    private static boolean isSeparator(final char ch) {
        return ch == UNIX_NAME_SEPARATOR || ch == WINDOWS_NAME_SEPARATOR;
    }

    /**
     * Determines if Windows file system is in use.
     *
     * @return true if the system is Windows
     */
    static boolean isSystemWindows() {
        return SYSTEM_NAME_SEPARATOR == WINDOWS_NAME_SEPARATOR;
    }

    /**
     * Checks whether a given string is a valid host name according to RFC 3986.
     *
     * <p>
     * Accepted are IP addresses (v4 and v6) as well as what the RFC calls a "reg-name". Percent encoded names don't seem to be valid names in UNC paths.
     * </p>
     *
     * @see "https://tools.ietf.org/html/rfc3986#section-3.2.2"
     * @param name the hostname to validate
     * @return true if the given name is a valid host name
     */
    private static boolean isValidHostName(final String name) {
        return isIPv6Address(name) || isRFC3986HostName(name);
    }

    /**
     * Normalizes a path, removing double and single dot path steps.
     * <p>
     * This method normalizes a path to a standard format. The input may contain separators in either Unix or Windows format. The output will contain separators
     * in the format of the system.
     * <p>
     * A trailing slash will be retained. A double slash will be merged to a single slash (but UNC names are handled). A single dot path segment will be
     * removed. A double dot will cause that path segment and the one before to be removed. If the double dot has no parent path segment to work with,
     * {@code null} is returned.
     * <p>
     * The output will be the same on both Unix and Windows except for the separator character.
     *
     * <pre>
     * /foo//               --&gt;   /foo/
     * /foo/./              --&gt;   /foo/
     * /foo/../bar          --&gt;   /bar
     * /foo/../bar/         --&gt;   /bar/
     * /foo/../bar/../baz   --&gt;   /baz
     * //foo//./bar         --&gt;   //foo/bar
     * /../                 --&gt;   null
     * ../foo               --&gt;   null
     * foo/bar/..           --&gt;   foo/
     * foo/../../bar        --&gt;   null
     * foo/../bar           --&gt;   bar
     * //server/foo/../bar  --&gt;   //server/bar
     * //server/../bar      --&gt;   null
     * C:\foo\..\bar        --&gt;   C:\bar
     * C:\..\bar            --&gt;   null
     * ~/foo/../bar/        --&gt;   ~/bar/
     * ~/../bar             --&gt;   null
     * </pre>
     *
     * (Note the file separator returned will be correct for Windows/Unix)
     *
     * @param fileName the fileName to normalize, null returns null
     * @return the normalized fileName, or null if invalid
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static String normalize(final String fileName) {
        return doNormalize(fileName, SYSTEM_NAME_SEPARATOR, true);
    }

    /**
     * Normalizes a path, removing double and single dot path steps.
     * <p>
     * This method normalizes a path to a standard format. The input may contain separators in either Unix or Windows format. The output will contain separators
     * in the format specified.
     * <p>
     * A trailing slash will be retained. A double slash will be merged to a single slash (but UNC names are handled). A single dot path segment will be
     * removed. A double dot will cause that path segment and the one before to be removed. If the double dot has no parent path segment to work with,
     * {@code null} is returned.
     * <p>
     * The output will be the same on both Unix and Windows except for the separator character.
     *
     * <pre>
     * /foo//               --&gt;   /foo/
     * /foo/./              --&gt;   /foo/
     * /foo/../bar          --&gt;   /bar
     * /foo/../bar/         --&gt;   /bar/
     * /foo/../bar/../baz   --&gt;   /baz
     * //foo//./bar         --&gt;   /foo/bar
     * /../                 --&gt;   null
     * ../foo               --&gt;   null
     * foo/bar/..           --&gt;   foo/
     * foo/../../bar        --&gt;   null
     * foo/../bar           --&gt;   bar
     * //server/foo/../bar  --&gt;   //server/bar
     * //server/../bar      --&gt;   null
     * C:\foo\..\bar        --&gt;   C:\bar
     * C:\..\bar            --&gt;   null
     * ~/foo/../bar/        --&gt;   ~/bar/
     * ~/../bar             --&gt;   null
     * </pre>
     *
     * The output will be the same on both Unix and Windows including the separator character.
     *
     * @param fileName the fileName to normalize, null returns null
     * @param unixSeparator {@code true} if a Unix separator should be used or {@code false} if a Windows separator should be used.
     * @return the normalized fileName, or null if invalid
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static String normalize(final String fileName, final boolean unixSeparator) {
        return doNormalize(fileName, toSeparator(unixSeparator), true);
    }

    /**
     * Normalizes a path, removing double and single dot path steps, and removing any final directory separator.
     * <p>
     * This method normalizes a path to a standard format. The input may contain separators in either Unix or Windows format. The output will contain separators
     * in the format of the system.
     * <p>
     * A trailing slash will be removed. A double slash will be merged to a single slash (but UNC names are handled). A single dot path segment will be removed.
     * A double dot will cause that path segment and the one before to be removed. If the double dot has no parent path segment to work with, {@code null} is
     * returned.
     * <p>
     * The output will be the same on both Unix and Windows except for the separator character.
     *
     * <pre>
     * /foo//               --&gt;   /foo
     * /foo/./              --&gt;   /foo
     * /foo/../bar          --&gt;   /bar
     * /foo/../bar/         --&gt;   /bar
     * /foo/../bar/../baz   --&gt;   /baz
     * //foo//./bar         --&gt;   /foo/bar
     * /../                 --&gt;   null
     * ../foo               --&gt;   null
     * foo/bar/..           --&gt;   foo
     * foo/../../bar        --&gt;   null
     * foo/../bar           --&gt;   bar
     * //server/foo/../bar  --&gt;   //server/bar
     * //server/../bar      --&gt;   null
     * C:\foo\..\bar        --&gt;   C:\bar
     * C:\..\bar            --&gt;   null
     * ~/foo/../bar/        --&gt;   ~/bar
     * ~/../bar             --&gt;   null
     * </pre>
     *
     * (Note the file separator returned will be correct for Windows/Unix)
     *
     * @param fileName the fileName to normalize, null returns null
     * @return the normalized fileName, or null if invalid
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static String normalizeNoEndSeparator(final String fileName) {
        return doNormalize(fileName, SYSTEM_NAME_SEPARATOR, false);
    }

    /**
     * Normalizes a path, removing double and single dot path steps, and removing any final directory separator.
     * <p>
     * This method normalizes a path to a standard format. The input may contain separators in either Unix or Windows format. The output will contain separators
     * in the format specified.
     * <p>
     * A trailing slash will be removed. A double slash will be merged to a single slash (but UNC names are handled). A single dot path segment will be removed.
     * A double dot will cause that path segment and the one before to be removed. If the double dot has no parent path segment to work with, {@code null} is
     * returned.
     * <p>
     * The output will be the same on both Unix and Windows including the separator character.
     *
     * <pre>
     * /foo//               --&gt;   /foo
     * /foo/./              --&gt;   /foo
     * /foo/../bar          --&gt;   /bar
     * /foo/../bar/         --&gt;   /bar
     * /foo/../bar/../baz   --&gt;   /baz
     * //foo//./bar         --&gt;   /foo/bar
     * /../                 --&gt;   null
     * ../foo               --&gt;   null
     * foo/bar/..           --&gt;   foo
     * foo/../../bar        --&gt;   null
     * foo/../bar           --&gt;   bar
     * //server/foo/../bar  --&gt;   //server/bar
     * //server/../bar      --&gt;   null
     * C:\foo\..\bar        --&gt;   C:\bar
     * C:\..\bar            --&gt;   null
     * ~/foo/../bar/        --&gt;   ~/bar
     * ~/../bar             --&gt;   null
     * </pre>
     *
     * @param fileName the fileName to normalize, null returns null
     * @param unixSeparator {@code true} if a Unix separator should be used or {@code false} if a Windows separator should be used.
     * @return the normalized fileName, or null if invalid
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static String normalizeNoEndSeparator(final String fileName, final boolean unixSeparator) {
        return doNormalize(fileName, toSeparator(unixSeparator), false);
    }

    /**
     * Removes the extension from a fileName.
     * <p>
     * This method returns the textual part of the fileName before the last dot. There must be no directory separator after the dot.
     *
     * <pre>
     * foo.txt    --&gt; foo
     * a\b\c.jpg  --&gt; a\b\c
     * a\b\c      --&gt; a\b\c
     * a.b\c      --&gt; a.b\c
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param fileName the fileName to query, null returns null
     * @return the fileName minus the extension
     * @throws IllegalArgumentException if the fileName contains the null character ({@code U+0000})
     */
    public static String removeExtension(final String fileName) {
        if (fileName == null) {
            return null;
        }
        requireNonNullChars(fileName);

        final int index = indexOfExtension(fileName);
        if (index == NOT_FOUND) {
            return fileName;
        }
        return fileName.substring(0, index);
    }

    /**
     * Checks the input for null characters ({@code U+0000}), a sign of unsanitized data being passed to file level functions.
     *
     * This may be used for poison byte attacks.
     *
     * @param path the path to check
     * @return The input
     * @throws IllegalArgumentException if path contains the null character ({@code U+0000})
     */
    private static String requireNonNullChars(final String path) {
        if (path.indexOf(0) >= 0) {
            throw new IllegalArgumentException("Null character present in file/path name. "
                    + "There are no known legitimate use cases for such data, but several injection attacks may use it");
        }
        return path;
    }

    /**
     * Converts all separators to the system separator.
     *
     * @param path the path to be changed, null ignored.
     * @return the updated path.
     */
    public static String separatorsToSystem(final String path) {
        return FileSystem.getCurrent().normalizeSeparators(path);
    }

    /**
     * Converts all separators to the Unix separator of forward slash.
     *
     * @param path the path to be changed, null ignored.
     * @return the new path.
     */
    public static String separatorsToUnix(final String path) {
        return FileSystem.LINUX.normalizeSeparators(path);
    }

    /**
     * Converts all separators to the Windows separator of backslash.
     *
     * @param path the path to be changed, null ignored.
     * @return the updated path.
     */
    public static String separatorsToWindows(final String path) {
        return FileSystem.WINDOWS.normalizeSeparators(path);
    }

    /**
     * Splits a string into a number of tokens. The text is split by '?' and '*'. Where multiple '*' occur consecutively they are collapsed into a single '*'.
     *
     * @param text the text to split
     * @return the array of tokens, never null
     */
    static String[] splitOnTokens(final String text) {
        // used by wildcardMatch
        // package level so a unit test may run on this

        if (text.indexOf('?') == NOT_FOUND && text.indexOf('*') == NOT_FOUND) {
            return new String[] {text};
        }

        final char[] array = text.toCharArray();
        final ArrayList<String> list = new ArrayList<>();
        final StringBuilder buffer = new StringBuilder();
        char prevChar = 0;
        for (final char ch : array) {
            if (ch == '?' || ch == '*') {
                if (buffer.length() != 0) {
                    list.add(buffer.toString());
                    buffer.setLength(0);
                }
                if (ch == '?') {
                    list.add("?");
                }
                else if (prevChar != '*') {
                    // ch == '*' here; check if previous char was '*'
                    list.add("*");
                }
            }
            else {
                buffer.append(ch);
            }
            prevChar = ch;
        }
        if (buffer.length() != 0) {
            list.add(buffer.toString());
        }

        return list.toArray(EMPTY_STRING_ARRAY);
    }

    /**
     * Returns '/' if given true, '\\' otherwise.
     *
     * @param unixSeparator which separator to return.
     * @return '/' if given true, '\\' otherwise.
     */
    private static char toSeparator(final boolean unixSeparator) {
        return unixSeparator ? UNIX_NAME_SEPARATOR : WINDOWS_NAME_SEPARATOR;
    }

    /**
     * Checks a fileName to see if it matches the specified wildcard matcher, always testing case-sensitive.
     * <p>
     * The wildcard matcher uses the characters '?' and '*' to represent a single or multiple (zero or more) wildcard characters. This is the same as often
     * found on DOS/Unix command lines. The check is case-sensitive always.
     *
     * <pre>
     * wildcardMatch("c.txt", "*.txt")      --&gt; true
     * wildcardMatch("c.txt", "*.jpg")      --&gt; false
     * wildcardMatch("a/b/c.txt", "a/b/*")  --&gt; true
     * wildcardMatch("c.txt", "*.???")      --&gt; true
     * wildcardMatch("c.txt", "*.????")     --&gt; false
     * </pre>
     *
     * N.B. the sequence "*?" does not work properly at present in match strings.
     *
     * @param fileName the fileName to match on
     * @param wildcardMatcher the wildcard string to match against
     * @return true if the fileName matches the wildcard string
     * @see IOCase#SENSITIVE
     */
    public static boolean wildcardMatch(final String fileName, final String wildcardMatcher) {
        return wildcardMatch(fileName, wildcardMatcher, IOCase.SENSITIVE);
    }

    /**
     * Checks a fileName to see if it matches the specified wildcard matcher allowing control over case-sensitivity.
     * <p>
     * The wildcard matcher uses the characters '?' and '*' to represent a single or multiple (zero or more) wildcard characters. N.B. the sequence "*?" does
     * not work properly at present in match strings.
     *
     * @param fileName the fileName to match on
     * @param wildcardMatcher the wildcard string to match against
     * @param ioCase what case sensitivity rule to use, null means case-sensitive
     * @return true if the fileName matches the wildcard string
     */
    public static boolean wildcardMatch(final String fileName, final String wildcardMatcher, IOCase ioCase) {
        if (fileName == null && wildcardMatcher == null) {
            return true;
        }
        if (fileName == null || wildcardMatcher == null) {
            return false;
        }
        ioCase = IOCase.value(ioCase, IOCase.SENSITIVE);
        final String[] wcs = splitOnTokens(wildcardMatcher);
        boolean anyChars = false;
        int textIdx = 0;
        int wcsIdx = 0;
        final Deque<int[]> backtrack = new ArrayDeque<>(wcs.length);

        // loop around a backtrack stack, to handle complex * matching
        do {
            if (!backtrack.isEmpty()) {
                final int[] array = backtrack.pop();
                wcsIdx = array[0];
                textIdx = array[1];
                anyChars = true;
            }

            // loop whilst tokens and text left to process
            while (wcsIdx < wcs.length) {

                if (wcs[wcsIdx].equals("?")) {
                    // ? so move to next text char
                    textIdx++;
                    if (textIdx > fileName.length()) {
                        break;
                    }
                    anyChars = false;

                }
                else if (wcs[wcsIdx].equals("*")) {
                    // set any chars status
                    anyChars = true;
                    if (wcsIdx == wcs.length - 1) {
                        textIdx = fileName.length();
                    }

                }
                else {
                    // matching text token
                    if (anyChars) {
                        // any chars then try to locate text token
                        textIdx = ioCase.checkIndexOf(fileName, textIdx, wcs[wcsIdx]);
                        if (textIdx == NOT_FOUND) {
                            // token not found
                            break;
                        }
                        final int repeat = ioCase.checkIndexOf(fileName, textIdx + 1, wcs[wcsIdx]);
                        if (repeat >= 0) {
                            backtrack.push(new int[] {wcsIdx, repeat});
                        }
                    }
                    else if (!ioCase.checkRegionMatches(fileName, textIdx, wcs[wcsIdx])) {
                        // matching from current position
                        // couldn't match token
                        break;
                    }

                    // matched text token, move text index to end of matched token
                    textIdx += wcs[wcsIdx].length();
                    anyChars = false;
                }

                wcsIdx++;
            }

            // full match
            if (wcsIdx == wcs.length && textIdx == fileName.length()) {
                return true;
            }

        } while (!backtrack.isEmpty());

        return false;
    }

    /**
     * Checks a fileName to see if it matches the specified wildcard matcher using the case rules of the system.
     * <p>
     * The wildcard matcher uses the characters '?' and '*' to represent a single or multiple (zero or more) wildcard characters. This is the same as often
     * found on DOS/Unix command lines. The check is case-sensitive on Unix and case-insensitive on Windows.
     *
     * <pre>
     * wildcardMatch("c.txt", "*.txt")      --&gt; true
     * wildcardMatch("c.txt", "*.jpg")      --&gt; false
     * wildcardMatch("a/b/c.txt", "a/b/*")  --&gt; true
     * wildcardMatch("c.txt", "*.???")      --&gt; true
     * wildcardMatch("c.txt", "*.????")     --&gt; false
     * </pre>
     *
     * N.B. the sequence "*?" does not work properly at present in match strings.
     *
     * @param fileName the fileName to match on
     * @param wildcardMatcher the wildcard string to match against
     * @return true if the fileName matches the wildcard string
     * @see IOCase#SYSTEM
     */
    public static boolean wildcardMatchOnSystem(final String fileName, final String wildcardMatcher) {
        return wildcardMatch(fileName, wildcardMatcher, IOCase.SYSTEM);
    }

    public enum IOCase {

        /**
         * The constant for case-sensitive regardless of operating system.
         */
        SENSITIVE("Sensitive", true),

        /**
         * The constant for case-insensitive regardless of operating system.
         */
        INSENSITIVE("Insensitive", false),

        /**
         * The constant for case sensitivity determined by the current operating system. Windows is case-insensitive when comparing file names, Unix is
         * case-sensitive.
         * <p>
         * <strong>Note:</strong> This only caters for Windows and Unix. Other operating systems (e.g. OSX and OpenVMS) are treated as case-sensitive if they
         * use the Unix file separator and case-insensitive if they use the Windows file separator (see {@link java.io.File#separatorChar}).
         * </p>
         * <p>
         * If you serialize this constant on Windows, and deserialize on Unix, or vice versa, then the value of the case-sensitivity flag will change.
         * </p>
         */
        SYSTEM("System", FileSystem.getCurrent().isCaseSensitive());

        /** Serialization version. */
        private static final long serialVersionUID = -6343169151696340687L;

        /** The enumeration name. */
        private final String name;

        /** The sensitivity flag. */
        private final transient boolean sensitive;

        /**
         * Constructs a new instance.
         *
         * @param name the name
         * @param sensitive the sensitivity
         */
        IOCase(final String name, final boolean sensitive) {
            this.name = name;
            this.sensitive = sensitive;
        }

        /**
         * Factory method to create an IOCase from a name.
         *
         * @param name the name to find
         * @return the IOCase object
         * @throws IllegalArgumentException if the name is invalid
         */
        public static IOCase forName(final String name) {
            return Stream.of(IOCase.values()).filter(ioCase -> ioCase.getName().equals(name)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Illegal IOCase name: " + name));
        }

        /**
         * Tests for cases sensitivity in a null-safe manner.
         *
         * @param ioCase an IOCase.
         * @return true if the input is non-null and {@link #isCaseSensitive()}.
         * @since 2.10.0
         */
        public static boolean isCaseSensitive(final IOCase ioCase) {
            return ioCase != null && ioCase.isCaseSensitive();
        }

        /**
         * Returns the given value if not-null, the defaultValue if null.
         *
         * @param value the value to test.
         * @param defaultValue the default value.
         * @return the given value if not-null, the defaultValue if null.
         * @since 2.12.0
         */
        public static IOCase value(final IOCase value, final IOCase defaultValue) {
            return value != null ? value : defaultValue;
        }

        /**
         * Compares two strings using the case-sensitivity rule.
         * <p>
         * This method mimics {@link String#compareTo} but takes case-sensitivity into account.
         * </p>
         *
         * @param str1 the first string to compare, not null
         * @param str2 the second string to compare, not null
         * @return true if equal using the case rules
         * @throws NullPointerException if either string is null
         */
        public int checkCompareTo(final String str1, final String str2) {
            Objects.requireNonNull(str1, "str1");
            Objects.requireNonNull(str2, "str2");
            return sensitive ? str1.compareTo(str2) : str1.compareToIgnoreCase(str2);
        }

        /**
         * Checks if one string ends with another using the case-sensitivity rule.
         * <p>
         * This method mimics {@link String#endsWith} but takes case-sensitivity into account.
         * </p>
         *
         * @param str the string to check
         * @param end the end to compare against
         * @return true if equal using the case rules, false if either input is null
         */
        public boolean checkEndsWith(final String str, final String end) {
            if (str == null || end == null) {
                return false;
            }
            final int endLen = end.length();
            return str.regionMatches(!sensitive, str.length() - endLen, end, 0, endLen);
        }

        /**
         * Compares two strings using the case-sensitivity rule.
         * <p>
         * This method mimics {@link String#equals} but takes case-sensitivity into account.
         * </p>
         *
         * @param str1 the first string to compare, not null
         * @param str2 the second string to compare, not null
         * @return true if equal using the case rules
         * @throws NullPointerException if either string is null
         */
        public boolean checkEquals(final String str1, final String str2) {
            Objects.requireNonNull(str1, "str1");
            Objects.requireNonNull(str2, "str2");
            return sensitive ? str1.equals(str2) : str1.equalsIgnoreCase(str2);
        }

        /**
         * Checks if one string contains another starting at a specific index using the case-sensitivity rule.
         * <p>
         * This method mimics parts of {@link String#indexOf(String, int)} but takes case-sensitivity into account.
         * </p>
         *
         * @param str the string to check, not null
         * @param strStartIndex the index to start at in str
         * @param search the start to search for, not null
         * @return the first index of the search String, -1 if no match or {@code null} string input
         * @throws NullPointerException if either string is null
         * @since 2.0
         */
        public int checkIndexOf(final String str, final int strStartIndex, final String search) {
            final int endIndex = str.length() - search.length();
            if (endIndex >= strStartIndex) {
                for (int i = strStartIndex; i <= endIndex; i++) {
                    if (checkRegionMatches(str, i, search)) {
                        return i;
                    }
                }
            }
            return -1;
        }

        /**
         * Checks if one string contains another at a specific index using the case-sensitivity rule.
         * <p>
         * This method mimics parts of {@link String#regionMatches(boolean, int, String, int, int)} but takes case-sensitivity into account.
         * </p>
         *
         * @param str the string to check, not null
         * @param strStartIndex the index to start at in str
         * @param search the start to search for, not null
         * @return true if equal using the case rules
         * @throws NullPointerException if either string is null
         */
        public boolean checkRegionMatches(final String str, final int strStartIndex, final String search) {
            return str.regionMatches(!sensitive, strStartIndex, search, 0, search.length());
        }

        /**
         * Checks if one string starts with another using the case-sensitivity rule.
         * <p>
         * This method mimics {@link String#startsWith(String)} but takes case-sensitivity into account.
         * </p>
         *
         * @param str the string to check
         * @param start the start to compare against
         * @return true if equal using the case rules, false if either input is null
         */
        public boolean checkStartsWith(final String str, final String start) {
            return str != null && start != null && str.regionMatches(!sensitive, 0, start, 0, start.length());
        }

        /**
         * Gets the name of the constant.
         *
         * @return the name of the constant
         */
        public String getName() {
            return name;
        }

        /**
         * Does the object represent case-sensitive comparison.
         *
         * @return true if case-sensitive
         */
        public boolean isCaseSensitive() {
            return sensitive;
        }

        /**
         * Replaces the enumeration from the stream with a real one. This ensures that the correct flag is set for SYSTEM.
         *
         * @return the resolved object
         */
        private Object readResolve() {
            return forName(name);
        }

        /**
         * Gets a string describing the sensitivity.
         *
         * @return a string describing the sensitivity
         */
        @Override
        public String toString() {
            return name;
        }

    }

    /**
     * Abstracts an OS' file system details, currently supporting the single use case of converting a file name String to a legal file name with
     * {@link #toLegalFileName(String, char)}.
     * <p>
     * The starting point of any operation is {@link #getCurrent()} which gets you the enum for the file system that matches the OS hosting the running JVM.
     * </p>
     *
     * @since 2.7
     */
    public enum FileSystem {

        /**
         * Generic file system.
         */
        GENERIC(4096, false, false, Integer.MAX_VALUE, Integer.MAX_VALUE, new int[] {0}, new String[] {}, false, false, '/'),

        /**
         * Linux file system.
         */
        LINUX(8192, true, true, 255, 4096, new int[] {0, '/'}, new String[] {}, false, false, '/'),

        /**
         * MacOS file system.
         */
        MAC_OSX(4096, true, true, 255, 1024, new int[] {0, '/', ':'}, new String[] {}, false, false, '/'),

        /**
         * Windows file system.
         * <p>
         * The reserved characters are defined in the <a href="https://docs.microsoft.com/en-us/windows/win32/fileio/naming-a-file">Naming Conventions
         * (microsoft.com)</a>.
         * </p>
         *
         * @see <a href="https://docs.microsoft.com/en-us/windows/win32/fileio/naming-a-file">Naming Conventions (microsoft.com)</a>
         * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/fileapi/nf-fileapi-createfilea#consoles"> CreateFileA function - Consoles
         * (microsoft.com)</a>
         */
        WINDOWS(4096, false, true, 255, 32000, // KEEP THIS ARRAY SORTED!
                new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, '"', '*', '/',
                    ':', '<', '>', '?', '\\', '|'},
                new String[] {"AUX", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "CON", "CONIN$", "CONOUT$", "LPT1", "LPT2",
                    "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9", "NUL", "PRN"},
                true, true, '\\');

        /**
         * <p>
         * Is {@code true} if this is Linux.
         * </p>
         * <p>
         * The field will return {@code false} if {@code OS_NAME} is {@code null}.
         * </p>
         */
        private static final boolean IS_OS_LINUX = getOsMatchesName("Linux");

        /**
         * <p>
         * Is {@code true} if this is Mac.
         * </p>
         * <p>
         * The field will return {@code false} if {@code OS_NAME} is {@code null}.
         * </p>
         */
        private static final boolean IS_OS_MAC = getOsMatchesName("Mac");

        /**
         * The prefix String for all Windows OS.
         */
        private static final String OS_NAME_WINDOWS_PREFIX = "Windows";

        /**
         * <p>
         * Is {@code true} if this is Windows.
         * </p>
         * <p>
         * The field will return {@code false} if {@code OS_NAME} is {@code null}.
         * </p>
         */
        private static final boolean IS_OS_WINDOWS = getOsMatchesName(OS_NAME_WINDOWS_PREFIX);

        /**
         * The current FileSystem.
         */
        private static final FileSystem CURRENT = current();

        private final int blockSize;
        private final boolean casePreserving;
        private final boolean caseSensitive;
        private final int[] illegalFileNameChars;
        private final int maxFileNameLength;
        private final int maxPathLength;
        private final String[] reservedFileNames;
        private final boolean reservedFileNamesExtensions;
        private final boolean supportsDriveLetter;
        private final char nameSeparator;
        private final char nameSeparatorOther;

        /**
         * Constructs a new instance.
         *
         * @param blockSize file allocation block size in bytes.
         * @param caseSensitive Whether this file system is case-sensitive.
         * @param casePreserving Whether this file system is case-preserving.
         * @param maxFileLength The maximum length for file names. The file name does not include folders.
         * @param maxPathLength The maximum length of the path to a file. This can include folders.
         * @param illegalFileNameChars Illegal characters for this file system.
         * @param reservedFileNames The reserved file names.
         * @param reservedFileNamesExtensions TODO
         * @param supportsDriveLetter Whether this file system support driver letters.
         * @param nameSeparator The name separator, '\\' on Windows, '/' on Linux.
         */
        FileSystem(final int blockSize, final boolean caseSensitive, final boolean casePreserving, final int maxFileLength, final int maxPathLength,
                final int[] illegalFileNameChars, final String[] reservedFileNames, final boolean reservedFileNamesExtensions,
                final boolean supportsDriveLetter, final char nameSeparator) {
            this.blockSize = blockSize;
            this.maxFileNameLength = maxFileLength;
            this.maxPathLength = maxPathLength;
            this.illegalFileNameChars = Objects.requireNonNull(illegalFileNameChars, "illegalFileNameChars");
            this.reservedFileNames = Objects.requireNonNull(reservedFileNames, "reservedFileNames");
            this.reservedFileNamesExtensions = reservedFileNamesExtensions;
            this.caseSensitive = caseSensitive;
            this.casePreserving = casePreserving;
            this.supportsDriveLetter = supportsDriveLetter;
            this.nameSeparator = nameSeparator;
            this.nameSeparatorOther = FilenameUtils.flipSeparator(nameSeparator);
        }

        /**
         * Gets the current file system.
         *
         * @return the current file system
         */
        private static FileSystem current() {
            if (IS_OS_LINUX) {
                return LINUX;
            }
            if (IS_OS_MAC) {
                return MAC_OSX;
            }
            if (IS_OS_WINDOWS) {
                return WINDOWS;
            }
            return GENERIC;
        }

        /**
         * Gets the current file system.
         *
         * @return the current file system
         */
        public static FileSystem getCurrent() {
            return CURRENT;
        }

        /**
         * Decides if the operating system matches.
         *
         * @param osNamePrefix the prefix for the os name
         * @return true if matches, or false if not or can't determine
         */
        private static boolean getOsMatchesName(final String osNamePrefix) {
            return isOsNameMatch(getSystemProperty("os.name"), osNamePrefix);
        }

        /**
         * <p>
         * Gets a System property, defaulting to {@code null} if the property cannot be read.
         * </p>
         * <p>
         * If a {@link SecurityException} is caught, the return value is {@code null} and a message is written to {@code System.err}.
         * </p>
         *
         * @param property the system property name
         * @return the system property value or {@code null} if a security problem occurs
         */
        private static String getSystemProperty(final String property) {
            try {
                return System.getProperty(property);
            }
            catch (final SecurityException ex) {
                // we are not allowed to look at this property
                System.err.println(
                        "Caught a SecurityException reading the system property '" + property + "'; the SystemUtils property value will default to null.");
                return null;
            }
        }

        /**
         * Copied from Apache Commons Lang CharSequenceUtils.
         *
         * Returns the index within {@code cs} of the first occurrence of the specified character, starting the search at the specified index.
         * <p>
         * If a character with value {@code searchChar} occurs in the character sequence represented by the {@code cs} object at an index no smaller than
         * {@code start}, then the index of the first such occurrence is returned. For values of {@code searchChar} in the range from 0 to 0xFFFF (inclusive),
         * this is the smallest value <i>k</i> such that:
         * </p>
         * <blockquote>
         *
         * <pre>
         * (this.charAt(<i>k</i>) == searchChar) &amp;&amp; (<i>k</i> &gt;= start)
         * </pre>
         *
         * </blockquote> is true. For other values of {@code searchChar}, it is the smallest value <i>k</i> such that: <blockquote>
         *
         * <pre>
         * (this.codePointAt(<i>k</i>) == searchChar) &amp;&amp; (<i>k</i> &gt;= start)
         * </pre>
         *
         * </blockquote>
         * <p>
         * is true. In either case, if no such character occurs in {@code cs} at or after position {@code start}, then {@code -1} is returned.
         * </p>
         * <p>
         * There is no restriction on the value of {@code start}. If it is negative, it has the same effect as if it were zero: the entire {@link CharSequence}
         * may be searched. If it is greater than the length of {@code cs}, it has the same effect as if it were equal to the length of {@code cs}: {@code -1}
         * is returned.
         * </p>
         * <p>
         * All indices are specified in {@code char} values (Unicode code units).
         * </p>
         *
         * @param cs the {@link CharSequence} to be processed, not null
         * @param searchChar the char to be searched for
         * @param start the start index, negative starts at the string start
         * @return the index where the search char was found, -1 if not found
         * @since 3.6 updated to behave more like {@link String}
         */
        private static int indexOf(final CharSequence cs, final int searchChar, int start) {
            if (cs instanceof String) {
                return ((String) cs).indexOf(searchChar, start);
            }
            final int sz = cs.length();
            if (start < 0) {
                start = 0;
            }
            if (searchChar < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
                for (int i = start; i < sz; i++) {
                    if (cs.charAt(i) == searchChar) {
                        return i;
                    }
                }
                return -1;
            }
            // supplementary characters (LANG1300)
            if (searchChar <= Character.MAX_CODE_POINT) {
                final char[] chars = Character.toChars(searchChar);
                for (int i = start; i < sz - 1; i++) {
                    final char high = cs.charAt(i);
                    final char low = cs.charAt(i + 1);
                    if (high == chars[0] && low == chars[1]) {
                        return i;
                    }
                }
            }
            return -1;
        }

        /**
         * Decides if the operating system matches.
         * <p>
         * This method is package private instead of private to support unit test invocation.
         * </p>
         *
         * @param osName the actual OS name
         * @param osNamePrefix the prefix for the expected OS name
         * @return true if matches, or false if not or can't determine
         */
        private static boolean isOsNameMatch(final String osName, final String osNamePrefix) {
            if (osName == null) {
                return false;
            }
            return osName.toUpperCase(Locale.ROOT).startsWith(osNamePrefix.toUpperCase(Locale.ROOT));
        }

        /**
         * Null-safe replace.
         *
         * @param path the path to be changed, null ignored.
         * @param oldChar the old character.
         * @param newChar the new character.
         * @return the new path.
         */
        private static String replace(final String path, final char oldChar, final char newChar) {
            return path == null ? null : path.replace(oldChar, newChar);
        }

        /**
         * Gets the file allocation block size in bytes.
         *
         * @return the file allocation block size in bytes.
         *
         * @since 2.12.0
         */
        public int getBlockSize() {
            return blockSize;
        }

        /**
         * Gets a cloned copy of the illegal characters for this file system.
         *
         * @return the illegal characters for this file system.
         */
        public char[] getIllegalFileNameChars() {
            final char[] chars = new char[illegalFileNameChars.length];
            for (int i = 0; i < illegalFileNameChars.length; i++) {
                chars[i] = (char) illegalFileNameChars[i];
            }
            return chars;
        }

        /**
         * Gets a cloned copy of the illegal code points for this file system.
         *
         * @return the illegal code points for this file system.
         * @since 2.12.0
         */
        public int[] getIllegalFileNameCodePoints() {
            return this.illegalFileNameChars.clone();
        }

        /**
         * Gets the maximum length for file names. The file name does not include folders.
         *
         * @return the maximum length for file names.
         */
        public int getMaxFileNameLength() {
            return maxFileNameLength;
        }

        /**
         * Gets the maximum length of the path to a file. This can include folders.
         *
         * @return the maximum length of the path to a file.
         */
        public int getMaxPathLength() {
            return maxPathLength;
        }

        /**
         * Gets the name separator, '\\' on Windows, '/' on Linux.
         *
         * @return '\\' on Windows, '/' on Linux.
         *
         * @since 2.12.0
         */
        public char getNameSeparator() {
            return nameSeparator;
        }

        /**
         * Gets a cloned copy of the reserved file names.
         *
         * @return the reserved file names.
         */
        public String[] getReservedFileNames() {
            return reservedFileNames.clone();
        }

        /**
         * Tests whether this file system preserves case.
         *
         * @return Whether this file system preserves case.
         */
        public boolean isCasePreserving() {
            return casePreserving;
        }

        /**
         * Tests whether this file system is case-sensitive.
         *
         * @return Whether this file system is case-sensitive.
         */
        public boolean isCaseSensitive() {
            return caseSensitive;
        }

        /**
         * Tests if the given character is illegal in a file name, {@code false} otherwise.
         *
         * @param c the character to test
         * @return {@code true} if the given character is illegal in a file name, {@code false} otherwise.
         */
        private boolean isIllegalFileNameChar(final int c) {
            return Arrays.binarySearch(illegalFileNameChars, c) >= 0;
        }

        /**
         * Tests if a candidate file name (without a path) such as {@code "filename.ext"} or {@code "filename"} is a potentially legal file name. If the file
         * name length exceeds {@link #getMaxFileNameLength()}, or if it contains an illegal character then the check fails.
         *
         * @param candidate a candidate file name (without a path) like {@code "filename.ext"} or {@code "filename"}
         * @return {@code true} if the candidate name is legal
         */
        public boolean isLegalFileName(final CharSequence candidate) {
            if (candidate == null || candidate.length() == 0 || candidate.length() > maxFileNameLength) {
                return false;
            }
            if (isReservedFileName(candidate)) {
                return false;
            }
            return candidate.chars().noneMatch(this::isIllegalFileNameChar);
        }

        /**
         * Tests whether the given string is a reserved file name.
         *
         * @param candidate the string to test
         * @return {@code true} if the given string is a reserved file name.
         */
        public boolean isReservedFileName(final CharSequence candidate) {
            final CharSequence test = reservedFileNamesExtensions ? trimExtension(candidate) : candidate;
            return Arrays.binarySearch(reservedFileNames, test) >= 0;
        }

        /**
         * Converts all separators to the Windows separator of backslash.
         *
         * @param path the path to be changed, null ignored
         * @return the updated path
         * @since 2.12.0
         */
        public String normalizeSeparators(final String path) {
            return replace(path, nameSeparatorOther, nameSeparator);
        }

        /**
         * Tests whether this file system support driver letters.
         * <p>
         * Windows supports driver letters as do other operating systems. Whether these other OS's still support Java like OS/2, is a different matter.
         * </p>
         *
         * @return whether this file system support driver letters.
         * @since 2.9.0
         * @see <a href="https://en.wikipedia.org/wiki/Drive_letter_assignment">Operating systems that use drive letter assignment</a>
         */
        public boolean supportsDriveLetter() {
            return supportsDriveLetter;
        }

        /**
         * Converts a candidate file name (without a path) like {@code "filename.ext"} or {@code "filename"} to a legal file name. Illegal characters in the
         * candidate name are replaced by the {@code replacement} character. If the file name length exceeds {@link #getMaxFileNameLength()}, then the name is
         * truncated to {@link #getMaxFileNameLength()}.
         *
         * @param candidate a candidate file name (without a path) like {@code "filename.ext"} or {@code "filename"}
         * @param replacement Illegal characters in the candidate name are replaced by this character
         * @return a String without illegal characters
         */
        public String toLegalFileName(final String candidate, final char replacement) {
            if (isIllegalFileNameChar(replacement)) {
                // %s does not work properly with NUL
                throw new IllegalArgumentException(String.format("The replacement character '%s' cannot be one of the %s illegal characters: %s",
                        replacement == '\0' ? "\\0" : replacement, name(), Arrays.toString(illegalFileNameChars)));
            }
            final String truncated = candidate.length() > maxFileNameLength ? candidate.substring(0, maxFileNameLength) : candidate;
            final int[] array = truncated.chars().map(i -> isIllegalFileNameChar(i) ? replacement : i).toArray();
            return new String(array, 0, array.length);
        }

        CharSequence trimExtension(final CharSequence cs) {
            final int index = indexOf(cs, '.', 0);
            return index < 0 ? cs : cs.subSequence(0, index);
        }
    }
}
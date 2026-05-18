/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.showcase.util.taglib;

import org.primefaces.cdk.spi.taglib.Taglib;
import org.primefaces.cdk.spi.taglib.TaglibParser;
import org.primefaces.util.LangUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.FacesException;
import jakarta.inject.Named;

import lombok.Getter;

@Named
@ApplicationScoped
@Getter
public class TagLibProvider {

    private Taglib taglib;
    private Map<String, TagInfo> tags;
    private List<TagInfo> tagsSorted;
    private List<FunctionInfo> functionsSorted;

    @PostConstruct
    public void init() {
        TaglibParser taglibParser = new TaglibParser();
        try {
            taglib = taglibParser.parse(LangUtils.getContextClassLoader().getResourceAsStream(
                    "/META-INF/primefaces.taglib.xml"));
            tags = taglib.getTags().stream().collect(
                    Collectors.toMap(t -> t.getTagName(), t -> new TagInfo(t)));
            tagsSorted = tags.values().stream()
                    .sorted(Comparator.comparing(o -> o.getTag().getTagName()))
                    .collect(Collectors.toList());
            functionsSorted = taglib.getFunctions().stream()
                    .map(f -> new FunctionInfo(f))
                    .sorted(Comparator.comparing(o -> o.getFunction().getName()))
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            throw new FacesException("Could not parse taglib!", e);
        }
    }

}

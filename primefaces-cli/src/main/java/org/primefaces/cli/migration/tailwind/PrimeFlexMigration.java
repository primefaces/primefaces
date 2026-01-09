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
package org.primefaces.cli.migration.tailwind;

import org.primefaces.cli.migration.AbstractPrimeMigration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import picocli.CommandLine;

@CommandLine.Command(
        name = "TailwindMigration",
        mixinStandardHelpOptions = true,
        version = "3.0.0",
        description = "Converts PrimeFlex CSS classes to Tailwind CSS equivalents using dictionary + regex patterns.",
        headerHeading = "@|bold,underline Usage|@:%n%n",
        descriptionHeading = "%n@|bold,underline Description|@:%n",
        parameterListHeading = "%n@|bold,underline Parameters|@:%n",
        optionListHeading = "%n@|bold,underline Options|@:%n"
)
//CHECKSTYLE:OFF
public class PrimeFlexMigration extends AbstractPrimeMigration implements Runnable {

    /**
     * Translation dictionary - maps exact PrimeFlex class names to Tailwind equivalents.
     */
    private Map<String, String> translationDict = new HashMap<>();

    public static void main(String[] args) {
        int exitCode = new CommandLine(new PrimeFlexMigration()).execute(args);
        System.exit(exitCode);
    }

    @Override
    protected void initReplaceRegEx() {
        // Load the dictionary first
        loadTranslationDictionary();

        // Then initialize regex patterns for dynamic cases not covered by dictionary
        initDynamicPatterns();
    }

    /**
     * Load the translation dictionary directly into the map.
     * This contains ~2,500+ exact mappings from PrimeFlex to Tailwind.
     */
    private void loadTranslationDictionary() {
        // Grid System
        put("grid", "grid grid-cols-12 gap-4");
        put("grid-nogutter", "");

        // Legacy/form utilities - moved from regex to dictionary
        put("field", "mb-4");
        put("formgrid", "grid grid-cols-12 gap-4");

        // Grid Columns (1-12)
        for (int i = 1; i <= 12; i++) {
            put("col-" + i, "col-span-" + i);
        }

        // Responsive Grid Columns
        String[] breakpoints = {"sm", "md", "lg", "xl"};
        for (String bp : breakpoints) {
            for (int i = 1; i <= 12; i++) {
                put(bp + ":col-" + i, bp + ":col-span-" + i);
            }
        }

        // Column Offsets
        for (int i = 0; i <= 12; i++) {
            put("col-offset-" + i, "col-start-" + (i + 1));
            for (String bp : breakpoints) {
                put(bp + ":col-offset-" + i, bp + ":col-start-" + (i + 1));
            }
        }

        // Text/Surface Colors (0-900)
        int[] colorValues = {0, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900};
        String[] colorPrefixes = {"text", "focus:text", "hover:text", "active:text"};
        String[] surfacePrefixes = {"surface", "focus:surface", "hover:surface", "active:surface"};
        String[] borderPrefixes = {"border", "focus:border", "hover:border", "active:border"};

        for (int val : colorValues) {
            int darkVal = 900 - val;
            for (String prefix : colorPrefixes) {
                put(prefix + "-" + val, prefix.replace("text", "text-surface") + "-" + val + " dark:" + prefix.replace("text", "text-surface") + "-" + darkVal);
            }
            for (String prefix : surfacePrefixes) {
                put(prefix + "-" + val, prefix.replace("surface", "bg-surface") + "-" + val + " dark:" + prefix.replace("surface", "bg-surface") + "-" + darkVal);
            }
            for (String prefix : borderPrefixes) {
                put(prefix + "-" + val, prefix.replace("border", "border-surface") + "-" + val + " dark:" + prefix.replace("border", "border-surface") + "-" + darkVal);
            }
        }

        // Bluegray to Slate mappings
        String[] blueGrayPrefixes = {"bg-bluegray", "text-bluegray", "border-bluegray",
                "focus:bg-bluegray", "focus:text-bluegray", "focus:border-bluegray",
                "hover:bg-bluegray", "hover:text-bluegray", "hover:border-bluegray",
                "active:bg-bluegray", "active:text-bluegray", "active:border-bluegray"};
        for (String prefix : blueGrayPrefixes) {
            for (int val : new int[]{50, 100, 200, 300, 400, 500, 600, 700, 800, 900}) {
                put(prefix + "-" + val, prefix.replace("bluegray", "slate") + "-" + val);
            }
        }

        // Alpha Colors (white and black)
        String[] alphaPrefixes = {"bg", "text", "border", "focus:bg", "focus:text", "focus:border",
                "hover:bg", "hover:text", "hover:border", "active:bg", "active:text", "active:border"};
        for (String prefix : alphaPrefixes) {
            for (int alpha = 10; alpha <= 90; alpha += 10) {
                put(prefix + "-white-alpha-" + alpha, prefix + "-white/" + alpha);
                put(prefix + "-black-alpha-" + alpha, prefix + "-black/" + alpha);
            }
        }

        // Special Surface Colors
        put("bg-primary", "bg-primary text-primary-contrast");
        put("focus:bg-primary", "focus:bg-primary focus:text-primary-contrast");
        put("hover:bg-primary", "hover:bg-primary hover:text-primary-contrast");
        put("active:bg-primary", "active:bg-primary active:text-primary-contrast");

        put("bg-primary-reverse", "text-primary bg-primary-contrast");
        put("focus:bg-primary-reverse", "focus:text-primary focus:bg-primary-contrast");
        put("hover:bg-primary-reverse", "hover:text-primary hover:bg-primary-contrast");
        put("active:bg-primary-reverse", "active:text-primary active:bg-primary-contrast");

        put("text-color-secondary", "text-muted-color");
        put("focus:text-color-secondary", "focus:text-muted-color");
        put("hover:text-color-secondary", "hover:text-muted-color");
        put("active:text-color-secondary", "active:text-muted-color");

        put("surface-ground", "bg-surface-50 dark:bg-surface-950");
        put("surface-section", "bg-surface-0 dark:bg-surface-950");
        put("surface-card", "bg-surface-0 dark:bg-surface-900");
        put("surface-overlay", "bg-surface-0 dark:bg-surface-900");
        put("surface-hover", "bg-emphasis");
        put("surface-border", "border-surface");

        // Spacing (padding and margin 0-8)
        String[] directions = {"", "t", "r", "b", "l", "x", "y"};
        for (int i = 0; i <= 8; i++) {
            for (String dir : directions) {
                String twVal = (i <= 2) ? String.valueOf(i) : (i == 3 ? "4" : i == 4 ? "6" : i == 5 ? "8" : i == 6 ? "12" : i == 7 ? "16" : "20");
                put("p" + dir + "-" + i, "p" + dir + "-" + twVal);
                put("m" + dir + "-" + i, "m" + dir + "-" + twVal);
                if (i > 0) {
                    put("-m" + dir + "-" + i, "-m" + dir + "-" + twVal);
                }

                // Responsive variants
                for (String bp : breakpoints) {
                    put(bp + ":p" + dir + "-" + i, bp + ":p" + dir + "-" + twVal);
                    put(bp + ":m" + dir + "-" + i, bp + ":m" + dir + "-" + twVal);
                    if (i > 0) {
                        put(bp + ":-m" + dir + "-" + i, bp + ":-m" + dir + "-" + twVal);
                    }
                }
            }
            put("m-auto", "m-auto");
            put("mt-auto", "mt-auto");
            put("mr-auto", "mr-auto");
            put("mb-auto", "mb-auto");
            put("ml-auto", "ml-auto");
            put("mx-auto", "mx-auto");
            put("my-auto", "my-auto");
        }

        // Gap utilities
        for (int i = 0; i <= 8; i++) {
            String twVal = (i <= 2) ? String.valueOf(i) : (i == 3 ? "4" : i == 4 ? "6" : i == 5 ? "8" : i == 6 ? "12" : i == 7 ? "16" : "20");
            put("gap-" + i, "gap-" + twVal);
            put("row-gap-" + i, "gap-y-" + twVal);
            put("column-gap-" + i, "gap-x-" + twVal);

            for (String bp : breakpoints) {
                put(bp + ":gap-" + i, bp + ":gap-" + twVal);
                put(bp + ":row-gap-" + i, bp + ":gap-y-" + twVal);
                put(bp + ":column-gap-" + i, bp + ":gap-x-" + twVal);
            }
        }

        // Flex utilities
        put("flex-column", "flex-col");
        put("flex-column-reverse", "flex-col-reverse");
        put("justify-content-start", "justify-start");
        put("justify-content-end", "justify-end");
        put("justify-content-center", "justify-center");
        put("justify-content-between", "justify-between");
        put("justify-content-around", "justify-around");
        put("justify-content-evenly", "justify-evenly");
        put("align-content-start", "content-start");
        put("align-content-end", "content-end");
        put("align-content-center", "content-center");
        put("align-content-between", "content-between");
        put("align-content-around", "content-around");
        put("align-content-evenly", "content-evenly");
        put("align-items-stretch", "items-stretch");
        put("align-items-start", "items-start");
        put("align-items-center", "items-center");
        put("align-items-end", "items-end");
        put("align-items-baseline", "items-baseline");
        put("align-self-auto", "self-auto");
        put("align-self-start", "self-start");
        put("align-self-end", "self-end");
        put("align-self-center", "self-center");
        put("align-self-stretch", "self-stretch");
        put("align-self-baseline", "self-baseline");

        // Flex order, grow, shrink
        for (int i = 0; i <= 6; i++) {
            put("flex-order-" + i, i == 0 ? "order-none" : "order-" + i);
        }
        put("flex-grow-0", "grow-0");
        put("flex-grow-1", "grow");
        put("flex-shrink-0", "shrink-0");
        put("flex-shrink-1", "shrink");

        // Text utilities
        put("text-overflow-clip", "text-clip");
        put("text-overflow-ellipsis", "text-ellipsis");
        put("font-italic", "italic");
        put("line-height-1", "leading-none");
        put("line-height-2", "leading-tight");
        put("line-height-3", "leading-normal");
        put("line-height-4", "leading-loose");
        put("white-space-normal", "whitespace-normal");
        put("white-space-nowrap", "whitespace-nowrap");

        // Vertical align
        String[] valigns = {"baseline", "top", "middle", "bottom", "text-top", "text-bottom", "sub", "super"};
        for (String va : valigns) {
            put("vertical-align-" + va, "align-" + va);
        }

        // Shadow utilities
        put("shadow-none", "shadow-none");
        for (int i = 1; i <= 8; i++) {
            String twShadow = i <= 3 ? "shadow-sm" : i <= 6 ? "shadow" : i == 7 ? "shadow-md" : i <= 4 ? "shadow-lg" : "shadow-xl";
            if (i >= 5) twShadow = i == 5 ? "shadow-xl" : "shadow-2xl";
            put("shadow-" + i, twShadow);
            put("focus:shadow-" + i, "focus:" + twShadow);
            put("hover:shadow-" + i, "hover:" + twShadow);
            put("active:shadow-" + i, "active:" + twShadow);
        }
        put("focus:shadow-none", "focus:shadow-none");
        put("hover:shadow-none", "hover:shadow-none");
        put("active:shadow-none", "active:shadow-none");

        // Border utilities
        put("border-none", "border-0");
        put("border-1", "border");
        put("border-2", "border-2");
        put("border-3", "border-4");

        String[] borderSides = {"top", "right", "bottom", "left", "x", "y"};
        for (String side : borderSides) {
            String sideShort = side.equals("top") ? "t" : side.equals("right") ? "r" :
                    side.equals("bottom") ? "b" : side.equals("left") ? "l" : side;
            put("border-" + side + "-none", "border-" + sideShort + "-0");
            put("border-" + side + "-1", "border-" + sideShort);
            put("border-" + side + "-2", "border-" + sideShort + "-2");
            put("border-" + side + "-3", "border-" + sideShort + "-4");
        }

        // Border radius
        put("border-noround", "rounded-none");
        put("border-round", "rounded-border");
        put("border-round-xs", "rounded-sm");
        put("border-round-sm", "rounded");
        put("border-round-md", "rounded-md");
        put("border-round-lg", "rounded-lg");
        put("border-round-xl", "rounded-xl");
        put("border-round-2xl", "rounded-2xl");
        put("border-round-3xl", "rounded-3xl");
        put("border-circle", "rounded-full");

        String[] roundSides = {"left", "top", "right", "bottom"};
        for (String side : roundSides) {
            String s = side.substring(0, 1);
            put("border-noround-" + side, "rounded-" + s + "-none");
            put("border-round-" + side, "rounded-" + s);
            put("border-round-" + side + "-xs", "rounded-" + s + "-sm");
            put("border-round-" + side + "-sm", "rounded-" + s);
            put("border-round-" + side + "-md", "rounded-" + s + "-md");
            put("border-round-" + side + "-lg", "rounded-" + s + "-lg");
            put("border-round-" + side + "-xl", "rounded-" + s + "-xl");
            put("border-round-" + side + "-2xl", "rounded-" + s + "-2xl");
            put("border-round-" + side + "-3xl", "rounded-" + s + "-3xl");
            put("border-circle-" + side, "rounded-" + s + "-full");
        }

        // Width utilities
        for (int i = 1; i <= 12; i++) {
            put("w-" + i, "w-" + i + "/12");
        }

        // Width/Height in rem
        for (int i = 1; i <= 30; i++) {
            String twVal = i <= 16 ? (i <= 12 ? String.valueOf(i * 4) :
                    i == 13 ? "52" : i == 14 ? "56" : i == 15 ? "60" : "64") :
                    i == 18 ? "72" : i == 20 ? "80" : i == 24 ? "96" : "[" + i + "rem]";
            put("w-" + i + "rem", "w-" + twVal);
            put("h-" + i + "rem", "h-" + twVal);
            put("max-w-" + i + "rem", "max-w-" + twVal);
            put("max-h-" + i + "rem", "max-h-" + twVal);
        }

        // Position utilities
        put("top-50", "top-1/2");
        put("top-100", "top-full");
        put("left-50", "left-1/2");
        put("left-100", "left-full");
        put("right-50", "right-1/2");
        put("right-100", "right-full");
        put("bottom-50", "bottom-1/2");
        put("bottom-100", "bottom-full");

        // Z-index
        for (int i = 1; i <= 5; i++) {
            put("z-" + i, "z-" + (i * 10));
        }

        // Misc utilities
        put("outline-none", "outline-0");
        put("reset", "");
        put("min-w-screen", "min-w-[100vw]");
        put("max-w-screen", "max-w-[100vw]");

        // Transitions
        int[] durations = {100, 150, 200, 300, 400, 500, 1000, 2000, 3000};
        for (int d : durations) {
            String val = (d == 400 || d == 2000 || d == 3000) ? "[" + d + "ms]" : String.valueOf(d);
            put("transition-duration-" + d, "duration-" + val);
            put("transition-delay-" + d, "delay-" + (d <= 500 ? String.valueOf(d) : "[" + d + "ms]"));
            if (d <= 500) {
                put("animation-duration-" + d, "animate-duration-" + d);
                put("animation-delay-" + d, "animate-delay-" + d);
            } else {
                put("animation-duration-" + d, "animate-duration-" + d);
            }
        }

        put("transition-linear", "ease-linear");
        put("transition-ease-in", "ease-in");
        put("transition-ease-out", "ease-out");
        put("transition-ease-in-out", "ease-in-out");

        // Transforms
        put("translate-x-100", "translate-x-full");
        put("-translate-x-100", "-translate-x-full");
        put("translate-y-100", "translate-y-full");
        put("-translate-y-100", "-translate-y-full");

        // Animations
        String[] anims = {"fadein", "fadeout", "slidedown", "slideup", "scalein",
                "fadeinleft", "fadeoutleft", "fadeinright", "fadeoutright",
                "fadeinup", "fadeoutup", "fadeindown", "fadeoutdown",
                "flip", "flipup", "flipleft", "flipright",
                "zoomin", "zoomindown", "zoominleft", "zoominright", "zoominup"};
        for (String anim : anims) {
            put(anim, "animate-" + anim);
        }
        put("animate-width", "animate-width");

        put("animation-iteration-1", "animate-once");
        put("animation-iteration-2", "animate-twice");
        put("animation-iteration-infinite", "animate-infinite");
        put("animation-linear", "animate-ease-linear");
        put("animation-ease-in", "animate-ease-in");
        put("animation-ease-out", "animate-ease-out");
        put("animation-ease-in-out", "animate-ease-in-out");
        put("animation-fill-none", "animate-fill-none");
        put("animation-fill-forwards", "animate-fill-forwards");
        put("animation-fill-backwards", "animate-fill-backwards");
        put("animation-fill-both", "animate-fill-both");

        System.out.println("Loaded " + translationDict.size() + " translation mappings.");
    }

    private void put(String key, String value) {
        translationDict.put(key, value);
    }

    /**
     * Initialize regex patterns for dynamic transformations.
     * These handle cases where the dictionary can't cover all variations.
     */
    private void initDynamicPatterns() {
        // Optional p- prefix handling (for backward compatibility)
        replaceRegex.put("\\b(p-)?grid\\b", "grid");
        replaceRegex.put("\\b(p-)?col-fixed\\b", "col-auto");
        replaceRegex.put("\\b(p-)?col\\b(?!-)", "col-span-12");

        // Dynamic patterns - covers ANY numeric value beyond dictionary (9+)
        replaceRegex.put("\\b(p-)?p([trblxy]?)-(\\d+)\\b", "p$2-$3");
        replaceRegex.put("\\b(p-)?m([trblxy]?)-(\\d+)\\b", "m$2-$3");
        replaceRegex.put("\\b(p-)?(xl|lg|md|sm):p([trblxy]?)-(\\d+)\\b", "$2:p$3-$4");
        replaceRegex.put("\\b(p-)?(xl|lg|md|sm):m([trblxy]?)-(\\d+)\\b", "$2:m$3-$4");
        replaceRegex.put("\\b(p-)?gap-(\\d+)\\b", "gap-$2");
        replaceRegex.put("\\b(p-)?gap-([xy])-(\\d+)\\b", "gap-$2-$3");
        replaceRegex.put("\\b(p-)?(xl|lg|md|sm):gap-(\\d+)\\b", "$2:gap-$3");
        replaceRegex.put("\\b(p-)?(xl|lg|md|sm):gap-([xy])-(\\d+)\\b", "$2:gap-$3-$4");
        replaceRegex.put("\\b(p-)?col-(\\d+)\\b", "col-span-$2");
        replaceRegex.put("\\b(p-)?(xl|lg|md|sm):col-(\\d+)\\b", "$2:col-span-$3");

        // Shorthand aliases with optional p- prefix
        replaceRegex.put("\\b(p-)?(jc|justify)-start\\b", "justify-start");
        replaceRegex.put("\\b(p-)?(jc|justify)-end\\b", "justify-end");
        replaceRegex.put("\\b(p-)?(jc|justify)-center\\b", "justify-center");
        replaceRegex.put("\\b(p-)?(jc|justify)-between\\b", "justify-between");
        replaceRegex.put("\\b(p-)?(jc|justify)-around\\b", "justify-around");
        replaceRegex.put("\\b(p-)?(jc|justify)-evenly\\b", "justify-evenly");
        replaceRegex.put("\\b(p-)?(ai|align)-start\\b", "items-start");
        replaceRegex.put("\\b(p-)?(ai|align)-end\\b", "items-end");
        replaceRegex.put("\\b(p-)?(ai|align)-center\\b", "items-center");
        replaceRegex.put("\\b(p-)?(ai|align)-baseline\\b", "items-baseline");
        replaceRegex.put("\\b(p-)?(ai|align)-stretch\\b", "items-stretch");

        // Display with optional prefix
        replaceRegex.put("\\b(p-)?d-none\\b", "hidden");
        replaceRegex.put("\\b(p-)?d-block\\b", "block");
        replaceRegex.put("\\b(p-)?d-inline\\b", "inline");
        replaceRegex.put("\\b(p-)?d-inline-block\\b", "inline-block");
        replaceRegex.put("\\b(p-)?d-flex\\b", "flex");
        replaceRegex.put("\\b(p-)?d-inline-flex\\b", "inline-flex");

        // Flex with optional prefix
        replaceRegex.put("\\b(p-)?flex-row\\b", "flex-row");
        replaceRegex.put("\\b(p-)?flex-column\\b", "flex-col");
        replaceRegex.put("\\b(p-)?flex-row-reverse\\b", "flex-row-reverse");
        replaceRegex.put("\\b(p-)?flex-column-reverse\\b", "flex-col-reverse");
        replaceRegex.put("\\b(p-)?flex-nowrap\\b", "flex-nowrap");
        replaceRegex.put("\\b(p-)?flex-wrap\\b", "flex-wrap");
        replaceRegex.put("\\b(p-)?flex-wrap-reverse\\b", "flex-wrap-reverse");

        // Text/Font with optional prefix
        replaceRegex.put("\\b(p-)?text-left\\b", "text-left");
        replaceRegex.put("\\b(p-)?text-right\\b", "text-right");
        replaceRegex.put("\\b(p-)?text-center\\b", "text-center");
        replaceRegex.put("\\b(p-)?text-justify\\b", "text-justify");
        replaceRegex.put("\\b(p-)?text-bold\\b", "font-bold");
        replaceRegex.put("\\b(p-)?text-normal\\b", "font-normal");
        replaceRegex.put("\\b(p-)?text-light\\b", "font-light");
        replaceRegex.put("\\b(p-)?text-italic\\b", "italic");
        replaceRegex.put("\\b(p-)?text-uppercase\\b", "uppercase");
        replaceRegex.put("\\b(p-)?text-lowercase\\b", "lowercase");
        replaceRegex.put("\\b(p-)?text-capitalize\\b", "capitalize");
        replaceRegex.put("\\b(p-)?text-nowrap\\b", "whitespace-nowrap");
        replaceRegex.put("\\b(p-)?text-truncate\\b", "truncate");

        // Width/Height with optional prefix
        replaceRegex.put("\\b(p-)?w-full\\b", "w-full");
        replaceRegex.put("\\b(p-)?h-full\\b", "h-full");

        // Border radius with optional prefix
        replaceRegex.put("\\b(p-)?border-round\\b", "rounded");
        replaceRegex.put("\\b(p-)?border-circle\\b", "rounded-full");
        replaceRegex.put("\\b(p-)?border-noround\\b", "rounded-none");

        // Shadows with optional prefix
        replaceRegex.put("\\b(p-)?shadow-(1|2|3)\\b", "shadow-sm");
        replaceRegex.put("\\b(p-)?shadow-(4|5|6)\\b", "shadow");
        replaceRegex.put("\\b(p-)?shadow-(7|8|9)\\b", "shadow-md");
        replaceRegex.put("\\b(p-)?shadow-(1[0-2])\\b", "shadow-lg");

        // Position with optional prefix
        replaceRegex.put("\\b(p-)?position-static\\b", "static");
        replaceRegex.put("\\b(p-)?position-relative\\b", "relative");
        replaceRegex.put("\\b(p-)?position-absolute\\b", "absolute");
        replaceRegex.put("\\b(p-)?position-fixed\\b", "fixed");
        replaceRegex.put("\\b(p-)?position-sticky\\b", "sticky");

        // Overflow with optional prefix
        replaceRegex.put("\\b(p-)?overflow-hidden\\b", "overflow-hidden");
        replaceRegex.put("\\b(p-)?overflow-auto\\b", "overflow-auto");
        replaceRegex.put("\\b(p-)?overflow-scroll\\b", "overflow-scroll");
        replaceRegex.put("\\b(p-)?overflow-visible\\b", "overflow-visible");
    }

    @Override
    public String migrateSource(String source) {
        if (source == null || source.isEmpty()) {
            return source;
        }

        // Only process class and styleClass attributes - order matters for proper replacement
        // Process double quotes first, then single quotes
        source = replaceClassAttributes(source, "class", "\"");
        source = replaceClassAttributes(source, "class", "'");
        source = replaceClassAttributes(source, "styleClass", "\"");
        source = replaceClassAttributes(source, "styleClass", "'");

        return source;
    }

    private String replaceClassAttributes(String content, String attributeName, String quoteChar) {
        // Build regex that ONLY matches the specific attribute name followed by = and quoted value
        // The \\b ensures we match whole attribute names only (not partial matches like "field" in "textfield")
        String escapedQuote = Pattern.quote(quoteChar);
        String escapedAttrName = Pattern.quote(attributeName);

        // Regex breakdown:
        // \\b - word boundary
        // (class|styleClass) - exact attribute name
        // \\s*=\\s* - optional whitespace around equals
        // " - opening quote
        // ([^"]*) - capture the value (anything except closing quote)
        // " - closing quote
        String regex = "\\b" + escapedAttrName + "\\s*=\\s*" + escapedQuote + "([^" + escapedQuote + "]*)" + escapedQuote;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String originalClasses = m.group(1); // The value inside quotes
            String replacedClasses = migrateClassValue(originalClasses);

            // Reconstruct: attributeName="replacedValue"
            String replacement = attributeName + "=" + quoteChar + Matcher.quoteReplacement(replacedClasses) + quoteChar;
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    protected String migrateClassValue(String classValue) {
        if (classValue == null || classValue.trim().isEmpty()) {
            return classValue;
        }

        String[] tokens = classValue.trim().split("\\s+");
        List<String> out = new ArrayList<>();

        for (String token : tokens) {
            String transformed = transformToken(token);

            for (String piece : transformed.trim().split("\\s+")) {
                if (!piece.isEmpty()) {
                    out.add(piece);
                }
            }
        }

        List<String> deduped = new ArrayList<>();
        for (String s : out) {
            if (!deduped.contains(s)) {
                deduped.add(s);
            }
        }

        return String.join(" ", deduped);
    }

    private String transformToken(String token) {
        if (translationDict.containsKey(token)) {
            String dictResult = translationDict.get(token);
            return dictResult != null ? dictResult : "";
        }

        if (token.startsWith("p-")) {
            String withoutPrefix = token.substring(2);
            if (translationDict.containsKey(withoutPrefix)) {
                String dictResult = translationDict.get(withoutPrefix);
                return dictResult != null ? dictResult : "";
            }
        }

        String result = token;
        for (Map.Entry<String, String> entry : replaceRegex.entrySet()) {
            String patternStr = entry.getKey();
            String replacement = entry.getValue();
            String anchored = "^(?:" + patternStr + ")$";
            try {
                result = result.replaceAll(anchored, replacement);
                if (!result.equals(token)) {
                    break;
                }
            } catch (Exception ignored) {
            }
        }

        return result;
    }
}
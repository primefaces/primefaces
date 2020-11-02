/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.convert;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test the Java date time pattern to jquery UI pattern conversion {@link DateTimePatternConverter}.
 */
public class DateTimePatternConverterTest {
    private static final PatternConverter[] PATTERN_CONVERTERS = new PatternConverter[] { new OldTimePatternConverter(),
            new OldDatePatternConverter() };

    private PatternConverter converter;

    @Test
    public void testCommonPatterns() {
        assertConvertsSame("yy-mm-dd", "yyyy-MM-dd");
        assertConvertsSame("dd-mm-yy", "dd-MM-yyyy");
        assertConvertsSame("yy-mm-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
        assertConvertsSame("HH:mm:ss.l", "HH:mm:ss.SSS");
        assertConvertsSame("yy-mm-dd HH:mm:ss.l", "yyyy-MM-dd HH:mm:ss.SSS");
        assertConvertsSame("yy-mm-dd HH:mm:ss.l z", "yyyy-MM-dd HH:mm:ss.SSS Z");
        assertConvertsSame("dd.mm.yy", "dd.MM.yyyy");
        assertConvertsSame("dd.mm.yy HH:mm", "dd.MM.yyyy HH:mm");
        assertConvertsSame("HH:mm", "HH:mm");
        assertConvertsSame("mm/dd/yy", "MM/dd/yyyy");
        assertConvertsSame("yy-M-dd", "yyyy-MMM-dd");
        assertConvertsSame("mm/dd/yy HH:mm:ss", "MM/dd/yyyy HH:mm:ss");
        assertConvertsSame("mm/yy", "MM/yyyy");
    }

    @Test
    public void testDoublePatternLetters() {
        assertConvertsSame("AA", "AA");
        assertConvertsSame("BB", "BB");
        assertConvertsSame("CC", "CC");
        assertConvertsSame("oo", "DD");
        assertConvertsDifferently("D", "EE", "EE");
        assertConvertsSame("FF", "FF");
        assertConvertsSame("GG", "GG");
        assertConvertsSame("HH", "HH");
        assertConvertsSame("II", "II");
        assertConvertsSame("JJ", "JJ");
        assertConvertsSame("KK", "KK");
        assertConvertsSame("LL", "LL");
        assertConvertsSame("mm", "MM");
        assertConvertsSame("NN", "NN");
        assertConvertsSame("OO", "OO");
        assertConvertsSame("PP", "PP");
        assertConvertsSame("QQ", "QQ");
        assertConvertsSame("RR", "RR");
        assertConvertsDifferently("l", "SS", "SS");
        assertConvertsSame("TT", "TT");
        assertConvertsSame("UU", "UU");
        assertConvertsSame("VV", "VV");
        assertConvertsSame("WW", "WW");
        assertConvertsSame("z", "XX");
        assertConvertsSame("YY", "YY");
        assertConvertsSame("z", "ZZ");

        assertConvertsSame("TT", "aa");
        assertConvertsSame("bb", "bb");
        assertConvertsSame("cc", "cc");
        assertConvertsSame("dd", "dd");
        assertConvertsSame("ee", "ee");
        assertConvertsSame("ff", "ff");
        assertConvertsSame("gg", "gg");
        assertConvertsSame("hh", "hh");
        assertConvertsSame("ii", "ii");
        assertConvertsSame("jj", "jj");
        assertConvertsSame("kk", "kk");
        assertConvertsSame("ll", "ll");
        assertConvertsSame("mm", "mm");
        assertConvertsSame("nn", "nn");
        assertConvertsSame("oo", "oo");
        assertConvertsSame("pp", "pp");
        assertConvertsSame("qq", "qq");
        assertConvertsSame("rr", "rr");
        assertConvertsSame("ss", "ss");
        assertConvertsSame("tt", "tt");
        assertConvertsSame("y", "uu");
        assertConvertsSame("vv", "vv");
        assertConvertsSame("ww", "ww");
        assertConvertsSame("xx", "xx");
        assertConvertsSame("y", "yy");
        assertConvertsDifferently("z", "zz", "zz");
    }

    @Test
    public void testEmpty() {
        assertConvertsSame("", null);
        assertConvertsSame("", "");
    }

    @Test
    public void testLiteral() {
        // Literal that needs no quoting
        assertConvertsSame(".", ".");
        assertConvertsSame(":", ":");
        assertConvertsSame("/", "/");
        assertConvertsSame(" ", " ");
        assertConvertsSame("-", "-");
        assertConvertsSame(".:/- ", ".:/- ");
        // Combined quoted and unquoted literal
        assertConvertsDifferently("/.:", "/'.':", "/'.':");
        // Literal that only needs to be quoted Java side
        assertConvertsDifferently("bar", "'bTTr'", "'bar'");
        // Literal that only needs to be quoted client side
        assertConvertsDifferently("'@'", "@", "@");
        assertConvertsDifferently("'!'", "!", "!");
        // Literal that always needs to be quoted
        assertConvertsSame("'bit'", "'bit'");
        assertConvertsDifferently("'yyyy'", "'yy'", "'yyyy'");
        // Escaped quotation mark
        assertConvertsSame("''", "''");
        assertConvertsSame(".'':", ".'':");
        // Double escaped quotation mark
        assertConvertsSame("''''", "''''");
        // Quotation mark in quote
        assertConvertsDifferently("a''b", "'TT''b'", "'a''b'");
        assertConvertsSame("'y''y'", "'y''y'");
        // Unicode
        assertConvertsSame("月曜日", "月曜日");
        assertConvertsSame("/月/曜/日", "/月/曜/日");
        assertConvertsDifferently("月曜日", "月'曜'日", "月'曜'日");
        assertConvertsDifferently("月''曜''日", "'月''曜''日'", "'月''曜''日'");
    }

    @Test
    public void testQuadruplePatternLetters() {
        assertConvertsSame("AAAA", "AAAA");
        assertConvertsSame("BBBB", "BBBB");
        assertConvertsSame("CCCC", "CCCC");
        assertConvertsSame("oo", "DDDD");
        assertConvertsSame("DD", "EEEE");
        assertConvertsSame("FFFF", "FFFF");
        assertConvertsSame("GGGG", "GGGG");
        assertConvertsSame("HH", "HHHH");
        assertConvertsSame("IIII", "IIII");
        assertConvertsSame("JJJJ", "JJJJ");
        assertConvertsSame("KKKK", "KKKK");
        assertConvertsSame("LLLL", "LLLL");
        assertConvertsSame("MM", "MMMM");
        assertConvertsSame("NNNN", "NNNN");
        assertConvertsSame("OOOO", "OOOO");
        assertConvertsSame("PPPP", "PPPP");
        assertConvertsSame("QQQQ", "QQQQ");
        assertConvertsSame("RRRR", "RRRR");
        assertConvertsSame("l", "SSSS");
        assertConvertsSame("TTTT", "TTTT");
        assertConvertsSame("UUUU", "UUUU");
        assertConvertsSame("VVVV", "VVVV");
        assertConvertsSame("WWWW", "WWWW");
        assertConvertsSame("Z", "XXXX");
        assertConvertsSame("YYYY", "YYYY");
        assertConvertsSame("z", "ZZZZ");

        assertConvertsSame("TT", "aaaa");
        assertConvertsSame("bbbb", "bbbb");
        assertConvertsSame("cccc", "cccc");
        assertConvertsDifferently("dd", "dddd", "dddd");
        assertConvertsSame("eeee", "eeee");
        assertConvertsSame("ffff", "ffff");
        assertConvertsSame("gggg", "gggg");
        assertConvertsSame("hh", "hhhh");
        assertConvertsSame("iiii", "iiii");
        assertConvertsSame("jjjj", "jjjj");
        assertConvertsSame("kkkk", "kkkk");
        assertConvertsSame("llll", "llll");
        assertConvertsSame("mm", "mmmm");
        assertConvertsSame("nnnn", "nnnn");
        assertConvertsSame("oooo", "oooo");
        assertConvertsSame("pppp", "pppp");
        assertConvertsSame("qqqq", "qqqq");
        assertConvertsSame("rrrr", "rrrr");
        assertConvertsSame("ss", "ssss");
        assertConvertsSame("tttt", "tttt");
        assertConvertsSame("yy", "uuuu");
        assertConvertsSame("vvvv", "vvvv");
        assertConvertsSame("wwww", "wwww");
        assertConvertsSame("xxxx", "xxxx");
        assertConvertsSame("yy", "yyyy");
        assertConvertsDifferently("z", "zzzz", "zzzz");
    }

    @Test
    public void testQuintuplePatternLetters() {
        assertConvertsSame("AAAAA", "AAAAA");
        assertConvertsSame("BBBBB", "BBBBB");
        assertConvertsSame("CCCCC", "CCCCC");
        assertConvertsSame("oo", "DDDDD");
        assertConvertsSame("DD", "EEEEE");
        assertConvertsSame("FFFFF", "FFFFF");
        assertConvertsSame("GGGGG", "GGGGG");
        assertConvertsSame("HH", "HHHHH");
        assertConvertsSame("IIIII", "IIIII");
        assertConvertsSame("JJJJJ", "JJJJJ");
        assertConvertsSame("KKKKK", "KKKKK");
        assertConvertsSame("LLLLL", "LLLLL");
        assertConvertsSame("MM", "MMMMM");
        assertConvertsSame("NNNNN", "NNNNN");
        assertConvertsSame("OOOOO", "OOOOO");
        assertConvertsSame("PPPPP", "PPPPP");
        assertConvertsSame("QQQQQ", "QQQQQ");
        assertConvertsSame("RRRRR", "RRRRR");
        assertConvertsSame("l", "SSSSS");
        assertConvertsSame("TTTTT", "TTTTT");
        assertConvertsSame("UUUUU", "UUUUU");
        assertConvertsSame("VVVVV", "VVVVV");
        assertConvertsSame("WWWWW", "WWWWW");
        assertConvertsSame("Z", "XXXXX");
        assertConvertsSame("YYYYY", "YYYYY");
        assertConvertsSame("z", "ZZZZZ");

        assertConvertsSame("TT", "aaaaa");
        assertConvertsSame("bbbbb", "bbbbb");
        assertConvertsSame("ccccc", "ccccc");
        assertConvertsDifferently("dd", "ddddd", "ddddd");
        assertConvertsSame("eeeee", "eeeee");
        assertConvertsSame("fffff", "fffff");
        assertConvertsSame("ggggg", "ggggg");
        assertConvertsSame("hh", "hhhhh");
        assertConvertsSame("iiiii", "iiiii");
        assertConvertsSame("jjjjj", "jjjjj");
        assertConvertsSame("kkkkk", "kkkkk");
        assertConvertsSame("lllll", "lllll");
        assertConvertsSame("mm", "mmmmm");
        assertConvertsSame("nnnnn", "nnnnn");
        assertConvertsSame("ooooo", "ooooo");
        assertConvertsSame("ppppp", "ppppp");
        assertConvertsSame("qqqqq", "qqqqq");
        assertConvertsSame("rrrrr", "rrrrr");
        assertConvertsSame("ss", "sssss");
        assertConvertsSame("ttttt", "ttttt");
        assertConvertsDifferently("yy", "yyu", "uuuuu");
        assertConvertsSame("vvvvv", "vvvvv");
        assertConvertsSame("wwwww", "wwwww");
        assertConvertsSame("xxxxx", "xxxxx");
        assertConvertsSame("yy", "yyyyy");
        assertConvertsDifferently("z", "zzzzz", "zzzzz");
    }

    @Test
    public void testSimpleDateFormatExamples() {
        assertConvertsDifferently("yy.mm.dd G' at 'HH:mm:ss z", "yy.mm.dd G 'TTt' HH:mm:ss z",
                "yyyy.MM.dd G 'at' HH:mm:ss z");
        assertConvertsSame("D, M d, ''y", "EEE, MMM d, ''yy");
        assertConvertsSame("h:mm TT", "h:mm a");
        assertConvertsDifferently("hh' o''clock 'TT, z", "hh 'o''clock' TT, zzzz", "hh 'o''clock' a, zzzz");
        assertConvertsSame("K:mm TT, z", "K:mm a, z");
        assertConvertsSame("yy.MM.dd GGG hh:mm TT", "yyyyy.MMMMM.dd GGG hh:mm aaa");
        assertConvertsSame("D, d M yy HH:mm:ss z", "EEE, d MMM yyyy HH:mm:ss Z");
        assertConvertsSame("ymmddHHmmssz", "yyMMddHHmmssZ");
        assertConvertsSame("yy-mm-dd'T'HH:mm:ss.lz", "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        assertConvertsSame("yy-mm-dd'T'HH:mm:ss.lZ", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        assertConvertsDifferently("YYYY-Www-yy", "YYYY-'W'ww-u", "YYYY-'W'ww-u");
    }

    @Test
    public void testSinglePatternLetters() {
        assertConvertsSame("A", "A");
        assertConvertsSame("B", "B");
        assertConvertsSame("C", "C");
        assertConvertsSame("o", "D");
        assertConvertsDifferently("D", "E", "E");
        assertConvertsSame("F", "F");
        assertConvertsSame("G", "G");
        assertConvertsSame("H", "H");
        assertConvertsSame("I", "I");
        assertConvertsSame("J", "J");
        assertConvertsSame("K", "K");
        assertConvertsSame("L", "L");
        assertConvertsSame("m", "M");
        assertConvertsSame("N", "N");
        assertConvertsSame("O", "O");
        assertConvertsSame("P", "P");
        assertConvertsSame("Q", "Q");
        assertConvertsSame("R", "R");
        assertConvertsDifferently("l", "S", "S");
        assertConvertsSame("T", "T");
        assertConvertsSame("U", "U");
        assertConvertsSame("V", "V");
        assertConvertsSame("W", "W");
        assertConvertsDifferently("z", "X", "X");
        assertConvertsSame("Y", "Y");
        assertConvertsSame("z", "Z");

        assertConvertsSame("TT", "a");
        assertConvertsSame("b", "b");
        assertConvertsSame("c", "c");
        assertConvertsSame("d", "d");
        assertConvertsSame("e", "e");
        assertConvertsSame("f", "f");
        assertConvertsSame("g", "g");
        assertConvertsSame("h", "h");
        assertConvertsSame("i", "i");
        assertConvertsSame("j", "j");
        assertConvertsSame("k", "k");
        assertConvertsSame("l", "l");
        assertConvertsSame("m", "m");
        assertConvertsSame("n", "n");
        assertConvertsSame("o", "o");
        assertConvertsSame("p", "p");
        assertConvertsSame("q", "q");
        assertConvertsSame("r", "r");
        assertConvertsSame("s", "s");
        assertConvertsSame("t", "t");
        assertConvertsDifferently("yy", "u", "u");
        assertConvertsSame("v", "v");
        assertConvertsSame("w", "w");
        assertConvertsSame("x", "x");
        assertConvertsDifferently("yy", "y", "y");
        assertConvertsSame("z", "z");
    }

    @Test
    public void testTriplePatternLetters() {
        assertConvertsSame("AAA", "AAA");
        assertConvertsSame("BBB", "BBB");
        assertConvertsSame("CCC", "CCC");
        assertConvertsSame("oo", "DDD");
        assertConvertsSame("D", "EEE");
        assertConvertsSame("FFF", "FFF");
        assertConvertsSame("GGG", "GGG");
        assertConvertsSame("HH", "HHH");
        assertConvertsSame("III", "III");
        assertConvertsSame("JJJ", "JJJ");
        assertConvertsSame("KKK", "KKK");
        assertConvertsSame("LLL", "LLL");
        assertConvertsSame("M", "MMM");
        assertConvertsSame("NNN", "NNN");
        assertConvertsSame("OOO", "OOO");
        assertConvertsSame("PPP", "PPP");
        assertConvertsSame("QQQ", "QQQ");
        assertConvertsSame("RRR", "RRR");
        assertConvertsSame("l", "SSS");
        assertConvertsSame("TTT", "TTT");
        assertConvertsSame("UUU", "UUU");
        assertConvertsSame("VVV", "VVV");
        assertConvertsSame("WWW", "WWW");
        assertConvertsSame("Z", "XXX");
        assertConvertsSame("YYY", "YYY");
        assertConvertsSame("z", "ZZZ");

        assertConvertsSame("TT", "aaa");
        assertConvertsSame("bbb", "bbb");
        assertConvertsSame("ccc", "ccc");
        assertConvertsDifferently("dd", "ddd", "ddd");
        assertConvertsSame("eee", "eee");
        assertConvertsSame("fff", "fff");
        assertConvertsSame("ggg", "ggg");
        assertConvertsSame("hh", "hhh");
        assertConvertsSame("iii", "iii");
        assertConvertsSame("jjj", "jjj");
        assertConvertsSame("kkk", "kkk");
        assertConvertsSame("lll", "lll");
        assertConvertsSame("mm", "mmm");
        assertConvertsSame("nnn", "nnn");
        assertConvertsSame("ooo", "ooo");
        assertConvertsSame("ppp", "ppp");
        assertConvertsSame("qqq", "qqq");
        assertConvertsSame("rrr", "rrr");
        assertConvertsSame("ss", "sss");
        assertConvertsSame("ttt", "ttt");
        assertConvertsDifferently("yy", "yu", "uuu");
        assertConvertsSame("vvv", "vvv");
        assertConvertsSame("www", "www");
        assertConvertsSame("xxx", "xxx");
        assertConvertsSame("yy", "yyy");
        assertConvertsDifferently("z", "zzz", "zzz");
    }

    /**
     * Checks that the pattern is converted differently compared to before PF 9
     * (i.e. the conversion was fixed)
     */
    private void assertConvertsDifferently(String expectedPattern, String oldExpectedPattern, String inputPattern) {
        String oldConverted = oldConvertPattern(inputPattern);
        String converted = converter.convert(inputPattern);
        assertEquals(expectedPattern, converted, "Expected new conversion to match result");
        assertEquals(oldExpectedPattern, oldConverted, "Expected old conversion to match result");
    }

    /** Checks that the pattern is converted the same way as before PF 9 */
    private void assertConvertsSame(String expectedPattern, String inputPattern) {
        assertConvertsDifferently(expectedPattern, expectedPattern, inputPattern);
    }

    @BeforeEach
    private void setup() {
        this.converter = new DateTimePatternConverter();
    }

    private static final String oldConvertPattern(String pattern) {
        if (pattern == null) {
            return "";
        }
        String convertedPattern = pattern;
        for (PatternConverter converter : PATTERN_CONVERTERS) {
            convertedPattern = converter.convert(convertedPattern);
        }
        return convertedPattern;
    }
}

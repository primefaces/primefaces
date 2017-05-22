/*
 * Copyright 2016 husnu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.quilleditor;

import java.util.HashMap;

public class QuillEditorRgbCodes {
    public HashMap<String, String> rgbCodesTable = new HashMap<String, String>();

    public static String rgbBlack = "rgb(0, 0, 0)";
    public static String rgbRed = "rgb(230, 0, 0)";
    public static String rgbOrange = "rgb(255, 153, 0)";
    public static String rgbYellow = "rgb(255, 255, 0)";
    public static String rgbGreen = "rgb(0, 138, 0)";
    public static String rgbBlue = "rgb(0, 102, 204)";
    public static String rgbBlueViolet = "rgb(153, 51, 255)";
    public static String rgbWhite = "rgb(255, 255, 255)";
    public static String rgbCosmos = "rgb(250, 204, 204)";
    public static String rgbBlanchedAlmond = "rgb(255, 235, 204)";
    public static String rgbCream = "rgb(255, 255, 204)";
    public static String rgbGrannayApple = "rgb(204, 232, 204)";
    public static String rgbPattensBlue = "rgb(204, 224, 245)";
    public static String rgbBlueChalk = "rgb(235, 214, 255)";
    public static String rgbSilver = "rgb(187, 187, 187)";
    public static String rgbBitterSweet = "rgb(240, 102, 102)";
    public static String rgbGrandis = "rgb(255, 194, 102)";
    public static String rgbLaserLemon = "rgb(255, 255, 102)";
    public static String rgbFern = "rgb(102, 185, 102)";
    public static String rgbPictonBlue = "rgb(102, 163, 224)";
    public static String rgbHeliotrope = "rgb(194, 133, 255)";
    public static String rgbGrey = "rgb(136, 136, 136)";
    public static String rgbDarkRed = "rgb(161, 0, 0)";
    public static String rgbDarkGoldenrod = "rgb(178, 107, 0)";
    public static String rgbLaRioja = "rgb(178, 178, 0)";
    public static String rgbCobalt = "rgb(0, 71, 178)";
    public static String rgbPurpleHeart = "rgb(107, 36, 178)";
    public static String rgbCharcoal = "rgb(68, 68, 68)";
    public static String rgbMaroon = "rgb(92, 0, 0)";
    public static String rgbRawUmber = "rgb(102, 61, 0)";
    public static String rgbOlive = "rgb(102, 102, 0)";
    public static String rgbMyrtle = "rgb(0, 55, 0)";
    public static String rgbSapphire = "rgb(0, 41, 102)";
    public static String rgbChristalle = "rgb(61, 20, 102)";

    public QuillEditorRgbCodes() {
        rgbCodesTable.put("black", rgbBlack);
        rgbCodesTable.put("red", rgbRed);
        rgbCodesTable.put("orange", rgbOrange);
        rgbCodesTable.put("yellow", rgbYellow);
        rgbCodesTable.put("green", rgbGreen);
        rgbCodesTable.put("blue", rgbBlue);
        rgbCodesTable.put("blueViolet", rgbBlueViolet);
        rgbCodesTable.put("white", rgbWhite);
        rgbCodesTable.put("cosmos", rgbCosmos);
        rgbCodesTable.put("blachedAlmond", rgbBlanchedAlmond);
        rgbCodesTable.put("cream", rgbCream);
        rgbCodesTable.put("grannayApple", rgbGrannayApple);
        rgbCodesTable.put("pattensBlue", rgbPattensBlue);
        rgbCodesTable.put("blueChalk", rgbBlueChalk);
        rgbCodesTable.put("silver", rgbSilver);
        rgbCodesTable.put("bitterSweet", rgbBitterSweet);
        rgbCodesTable.put("grandis", rgbGrandis);
        rgbCodesTable.put("laserLemon", rgbLaserLemon);
        rgbCodesTable.put("fern", rgbFern);
        rgbCodesTable.put("pictonBlue", rgbPictonBlue);
        rgbCodesTable.put("heliotrope", rgbHeliotrope);
        rgbCodesTable.put("grey", rgbGrey);
        rgbCodesTable.put("darkRed", rgbDarkRed);
        rgbCodesTable.put("darkGoldenrod", rgbDarkGoldenrod);
        rgbCodesTable.put("laRioja", rgbLaRioja);
        rgbCodesTable.put("cobalt", rgbCobalt);
        rgbCodesTable.put("purpleHeart", rgbPurpleHeart);
        rgbCodesTable.put("charcoal", rgbCharcoal);
        rgbCodesTable.put("maroon", rgbMaroon);
        rgbCodesTable.put("rawUmber", rgbRawUmber);
        rgbCodesTable.put("olive", rgbOlive);
        rgbCodesTable.put("myrtle", rgbMyrtle);
        rgbCodesTable.put("sapphire", rgbSapphire);
        rgbCodesTable.put("christalle", rgbChristalle);

    }

   
    public HashMap<String, String> getRgbCodesTable() {
        return rgbCodesTable;
    }

    public void setRgbCodesTable(HashMap<String, String> rgbCodesTable) {
        this.rgbCodesTable = rgbCodesTable;
    }
}
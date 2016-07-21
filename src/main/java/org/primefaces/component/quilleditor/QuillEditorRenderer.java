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

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.quillEditor.QuillEditor;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class QuillEditorRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        QuillEditor editor = (QuillEditor) component;
        String inputParam = "textareaquill";
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String value = params.get(inputParam);
        editor.setSubmittedValue(value);

        String inputParam2 = "textareaspecial";
        Map<String, String> params2 = context.getExternalContext().getRequestParameterMap();
        String value2 = params2.get(inputParam2);
        editor.setSubmittedValue(value2);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        QuillEditor quillEditor = (QuillEditor) component;

        buildWithStyle(facesContext, quillEditor);
        withJavascript(facesContext, quillEditor);
    }

    private void buildWithStyle(FacesContext context, QuillEditor quillEditor) throws IOException {

        QuillEditorRgbCodes editorRgbCodes = new QuillEditorRgbCodes();

        String valueToRender = ComponentUtils.getValueToRender(context, quillEditor);

        ResponseWriter responseWriter = context.getResponseWriter();
        String clientId = quillEditor.getClientId();

        responseWriter.startElement("div", quillEditor);
        responseWriter.writeAttribute("class", "container", null);

        responseWriter.startElement("div", quillEditor);
        responseWriter.writeAttribute("class", "quill-wrapper", null);

        responseWriter.startElement("div", quillEditor);
        responseWriter.writeAttribute("id", "full-toolbar", null);
        responseWriter.writeAttribute("class", "ui-widget-header ui-corner-top toolbar ql-toolbar ql-snow", null);

        if (quillEditor.getFacet("toolbar") != null) {
            encodeCostumize(context, quillEditor);

        } else {

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("class", "ql-format-group", null);

            responseWriter.startElement("select", quillEditor);
            responseWriter.writeAttribute("title", "Font", null);
            responseWriter.writeAttribute("class", "ql-font", null);

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", "sans-serif", null);
            responseWriter.writeAttribute("selected", "", null);
            responseWriter.write("Sans Serif");
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", "serif", null);
            responseWriter.write("Serif");
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", "monospace", null);
            responseWriter.write("Monospace");
            responseWriter.endElement("option");

            responseWriter.endElement("select");

            //select element
            responseWriter.startElement("select", quillEditor);
            responseWriter.writeAttribute("title", "Size", null);
            responseWriter.writeAttribute("class", "ql-size", null);

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", "10px", null);
            responseWriter.write("Small");
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", "13px", null);
            responseWriter.writeAttribute("selected", "", null);
            responseWriter.write("Normal");
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", "18px", null);
            responseWriter.write("Large");
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", "32px", null);
            responseWriter.write("Huge");
            responseWriter.endElement("option");

            responseWriter.endElement("select");
            responseWriter.endElement("span");

            //ql-format group 2
            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("class", "ql-format-group", null);

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("title", "Bold", null);
            responseWriter.writeAttribute("class", "ql-format-button ql-bold", null);
            responseWriter.endElement("span");

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("class", "ql-format-separator", null);
            responseWriter.endElement("span");

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("title", "Italic", null);
            responseWriter.writeAttribute("class", "ql-format-button ql-italic", null);
            responseWriter.endElement("span");

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("class", "ql-format-separator", null);
            responseWriter.endElement("span");

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("title", "Underline", null);
            responseWriter.writeAttribute("class", "ql-format-button ql-underline", null);
            responseWriter.endElement("span");

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("class", "ql-format-separator", null);
            responseWriter.endElement("span");

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("title", "Strikethrough", null);
            responseWriter.writeAttribute("class", "ql-format-button ql-strike", null);
            responseWriter.endElement("span");

            //ql-format group 2 span finish
            responseWriter.endElement("span");

            //ql-format group 3 span 
            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("class", "ql-format-group", null);

            responseWriter.startElement("select", quillEditor);
            responseWriter.writeAttribute("title", "Text Color", null);
            responseWriter.writeAttribute("class", "ql-color", null);

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbBlack, null);//black
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbBlack, null);
            responseWriter.writeAttribute("selected", "", null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbRed, null);//red
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbRed, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbOrange, null);//orange
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbOrange, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbYellow, null);//yellow
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbYellow, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbGreen, null);//green
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbGreen, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbBlue, null);//blue
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbBlue, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbBlueViolet, null);//blueViolet
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbBlueViolet, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbWhite, null);//white
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbWhite, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbCosmos, null);//cosmos
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbCosmos, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbBlanchedAlmond, null);//Blanched Almond
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbBlanchedAlmond, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbCream, null);//Cream
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbCream, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbGrannayApple, null);//Granny Apple
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbGrannayApple, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbPattensBlue, null);//Pattens Blue
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbPattensBlue, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbBlueChalk, null);//Blue Chalk
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbBlueChalk, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbSilver, null);//Silver
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbSilver, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbBitterSweet, null);//Bittersweet
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbBitterSweet, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbGrandis, null);//Grandis
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbGrandis, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbLaserLemon, null);//Laser Lemon
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbLaserLemon, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbFern, null);//Fern
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbFern, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbPictonBlue, null);//Picton Blue
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbPictonBlue, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbHeliotrope, null);//Heliotrope
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbHeliotrope, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbGrey, null);//Grey
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbGrey, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbDarkRed, null);//Dark Red
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbDarkRed, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbDarkGoldenrod, null);//Dark Goldenrod
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbDarkGoldenrod, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbLaRioja, null);//La Rioja
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbLaRioja, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbCobalt, null);//Cobalt
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbCobalt, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbPurpleHeart, null);//Purple Heart
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbPurpleHeart, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbCharcoal, null);//Charcoal
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbCharcoal, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbMaroon, null);//Maroon
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbMaroon, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbRawUmber, null);//Raw Umber
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbRawUmber, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbOlive, null);//Olive
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbOlive, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbMyrtle, null);//Myrtle
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbMyrtle, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbSapphire, null);//Sapphire
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbSapphire, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbChristalle, null);//Christalle
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbChristalle, null);
            responseWriter.endElement("option");

            responseWriter.endElement("select");

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("class", "ql-format-separator", null);

            responseWriter.endElement("span");

            responseWriter.startElement("select", quillEditor);
            responseWriter.writeAttribute("title", "Background Color", null);
            responseWriter.writeAttribute("class", "ql-background", null);

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbBlack, null);//black
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbBlack, null);
            responseWriter.writeAttribute("selected", "", null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbRed, null);//red
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbRed, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbOrange, null);//orange
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbOrange, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbYellow, null);//yellow
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbYellow, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbGreen, null);//green
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbGreen, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbBlue, null);//blue
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbBlue, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbBlueViolet, null);//blueViolet
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbBlueViolet, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbWhite, null);//white
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbWhite, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbCosmos, null);//cosmos
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbCosmos, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbBlanchedAlmond, null);//Blanched Almond
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbBlanchedAlmond, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbCream, null);//Cream
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbCream, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbGrannayApple, null);//Granny Apple
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbGrannayApple, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbPattensBlue, null);//Pattens Blue
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbPattensBlue, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbBlueChalk, null);//Blue Chalk
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbBlueChalk, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbSilver, null);//Silver
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbSilver, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbBitterSweet, null);//Bittersweet
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbBitterSweet, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbGrandis, null);//Grandis
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbGrandis, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbLaserLemon, null);//Laser Lemon
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbLaserLemon, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbFern, null);//Fern
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbFern, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbPictonBlue, null);//Picton Blue
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbPictonBlue, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbHeliotrope, null);//Heliotrope
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbHeliotrope, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbGrey, null);//Grey
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbGrey, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbDarkRed, null);//Dark Red
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbDarkRed, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbDarkGoldenrod, null);//Dark Goldenrod
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbDarkGoldenrod, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbLaRioja, null);//La Rioja
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbLaRioja, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbCobalt, null);//Cobalt
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbCobalt, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbPurpleHeart, null);//Purple Heart
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbPurpleHeart, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbCharcoal, null);//Charcoal
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbCharcoal, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbMaroon, null);//Maroon
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbMaroon, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbRawUmber, null);//Raw Umber
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbRawUmber, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbOlive, null);//Olive
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbOlive, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbMyrtle, null);//Myrtle
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbMyrtle, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbSapphire, null);//Sapphire
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbSapphire, null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", QuillEditorRgbCodes.rgbChristalle, null);//Christalle
            responseWriter.writeAttribute("label", QuillEditorRgbCodes.rgbChristalle, null);
            responseWriter.endElement("option");

            responseWriter.endElement("select");

            //ql-format group 3 span finish
            responseWriter.endElement("span");

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("class", "ql-format-group", null);

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("title", "List", null);
            responseWriter.writeAttribute("class", "ql-format-button ql-list", null);
            responseWriter.endElement("span");

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("class", "ql-format-separator", null);
            responseWriter.endElement("span");

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("title", "Bullet", null);
            responseWriter.writeAttribute("class", "ql-format-button ql-bullet", null);
            responseWriter.endElement("span");

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("class", "ql-format-separator", null);
            responseWriter.endElement("span");

            responseWriter.startElement("select", quillEditor);
            responseWriter.writeAttribute("title", "Text Alignment", null);
            responseWriter.writeAttribute("class", "ql-align", null);

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", "left", null);
            responseWriter.writeAttribute("label", "Left", null);
            responseWriter.writeAttribute("selected", "", null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", "center", null);
            responseWriter.writeAttribute("label", "Center", null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", "right", null);
            responseWriter.writeAttribute("label", "Right", null);
            responseWriter.endElement("option");

            responseWriter.startElement("option", quillEditor);
            responseWriter.writeAttribute("value", "justify", null);
            responseWriter.writeAttribute("label", "Justify", null);
            responseWriter.endElement("option");

            responseWriter.endElement("select");

            responseWriter.endElement("span");

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("class", "ql-format-group", null);

            responseWriter.startElement("span", quillEditor);
            responseWriter.writeAttribute("class", "ql-format-button ql-link", null);
            responseWriter.writeAttribute("title", "Link", null);
            responseWriter.endElement("span");

            responseWriter.endElement("span");
        }
        responseWriter.endElement("div");

        //FULL EDITOR START DIV ELEMENT
        String fullEditorStyle = "height:300px;border-width:1px;border-style:solid;border-color:#b5b5b5;";

        responseWriter.startElement("div", quillEditor);
        responseWriter.writeAttribute("class", "editor", null);
        responseWriter.writeAttribute("id", "full-editor", null);
        responseWriter.writeAttribute("style", fullEditorStyle, null);

        responseWriter.startElement("div", quillEditor);

        responseWriter.startElement("span", quillEditor);
        responseWriter.writeAttribute("style", "font-size: 18px;", null);
        responseWriter.write("PRIMEFACES && PRIMENG && PRIMEUI...");

        responseWriter.endElement("span");
        responseWriter.endElement("div");

        if (valueToRender != null) {
            responseWriter.write(valueToRender);
        }

        responseWriter.endElement("div");

        responseWriter.startElement("textarea", quillEditor);
        responseWriter.writeAttribute("name", "textareaquill", null);
        responseWriter.writeAttribute("id", "textareaquill", null);
        responseWriter.writeAttribute("style", "display:none;", null);
        responseWriter.writeAttribute("tabindex", "0", null);
        responseWriter.endElement("textarea");

        responseWriter.endElement("div");
        responseWriter.endElement("div");

    }

    public void encodeCostumize(FacesContext context, QuillEditor editor) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        UIComponent facet = editor.getFacet("toolbar");
        if (facet != null) {
            facet.encodeAll(context);
        }

        writer.startElement("textarea", editor);
        writer.writeAttribute("name", "textareaspecial", null);
        writer.writeAttribute("id", "textareaspecial", null);
        writer.writeAttribute("style", "display:none;", null);
        writer.writeAttribute("tabindex", "0", null);
        writer.endElement("textarea");

    }

    private void withJavascript(FacesContext context, QuillEditor quillEditor) throws IOException {
        String clientId = quillEditor.getClientId(context);
        WidgetBuilder widgetBuilder = getWidgetBuilder(context);
        widgetBuilder.init("QuillEditor", quillEditor.resolveWidgetVar(), clientId);

        QuillEditorRgbCodes codes = new QuillEditorRgbCodes();
        String textColor = codes.getRgbCodesTable().get(quillEditor.getTextColor());
        String backgroundColor = codes.getRgbCodesTable().get(quillEditor.getBackgroundColor());

        boolean italic = quillEditor.isItalic();
        boolean bold = quillEditor.isBold();
        boolean underline = quillEditor.isUnderline();
        int textSize = quillEditor.getTextSize();

        widgetBuilder.attr("textColors", textColor).
                attr("backgroundColors", backgroundColor).
                attr("italic", italic).
                attr("bold", bold).
                attr("underline", underline).
                attr("textSize", textSize);

        widgetBuilder.finish();
    }
}

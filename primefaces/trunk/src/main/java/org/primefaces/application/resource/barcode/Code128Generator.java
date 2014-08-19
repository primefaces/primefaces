/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.application.resource.barcode;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

public class Code128Generator implements BarcodeGenerator {

    public void generate(OutputStream stream, String value) throws IOException {
        Code128Bean bean = new Code128Bean();
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(stream, "image/x-png", 150, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        bean.generateBarcode(canvas, value);
        canvas.finish();
    }
    
}

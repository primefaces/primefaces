/* 
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
package org.primefaces.component.effect;

import static org.junit.Assert.*;

import org.junit.Test;

public class EffectBuilderTest {

	@Test
	public void buildHighlightEffectWithNoOptions() {
		String effect = new EffectBuilder("highlight", "id", true).atSpeed(1000).build();

		assertEquals("$(PrimeFaces.escapeClientId('id')).effect('highlight',{},1000);", effect);
	}

	@Test
	public void buildHighlightEffectWithAnOption() {
		String effect = new EffectBuilder("highlight","id", false).withOption("startcolor", "'#FFFFFF'").atSpeed(5000).build();

		assertEquals("$(PrimeFaces.escapeClientId('id')).stop(true,true).effect('highlight',{startcolor:'#FFFFFF'},5000);", effect);
	}

	@Test
	public void buildHighlightEffectWitManyOptions() {
		String effect = new EffectBuilder("highlight","id", false)
													.withOption("startcolor", "'#FFFFFF'")
													.withOption("endcolor", "'#CCCCCC'")
													.withOption("restorecolor", "'#000000'")
													.atSpeed(1000)
													.build();

		assertEquals("$(PrimeFaces.escapeClientId('id')).stop(true,true).effect('highlight',{startcolor:'#FFFFFF',endcolor:'#CCCCCC',restorecolor:'#000000'},1000);", effect);
	}
}
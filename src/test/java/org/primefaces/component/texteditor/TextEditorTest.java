package org.primefaces.component.texteditor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.primefaces.component.calendar.CalendarRenderer;

public class TextEditorTest {

	private TextEditorRenderer renderer;

	@Before
	public void setup() {
		renderer = new TextEditorRenderer();
	}

	@After
	public void teardown() {
		renderer = null;
	}

	//TODO must be elaborated on to cover all supported tags
	@Test
	public void htmlSupportedByComponentShouldBeAllowedPerDefault() {
		TextEditor editor = new TextEditor();
		String value = "<u>Test</u><img src=\"data:image/png;base64,COFFEE\" />";
		String sanitized = renderer.sanitizeHtml(value, editor);
		Assert.assertEquals(value, sanitized);
	}

	@Test
	public void scriptShouldNeverBeAllowed() {
		TextEditor editor = new TextEditor();
		String value = "<script>alert('oops');</script><b>test</b>";
		String sanitized = renderer.sanitizeHtml(value, editor);
		Assert.assertEquals("<b>test</b>", sanitized);
	}

	@Test
	public void imagesShouldNotBeAllowed() {
		TextEditor editor = new TextEditor();
		editor.setAllowImages(false);
		String value = "<img src=\"data:image/png;base64,COFFEE\" /><b>test</b>";
		String sanitized = renderer.sanitizeHtml(value, editor);
		Assert.assertEquals("<b>test</b>", sanitized);
	}

}

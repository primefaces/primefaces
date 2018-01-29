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

	@Test
	public void htmlSupportedByComponentShouldBeAllowedPerDefault() {
		TextEditor editor = new TextEditor();
		String value = "<p><a href=\"https://www.primefaces.org\" target=\"_blank\">Link</a> <strong>bold </strong><span class=\"ql-font-monospace\">monospace</span> <span class=\"ql-size-huge\">huge </span><span class=\"ql-font-serif ql-size-small\">small serif </span><span style=\"color: rgb(230, 0, 0);\">red </span><span style=\"background-color: rgb(255, 255, 0);\">yellow </span>x<sup>2</sup> <img src=\"data:image/png;base64,COFFEE\" /></p>";
		String sanitized = renderer.sanitizeHtml(value, editor);
		Assert.assertTrue(sanitized.contains("<a href") && sanitized.contains("target="));
		Assert.assertTrue(sanitized.contains("<strong>bold"));
		Assert.assertTrue(sanitized.contains("<span class=\"ql-font-monospace"));
		Assert.assertTrue(sanitized.contains("<span style=\"background"));
		Assert.assertTrue(sanitized.contains("<sup>2"));
		Assert.assertTrue(sanitized.contains("<img") && sanitized.contains("COFFEE"));
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

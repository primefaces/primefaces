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
package org.primefaces.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultDashboardModelTest {

	private DashboardModel model;

	@Test
	public void columnsHaveWidgets() {
		assertEquals(2, model.getColumn(0).getWidgetCount());
		assertEquals(3, model.getColumn(1).getWidgetCount());
		assertEquals(1, model.getColumn(2).getWidgetCount());
		assertEquals("widget1", model.getColumn(0).getWidget(0));
		assertEquals("widget5", model.getColumn(1).getWidget(2));
	}

	@Test
	public void reorderWidgetInSameColumn() {
		//Move widget2 on top of widget1
		DashboardColumn column1 = model.getColumn(0);
		column1.reorderWidget(0, "widget2");

		assertEquals("widget2", column1.getWidget(0));
		assertEquals("widget1", column1.getWidget(1));
		assertEquals(2, column1.getWidgetCount());

		DashboardColumn column2 = model.getColumn(1);
		column2.reorderWidget(0, "widget5");

		assertEquals("widget5", column2.getWidget(0));
		assertEquals("widget3", column2.getWidget(1));
		assertEquals("widget4", column2.getWidget(2));
		assertEquals(3, column2.getWidgetCount());
	}

	@Test
	public void transferWidgets() {
		//Move widget3 and widget5 from column2 to column3
		DashboardColumn column2 = model.getColumn(1);
		DashboardColumn column3 = model.getColumn(2);

		model.transferWidget(column2, column3, "widget3", 0);
        model.transferWidget(column2, column3, "widget5", 2);
		assertEquals("widget4", column2.getWidget(0));
		assertEquals(1, column2.getWidgetCount());

		assertEquals("widget3", column3.getWidget(0));
		assertEquals("widget6", column3.getWidget(1));
        assertEquals("widget5", column3.getWidget(2));
		assertEquals(3, column3.getWidgetCount());
	}

	@Before
	public void setup() {
		model = new DefaultDashboardModel();
		DefaultDashboardColumn column1 = new DefaultDashboardColumn();
		column1.addWidget("widget1");
		column1.addWidget("widget2");

		DefaultDashboardColumn column2 = new DefaultDashboardColumn();
		column2.addWidget("widget3");
		column2.addWidget("widget4");
		column2.addWidget("widget5");

		DefaultDashboardColumn column3 = new DefaultDashboardColumn();
		column3.addWidget("widget6");

		model.addColumn(column1);
		model.addColumn(column2);
		model.addColumn(column3);
	}

	@After
	public void teardown() {
		model = null;
	}
}

/*
 * Copyright 2009 Prime Technology.
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
package org.primefaces.component.chart;

import java.util.List;

import org.apache.shale.test.el.MockValueExpression;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.primefaces.component.chart.line.LineChart;
import org.primefaces.component.chart.series.ChartSeries;

import static org.junit.Assert.*;

public class BaseChartRendererTest {
	
	private BaseChartRenderer renderer;
	
	@Before
	public void setup() {
		renderer = new BaseChartRenderer();
	}
	
	@After
	public void teardown() {
		renderer = null;
	}

	@Test
	public void resolvesFieldNameFromValueExpression() {
		MockValueExpression expr1 = new MockValueExpression("#{person.name}", String.class);
		MockValueExpression expr2 = new MockValueExpression("#{person.address.city}", String.class);
		 
		assertEquals("name", renderer.getFieldName(expr1));
		assertEquals("city", renderer.getFieldName(expr2));
	}
	
	@Test
	public void getSeriesGivesTheNestedSeriesChildrenComponents() {
		LineChart chart = new LineChart();
		ChartSeries series1 = new ChartSeries();
		ChartSeries series2 = new ChartSeries();
		
		chart.getChildren().add(series1);
		chart.getChildren().add(series2);
		
		List<ChartSeries> series = renderer.getSeries(chart);
		
		assertEquals(2, series.size());
		assertTrue(series.contains(series1));
		assertTrue(series.contains(series2));
	}
}
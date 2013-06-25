package org.primefaces.util.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;
import javax.faces.component.UIPanel;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class SearchExpressionFacadeTest
{
	@Before
	public void setup()
	{
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		attributes.put(UINamingContainer.SEPARATOR_CHAR_PARAM_NAME, ':');

		FacesContext context = new FacesContextMock(attributes);
		context.setViewRoot(new UIViewRoot());
	}

	private UIComponent resolveComponent(UIComponent source, String expression)
	{
		FacesContext context = FacesContext.getCurrentInstance();

		return SearchExpressionFacade.resolveComponent(context, source, expression);
	}


	private String resolveComponentForClient(UIComponent source, String expression)
	{
		FacesContext context = FacesContext.getCurrentInstance();

		return SearchExpressionFacade.resolveComponentForClient(context, source, expression);
	}





	@Test
	public void resolveComponent_Parent() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertSame("Failed", innerContainer, resolveComponent(source, "@parent"));
	}

	@Test
	public void resolveComponent_ParentParent() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertSame("Failed", outerContainer, resolveComponent(source, "@parent:@parent"));
	}

	@Test
	public void resolveComponent_Form() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertSame("Failed", form, resolveComponent(source, "@form"));
	}

	@Test
	public void resolveComponent_FormParent() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertSame("Failed", root, resolveComponent(source, "@form:@parent"));
	}

	@Test
	public void resolveComponent_All() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertSame("Failed", root, resolveComponent(source, "@all"));
	}

	@Test
	public void resolveComponent_This() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertSame("Failed", source, resolveComponent(source, "@this"));
	}

	@Test
	public void resolveComponent_ThisParent() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertSame("Failed", innerContainer, resolveComponent(source, "@this:@parent"));
	}

	@Test
	public void resolveComponent_Namingcontainer() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertSame("Failed", innerContainer, resolveComponent(source, "@namingcontainer"));
	}

	@Test
	public void resolveComponent_NamingcontainerNamingcontainer() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertSame("Failed", outerContainer, resolveComponent(source, "@namingcontainer:@namingcontainer"));
	}

	@Test
	public void resolveComponent_NamingcontainerParent() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertSame("Failed", outerContainer, resolveComponent(source, "@namingcontainer:@parent"));
	}

	@Test
	public void resolveComponent_None() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertEquals("Failed", null, resolveComponent(source, "@none"));
	}

	@Test
	public void resolveComponent_Absolute() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		source.setId("source");
		innerContainer.getChildren().add(source);

		assertSame("Failed", source, resolveComponent(source, " :form:outerContainer:innerContainer:source "));
	}

	@Test
	public void resolveComponent_Relative() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		component.setId("other");
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		source.setId("source");
		innerContainer.getChildren().add(source);

		assertSame("Failed", component, resolveComponent(source, " other "));
	}


	@Test
	public void resolveComponent_AbsoluteForm() {

		UIComponent root = new UIPanel();
		root.setId("root");

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		component.setId("other");
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		source.setId("source");
		innerContainer.getChildren().add(source);

		assertSame("Failed", root, resolveComponent(source, " :form:@parent "));
	}

	@Test
	public void resolveComponent_AbsoluteNamingcontainer() {

		UIComponent root = new UIPanel();
		root.setId("root");

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		component.setId("other");
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		source.setId("source");
		innerContainer.getChildren().add(source);

		assertSame("Failed", form, resolveComponent(source, " :form:outerContainer:@namingcontainer "));
	}

	@Test
	public void resolveComponent_AbsoluteNamingcontainerParent() {

		UIComponent root = new UIPanel();
		root.setId("root");

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		component.setId("other");
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		source.setId("source");
		innerContainer.getChildren().add(source);

		assertEquals("Failed", root, resolveComponent(source, " :form:outerContainer:@namingcontainer:@parent "));
	}

	@Test
	public void resolveComponentForClient_None() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "@none", resolveComponentForClient(source, " @none"));
	}

	@Test
	public void resolveComponentForClient_PFS() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "@(.myClass, div)", resolveComponentForClient(source, "@(.myClass, div) "));
	}

	@Test
	public void resolveComponentForClient_All() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "@all", resolveComponentForClient(source, "@all"));
	}

	@Test
	public void resolveComponentForClient_ClientId() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "@clientId(myForm:myDiv)", resolveComponentForClient(source, " @clientId(myForm:myDiv)"));
	}

	@Test
	public void resolveComponentForClient_WidgetVar() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "@widgetVar(myDialog_widget)", resolveComponentForClient(source, " @widgetVar(myDialog_widget)"));
	}

	@Test
	public void resolveComponent_NotNestablePasstrough() {

		UIComponent source = new UICommand();
		source.setId("source");

		try {
			resolveComponent(source, " @clientId(myForm:myDiv):asd");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}

		try {
			resolveComponent(source, " @none:@all:asd");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}
	}

	@Test
	public void resolveComponentForClient_NotNestablePasstrough() {

		UIComponent source = new UICommand();
		source.setId("source");

		try {
			resolveComponentForClient(source, " @clientId(myForm:myDiv):asd");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}

		try {
			resolveComponentForClient(source, " @none:@all:asd");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}
	}

	@Test
	public void resolveComponentForClient_Parent() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "form:outerContainer:innerContainer", resolveComponentForClient(source, " @parent "));
	}

	@Test
	public void resolveComponentForClient_This() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		source.setId("source");
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "form:outerContainer:innerContainer:source", resolveComponentForClient(source, " @this "));
	}

	@Test
	public void resolveComponentForClient_Namingcontainer() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		source.setId("source");
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "form:outerContainer:innerContainer", resolveComponentForClient(source, " @namingcontainer "));
	}

	@Test
	public void resolveComponentForClient_Form() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		source.setId("source");
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "form", resolveComponentForClient(source, " @form "));
	}

	@Test
	public void resolveComponentForClient_Root() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "form", resolveComponentForClient(source, " :form "));
	}

	@Test
	public void resolveComponentForClient_Absolute() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		source.setId("source");
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "form:outerContainer:innerContainer:source", resolveComponentForClient(source, " :form:outerContainer:innerContainer:source "));
	}

	@Test
	public void resolveComponentForClient_Relative() {

		UIComponent root = new UIPanel();

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		component.setId("other");
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		source.setId("source");
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "form:outerContainer:innerContainer:other", resolveComponentForClient(source, " other "));
	}


	@Test
	public void resolveComponentForClient_AbsoluteForm() {

		UIComponent root = new UIPanel();
		root.setId("root");

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		component.setId("other");
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		source.setId("source");
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "root", resolveComponentForClient(source, " :form:@parent "));
	}

	@Test
	public void resolveComponentForClient_AbsoluteNamingcontainer() {

		UIComponent root = new UIPanel();
		root.setId("root");

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		component.setId("other");
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		source.setId("source");
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "form", resolveComponentForClient(source, " :form:outerContainer:@namingcontainer "));
	}

	@Test
	public void resolveComponentForClient_AbsoluteNamingcontainerParent() {

		UIComponent root = new UIPanel();
		root.setId("root");

		UIForm form = new UIForm();
		form.setId("form");
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		component.setId("other");
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		source.setId("source");
		innerContainer.getChildren().add(source);

		assertEquals("Failed", "root", resolveComponentForClient(source, " :form:outerContainer:@namingcontainer:@parent "));
	}

	@Test
	public void resolveComponent_AbsoluteKeywordStart() {

		UIComponent source = new UICommand();
		source.setId("source");

		try {
			resolveComponent(source, " :@form:asd");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}
	}

	@Test
	public void resolveComponentForClient_AbsoluteKeywordStart() {

		UIComponent source = new UICommand();
		source.setId("source");

		try {
			resolveComponentForClient(source, " :@form:asd");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}
	}
}

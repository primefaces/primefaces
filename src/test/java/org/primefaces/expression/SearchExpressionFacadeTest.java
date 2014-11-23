package org.primefaces.expression;

import org.primefaces.mock.FacesContextMock;
import org.primefaces.mock.TestVisitContextFactory;
import java.util.Collection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;
import javax.faces.component.UIPanel;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitContextFactory;
import javax.faces.component.visit.VisitHint;
import javax.faces.context.FacesContext;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.expression.SearchExpressionFacade;

public class SearchExpressionFacadeTest
{
	@Before
	public void setup()
	{
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		attributes.put(UINamingContainer.SEPARATOR_CHAR_PARAM_NAME, ':');

		FacesContext context = new FacesContextMock(attributes);
		context.setViewRoot(new UIViewRoot());
        FactoryFinder.setFactory(FactoryFinder.VISIT_CONTEXT_FACTORY, TestVisitContextFactory.class.getName());
	}

	private UIComponent resolveComponent(UIComponent source, String expression)
	{
		FacesContext context = FacesContext.getCurrentInstance();

		return SearchExpressionFacade.resolveComponent(context, source, expression);
	}

	private UIComponent resolveComponent(UIComponent source, String expression, int options)
	{
		FacesContext context = FacesContext.getCurrentInstance();

		return SearchExpressionFacade.resolveComponent(context, source, expression, options);
	}

	private String resolveComponentForClient(UIComponent source, String expression)
	{
		FacesContext context = FacesContext.getCurrentInstance();

		return SearchExpressionFacade.resolveComponentForClient(context, source, expression);
	}

    private List<UIComponent> resolveComponents(UIComponent source, String expression)
    {
        FacesContext context = FacesContext.getCurrentInstance();

        return SearchExpressionFacade.resolveComponents(context, source, expression);
    }


    private String resolveComponentsForClient(UIComponent source, String expression)
    {
        FacesContext context = FacesContext.getCurrentInstance();

        return SearchExpressionFacade.resolveComponentsForClient(context, source, expression);
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
	public void resolveComponent_ParentChild() {

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

	    assertSame("Failed", component, resolveComponent(source, " @parent:@child(0) "));
	    assertSame("Failed", source, resolveComponent(source, " @parent:@child(1) "));
	}

	@Test
	public void resolveComponentForClient_ParentChild() {

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

	    assertEquals("Failed", "form:outerContainer:innerContainer:other", resolveComponentForClient(source, " @parent:@child(0) "));
	    assertEquals("Failed", "form:outerContainer:innerContainer:source", resolveComponentForClient(source, " @parent:@child(1) "));
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
			resolveComponent(source, " @widgetVar(myForm:myDiv):asd");
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

	@Test
	public void resolveComponentsForClient_RelativeAndParent() {

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

	    assertEquals("Failed", "form:outerContainer:innerContainer:other form:outerContainer:innerContainer", resolveComponentsForClient(source, " other @parent"));
	}

	@Test
	public void resolveComponentsForClient_RelativeAndParentParent() {

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

	    assertEquals("Failed", "form:outerContainer:innerContainer:other form:outerContainer", resolveComponentsForClient(source, " other @parent:@parent"));
	}

	@Test
	public void resolveComponentsForClient_RelativeAndThisParent() {

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

	    assertEquals("Failed", "form:outerContainer:innerContainer:other form:outerContainer:innerContainer", resolveComponentsForClient(source, " other @this:@parent"));
	}

	@Test
	public void resolveComponentsForClient_RelativeAndPFSAndWidgetVarAndFormParent() {

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

	    assertEquals("Failed", "form:outerContainer:innerContainer:other @(.myClass, .myClass2) @widgetVar(test) root @(.myClass :not:(select))",
	    		resolveComponentsForClient(source, " other,@(.myClass, .myClass2) @widgetVar(test),@form:@parent @(.myClass :not:(select))"));
	}

	@Test
	public void resolveComponents_RelativeAndParent() {

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


	    List<UIComponent> resolvedComponents = resolveComponents(source, " other @parent");
	    assertTrue("Failed", resolvedComponents.contains(component));
	    assertTrue("Failed", resolvedComponents.contains(innerContainer));
	    assertEquals("Failed", 2, resolvedComponents.size());
	}

	@Test
	public void resolveComponents_RelativeAndParentParent() {

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

        List<UIComponent> resolvedComponents = resolveComponents(source, " other @parent:@parent ");
        assertTrue("Failed", resolvedComponents.contains(component));
        assertTrue("Failed", resolvedComponents.contains(outerContainer));
        assertEquals("Failed", 2, resolvedComponents.size());
	}

	@Test
	public void resolveComponents_RelativeAndThisParent() {

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

        List<UIComponent> resolvedComponents = resolveComponents(source, " other,@this:@parent ");
        assertTrue("Failed", resolvedComponents.contains(component));
        assertTrue("Failed", resolvedComponents.contains(innerContainer));
	    assertEquals("Failed", 2, resolvedComponents.size());
	}

	@Test
	public void resolveComponentsForClient_PFSNestedParenthese() {
	    UIComponent source = new UICommand();
	    source.setId("source");

	    assertEquals("@(.ui-panel :input:not(select)) @widgetVar(test)", resolveComponentsForClient(source, " @(.ui-panel :input:not(select)),@widgetVar(test) "));

	}
	
	@Test
	public void resolveComponentsForClient_PFSMultipleIds() {
	    UIComponent source = new UICommand();
	    source.setId("source");

	    assertEquals("source @(.ui-panel :input:not(select), #myPanel, #myPanel2) @(myId3) source", resolveComponentsForClient(source, " @this,@(.ui-panel :input:not(select), #myPanel, #myPanel2) @(myId3),@this"));

	}
	
	@Test
	public void resolveComponentForClient_NonCombineableAllAndNone() {

		UIComponent source = new UICommand();
		source.setId("source");

		try {
			resolveComponentsForClient(source, " :@form:asd @none @all ");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}
	}
	
	
	@Test
	public void resolveComponentWithParentFallback() {

	    UIComponent root = new UIPanel();

	    UIForm form = new UIForm();
	    form.setId("form");
	    root.getChildren().add(form);

	    assertEquals(
	    		root, 
	    		SearchExpressionFacade.resolveComponent(
	    				FacesContext.getCurrentInstance(), form, null, SearchExpressionFacade.PARENT_FALLBACK));

	    assertEquals(
	    		root, 
	    		SearchExpressionFacade.resolveComponent(
	    				FacesContext.getCurrentInstance(), form, " ", SearchExpressionFacade.PARENT_FALLBACK));
	}
	
	@Test
	public void resolveComponentsForClientWithParentFallback() {

	    UIComponent root = new UIPanel();
	    root.setId("test");

	    UIForm form = new UIForm();
	    form.setId("form");
	    root.getChildren().add(form);

	    assertEquals(
	    		"test", 
	    		SearchExpressionFacade.resolveComponentsForClient(
	    				FacesContext.getCurrentInstance(), form, null, SearchExpressionFacade.PARENT_FALLBACK));

	    assertEquals(
	    		"test", 
	    		SearchExpressionFacade.resolveComponentsForClient(
	    				FacesContext.getCurrentInstance(), form, " ", SearchExpressionFacade.PARENT_FALLBACK));
	}
	
	@Test
	public void resolveComponent_Next() {

	    UIComponent root = new UIPanel();
	    root.setId("root");

	    UIComponent command1 = new UICommand();
	    command1.setId("command1");
	    root.getChildren().add(command1);

	    UIComponent command2 = new UICommand();
	    command2.setId("command2");
	    root.getChildren().add(command2);
	    
	    UIComponent command3 = new UICommand();
	    command3.setId("command3");
	    root.getChildren().add(command3);

	    assertSame("Failed", command2, resolveComponent(command1, " @next "));
	    assertSame("Failed", command3, resolveComponent(command2, " @next "));

		try {
			resolveComponent(command3, " @next");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}
	}

	@Test
	public void resolveComponent_NextNext() {

	    UIComponent root = new UIPanel();
	    root.setId("root");

	    UIComponent command1 = new UICommand();
	    command1.setId("command1");
	    root.getChildren().add(command1);

	    UIComponent command2 = new UICommand();
	    command2.setId("command2");
	    root.getChildren().add(command2);
	    
	    UIComponent command3 = new UICommand();
	    command3.setId("command3");
	    root.getChildren().add(command3);

	    assertSame("Failed", command3, resolveComponent(command1, " @next:@next "));
	    
		try {
			resolveComponent(command2, " @next:@next");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}
	    
	    
		try {
			resolveComponent(command3, " @next:@next");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}
	}
	
	@Test
	public void resolveComponent_Previous() {

	    UIComponent root = new UIPanel();
	    root.setId("root");

	    UIComponent command1 = new UICommand();
	    command1.setId("command1");
	    root.getChildren().add(command1);

	    UIComponent command2 = new UICommand();
	    command2.setId("command2");
	    root.getChildren().add(command2);
	    
	    UIComponent command3 = new UICommand();
	    command3.setId("command3");
	    root.getChildren().add(command3);

	    assertSame("Failed", command1, resolveComponent(command2, " @previous "));
	    assertSame("Failed", command2, resolveComponent(command3, " @previous "));

		try {
			resolveComponent(command1, " @previous");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}
	}
	
	@Test
	public void resolveComponent_PreviousPrevious() {

	    UIComponent root = new UIPanel();
	    root.setId("root");

	    UIComponent command1 = new UICommand();
	    command1.setId("command1");
	    root.getChildren().add(command1);

	    UIComponent command2 = new UICommand();
	    command2.setId("command2");
	    root.getChildren().add(command2);
	    
	    UIComponent command3 = new UICommand();
	    command3.setId("command3");
	    root.getChildren().add(command3);

	    assertSame("Failed", command1, resolveComponent(command3, " @previous:@previous "));

		try {
			resolveComponent(command2, " @previous:@previous");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}
	    
		try {
			resolveComponent(command1, " @previous:@previous");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}
	}
	
	@Test
	public void resolveComponent_FormChildNextNext() {

	    UIForm root = new UIForm();
	    root.setId("form");

	    UIComponent command1 = new UICommand();
	    command1.setId("command1");
	    root.getChildren().add(command1);

	    UIComponent command2 = new UICommand();
	    command2.setId("command2");
	    root.getChildren().add(command2);
	    
	    UIComponent command3 = new UICommand();
	    command3.setId("command3");
	    root.getChildren().add(command3);

	    assertSame("Failed", command3, resolveComponent(command1, " @form:@child(0):@next:@next "));
	}
	
	
	@Test
	public void resolveComponent_NoResult() {
	    UIForm root = new UIForm();
	    root.setId("form");

	    UIComponent command1 = new UICommand();
	    command1.setId("command1");
	    root.getChildren().add(command1);

	    UIComponent command2 = new UICommand();
	    command2.setId("command2");
	    root.getChildren().add(command2);

	    try {
	    	assertSame("Failed", root, resolveComponent(command1, " command1:@parent:command3 "));
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}
	}

	@Test
	public void resolveComponent_IgnoreNoResult() {
	    UIForm root = new UIForm();
	    root.setId("form");

	    UIComponent command1 = new UICommand();
	    command1.setId("command1");
	    root.getChildren().add(command1);

	    UIComponent command2 = new UICommand();
	    command2.setId("command2");
	    root.getChildren().add(command2);

	    assertSame("Failed", null,
	    		resolveComponent(command1, " command3 ", SearchExpressionFacade.IGNORE_NO_RESULT));
	}
    
	@Test
	public void resolveComponent_WidgetVarNext() {

		UIComponent root = new UIPanel();
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

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
        
        InputText input = new InputText();
        input.setWidgetVar("myInput_Widget");
        outerContainer.getChildren().add(input);
        
        InputText input2 = new InputText();
        input2.setWidgetVar("myInput_Widget2");
        outerContainer.getChildren().add(input2);

		assertEquals("Failed", input2, resolveComponent(source, " @widgetVar(myInput_Widget):@next"));
	}
    
	@Test
	public void resolveComponentForClient_WidgetVarNext() {

		UIComponent root = new UIPanel();
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

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
        
        InputText input = new InputText();
        input.setWidgetVar("myInput_Widget");
        outerContainer.getChildren().add(input);
        
        InputText input2 = new InputText();
        input2.setId("input2");
        input2.setWidgetVar("myInput_Widget2");
        outerContainer.getChildren().add(input2);

		assertEquals("Failed", input2.getClientId(), resolveComponentForClient(source, " @widgetVar(myInput_Widget):@next"));
	}
    
	@Test
	public void resolveComponent_WidgetVar() {

		UIComponent root = new UIPanel();
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

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
        
        InputText input = new InputText();
        input.setWidgetVar("myInput_Widget");
        outerContainer.getChildren().add(input);

		assertEquals("Failed", input, resolveComponent(source, " @widgetVar(myInput_Widget)"));
	}
    
}

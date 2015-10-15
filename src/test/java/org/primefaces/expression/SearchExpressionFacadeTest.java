package org.primefaces.expression;

import java.util.ArrayList;
import org.primefaces.mock.FacesContextMock;
import org.primefaces.mock.TestVisitContextFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
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
import org.primefaces.component.inputtext.InputText;
import org.primefaces.expression.SearchExpressionFacade;

public class SearchExpressionFacadeTest {
    
	@Before
	public void setup() {
		Map<Object, Object> attributes = new HashMap<Object, Object>();
		attributes.put(UINamingContainer.SEPARATOR_CHAR_PARAM_NAME, ':');

		FacesContext context = new FacesContextMock(attributes);
		context.setViewRoot(new UIViewRoot());
        FactoryFinder.setFactory(FactoryFinder.VISIT_CONTEXT_FACTORY, TestVisitContextFactory.class.getName());
	}

	private UIComponent resolveComponent(UIComponent source, String expression) {
		FacesContext context = FacesContext.getCurrentInstance();

		return SearchExpressionFacade.resolveComponent(context, source, expression);
	}

	private UIComponent resolveComponent(UIComponent source, String expression, int options) {
		FacesContext context = FacesContext.getCurrentInstance();

		return SearchExpressionFacade.resolveComponent(context, source, expression, options);
	}

	private String resolveClientId(UIComponent source, String expression) {
		FacesContext context = FacesContext.getCurrentInstance();

		return SearchExpressionFacade.resolveClientId(context, source, expression);
	}

	private String resolveClientId(UIComponent source, String expression, int options) {
		FacesContext context = FacesContext.getCurrentInstance();

		return SearchExpressionFacade.resolveClientId(context, source, expression, options);
	}
    
    private List<UIComponent> resolveComponents(UIComponent source, String expression) {
        FacesContext context = FacesContext.getCurrentInstance();

        return SearchExpressionFacade.resolveComponents(context, source, expression);
    }

    private List<UIComponent> resolveComponents(UIComponent source, String expression, int options) {
        FacesContext context = FacesContext.getCurrentInstance();

        return SearchExpressionFacade.resolveComponents(context, source, expression, options);
    }

    private String resolveClientIds(UIComponent source, String expression) {
        FacesContext context = FacesContext.getCurrentInstance();

        return SearchExpressionFacade.resolveClientIds(context, source, expression);
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
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

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
	public void resolveClientId_ParentChild() {

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

	    assertEquals("Failed", "form:outerContainer:innerContainer:other", resolveClientId(source, " @parent:@child(0) "));
	    assertEquals("Failed", "form:outerContainer:innerContainer:source", resolveClientId(source, " @parent:@child(1) "));
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
	public void resolveClientId_None() {

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

		assertEquals("Failed", "@none", resolveClientId(source, " @none"));
	}

	@Test
	public void resolveClientId_PFS() {

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

		assertEquals("Failed", "@(.myClass, div)", resolveClientId(source, "@(.myClass, div) "));
	}

	@Test
	public void resolveClientId_All() {

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

		assertEquals("Failed", "@all", resolveClientId(source, "@all"));
	}

	@Test
	public void resolveClientId_WidgetVar() {

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

		assertEquals("Failed", "@widgetVar(myDialog_widget)", resolveClientId(source, " @widgetVar(myDialog_widget)"));
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
	public void resolveClientId_NotNestablePasstrough() {

		UIComponent source = new UICommand();
		source.setId("source");

		try {
			resolveClientId(source, " @none:@all:asd");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}
	}

	@Test
	public void resolveClientId_Parent() {

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

		assertEquals("Failed", "form:outerContainer:innerContainer", resolveClientId(source, " @parent "));
	}

	@Test
	public void resolveClientId_This() {

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

		assertEquals("Failed", "form:outerContainer:innerContainer:source", resolveClientId(source, " @this "));
	}

	@Test
	public void resolveClientId_Namingcontainer() {

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

		assertEquals("Failed", "form:outerContainer:innerContainer", resolveClientId(source, " @namingcontainer "));
	}

	@Test
	public void resolveClientId_Form() {

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

		assertEquals("Failed", "form", resolveClientId(source, " @form "));
	}

	@Test
	public void resolveClientId_Root() {

		UIComponent root = new UIPanel();
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

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

		assertEquals("Failed", "form", resolveClientId(source, " :form "));
	}

	@Test
	public void resolveClientId_Absolute() {

		UIComponent root = new UIPanel();
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

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

		assertEquals("Failed", "form:outerContainer:innerContainer:source", resolveClientId(source, " :form:outerContainer:innerContainer:source "));
	}

	@Test
	public void resolveClientId_Relative() {

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

		assertEquals("Failed", "form:outerContainer:innerContainer:other", resolveClientId(source, " other "));
	}


	@Test
	public void resolveClientId_AbsoluteForm() {

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

		assertEquals("Failed", "root", resolveClientId(source, " :form:@parent "));
	}

	@Test
	public void resolveClientId_AbsoluteNamingcontainer() {

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

		assertEquals("Failed", "form", resolveClientId(source, " :form:outerContainer:@namingcontainer "));
	}

	@Test
	public void resolveClientId_AbsoluteNamingcontainerParent() {

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

		assertEquals("Failed", "root", resolveClientId(source, " :form:outerContainer:@namingcontainer:@parent "));
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
	public void resolveClientId_AbsoluteKeywordStart() {

		UIComponent source = new UICommand();
		source.setId("source");

		try {
			resolveClientId(source, " :@form:asd");
			Assert.fail("This should actually raise an exception");
		} catch (Exception e) {
			assertEquals(FacesException.class, e.getClass());
		}
	}

	@Test
	public void resolveClientIds_RelativeAndParent() {

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

	    assertEquals("Failed", "form:outerContainer:innerContainer:other form:outerContainer:innerContainer", resolveClientIds(source, " other @parent"));
	}

	@Test
	public void resolveClientIds_RelativeAndParentParent() {

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

	    assertEquals("Failed", "form:outerContainer:innerContainer:other form:outerContainer", resolveClientIds(source, " other @parent:@parent"));
	}

	@Test
	public void resolveClientIds_RelativeAndThisParent() {

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

	    assertEquals("Failed", "form:outerContainer:innerContainer:other form:outerContainer:innerContainer", resolveClientIds(source, " other @this:@parent"));
	}

	@Test
	public void resolveClientIds_RelativeAndPFSAndWidgetVarAndFormParent() {

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
	    		resolveClientIds(source, " other,@(.myClass, .myClass2) @widgetVar(test),@form:@parent @(.myClass :not:(select))"));
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
	public void resolveClientIds_PFSNestedParenthese() {
	    UIComponent source = new UICommand();
	    source.setId("source");

	    assertEquals("@(.ui-panel :input:not(select)) @widgetVar(test)", resolveClientIds(source, " @(.ui-panel :input:not(select)),@widgetVar(test) "));

	}
	
	@Test
	public void resolveClientIds_PFSMultipleIds() {
	    UIComponent source = new UICommand();
	    source.setId("source");

	    assertEquals("source @(.ui-panel :input:not(select), #myPanel, #myPanel2) @(myId3) source", resolveClientIds(source, " @this,@(.ui-panel :input:not(select), #myPanel, #myPanel2) @(myId3),@this"));

	}
	
	@Test
	public void resolveClientId_NonCombineableAllAndNone() {

		UIComponent source = new UICommand();
		source.setId("source");

		try {
			resolveClientIds(source, " :@form:asd @none @all ");
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
	    				FacesContext.getCurrentInstance(), form, null, SearchExpressionFacade.Options.PARENT_FALLBACK));

	    assertEquals(
	    		root, 
	    		SearchExpressionFacade.resolveComponent(
	    				FacesContext.getCurrentInstance(), form, " ", SearchExpressionFacade.Options.PARENT_FALLBACK));
	}
	
	@Test
	public void resolveClientIdsWithParentFallback() {

	    UIComponent root = new UIPanel();
	    root.setId("test");

	    UIForm form = new UIForm();
	    form.setId("form");
	    root.getChildren().add(form);

	    assertEquals(
	    		"test", 
	    		SearchExpressionFacade.resolveClientIds(
	    				FacesContext.getCurrentInstance(), form, null, SearchExpressionFacade.Options.PARENT_FALLBACK));

	    assertEquals(
	    		"test", 
	    		SearchExpressionFacade.resolveClientIds(
	    				FacesContext.getCurrentInstance(), form, " ", SearchExpressionFacade.Options.PARENT_FALLBACK));
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
			assertEquals(ComponentNotFoundException.class, e.getClass());
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
			assertEquals(ComponentNotFoundException.class, e.getClass());
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
	    		resolveComponent(command1, " command3 ", SearchExpressionFacade.Options.IGNORE_NO_RESULT));
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
	public void resolveClientId_WidgetVarNext() {

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

		assertEquals("Failed", input2.getClientId(), resolveClientId(source, " @widgetVar(myInput_Widget):@next"));
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
    
    
	@Test
	public void resolveClientId_AbsoluteWithFormPrependIdFalse() {

		UIComponent root = new UIPanel();
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

		UIForm form = new UIForm();
		form.setId("form");
        form.setPrependId(false);
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

		assertEquals("Failed", "outerContainer:innerContainer:source", resolveClientId(source, " :form:outerContainer:innerContainer:source "));
	}
    
	@Test
	public void resolveClientId_AbsoluteWithFormPrependIdFalse_InvokeOnComponent() {

		UIComponent root = new UIPanel();
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

		UIForm form = new UIForm();
		form.setId("form");
        form.setPrependId(false);
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

		assertEquals("Failed", "outerContainer:innerContainer:source", resolveClientId(source, " outerContainer:innerContainer:source "));
	}

	@Test
	public void resolveClientId_AbsoluteWithFormPrependIdFalse_InvokeOnComponentSkipUnrendered() {

		UIComponent root = new UIPanel();
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

		UIForm form = new UIForm();
		form.setId("form");
        form.setPrependId(false);
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

		assertEquals("Failed", "outerContainer:innerContainer:source",
                resolveClientId(source, " outerContainer:innerContainer:source ", SearchExpressionFacade.Options.IGNORE_NO_RESULT));
	}
    
	@Test
	public void resolveClientId_AbsoluteWithFormPrependIdFalse_InvokeOnComponentSkipUnrendered_Unredered() {

		UIComponent root = new UIPanel();
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

		UIForm form = new UIForm();
		form.setId("form");
        form.setPrependId(false);
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
        innerContainer.setRendered(false);
		outerContainer.getChildren().add(innerContainer);

		UIComponent component = new UIOutput();
		innerContainer.getChildren().add(component);

		UIComponent source = new UICommand();
		source.setId("source");
		innerContainer.getChildren().add(source);

		assertEquals("Failed", null,
                resolveClientId(source, " outerContainer:innerContainer:source ", SearchExpressionFacade.Options.IGNORE_NO_RESULT));
	}
    
	@Test
	public void resolveComponents_SimpleMultiSearchExpressionResolver() {

		UIComponent root = new UIPanel();
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

		UIForm form = new UIForm();
		form.setId("form");
        form.setPrependId(false);
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

        ArrayList<UIComponent> components = new ArrayList<UIComponent>();
        components.add(outerContainer);
        components.add(innerContainer);
		
        SearchExpressionResolverFactory.registerResolver("@test", new TestMultiSearchExpressionResolver(components));
        List<UIComponent> result = resolveComponents(root, " @test ");
        assertTrue(result.size() == 2);
        assertTrue(result.contains(outerContainer));
        assertTrue(result.contains(innerContainer));
        
        SearchExpressionResolverFactory.removeResolver("@test");
	}
    
	@Test
	public void resolveComponents_SimpleMultiSearchExpressionResolver_Parent() {

		UIComponent root = new UIPanel();
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

		UIForm form = new UIForm();
		form.setId("form");
        form.setPrependId(false);
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

        ArrayList<UIComponent> components = new ArrayList<UIComponent>();
        components.add(outerContainer);
        components.add(innerContainer);
		
        SearchExpressionResolverFactory.registerResolver("@test", new TestMultiSearchExpressionResolver(components));
        List<UIComponent> result = resolveComponents(root, " @test:@parent ");
        assertTrue(result.size() == 2);
        assertTrue(result.contains(outerContainer));
        assertTrue(result.contains(form));
        
        SearchExpressionResolverFactory.removeResolver("@test");
	}
    
	@Test
	public void resolveComponents_SimpleMultiSearchExpressionResolver_ParentParent() {

		UIComponent root = new UIPanel();
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

		UIForm form = new UIForm();
		form.setId("form");
        form.setPrependId(false);
		root.getChildren().add(form);

		UINamingContainer outerContainer = new UINamingContainer();
		outerContainer.setId("outerContainer");
		form.getChildren().add(outerContainer);

		UINamingContainer innerContainer = new UINamingContainer();
		innerContainer.setId("innerContainer");
		outerContainer.getChildren().add(innerContainer);

        ArrayList<UIComponent> components = new ArrayList<UIComponent>();
        components.add(outerContainer);
        components.add(innerContainer);
		
        SearchExpressionResolverFactory.registerResolver("@test", new TestMultiSearchExpressionResolver(components));
        List<UIComponent> result = resolveComponents(root, " @test:@parent:@parent ");
        assertTrue(result.size() == 2);
        assertTrue(result.contains(root));
        assertTrue(result.contains(form));
        
        SearchExpressionResolverFactory.removeResolver("@test");
	}
    
	@Test
	public void resolveComponents_SimpleMultiSearchExpressionResolver_ParentParent_IgnoreNoResult() {

		UIComponent root = new UIPanel();
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

		UIForm form = new UIForm();
		form.setId("form");
        form.setPrependId(false);
		root.getChildren().add(form);


        ArrayList<UIComponent> components = new ArrayList<UIComponent>();
        components.add(form);
        components.add(root);
		
        SearchExpressionResolverFactory.registerResolver("@test", new TestMultiSearchExpressionResolver(components));
        List<UIComponent> result = resolveComponents(root, " @test:@parent:@parent ", SearchExpressionFacade.Options.IGNORE_NO_RESULT);
        assertTrue(result.size() == 1);
        assertTrue(result.contains(FacesContext.getCurrentInstance().getViewRoot()));
        
        SearchExpressionResolverFactory.removeResolver("@test");
	}
    
	@Test
	public void resolveClientIds_MultiSearchExpressionResolver_ClientIdSearchExpressionResolver() {

		UIComponent root = new UIPanel();
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

		UIForm form = new UIForm();
		form.setId("form");
        form.setPrependId(false);
		root.getChildren().add(form);


        ArrayList<UIComponent> components = new ArrayList<UIComponent>();
        components.add(form);
        components.add(root);
		
        SearchExpressionResolverFactory.registerResolver("@test", new TestMultiSearchExpressionResolver(components));
        SearchExpressionResolverFactory.registerResolver("@test2", new TestClientIdSearchExpressionResolver("hallo client id"));
        
        String result = resolveClientIds(root, " @test:@test2 ");
        assertEquals("hallo client id hallo client id", result);

        SearchExpressionResolverFactory.removeResolver("@test");
        SearchExpressionResolverFactory.removeResolver("@test2");
	}
}

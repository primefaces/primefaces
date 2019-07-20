package org.primefaces.model.menu;

import org.primefaces.event.MenuActionEvent;
import org.primefaces.util.SerializableFunction;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.MethodNotFoundException;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import java.util.List;
import java.util.function.Consumer;

public interface MenuItemHolder {

    List getElements();

    default void handleBroadcast(FacesEvent event, FacesContext context, Consumer<FacesEvent> broadcast) throws AbortProcessingException {
        if (event instanceof MenuActionEvent) {
            MenuActionEvent menuActionEvent = (MenuActionEvent) event;
            MenuItem menuItem = menuActionEvent.getMenuItem();

            SerializableFunction<MenuItem, String> function = menuItem.getFunction();
            String command = menuItem.getCommand();
            if (function != null) {
                String outcome = function.apply(menuItem);
                context.getApplication().getNavigationHandler().handleNavigation(context, null, outcome);
            }
            else if (command != null) {
                ELContext elContext = context.getELContext();
                ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();

                Object invokeResult = null;
                try {
                    MethodExpression me = expressionFactory.createMethodExpression(elContext, command,
                            String.class, new Class[0]);
                    invokeResult = me.invoke(elContext, null);
                }
                catch (MethodNotFoundException mnfe1) {
                    try {
                        MethodExpression me = expressionFactory.createMethodExpression(elContext, command,
                                String.class, new Class[]{ActionEvent.class});
                        invokeResult = me.invoke(elContext, new Object[]{event});
                    }
                    catch (MethodNotFoundException mnfe2) {
                        MethodExpression me = expressionFactory.createMethodExpression(elContext, command,
                                String.class, new Class[]{MenuActionEvent.class});
                        invokeResult = me.invoke(elContext, new Object[]{event});
                    }
                }
                finally {
                    String outcome = (invokeResult != null) ? invokeResult.toString() : null;

                    context.getApplication().getNavigationHandler().handleNavigation(context, command, outcome);
                }
            }
        }
        else {
            broadcast.accept(event);
        }
    }
}

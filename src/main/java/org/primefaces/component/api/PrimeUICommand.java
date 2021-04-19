package org.primefaces.component.api;

public interface PrimeUICommand extends AjaxSource, PrimeUIComponent {

    enum PropertyKeys {
        ajax,
        async,
        process,
        update,
        onstart,
        oncomplete,
        onerror,
        onsuccess,
        global,
        delay,
        timeout,
        partialSubmit,
        ignoreAutoUpdate,
        resetValues,
        ignoreComponentNotFound,
        form
    }

    @Override
    default boolean isAjaxified() {
        return isAjax();
    }

    default boolean isAjax() {
        return (Boolean) getState().eval(PropertyKeys.ajax, true);
    }

    default void setAjax(boolean ajax) {
        getState().put(PropertyKeys.ajax, ajax);
    }

    default boolean isAsync() {
        return (Boolean) getState().eval(PropertyKeys.async, false);
    }

    default void setAsync(boolean async) {
        getState().put(PropertyKeys.async, async);
    }

    default String getProcess() {
        return (String) getState().eval(PropertyKeys.process, null);
    }

    default void setProcess(String process) {
        getState().put(PropertyKeys.process, process);
    }

    default String getUpdate() {
        return (String) getState().eval(PropertyKeys.update, null);
    }

    default void setUpdate(String update) {
        getState().put(PropertyKeys.update, update);
    }

    default String getOnstart() {
        return (String) getState().eval(PropertyKeys.onstart, null);
    }

    default void setOnstart(String onstart) {
        getState().put(PropertyKeys.onstart, onstart);
    }

    default String getOncomplete() {
        return (String) getState().eval(PropertyKeys.oncomplete, null);
    }

    default void setOncomplete(String oncomplete) {
        getState().put(PropertyKeys.oncomplete, oncomplete);
    }

    default String getOnerror() {
        return (String) getState().eval(PropertyKeys.onerror, null);
    }

    default void setOnerror(String onerror) {
        getState().put(PropertyKeys.onerror, onerror);
    }

    default String getOnsuccess() {
        return (String) getState().eval(PropertyKeys.onsuccess, null);
    }

    default void setOnsuccess(String onsuccess) {
        getState().put(PropertyKeys.onsuccess, onsuccess);
    }

    default boolean isGlobal() {
        return (Boolean) getState().eval(PropertyKeys.global, true);
    }

    default void setGlobal(boolean global) {
        getState().put(PropertyKeys.global, global);
    }

    default String getDelay() {
        return (String) getState().eval(PropertyKeys.delay, null);
    }

    default void setDelay(String delay) {
        getState().put(PropertyKeys.delay, delay);
    }

    default int getTimeout() {
        return (Integer) getState().eval(PropertyKeys.timeout, 0);
    }

    default void setTimeout(int timeout) {
        getState().put(PropertyKeys.timeout, timeout);
    }

    default boolean isPartialSubmit() {
        return (Boolean) getState().eval(PropertyKeys.partialSubmit, false);
    }

    default void setPartialSubmit(boolean partialSubmit) {
        getState().put(PropertyKeys.partialSubmit, partialSubmit);
    }

    default boolean isResetValues() {
        return (Boolean) getState().eval(PropertyKeys.resetValues, false);
    }

    default void setResetValues(boolean resetValues) {
        getState().put(PropertyKeys.resetValues, resetValues);
    }

    default boolean isIgnoreAutoUpdate() {
        return (Boolean) getState().eval(PropertyKeys.ignoreAutoUpdate, false);
    }

    default void setIgnoreAutoUpdate(boolean ignoreAutoUpdate) {
        getState().put(PropertyKeys.ignoreAutoUpdate, ignoreAutoUpdate);
    }

    default String getForm() {
        return (String) getState().eval(PropertyKeys.form, null);
    }

    default void setForm(String form) {
        getState().put(PropertyKeys.form, form);
    }

    @Override
    default boolean isPartialSubmitSet() {
        return (getState().get(PrimeUICommand.PropertyKeys.partialSubmit) != null) || (getValueExpression(PropertyKeys.partialSubmit.toString()) != null);
    }

    @Override
    default boolean isResetValuesSet() {
        return (getState().get(PrimeUICommand.PropertyKeys.resetValues) != null) || (getValueExpression(PropertyKeys.resetValues.toString()) != null);
    }

    @Override
    default boolean isIgnoreComponentNotFound() {
        return (java.lang.Boolean) getState().eval(PropertyKeys.ignoreComponentNotFound, false);
    }

    default void setIgnoreComponentNotFound(boolean ignoreComponentNotFound) {
        getState().put(PropertyKeys.ignoreComponentNotFound, ignoreComponentNotFound);
    }
}

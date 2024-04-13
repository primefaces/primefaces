package org.primefaces.showcase;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.DataExporters;
import org.primefaces.showcase.view.data.dataexporter.TextExporter;

import javax.faces.application.Application;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

public class PFShowcaseSystemEventListener implements SystemEventListener {

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        DataExporters.register(DataTable.class, TextExporter.class, "txt");
    }

    @Override
    public boolean isListenerForSource(Object source) {
        return source instanceof Application;
    }
}

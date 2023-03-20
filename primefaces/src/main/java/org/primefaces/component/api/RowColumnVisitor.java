package org.primefaces.component.api;

import java.io.IOException;

import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.row.Row;

public interface RowColumnVisitor {

    void visitColumn(int index, UIColumn column) throws IOException;

    void visitRow(int index, Row row) throws IOException;

    void visitColumnGroup(int index, ColumnGroup colGroup) throws IOException;

    class Adapter implements RowColumnVisitor {

        @Override
        public void visitColumn(int index, UIColumn column) throws IOException {
            // NOOP
        }

        @Override
        public void visitRow(int index, Row row) throws IOException {
            // NOOP
        }

        @Override
        public void visitColumnGroup(int index, ColumnGroup colGroup) throws IOException {
            // NOOP
        }
    }
}

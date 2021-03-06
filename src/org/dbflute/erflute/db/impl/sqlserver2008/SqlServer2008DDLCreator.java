package org.dbflute.erflute.db.impl.sqlserver2008;

import org.dbflute.erflute.db.impl.sqlserver.SqlServerDDLCreator;
import org.dbflute.erflute.editor.model.ERDiagram;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.ERTable;
import org.dbflute.erflute.editor.model.diagram_contents.element.node.table.index.ERIndex;

public class SqlServer2008DDLCreator extends SqlServerDDLCreator {

    public SqlServer2008DDLCreator(ERDiagram diagram, boolean semicolon) {
        super(diagram, semicolon);
    }

    @Override
    public String doBuildDropIndex(ERIndex index, ERTable table) {
        StringBuilder ddl = new StringBuilder();

        ddl.append("DROP INDEX ");
        ddl.append(this.getIfExistsOption());
        ddl.append(filter(index.getName()));
        ddl.append(" ON ");
        ddl.append(filter(table.getNameWithSchema(this.getDiagram().getDatabase())));

        if (this.semicolon) {
            ddl.append(";");
        }

        return ddl.toString();
    }
}

package org.insightech.er.db.impl.sqlite;

import org.insightech.er.db.sqltype.SqlType;
import org.insightech.er.db.sqltype.SqlTypeManagerBase;

public class SQLiteSqlTypeManager extends SqlTypeManagerBase {

    public int getByteLength(SqlType type, Integer length, Integer decimal) {
        return 0;
    }

}

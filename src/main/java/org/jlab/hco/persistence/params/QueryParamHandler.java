package org.jlab.hco.persistence.params;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface QueryParamHandler {
    String getSqlWhereClause();

    void assignParameterValues(PreparedStatement stmt) throws SQLException;
}

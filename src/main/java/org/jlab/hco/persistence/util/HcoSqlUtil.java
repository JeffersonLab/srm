package org.jlab.hco.persistence.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author ryans
 */
public final class HcoSqlUtil {

    private static final Logger logger = Logger.getLogger(
            HcoSqlUtil.class.getName());

    private static DataSource source;

    static {
        try {
            source = (DataSource) new InitialContext().lookup("jdbc/hco");
        } catch (NamingException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private HcoSqlUtil() {
        // not public
    }

    public static Connection getConnection() throws SQLException {
        return source.getConnection();
    }

    public static Throwable findCause(Class<? extends Throwable> expected, Throwable exc) {
        Throwable cause = null;
        if(expected.isInstance(exc)) {
            cause = exc;
        } else if(exc != null) {
            cause = findCause(expected, exc.getCause());
        }
        return cause;
    }
}

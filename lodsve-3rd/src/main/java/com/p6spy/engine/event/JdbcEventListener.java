/**
 * P6Spy
 * <p>
 * Copyright (C) 2002 - 2017 P6Spy
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.p6spy.engine.event;

import com.p6spy.engine.common.CallableStatementInformation;
import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.ResultSetInformation;
import com.p6spy.engine.common.StatementInformation;

import java.sql.SQLException;

/**
 * Implementations of this class receive notifications for interesting JDBC events.
 * <p>
 * This class intentionally is not an interface so that methods can be added without breaking existing implementations.
 * <p>
 * There are two ways to register your custom implementation of this class.
 * The fist way is to add the fully qualified class name of your implementation to
 * <code>src/main/resources/META-INF/services/com.p6spy.engine.event.JdbcEventListener</code>.
 * <p>
 * The second way is to implement a {@link com.p6spy.engine.spy.P6Factory}
 * <p>
 * <b>NOTE:</b> Exceptions thrown in this event listener won't be caught. So you have to make sure that your event
 * listener does not throw exceptions. For example, if your {@link #onConnectionWrapped} method throws an exception
 * your application won't be able to create any {@link java.sql.Connection}.
 */
public abstract class JdbcEventListener {

    /**
     * This callback method is executed after a {@link java.sql.Connection} obtained from a {@link javax.sql.DataSource} or a {@link java.sql.Driver}.
     * <p>
     * The {@link ConnectionInformation} holds information about the creator of the connection which is either
     * {@link ConnectionInformation#dataSource}, {@link ConnectionInformation#driver} or
     * {@link ConnectionInformation#pooledConnection}, though {@link ConnectionInformation#connection} itself is <code>null</code>
     * when {@link java.sql.SQLException} is not <code>null</code> and vise versa.
     *
     * @param connectionInformation The meta information about the wrapped {@link java.sql.Connection}
     * @param e                     The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                              there was no exception).
     */
    public void onAfterGetConnection(ConnectionInformation connectionInformation, SQLException e) {
    }

    /**
     * This callback method is executed after a wrapped {@link java.sql.Connection} has been created.
     * <p>
     * The {@link ConnectionInformation} holds information about the creator of the connection which is either
     * {@link ConnectionInformation#dataSource}, {@link ConnectionInformation#driver} or
     * {@link ConnectionInformation#pooledConnection}.
     *
     * @param connectionInformation The meta information about the wrapped {@link java.sql.Connection}
     *
     * @deprecated Use {@link #onAfterGetConnection(ConnectionInformation, java.sql.SQLException)}
     */
    public void onConnectionWrapped(ConnectionInformation connectionInformation) {
    }

    /**
     * This callback method is executed before the {@link java.sql.PreparedStatement#addBatch()} method is invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     */
    public void onBeforeAddBatch(PreparedStatementInformation statementInformation) {
    }

    /**
     * This callback method is executed after the {@link java.sql.Statement#addBatch(String)} or
     * {@link java.sql.PreparedStatement#addBatch(String)} method is invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param timeElapsedNanos     The execution time of the execute call
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterAddBatch(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    }

    /**
     * This callback method is executed before the {@link java.sql.Statement#addBatch(String)} or
     * {@link java.sql.PreparedStatement#addBatch(String)} method is invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param sql                  The SQL string provided to the execute method
     */
    public void onBeforeAddBatch(StatementInformation statementInformation, String sql) {
    }

    /**
     * This callback method is executed after the {@link java.sql.Statement#addBatch(String)} or
     * {@link java.sql.PreparedStatement#addBatch(String)} method is invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param timeElapsedNanos     The execution time of the execute call
     * @param sql                  The SQL string provided to the execute method
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterAddBatch(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    }


    /**
     * This callback method is executed before any of the {@link java.sql.PreparedStatement#execute()} methods are invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     */
    public void onBeforeExecute(PreparedStatementInformation statementInformation) {
    }

    /**
     * This callback method is executed after any the {@link java.sql.PreparedStatement#execute()} methods are invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param timeElapsedNanos     The execution time of the execute call
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterExecute(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    }

    /**
     * This callback method is executed before any of the {@link java.sql.Statement#execute(String)} methods are invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param sql                  The SQL string provided to the execute method
     */
    public void onBeforeExecute(StatementInformation statementInformation, String sql) {
    }

    /**
     * This callback method is executed after any the {@link java.sql.Statement#execute(String)} methods are invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param timeElapsedNanos     The execution time of the execute call
     * @param sql                  The SQL string provided to the execute method
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterExecute(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    }


    /**
     * This callback method is executed before the {@link java.sql.Statement#executeBatch()} method is invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     */
    public void onBeforeExecuteBatch(StatementInformation statementInformation) {
    }

    /**
     * This callback method is executed after the {@link java.sql.Statement#executeBatch()} method is invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param timeElapsedNanos     The execution time of the execute call
     * @param updateCounts         An array of update counts or null if an exception was thrown
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterExecuteBatch(StatementInformation statementInformation, long timeElapsedNanos, int[] updateCounts, SQLException e) {
    }


    /**
     * This callback method is executed before the {@link java.sql.PreparedStatement#executeUpdate()} method is invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     */
    public void onBeforeExecuteUpdate(PreparedStatementInformation statementInformation) {
    }

    /**
     * This callback method is executed after the {@link java.sql.PreparedStatement#executeUpdate()} method is invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param timeElapsedNanos     The execution time of the execute call
     * @param rowCount             Either the row count for SQL Data Manipulation Language (DML) statements or 0 for SQL
     *                             statements that return nothing or if an exception was thrown
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterExecuteUpdate(PreparedStatementInformation statementInformation, long timeElapsedNanos, int rowCount, SQLException e) {
    }

    /**
     * This callback method is executed before any of the {@link java.sql.Statement#executeUpdate(String)} methods are invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param sql                  The SQL string provided to the execute method
     */
    public void onBeforeExecuteUpdate(StatementInformation statementInformation, String sql) {
    }

    /**
     * This callback method is executed after any of the {@link java.sql.Statement#executeUpdate(String)} methods are invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param timeElapsedNanos     The execution time of the execute call
     * @param sql                  The SQL string provided to the execute method
     * @param rowCount             Either the row count for SQL Data Manipulation Language (DML) statements or 0 for SQL
     *                             statements that return nothing or if an exception was thrown
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterExecuteUpdate(StatementInformation statementInformation, long timeElapsedNanos, String sql, int rowCount, SQLException e) {
    }


    /**
     * This callback method is executed before the {@link java.sql.PreparedStatement#executeQuery()} method is invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     */
    public void onBeforeExecuteQuery(PreparedStatementInformation statementInformation) {
    }

    /**
     * This callback method is executed after the {@link java.sql.PreparedStatement#executeQuery()} method is invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param timeElapsedNanos     The execution time of the execute call
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterExecuteQuery(PreparedStatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    }

    /**
     * This callback method is executed before the {@link java.sql.Statement#executeQuery(String)} method is invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param sql                  The SQL string provided to the execute method
     */
    public void onBeforeExecuteQuery(StatementInformation statementInformation, String sql) {
    }

    /**
     * This callback method is executed after the {@link java.sql.Statement#executeQuery(String)} method is invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param timeElapsedNanos     The execution time of the execute call
     * @param sql                  The SQL string provided to the execute method
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterExecuteQuery(StatementInformation statementInformation, long timeElapsedNanos, String sql, SQLException e) {
    }


    /**
     * This callback method is executed after any of the {@link java.sql.PreparedStatement}.set* methods are invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param parameterIndex       The first parameter is 1, the second is 2, ...
     * @param value                the column value; if the value is SQL NULL, the value returned is null
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterPreparedStatementSet(PreparedStatementInformation statementInformation, int parameterIndex, Object value, SQLException e) {
    }

    /**
     * This callback method is executed after any of the {@link java.sql.CallableStatement}.set* methods are invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param parameterName        The name of the parameter
     * @param value                the column value; if the value is SQL NULL, the value returned is null
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterCallableStatementSet(CallableStatementInformation statementInformation, String parameterName, Object value, SQLException e) {
    }

    /**
     * This callback method is executed after the {@link java.sql.Statement#getResultSet()} method is invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param timeElapsedNanos     The execution time of the execute call
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterGetResultSet(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
    }

    /**
     * This callback method is executed before the {@link java.sql.ResultSet#next()} method is invoked.
     *
     * @param resultSetInformation The meta information about the {@link java.sql.ResultSet} being invoked
     */
    public void onBeforeResultSetNext(ResultSetInformation resultSetInformation) {
    }

    /**
     * This callback method is executed after the {@link java.sql.ResultSet#next()} method is invoked.
     *
     * @param resultSetInformation The meta information about the {@link java.sql.ResultSet} being invoked
     * @param timeElapsedNanos     The execution time of the execute call
     * @param hasNext              The return value of {@link java.sql.ResultSet#next()}
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterResultSetNext(ResultSetInformation resultSetInformation, long timeElapsedNanos, boolean hasNext, SQLException e) {
    }

    /**
     * This callback method is executed after the {@link java.sql.ResultSet#close()} method is invoked.
     *
     * @param resultSetInformation The meta information about the {@link java.sql.ResultSet} being invoked
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterResultSetClose(ResultSetInformation resultSetInformation, SQLException e) {
    }

    /**
     * This callback method is executed after any of the {@link java.sql.ResultSet}#get*(String) methods are invoked.
     *
     * @param resultSetInformation The meta information about the {@link java.sql.ResultSet} being invoked
     * @param columnLabel          The label for the column specified with the SQL AS clause. If the SQL AS clause was
     *                             not specified, then the label is the name of the column
     * @param value                The column value; if the value is SQL NULL, the value returned is null
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterResultSetGet(ResultSetInformation resultSetInformation, String columnLabel, Object value, SQLException e) {
    }

    /**
     * This callback method is executed after any of the {@link java.sql.ResultSet}#get*(int) methods are invoked.
     *
     * @param resultSetInformation The meta information about the {@link java.sql.ResultSet} being invoked
     * @param columnIndex          the first column is 1, the second is 2, ...
     * @param value                the column value; if the value is SQL NULL, the value returned is null
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterResultSetGet(ResultSetInformation resultSetInformation, int columnIndex, Object value, SQLException e) {
    }

    /**
     * This callback method is executed before the {@link java.sql.Connection#commit()} method is invoked.
     *
     * @param connectionInformation The meta information about the {@link java.sql.Connection} being invoked
     */
    public void onBeforeCommit(ConnectionInformation connectionInformation) {
    }

    /**
     * This callback method is executed after the {@link java.sql.Connection#commit()} method is invoked.
     *
     * @param connectionInformation The meta information about the {@link java.sql.Connection} being invoked
     * @param timeElapsedNanos      The execution time of the execute call
     * @param e                     The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                              there was no exception).
     */
    public void onAfterCommit(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {
    }

    /**
     * This callback method is executed after the {@link java.sql.Connection#close()} method is invoked.
     *
     * @param connectionInformation The meta information about the {@link java.sql.Connection} being invoked
     * @param e                     The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                              there was no exception).
     */
    public void onAfterConnectionClose(ConnectionInformation connectionInformation, SQLException e) {
    }

    /**
     * This callback method is executed before the {@link java.sql.Connection#rollback()} or the {@link
     * java.sql.Connection#rollback(java.sql.Savepoint)} method is invoked.
     *
     * @param connectionInformation The meta information about the {@link java.sql.Connection} being invoked
     */
    public void onBeforeRollback(ConnectionInformation connectionInformation) {
    }

    /**
     * This callback method is executed after the {@link java.sql.Connection#rollback()} or the {@link
     * java.sql.Connection#rollback(java.sql.Savepoint)} method is invoked.
     *
     * @param connectionInformation The meta information about the {@link java.sql.Connection} being invoked
     * @param timeElapsedNanos      The execution time of the execute call
     * @param e                     The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                              there was no exception).
     */
    public void onAfterRollback(ConnectionInformation connectionInformation, long timeElapsedNanos, SQLException e) {
    }

    /**
     * This callback method is executed after the {@link java.sql.Statement#close()} method is invoked.
     *
     * @param statementInformation The meta information about the {@link java.sql.Statement} being invoked
     * @param e                    The {@link java.sql.SQLException} which may be triggered by the call (<code>null</code> if
     *                             there was no exception).
     */
    public void onAfterStatementClose(StatementInformation statementInformation, SQLException e) {
    }

}
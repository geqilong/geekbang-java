package org.geektimes.projects.user.mybatis.annotation;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class MyTransaction implements Transaction {
    private Transaction transaction;
    private TransactionIsolationLevel level;
    private boolean autoCommit;

    public MyTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        this.transaction = new JdbcTransaction(dataSource, level, autoCommit);
        this.level = level;
        this.autoCommit = autoCommit;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = transaction.getConnection();
        connection.setTransactionIsolation(level.getLevel());
        connection.setAutoCommit(autoCommit);
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        transaction.commit();
    }

    @Override
    public void rollback() throws SQLException {
        transaction.rollback();
    }

    @Override
    public void close() throws SQLException {
        transaction.close();
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return transaction.getTimeout();
    }

    public TransactionIsolationLevel getLevel() {
        return level;
    }

    public void setLevel(TransactionIsolationLevel level) {
        this.level = level;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }
}
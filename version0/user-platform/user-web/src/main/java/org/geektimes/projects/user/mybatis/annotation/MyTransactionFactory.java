package org.geektimes.projects.user.mybatis.annotation;


import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

public class MyTransactionFactory  implements TransactionFactory {

    @Override
    public Transaction newTransaction(Connection conn) {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new MyTransaction(dataSource, level, autoCommit);
    }
}

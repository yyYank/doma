/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.internal.jdbc.query;

import static org.seasar.doma.internal.util.AssertionUtil.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.seasar.doma.entity.Entity;
import org.seasar.doma.internal.expr.ExpressionEvaluator;
import org.seasar.doma.internal.jdbc.sql.NodePreparedSqlBuilder;
import org.seasar.doma.internal.jdbc.sql.PreparedSql;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.SqlExecutionSkipCause;
import org.seasar.doma.jdbc.SqlFile;

/**
 * @author taedium
 * 
 */
public abstract class SqlFileBatchModifyQuery<I, E extends Entity<I>>
        implements BatchModifyQuery {

    protected final Class<E> entityClass;

    protected Config config;

    protected String sqlFilePath;

    protected String parameterName;

    protected List<E> entities;

    protected String callerClassName;

    protected String callerMethodName;

    protected final List<PreparedSql> sqls = new ArrayList<PreparedSql>();

    protected boolean optimisticLockCheckRequired;

    protected boolean executable;

    protected SqlExecutionSkipCause sqlExecutionSkipCause = SqlExecutionSkipCause.BATCH_TARGET_NONEXISTENT;

    protected int queryTimeout;

    protected int batchSize;

    protected E entity;

    public SqlFileBatchModifyQuery(Class<E> entityClass) {
        assertNotNull(entityClass);
        this.entityClass = entityClass;
    }

    public void compile() {
        assertNotNull(config, sqlFilePath, parameterName, callerClassName, callerMethodName);
        Iterator<? extends E> it = entities.iterator();
        if (it.hasNext()) {
            executable = true;
            sqlExecutionSkipCause = null;
            entity = it.next();
            prepareOptions();
            prepareSql();
        } else {
            return;
        }
        while (it.hasNext()) {
            entity = it.next();
            prepareSql();
        }
        assertEquals(entities.size(), sqls.size());
    }

    protected void prepareOptions() {
        if (queryTimeout <= 0) {
            queryTimeout = config.queryTimeout();
        }
    }

    protected void prepareSql() {
        ExpressionEvaluator evaluator = new ExpressionEvaluator(Collections
                .singletonMap(parameterName, entity));
        NodePreparedSqlBuilder sqlBuilder = new NodePreparedSqlBuilder(config,
                evaluator);
        SqlFile sqlFile = config.sqlFileRepository()
                .getSqlFile(sqlFilePath, config.dialect());
        config.jdbcLogger()
                .logSqlFile(callerClassName, callerMethodName, sqlFile);
        PreparedSql sql = sqlBuilder.build(sqlFile.getSqlNode());
        sqls.add(sql);
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setSqlFilePath(String sqlFilePath) {
        this.sqlFilePath = sqlFilePath;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public void setEntities(List<I> entities) {
        assertNotNull(entities);
        this.entities = new ArrayList<E>(entities.size());
        for (I i : entities) {
            E entity = entityClass.cast(i);
            this.entities.add(entity);
        }
    }

    public void setCallerClassName(String callerClassName) {
        this.callerClassName = callerClassName;
    }

    public void setCallerMethodName(String callerMethodName) {
        this.callerMethodName = callerMethodName;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public PreparedSql getSql() {
        return sqls.get(0);
    }

    @Override
    public String getClassName() {
        return callerClassName;
    }

    @Override
    public String getMethodName() {
        return callerMethodName;
    }

    @Override
    public List<PreparedSql> getSqls() {
        return sqls;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public boolean isOptimisticLockCheckRequired() {
        return optimisticLockCheckRequired;
    }

    @Override
    public boolean isAutoGeneratedKeysSupported() {
        return false;
    }

    @Override
    public boolean isExecutable() {
        return executable;
    }

    @Override
    public SqlExecutionSkipCause getSqlExecutionSkipCause() {
        return sqlExecutionSkipCause;
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public int getBatchSize() {
        return batchSize;
    }

    @Override
    public String toString() {
        return sqls.toString();
    }

}

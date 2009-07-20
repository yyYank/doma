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
package org.seasar.doma.internal.jdbc.dialect;

import static org.seasar.doma.internal.util.AssertionUtil.*;

import org.seasar.doma.internal.jdbc.sql.node.AnonymousNode;
import org.seasar.doma.internal.jdbc.sql.node.SelectStatementNode;
import org.seasar.doma.internal.jdbc.sql.node.SelectStatementNodeVisitor;
import org.seasar.doma.jdbc.SelectForUpdateType;
import org.seasar.doma.jdbc.SqlNode;

/**
 * @author taedium
 * 
 */
public class StandardForUpdateTransformer implements
        SelectStatementNodeVisitor<SqlNode, Void> {

    protected final SelectForUpdateType forUpdateType;

    protected int waitSeconds;

    protected String[] aliases;

    protected boolean processed;

    public StandardForUpdateTransformer(SelectForUpdateType forUpdateType,
            int waitSeconds, String... aliases) {
        assertNotNull(forUpdateType);
        this.forUpdateType = forUpdateType;
        this.waitSeconds = waitSeconds;
        this.aliases = aliases;
    }

    public SqlNode transform(SqlNode sqlNode) {
        AnonymousNode result = new AnonymousNode();
        for (SqlNode child : sqlNode.getChildren()) {
            result.addNode(child.accept(this, null));
        }
        return result;
    }

    @Override
    public SqlNode visitSelectStatementNode(SelectStatementNode node, Void p) {
        return node;
    }

    @Override
    public SqlNode visitUnknownNode(SqlNode node, Void p) {
        return node;
    }

}

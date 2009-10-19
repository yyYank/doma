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
package org.seasar.doma.jdbc;

import org.seasar.doma.MessageCode;
import org.seasar.doma.internal.message.DomaMessageCode;

/**
 * 一意制約違反が発生した場合にスローされる例外です。
 * 
 * @author taedium
 * 
 */
public class UniqueConstraintException extends JdbcException {

    private static final long serialVersionUID = 1L;

    /** 未加工SQL */
    protected final String rawSql;

    /** フォーマット済みSQL、バッチ処理時にスローされた場合 {@code null} */
    protected final String formattedSql;

    /**
     * SQLを指定してインスタンスを構築します。
     * 
     * @param sql
     *            SQL
     * @param cause
     *            原因
     */
    public UniqueConstraintException(Sql<?> sql, Throwable cause) {
        this(sql.getRawSql(), sql.getFormattedSql(), cause);
    }

    /**
     * 未加工SQLとフォーマット済みSQLを指定してインスタンスを構築します。
     * 
     * @param rawSql
     *            未加工SQL
     * @param formattedSql
     *            フォーマット済みSQL
     * @param cause
     *            原因
     */
    public UniqueConstraintException(String rawSql, String formattedSql,
            Throwable cause) {
        super(DomaMessageCode.DOMA2004, formattedSql, rawSql, cause);
        this.rawSql = rawSql;
        this.formattedSql = formattedSql;
    }

    /**
     * メッセージコードと未加工SQLを指定してインスタンスを構築します。
     * 
     * @param messageCode
     *            メッセージコード
     * @param rawSql
     *            未加工SQL
     * @param cause
     *            原因
     */
    protected UniqueConstraintException(MessageCode messageCode, String rawSql,
            Throwable cause) {
        super(messageCode, cause, rawSql, cause);
        this.rawSql = rawSql;
        this.formattedSql = null;
    }

    /**
     * 未加工SQLを返します。
     * 
     * @return 未加工SQL
     */
    public String getRawSql() {
        return rawSql;
    }

    /**
     * フォーマット済みSQLを返します。
     * 
     * @return フォーマット済みSQL、存在しない場合 {@code null}
     */
    public String getFormattedSql() {
        return formattedSql;
    }

}

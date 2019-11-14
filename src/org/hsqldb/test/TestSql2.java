/* Copyright (c) 2001-2019, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG,
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package org.hsqldb.test;

import junit.framework.TestCase;
import junit.framework.TestResult;

import java.sql.*;

/**
 * Test sql statements via jdbc against in-memory database
 *
 * @author Fred Toussi (fredt@users dot sourceforge.net)
 */
@SuppressWarnings("SqlDialectInspection")
public class TestSql2 extends TestBase {

    Statement stmnt;
    Connection connection;

    public TestSql2(String name) {
        super(name);
    }

    protected void setUp() throws Exception {

        super.setUp();
        connection = super.newConnection();
        stmnt = connection.createStatement();
    }

    @SuppressWarnings("SqlNoDataSourceInspection")
    public void test0() throws SQLException {
        stmnt.execute("drop table test if exists");
        stmnt.execute("create table test(id int primary key, value varchar(32))");
        try {
            connection.setAutoCommit(false);
            stmnt.execute("insert into test values(1, 'a')");
            stmnt.execute("insert into test values(2, 'b')");
            stmnt.execute("insert into test values(3, 'c')");
            stmnt.executeQuery("select * from test where id > 1");
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
        }
//        stmnt.execute(
//                "CREATE TABLE T (I IDENTITY, A CHAR(20), B CHAR(20));");
//        stmnt.execute(
//                "INSERT INTO T VALUES (NULL, 'get_column_name', '"
//                        + getColumnName + "');");
//
//        ResultSet rs = stmnt.executeQuery(
//                "SELECT I, A, B, A \"aliasA\", B \"aliasB\", 1 FROM T;");
//        ResultSetMetaData rsmd = rs.getMetaData();
//
//        result5 = "";
//
//        for (; rs.next(); ) {
//            for (int i = 0; i < rsmd.getColumnCount(); i++) {
//                result5 += rsmd.getColumnName(i + 1) + ":"
//                        + rs.getString(i + 1) + ":";
//            }
//
//            result5 += "\n";
//        }
//
//        rs.close();
    }

//    public static void main(String[] argv) {
//
//        TestResult result = new TestResult();
//        TestCase testA = new TestSql2("testMetaData");
//        TestCase testB = new TestSql2("testDoubleNaN");
//        TestCase testC = new TestSql2("testAny");
//
//        testA.run(result);
//        testB.run(result);
//        testC.run(result);
//        System.out.println("TestSql error count: " + result.failureCount());
//    }
}

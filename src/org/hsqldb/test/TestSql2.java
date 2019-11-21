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

import org.hsqldb.Database;
import org.hsqldb.server.Server;
import org.hsqldb.server.WebServer;

import java.sql.*;

/**
 * Test sql statements via jdbc against in-memory database
 *
 * @author Fred Toussi (fredt@users dot sourceforge.net)
 */
@SuppressWarnings({"SqlDialectInspection", "FieldCanBeLocal", "unused"})
public class TestSql2 {

    private static String dbPath = "mem:test;sql.enforce_strict_size=true;sql.restrict_exec=true;hsqldb.tx=mvcc";
    private static String serverProps;
    private static String url;
    private static String user = "sa";
    private static String password = "";
    private static Server server;
    private static boolean isNetwork = true;
    private static boolean isHTTP = false;    // Set false to test HSQL protocol, true to test HTTP, in which case you can use isUseTestServlet to target either HSQL's webserver, or the Servlet server-mode
    private static boolean isServlet = false;

    interface TxHandler {
        void handle(Statement stmt) throws Exception;
    }

    private static void doTx(TxHandler tx) throws SQLException {
        Connection conn = newConnection();
        Statement stmt = conn.createStatement();
        try {
            conn.setAutoCommit(false);
            tx.handle(stmt);
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
        }
    }

    @SuppressWarnings("Duplicates")
    static protected void setUp() {
        if (isNetwork) {
            //  change the url to reflect your preferred db location and name
            if (url == null) {
                if (isServlet) {
                    url = "jdbc:hsqldb:http://localhost:8080/HSQLwebApp/test";
                } else if (isHTTP) {
                    url = "jdbc:hsqldb:http://localhost:8085/test";
                } else {
                    url = "jdbc:hsqldb:hsql://localhost/test";
                }
            }
            if (!isServlet) {
                server = isHTTP ? new WebServer()
                        : new Server();
                if (isHTTP) {
                    server.setPort(8085);
                }
                server.setDatabaseName(0, "test");
                server.setDatabasePath(0, dbPath);
                server.setLogWriter(null);
                server.setErrWriter(null);
                server.start();
            }
        } else {
            if (url == null) {
                url = "jdbc:hsqldb:" + dbPath;
            }
        }
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
            Connection conn = newConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("drop table test if exists");
            stmt.execute("create table test(id int primary key, value varchar(32))");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(".setUp() error: " + e.getMessage());
        }
    }

    static void tearDown() {

        if (isNetwork && !isServlet) {
            server.shutdownWithCatalogs(Database.CLOSEMODE_IMMEDIATELY);

            server = null;
        }
    }

    static Connection newConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private static void select() throws SQLException {
        doTx(stmt -> {
            ResultSet res = stmt.executeQuery("select * from test");
            System.out.println(res.next());
        });
    }

    private static void insert(int i) throws SQLException {
        doTx(stmt -> {
            stmt.execute(String.format("insert into test values(%d, '%s')", i, 'a' - 1 + i));
        });
    }

    @SuppressWarnings("SqlNoDataSourceInspection")
    public static void main(String[] args) throws Exception {
        setUp();
        doTx(stmt -> {
            stmt.execute("insert into test values(1, 'a')");
            doTx(stmt1 -> {
                stmt1.execute("insert into test values(2, 'b')");
                select();
            });
        });
        tearDown();
    }
}

package net.algem.util;

import junit.framework.TestCase;
import net.algem.TestProperties;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataConnectionTest extends TestCase {

    private DataConnection dc;
    private SimpleService simpleService;

    public void setUp() throws Exception {
        super.setUp();
        dc = TestProperties.getDataConnection();
        simpleService = new SimpleService(dc);
        simpleService.initTables();
    }

    public void tearDown() throws Exception {
        simpleService.dropTables();
    }

    private void txError() {
        throw new RuntimeException("simulated tx error");
    }

    public void testWithoutTransaction() throws Exception {
        try {
            simpleService.op1();
            txError();
            simpleService.op2();
        } catch (Exception e) {

        }
        assertEquals(1, simpleService.count());
    }

    public void testWithTransaction() throws Exception {

        try {
            dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
                @Override
                public Void run(DataConnection conn) throws Exception {
                    simpleService.op1();
                    txError();
                    simpleService.op2();
                    return null;
                }
            });
        } catch (Exception e) {

        }
        assertEquals(0, simpleService.count());
    }


    static class SimpleService {
        final DataConnection dc;

        SimpleService(DataConnection dc) {
            this.dc = dc;
        }

        public void initTables() throws SQLException {
            dc.executeUpdate("create table if not exists test (value int)");
        }

        void insertVal(int value) throws SQLException {
            dc.executeUpdate("insert into test values (" + value + ")");
        }

        public void op1() throws Exception {
            insertVal(1);
        }

        public void op2() throws Exception {
            insertVal(2);
        }

        public int count() throws SQLException {
            ResultSet resultSet = dc.executeQuery("select count(*) from test");
            resultSet.next();
            int c = resultSet.getInt(1);
            resultSet.close();
            return c;
        }

        public void dropTables() throws SQLException {
            dc.executeUpdate("drop table test");
        }
    }
}
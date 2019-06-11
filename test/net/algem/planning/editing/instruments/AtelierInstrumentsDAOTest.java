package net.algem.planning.editing.instruments;

import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.util.DataConnection;

public class AtelierInstrumentsDAOTest extends TestCase {
    private AtelierInstrumentsDAO dao;
    private DataConnection dc;

    @Override
    protected void setUp() throws Exception {
        dc = TestProperties.getDataConnection();
        dao = new AtelierInstrumentsDAO(dc);
        dc.executeUpdate("insert into atelier_instruments values (10, 20, 30)");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dc.executeUpdate("delete from " + AtelierInstrumentsDAO.TABLE);
    }

    public void testFind() throws Exception {
        assertEquals(new AtelierInstrument(10, 20, 30), dao.find(10, 20));
    }

    public void testFindMissing() throws Exception {
        assertEquals(null, dao.find(10, 30));
        assertEquals(null, dao.find(20, 20));
    }

    public void testSave() throws Exception {
        AtelierInstrument row = new AtelierInstrument(40, 50, 60);
        dao.save(row);
        assertEquals(row, dao.find(40, 50));

        //Update
        AtelierInstrument updatedRow = new AtelierInstrument(40, 50, 70);
        dao.save(updatedRow);
        assertEquals(updatedRow, dao.find(40, 50));
    }

    public void testDelete() throws Exception {
        assertNotNull(dao.find(10, 20));
        dao.delete(10, 20);
        assertNull(dao.find(10, 20));
    }
}
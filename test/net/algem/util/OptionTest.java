package net.algem.util;

import junit.framework.TestCase;

public class OptionTest extends TestCase {

    private Option<String> stringOption;
    private Option<String> none;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        stringOption = Option.of("Hello");
        none = Option.none();
    }

    public void testIsPresent() throws Exception {
        //Given a filled option
        //When I test if a value is present
        //Then it returns true
        assertTrue(stringOption.isPresent());

        //Given an empty option
        //When I test if a value is present
        //Then it returns false
        assertFalse(none.isPresent());
    }

    public void testForeach() throws Exception {
        //Given a filled option
        //When I iterate over the option
        //Then the loop is called exactly once with the inner value of the option
        int i = 0;
        for (String s : stringOption) {
            assertEquals("Hello", s);
            i++;
        }
        assertEquals(1, i);

        //Given an empty option
        //When I iterate over the option
        //Then the loop is never called
        for (String ignored : none) {
            assertTrue("The loop is never called", false);
        }
    }

}
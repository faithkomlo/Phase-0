package edu.lehigh.cse216.phase0.backend.fko218;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }


    public void testNullVal1Val2() {
        // This test ensures that a datum without val1 or val2 fields won't be
        // inserted
        App a = new App();
        String initialData = a.getAllData();
        Datum d = new Datum();
        d.val1 = null; 
        d.val2 = null; 
        a.insertDatum(d);
        String finalData = a.getAllData();
        assertTrue(initialData.equals(finalData));
    }

    public void testNullVal1() {
        App a = new App();
        String initialData = a.getAllData();
        Datum d = new Datum();
        d.val1 = null; 
        d.val2 = "World"; 
        a.insertDatum(d);
        String finalData = a.getAllData();
        assertTrue(initialData.equals(finalData));
    }

    public void testNullVal2() {
        App a = new App();
        String initialData = a.getAllData();
        Datum d = new Datum();
        d.val1 = "Hello";
        d.val2 = null;
        a.insertDatum(d);
        String finalData = a.getAllData();
        assertTrue(initialData.equals(finalData));
    }

    public void testInitialEmpty() {
        App a = new App();
        String initialData = a.getAllData();
        assertTrue(initialData.equals("[]"));
    }

    public void testNoNullVals() {
        App a = new App();
        Datum d = new Datum();
        d.val1 = "Hello";
        d.val2 = "World";
        a.insertDatum(d);
        String finalData = a.getAllData();
        assertTrue(finalData.equals("[{\"index\":0,\"val1\":\"Hello\"," + "\"val2\":\"World\"}]"));
    }
}

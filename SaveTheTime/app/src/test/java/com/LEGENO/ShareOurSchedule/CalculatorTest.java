package com.LEGENO.SaveTheTime;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CalculatorTest {
    private Calculator calculator;

    @Before
    public void setUp() throws Exception{
        calculator=new Calculator();
    }
    @Test
    public void testAdd(){
        assertEquals(3,calculator.add(1,2));
    }
    @Test
    public void subTest(){
        assertEquals(-1,calculator.sub(1,2));
    }
}

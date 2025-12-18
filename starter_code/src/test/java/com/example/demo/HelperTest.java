package com.example.demo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HelperTest {

    //Always void and public
    @Test
    public void testHelper() {
        assertEquals(3, 3);
    }

    @Test
    public void test1() {
        List<String> nameList = Arrays.asList("A", "B", "C");
        //Helper.getCount(nameList);
        assertEquals(3, nameList.size());
    }

    @Test
    public void verifyStatus() {
        List<Integer> yearsExp = Arrays.asList(13, 4, 5, 2, 20, 1, 19);
        IntSummaryStatistics stats = Helper.getStats(yearsExp);
        assertEquals(20, stats.getMax());
        List<Integer> expectedList = Arrays.asList(13, 4, 5, 2, 20, 1, 19);
        assertEquals(expectedList, yearsExp);

    }

    @Test
    public void compareArrays() {
        int[] yrs = {1, 2, 3};
        int[] expected = {1, 2, 3,};
        assertArrayEquals(expected, yrs);
    }

    @Test
    public void testGetMergedList() {
        // Test with normal list
        List<String> names = Arrays.asList("John", "Jane", "Bob");
        String result = Helper.getMergedList(names);
        assertEquals("John, Jane, Bob", result);
    }
}

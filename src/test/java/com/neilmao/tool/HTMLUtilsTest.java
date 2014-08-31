package com.neilmao.tool;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Created with IntelliJ IDEA.
 * User: neilmao
 * Date: 31/08/2014
 * Time: 1:23 AM
 */
public class HTMLUtilsTest {

    @Test
    public void testExtractBetween() {
        String text = "<title>I'm the title</title>";
        String startStr = "<title>";
        String endStr = "</title>";

        assertEquals("I'm the title", HTMLUtils.extractBetween(text, startStr, false, endStr, false));
        assertEquals(text, HTMLUtils.extractBetween(text, startStr, true, endStr, true));
        assertEquals("<title>I'm the title", HTMLUtils.extractBetween(text, startStr, true, endStr, false));
        assertEquals("I'm the title</title>", HTMLUtils.extractBetween(text, startStr, false, endStr, true));
    }


    @Test
    public void testSearchByPattern() throws Exception {
        String text = "aaabbbcccdddeee";
        String regex = "aab\\w+de";

        assertEquals("aabbbcccddde", HTMLUtils.searchByPattern(text, regex));
    }
}

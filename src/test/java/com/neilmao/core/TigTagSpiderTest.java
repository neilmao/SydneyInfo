package com.neilmao.core;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: neilmao
 * Date: 26/08/2014
 * Time: 11:43 PM
 */
public class TigTagSpiderTest {

    private TigTagSpider spider;

    @Before
    public void setup() {
        spider = new TigTagSpider();
    }


    @Test
    public void testLogin() throws Exception {

        assertTrue(spider.login());

        //System.setIn(System.in);
        //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //String code = System.console().readLine();

        //assertTrue(spider.typeVerificationCode(code));
    }
}

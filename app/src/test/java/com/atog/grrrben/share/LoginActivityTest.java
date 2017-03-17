package com.atog.grrrben.share;

import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Test LoginActivity
 */
public class LoginActivityTest {

    private LoginActivity la = new LoginActivity();

    @Test
    public void isEmailValid() throws Exception {
        assertThat(la.isEmailValid("name@email.com"), is(true));
    }

    @Test
    public void isPasswordValid() throws Exception {
        assertThat(la.isPasswordValid("12345678"), is(true));
    }

    @Test
    public void startNextActivity() throws Exception {
        // todo
        assertTrue(true);
    }

}
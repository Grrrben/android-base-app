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
        assertThat(la.isEmailValid("@nohandle.com"), is(false));
        assertThat(la.isEmailValid("nodomain@"), is(false));
        assertThat(la.isEmailValid("name@nodotcom"), is(false));
    }

    @Test
    public void isPasswordValid() throws Exception {
        assertThat(la.isPasswordValid("longenough"), is(true));
        assertThat(la.isPasswordValid("short"), is(false));
    }

    @Test
    public void startNextActivity() throws Exception {
        // todo
        assertTrue(true);
    }

}
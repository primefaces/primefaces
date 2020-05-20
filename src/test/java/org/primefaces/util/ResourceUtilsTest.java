package org.primefaces.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ResourceUtilsTest {

    @Test
    void appendCacheBuster_true() {
        String url ="http://primefaces.org";
        String result = ResourceUtils.appendCacheBuster(url, true);
        assertEquals("http://primefaces.org?pfdrid_c=true", result);

    }

    @Test
    void appendCacheBuster_false() {
        String url ="http://primefaces.org";
        String result = ResourceUtils.appendCacheBuster(url, false);
        assertTrue(result.matches("http:\\/\\/primefaces.org\\?pfdrid_c=false&uid=.*"));
    }

}

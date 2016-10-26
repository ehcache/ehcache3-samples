package org.terracotta.demo.service.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Henri Tremblay
 */
public class HttpUtilTest {

    @Test
    public void utf8Encode() throws Exception {
        String actual = HttpUtil.utf8Encode("L'avoir & l'été");
        assertThat(actual).isEqualTo("L%27avoir+%26+l%27%C3%A9t%C3%A9");
    }

}

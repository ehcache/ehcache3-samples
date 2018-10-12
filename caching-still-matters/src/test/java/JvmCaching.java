import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JvmCaching {

  @Test
  public void testCache() {

    assertTrue(Integer.valueOf(512) == Integer.valueOf(512));

  }
}

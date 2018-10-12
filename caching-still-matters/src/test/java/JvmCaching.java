import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Demonstrate that the JDK is storing integer from -127 to
 */
public class JvmCaching {

  @Test
  public void testCache() {
    for (int i = -140; i < 150 ; i++) {
      boolean cached = Integer.valueOf(i) == Integer.valueOf(i);
      System.out.printf("Boxed %-4s cached? %s%n", i, (cached ? "YES" : "No :-("));
    }
  }

}
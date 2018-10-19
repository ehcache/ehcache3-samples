
/**
 * Demonstrate that the JDK is caching the boxed form of the most frequently used integers.
 */
public class JvmCaching {

  public static void main(String[] args) {
    assert Integer.valueOf(512) == Integer.valueOf(512);

//    for (int i = -140; i < 150 ; i++) {
//      boolean cached = Integer.valueOf(i) == Integer.valueOf(i);
//      System.out.printf("Boxed %-4s cached? %s%n", i, (cached ? "YES" : "No :-("));
//    }
  }

}
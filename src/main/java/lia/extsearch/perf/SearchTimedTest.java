package lia.extsearch.perf;

import com.clarkware.junitperf.TimedTest;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class SearchTimedTest {

  public static Test suite() {

    int maxTimeInMillis = 100;

    //Test test = new SearchTest("testSearchByTimestamp");
    Test test = new SearchTest("testSearchByDay");

    TestSuite suite = new TestSuite();
    suite.addTest(test); // runs first to warm up the cache
    suite.addTest(new TimedTest(test, maxTimeInMillis));

    return suite;

    //
    // Use this to ensure that the index is
    // created before the performance test.
    //
    //return makeTestFixture(suite);
  }

  public static Test makeTestFixture(Test test) {

    TestSetup oneTimer = new TestSetup(test) {

      public void setUp() throws Exception {
        IndexBuilder builder = new IndexBuilder();
        builder.buildIndex(1000);
      }
    };

    return oneTimer;
  }

  public static void main(String args[]) {
    junit.textui.TestRunner.run(suite());
  }
}

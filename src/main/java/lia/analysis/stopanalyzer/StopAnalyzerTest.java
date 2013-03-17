package lia.analysis.stopanalyzer;

import junit.framework.TestCase;
import lia.analysis.AnalyzerUtils;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.Token;
import java.io.IOException;

public class StopAnalyzerTest extends TestCase {
  private StopAnalyzer stopAnalyzer;

  public void setUp() {
    stopAnalyzer = new StopAnalyzer();
  }

  public void testHoles() throws Exception {
    String[] expected = { "one", "enough"};

    AnalyzerUtils.assertTokensEqual(tokensFrom("one is not enough"),
                                    expected);
    AnalyzerUtils.assertTokensEqual(tokensFrom("one is enough"),
                                    expected);
    AnalyzerUtils.assertTokensEqual(tokensFrom("one enough"),
                                    expected);
    AnalyzerUtils.assertTokensEqual(tokensFrom("one but not enough"),
                                    expected);
  }

  private Token[] tokensFrom(String text) throws IOException {
    return AnalyzerUtils.tokensFromAnalysis(stopAnalyzer,
                                            text);
  }


}

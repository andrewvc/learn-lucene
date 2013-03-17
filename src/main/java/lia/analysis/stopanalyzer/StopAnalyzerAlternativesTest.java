package lia.analysis.stopanalyzer;

import junit.framework.TestCase;
import org.apache.lucene.analysis.Token;
import lia.analysis.stopanalyzer.StopAnalyzerFlawed;
import lia.analysis.stopanalyzer.StopAnalyzer2;
import lia.analysis.AnalyzerUtils;

public class StopAnalyzerAlternativesTest extends TestCase {
  public void testStopAnalyzer2() throws Exception {
    Token[] tokens =
      AnalyzerUtils.tokensFromAnalysis(
        new StopAnalyzer2(), "The quick brown...");

      AnalyzerUtils.assertTokensEqual(tokens,
                                new String[] {"quick", "brown"});
  }

  public void testStopAnalyzerFlawed() throws Exception {
    Token[] tokens =
      AnalyzerUtils.tokensFromAnalysis(
        new StopAnalyzerFlawed(), "The quick brown...");

    assertEquals("the", tokens[0].termText());
  }

  /**
   * Illustrates that "the" is not removed, although it is lowercased
   */
  public static void main(String[] args) throws Exception {
    AnalyzerUtils.displayTokens(
      new StopAnalyzerFlawed(), "The quick brown...");
  }
}

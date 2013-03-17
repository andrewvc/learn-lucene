package lia.analysis.synonym;

import lia.analysis.AnalyzerUtils;
import java.io.IOException;
import java.io.File;

public class SynonymAnalyzerViewer {
  public static void main(String[] args) throws IOException {
    SynonymEngine engine = new WordNetSynonymEngine(new File(args[0]));
      //new MockSynonymEngine();

    AnalyzerUtils.displayTokensWithPositions(
      new SynonymAnalyzer(engine),
      "The quick brown fox jumps over the lazy dogs");

    AnalyzerUtils.displayTokensWithPositions(
      new SynonymAnalyzer(engine),
      "\"Oh, we get both kinds - country AND western!\" - B.B.");
  }
}

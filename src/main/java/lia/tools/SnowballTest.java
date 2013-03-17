package lia.tools;

import junit.framework.TestCase;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;

import java.io.StringReader;

public class SnowballTest extends TestCase {
  public void testEnglish() throws Exception {
    Analyzer analyzer = new SnowballAnalyzer("English");

    assertAnalyzesTo(analyzer,
        "stemming algorithms", new String[] {"stem", "algorithm"});
  }

  public void testSpanish() throws Exception {
    Analyzer analyzer = new SnowballAnalyzer("Spanish");

    assertAnalyzesTo(analyzer,
        "algoritmos", new String[] {"algoritm"});
  }

  public void assertAnalyzesTo(Analyzer analyzer, String input,
                               String[] output) throws Exception {
    TokenStream stream =
        analyzer.tokenStream("field", new StringReader(input));

    for (int i=0; i<output.length; i++) {
      Token token = stream.next();
      assertNotNull(token);
      assertEquals(output[i], token.termText());
    }
    assertNull(stream.next());
    stream.close();
  }
}

package lia.analysis.codec;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import java.io.Reader;

public class MetaphoneInjectionAnalyzer extends Analyzer {
  public TokenStream tokenStream(String fieldName, Reader reader) {
    // lowercasing is added, whereas the replacement analyzer does not
    return new MetaphoneInjectionFilter(new LowerCaseTokenizer(reader));
  }
}

package lia.analysis.keyword;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import java.io.IOException;
import java.io.Reader;

/**
 * "Tokenizes" the entire stream as a single token.
 */
public class KeywordAnalyzer extends Analyzer {
  public TokenStream tokenStream(String fieldName,
                                 final Reader reader) {
    return new TokenStream() {
      private boolean done;
      private final char[] buffer = new char[1024];
      public Token next() throws IOException {
        if (!done) {
          done = true;
          StringBuffer buffer = new StringBuffer();
          int length = 0;
          while (true) {
            length = reader.read(this.buffer);
            if (length == -1) break;

            buffer.append(this.buffer, 0, length);
          }
          String text = buffer.toString();
          return new Token(text, 0, text.length());
        }
        return null;
      }
    };
  }
}

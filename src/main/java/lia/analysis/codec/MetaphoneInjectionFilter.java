package lia.analysis.codec;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.EncoderException;
import java.io.IOException;

/**
 * Remove??  We don't show this anymore since SynonymAnalyzer
 * demonstrates placing more than one token in a position
 */
public class MetaphoneInjectionFilter extends TokenFilter {
  public static String METAPHONE = "METAPHONE";

  private Metaphone metaphoner = new Metaphone();
  private Token save;

  public MetaphoneInjectionFilter(TokenStream input) {
    super(input);
  }

  public Token next() throws IOException {

    // emit saved token, if available
    if (save != null) {
      Token temp = save;
      save = null;
      return temp;
    }

    // pull next token from stream
    Token t = input.next();

    if (t == null) return null;   // all done

    // create metaphone, save until next request
    if (save == null) {
      String value = t.termText();

      try {
        value = metaphoner.encode(t.termText());
      } catch (EncoderException ignored) {
        // ignored
      }

      save = new Token(value, t.startOffset(),
                t.endOffset(), METAPHONE);
      save.setPositionIncrement(0);
    }

    return t;

  }
}

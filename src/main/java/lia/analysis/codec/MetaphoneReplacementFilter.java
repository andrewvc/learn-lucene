package lia.analysis.codec;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

import java.io.IOException;

public class MetaphoneReplacementFilter extends TokenFilter {
  public static final String METAPHONE = "METAPHONE";

  private StringEncoder metaphoner = new Metaphone();

  public MetaphoneReplacementFilter(TokenStream input) {
    super(input);
  }

  public Token next() throws IOException {
    // pull next token from stream
    Token t = input.next();

    if (t == null) return null;   // all done

    try {
      return new Token(metaphoner.encode(t.termText()),
                       t.startOffset(),
                       t.endOffset(),
                       METAPHONE);
    } catch (EncoderException e) {
      // if cannot encode, simply return original token
      return t;
    }

  }
}

package lia.analysis.synonym;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Token;
import java.io.IOException;
import java.util.Stack;

public class SynonymFilter extends TokenFilter {
  public static final String TOKEN_TYPE_SYNONYM = "SYNONYM";

  private Stack synonymStack;
  private SynonymEngine engine;

  public SynonymFilter(TokenStream in, SynonymEngine engine) {
    super(in);
    synonymStack = new Stack();
    this.engine = engine;
  }

  public Token next() throws IOException {
    if (synonymStack.size() > 0) {
      return (Token) synonymStack.pop();
    }

    Token token = input.next();
    if (token == null) {
      return null;
    }

    addAliasesToStack(token);

    return token;
  }

  private void addAliasesToStack(Token token) throws IOException {
    String[] synonyms = engine.getSynonyms(token.termText());

    if (synonyms == null) return;

    for (int i = 0; i < synonyms.length; i++) {
      Token synToken = new Token(synonyms[i],
                                 token.startOffset(),
                                 token.endOffset(),
                                 TOKEN_TYPE_SYNONYM);
      synToken.setPositionIncrement(0);

      synonymStack.push(synToken);
    }
  }
}

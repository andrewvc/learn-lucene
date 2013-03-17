package lia.analysis.nutch;

import net.nutch.analysis.NutchDocumentAnalyzer;
import net.nutch.searcher.QueryTranslator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class NutchExample {
  public static void main(String[] args) throws IOException {
    NutchDocumentAnalyzer analyzer = new NutchDocumentAnalyzer();
    displayTokensWithDetails(analyzer, "The quick brown fox...");

    net.nutch.searcher.Query nutchQuery =
        net.nutch.searcher.Query.parse("\"the quick brown\"");
    Query query = QueryTranslator.translate(nutchQuery);
    System.out.println("query = " + query);
  }

  /**
   * Copy of AnalyzerUtils.displayTokensWithPositions, except
   * uses the "content" field instead of "contents".  Nutch
   * demands "content".
   */
  private static void displayTokensWithDetails(Analyzer analyzer,
                                 String text) throws IOException {
    Token[] tokens = tokensFromAnalysis(analyzer, text);

    int position = 0;

    for (int i = 0; i < tokens.length; i++) {
      Token token = tokens[i];

      int increment = token.getPositionIncrement();

      if (increment > 0) {
        position = position + increment;
        System.out.println();
        System.out.print(position + ": ");
      }

      System.out.print("[" + token.termText() +
          ":" + token.type() + "] ");
    }
    System.out.println();
  }

  /**
   * Copy of AnalyzerUtils.tokensFromAnalysis, except
   * uses the "content" field instead of "contents".  Nutch
   * demands "content".
   */
  private static Token[] tokensFromAnalysis(Analyzer analyzer,
                               String text) throws IOException {
    TokenStream stream =
        analyzer.tokenStream("content", new StringReader(text));
    ArrayList tokenList = new ArrayList();
    while (true) {
      Token token = stream.next();
      if (token == null) break;

      tokenList.add(token);
    }

    return (Token[]) tokenList.toArray(new Token[0]);
  }

}

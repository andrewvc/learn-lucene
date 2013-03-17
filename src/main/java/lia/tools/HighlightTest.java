package lia.tools;

import lia.common.LiaTestCase;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;

import java.io.StringReader;

public class HighlightTest extends LiaTestCase {
  public void testHighlighting() throws Exception {
    String text = "The quick brown fox jumps over the lazy dog";

    TermQuery query = new TermQuery(new Term("field", "fox"));
    Scorer scorer = new QueryScorer(query);
    Highlighter highlighter = new Highlighter(scorer);

    TokenStream tokenStream =
        new SimpleAnalyzer().tokenStream("field",
            new StringReader(text));

    assertEquals("The quick brown <B>fox</B> jumps over the lazy dog",
        highlighter.getBestFragment(tokenStream, text));
  }

  public void testHits() throws Exception {
    IndexSearcher searcher = new IndexSearcher(directory);

    TermQuery query = new TermQuery(new Term("title", "action"));
    Hits hits = searcher.search(query);

    QueryScorer scorer = new QueryScorer(query);
    Highlighter highlighter = new Highlighter(scorer);

    for (int i = 0; i < hits.length(); i++) {
      String title = hits.doc(i).get("title");

      TokenStream stream =
          new SimpleAnalyzer().tokenStream("title",
              new StringReader(title));
      String fragment =
          highlighter.getBestFragment(stream, title);

      System.out.println(fragment);
    }
  }

}

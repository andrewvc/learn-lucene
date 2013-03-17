package lia.analysis.synonym;

import junit.framework.TestCase;
import lia.analysis.AnalyzerUtils;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.queryParser.QueryParser;

public class SynonymAnalyzerTest extends TestCase {
  private RAMDirectory directory;
  private IndexSearcher searcher;
  private static SynonymAnalyzer synonymAnalyzer =
                      new SynonymAnalyzer(new MockSynonymEngine());

  public void setUp() throws Exception {
    directory = new RAMDirectory();

    IndexWriter writer = new IndexWriter(directory,
                                         synonymAnalyzer,
                                         true);
    Document doc = new Document();
    doc.add(Field.Text("content",
                   "The quick brown fox jumps over the lazy dogs"));
    writer.addDocument(doc);
    writer.close();

    searcher = new IndexSearcher(directory);
  }

  public void tearDown() throws Exception {
    searcher.close();
  }

  public void testJumps() throws Exception {
    Token[] tokens =
     AnalyzerUtils.tokensFromAnalysis(synonymAnalyzer, "jumps");

    AnalyzerUtils.assertTokensEqual(tokens,
                           new String[] {"jumps", "hops", "leaps"});

    // ensure synonyms are in the same position as the original
    assertEquals("jumps", 1, tokens[0].getPositionIncrement());
    assertEquals("hops", 0, tokens[1].getPositionIncrement());
    assertEquals("leaps", 0, tokens[2].getPositionIncrement());
  }

  public void testSearchByAPI() throws Exception {

    TermQuery tq = new TermQuery(new Term("content", "hops"));
    Hits hits = searcher.search(tq);
    assertEquals(1, hits.length());

    PhraseQuery pq = new PhraseQuery();
    pq.add(new Term("content", "fox"));
    pq.add(new Term("content", "hops"));
    hits = searcher.search(pq);
    assertEquals(1, hits.length());
  }

  public void testWithQueryParser() throws Exception {
    Query query = QueryParser.parse("\"fox jumps\"",
                                    "content",
                                    synonymAnalyzer);
    Hits hits = searcher.search(query);
    assertEquals("!!!! what?!", 0, hits.length());

    query = QueryParser.parse("\"fox jumps\"",
                              "content",
                              new StandardAnalyzer());
    hits = searcher.search(query);
    assertEquals("*whew*", 1, hits.length());
  }

  public static void main(String[] args) throws Exception {
    Query query = QueryParser.parse("\"fox jumps\"",
                     "content",
                     synonymAnalyzer);

    System.out.println("\"fox jumps\" parses to " +
                                         query.toString("content"));

    System.out.println("From AnalyzerUtils.tokensFromAnalysis: ");
    AnalyzerUtils.displayTokens(synonymAnalyzer,
                                     "\"fox jumps\"");
  }
}

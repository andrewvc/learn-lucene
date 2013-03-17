package lia.searching;

import lia.common.LiaTestCase;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class BasicSearchingTest extends LiaTestCase {

  public void testTerm() throws Exception {
    IndexSearcher searcher = new IndexSearcher(directory);
    Term t = new Term("subject", "ant");
    Query query = new TermQuery(t);
    Hits hits = searcher.search(query);
    assertEquals("JDwA", 1, hits.length());

    t = new Term("subject", "junit");
    hits = searcher.search(new TermQuery(t));
    assertEquals(2, hits.length());

    searcher.close();
  }

  public void testKeyword() throws Exception {
    IndexSearcher searcher = new IndexSearcher(directory);
    Term t = new Term("isbn", "1930110995");
    Query query = new TermQuery(t);
    Hits hits = searcher.search(query);
    assertEquals("JUnit in Action", 1, hits.length());
  }

  public void testQueryParser() throws Exception {
    IndexSearcher searcher = new IndexSearcher(directory);

    Query query = QueryParser.parse("+JUNIT +ANT -MOCK",
                                    "contents",
                                    new SimpleAnalyzer());
    Hits hits = searcher.search(query);
    assertEquals(1, hits.length());
    Document d = hits.doc(0);
    assertEquals("Java Development with Ant", d.get("title"));

    query = QueryParser.parse("mock OR junit",
                              "contents",
                              new SimpleAnalyzer());
    hits = searcher.search(query);
    assertEquals("JDwA and JIA", 2, hits.length());
  }
}

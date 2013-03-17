package lia.searching;

import lia.common.LiaTestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;

import java.util.Locale;

public class QueryParserTest extends LiaTestCase {
  private Analyzer analyzer;
  private IndexSearcher searcher;

  protected void setUp() throws Exception {
    super.setUp();
    analyzer = new WhitespaceAnalyzer();
    searcher = new IndexSearcher(directory);
  }

  public void testToString() throws Exception {
    BooleanQuery query = new BooleanQuery();
    query.add(new FuzzyQuery(new Term("field", "kountry")),
        true, false);
    query.add(new TermQuery(new Term("title", "western")),
        false, false);
    assertEquals("both kinds", "+kountry~0.5 title:western",
        query.toString("field"));
  }

  public void testPrefixQuery() throws Exception {
    QueryParser parser = new QueryParser("category", new StandardAnalyzer());
    parser.setLowercaseWildcardTerms(false);
    System.out.println(parser.parse("/Computers/technology*").toString("category"));
  }

  public void testGrouping() throws Exception {
    Query query = QueryParser.parse(
        "(agile OR extreme) AND methodology",
        "subject",
        analyzer);
    Hits hits = searcher.search(query);

    assertHitsIncludeTitle(hits, "Extreme Programming Explained");
    assertHitsIncludeTitle(hits, "The Pragmatic Programmer");
  }

  public void testRangeQuery() throws Exception {
    Query query = QueryParser.parse("pubmonth:[200401 TO 200412]", "subject", analyzer);

    assertTrue(query instanceof RangeQuery);

    Hits hits = searcher.search(query);
    assertHitsIncludeTitle(hits, "Lucene in Action");

    query = QueryParser.parse("{200201 TO 200208}", "pubmonth", analyzer);

    hits = searcher.search(query);
    assertEquals("JDwA in 200208", 0, hits.length());
  }
  
  public void testDateRangeQuery() throws Exception {
    String expression = "modified:[1/1/04 TO 12/31/04]";
    QueryParser parser = new QueryParser("subject", analyzer);
    parser.setLocale(Locale.US);
    Query query = parser.parse(expression);

    Hits hits = searcher.search(query);
    assertTrue(hits.length() > 0);
    System.out.println(expression + " parsed to " + query);
  }

  public void testSlop() throws Exception {
    Query q = QueryParser.parse("\"exact phrase\"", "field", analyzer);
    assertEquals("zero slop",
        "\"exact phrase\"", q.toString("field"));

    QueryParser qp = new QueryParser("field", analyzer);
    qp.setPhraseSlop(5);
    q = qp.parse("\"sloppy phrase\"");
    assertEquals("sloppy, implicitly",
        "\"sloppy phrase\"~5", q.toString("field"));
  }

  public void testPhraseQuery() throws Exception {
    Query q = QueryParser.parse("\"This is Some Phrase*\"",
        "field", new StandardAnalyzer());
    assertEquals("analyzed",
        "\"some phrase\"", q.toString("field"));

    q = QueryParser.parse("\"term\"", "field", analyzer);
    assertTrue("reduced to TermQuery", q instanceof TermQuery);
  }

  public void testLowercasing() throws Exception {
    Query q = QueryParser.parse("PrefixQuery*", "field", analyzer);
    assertEquals("lowercased",
        "prefixquery*", q.toString("field"));

    QueryParser qp = new QueryParser("field", analyzer);
    qp.setLowercaseWildcardTerms(false);
    q = qp.parse("PrefixQuery*");
    assertEquals("not lowercased",
        "PrefixQuery*", q.toString("field"));
  }

  public void testWildcard() {
    try {
      QueryParser.parse("*xyz", "field", analyzer);
      fail("Leading wildcard character should not be allowed");
    } catch (ParseException expected) {
      assertTrue(true);
    }
  }

  public void testBoost() throws Exception {
    Query q = QueryParser.parse("term^2", "field", analyzer);
    assertEquals("term^2.0", q.toString("field"));
  }


  public void testParseException() {
    try {
      QueryParser.parse("^&#", "contents", analyzer);
    } catch (ParseException expected) {
      // expression is invalid, as expected
      assertTrue(true);
      return;
    }

    fail("ParseException expected, but not thrown");
  }

//  public void testStopWord() throws ParseException {
//    Query q = QueryParser.parse("the AND drag", "field",
//        new StopAnalyzer());
//    //  QueryParser fails on the previous line - this is a known 
//    //  issue
//  }
}

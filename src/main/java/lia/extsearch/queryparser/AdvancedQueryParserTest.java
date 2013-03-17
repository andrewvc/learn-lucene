package lia.extsearch.queryparser;

import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class AdvancedQueryParserTest extends TestCase {
  private Analyzer analyzer;
  private RAMDirectory directory;

  protected void setUp() throws Exception {
    super.setUp();
    analyzer = new WhitespaceAnalyzer();

    directory = new RAMDirectory();
    IndexWriter writer = new IndexWriter(directory, analyzer,
        true);
    for (int i = 1; i <= 500; i++) {
      Document doc = new Document();
      doc.add(Field.Keyword("id", NumberUtils.pad(i)));
      writer.addDocument(doc);
    }
    writer.close();
  }

  public void testCustomQueryParser() {
    CustomQueryParser parser =
        new CustomQueryParser("field", analyzer);
    try {
      parser.parse("a?t");
      fail("Wildcard queries should not be allowed");
    } catch (ParseException expected) {
      // expected
      assertTrue(true);
    }

    try {
      parser.parse("xunit~");
      fail("Fuzzy queries should not be allowed");
    } catch (ParseException expected) {
      // expected
      assertTrue(true);
    }
  }

  public void testIdRangeQuery() throws Exception {
    CustomQueryParser parser =
        new CustomQueryParser("field", analyzer);

    Query query = parser.parse("id:[37 TO 346]");

    assertEquals("padded", "id:[0000000037 TO 0000000346]",
                           query.toString("field"));

    IndexSearcher searcher = new IndexSearcher(directory);
    Hits hits = searcher.search(query);

    assertEquals(310, hits.length());

    System.out.println(parser.parse("special:[term TO *]"));
    System.out.println(parser.parse("special:[* TO term]"));
  }

  public void testPhraseQuery() throws Exception {
    CustomQueryParser parser =
        new CustomQueryParser("field", analyzer);

    Query query = parser.parse("singleTerm");
    assertTrue("TermQuery", query instanceof TermQuery);

    query = parser.parse("\"a phrase\"");
    assertTrue("SpanNearQuery", query instanceof SpanNearQuery);
  }


}

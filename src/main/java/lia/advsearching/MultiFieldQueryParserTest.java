package lia.advsearching;

import lia.common.LiaTestCase;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

public class MultiFieldQueryParserTest extends LiaTestCase {
  public void testDefaultOperator() throws Exception {
    Query query = MultiFieldQueryParser.parse("development",
        new String[]{"title", "subject"},
        new SimpleAnalyzer());

    IndexSearcher searcher = new IndexSearcher(directory);
    Hits hits = searcher.search(query);

    assertHitsIncludeTitle(hits, "Java Development with Ant");

    // has "development" in the subject field
    assertHitsIncludeTitle(hits, "Extreme Programming Explained");
  }

  public void testSpecifiedOperator() throws Exception {
    Query query = MultiFieldQueryParser.parse("development",
        new String[]{"title", "subject"},
        new int[]{MultiFieldQueryParser.REQUIRED_FIELD,
                  MultiFieldQueryParser.REQUIRED_FIELD},
        new SimpleAnalyzer());

    IndexSearcher searcher = new IndexSearcher(directory);
    Hits hits = searcher.search(query);

    assertHitsIncludeTitle(hits, "Java Development with Ant");
    assertEquals("one and only one", 1, hits.length());
  }

}

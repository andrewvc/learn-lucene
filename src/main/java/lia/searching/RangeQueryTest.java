package lia.searching;

import lia.common.LiaTestCase;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.RangeQuery;

public class RangeQueryTest extends LiaTestCase {
  private Term begin, end;

  protected void setUp() throws Exception {
    begin = new Term("pubmonth","198805");

    // pub date of TTC was October 1988
    end = new Term("pubmonth","198810");

    super.setUp();
  }

  public void testInclusive() throws Exception {
    RangeQuery query = new RangeQuery(begin, end, true);
    IndexSearcher searcher = new IndexSearcher(directory);

    Hits hits = searcher.search(query);
    assertEquals("tao", 1, hits.length());
  }

  public void testExclusive() throws Exception {
    RangeQuery query = new RangeQuery(begin, end, false);
    IndexSearcher searcher = new IndexSearcher(directory);

    Hits hits = searcher.search(query);
    assertEquals("there is no tao", 0, hits.length());
  }

}

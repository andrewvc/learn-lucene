package lia.extsearch.hitcollector;

import lia.common.LiaTestCase;
import lia.extsearch.hitcollector.BookLinkCollector;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Hits;

import java.util.Map;

public class HitCollectorTest extends LiaTestCase {

  public void testCollecting() throws Exception {
    TermQuery query = new TermQuery(new Term("contents", "junit"));
    IndexSearcher searcher = new IndexSearcher(directory);

    BookLinkCollector collector = new BookLinkCollector(searcher);
    searcher.search(query, collector);

    Map linkMap = collector.getLinks();
    assertEquals("Java Development with Ant",
                 linkMap.get("http://www.manning.com/antbook"));;

    Hits hits = searcher.search(query);
    dumpHits(hits);

    searcher.close();
  }
}

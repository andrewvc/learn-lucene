package lia.analysis.i18n;

import lia.common.LiaTestCase;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;

public class ChineseTest extends LiaTestCase {
  public void testChinese() throws Exception {
    IndexSearcher searcher = new IndexSearcher(directory);
    Hits hits = searcher.search(
        new TermQuery(new Term("contents", "道")));
    assertEquals("tao", 1, hits.length());
  }
}

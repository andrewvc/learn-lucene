package lia.searching;

import lia.common.LiaTestCase;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;

public class BooleanQueryTest extends LiaTestCase {
  public void testAnd() throws Exception {
    TermQuery searchingBooks =
                   new TermQuery(new Term("subject","search"));

    RangeQuery currentBooks =
                    new RangeQuery(new Term("pubmonth","200401"),
                                   new Term("pubmonth","200412"),
                                   true);

    BooleanQuery currentSearchingBooks = new BooleanQuery();
    currentSearchingBooks.add(searchingBooks, true, false);
    currentSearchingBooks.add(currentBooks, true, false);

    IndexSearcher searcher = new IndexSearcher(directory);
    Hits hits = searcher.search(currentSearchingBooks);

    assertHitsIncludeTitle(hits, "Lucene in Action");
  }

  public void testOr() throws Exception {
    TermQuery methodologyBooks = new TermQuery(
        new Term("category",
            "/technology/computers/programming/methodology"));

    TermQuery easternPhilosophyBooks = new TermQuery(
        new Term("category",
            "/philosophy/eastern"));

    BooleanQuery enlightenmentBooks = new BooleanQuery();
    enlightenmentBooks.add(methodologyBooks, false, false);
    enlightenmentBooks.add(easternPhilosophyBooks, false, false);

    IndexSearcher searcher = new IndexSearcher(directory);
    Hits hits = searcher.search(enlightenmentBooks);
    System.out.println("or = " + enlightenmentBooks);

    assertHitsIncludeTitle(hits, "Extreme Programming Explained");
    assertHitsIncludeTitle(hits,
                               "Tao Te Ching \u9053\u5FB7\u7D93");
  }
}

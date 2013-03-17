package lia.searching;

import lia.common.LiaTestCase;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;

public class PrefixQueryTest extends LiaTestCase {
  public void testPrefix() throws Exception {
    IndexSearcher searcher = new IndexSearcher(directory);

    // search for programming books, including subcategories
    Term term = new Term("category",
                         "/technology/computers/programming");
    PrefixQuery query = new PrefixQuery(term);

    Hits hits = searcher.search(query);
    int programmingAndBelow = hits.length();

    // only programming books, not subcategories
    hits = searcher.search(new TermQuery(term));
    int justProgramming = hits.length();

    assertTrue(programmingAndBelow > justProgramming);
  }
}

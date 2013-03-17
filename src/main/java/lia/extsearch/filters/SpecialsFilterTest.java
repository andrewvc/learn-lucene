package lia.extsearch.filters;

import lia.common.LiaTestCase;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.Term;

public class SpecialsFilterTest extends LiaTestCase {
  private Query allBooks;
  private IndexSearcher searcher;

  protected void setUp() throws Exception {
    super.setUp();

    allBooks = new RangeQuery(new Term("pubmonth","190001"),
                                   new Term("pubmonth", "200512"),
                                   true);
    searcher = new IndexSearcher(directory);
  }

  public void testCustomFilter() throws Exception {
    String[] isbns = new String[] {"0060812451", "0465026567"};

    SpecialsAccessor accessor = new MockSpecialsAccessor(isbns);
    Filter filter = new SpecialsFilter(accessor);
    Hits hits = searcher.search(allBooks, filter);
    assertEquals("the specials", isbns.length, hits.length());
  }

  public void testFilteredQuery() throws Exception {
    String[] isbns = new String[] {"0854402624"};  // Steiner

    SpecialsAccessor accessor = new MockSpecialsAccessor(isbns);
    Filter filter = new SpecialsFilter(accessor);

    WildcardQuery educationBooks =
        new WildcardQuery(new Term("category", "*education*"));
    FilteredQuery edBooksOnSpecial =
        new FilteredQuery(educationBooks, filter);

    TermQuery logoBooks =
        new TermQuery(new Term("subject", "logo"));


    BooleanQuery logoOrEdBooks = new BooleanQuery();
    logoOrEdBooks.add(logoBooks, false, false);
    logoOrEdBooks.add(edBooksOnSpecial, false, false);

    Hits hits = searcher.search(logoOrEdBooks);
    System.out.println(logoOrEdBooks.toString());
    assertEquals("Papert and Steiner", 2, hits.length());
  }
}

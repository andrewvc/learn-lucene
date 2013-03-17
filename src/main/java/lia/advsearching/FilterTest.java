package lia.advsearching;

import lia.common.LiaTestCase;
import org.apache.lucene.document.DateField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.DateFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryFilter;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;

import java.util.Date;

public class FilterTest extends LiaTestCase {
  private Query allBooks;
  private IndexSearcher searcher;
  private int numAllBooks;
  private CachingWrapperFilter cachingFilter;

  protected void setUp() throws Exception {
    super.setUp();

    allBooks = new RangeQuery(new Term("pubmonth","190001"),
                                   new Term("pubmonth", "200512"),
                                   true);
    searcher = new IndexSearcher(directory);
    Hits hits = searcher.search(allBooks);
    numAllBooks = hits.length();
  }

  public void testDateFilter() throws Exception {
    Date jan1 = parseDate("2004-01-01");
    Date jan31 = parseDate("2004-01-31");
    Date dec31 = parseDate("2004-12-31");

    DateFilter filter = new DateFilter("modified", jan1, dec31);

    Hits hits = searcher.search(allBooks, filter);
    assertEquals("all modified in 2004",
        numAllBooks, hits.length());

    filter = new DateFilter("modified", jan1, jan31);
    hits = searcher.search(allBooks, filter);
    assertEquals("none modified in January",
        0, hits.length());
  }

  public void testQueryFilter() throws Exception {
    TermQuery categoryQuery =
       new TermQuery(new Term("category", "/philosophy/eastern"));

    Filter categoryFilter = new QueryFilter(categoryQuery);

    Hits hits = searcher.search(allBooks, categoryFilter);
    assertEquals("only tao te ching", 1, hits.length());
  }

  public void testFilterAlternative() throws Exception {
    TermQuery categoryQuery =
       new TermQuery(new Term("category", "/philosophy/eastern"));

    BooleanQuery constrainedQuery = new BooleanQuery();
    constrainedQuery.add(allBooks, true, false);
    constrainedQuery.add(categoryQuery, true, false);

    Hits hits = searcher.search(constrainedQuery);
    assertEquals("only tao te ching", 1, hits.length());
  }


  public void testQueryFilterWithRangeQuery() throws Exception {
    Date jan1 = parseDate("2004-01-01");
    Date dec31 = parseDate("2004-12-31");

    Term start = new Term("modified",
        DateField.dateToString(jan1));
    Term end = new Term("modified",
        DateField.dateToString(dec31));

    Query rangeQuery = new RangeQuery(start, end, true);

    Filter filter = new QueryFilter(rangeQuery);
    Hits hits = searcher.search(allBooks, filter);
    assertEquals("all of 'em", numAllBooks, hits.length());
  }

  public void testCachingWrapper() throws Exception {
    Date jan1 = parseDate("2004-01-01");
    Date dec31 = parseDate("2004-12-31");

    DateFilter dateFilter =
        new DateFilter("modified", jan1, dec31);

    cachingFilter =
        new CachingWrapperFilter(dateFilter);
    Hits hits = searcher.search(allBooks, cachingFilter);
    assertEquals("all of 'em", numAllBooks, hits.length());
  }
}

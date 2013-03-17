package lia.extsearch.perf;

import com.clarkware.profiler.Profiler;
import org.apache.lucene.document.DateField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class Search {

  private IndexBuilder index;

  public Search() throws Exception {
    index = new IndexBuilder();
  }

  public Hits searchByTimestamp(Date begin, Date end)
      throws Exception {
    Term beginTerm = new Term("last-modified",
        DateField.dateToString(begin));
    Term endTerm = new Term("last-modified",
        DateField.dateToString(end));

    Query query = new RangeQuery(beginTerm, endTerm, true);

    return newSearcher(
        index.byTimestampIndexDirName()).search(query);
  }

  public Hits searchByDay(String begin, String end)
      throws Exception {
    Term beginTerm = new Term("last-modified", begin);
    Term endTerm = new Term("last-modified", end);

    Query query = new RangeQuery(beginTerm, endTerm, true);

    return newSearcher(index.byDayIndexDirName()).search(query);
  }

  public static Date janOneTimestamp() {
    Calendar firstDay = GregorianCalendar.getInstance();
    firstDay.set(2004, 0, 01); // Jan = 0
    return firstDay.getTime();
  }

  public static Date todayTimestamp() {
    return GregorianCalendar.getInstance().getTime();
  }

  public static String today() {
    SimpleDateFormat dateFormat =
        (SimpleDateFormat) SimpleDateFormat.getDateInstance();
    dateFormat.applyPattern("yyyyMMdd");
    return dateFormat.format(todayTimestamp());
  }

  private IndexSearcher newSearcher(String indexDirName)
      throws IOException {
    Directory indexDirectory =
        FSDirectory.getDirectory(indexDirName, false);
    return new IndexSearcher(indexDirectory);
  }

  public static void main(String args[]) throws Exception {

    Search s = new Search();

    //
    // Cache because it makes Lucene feel good
    //
    Profiler.begin("searchByTimestamp: 1");
    s.searchByTimestamp(Search.janOneTimestamp(),
        Search.todayTimestamp());
    Profiler.end("searchByTimestamp: 1");
    Profiler.begin("searchByDay: 1");
    s.searchByDay("20040101", Search.today());
    Profiler.end("searchByDay: 1");

    //
    // Search by timestamp
    //
    Profiler.begin("searchByTimestamp: 2");
    Hits hits = s.searchByTimestamp(Search.janOneTimestamp(),
        Search.todayTimestamp());
    System.out.println(hits.length() + " hits by timestamp");
    Profiler.end("searchByTimestamp: 2");

    //
    // Searby by day
    //
    Profiler.begin("searchByDay: 2");
    hits = s.searchByDay("20040101", Search.today());
    System.out.println(hits.length() + " hits by day");
    Profiler.end("searchByDay: 2");

    System.out.println("");
    Profiler.print();
  }
}

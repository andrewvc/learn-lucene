package lia.advsearching;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.text.DecimalFormat;

public class SortingExample {
  private Directory directory;

  public SortingExample(Directory directory) {
    this.directory = directory;
  }

  public void displayHits(Query query, Sort sort)
      throws IOException {
    IndexSearcher searcher = new IndexSearcher(directory);

    Hits hits = searcher.search(query, sort);

    System.out.println("\nResults for: " +
        query.toString() + " sorted by " + sort);

    System.out.println(StringUtils.rightPad("Title", 30) +
        StringUtils.rightPad("pubmonth", 10) +
        StringUtils.center("id", 4) +
        StringUtils.center("score", 15));

    DecimalFormat scoreFormatter = new DecimalFormat("0.######");
    for (int i = 0; i < hits.length(); i++) {
      Document doc = hits.doc(i);
      System.out.println(
          StringUtils.rightPad(
              StringUtils.abbreviate(doc.get("title"), 29), 30) +
          StringUtils.rightPad(doc.get("pubmonth"), 10) +
          StringUtils.center("" + hits.id(i), 4) +
          StringUtils.leftPad(
              scoreFormatter.format(hits.score(i)), 12));
      System.out.println("   " + doc.get("category"));
//      System.out.println(searcher.explain(query, hits.id(i)));
    }

    searcher.close();
  }

  public static void main(String[] args) throws Exception {
    Term earliest = new Term("pubmonth", "190001");
    Term latest = new Term("pubmonth", "201012");
    RangeQuery allBooks = new RangeQuery(earliest, latest, true);

    String indexDir = System.getProperty("index.dir");

    FSDirectory directory =
        FSDirectory.getDirectory(indexDir, false);
    SortingExample example = new SortingExample(directory);

    example.displayHits(allBooks, Sort.RELEVANCE);

    example.displayHits(allBooks, Sort.INDEXORDER);

    example.displayHits(allBooks, new Sort("category"));

    example.displayHits(allBooks, new Sort("pubmonth", true));

    example.displayHits(allBooks,
        new Sort(new SortField[]{
          new SortField("category"),
          SortField.FIELD_SCORE,
          new SortField("pubmonth", SortField.INT, true)
        }));


    example.displayHits(allBooks, new Sort(new SortField[] {SortField.FIELD_SCORE, new SortField("category")}));
  }
}

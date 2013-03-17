package lia.extsearch.sorting;

import junit.framework.TestCase;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

import lia.extsearch.sorting.DistanceComparatorSource;

public class DistanceSortingTest extends TestCase {
  private RAMDirectory directory;
  private IndexSearcher searcher;
  private Query query;

  protected void setUp() throws Exception {
    directory = new RAMDirectory();
    IndexWriter writer =
        new IndexWriter(directory, new WhitespaceAnalyzer(), true);
    addPoint(writer, "El Charro", "restaurant", 1, 2);
    addPoint(writer, "Cafe Poca Cosa", "restaurant", 5, 9);
    addPoint(writer, "Los Betos", "restaurant", 9, 6);
    addPoint(writer, "Nico's Taco Shop", "restaurant", 3, 8);

    writer.close();

    searcher = new IndexSearcher(directory);

    query = new TermQuery(new Term("type", "restaurant"));
  }

  private void addPoint(IndexWriter writer,
                        String name, String type, int x, int y)
      throws IOException {
    Document doc = new Document();
    doc.add(Field.Keyword("name", name));
    doc.add(Field.Keyword("type", type));
    doc.add(Field.Keyword("location", x + "," + y));
    writer.addDocument(doc);
  }

  public void testNearestRestaurantToHome() throws Exception {
    Sort sort = new Sort(new SortField("location",
        new DistanceComparatorSource(0, 0)));

    Hits hits = searcher.search(query, sort);

    assertEquals("closest",
        "El Charro", hits.doc(0).get("name"));
    assertEquals("furthest",
        "Los Betos", hits.doc(3).get("name"));
  }

  public void testNeareastRestaurantToWork() throws Exception {
    Sort sort = new Sort(new SortField("location",
        new DistanceComparatorSource(10, 10)));

    TopFieldDocs docs = searcher.search(query, null, 3, sort);

    assertEquals(4, docs.totalHits);
    assertEquals(3, docs.scoreDocs.length);

    FieldDoc fieldDoc = (FieldDoc) docs.scoreDocs[0];

    assertEquals("(10,10) -> (9,6) = sqrt(17)",
        new Float(Math.sqrt(17)),
        fieldDoc.fields[0]);

    Document document = searcher.doc(fieldDoc.doc);
    assertEquals("Los Betos", document.get("name"));

    //dumpDocs(sort, docs);
  }


  private void dumpDocs(Sort sort, TopFieldDocs docs)
      throws IOException {
    System.out.println("Sorted by: " + sort);
    ScoreDoc[] scoreDocs = docs.scoreDocs;
    for (int i = 0; i < scoreDocs.length; i++) {
      FieldDoc fieldDoc = (FieldDoc) scoreDocs[i];
      Float distance = (Float) fieldDoc.fields[0];
      Document doc = searcher.doc(fieldDoc.doc);
      System.out.println("   " + doc.get("name") +
          " @ (" + doc.get("location") + ") -> " + distance);
    }
  }
}

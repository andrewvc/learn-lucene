package lia.indexing;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;

/**
 *
 */
public class DocumentUpdateTest extends BaseIndexingTestCase {

  public void testUpdate() throws IOException {

    assertEquals(1, getHitCount("city", "Amsterdam"));

    IndexReader reader = IndexReader.open(dir);
    reader.delete(new Term("city", "Amsterdam"));
    reader.close();

    IndexWriter writer = new IndexWriter(dir, getAnalyzer(),
      false);
    Document doc = new Document();
    doc.add(Field.Keyword("id", "1"));
    doc.add(Field.UnIndexed("country", "Russia"));
    doc.add(Field.UnStored("contents",
      "St. Petersburg has lots of bridges"));
    doc.add(Field.Text("city", "St. Petersburg"));
    writer.addDocument(doc);
    writer.optimize();
    writer.close();

    assertEquals(0, getHitCount("city", "Amsterdam"));
    assertEquals(1, getHitCount("city", "Petersburg"));
  }

  protected Analyzer getAnalyzer() {
    return new WhitespaceAnalyzer();
  }

  private int getHitCount(String fieldName, String searchString)
    throws IOException {
    IndexSearcher searcher = new IndexSearcher(dir);
    Term t = new Term(fieldName, searchString);
    Query query = new TermQuery(t);
    Hits hits = searcher.search(query);
    int hitCount = hits.length();
    searcher.close();
    return hitCount;
  }
}

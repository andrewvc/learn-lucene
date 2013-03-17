package lia.indexing;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;

import junit.framework.TestCase;
import java.io.IOException;

/**
 *
 */
public class FieldLengthTest extends TestCase {

  private Directory dir;
  private String[] keywords = {"1", "2"};
  private String[] unindexed = {"Netherlands", "Italy"};
  private String[] unstored = {"Amsterdam has lots of bridges",
                               "Venice has lots of canals"};
  private String[] text = {"Amsterdam", "Venice"};

  protected void setUp() throws IOException {
    String indexDir =
      System.getProperty("java.io.tmpdir", "tmp") +
      System.getProperty("file.separator") + "index-dir";
    dir = FSDirectory.getDirectory(indexDir, true);
  }

  public void testFieldSize() throws IOException {
    addDocuments(dir, 10);
    assertEquals(1, getHitCount("contents", "bridges"));

    addDocuments(dir, 1);
    assertEquals(0, getHitCount("contents", "bridges"));
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

  private void addDocuments(Directory dir, int maxFieldLength)
    throws IOException {
    IndexWriter writer = new IndexWriter(dir, new SimpleAnalyzer(),
      true);
    writer.maxFieldLength = maxFieldLength;
    for (int i = 0; i < keywords.length; i++) {
      Document doc = new Document();
      doc.add(Field.Keyword("id", keywords[i]));
      doc.add(Field.UnIndexed("country", unindexed[i]));
      doc.add(Field.UnStored("contents", unstored[i]));
      doc.add(Field.Text("city", text[i]));
      writer.addDocument(doc);
    }
    writer.optimize();
    writer.close();
  }
}

package lia.advsearching;

import junit.framework.TestCase;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhrasePrefixQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

public class PhrasePrefixQueryTest extends TestCase {
  private IndexSearcher searcher;

  protected void setUp() throws Exception {
    RAMDirectory directory = new RAMDirectory();
    IndexWriter writer = new IndexWriter(directory,
        new WhitespaceAnalyzer(), true);
    Document doc1 = new Document();
    doc1.add(Field.Text("field",
              "the quick brown fox jumped over the lazy dog"));
    writer.addDocument(doc1);
    Document doc2 = new Document();
    doc2.add(Field.Text("field",
              "the fast fox hopped over the hound"));
    writer.addDocument(doc2);
    writer.close();

    searcher = new IndexSearcher(directory);
  }

  public void testBasic() throws Exception {
    PhrasePrefixQuery query = new PhrasePrefixQuery();
    query.add(new Term[] {
      new Term("field", "quick"),
      new Term("field", "fast")
    });
    query.add(new Term("field", "fox"));
    System.out.println(query);

    Hits hits = searcher.search(query);
    assertEquals("fast fox match", 1, hits.length());

    query.setSlop(1);
    hits = searcher.search(query);
    assertEquals("both match", 2, hits.length());
  }

  public void testAgainstOR() throws Exception {
    PhraseQuery quickFox = new PhraseQuery();
    quickFox.setSlop(1);
    quickFox.add(new Term("field", "quick"));
    quickFox.add(new Term("field", "fox"));

    PhraseQuery fastFox = new PhraseQuery();
    fastFox.add(new Term("field", "fast"));
    fastFox.add(new Term("field", "fox"));

    BooleanQuery query = new BooleanQuery();
    query.add(quickFox, false, false);
    query.add(fastFox, false, false);
    Hits hits = searcher.search(query);
    assertEquals(2, hits.length());
  }


  private void debug(Hits hits) throws IOException {
    for (int i=0; i < hits.length(); i++) {
      Document doc = hits.doc(i);
      System.out.println(hits.score(i) + ": " + doc.get("field"));
    }

  }
}
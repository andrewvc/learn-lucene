package lia.indexing;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;

import java.io.IOException;

/**
 *
 */
public class DocumentDeleteTest extends BaseIndexingTestCase {

  public void testDeleteBeforeIndexMerge() throws IOException {
    IndexReader reader = IndexReader.open(dir);
    assertEquals(2, reader.maxDoc());
    assertEquals(2, reader.numDocs());
    reader.delete(1);

    assertTrue(reader.isDeleted(1));
    assertTrue(reader.hasDeletions());
    assertEquals(2, reader.maxDoc());
    assertEquals(1, reader.numDocs());

    reader.close();

    reader = IndexReader.open(dir);

    assertEquals(2, reader.maxDoc());
    assertEquals(1, reader.numDocs());

    reader.close();
  }

  public void testDeleteAfterIndexMerge() throws IOException {
    IndexReader reader = IndexReader.open(dir);
    assertEquals(2, reader.maxDoc());
    assertEquals(2, reader.numDocs());
    reader.delete(1);
    reader.close();

    IndexWriter writer = new IndexWriter(dir, getAnalyzer(),
      false);
    writer.optimize();
    writer.close();

    reader = IndexReader.open(dir);

    assertFalse(reader.isDeleted(1));
    assertFalse(reader.hasDeletions());
    assertEquals(1, reader.maxDoc());
    assertEquals(1, reader.numDocs());

    reader.close();
  }
}

package lia.indexing;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * Creates a file-based index, and then reads it into memory.
 */
public class FS2RAMDirectoryTest extends BaseIndexingTestCase {

  public void testSlurp() throws IOException {
    IndexReader fsDirReader = IndexReader.open(dir);
    assertEquals(keywords.length, fsDirReader.maxDoc());
    assertEquals(keywords.length, fsDirReader.numDocs());

    RAMDirectory ramDir = new RAMDirectory(dir);
    IndexReader ramDirReader = IndexReader.open(ramDir);
    assertEquals(fsDirReader.maxDoc(), ramDirReader.maxDoc());
    assertEquals(fsDirReader.numDocs(), ramDirReader.numDocs());

    fsDirReader.close();
    ramDir.close();
  }
}

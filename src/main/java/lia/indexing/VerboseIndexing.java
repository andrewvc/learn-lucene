package lia.indexing;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.SimpleAnalyzer;

import java.io.IOException;

/**
 *
 */
public class VerboseIndexing {

  private void index() throws IOException {
    String dirPath =
      System.getProperty("java.io.tmpdir", "tmp") +
      System.getProperty("file.separator") + "verbose-index";

    Directory dir = FSDirectory.getDirectory(dirPath, true);

    IndexWriter writer = new IndexWriter(dir, new SimpleAnalyzer(),
      true);

    writer.infoStream = System.out;

    for (int i = 0; i < 100; i++) {
      Document doc = new Document();
      doc.add(Field.Keyword("keyword", "goober"));
      writer.addDocument(doc);
    }
    writer.optimize();
    writer.close();
  }

  public static void main(String[] args) throws IOException {
    VerboseIndexing vi = new VerboseIndexing();
    vi.index();
  }
}

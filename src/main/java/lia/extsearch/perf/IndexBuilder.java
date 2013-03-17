package lia.extsearch.perf;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class IndexBuilder {

  private String byTimestampIndexDirName;
  private String byDayIndexDirName;

  public IndexBuilder() throws Exception {

    String indexDirName = System.getProperty("perf.temp.dir");
    if (indexDirName == null) {
      throw new RuntimeException("Property 'perf.temp.dir' not defined!");
    }

    byTimestampIndexDirName = indexDirName + File.separator + "timestamp";
    byDayIndexDirName = indexDirName + File.separator + "day";
  }

  public String byTimestampIndexDirName() {
    return byTimestampIndexDirName;
  }

  public String byDayIndexDirName() {
    return byDayIndexDirName;
  }

  public void buildIndex(int size) throws Exception {
    buildIndexByTimestamp(byTimestampIndexDirName(), size);
    buildIndexByDay(byDayIndexDirName(), size);
  }

  public void buildIndexByTimestamp(String dirName, int size)
      throws Exception {

    IndexWriter writer = newIndexWriter(dirName);

    Calendar timestamp = GregorianCalendar.getInstance();
    timestamp.set(Calendar.DATE,
        timestamp.get(Calendar.DATE) - 1);
    for (int i = 0; i < size; i++) {
      timestamp.set(Calendar.SECOND,
          timestamp.get(Calendar.SECOND) + 1);
      Date now = timestamp.getTime();
      Document document = new Document();
      document.add(Field.Keyword("last-modified", now));
      writer.addDocument(document);
    }

    writer.close();
  }

  public void buildIndexByDay(String dirName, int size)
      throws Exception {

    String today = Search.today();

    IndexWriter writer = newIndexWriter(dirName);

    for (int i = 0; i < size; i++) {
      Document document = new Document();
      document.add(Field.Keyword("last-modified", today));
      writer.addDocument(document);
    }

    writer.close();
  }

  private IndexWriter newIndexWriter(String dirName)
      throws IOException {
    Directory indexDirectory =
        FSDirectory.getDirectory(dirName, true);
    IndexWriter writer =
        new IndexWriter(indexDirectory,
            new StandardAnalyzer(), true);
    return writer;
  }

  public static void main(String args[]) throws Exception {

    if (args.length != 1) {
      System.out.println("Usage: IndexBuilder <size>");
      System.exit(0);
    }

    IndexBuilder builder = new IndexBuilder();
    builder.buildIndex(Integer.parseInt(args[0]));
  }
}

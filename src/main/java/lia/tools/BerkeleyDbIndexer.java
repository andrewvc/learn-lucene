package lia.tools;

import com.sleepycat.db.DbEnv;
import com.sleepycat.db.Db;
import com.sleepycat.db.DbException;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.db.DbDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class BerkeleyDbIndexer {
  public static void main(String[] args) throws IOException, DbException {
    if (args.length != 1) {
      System.err.println("Usage: BerkeleyDbIndexer <index dir>");
      System.exit(-1);
    }
    String indexDir = args[0];

    DbEnv env = new DbEnv(0);
    Db index = new Db(env, 0);
    Db blocks = new Db(env, 0);
    File dbHome = new File(indexDir);
    int flags = Db.DB_CREATE;

    if (dbHome.exists()) {
      File[] files = dbHome.listFiles();

      for (int i = 0; i < files.length; i++)
        if (files[i].getName().startsWith("__"))
          files[i].delete();
      dbHome.delete();
    }

    dbHome.mkdir();

    env.open(indexDir, Db.DB_INIT_MPOOL | flags, 0);
    index.open(null, "__index__", null, Db.DB_BTREE, flags, 0);
    blocks.open(null, "__blocks__", null, Db.DB_BTREE, flags, 0);

    DbDirectory directory = new DbDirectory(null, index, blocks, 0);
    IndexWriter writer = new IndexWriter(directory,
        new StandardAnalyzer(),
        true);

    Document doc = new Document();
    doc.add(Field.Text("contents", "The quick brown fox..."));
    writer.addDocument(doc);

    writer.optimize();
    writer.close();

    index.close(0);
    blocks.close(0);
    env.close(0);

    System.out.println("Indexing Complete");
  }
}

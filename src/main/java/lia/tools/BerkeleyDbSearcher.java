package lia.tools;

import com.sleepycat.db.Db;
import com.sleepycat.db.DbEnv;
import com.sleepycat.db.DbException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.db.DbDirectory;

import java.io.IOException;

public class BerkeleyDbSearcher {
  public static void main(String[] args) throws IOException, DbException {
    if (args.length != 1) {
      System.err.println("Usage: BerkeleyDbSearcher <index dir>");
      System.exit(-1);
    }
    String indexDir = args[0];

    DbEnv env = new DbEnv(0);
    Db index = new Db(env, 0);
    Db blocks = new Db(env, 0);

    env.open(indexDir, Db.DB_INIT_MPOOL, 0);
    index.open(null, "__index__", null, Db.DB_BTREE, 0, 0);
    blocks.open(null, "__blocks__", null, Db.DB_BTREE, 0, 0);

    DbDirectory directory = new DbDirectory(null, index, blocks, 0);

    IndexSearcher searcher = new IndexSearcher(directory);
    Hits hits = searcher.search(new TermQuery(new Term("contents", "fox")));
    System.out.println(hits.length() + " documents found");
    searcher.close();

    index.close(0);
    blocks.close(0);
    env.close(0);
  }
}

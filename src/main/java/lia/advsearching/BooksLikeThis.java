package lia.advsearching;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;

public class BooksLikeThis {

  public static void main(String[] args) throws IOException {
    String indexDir = System.getProperty("index.dir");

    FSDirectory directory =
        FSDirectory.getDirectory(indexDir, false);

    IndexReader reader = IndexReader.open(directory);
    int numDocs = reader.maxDoc();

    BooksLikeThis blt = new BooksLikeThis(reader);
    for (int i = 0; i < numDocs; i++) {
      System.out.println();
      Document doc = reader.document(i);
      System.out.println(doc.get("title"));

      Document[] docs = blt.docsLike(i, 10);
      if (docs.length == 0) {
        System.out.println("  None like this");
      }
      for (int j = 0; j < docs.length; j++) {
        Document likeThisDoc = docs[j];
        System.out.println("  -> " + likeThisDoc.get("title"));
      }
    }
  }

  private IndexReader reader;
  private IndexSearcher searcher;

  public BooksLikeThis(IndexReader reader) {
    this.reader = reader;
    searcher = new IndexSearcher(reader);
  }

  public Document[] docsLike(int id, int max) throws IOException {
    Document doc = reader.document(id);

    String[] authors = doc.getValues("author");
    BooleanQuery authorQuery = new BooleanQuery();
    for (int i = 0; i < authors.length; i++) {
      String author = authors[i];
      authorQuery.add(new TermQuery(new Term("author", author)),
          false, false);
    }
    authorQuery.setBoost(2.0f);

    TermFreqVector vector =
        reader.getTermFreqVector(id, "subject");

    BooleanQuery subjectQuery = new BooleanQuery();
    for (int j = 0; j < vector.size(); j++) {
      TermQuery tq = new TermQuery(
          new Term("subject", vector.getTerms()[j]));
      subjectQuery.add(tq, false, false);
    }

    BooleanQuery likeThisQuery = new BooleanQuery();
    likeThisQuery.add(authorQuery, false, false);
    likeThisQuery.add(subjectQuery, false, false);

    // exclude myself
    likeThisQuery.add(new TermQuery(
        new Term("isbn", doc.get("isbn"))), false, true);

    System.out.println("  Query: " +
        likeThisQuery.toString("contents"));
    Hits hits = searcher.search(likeThisQuery);
    int size = max;
    if (max > hits.length()) size = hits.length();

    Document[] docs = new Document[size];
    for (int i = 0; i < size; i++) {
      docs[i] = hits.doc(i);
    }

    return docs;
  }

}

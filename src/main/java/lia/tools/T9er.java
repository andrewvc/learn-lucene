package lia.tools;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;

import java.util.HashMap;

public class T9er {
  private static String[] keys = {         "2abc", "3def",
                                  "4ghi",  "5jkl", "6mno",
                                  "7pqrs", "8tuv", "9wxyz"};

  private static HashMap keyMap = new HashMap();

  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("Usage: T9er <WordNet index dir> <t9 index>");
    }

    for (int i = 0; i < keys.length; i++) {
      String key = keys[i];
      for (int j=1; j < key.length(); j++) {
        keyMap.put(new Character(key.charAt(j)), new Character(key.charAt(0)));
        System.out.println(new Character(key.charAt(j)) + " = " + new Character(key.charAt(0)));
      }

    }

    String indexDir = args[0];
    String t9dir = args[1];

    IndexReader reader = IndexReader.open(indexDir);

    int numDocs = reader.maxDoc();
    System.out.println("Processing " + numDocs + " words");

    IndexWriter writer = new IndexWriter(t9dir, new WhitespaceAnalyzer(), true);

    for (int i = 0; i < numDocs; i++) {
      Document origDoc = reader.document(i);
      String word = origDoc.get("word");
      if (word == null || word.length() == 0) continue;

      Document newDoc = new Document();
      newDoc.add(Field.Keyword("word", word));
      newDoc.add(Field.Keyword("t9", t9(word)));
      newDoc.add(new Field("length",
          Integer.toString(word.length()), false, true, false));

      writer.addDocument(newDoc);
      if (i % 100 == 0) {
        System.out.println("Document " + i);
      }
    }

    writer.optimize();
    writer.close();

    reader.close();
  }

  private static String t9(String word) {
    char[] t9 = new char[word.length()];
    for (int i=0; i < word.length(); i++) {
      t9[i] = ((Character) keyMap.get(new Character(word.charAt(i)))).charValue();
    }

    return new String(t9);
  }
}
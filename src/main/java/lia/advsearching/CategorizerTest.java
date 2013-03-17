package lia.advsearching;

import lia.common.LiaTestCase;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class CategorizerTest extends LiaTestCase {
  Map categoryMap;

  protected void setUp() throws Exception {
    super.setUp();

    categoryMap = new TreeMap();

    buildCategoryVectors();
//    dumpCategoryVectors();
  }

  public void testCategorization() throws Exception {
    assertEquals("/technology/computers/programming/methodology",
        getCategory("extreme agile methodology"));
    assertEquals("/education/pedagogy",
        getCategory("montessori education philosophy"));

  }

  private void dumpCategoryVectors() {
    Iterator categoryIterator = categoryMap.keySet().iterator();
    while (categoryIterator.hasNext()) {
      String category = (String) categoryIterator.next();
      System.out.println("Category " + category);

      Map vectorMap = (Map) categoryMap.get(category);
      Iterator vectorIterator = vectorMap.keySet().iterator();
      while (vectorIterator.hasNext()) {
        String term = (String) vectorIterator.next();
        System.out.println("    " + term + " = " + vectorMap.get(term));
      }

    }
  }

  private void buildCategoryVectors() throws IOException {
    IndexReader reader = IndexReader.open(directory);

    int maxDoc = reader.maxDoc();

    for (int i = 0; i < maxDoc; i++) {
      if (!reader.isDeleted(i)) {
        Document doc = reader.document(i);
        String category = doc.get("category");

        Map vectorMap = (Map) categoryMap.get(category);
        if (vectorMap == null) {
          vectorMap = new TreeMap();
          categoryMap.put(category, vectorMap);
        }

        TermFreqVector termFreqVector =
            reader.getTermFreqVector(i, "subject");

        addTermFreqToMap(vectorMap, termFreqVector);
      }
    }
  }

  private void addTermFreqToMap(Map vectorMap,
                                TermFreqVector termFreqVector) {
    String[] terms = termFreqVector.getTerms();
    int[] freqs = termFreqVector.getTermFrequencies();

    for (int i = 0; i < terms.length; i++) {
      String term = terms[i];

      if (vectorMap.containsKey(term)) {
        Integer value = (Integer) vectorMap.get(term);
        vectorMap.put(term,
            new Integer(value.intValue() + freqs[i]));
      } else {
        vectorMap.put(term, new Integer(freqs[i]));
      }
    }
  }


  private String getCategory(String subject) {
    String[] words = subject.split(" ");

    Iterator categoryIterator = categoryMap.keySet().iterator();
    double bestAngle = Double.MAX_VALUE;
    String bestCategory = null;

    while (categoryIterator.hasNext()) {
      String category = (String) categoryIterator.next();
//      System.out.println(category);

      double angle = computeAngle(words, category);
//      System.out.println(" -> angle = " + angle + " (" + Math.toDegrees(angle) + ")");
      if (angle < bestAngle) {
        bestAngle = angle;
        bestCategory = category;
      }
    }

    return bestCategory;
  }

  private double computeAngle(String[] words, String category) {
    // assume words are unique and only occur once

    Map vectorMap = (Map) categoryMap.get(category);

    int dotProduct = 0;
    int sumOfSquares = 0;
    for (int i = 0; i < words.length; i++) {
      String word = words[i];
      int categoryWordFreq = 0;

      if (vectorMap.containsKey(word)) {
        categoryWordFreq =
            ((Integer) vectorMap.get(word)).intValue();
      }

      dotProduct += categoryWordFreq;  // optimized because we assume frequency in words is 1
      sumOfSquares += categoryWordFreq * categoryWordFreq;
    }


    double denominator;
    if (sumOfSquares == words.length) {
      // avoid precision issues for special case
      denominator = sumOfSquares; // sqrt x * sqrt x = x
    } else {
      denominator = Math.sqrt(sumOfSquares) *
                    Math.sqrt(words.length);
    }

    double ratio = dotProduct / denominator;

    return Math.acos(ratio);
  }

}

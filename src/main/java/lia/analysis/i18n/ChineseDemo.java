package lia.analysis.i18n;

import lia.analysis.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Label;
import java.io.IOException;
import java.util.Locale;

public class ChineseDemo {
  private static String[] strings = {"道德經"};

  private static Analyzer[] analyzers = {
    new SimpleAnalyzer(),
    new StandardAnalyzer(),
    new ChineseAnalyzer(),
    new CJKAnalyzer()
  };

  public static void main(String args[]) throws Exception {

    for (int i = 0; i < strings.length; i++) {
      String string = strings[i];
      for (int j = 0; j < analyzers.length; j++) {
        Analyzer analyzer = analyzers[j];
        analyze(string, analyzer);
      }
    }

  }

  private static void analyze(String string, Analyzer analyzer)
      throws IOException {
    StringBuffer buffer = new StringBuffer();
    Token[] tokens =
        AnalyzerUtils.tokensFromAnalysis(analyzer, string);
    for (int i = 0; i < tokens.length; i++) {
      buffer.append("[");
      buffer.append(tokens[i].termText());
      buffer.append("] ");
    }

    String output = buffer.toString();

    Frame f = new Frame();
    String name = analyzer.getClass().getName();
    f.setTitle(name.substring(name.lastIndexOf('.') + 1)
        + " : " + string);
    f.setResizable(false);

    Font font = new Font(null, Font.PLAIN, 36);
    int width = getWidth(f.getFontMetrics(font), output);

    f.setSize((width < 250) ? 250 : width + 50, 75);

    Label label = new Label(buffer.toString());
    label.setSize(width, 75);
    label.setAlignment(Label.CENTER);
    label.setFont(font);
    label.setLocale(Locale.CHINA);
    f.add(label);

    f.setVisible(true);
  }

  private static int getWidth(FontMetrics metrics, String s) {
    int size = 0;
    for (int i = 0; i < s.length(); i++) {
      size += metrics.charWidth(s.charAt(i));
    }

    return size;
  }
}
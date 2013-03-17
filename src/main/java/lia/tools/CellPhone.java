package lia.tools;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Date;

public class CellPhone extends JFrame {

  private JTextField screen = new JTextField();
  private JTextField status = new JTextField();
  private IndexSearcher searcher;

  private String number = "";

  private int curWord;
  private String[] words;
  private boolean debug = false;

  public CellPhone(Directory directory) throws HeadlessException, IOException {

    setTitle("CellPhone");

    JPanel phoneButtons = new JPanel(new GridLayout(4, 3));
    phoneButtons.add(new JButton(new PhoneButtonAction('1', "[next]")));
    phoneButtons.add(new JButton(new PhoneButtonAction('2', "abc")));
    phoneButtons.add(new JButton(new PhoneButtonAction('3', "def")));
    phoneButtons.add(new JButton(new PhoneButtonAction('4', "ghi")));
    phoneButtons.add(new JButton(new PhoneButtonAction('5', "jkl")));
    phoneButtons.add(new JButton(new PhoneButtonAction('6', "mno")));
    phoneButtons.add(new JButton(new PhoneButtonAction('7', "pqrs")));
    phoneButtons.add(new JButton(new PhoneButtonAction('8', "tuv")));
    phoneButtons.add(new JButton(new PhoneButtonAction('9', "wxyz")));
    phoneButtons.add(new JButton(new PhoneButtonAction('*', "[back]")));
    phoneButtons.add(new JButton(new PhoneButtonAction('0', "[debug]")));
    phoneButtons.add(new JButton(new PhoneButtonAction('#', "[clear]")));

    getContentPane().setLayout(new BorderLayout());

    status.setBackground(Color.LIGHT_GRAY);
    getContentPane().add(screen, BorderLayout.NORTH);
    getContentPane().add(phoneButtons, BorderLayout.CENTER);
    getContentPane().add(status, BorderLayout.SOUTH);
    pack();

    searcher = new IndexSearcher(directory);
  }


  private class PhoneButtonAction extends AbstractAction {
    private char key;

    public PhoneButtonAction(char key, String name) {
      super("<html><center><b>" + key + "</b><br>" + name +"</center></html>");
      this.key = key;
    }

    public void actionPerformed(ActionEvent event) {
      String message = null;

      switch (key) {
        // debug current number
        case '0':
          debug = !debug;
          message = "DEBUG " + (debug ? "ON" : "OFF");
          break;

        // next matching word
        case '1':
          curWord++;
          if (words == null || curWord >= words.length) curWord = 0;
          message = "";
          break;


        // clear
        case '#':
          number = "";
          words = null;
          curWord = 0;
          message = "Cleared";
          break;

        // back up a digit
        case '*':
          if (number.length() > 0) {
            number = number.substring(0, number.length() - 1);
          }
          break;

        default:
          number = number + key;
      }

      if (message == null) {
        long time = findWords();
        message = "(" + time + "ms)";
      }

      screen.setText(getCurrentWord());
      updateStatus(message);
    }

  }

  private String getCurrentWord() {
    if (words == null || words.length == 0) return "";

    return words[curWord];
  }

  private void updateStatus(String additonal) {
    String message = "no words";
    if (words != null && words.length > 0) {
      message = (curWord + 1) + "/" + words.length;
    }
    status.setText("#" + number + ": " + message + " " + additonal);
  }

  private long findWords() {
    if (number == null || number.length() == 0) {
      words = null;
      return -1;
    }

    BooleanQuery query = new BooleanQuery();
    Term term = new Term("t9", number);
    TermQuery termQuery = new TermQuery(term);
    termQuery.setBoost(2.0f);
    WildcardQuery plus2 = new WildcardQuery(new Term("t9", number + "??"));
    query.add(termQuery, false, false);
    query.add(plus2, false, false);

    long time = -1;
    try {
      Date start = new Date();
      Hits hits = searcher.search(query,
          new Sort(new SortField[] {SortField.FIELD_SCORE, 
                                    new SortField("length",
                                        SortField.INT),
                                    new SortField("word")}));
      Date end = new Date();
      time = end.getTime() - start.getTime();

      if (debug) System.out.println("---- " + number);
      words = new String[hits.length()];
      curWord = 0;
      for (int i=0; i < hits.length(); i++) {
        if (debug) {
          System.out.println(hits.doc(i).get("word") + " - " + hits.score(i));
          System.out.println(searcher.explain(query, hits.id(i)));
        }
        words[i] = hits.doc(i).get("word");
      }
      if (debug) {
        if (hits.length() > 0) {
          System.out.println(number + ": " + hits.doc(0).get("word") + " (" + hits.length() + ")");
        }
      }
    } catch (IOException e) {
      updateStatus("Exception!  See console.");
      e.printStackTrace();
    }

    return time;
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("java CellPhone <index>");
      System.exit(-1);
    }

    CellPhone app = new CellPhone(new RAMDirectory(FSDirectory.getDirectory(args[0], false)));
    app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    app.setVisible(true);
  }


}



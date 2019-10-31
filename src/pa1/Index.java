package pa1;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import api.TaggedVertex;
import api.Util;

/**
 * Implementation of an inverted index for a web graph.
 * 
 * @author Benjamin Vogel
 */
public class Index
{
  private List<TaggedVertex<String>> indexUrls;
  private HashMap<String, List<URLOccurrence>> invertedIndex;

  /**
   * Constructs an index from the given list of urls.  The
   * tag value for each url is the indegree of the corresponding
   * node in the graph to be indexed.
   * @param urls
   *   information about graph to be indexed
   */
  public Index(List<TaggedVertex<String>> urls)
  {
    indexUrls = urls;
    invertedIndex = new HashMap<String, List<URLOccurrence>>();
  }
  
  /**
   * Creates the index.
   */
  public void makeIndex()
  {
    for(String url : indexUrls)
    {
      // Initially make the connection and grab the text
      String document = null;
      try
      {
        // TODO: Implement a politeness policy
        document = Jsoup.connect(url).get().body().text();
      }
      catch (UnsupportedMimeTypeException e)
      {
        System.out.println("--unsupported document type, do nothing");
        continue;
      } 
      catch (HttpStatusException e)
      {
        System.out.println("--invalid link, do nothing");
        continue;
      }

      // Create a new scanner for the document
      Scanner scanner = new Scanner(document);

      // Create an individual hashmap to track the occurrence of each word in this url
      HashMap<String, URLOccurrence> wordOccurrence = new HashMap<String, URLOccurrence>();

      while (scanner.hasNext())
      {
        // Get the next word, strip punctuation, and check if it's a STOP word
        String word = scanner.next();

        word = Util.stripPunctuation(word);

        if (!Util.isStopWord(word))
        {
          // Get the occurrence object from the hashmap
          URLOccurrence occurrence = wordOccurrence.get(word);

          if (occurrence == null)
          {
            // This is a new word, so initialize a URLOccurrence object
            occurrence = new URLOccurrence(url, 1);
          }
          else
          {
            // We've seen this word before, so just increment the number of times we've seen it
            occurrence.incrementOccurrences();
          }

          // Place the object back in the hashmap
          wordOccurrence.put(word, occurrence);
        }
      }
      scanner.close();

      Set<String> wordSet = wordOccurrence.keySet();

      // Iterate through each word in the hashmap above and place the occurrence in the list within invertedIndex
      for (String word : wordSet)
      {
        URLOccurrence occurrence = wordOccurrence.get(word);
        List<URLOccurrence> totalOccurrences = invertedIndex.get(word);

        // Check if it is a new word in the invertedIndex
        if (totalOccurrences == null)
        {
          totalOccurrences = new List<URLOccurrence>(occurrence);
        }
        else
        {
          totalOccurrences.add(occurrence);
        }

        invertedIndex.put(word, totalOccurrences);
      }
    }
  }
  
  /**
   * Searches the index for pages containing keyword w.  Returns a list
   * of urls ordered by ranking (largest to smallest).  The tag 
   * value associated with each url is its ranking.  
   * The ranking for a given page is the number of occurrences
   * of the keyword multiplied by the indegree of its url in
   * the associated graph.  No pages with rank zero are included.
   * @param w
   *   keyword to search for
   * @return
   *   ranked list of urls
   */
  public List<TaggedVertex<String>> search(String w)
  {
    // TODO
    return null;
  }


  /**
   * Searches the index for pages containing both of the keywords w1
   * and w2.  Returns a list of qualifying
   * urls ordered by ranking (largest to smallest).  The tag 
   * value associated with each url is its ranking.  
   * The ranking for a given page is the number of occurrences
   * of w1 plus number of occurrences of w2, all multiplied by the 
   * indegree of its url in the associated graph.
   * No pages with rank zero are included.
   * @param w1
   *   first keyword to search for
   * @param w2
   *   second keyword to search for
   * @return
   *   ranked list of urls
   */
  public List<TaggedVertex<String>> searchWithAnd(String w1, String w2)
  {
    // TODO
    return null;
  }
  
  /**
   * Searches the index for pages containing at least one of the keywords w1
   * and w2.  Returns a list of qualifying
   * urls ordered by ranking (largest to smallest).  The tag 
   * value associated with each url is its ranking.  
   * The ranking for a given page is the number of occurrences
   * of w1 plus number of occurrences of w2, all multiplied by the 
   * indegree of its url in the associated graph.
   * No pages with rank zero are included.
   * @param w1
   *   first keyword to search for
   * @param w2
   *   second keyword to search for
   * @return
   *   ranked list of urls
   */
  public List<TaggedVertex<String>> searchWithOr(String w1, String w2)
  {
    // TODO
    return null;
  }
  
  /**
   * Searches the index for pages containing keyword w1
   * but NOT w2.  Returns a list of qualifying urls
   * ordered by ranking (largest to smallest).  The tag 
   * value associated with each url is its ranking.  
   * The ranking for a given page is the number of occurrences
   * of w1, multiplied by the 
   * indegree of its url in the associated graph.
   * No pages with rank zero are included.
   * @param w1
   *   first keyword to search for
   * @param w2
   *   second keyword to search for
   * @return
   *   ranked list of urls
   */
  public List<TaggedVertex<String>> searchAndNot(String w1, String w2)
  {
    // TODO
    return null;
  }
}

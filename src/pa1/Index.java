package pa1;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

import api.TaggedVertex;
import api.Util;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Implementation of an inverted index for a web graph.
 * 
 * @author Benjamin Vogel
 */
public class Index
{
  private List<TaggedVertex<String>> indexUrls;
  private HashMap<String, ArrayList<URLOccurrence>> invertedIndex;
  private int politenessTracker;

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
    invertedIndex = new HashMap<String, ArrayList<URLOccurrence>>();
    politenessTracker = 0;
  }
  
  /**
   * Creates the index.
   */
  public void makeIndex()
  {
    for(TaggedVertex<String> url : indexUrls)
    {
      // Politeness policy
      if (politenessTracker > 50)
      {
        try
        {
          Thread.sleep(3000);
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
          return;
        }
        politenessTracker = 0;
      }
      // Initially make the connection and grab the text
      String document = null;
      try
      {
        document = Jsoup.connect(url.getVertexData()).get().body().text();
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
      catch(IOException e)
      {
        e.printStackTrace();
        continue;
      }
      finally
      {
        politenessTracker++;
      }

      // Create a new scanner for the document
      Scanner scanner = new Scanner(document);

      // Create an individual HashMap to track the occurrence of each word in this url
      HashMap<String, URLOccurrence> wordOccurrence = new HashMap<String, URLOccurrence>();

      while (scanner.hasNext())
      {
        // Get the next word, strip punctuation, and check if it's a STOP word
        String word = scanner.next();

        word = Util.stripPunctuation(word);

        if (!Util.isStopWord(word))
        {
          URLOccurrence occurrence;

          if (!wordOccurrence.containsKey(word))
          {
            // This is a new word, so initialize a URLOccurrence object
            occurrence = new URLOccurrence(url.getVertexData(), url.getTagValue(), 1);
          }
          else
          {
            // Get the occurrence object from the HashMap
            occurrence = wordOccurrence.get(word);

            // We've seen this word before, so just increment the number of times we've seen it
            occurrence.incrementOccurrences();
          }

          if (occurrence.getRank() > 0)
          {
            // Place the object back in the HashMap
            wordOccurrence.put(word, occurrence);
          }
        }
      }
      scanner.close();

      Set<String> wordSet = wordOccurrence.keySet();

      // Iterate through each word in the HashMap above and place the occurrence in the list within invertedIndex
      for (String word : wordSet)
      {
        URLOccurrence occurrence = wordOccurrence.get(word);
        ArrayList<URLOccurrence> totalOccurrences;

        // Check if it is a new word in the invertedIndex
        if (!invertedIndex.containsKey(word))
        {
          totalOccurrences = new ArrayList<URLOccurrence>();
          totalOccurrences.add(occurrence);
        }
        else
        {
          // Get the urls and the ranks within the inverted index
          totalOccurrences = invertedIndex.get(word);
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
    // Start our new ranked list
    List<TaggedVertex<String>> rankedList = new ArrayList<TaggedVertex<String>>();

    // Check to make sure the word is in the inverted index
    if (invertedIndex.containsKey(w))
    {
      // Get the urls and the ranks within the inverted index
      List<URLOccurrence> totalOccurrences = invertedIndex.get(w);

      // Increment over each of the urls and add them to the list
      for (URLOccurrence url : totalOccurrences)
      {
        RankVertex urlRank = new RankVertex(url.getURL(), url.getRank());
        rankedList.add(urlRank);
      }

      // Sort the list based on rank in descending order
      rankedList.sort(Comparator.comparing(TaggedVertex<String>::getTagValue).reversed());
    }
    return rankedList;
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
    HashMap<String, Integer> urlRankW1 = new HashMap<String, Integer>();

    // Get the ranked lists for each word
    List<TaggedVertex<String>> rankedListW1 = searchNoSort(w1);
    List<TaggedVertex<String>> rankedListW2 = searchNoSort(w2);

    List<TaggedVertex<String>> rankedANDList = new ArrayList<TaggedVertex<String>>();

    for (TaggedVertex<String> vertex : rankedListW1)
    {
      // Place each of the url's for word 1 in the HashMap
      urlRankW1.put(vertex.getVertexData(), vertex.getTagValue());
    }

    // Go through each vertex in the search list
    for (TaggedVertex<String> compareVertex : rankedListW2)
    {
      if (urlRankW1.containsKey(compareVertex.getVertexData()))
      {
        // If the url is in the HashMap, then create a new vertex with that url, add the two ranks, and then add that to the list
        Integer w1Rank = urlRankW1.get(compareVertex.getVertexData());
        RankVertex newANDVertex = new RankVertex(compareVertex.getVertexData(), compareVertex.getTagValue() + w1Rank);
        rankedANDList.add(newANDVertex);
      }
    }

    // Sort the list based on rank in descending order
    rankedANDList.sort(Comparator.comparing(TaggedVertex<String>::getTagValue).reversed());
    return rankedANDList;
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
    // Or is just AND, the ranks of W1 NOT W2, and W2 NOT W1 
    List<TaggedVertex<String>> searchORList = searchWithAnd(w1, w2);
    searchORList.addAll(searchAndNot(w1, w2));
    searchORList.addAll(searchAndNot(w2, w1));
    searchORList.sort(Comparator.comparing(TaggedVertex<String>::getTagValue).reversed());
    return searchORList;
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
    List<TaggedVertex<String>> searchNOTList;
    List<TaggedVertex<String>> searchW1List = searchNoSort(w1);
    List<TaggedVertex<String>> searchW2List = searchNoSort(w2);

    // Create a new HashMap that contains all the urls that contain one word and not the other
    HashMap<String, TaggedVertex<String>> notMap = new HashMap<String, TaggedVertex<String>>();

    for (TaggedVertex<String> w1Vertex : searchW1List)
    {
        // If the map does not contain the vertex, then it hasn't appeared in the HashMap, so add it
        notMap.put(w1Vertex.getVertexData(), w1Vertex);
    }

    for (TaggedVertex<String> w2Vertex : searchW2List)
    {
      // This URL contains both w1 and w2, so remove it from the Map
      notMap.remove(w2Vertex.getVertexData());
    }

    // Convert the map to values and then sort
    searchNOTList = new ArrayList<TaggedVertex<String>>(notMap.values());
    searchNOTList.sort(Comparator.comparing(TaggedVertex<String>::getTagValue).reversed());

    return searchNOTList;
  }

  /**
   * The exact same method as search() above, but it doesn't sort the list. This allows for other methods
   * to use it as a helper and reduce their runtimes.
   * @param w
   *    keyword to search for
   * @return
   *    ranked list of urls
   */
  private List<TaggedVertex<String>> searchNoSort(String w) {
    // Start our new ranked list
    List<TaggedVertex<String>> rankedList = new ArrayList<TaggedVertex<String>>();

    // Check to make sure the word is in the inverted index
    if (invertedIndex.containsKey(w)) {
      // Get the urls and the ranks within the inverted index
      List<URLOccurrence> totalOccurrences = invertedIndex.get(w);

      // Increment over each of the urls and add them to the list
      for (URLOccurrence url : totalOccurrences) {
        RankVertex urlRank = new RankVertex(url.getURL(), url.getRank());
        rankedList.add(urlRank);
      }
    }

    return rankedList;
  }
}

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
    for(TaggedVertex<String> url : indexUrls)
    {
      // Initially make the connection and grab the text
      String document = null;
      try
      {
        // TODO: Implement a politeness policy
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
          URLOccurrence occurrence;

          if (!wordOccurrence.containsKey(word))
          {
            // This is a new word, so initialize a URLOccurrence object
            occurrence = new URLOccurrence(url.getVertexData(), url.getTagValue(), 1);
          }
          else
          {
            // Get the occurrence object from the hashmap
            occurrence = wordOccurrence.get(word);

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
        List<URLOccurrence> totalOccurrences;

        // Check if it is a new word in the invertedIndex
        if (!invertedIndex.containsKey(word))
        {
          totalOccurrences = new List<URLOccurrence>(occurrence);
        }
        else
        {
          // Get the urls and the ranks within the inverted index
          totalOccurrences = invertedIndex.get(w);
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
    List<RankVertex> rankedList = new List<RankVertex>();

    // Check to make sure the word is in the inverted index
    if (invertedIndex.containsKey(key))
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
      rankedList.sort(Comparator.comparing(RankVertex::getTagValue).reversed());
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
    List<TaggedVertex<String>> rankedListW1 = search(w1);
    List<TaggedVertex<String>> rankedListW2 = search(w2);

    List<RankVertex> rankedANDList = new List<RankVertex>();

    for (TaggedVertex<String> vertex : rankedListW1)
    {
      // Place each of the url's for word 1 in the hashmap
      urlRankW1.put(vertex.getVertexData(), vertex.getTagValue());
    }

    // Go through each vertex in the search list
    for (TaggedVertex<String> compareVertex : rankedListW2)
    {
      if (urlRankW1.containsKey(compareVertex.getVertexData()))
      {
        // If the url is in the hashmap, then create a new vertex with that url, add the two ranks, and then add that to the list
        Integer w1Rank = urlRankW1.get(compareVertex.getVertexData());
        RankVertex newANDVertex = new RankVertex(compareVertex.getVertexData(), compareVertex.getTagValue() + w1Rank);
        rankedANDList.add(newANDVertex);
      }
    }

    // Sort the list based on rank in descending order
    rankedANDList.sort(Comparator.comparing(RankVertex::getTagValue).reversed());
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
    List<TaggedVertex<String>> searchNOTList = new List<TaggedVertex<String>>();
    List<TaggedVertex<String>> searchW1List = search(w1);
    List<TaggedVertex<String>> searchW2List = search(w2);

    // Create a new hashmap that contains all the urls that contain one word and not the other
    HashMap<String, TaggedVertex<String>> notMap = new HashMap<String, TaggedVertex<String>>();

    for (TaggedVertex<String> w1Vertex : searchW1List)
    {
      
      if (notMap.containsKey(w1Vertex.getVertexData()))
      {
        // If the map does not contain the vertex, then it hasn't appeared in the hashmap, so add it
        notMap.put(w1Vertex.getVertexData(), w1Vertex);
      }
      else
      {
        // However, if it is there, it means that both word 1 and word 2 have that url, so remove it
        notMap.remove(w1Vertex.getVertexData());
      }
    }

    for (TaggedVertex<String> w2Vertex : searchW2List)
    {
      // If the vertex is null, then it hasn't appeared in the hashmap, so add it
      if (notMap.containsKey(w2Vertex.getVertexData()))
      {
        notMap.put(w2Vertex.getVertexData(), w2Vertex);
      }
      else
      {
        // However, if it is there, it means that both word 1 and word 2 have that url, so remove it
        notMap.remove(w2Vertex.getVertexData());
      }
    }

    // Convert the map to values and then sort
    searchNOTList = notMap.values();
    searchNOTList.sort(Comparator.comparing(TaggedVertex<String>::getTagValue).reversed());

    return searchNOTList;
  }
}

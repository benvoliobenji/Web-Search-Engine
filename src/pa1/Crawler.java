package pa1;

import api.Graph;
import api.Util;

import java.util.ArrayList;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Implementation of a basic web crawler that creates a graph of some
 * portion of the world wide web.
 *
 * @author Benjamin Vogel
 */
public class Crawler
{
  private String seed = "";
  private int maximumDepth = 0;
  private int maximumPages = 0;
  /**
   * Constructs a Crawler that will start with the given seed url, including
   * only up to maxPages pages at distance up to maxDepth from the seed url.
   * @param seedUrl
   * @param maxDepth
   * @param maxPages
   */
  public Crawler(String seedUrl, int maxDepth, int maxPages)
  {
    seed = seedUrl;
    maximumDepth = maxDepth;
    maximumPages = maxPages;
    // TODO: Make a graph here or just make one and return it in the crawl() method?
  }
  
  /**
   * Creates a web graph for the portion of the web obtained by a BFS of the 
   * web starting with the seed url for this object, subject to the restrictions
   * implied by maxDepth and maxPages.  
   * @return
   *   an instance of Graph representing this portion of the web
   */
  public Graph<String> crawl()
  {
    // Initialize the discovered arraylist to keep track of the pages crawled
    // TODO: Change discovered array list to be of integers and have the currentPages be the index
    ArrayList<Vertex> discovered = new ArrayList<Vertex>();
    Queue<Vertex> Q = new Queue();

    Vertex seedVertex = new Vertex(seed, 0);
    Q.add(seedVertex);
    discovered.add(seedVertex);

    int currentDepth = 0;
    int currentPages = 1;

    while (!Q.isEmpty())
    {
      Vertex currentVertex = Q.remove();

      Document doc = Jsoup.connect(currentVertex.getUrl()).get();

      Element links = doc.select("a[href]");
      for (Element link : links)
      {
        String v = link.attr("abs:href");

        Document temp = null;
        if (!Util.ignoreLink(currentVertex.getUrl(), v))
        {
          try
          {
            // TODO: Implement a politeness policy
            temp = Jsoup.connect(v).get();
          }
          catch (UnsupportedMimeTypeException e)
          {
            System.out.println("--unsupported document type, do nothing");
            continue;
          } 
          catch (HttpStatusException  e)
          {
            System.out.println("--invalid link, do nothing");
            continue;
          }

          Vertex newVertex = new Vertex(v);

          if (!discovered.contains(newVertex))
          {
            int newVertexDepth = currentVertex.getDepth() + 1;
            if (currentPages < maximumPages && newVertexDepth <= maximumDepth)
            {
              // Set the new vertex's depth and in degree
              newVertex.setDepth(newVertexDepth);

              discovered.add(newVertex);
              Q.add(newVertex);
              currentPages++;
            }
          }
        }
        else
        {
          System.out.println("--ignore");
          continue;
        }
      }
    }
    return null;
  }
}

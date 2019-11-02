package pa1;

import api.Graph;
import api.Util;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.HashMap;

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
    LinkedHashMap<String, Vertex> discovered = new HashMap<String, Vertex>();
    Queue<Vertex> Q = new Queue();

    ArrayList<Thread> crawlerThreads = new ArrayList<Thread>();
    boolean crawling = true;

    Vertex seedVertex = new Vertex(seed, 0);
    Q.add(seedVertex);
    discovered.put(seedVertex.getUrl(), seedVertex);

    AtomicInteger politenessInteger = new AtomicInteger(0);

    do
    {
      synchronized(Q)
      {
        // Empty out the queue for this layer
        while(!Q.isEmpty())
        {
          Vertex currentVertex = Q.remove();

          // Spawn a new crawler thread and add it to the arraylist
          CrawlerThread newCrawlerThread = new CrawlerThread(currentVertex, discovered, Q, maximumPages, maximumDepth);
          newCrawlerThread.run();
          crawlerThreads.add(newCrawlerThread);
        }

      }


      // Wait until all threads are done
      // Simply put, this synchronizes so we don't move past each "layer" without waiting for all of the threads to finish
      for (Thread crawlThread : crawlerThreads)
      {
        try
        {
          crawlThread.join();
        }
        catch (InterruptedException e)
        {
          // Change this maybe?
          throw new RuntimeException(e); 
        }
      }

      // Now that all threads are joined, check if the queue is still empty
      crawling = !Q.isEmpty();

    } while (crawling);

    VertexGraph graph = new VertexGraph(discovered);
    return graph;
  }
}

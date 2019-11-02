package pa1;

import api.Graph;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

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
    // Initialize the discovered ArrayList to keep track of the pages crawled
    LinkedHashMap<String, Vertex> discovered = new LinkedHashMap<>();
    Queue<Vertex> Q = new LinkedList<Vertex>();

    ArrayList<Thread> crawlerThreads = new ArrayList<Thread>();
    boolean crawling = true;

    Vertex seedVertex = new Vertex(seed, 0);
    Q.add(seedVertex);
    discovered.put(seedVertex.getUrl(), seedVertex);

    // Variables used to enforce correct politeness
    AtomicInteger politenessInteger = new AtomicInteger(0);
    Semaphore politenessSemaphore = new Semaphore(1);

    do
    {
      synchronized(Q)
      {
        // Empty out the queue for this layer
        while(!Q.isEmpty())
        {
          Vertex currentVertex = Q.remove();

          // Spawn a new crawler thread and add it to the ArrayList
          CrawlerThread newCrawlerThread = new CrawlerThread(currentVertex, discovered, Q,
                  maximumPages, maximumDepth, politenessInteger, politenessSemaphore);
          newCrawlerThread.start();
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

    return new VertexMap(discovered);
  }
}

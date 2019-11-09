package pa1;

import api.Util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A thread used to crawl the web in parallel. This is designed to eliminate one of the bottlenecks of the system,
 * sequentially calling jsoup.connect().get() on urls as well as modify the graph and the queue to update based on
 * information gained from the crawl.
 * @author Benjamin Vogel
 */
class CrawlerThread extends Thread
{
    private Vertex threadVertex;
    private LinkedHashMap<String, Vertex> graph;
    private Queue<Vertex> urlQueue;
    private int maxPages;
    private int maxDepth;
    private AtomicInteger politenessInteger;
    private Semaphore politenessSemaphore;

    /**
     * Constructs a new CrawlerThread object. It will use the information from the newThreadVertex object
     * to search through that url and find more urls to add to the graph and queue up to the maximum pages and maximum
     * depth, adhering to a politeness policy.
     * @param newThreadVertex
     *      The url data structure to search through.
     * @param crawledGraph
     *      The "discovered" graph to get vertices from and add to
     * @param graphQueue
     *      The queue of undiscovered vertices to crawl to
     * @param maximumPages
     *      The maximum number of "nodes" in the graph
     * @param maximumDepth
     *      The maximum amount of urls from the seed url a node can be from
     * @param politenessInt
     *      The number of requests the crawler has made before pausing for 3 seconds.
     * @param politenessSem
     *      The semaphore used to make sure we strictly adhere to the politeness policy
     */
    public CrawlerThread(Vertex newThreadVertex, LinkedHashMap<String, Vertex> crawledGraph, Queue<Vertex> graphQueue,
                         int maximumPages, int maximumDepth, AtomicInteger politenessInt, Semaphore politenessSem)
    {
        threadVertex = newThreadVertex;
        graph = crawledGraph;
        urlQueue = graphQueue;
        maxPages = maximumPages;
        maxDepth = maximumDepth;
        politenessInteger = politenessInt;
        politenessSemaphore = politenessSem;
    }


    /**
     * This method connects to the base url provided in the constructor and begins searching through the
     * page for more urls. If it finds a valid url, it will check the graph to determine if it has been discovered
     * before or not. If it hasn't add it to the graph if either of the maximums hasn't been achieved yet, and add
     * it to the queue as well. If it has been discovered, update its indegree as well as the
     * original vertex's outdegree.
     */
    @Override
    public void run()
    {
        Document doc = null;
        try
        {
            // Politeness policy
            try
            {
                // Used so that we don't have multiple threads sleeping, instead the rest just wait on the
                // semaphore
                if (PolitenessPolicy()) return;

                doc = Jsoup.connect(threadVertex.getUrl()).get();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                return;
            }
        }
        catch (UnsupportedMimeTypeException e)
        {
            System.out.println("--unsupported document type, do nothing");
            return;
        }
        catch (HttpStatusException  e)
        {
            System.out.println("--invalid link, do nothing");
            return;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
        finally
        {
            politenessInteger.incrementAndGet();
        }

        Elements links = doc.select("a[href]");
        for (Element link : links)
        {
            String v = link.attr("abs:href");
//            System.out.println("Found: " + v);

//            Document temp = null;
            if (!Util.ignoreLink(threadVertex.getUrl(), v))
            {
                // This was originally trying to catch invalid links before adding them to the graph
                // but I realized those are also valid notes, just leaf nodes.
                // This also greatly speeds up the execution of the program.
//                try
//                {
//                    try
//                    {
//                        // Used so that we don't have multiple threads sleeping, instead the rest just wait on the
//                        // semaphore
//                        if (PolitenessPolicy()) return;
//
//                        temp = Jsoup.connect(v).get();
//                    }
//                    catch (InterruptedException e)
//                    {
//                        e.printStackTrace();
//                        return;
//                    }
//                }
//                catch (UnsupportedMimeTypeException e)
//                {
//                    System.out.println("--unsupported document type, do nothing");
//                    return;
//                }
//                catch (HttpStatusException  e)
//                {
//                    System.out.println("--invalid link, do nothing");
//                    return;
//                }
//                catch (IOException e)
//                {
//                    e.printStackTrace();
//                    return;
//                }

                Vertex newVertex = new Vertex(v);

                // We need to synchronize the graph and the queue among each thread
                // This avoids dirty reads and dirty writes
                synchronized(graph)
                {
                    synchronized(urlQueue)
                    {
                        if (!graph.containsKey(newVertex.getUrl()))
                        {
                            int newVertexDepth = threadVertex.getDepth() + 1;
                            int currentPages = graph.size();

                            if (currentPages < maxPages && newVertexDepth <= maxDepth)
                            {
                                // Set the new vertex's depth and add an ancestor
                                newVertex.setDepth(newVertexDepth);
                                newVertex.addAncestor(threadVertex);

                                // Update the current vertex to have this one as a child
                                threadVertex.addChild(newVertex);
                                graph.put(threadVertex.getUrl(), threadVertex);

                                // Place the new vertex in the discovered HashMap and place it in the queue
                                graph.put(newVertex.getUrl(), newVertex);

                                urlQueue.add(newVertex);
                            }
                        }
                        else
                        {
                            // Done to update relationships of ancestors and children
                            Vertex oldVertex = graph.get(newVertex.getUrl());

                            oldVertex.addAncestor(threadVertex);
                            threadVertex.addChild(oldVertex);

                            graph.put(oldVertex.getUrl(), oldVertex);
                            graph.put(threadVertex.getUrl(), threadVertex);
                        }
                    }
                }
            }
            else
            {
                System.out.println("--ignore");
                return;
            }
        }
    }

    private boolean PolitenessPolicy() throws InterruptedException {
        politenessSemaphore.acquire();

        if (politenessInteger.incrementAndGet() > 50)
        {
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                return true;
            }
            politenessInteger.set(1);
        }
        politenessSemaphore.release();
        return false;
    }
}
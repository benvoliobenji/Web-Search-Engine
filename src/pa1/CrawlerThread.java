package pa1;

import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

class CrawlerThread extends Thread
{
    private Vertex threadVertex;
    private LinkedHashMap<String, Vertex> graph;
    private Queue<E> urlQueue;
    int maxPages;
    int maxDepth;
    AtomicInteger politenessInteger;

    public CrawlerThread(Vertex newThreadVertex, LinkedHashMap<String, Vertex> crawledGraph, Queue graphQueue, int maximumPages, int maximumDepth, AtomicInteger politenessInt)
    {
        threadVertex = newThreadVertex;
        graph = crawledGraph;
        urlQueue = graphQueue;
        maxPages = maximumPages;
        maxDepth = maximumDepth;
        politenessInteger = politenessInt;
    }


    @Override
    public void run()
    {
        // Politeness policy for each connection
        if (politenessInteger.get() >= 50)
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
            politenessInteger.set(0);
        }

        Document doc = Jsoup.connect(threadVertex.getUrl()).get();
        politenessInteger.incrementAndGet();

        Element links = doc.select("a[href]");
        for (Element link : links)
        {
            String v = link.attr("abs:href");

            Document temp = null;
            if (!Util.ignoreLink(threadVertex.getUrl(), v))
            {
                try
                {
                    // Politeness policy
                    if (politenessInteger.get() >= 50)
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
                        politenessInteger.set(0);
                    }

                    temp = Jsoup.connect(v).get();
                    politenessInteger.incrementAndGet();
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

                                Q.add(newVertex);
                            }
                        }
                        else
                        {
                            // Done to update relationships of ancestors and children
                            Vertex oldVertex = discovered.get(newVertex.getUrl());

                            oldVertex.addAncestor(threadVertex);
                            threadVertex.addChild(oldVertex);

                            discovered.put(oldVertex.getUrl(), oldVertex);
                            discovered.put(threadVertex.getUrl(), threadVertex);
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
}
package pa1;

import api.Graph;
import api.TaggedVertex;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 *  A small main class to try out the Crawler and Index classes
 * @author Benjamin Vogel
 */
public class Main
{
    // Just a small main method to test out the code
    public static void main(String args[])
    {
        Instant start = Instant.now();
        Crawler crawler = new Crawler("http://web.cs.iastate.edu/~smkautz", 10, 100);
        Graph<String> graph = crawler.crawl();
        Instant end = Instant.now();

        long timeElapsed = Duration.between(start, end).toMillis();
        System.out.println(graph.vertexData());
        System.out.println("Time Taken: " + timeElapsed);

        Index index = new Index(graph.vertexDataWithIncomingCounts());
        index.makeIndex();

        List<TaggedVertex<String>> rankedList = index.search("facebook");

        for(TaggedVertex<String> vertex : rankedList)
        {
            System.out.println("URL: " + vertex.getVertexData() + "/t Rank: " + vertex.getTagValue());
        }
    }
}
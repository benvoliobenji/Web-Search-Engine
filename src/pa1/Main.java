package pa1;

import api.Graph;

public class Main
{
    public static void main(String args[])
    {
        Crawler crawler = new Crawler("http://web.cs.iastate.edu/~smkautz", 4, 6);
        Graph<String> graph = crawler.crawl();

        System.out.println(graph.vertexData());
    }
}
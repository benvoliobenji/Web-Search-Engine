package pa1;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import api.Graph;
import api.TaggedVertex;;

/**
 * The implementation of the Graph interface which is returned in the Crawler crawl() method. This data structure
 * holds the vertices in the graph and returns a number of functions needed for the Index class to make a proper
 * inverted index.
 * @author Benjamin Vogel
 */
class VertexMap implements Graph<String>
{
    private LinkedHashMap<String, Vertex> graph;
    private ArrayList<Vertex> graphList;
    private ArrayList<TaggedVertex<String>> incomingList;

    /**
     * Constructs a new Graph object from the Linked HashMap created in the crawl() method.
     * @param newGraph
     *      The list of vertices representing the graph.
     */
    public VertexMap(LinkedHashMap<String, Vertex> newGraph)
    {
        graph = newGraph;

        graphList = new ArrayList<Vertex>(graph.values());

        incomingList = new ArrayList<TaggedVertex<String>>();
        int index = 0;

        for (Vertex vertex : graphList)
        {
            incomingList.add((TaggedVertex<String>) vertex);

            // Update the index of each vertex
            vertex.setIndex(index);
            graphList.set(index, vertex);
            index++;
        }
    }

    /**
     * Returns the URLs of the vertices in the graph
     * @return
     *      A list of URLs of each of the vertices in the graph.
     */
    public ArrayList<String> vertexData()
    {
        return new ArrayList<String>(graph.keySet());
    }

    /**
     * Returns a list of TaggedVertex of type String which contain the
     * URLs and the indegree of each vertex.
     * @return
     *      An ArrayList of TaggedVertexes.
     */
    public ArrayList<TaggedVertex<String>> vertexDataWithIncomingCounts()
    {
        return incomingList;
    }

    public List<Integer> getNeighbors(int index)
    {
        if (index >= graphList.size() || index < 0) { throw new IndexOutOfBoundsException(); }

        ArrayList<Integer> neighborIndexList = new ArrayList<Integer>();

        Vertex ancestorVertex = graphList.get(index);
        ArrayList<Vertex> neighbors = ancestorVertex.getChildren();

        for(Vertex neighbor : neighbors)
        {
            neighborIndexList.add(neighbor.getIndex());
        }

        return neighborIndexList;
    }

    public List<Integer> getIncoming(int index)
    {
        if (index >= graphList.size() || index < 0) { throw new IndexOutOfBoundsException(); }
        ArrayList<Integer> incomingIndexList = new ArrayList<Integer>();

        Vertex childVertex = graphList.get(index);
        ArrayList<Vertex> incoming = childVertex.getAncestors();

        for(Vertex ancestor : incoming)
        {
            incomingIndexList.add(ancestor.getIndex());
        }

        return incomingIndexList;
    }
}
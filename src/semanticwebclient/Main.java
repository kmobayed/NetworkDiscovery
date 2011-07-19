package semanticwebclient;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;
import de.fuberlin.wiwiss.ng4j.semwebclient.SemanticWebClient;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

  
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        Main.discover(args[0]);
        long endTime = System.currentTimeMillis();
        System.out.println("DONE");
        System.out.println("Network discovery time (seconds):"+ (endTime-startTime)/1000);
    }


    public static void discover(String site) throws IOException
    {
        FileWriter outFile;
        outFile = new FileWriter("graphviz.dot");
        PrintWriter out = new PrintWriter(outFile);
        SemanticWebClient semweb = new SemanticWebClient();

        String queryString =
        "PREFIX scho: <http://localhost/scho.xml#> " +
        "SELECT DISTINCT ?site1 ?site2 WHERE {" +
        "{ <http://localhost/"+site+"/scho.xml#Project1> a scho:Project . " +
//        "?site1 a scho:Site . " +
//        "?site2 a scho:Site . " +
//        "?pull a scho:PullFeed . " +
//        "?push a scho:PushFeed . " +
        "?pull scho:relatedPush ?push . " +
        "?push scho:onSite ?site1 . " +
        "?site2 scho:hasPull ?pull . " +
        "}"+
        "FILTER (?site1 != ?site2)"+
        "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, semweb.asJenaModel("default"));
        System.out.println("strict digraph G {");
        out.println("strict digraph G {");

        for (ResultSet rs1 = qe.execSelect() ; rs1.hasNext() ; )
        {
            QuerySolution binding1 = rs1.nextSolution();
            Resource site1=((Resource) binding1.get("site1"));
            Resource site2=((Resource) binding1.get("site2"));
            System.out.println("\t"+site1.getLocalName()+"\t->\t"+site2.getLocalName()+"\t;");
            out.println("\t\""+site2.getLocalName()+"\"\t->\t\""+site1.getLocalName()+"\"\t;");
        }
        qe.close();
        out.println("}");
        System.out.println("}");
        out.close();
    }
}

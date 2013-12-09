/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cartel;

import com.csvreader.CsvReader;
import it.uniroma1.dis.wsngroup.gexf4j.core.EdgeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.Gexf;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Mode;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.dynamic.TimeFormat;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.viz.NodeShape;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author C. Levallois
 */
public class CartelStatic {

    static public String[] headers;
    static public boolean readHeaders;
    static CsvReader transactionsCsv;
    static String textDelimiter = "\"";
    static String fieldDelimiter = ",";
    static String pathFile = "D:\\Docs Pro Clement\\Dropbox\\Cartel networks\\";
    static String nameFile = "dataset_12092013.csv";

    public static void main(String[] args) {
        try {

            //set up the csv reader
            char textdelimiter = textDelimiter.charAt(0);
            char fielddelimiter = fieldDelimiter.charAt(0);
            transactionsCsv = new CsvReader(new BufferedReader(new FileReader(pathFile + nameFile)), fielddelimiter);
            transactionsCsv.setTextQualifier(textdelimiter);
            transactionsCsv.setUseTextQualifier(true);
            readHeaders = transactionsCsv.readHeaders();
            headers = transactionsCsv.getHeaders();

            //set up the gexf object and its graph object
            Gexf gexf = new GexfImpl();
            Calendar date = Calendar.getInstance();

            gexf.getMetadata()
                    .setLastModified(date.getTime())
                    .setCreator("Gephi.org")
                    .setDescription("A Web network");
            gexf.setVisualization(true);

            Graph graph = gexf.getGraph();
            graph.setDefaultEdgeType(EdgeType.UNDIRECTED).setMode(Mode.DYNAMIC).setTimeType(TimeFormat.INTEGER);

            AttributeList attrList = new AttributeListImpl(AttributeClass.NODE);
            graph.getAttributeLists().add(attrList);

            Attribute attName = attrList.createAttribute("0", AttributeType.STRING, "name");
            Attribute attNationality = attrList.createAttribute("1", AttributeType.STRING, "nationality");
            Attribute attDegree = attrList.createAttribute("2", AttributeType.STRING, "number of ties");

            //ADDING NODES
            int index = 0;
            Set<String> setNodes = new HashSet();
            Node node;
            Node nodeSource = null;
            Node nodeTarget = null;
            while (transactionsCsv.readRecord()) {
                String[] values = transactionsCsv.getValues();

                if (setNodes.add(values[0])) {
                    node = graph.createNode(String.valueOf(index++));
                    node.setLabel(values[0]);
                    node.setSize(20);
                    node.getAttributeValues().addValue(attName, values[0]);
                    node.getAttributeValues().addValue(attNationality, values[7]);
                }

                if (setNodes.add(values[1])) {
                    node = graph.createNode(String.valueOf(index++));
                    node.setLabel(values[1]);
                    node.setSize(20);
                    node.getAttributeValues().addValue(attName, values[1]);
                    node.getAttributeValues().addValue(attNationality, values[8]);
                }

                //ADDING EDGE
                for (Node n : graph.getNodes()) {
                    if (n.getAttributeValues().get(0).getValue().equals(values[0])) {
                        nodeSource = n;
                    }
                }
                for (Node n : graph.getNodes()) {
                    if (n.getAttributeValues().get(0).getValue().equals(values[1])) {
                        nodeTarget = n;
                    }
                }
                if (!nodeSource.hasEdgeTo(nodeTarget.getId())) {
                    nodeSource.connectTo(nodeTarget);
                }
            }

            //WRITE THE GEXF FILE
            StaxGraphWriter graphWriter = new StaxGraphWriter();
            File f = new File("dynamic_graph_sample.gexf");
            Writer out;

            out = new FileWriter(f, false);
            graphWriter.writeToStream(gexf, out, "UTF-8");
            System.out.println(f.getAbsolutePath());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CartelStatic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CartelStatic.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

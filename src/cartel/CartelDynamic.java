/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cartel;

import GIS.CountryCentroids;
import com.csvreader.CsvReader;
import it.uniroma1.dis.wsngroup.gexf4j.core.Edge;
import it.uniroma1.dis.wsngroup.gexf4j.core.EdgeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.Gexf;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Mode;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.Attribute;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeClass;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeList;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeType;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeValue;
import it.uniroma1.dis.wsngroup.gexf4j.core.dynamic.Spell;
import it.uniroma1.dis.wsngroup.gexf4j.core.dynamic.TimeFormat;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.SpellImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeValueImpl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author C. Levallois
 */
public class CartelDynamic {

    static public String[] headers;
    static public boolean readHeaders;
    static CsvReader transactionsCsv;
    static String textDelimiter = "\"";
    static String fieldDelimiter = ",";
    static String pathFile = "D:\\Docs Pro Clement\\Dropbox\\Cartel networks\\";
    static String nameFile = "dataset_14102013.csv";
    static boolean weightAsFreqAttribute = true;
    static Set<String> europeanCountries = new TreeSet();

    public static void main(String[] args) {
        try {

            String[] europeanCountriesAsArray = {"French", "British", "German", "Dutch", "Belgian", "Croatian", "Spanish", "Italian", "Luxembourgian", "Irish", "Greek", "Czech", "Danish", "Polish", "Norwegian", "Swiss", "Slovakian", "Slovenian", "Hungarian", "Austrian", "Swedish", "Finnish", "Portugese"};
            europeanCountries.addAll(Arrays.asList(europeanCountriesAsArray));
            
            CountryCentroids.init();
            //set up the csv reader
            char textdelimiter = textDelimiter.charAt(0);
            char fielddelimiter = fieldDelimiter.charAt(0);
            transactionsCsv = new CsvReader(new BufferedReader(new FileReader(pathFile + nameFile)), fielddelimiter);
            transactionsCsv.setTextQualifier(textdelimiter);
            transactionsCsv.setUseTextQualifier(true);
            readHeaders = transactionsCsv.readHeaders();
            headers = transactionsCsv.getHeaders();

            Map<String, Node> mapNodes = new HashMap();

            //finding the first and last year
            int firstYear = 3000;
            int lastYear = 0;
            while (transactionsCsv.readRecord()) {
                String[] values = transactionsCsv.getValues();

                if (values[2] == "" || values[3] == "") {
                    continue;
                }

                if (Integer.parseInt(values[5]) < firstYear) {
                    firstYear = Integer.parseInt(values[5]);
                }
                if (Integer.parseInt(values[6]) > lastYear) {
                    lastYear = Integer.parseInt(values[6]);
                }
            }

            System.out.println("first year: " + firstYear);
            System.out.println("last year: " + lastYear);

            //set up the gexf object and its graph object
            Gexf gexf = new GexfImpl();
            Calendar date = Calendar.getInstance();

            gexf.getMetadata()
                    .setLastModified(date.getTime())
                    .setCreator("Clement Levallois")
                    .setDescription("cartel network");
            gexf.setVisualization(true);

            Graph graph = gexf.getGraph();
            graph.setDefaultEdgeType(EdgeType.UNDIRECTED).setMode(Mode.DYNAMIC).setTimeType(TimeFormat.INTEGER);

            AttributeList attrListNodeStatic = new AttributeListImpl(AttributeClass.NODE);
            AttributeList attrListNodeDynamic = new AttributeListImpl(AttributeClass.NODE).setMode(Mode.DYNAMIC);
            AttributeList attrListEdgeDynamic = new AttributeListImpl(AttributeClass.EDGE).setMode(Mode.DYNAMIC);
            graph.getAttributeLists().add(attrListNodeStatic);
            graph.getAttributeLists().add(attrListNodeDynamic);
            graph.getAttributeLists().add(attrListEdgeDynamic);

            Attribute attNationality = attrListNodeStatic.createAttribute("1", AttributeType.STRING, "nationality");
            Attribute attLattitude = attrListNodeStatic.createAttribute("5", AttributeType.DOUBLE, "lat");
            Attribute attLongitude = attrListNodeStatic.createAttribute("6", AttributeType.DOUBLE, "lng");
            Attribute attSelfLoops = attrListNodeDynamic.createAttribute("3", AttributeType.INTEGER, "number of self loops");
            Attribute attWeightDynamic;
            if (!weightAsFreqAttribute) {
                attWeightDynamic = attrListEdgeDynamic.createAttribute("4", AttributeType.FLOAT, "Weight");
            } else {
                attWeightDynamic = attrListEdgeDynamic.createAttribute("4", AttributeType.FLOAT, "freq");
            }

            //ADDING NODES
            int indexNode = 0;
            int indexEdge = 0;
            Set<String> setNodes = new HashSet();
            Node node;
            Node nodeSource = null;
            Node nodeTarget = null;
            Spell spell;
            AttributeValue attValue;

            transactionsCsv = new CsvReader(new BufferedReader(new FileReader(pathFile + nameFile)), fielddelimiter);
            transactionsCsv.setTextQualifier(textdelimiter);
            transactionsCsv.setUseTextQualifier(true);
            readHeaders = transactionsCsv.readHeaders();
            headers = transactionsCsv.getHeaders();

            while (transactionsCsv.readRecord()) {
                String[] values = transactionsCsv.getValues();
                if (values[2] == "" || values[3] == "") {
                    continue;
                }


                if (!europeanCountries.contains(values[7]) || !europeanCountries.contains(values[8])) {
                    continue;
                }

                if (Integer.parseInt(values[5]) > Integer.parseInt(values[6])) {
                    System.out.println("line detected with inconsistent dates");
                    continue;
                }

                if (setNodes.add(values[7])) {
                    node = graph.createNode(values[7]);
                    node.setLabel(values[7]);
                    node.setSize(20);

                    attValue = new AttributeValueImpl(attNationality);
                    attValue.setValue(values[7]);
                    node.getAttributeValues().add(attValue);

                    attValue = new AttributeValueImpl(attLattitude);
                    attValue.setValue(CountryCentroids.getMap().get(values[7]).split(",")[0]);
                    node.getAttributeValues().add(attValue);

                    attValue = new AttributeValueImpl(attLongitude);
                    attValue.setValue(CountryCentroids.getMap().get(values[7]).split(",")[1]);
                    node.getAttributeValues().add(attValue);

                    mapNodes.put(values[7], node);
                }

                if (setNodes.add(values[8])) {
                    node = graph.createNode(values[8]);
                    node.setLabel(values[8]);
                    node.setSize(20);
//                    attValue = new AttributeValueImpl(attName);
//                    attValue.setValue(values[1]);
//                    node.getAttributeValues().add(attValue);
                    attValue = new AttributeValueImpl(attNationality);
                    attValue.setValue(values[8]);
                    node.getAttributeValues().add(attValue);

                    attValue = new AttributeValueImpl(attLattitude);
                    System.out.println("values 8: " + values[8]);
                    attValue.setValue(CountryCentroids.getMap().get(values[8]).split(",")[0]);
                    node.getAttributeValues().add(attValue);

                    attValue = new AttributeValueImpl(attLongitude);
                    attValue.setValue(CountryCentroids.getMap().get(values[8]).split(",")[1]);
                    node.getAttributeValues().add(attValue);

                    mapNodes.put(values[7], node);

                }

                //ADDING EDGE
                for (Node n : graph.getNodes()) {
                    if (n.getAttributeValues().get(0).getValue().equals(values[7])) {
                        nodeSource = n;
                    }
                }
                for (Node n : graph.getNodes()) {
                    if (n.getAttributeValues().get(0).getValue().equals(values[8])) {
                        nodeTarget = n;
                    }
                }
                if (!nodeSource.hasEdgeTo(nodeTarget.getId())) {
                    Edge edge = nodeSource.connectTo(nodeTarget);
                    spell = new SpellImpl();
                    spell.setStartValue(Integer.parseInt(values[5]));
                    spell.setEndValue(Integer.parseInt(values[6]));
                    edge.getSpells().add(spell);

                } else {
                    for (Edge edge : graph.getAllEdges()) {
                        if ((edge.getSource().equals(nodeSource) & edge.getTarget().equals(nodeTarget)) || (edge.getTarget().equals(nodeSource) & edge.getSource().equals(nodeTarget))) {
                            spell = new SpellImpl();
                            spell.setStartValue(Integer.parseInt(values[5]));
                            spell.setEndValue(Integer.parseInt(values[6]));
                            edge.getSpells().add(spell);
                        }
                    }
                }
            }

            // SETTING UP A DYNAMIC ATTRIBUTE FOR NODE:
            // NUMBER OF SELF LOOPS IN THE GIVEN YEAR
            Map<String, Map<Integer, Integer>> selfLoops = new TreeMap();
            transactionsCsv = new CsvReader(new BufferedReader(new FileReader(pathFile + nameFile)), fielddelimiter);
            transactionsCsv.setTextQualifier(textdelimiter);
            transactionsCsv.setUseTextQualifier(true);
            readHeaders = transactionsCsv.readHeaders();
            headers = transactionsCsv.getHeaders();

            while (transactionsCsv.readRecord()) {
                String[] values = transactionsCsv.getValues();
                if (values[2] == "" || values[3] == "") {
                    continue;
                }

                if (Integer.parseInt(values[5]) > Integer.parseInt(values[6])) {
                    System.out.println("line detected with inconsistent dates");
                    continue;
                }

                if (!europeanCountries.contains(values[7]) || !europeanCountries.contains(values[8])) {
                    continue;
                }


                if (values[7].equals(values[8])) {
                    for (int i = Integer.parseInt(values[5]); i <= Integer.parseInt(values[6]); i++) {
                        if (!selfLoops.containsKey(values[7])) {
                            Map<Integer, Integer> yearCount = new HashMap();
                            yearCount.put(i, 1);
                            selfLoops.put(values[7], yearCount);
                        } else {
                            Map<Integer, Integer> yearCount = selfLoops.get(values[7]);
                            if (!yearCount.containsKey(i)) {
                                yearCount.put(i, 1);
                            } else {
                                yearCount.put(i, yearCount.get(i) + 1);
                            }
                            selfLoops.put(values[7], yearCount);
                        }
                    }
                }
            }

            for (Node n : graph.getNodes()) {
                System.out.println("node: " + n.getLabel());
                Map<Integer, Integer> yearCount = selfLoops.get(n.getLabel());
                if (yearCount == null) {
                    yearCount = new HashMap();
                }
                for (int year = firstYear; year <= lastYear; year++) {
                    attValue = new AttributeValueImpl(attSelfLoops);
                    if (yearCount.containsKey(year)) {
                        attValue.setValue(String.valueOf(yearCount.get(year)));
                        attValue.setStartValue(year);
                        attValue.setEndValue(year);
                        n.getAttributeValues().add(attValue);
                    } else {
                        attValue.setValue("0");
                        attValue.setStartValue(year);
                        attValue.setEndValue(year);
                        n.getAttributeValues().add(attValue);
                    }
                }
            }

            // SETTING UP A DYNAMIC ATTRIBUTE FOR EDGE:
            // NUMBER OF CONNECTIONS IN A GIVEN YEAR (WITHOUT SELF LOOPS)
            Map<String, Map<Integer, Integer>> connections = new TreeMap();
            transactionsCsv = new CsvReader(new BufferedReader(new FileReader(pathFile + nameFile)), fielddelimiter);
            transactionsCsv.setTextQualifier(textdelimiter);
            transactionsCsv.setUseTextQualifier(true);
            readHeaders = transactionsCsv.readHeaders();
            headers = transactionsCsv.getHeaders();
            String currPair = "";

            while (transactionsCsv.readRecord()) {
                String[] values = transactionsCsv.getValues();
                if (values[2] == "" || values[3] == "") {
                    continue;
                }

                if (!europeanCountries.contains(values[7]) || !europeanCountries.contains(values[8])) {
                    continue;
                }


                if (Integer.parseInt(values[5]) > Integer.parseInt(values[6])) {
                    System.out.println("line detected with inconsistent dates");
                    continue;
                }

                if (values[7].equals(values[8])) {
                    continue;
                } else {
                    if (values[7].compareTo(values[8]) < 0) {
                        currPair = values[7] + values[8];
                    } else {
                        currPair = values[8] + values[7];
                    }
                    for (int i = Integer.parseInt(values[5]); i <= Integer.parseInt(values[6]); i++) {
                        if (!connections.containsKey(currPair)) {
                            Map<Integer, Integer> yearCount = new HashMap();
                            yearCount.put(i, 1);
                            connections.put(currPair, yearCount);
                        } else {
                            Map<Integer, Integer> yearCount = connections.get(currPair);
                            if (!yearCount.containsKey(i)) {
                                yearCount.put(i, 1);
                            } else {
                                yearCount.put(i, yearCount.get(i) + 1);
                            }
                            connections.put(values[7], yearCount);
                        }
                    }
                }
            }

            for (Edge e : graph.getAllEdges()) {
                if (e.getSource().getLabel().equals(e.getTarget().getLabel())) {
                    continue;
                }
                if (e.getSource().getLabel().compareTo(e.getTarget().getLabel()) < 0) {
                    currPair = e.getSource().getLabel() + e.getTarget().getLabel();
                } else {
                    currPair = e.getTarget().getLabel() + e.getSource().getLabel();
                }

                Map<Integer, Integer> yearCount = connections.get(currPair);
                if (yearCount == null) {
                    yearCount = new HashMap();
                }
                for (int year = firstYear; year <= lastYear; year++) {
                    attValue = new AttributeValueImpl(attWeightDynamic);
                    if (yearCount.containsKey(year)) {
                        attValue.setValue(String.valueOf(yearCount.get(year)));
                        attValue.setStartValue(year);
                        attValue.setEndValue(year);
                        e.getAttributeValues().add(attValue);
                    } else {
                        attValue.setValue("0");
                        attValue.setStartValue(year);
                        attValue.setEndValue(year);
                        e.getAttributeValues().add(attValue);
                    }
                }
            }


            DynamicDegreeCalculation calc = new DynamicDegreeCalculation();
            calc.compute(graph);
            

//WRITE THE GEXF FILE
//            StaxGraphWriter graphWriter = new StaxGraphWriter();
//            File f = new File("dynamic_graph_sample_26_11_2013.gexf");
//            Writer out;
//
//            out = new FileWriter(f, false);
//            graphWriter.writeToStream(gexf, out, "UTF-8");
//            System.out.println(f.getAbsolutePath());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CartelDynamic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CartelDynamic.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

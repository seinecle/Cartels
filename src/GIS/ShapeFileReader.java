/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GIS;

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
import it.uniroma1.dis.wsngroup.gexf4j.core.dynamic.TimeFormat;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.GexfImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.StaxGraphWriter;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeListImpl;
import it.uniroma1.dis.wsngroup.gexf4j.core.impl.data.AttributeValueImpl;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.geometry.Geometry;

/*
 Copyright 2008-2013 Clement Levallois
 Authors : Clement Levallois <clementlevallois@gmail.com>
 Website : http://www.clementlevallois.net


 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2013 Clement Levallois. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s): Clement Levallois

 */
public class ShapeFileReader {

    public static void main(String args[]) throws IOException {
        new ShapeFileReader().read();
    }

    public void read() throws IOException {

        //set up the gexf object and its graph object
        Gexf gexf = new GexfImpl();
        Calendar date = Calendar.getInstance();

        gexf.getMetadata()
                .setLastModified(date.getTime())
                .setCreator("Clement Levallois - www.clementlevallois.net")
                .setDescription("A map background");
        gexf.setVisualization(true);

        Graph graph = gexf.getGraph();
        graph.setDefaultEdgeType(EdgeType.UNDIRECTED).setMode(Mode.STATIC);

        AttributeList attrListNodeStatic = new AttributeListImpl(AttributeClass.NODE);
        graph.getAttributeLists().add(attrListNodeStatic);

        Attribute attLatitude = attrListNodeStatic.createAttribute("1", AttributeType.DOUBLE, "latitude");
        Attribute attLongitude = attrListNodeStatic.createAttribute("2", AttributeType.DOUBLE, "longitude");


        File file = new File("D:\\Docs Pro Clement\\E-humanities\\Datasets\\GIS\\Country contours\\TM_WORLD_BORDERS_SIMPL-0.3\\TM_WORLD_BORDERS_SIMPL-0.3.shp");
//        File file = new File("D:\\Docs Pro Clement\\E-humanities\\Datasets\\GIS\\Country contours\\ne_110m_admin_0_boundary_lines_land.shp");

        try {
            Map connect = new HashMap();
            connect.put("url", file.toURI().toURL());

            DataStore dataStore = DataStoreFinder.getDataStore(connect);
            String[] typeNames = dataStore.getTypeNames();
            String typeName = typeNames[0];

            System.out.println("Reading content " + typeName);

            FeatureSource featureSource = dataStore.getFeatureSource(typeName);
            FeatureCollection collection = featureSource.getFeatures();
            FeatureIterator iterator = collection.features();
            Integer indexNode = -1;
            Node node;
            AttributeValue attValue;

            BufferedWriter bw = new BufferedWriter(new FileWriter("D:/coordinates.txt"));
            List<String> countries = new ArrayList();


            try {
                while (iterator.hasNext()) {
                    Feature feature = iterator.next();
                    List<Property> collProperties = (List) feature.getProperties();
                    boolean skip = false;
                    String country = "";
                    String subregion = "";
                    String region = "";
                    for (Property p : collProperties) {
                        if (p.getName().toString().equals("NAME")) {
                            country = p.getValue().toString();
                            countries.add(country);
                        }
                        if (p.getName().toString().equals("REGION")) {
                            region = p.getValue().toString();
                        }
                        if (p.getName().toString().equals("SUBREGION")) {
                            subregion = p.getValue().toString();
                        }
                    }
                    if (!skip) {
                        bw.write("country:" + country + "\t");
                        bw.write("region:" + region + "\t");
                        bw.write("subregion:" + subregion + "\t");
                        for (Property p : collProperties) {

                            System.out.println("property: " + p.getName() + ", value: " + p.getValue().toString());
                            if (p.getName().toString().equals("the_geom") && p.getValue().toString().contains("MULTILINESTRING")) {
                                String toParse = p.getValue().toString();
                                toParse = toParse.replace("MULTILINESTRING ((", "");
                                toParse = toParse.replace("))", "");
                                String[] coord = toParse.split(",");

                                int indexPoint = 0;
                                Node nodeStart = null;
                                Node nodePrevious = null;
                                for (String point : coord) {
                                    point = point.trim();
                                    indexPoint++;
                                    node = graph.createNode(String.valueOf(indexNode++));
                                    node.setLabel(String.valueOf(indexNode));
                                    node.setSize(0);

                                    attValue = new AttributeValueImpl(attLatitude);
                                    attValue.setValue(point.split(" ")[0]);
                                    node.getAttributeValues().add(attValue);


                                    attValue = new AttributeValueImpl(attLongitude);
                                    attValue.setValue(point.split(" ")[1]);
                                    node.getAttributeValues().add(attValue);

                                    bw.write("node" + indexNode + ":" + point + "\t");

                                    if (indexPoint == 1) {
                                        nodeStart = node;
                                    }
                                    if (indexPoint > 1) {
                                        node.connectTo(nodePrevious);
                                        bw.write("edge:" + node.getId() + " " + nodePrevious.getId() + "\t");

                                    }
                                    if (indexPoint == coord.length) {
                                        node.connectTo(nodeStart);
                                        bw.write("edge:" + node.getId() + " " + nodeStart.getId() + "\t");
                                    }
                                    nodePrevious = node;
                                }
                            }
                            if (p.getName().toString().equals("the_geom") & p.getValue().toString().contains("MULTIPOLYGON")) {
                                String toParse = p.getValue().toString();
//                                if (toParse.contains("25.3166675")){
//                                    System.out.println("found!");
//                                }
                                toParse = toParse.replace("MULTIPOLYGON (((", "((");
                                toParse = toParse.replace(")))", "))");
                                String[] polygons = toParse.split("\\)+, \\(+");

                                for (String polygon : polygons) {

                                    toParse = polygon.replace("((", "");
                                    toParse = toParse.replace("))", "");
                                    String[] coord = toParse.split(",");

                                    int indexPoint = 0;
                                    Node nodeStart = null;
                                    Node nodePrevious = null;
                                    for (String point : coord) {
                                        point = point.trim();
                                        indexPoint++;
                                        indexNode++;
                                        node = graph.createNode(String.valueOf(indexNode));
                                        node.setLabel(String.valueOf(indexNode));
                                        node.setSize(0);

                                        attValue = new AttributeValueImpl(attLatitude);
                                        attValue.setValue(point.split(" ")[1]);
                                        node.getAttributeValues().add(attValue);

                                        attValue = new AttributeValueImpl(attLongitude);
                                        attValue.setValue(point.split(" ")[0]);
                                        node.getAttributeValues().add(attValue);
                                        bw.write("node" + indexNode + ":" + point + "\t");

                                        if (indexPoint == 1) {
                                            nodeStart = node;
                                        }
                                        if (indexPoint > 1) {
                                            node.connectTo(nodePrevious);
                                            bw.write("edge:" + node.getId() + " " + nodePrevious.getId() + "\t");
                                        }
                                        if (indexPoint == coord.length) {
                                            node.connectTo(nodeStart);
                                            bw.write("edge:" + node.getId() + " " + nodeStart.getId() + "\t");
                                        }
                                        nodePrevious = node;
                                    }
                                }
                            }
                        }
                        bw.write("\n");
                    }
                }
                bw.close();
            } finally {
                iterator.close();
            }

            Collections.sort(countries);
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for (String country : countries) {
                sb.append("\"").append(country).append("\"").append(",");
            }
            System.out.println("countries: " + sb.toString());



        } catch (Throwable e) {
            System.out.println("e: " + e.getMessage());
        }
        //WRITE THE GEXF FILE
        StaxGraphWriter graphWriter = new StaxGraphWriter();
        File f = new File("D:/world map.gexf");
        Writer out;
        out = new FileWriter(f, false);

        graphWriter.writeToStream(gexf, out,
                "UTF-8");
        System.out.println(f.getAbsolutePath());

    }
}

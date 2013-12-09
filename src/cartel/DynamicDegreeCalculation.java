/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cartel;

import it.uniroma1.dis.wsngroup.gexf4j.core.Edge;
import it.uniroma1.dis.wsngroup.gexf4j.core.Graph;
import it.uniroma1.dis.wsngroup.gexf4j.core.Node;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeValue;
import it.uniroma1.dis.wsngroup.gexf4j.core.data.AttributeValueList;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
public class DynamicDegreeCalculation {

    public void compute(Graph graph) throws FileNotFoundException, IOException {

        Map<String, Integer> weightedDegreeForOneCountryfForOneYear;

        Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("weighted degree");
        Row row;
        Cell cell;
        Map<String, Integer> countryIndices = new TreeMap();

        int index = 0;
        for (String country : CartelDynamic.europeanCountries) {
            index++;
            countryIndices.put(country, index);
        }

        //COLUMNS HEADER
        row = sheet.createRow((short) 0);
        index = 1;
        for (int i = 1948; i < 2009; i++) {
            cell = row.createCell(index);
            index++;
            cell.setCellValue(String.valueOf(i));
        }

        //CREATING EMPTY CELLS FOR EACH ROW
        for (String country : countryIndices.keySet()) {
            row = sheet.createRow((countryIndices.get(country)));
            index = 0;
            for (int i = 1948; i <= 2009; i++) {
                row.createCell(index);
                index++;
            }
        }


        //FILLING FIRST COLUMN WITH COUNTRIES
        for (String country : countryIndices.keySet()) {
            row = sheet.getRow(countryIndices.get(country));
            row.getCell(0).setCellValue(country);
        }



        int indexYear = 1;
        for (int i = 1948; i < 2009; i++) {
            weightedDegreeForOneCountryfForOneYear = new TreeMap();
            for (Node node : graph.getNodes()) {
                String nodeLabel = node.getLabel();
                int sumDegrees = 0;

                for (Edge edge : graph.getAllEdges()) {
                    if (!edge.getSource().getLabel().equals(nodeLabel) & !edge.getTarget().getLabel().equals(nodeLabel)) {
                        continue;
                    }
                    if (edge.getSource().getLabel().equals(edge.getTarget().getLabel())) {
                        continue;
                    }
                    AttributeValueList attributeValueList = edge.getAttributeValues();
                    for (AttributeValue attributeValue : attributeValueList) {
                        if (!attributeValue.getAttribute().getTitle().equals("freq")) {
                            continue;
                        }

                        if (((Integer) attributeValue.getStartValue()) != i) {
                            continue;
                        }
                        sumDegrees = sumDegrees + Integer.parseInt(attributeValue.getValue());
                    }
                }
                sumDegrees = sumDegrees/2;
                row = sheet.getRow(countryIndices.get(nodeLabel));
                cell = row.getCell(indexYear);
                cell.setCellValue(String.valueOf(sumDegrees));


            }
            indexYear++;
        }
        String pathFile = "D:/workbook weighted degree.xlsx";
        FileOutputStream fileOut = new FileOutputStream(pathFile);

        wb.write(fileOut);

        fileOut.close();

    }
}

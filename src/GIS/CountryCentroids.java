/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GIS;

import java.util.HashMap;
import java.util.Map;

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
public class CountryCentroids {

    static Map<String, String> map = new HashMap();

    public static void init() {
        map.put("French", "46,2");
        map.put("Dutch", "52.5,5.75");
        map.put("Italian", "42.8333,12.8333");
        map.put("Belgian", "50.8333,4");
        map.put("British", "54,-2");
        map.put("American", "38,-97");
        map.put("German", "51,9");
        map.put("Austrian", "47.3333,13.3333");
        map.put("Swedish", "62,15");
        map.put("Russian", "60,100");
        map.put("Polish", "52,20");
        map.put("Hungarian", "47,20");
        map.put("Slovakian", "48.6667,19.5");
        map.put("Swiss", "47,8");
        map.put("Norwegian", "62,10");
        map.put("Canadian", "60,-95");
        map.put("Finnish", "64,26");
        map.put("Portugese", "39.5,-8");
        map.put("Spanish", "40,-4");
        map.put("Japanese", "36,138");
        map.put("Luxembourgian", "49.75,6.1667");
        map.put("Irish", "53,-8");
        map.put("Korean", "37,127.5");
        map.put("Mexican", "23,-102");
        map.put("Danish", "56,10");
        map.put("Greek", "39,22");
        map.put("Croatian", "45.1667,15.5");
        map.put("Malaysian", "2.5,112.5");
        map.put("Singaporean", "1.3667,103.8");
        map.put("Chinese", "35,105");
        map.put("Hong Kong", "22.25,114.1667");
        map.put("South African", "-29,24");
        map.put("Kuwaiti", "29.3375,47.6581");
        map.put("Czech", "49.75,15.5");
        map.put("Tunisian", "34,9");
        map.put("Slovenian", "46,15");
        map.put("Taiwanese", "23.5,121");
        map.put("Brazilian", "-10,-55");
    }

    public static Map<String, String> getMap() {
        return map;
    }

}

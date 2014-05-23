/* 
 * Copyright 2012 Devoteam http://www.devoteam.com
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * 
 * This file is part of Multi-Protocol Test Suite (MTS).
 * 
 * Multi-Protocol Test Suite (MTS) is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License.
 * 
 * Multi-Protocol Test Suite (MTS) is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Multi-Protocol Test Suite (MTS).
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.devoteam.srit.xmlloader.core.coding.q931;

import com.devoteam.srit.xmlloader.core.utils.Utils;
import java.util.LinkedHashMap;
import java.util.List;
import org.dom4j.Element;

/**
 *
 * @author indiaye
 */
public class Dictionary {
	
    private LinkedHashMap<String,ElementInformationQ931> mapElementByName=new LinkedHashMap<String, ElementInformationQ931>();
    private LinkedHashMap<Integer,ElementInformationQ931> mapElementById=new LinkedHashMap<Integer, ElementInformationQ931>();
    private LinkedHashMap<String,Field> mapHeader= new LinkedHashMap<String, Field>();
    
    public Dictionary(Element root) throws Exception {
        
        List<Element> listElem=root.element("headerQ931").elements("field");
        for (Element element : listElem) {
            mapHeader.put(element.attributeValue("name"), new EnumerationField(element, null, this));
        }
        List<Element> list=root.elements("element");
        for (Element elem : list) {
            ElementInformationQ931 elemInf = new ElementInformationQ931(elem, this);
            mapElementByName.put(elem.attributeValue("name"), elemInf);
            mapElementById.put((int)(Utils.parseBinaryString(elem.attributeValue("identifier"))[0] & 0xff), elemInf);
        }

    }
    public LinkedHashMap<Integer, ElementInformationQ931> getMapElementById() {
        return mapElementById;
    }

    public LinkedHashMap<String, ElementInformationQ931> getMapElementByName() {
        return mapElementByName;
    }

    public LinkedHashMap<String, Field> getMapHeader() {
        return mapHeader;
    }

}
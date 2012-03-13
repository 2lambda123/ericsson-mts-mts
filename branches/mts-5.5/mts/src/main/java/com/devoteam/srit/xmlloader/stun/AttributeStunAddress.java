/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.devoteam.srit.xmlloader.stun;

import com.devoteam.srit.xmlloader.core.Parameter;
import gp.utils.arrays.*;

/**
 *
 * @author indiaye
 */
public class AttributeStunAddress extends AttributeStun {

    Integer08Array family = new Integer08Array(0);
    Integer16Array port = new Integer16Array(0);
    IPv4Array ipAdress = new IPv4Array(new DefaultArray(4));

    public AttributeStunAddress(Array type, int family, int port, String ipAdress) {
        super(type);
        setAdress(family, port, ipAdress);
    }

    public AttributeStunAddress(Array data) {
        super(data.subArray(0,2));
        this.length = new Integer16Array(data.subArray(2, 2));
        this.family = new Integer08Array(data.subArray(5, 1));
        this.port = new Integer16Array(data.subArray(6, 2));
        this.ipAdress = new IPv4Array(data.subArray(8, 4));
    }

    public void setAdress(int family, int port, String ipAddress) {
        this.family.setValue(family);
        this.port.setValue(port);
        this.ipAdress.setValue(ipAddress);
        this.length.setValue(this.family.length + this.port.length + this.ipAdress.length+1);
    }

    @Override
    public Array getValue() {
        SupArray array = new SupArray();
        array.addLast(new DefaultArray(1));
        array.addLast(family);
        array.addLast(port);
        array.addLast(ipAdress);
        return array;
    }

    public String getStringAttribute() {
        StringBuilder addressString = new StringBuilder();
        addressString.append("<address ");
        addressString.append("family=\"" + this.family.getValue() + "\",");
        addressString.append("port=\"" + this.port.getValue() + "\",");
        addressString.append("addressIP=\"" + this.ipAdress.getValue() + "\"/>");
        return addressString.toString();
    }

    public Parameter getParameterAtt(String param) {
        Parameter var = new Parameter();
        if (param.equalsIgnoreCase("family")) {
            var.add(this.family.getValue());
        }
        if (param.equalsIgnoreCase("port")) {
            var.add(this.port.getValue());
        }
        if (param.equalsIgnoreCase("addressIP")) {
            var.add(this.ipAdress.getValue());
        }
        return var;
    }
}

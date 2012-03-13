/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.devoteam.srit.xmlloader.gui.conf;

import com.devoteam.srit.xmlloader.core.PropertiesEnhanced;
import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;


/**
 *
 * @author jbor
 */
public class JPanelNumber extends JPanelGeneric implements JPanelConfInterface {

    private JTextField jTextFieldValue;
    private DocumentFilter filter;

    public JPanelNumber(PropertiesEnhanced config, String name, DocumentFilter filter){
        super(config, name);
        this.filter = filter;
        this.jTextFieldValue = new JTextField();
        ((AbstractDocument) this.jTextFieldValue.getDocument()).setDocumentFilter(this.filter);        
        this.add(this.jTextFieldValue);
        this.jTextFieldValue.setEditable(true);
        this.jTextFieldValue.setVisible(true);
        this.validate();
    }

    @Override
    public Component getSpecificComponent() {
        return jTextFieldValue;
    }   

    @Override
    public void resetSpecific() {
        this.newPropertiesParameterStructure = this.propertiesParameterStructure.clone();
        refresh();
        this.jTextFieldValue.setCaretPosition(0);
    }

    @Override
    public void save() {
        propertiesParameterStructure.setLocaleValue(newPropertiesParameterStructure.getLocaleValue());
        refresh();
    }

    @Override
    public void refreshSpecific() {
        String locale = this.newPropertiesParameterStructure.getLocaleValue();
        String globale = this.newPropertiesParameterStructure.getDefaultValue();

        jTextFieldValue.getDocument().removeDocumentListener(this);
        ((AbstractDocument) this.jTextFieldValue.getDocument()).setDocumentFilter(null);
        if(null != locale){
            if (!locale.equals(this.jTextFieldValue.getText())){
                this.jTextFieldValue.setText(locale);
            }
        }
        else{
            if (!globale.equals(this.jTextFieldValue.getText())){
                this.jTextFieldValue.setText(globale);
            }
        }
        ((AbstractDocument) this.jTextFieldValue.getDocument()).setDocumentFilter(this.filter);
        jTextFieldValue.getDocument().addDocumentListener(this);
    }

    public void touchedSpecific(){
        newPropertiesParameterStructure.setLocaleValue(jTextFieldValue.getText());
        refresh();
    }

    @Override
    public void initSpecific() {
    }

    @Override
    public boolean isDisplayedValueTheInitOne(){
        String oldValue = this.newPropertiesParameterStructure.getLocaleValue();
        String localeNew = this.jTextFieldValue.getText();
        if (oldValue == null){
            oldValue = this.newPropertiesParameterStructure.getDefaultValue();
        }
        return localeNew.equals(oldValue);
    }
}
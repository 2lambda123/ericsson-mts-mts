/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.devoteam.srit.xmlloader.core.utils;

/**
 *
 * @author gpasquiers
 */
public class ConfigCache {
    private String configName;
    private String parameterName;

    private String  stringValue;
    private boolean booleanValue;
    private int     intValue;
    private long    longValue;
    private double  doubleValue;

    private boolean stringIsInit;
    private boolean booleanIsInit;
    private boolean intIsInit;
    private boolean longIsInit;
    private boolean doubleIsInit;

    private long lastAccess;
    private long timeout;

    public ConfigCache(String configName, String parameterName){
        this(configName, parameterName, 1000);
    }

    public ConfigCache(String configName, String parameterName, long timeout){
        if(null == configName || null == parameterName){
            throw new RuntimeException("ConfigCache: configName nor parameterName can be null");
        }

        this.configName = configName;
        this.parameterName = parameterName;
        this.timeout = timeout;

        stringIsInit = false;
        booleanIsInit = false;
        intIsInit = false;
        longIsInit = false;
        doubleIsInit = false;

        lastAccess = 0;
    }

    private boolean shouldRefresh(){
        long now = System.currentTimeMillis();
        if(lastAccess < System.currentTimeMillis() - timeout
                || lastAccess < Config.getLastReset()){
            lastAccess = now;
            return true;
        }
        else{
            return false;
        }
    }

    public String getStringValue(String defaultValue){
        if(!stringIsInit || shouldRefresh()){
            stringIsInit = true;
            stringValue = Config.getConfigByName(configName).getString(parameterName, defaultValue);
        }
        return stringValue;
    }

    public String getStringValue() throws Exception{
        if(!stringIsInit || shouldRefresh()){
            stringIsInit = true;
            stringValue = Config.getConfigByName(configName).getString(parameterName);
        }
        return stringValue;
    }

    public boolean getBooleanValue(boolean defaultValue){
        if(!booleanIsInit || shouldRefresh()){
            booleanIsInit = true;
            booleanValue = Config.getConfigByName(configName).getBoolean(parameterName, defaultValue);
        }
        return booleanValue;
    }

    public boolean getBooleanValue() throws Exception{
        if(!booleanIsInit || shouldRefresh()){
            booleanIsInit = true;
            booleanValue = Config.getConfigByName(configName).getBoolean(parameterName);
        }
        return booleanValue;
    }

    public int getIntegerValue(int defaultValue){
        if(!intIsInit || shouldRefresh()){
            intIsInit = true;
            intValue = Config.getConfigByName(configName).getInteger(parameterName, defaultValue);
        }
        return intValue;
    }

    public int getIntegerValue() throws Exception{
        if(!intIsInit || shouldRefresh()){
            intIsInit = true;
            intValue = Config.getConfigByName(configName).getInteger(parameterName);
        }
        return intValue;
    }

    public long getLongValue(long defaultValue){
        if(!longIsInit || shouldRefresh()){
            longIsInit = true;
            longValue = Config.getConfigByName(configName).getLong(parameterName, defaultValue);
        }
        return longValue;
    }

    public long getLongValue() throws Exception{
        if(!longIsInit || shouldRefresh()){
            longIsInit = true;
            longValue = Config.getConfigByName(configName).getLong(parameterName);
        }
        return longValue;
    }

    public double getDoubleValue(double defaultValue){
        if(!doubleIsInit || shouldRefresh()){
            doubleIsInit = true;
            doubleValue = Config.getConfigByName(configName).getDouble(parameterName, defaultValue);
        }
        return doubleValue;
    }

    public double getDoubleValue() throws Exception{
        if(!doubleIsInit || shouldRefresh()){
            doubleIsInit = true;
            doubleValue = Config.getConfigByName(configName).getDouble(parameterName);
        }
        return doubleValue;
    }
}

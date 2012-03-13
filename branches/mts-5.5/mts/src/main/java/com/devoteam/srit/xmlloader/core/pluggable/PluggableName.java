/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.devoteam.srit.xmlloader.core.pluggable;

import java.io.Serializable;


/**
 *
 * @author gpasquiers
 */
public class PluggableName implements Serializable
{
    /**
     * String containing the name of the component
     */
    private String name;
    
    /**
     * String telling if and by what this this name is deprecated. Should be
     * used to warn the user about the imminent removal of this component.
     * set it to null for non-deprecated components.
     */
    private String deprecatedBy;
    
    /**
     * Priority of this naming. This is used to resolve potential conflicts
     * between two components named the same. for example, if a user wants
     * to redifine a component of the tool.
     * The priority takes value between Integer.MIN_VALUE to Integer.MAX_VALUE.
     * A greater priority value will averride a lower one.
     */
    private int priority;
    
    /**
     * Conveniance constructor.
     * - deprecated = false
     * - priority = Integer.MIN_VALUE
     * @param name
     */
    public PluggableName(String name)
    {
        this(name, Integer.MIN_VALUE, null);
    }

    /**
     * Conveniance constructor.
     * - deprecated = false
     * @param name
     * @param priority
     */
    public PluggableName(String name, int priority)
    {
        this(name, priority, null);
    }

    /**
     * Conveniance constructor
     * - priority = Integer.MIN_VALUE
     * @param name
     * @param deprecated
     */
    public PluggableName(String name, String deprecatedBy)
    {
        this(name, Integer.MIN_VALUE, deprecatedBy);
    }
    
    /**
     * Complete constructor.
     * @param name
     * @param priority
     * @param deprecated
     */
    public PluggableName(String name, int priority, String deprecatedBy)
    {
        if(null == name)
        {
            throw new IllegalArgumentException("name argument cannot be null");
        }
        
        this.name = name.toLowerCase();
        this.priority = priority;
        this.deprecatedBy = deprecatedBy;
    }
    
    /**
     * Returns true if this PluggableName has priority over the other.
     * @param other
     * @return
     */
    public boolean hasPriority(PluggableName other)
    {
        return this.priority > other.priority;
    }
    
    /**
     * Returns the deprecated status of this PluggableName.
     * @return
     */
    public boolean isDeprecated()
    {
        return this.deprecatedBy != null;
    }


    /**
     * Returns what is deprecating this component.
     * @return
     */
    public String deprecatedBy()
    {
        return this.deprecatedBy;
    }
    
    /**
     * Returns the priority value of this PluggableName.
     * @return
     */
    public int getPriority()
    {
        return this.priority;
    }
    
    /**
     * Returns the name of this PluggableName.
     * @return
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Generate a decent hashcode for objects of this class.
     * @return
     */
    @Override
    public int hashCode()
    {
        return (this.name + this.deprecatedBy + this.priority).hashCode();
    }

    /**
     * An equals method that makes use of all fields of this class.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final PluggableName other = (PluggableName) obj;
        if (this.name.equals(other.name))
        {
            return false;
        }
        if ((this.deprecatedBy==null) != (other.deprecatedBy==null))
        {
            return false;
        }
        if ((this.deprecatedBy!=null) && (other.deprecatedBy!=null) && !this.deprecatedBy.equals(other.deprecatedBy))
        {
            return false;
        }
        if (this.priority != other.priority)
        {
            return false;
        }
        return true;
    }
    
    /**
     * Adequate toString method.
     * @return
     */
    @Override
    public String toString()
    {
        String result = "[" + this.name + ", priority=" + this.priority;
        if(this.isDeprecated()) result += ", DEPRECATED";
        result +="]";
        return result;
    }
    
}

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

package com.devoteam.srit.xmlloader.sctp;

import gp.utils.arrays.Array;
import gp.utils.arrays.DefaultArray;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Element;

import com.devoteam.srit.xmlloader.core.Parameter;
import com.devoteam.srit.xmlloader.core.Runner;
import com.devoteam.srit.xmlloader.core.log.GlobalLogger;
import com.devoteam.srit.xmlloader.core.log.TextEvent;
import com.devoteam.srit.xmlloader.core.protocol.*;
import com.devoteam.srit.xmlloader.core.utils.Config;
import com.devoteam.srit.xmlloader.core.utils.Utils;

public abstract class MsgSctp extends Msg{
	
	private enum DataType
	{
		text,
		binary
	}
    
    /** Creates a new instance */
	public MsgSctp(Stack stack) throws Exception{
		super(stack);	
	}

	/**
	 * 
	 * @return the associated DataSctp
	 */
	public abstract DataSctp getDataSctp();

    /** Get the protocol of this message */
    @Override
	public String getProtocol() {
		return StackFactory.PROTOCOL_SCTP;  
	}

    /** 
     * Return true if the message is a request else return false
     */
	@Override
	public boolean isRequest() 
	{
		return true;
	}

    /** 
     * Get the type of the message
     * Used for message filtering with "type" attribute and for statistic counters 
     */
	@Override
	public String getType() 
	{
		return this.type;
	}
    
	/** Set the type of this message */
    public void setType(String type)
    {
        this.type = type;
    }    

    /** 
     * Get the result of the message (null if request)
     * Used for message filtering with "result" attribute and for statistic counters 
     */
	@Override
	public String getResult() throws Exception 
	{
		return null;
	}

	
    //-------------------------------------------------
    // methods for the encoding / decoding of the message
    //-------------------------------------------------

    /** 
     * encode the message to binary data 
     */
    @Override
    public byte[] encode() throws Exception
    {
    	DataSctp dataSctp = this.getDataSctp();
        return dataSctp.getData();
    }

    /** 
     * decode the message from binary data 
     */
    @Override
    public void decode(byte[] data) throws Exception
    {
    	DataSctp dataSctp = this.getDataSctp();
    	dataSctp.setData(data);
    }

    
    //---------------------------------------------------------------------
    // methods for the XML display / parsing of the message
    //---------------------------------------------------------------------

    /** Returns a short description of the message. Used for logging as INFO level */
    /** This methods HAS TO be quick to execute for performance reason */
    @Override
	public String toShortString() throws Exception 
    {
    	String ret = super.toShortString();

    	ret += "\n";
		try
		{
	    	DataSctp dataSctp = this.getDataSctp();
            ret += Utils.toStringBinary(dataSctp.getData(), Math.min(dataSctp.getLength(), 100));
		}
		catch(Exception e)
		{
            GlobalLogger.instance().getApplicationLogger().warn(TextEvent.Topic.PROTOCOL, e, "An error occured while logging the SCTP message : ", ret);
        	e.printStackTrace();
		}
		return ret;
	}

    /** 
     * Convert the message to XML document 
     */
    @Override
    public String toXml() throws Exception 
    {
		String xml = getTypeComplete();
		xml += "\n"; 

    	DataSctp dataSctp = this.getDataSctp();
    	InfoSctp sndrcvinfo = dataSctp.getInfo();
    	if (sndrcvinfo!=null)
		{
			xml += "<sctp ";
			int stream = sndrcvinfo.getStreamId() & 0xffff;
			xml += "stream=\"" + stream + "\", ";
			int ssn = sndrcvinfo.getSsn() & 0xffff;
			xml += "ssn=\"" + ssn + "\", ";	
			long ppid = ((long) Utils.convertLittleBigIndian(sndrcvinfo.getPpid())) & 0xffffffffl;
			xml += "ppid=\"" + ppid + "\", ";
			xml += "flags=\"" + sndrcvinfo.getFlags() + "\", ";
			long context = ((long) sndrcvinfo.getContext()) & 0xffffffffl;
			xml += "context=\"" + context + "\", ";
			long timetolive = ((long) sndrcvinfo.getTtl()) & 0xffffffffl;
			xml += "ttl=\"" + timetolive + "\", ";
			long tsn = ((long) Utils.convertLittleBigIndian(sndrcvinfo.getTsn())) & 0xffffffffl;
			xml += "tsn=\"" + tsn + "\", ";
			long cumtsn = ((long) Utils.convertLittleBigIndian(sndrcvinfo.getCumtsn())) & 0xffffffffl;
			xml += "cumtsn=\"" + cumtsn + "\", ";
			long aid = ((long) sndrcvinfo.getAssociation().getId()) & 0xffffffffl;			
			xml += "aid=\"" + aid + "\"/>\n";
			
			xml += toXml_SubElements();
			
			/*
			ChannelSctp channelSctp = (ChannelSctp) getChannel();
			if (channelSctp != null)
			{
				SocketSctp socketSctp = channelSctp.getSocketSctp();
				if (socketSctp != null)
				{
					SCTPSocket sctpSocket = socketSctp.getSctpSocket();
					if (sctpSocket != null)
					{
						AssociationId assoId = sctpData.sndrcvinfo.sinfo_assoc_id;
						if (assoId != null && assoId.hashCode() != 0)
						{
							//System.out.println("assoId" + assoId);
							int port= sctpSocket.getPeerInetPort(assoId);
							//int port=  0;
							Collection<InetAddress> col = sctpSocket.getPeerInetAddresses(assoId);
							if (col != null)
							{
								for (InetAddress ia : col)
								{	
									xml += "    <peer ";
									xml += "address=\"" + ia.getHostAddress() + "\" ";
									xml += "port=\"" + port + "\" ";
									xml += "/>\n";
								}
							}
						}
					}
				}	
			}
			*/
			xml += "</sctp>\n";
		}
		xml += Utils.byteTabToString(dataSctp.getData());
        return xml;

    }
    
    /** 
     * Convert the message sub elements to XML document
     * @see toXml
     */
    protected String toXml_SubElements() throws Exception 
    {
    	return "";
    }

    /** 
     * Parse the message from XML element 
     */
    @Override
    public void parseFromXml(Boolean request, Element root, Runner runner) throws Exception
    {
		DataSctp dataSctp = this.getDataSctp();
		dataSctp.clear();
    	this.setType("DATA");
    	 
		@SuppressWarnings("unchecked")
		List<Element> xmlData = root.elements("data");
		List<byte[]> datas = new LinkedList<byte[]>();
		for(Element element:xmlData)
		{
			switch(DataType.valueOf(element.attributeValue("format")))
			{
			case text:
			{
				String text = element.getText();
                // change the \n caractère to \r\n caracteres because the dom librairy return only \n.
                // this could make some trouble when the length is calculated in the scenario
                text = text.replace("\r\n","\n");                    
                text = text.replace("\n","\r\n");                    					
				datas.add(text.getBytes("UTF8"));
				break;
			}
			case binary:
			{
				String text = element.getTextTrim();
				datas.add(Utils.parseBinaryString(text));
			}
			}
		}

		//
		// Compute total length
		//
		int length = 0;
		for(byte[] data:datas)
		{
			length += data.length;
		}

		byte[] data = new byte[length];

		int i=0;
		for(byte[] aData:datas)
		{
			for(int j=0; j<aData.length; j++)
			{
				data[i] = aData[j];
				i++;
			}
		}	

		dataSctp.setData(data);

		// initialize from the configuration file		
        Config stackConfig = StackFactory.getStack(StackFactory.PROTOCOL_SCTP).getConfig();

		// Parse the XML file
		@SuppressWarnings("unchecked")
		List<Element> sctpElements = root.elements("sctp");

        BasicInfoSctp sndrcvinfo = new BasicInfoSctp();
        sndrcvinfo.setFromStackConfig( stackConfig );
        sndrcvinfo.setFromXml(sctpElements);
		dataSctp.setInfo(sndrcvinfo);

		// log datas
		GlobalLogger.instance().getSessionLogger().debug(TextEvent.Topic.PROTOCOL, "stream =" + sndrcvinfo.getStreamId());
		GlobalLogger.instance().getSessionLogger().debug(TextEvent.Topic.PROTOCOL, "ssn =" + sndrcvinfo.getSsn());
		GlobalLogger.instance().getSessionLogger().debug(TextEvent.Topic.PROTOCOL, "ppid =" + sndrcvinfo.getPpid());
		GlobalLogger.instance().getSessionLogger().debug(TextEvent.Topic.PROTOCOL, "flags =" + sndrcvinfo.getFlags());
		GlobalLogger.instance().getSessionLogger().debug(TextEvent.Topic.PROTOCOL, "context =" + sndrcvinfo.getContext());
		GlobalLogger.instance().getSessionLogger().debug(TextEvent.Topic.PROTOCOL, "ttl =" + sndrcvinfo.getTtl());
		GlobalLogger.instance().getSessionLogger().debug(TextEvent.Topic.PROTOCOL, "tsn =" + sndrcvinfo.getTsn());
		GlobalLogger.instance().getSessionLogger().debug(TextEvent.Topic.PROTOCOL, "cumtsn =" + sndrcvinfo.getCumtsn());			
		GlobalLogger.instance().getSessionLogger().debug(TextEvent.Topic.PROTOCOL, "aid =" + sndrcvinfo.getAssociation().getId() );		
    }

    
    //------------------------------------------------------
    // method for the "setFromMessage" <parameter> operation
    //------------------------------------------------------

    /** 
     * Get a parameter from the message
     */
    //@Nullable
    @Override
    public Parameter getParameter(String path) throws Exception
	{
		Parameter var = super.getParameter(path);
		if (var != null)
		{
			return var;
		}

		var = new Parameter();
        path = path.trim();
        String[] params = Utils.splitPath(path);

        if(params[0].equalsIgnoreCase("sctp")) 
        {
        	DataSctp dataSctp = this.getDataSctp();
        	
        	if (dataSctp != null && dataSctp.getInfo() != null)
        	{
        		InfoSctp sndrcvinfo = dataSctp.getInfo();
        		
	            if(params[1].equalsIgnoreCase("stream")) 
	            {
	            	int stream = sndrcvinfo.getStreamId() & 0xffff;
	            	var.add(Integer.toString(stream));
	            }
	            else if(params[1].equalsIgnoreCase("ssn")) 
	            {
	            	int ssn = sndrcvinfo.getSsn() & 0xffff;
	            	var.add(Integer.toString(ssn));
	            }
	            else if(params[1].equalsIgnoreCase("ppid")) 
	            {
	            	int ppidLe = (int)sndrcvinfo.getPpid();
	            	int ppidBe = Utils.convertLittleBigIndian(ppidLe);
	            	long ppid = ((long)ppidBe) & 0xffffffffl;
	            	var.add(Long.toString(ppid));
	            }
	            else if(params[1].equalsIgnoreCase("flags")) 
	            {
	            	int ppid = sndrcvinfo.getFlags() & 0xffff;
	            	var.add(Integer.toString(ppid));
	            }
	            else if(params[1].equalsIgnoreCase("context")) 
	            {
	            	long context = ((long) sndrcvinfo.getContext()) & 0xffffffff;
	            	var.add(Long.toString(context));
	            }
	            else if(params[1].equalsIgnoreCase("ttl")) 
	            {
	            	long ttl = ((long) sndrcvinfo.getTtl()) & 0xffffffff;
	            	var.add(Long.toString(ttl));
	            }
	            else if(params[1].equalsIgnoreCase("tsn"))
	            {
	            	long tsn = ((long) Utils.convertLittleBigIndian(sndrcvinfo.getTsn())) & 0xffffffff;
	            	var.add(Long.toString(tsn));
	            }
	            else if(params[1].equalsIgnoreCase("cumtsn")) 
	            {
	            	long cumtsn = ((long) Utils.convertLittleBigIndian(sndrcvinfo.getCumtsn() )) & 0xffffffff;
	            	var.add(Long.toString(cumtsn));
	            }
	            else if(params[1].equalsIgnoreCase("aid")) 
	            {
	            	long aid = ((long)sndrcvinfo.getAssociation().getId())  & 0xffffffff;;
	            	var.add(Long.toString(aid));
	            }
	            else if(params[1].equalsIgnoreCase("peerHosts")) 
		        {
	            	var = this.getParameterPeerHosts();
		        }
		        else if(params[1].equalsIgnoreCase("peerPort")) 
		        {
	            	var = this.getParameterPeerPort();
		        }
		        else 
		        {
		        	Parameter.throwBadPathKeywordException(path);
		        }
        	}
        }
        else if(params[0].equalsIgnoreCase("data")) 
        {
        	DataSctp dataSctp = this.getDataSctp();

        	if(params[1].equalsIgnoreCase("text")) 
            {
                var.add(new String(dataSctp.getData()));
            }
            else if(params[1].equalsIgnoreCase("binary")) 
            {
            	var.add(Array.toHexString(new DefaultArray(dataSctp.getData())));
            }
            else 
            {
            	Parameter.throwBadPathKeywordException(path);
            }
        }
        else if (params[0].equalsIgnoreCase("ua") && params[1].equalsIgnoreCase("ppid"))
        {
        	var.add("1111");
        }
        else 
        {
        	Parameter.throwBadPathKeywordException(path);
        }
		return var;
	}
    
    /**
     * 
     * @return peer hosts adresses
     */
    protected abstract Parameter getParameterPeerHosts() throws Exception;
    
    /**
     * 
     * @return peer hosts port
     */
    protected abstract Parameter getParameterPeerPort() throws Exception;

}

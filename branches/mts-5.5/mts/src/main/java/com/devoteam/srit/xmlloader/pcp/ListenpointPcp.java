/*
 * ListenpointPcp.java
 *
 * Created on 22 octobre 2008, 11:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.devoteam.srit.xmlloader.pcp;

import org.dom4j.Element;

import com.devoteam.srit.xmlloader.core.exception.ExecutionException;
import com.devoteam.srit.xmlloader.core.protocol.Listenpoint;
import com.devoteam.srit.xmlloader.core.protocol.Msg;
import com.devoteam.srit.xmlloader.core.protocol.Stack;
import com.portal.pcm.PCPServerOperation;

/**
 *
 * @author bbouvier
 */
public class ListenpointPcp extends Listenpoint
{    
    /** Creates a new instance of listenpoint */
    public ListenpointPcp(Stack stack) throws Exception
    {
        super(stack);
    }
    
	/** Creates a Listenpoint specific from XML tree*/
	public ListenpointPcp(Stack stack, Element root) throws Exception
	{
		super(stack, root);
	}

	/** Send a Msg to Channel*/
    @Override
    public synchronized boolean sendMessage(Msg msg, String remoteHost, int remotePort, String transport) throws Exception
    {
		if ((remoteHost == null) || (remotePort <= 0)) 
		{
            if(msg.getChannel() != null)
            {
                remoteHost = msg.getChannel().getRemoteHost();
                remotePort = msg.getChannel().getRemotePort();
            }
            else
            {
                throw new ExecutionException("Could not determine remote Host or remote Port");
            }
            msg.setRemoteHost(remoteHost);
            msg.setRemotePort(remotePort);
		}        
        ((PCPServerOperation)listenpointTcp.getAttachment()).send(((MsgPcp)msg).getFlist(), ((MsgPcp)msg).getOpCode());
//		return super.sendMessage(msg, remoteHost, remotePort, transport);
        return true;
    }
        
}

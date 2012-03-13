/*
 * ChannelTcp.java
 *
 * Created on 26 juin 2007, 10:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.devoteam.srit.xmlloader.tcp.bio;

import com.devoteam.srit.xmlloader.core.exception.ExecutionException;
import com.devoteam.srit.xmlloader.core.newstats.StatPool;
import com.devoteam.srit.xmlloader.core.protocol.Channel;
import com.devoteam.srit.xmlloader.core.protocol.Listenpoint;
import com.devoteam.srit.xmlloader.core.protocol.Msg;
import com.devoteam.srit.xmlloader.core.protocol.StackFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *
 * @author gpasquiers
 */
public class ChannelTcpBIO extends Channel
{
    private SocketTcpBIO socketTcp;
    
    private Listenpoint listenpoint;    

    private long startTimestamp = 0;
    
    /** Creates a new instance of ChannelTcp */
    public ChannelTcpBIO(String name, String aLocalHost, String aLocalPort, String aRemoteHost, String aRemotePort, String aProtocol) throws Exception
    {
        super(name, aLocalHost, aLocalPort, aRemoteHost, aRemotePort, aProtocol);
        socketTcp = null;
        listenpoint = null;
    }
    
    /** Creates a new instance of ChannelTcp */
    public ChannelTcpBIO(String name, Listenpoint listenpoint, Socket socket) throws Exception
    {
        super(
        		name, 
        		((InetSocketAddress)socket.getLocalSocketAddress()).getAddress().getHostAddress(), 
        		Integer.toString(((InetSocketAddress)socket.getLocalSocketAddress()).getPort()), 
        		((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress().getHostAddress(), 
        		Integer.toString(((InetSocketAddress)socket.getRemoteSocketAddress()).getPort()),
        		listenpoint.getProtocol()
        );
        
		StatPool.beginStatisticProtocol(StatPool.CHANNEL_KEY, StatPool.BIO_KEY, StackFactory.PROTOCOL_TCP, getProtocol());
		this.startTimestamp = System.currentTimeMillis();

        socketTcp = new SocketTcpBIO(socket);
        socketTcp.setChannelTcp(this);
        this.listenpoint = listenpoint;
    }

    /** Creates a new instance of ChannelTcp */
    public ChannelTcpBIO(Listenpoint listenpointTcp, String localHost, int localPort, String remoteHost, int remotePort, String aProtocol)
    {
    	super(localHost, localPort, remoteHost, remotePort, aProtocol);
        socketTcp = null;    	    
        this.listenpoint = listenpointTcp;        
    }

    /** Send a Msg to Channel */
    public synchronized boolean sendMessage(Msg msg) throws Exception
    {
        if(null == socketTcp)
        {
            throw new ExecutionException("SocketTcp is null, has the connection been opened ?");
        }
        
        msg.setChannel(this);
        socketTcp.send(msg);
        return true;
    }
    
    public boolean open() throws Exception
    {
    	if (socketTcp == null) 
    	{
    		StatPool.beginStatisticProtocol(StatPool.CHANNEL_KEY, StatPool.BIO_KEY, StackFactory.PROTOCOL_TCP, getProtocol());
    		this.startTimestamp = System.currentTimeMillis();
        	
    		InetAddress localAddr = InetAddress.getByName(getLocalHost());
    		Socket socket = new Socket(getRemoteHost(), getRemotePort(), localAddr, getLocalPort());
    		
            this.setLocalPort(socket.getLocalPort());
            this.setLocalHost(socket.getLocalAddress().getHostAddress());
            
            socketTcp = new SocketTcpBIO(socket);
    	}
		
        socketTcp.setChannelTcp(this);
        socketTcp.setDaemon(true);
        socketTcp.start();
        
        return true;
    }
    
    public boolean close()
    {
        SocketTcpBIO tmp = socketTcp;
    	if (tmp != null){
    		StatPool.endStatisticProtocol(StatPool.CHANNEL_KEY, StatPool.BIO_KEY, StackFactory.PROTOCOL_TCP, getProtocol(), startTimestamp);
    		tmp.close();
            socketTcp = null;
    	}
        return true;
    }
    
    /** Get the transport protocol of this message */
    public String getTransport() 
    {
    	return StackFactory.PROTOCOL_TCP;
    }

	public Listenpoint getListenpointTcp() {
		return listenpoint;
	}

    public SocketTcpBIO getSocketTcp() {
        return socketTcp;
    }
}

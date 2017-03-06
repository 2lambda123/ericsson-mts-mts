/* 
 * Copyright 2017 Ericsson http://www.ericsson.com
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

package com.devoteam.srit.xmlloader.core.protocol;

import javax.annotation.Nullable;

/**
*
* @author emicpou
* 
* the TransportStack acts as a transport layer stack,
* i.e. it is used directly or is used by a protocol layer TransportableStack
*/
public abstract class TransportStack extends Stack{
	
    /**
     * Creates a new instance
     */
    public TransportStack() throws Exception
    {
    	super();
    }
    
    /**
     * Creates a new instance
     * @param ctorConfig optional constructor config
     */
    public TransportStack( CtorConfig ctorConfig ) throws Exception{
    	super( ctorConfig );
    }

    /**
     * @return a new TransportInfos instance where to store transport related infos (or null is not implemented)
     */
    @Nullable
    public Listenpoint.TransportInfos createListenpointTransportInfos(){
    	return null;
    }

    /**
     * @return a new TransportInfos instance where to store transport related infos (or null is not implemented)
     */
    @Nullable
    public Channel.TransportInfos createChannelTransportInfos(){
    	return null;
    }

    /**
     * @return a new TransportInfos instance where to store transport related infos  (or null is not implemented)
     */
    @Nullable
    public Msg.TransportInfos createMsgTransportInfos(){
    	return null;
    }
       
}

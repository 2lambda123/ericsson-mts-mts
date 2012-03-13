package com.devoteam.srit.xmlloader.rtp.jmf;

import javax.media.rtp.event.ApplicationEvent;

/**
 * A class to handle floor taken message
 */
class FloorTakenHandler {

    private FloorTakenHandler() {
        // Avoid instanciation
    }

    /**
     * handle floor taken message.
     * <P>
     * <code>
     *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |V=2|P|0 0 0 1 0|   PT=APP=204  |           length              |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                      SSRC of PoC server                       |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |                          name=PoC1                            |
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |         SDES item CNAME followed by SDES item NAME            |
     * :                                                               :
     * |                                                               |          
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * </code>
     * @param ctrl the controler
     * @param evt the event
     */
    protected final static void handleFloorTaken(FloorController ctrl,
            ApplicationEvent evt) {
        // Floor has been taken by another UE
        // ----------------------------------
        String takenBy = extractUserIdentity(evt.getAppData());

        // Save the request subtype
        ctrl.setAcknoledgedType(evt.getAppSubType());
        
        // Stop retransmission timer
        ctrl.timerT0.stop();

        // Notify listeners: identity format is "display name" <URI>
        ctrl.notifyFloorListeners(new FloorEvent(FloorEvent.EVENT_FLOOR_TAKEN,
                takenBy));
    }
    
	/**
	 * Extract the user identity (uri:CNAME & display name:NAME) from a floor event
	 * 
	 * @param data Application data
	 * @return CNAME value
	 */
	private static String extractUserIdentity(byte[] data) {
		//  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
		// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		// |    CNAME=1    |     length    | user and domain name       ...
		// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		// ................|     NAME=2    |     length    | common name
		// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		//  of source .....................|             padding
		// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		String uri = "";
		String displayName = "";
		try {
			int length1 = data[1];
			int index = 2;
			if (length1 != 0) {
				// Read URI
				for(int i=index; i < (index + length1); i++) {
					String car = new Character((char)data[i]).toString();
					uri = uri + car;
				}		
				index = index + length1;
			} else {
				uri = "unknown";
			}
			
			// Read display name
			index++;
			int length2 = data[index];
			if (length2 != 0) {
				index++;
				for(int i=index; i < (index + length2); i++) {
					String car = new Character((char)data[i]).toString();
					displayName = displayName + car;
				}		
			}
		} catch(Exception e) {
			uri = "unknown";
			displayName = "";
		}
				
		String identity = uri;
		if (displayName.length() != 0) {
			identity = 	"\"" + displayName + "\" <" + identity + ">";
		}					
		
		return identity;
	}
}

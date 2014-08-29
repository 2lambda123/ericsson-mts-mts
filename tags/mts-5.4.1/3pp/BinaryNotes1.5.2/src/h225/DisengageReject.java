
package h225;
//
// This file was generated by the BinaryNotes compiler.
// See http://bnotes.sourceforge.net 
// Any modifications to this file will be lost upon recompilation of the source ASN.1. 
//

import org.bn.*;
import org.bn.annotations.*;
import org.bn.annotations.constraints.*;
import org.bn.coders.*;
import org.bn.types.*;




    @ASN1PreparedElement
    @ASN1Sequence ( name = "DisengageReject", isSet = false )
    public class DisengageReject implements IASN1PreparedElement {
            
        @ASN1Element ( name = "requestSeqNum", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private RequestSeqNum requestSeqNum = null;
                
  
        @ASN1Element ( name = "rejectReason", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private DisengageRejectReason rejectReason = null;
                
  
        @ASN1Element ( name = "nonStandardData", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private NonStandardParameter nonStandardData = null;
                
  
        
        public RequestSeqNum getRequestSeqNum () {
            return this.requestSeqNum;
        }

        

        public void setRequestSeqNum (RequestSeqNum value) {
            this.requestSeqNum = value;
        }
        
  
        
        public DisengageRejectReason getRejectReason () {
            return this.rejectReason;
        }

        

        public void setRejectReason (DisengageRejectReason value) {
            this.rejectReason = value;
        }
        
  
        
        public NonStandardParameter getNonStandardData () {
            return this.nonStandardData;
        }

        
        public boolean isNonStandardDataPresent () {
            return this.nonStandardData != null;
        }
        

        public void setNonStandardData (NonStandardParameter value) {
            this.nonStandardData = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DisengageReject.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            
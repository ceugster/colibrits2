/**
 * CFaultEx.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.datacontract.schemas._2004._07.GCDService;

public class CFaultEx  extends org.apache.axis.AxisFault  implements java.io.Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6925417841958910687L;

	private java.lang.String gcdErrorCode;

    private java.lang.String gcdErrorDetails;

    private java.lang.String gcdErrorMessage;

    public CFaultEx() {
    }

    public CFaultEx(
           java.lang.String gcdErrorCode,
           java.lang.String gcdErrorDetails,
           java.lang.String gcdErrorMessage) {
        this.gcdErrorCode = gcdErrorCode;
        this.gcdErrorDetails = gcdErrorDetails;
        this.gcdErrorMessage = gcdErrorMessage;
    }


    /**
     * Gets the gcdErrorCode value for this CFaultEx.
     * 
     * @return gcdErrorCode
     */
    public java.lang.String getGcdErrorCode() {
        return gcdErrorCode;
    }


    /**
     * Sets the gcdErrorCode value for this CFaultEx.
     * 
     * @param gcdErrorCode
     */
    public void setGcdErrorCode(java.lang.String gcdErrorCode) {
        this.gcdErrorCode = gcdErrorCode;
    }


    /**
     * Gets the gcdErrorDetails value for this CFaultEx.
     * 
     * @return gcdErrorDetails
     */
    public java.lang.String getGcdErrorDetails() {
        return gcdErrorDetails;
    }


    /**
     * Sets the gcdErrorDetails value for this CFaultEx.
     * 
     * @param gcdErrorDetails
     */
    public void setGcdErrorDetails(java.lang.String gcdErrorDetails) {
        this.gcdErrorDetails = gcdErrorDetails;
    }


    /**
     * Gets the gcdErrorMessage value for this CFaultEx.
     * 
     * @return gcdErrorMessage
     */
    public java.lang.String getGcdErrorMessage() {
        return gcdErrorMessage;
    }


    /**
     * Sets the gcdErrorMessage value for this CFaultEx.
     * 
     * @param gcdErrorMessage
     */
    public void setGcdErrorMessage(java.lang.String gcdErrorMessage) {
        this.gcdErrorMessage = gcdErrorMessage;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CFaultEx)) return false;
        CFaultEx other = (CFaultEx) obj;
//        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.gcdErrorCode==null && other.getGcdErrorCode()==null) || 
             (this.gcdErrorCode!=null &&
              this.gcdErrorCode.equals(other.getGcdErrorCode()))) &&
            ((this.gcdErrorDetails==null && other.getGcdErrorDetails()==null) || 
             (this.gcdErrorDetails!=null &&
              this.gcdErrorDetails.equals(other.getGcdErrorDetails()))) &&
            ((this.gcdErrorMessage==null && other.getGcdErrorMessage()==null) || 
             (this.gcdErrorMessage!=null &&
              this.gcdErrorMessage.equals(other.getGcdErrorMessage())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getGcdErrorCode() != null) {
            _hashCode += getGcdErrorCode().hashCode();
        }
        if (getGcdErrorDetails() != null) {
            _hashCode += getGcdErrorDetails().hashCode();
        }
        if (getGcdErrorMessage() != null) {
            _hashCode += getGcdErrorMessage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CFaultEx.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/GCDService", "CFaultEx"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gcdErrorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/GCDService", "GcdErrorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gcdErrorDetails");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/GCDService", "GcdErrorDetails"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gcdErrorMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://schemas.datacontract.org/2004/07/GCDService", "GcdErrorMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class<?> _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class<?> _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }


    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, this);
    }
}

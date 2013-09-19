/**
 * IService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface IService extends java.rmi.Remote {
    public java.lang.String GCDAbfrage(java.lang.String benutzer, java.lang.String kennwort, java.lang.String scanCode, java.lang.String befehl, java.lang.String wert, java.lang.String buchungsText, java.lang.Integer mandantenNummer, java.lang.Integer vertriebsNummer, java.lang.String firmenNummer, java.lang.Integer geschaeftsNummer) throws java.rmi.RemoteException;
    public java.lang.String GCD_V1(java.lang.String benutzer, java.lang.String kennwort, java.lang.Short PIN, java.lang.String scanCode, java.lang.String wert, java.lang.String befehl, java.lang.String buchungsText, java.lang.Integer mandantenNummer, java.lang.String firmenNummer, java.lang.Integer vertriebsNummer, java.lang.Integer geschaeftsNummer) throws java.rmi.RemoteException, org.datacontract.schemas._2004._07.GCDService.CFaultEx;
}

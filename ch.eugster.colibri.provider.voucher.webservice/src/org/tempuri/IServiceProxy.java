package org.tempuri;

public class IServiceProxy implements org.tempuri.IService {
  private String _endpoint = null;
  private org.tempuri.IService iService = null;
  
  public IServiceProxy() {
    _initIServiceProxy();
  }
  
  public IServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initIServiceProxy();
  }
  
  private void _initIServiceProxy() {
    try {
      iService = (new org.tempuri.CServiceLocator()).getBasicHttpBinding_IService();
      if (iService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iService != null)
      ((javax.xml.rpc.Stub)iService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.tempuri.IService getIService() {
    if (iService == null)
      _initIServiceProxy();
    return iService;
  }
  
  public java.lang.String GCDAbfrage(java.lang.String benutzer, java.lang.String kennwort, java.lang.String scanCode, java.lang.String befehl, java.lang.String wert, java.lang.String buchungsText, java.lang.Integer mandantenNummer, java.lang.Integer vertriebsNummer, java.lang.String firmenNummer, java.lang.Integer geschaeftsNummer) throws java.rmi.RemoteException{
    if (iService == null)
      _initIServiceProxy();
    return iService.GCDAbfrage(benutzer, kennwort, scanCode, befehl, wert, buchungsText, mandantenNummer, vertriebsNummer, firmenNummer, geschaeftsNummer);
  }
  
  public java.lang.String GCD_V1(java.lang.String benutzer, java.lang.String kennwort, java.lang.Short PIN, java.lang.String scanCode, java.lang.String wert, java.lang.String befehl, java.lang.String buchungsText, java.lang.Integer mandantenNummer, java.lang.String firmenNummer, java.lang.Integer vertriebsNummer, java.lang.Integer geschaeftsNummer) throws java.rmi.RemoteException, org.datacontract.schemas._2004._07.GCDService.CFaultEx{
    if (iService == null)
      _initIServiceProxy();
    return iService.GCD_V1(benutzer, kennwort, PIN, scanCode, wert, befehl, buchungsText, mandantenNummer, firmenNummer, vertriebsNummer, geschaeftsNummer);
  }
  
  
}
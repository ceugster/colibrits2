package ch.eugster.colibri.provider.galileo.kundenserver  ;

import com4j.*;

/**
 * kundenserver.kundenserver
 */
@IID("{D24B0EC2-4A51-45E8-A6B7-06C6C16BC1AD}")
public interface Ikundenserver extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "CDBPATH"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(7)
  @DefaultMethod
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cdbpath();


  /**
   * <p>
   * Setter method for the COM property "CDBPATH"
   * </p>
   * @param cdbpath Mandatory java.lang.Object parameter.
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(8)
  @DefaultMethod
  void cdbpath(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cdbpath);


  /**
   * <p>
   * Getter method for the COM property "CERROR"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(9)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cerror();


  /**
   * <p>
   * Setter method for the COM property "CERROR"
   * </p>
   * @param cerror Mandatory java.lang.Object parameter.
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(10)
  void cerror(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cerror);


  /**
   * <p>
   * Getter method for the COM property "CANREDE"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(11)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object canrede();


  /**
   * <p>
   * Setter method for the COM property "CANREDE"
   * </p>
   * @param canrede Mandatory java.lang.Object parameter.
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(12)
  void canrede(
    @MarshalAs(NativeType.VARIANT) java.lang.Object canrede);


  /**
   * <p>
   * Getter method for the COM property "CTITEL"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(13)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ctitel();


  /**
   * <p>
   * Setter method for the COM property "CTITEL"
   * </p>
   * @param ctitel Mandatory java.lang.Object parameter.
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(14)
  void ctitel(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ctitel);


  /**
   * <p>
   * Getter method for the COM property "CVORNAME"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(15)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cvorname();


  /**
   * <p>
   * Setter method for the COM property "CVORNAME"
   * </p>
   * @param cvorname Mandatory java.lang.Object parameter.
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(16)
  void cvorname(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cvorname);


  /**
   * <p>
   * Getter method for the COM property "CNAME1"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(17)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cnamE1();


  /**
   * <p>
   * Setter method for the COM property "CNAME1"
   * </p>
   * @param cnamE1 Mandatory java.lang.Object parameter.
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(18)
  void cnamE1(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cnamE1);


  /**
   * <p>
   * Getter method for the COM property "CNAME2"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(19)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cnamE2();


  /**
   * <p>
   * Setter method for the COM property "CNAME2"
   * </p>
   * @param cnamE2 Mandatory java.lang.Object parameter.
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(20)
  void cnamE2(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cnamE2);


  /**
   * <p>
   * Getter method for the COM property "CNAME3"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(14) //= 0xe. The runtime will prefer the VTID if present
  @VTID(21)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cnamE3();


  /**
   * <p>
   * Setter method for the COM property "CNAME3"
   * </p>
   * @param cnamE3 Mandatory java.lang.Object parameter.
   */

  @DISPID(14) //= 0xe. The runtime will prefer the VTID if present
  @VTID(22)
  void cnamE3(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cnamE3);


  /**
   * <p>
   * Getter method for the COM property "CSTRASSE"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(16) //= 0x10. The runtime will prefer the VTID if present
  @VTID(23)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cstrasse();


  /**
   * <p>
   * Setter method for the COM property "CSTRASSE"
   * </p>
   * @param cstrasse Mandatory java.lang.Object parameter.
   */

  @DISPID(16) //= 0x10. The runtime will prefer the VTID if present
  @VTID(24)
  void cstrasse(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cstrasse);


  /**
   * <p>
   * Getter method for the COM property "CLAND"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(18) //= 0x12. The runtime will prefer the VTID if present
  @VTID(25)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cland();


  /**
   * <p>
   * Setter method for the COM property "CLAND"
   * </p>
   * @param cland Mandatory java.lang.Object parameter.
   */

  @DISPID(18) //= 0x12. The runtime will prefer the VTID if present
  @VTID(26)
  void cland(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cland);


  /**
   * <p>
   * Getter method for the COM property "CPLZ"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(20) //= 0x14. The runtime will prefer the VTID if present
  @VTID(27)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cplz();


  /**
   * <p>
   * Setter method for the COM property "CPLZ"
   * </p>
   * @param cplz Mandatory java.lang.Object parameter.
   */

  @DISPID(20) //= 0x14. The runtime will prefer the VTID if present
  @VTID(28)
  void cplz(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cplz);


  /**
   * <p>
   * Getter method for the COM property "CORT"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(22) //= 0x16. The runtime will prefer the VTID if present
  @VTID(29)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cort();


  /**
   * <p>
   * Setter method for the COM property "CORT"
   * </p>
   * @param cort Mandatory java.lang.Object parameter.
   */

  @DISPID(22) //= 0x16. The runtime will prefer the VTID if present
  @VTID(30)
  void cort(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cort);


  /**
   * <p>
   * Getter method for the COM property "CTELEFON"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(24) //= 0x18. The runtime will prefer the VTID if present
  @VTID(31)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ctelefon();


  /**
   * <p>
   * Setter method for the COM property "CTELEFON"
   * </p>
   * @param ctelefon Mandatory java.lang.Object parameter.
   */

  @DISPID(24) //= 0x18. The runtime will prefer the VTID if present
  @VTID(32)
  void ctelefon(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ctelefon);


  /**
   * <p>
   * Getter method for the COM property "CTELEFON2"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(26) //= 0x1a. The runtime will prefer the VTID if present
  @VTID(33)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ctelefoN2();


  /**
   * <p>
   * Setter method for the COM property "CTELEFON2"
   * </p>
   * @param ctelefoN2 Mandatory java.lang.Object parameter.
   */

  @DISPID(26) //= 0x1a. The runtime will prefer the VTID if present
  @VTID(34)
  void ctelefoN2(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ctelefoN2);


  /**
   * <p>
   * Getter method for the COM property "CTELEFAX"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(28) //= 0x1c. The runtime will prefer the VTID if present
  @VTID(35)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ctelefax();


  /**
   * <p>
   * Setter method for the COM property "CTELEFAX"
   * </p>
   * @param ctelefax Mandatory java.lang.Object parameter.
   */

  @DISPID(28) //= 0x1c. The runtime will prefer the VTID if present
  @VTID(36)
  void ctelefax(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ctelefax);


  /**
   * <p>
   * Getter method for the COM property "CNATEL"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(30) //= 0x1e. The runtime will prefer the VTID if present
  @VTID(37)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cnatel();


  /**
   * <p>
   * Setter method for the COM property "CNATEL"
   * </p>
   * @param cnatel Mandatory java.lang.Object parameter.
   */

  @DISPID(30) //= 0x1e. The runtime will prefer the VTID if present
  @VTID(38)
  void cnatel(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cnatel);


  /**
   * <p>
   * Getter method for the COM property "CEMAIL"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(32) //= 0x20. The runtime will prefer the VTID if present
  @VTID(39)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cemail();


  /**
   * <p>
   * Setter method for the COM property "CEMAIL"
   * </p>
   * @param cemail Mandatory java.lang.Object parameter.
   */

  @DISPID(32) //= 0x20. The runtime will prefer the VTID if present
  @VTID(40)
  void cemail(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cemail);


  /**
   * <p>
   * Getter method for the COM property "NNACHLASS"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(34) //= 0x22. The runtime will prefer the VTID if present
  @VTID(41)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object nnachlass();


  /**
   * <p>
   * Setter method for the COM property "NNACHLASS"
   * </p>
   * @param nnachlass Mandatory java.lang.Object parameter.
   */

  @DISPID(34) //= 0x22. The runtime will prefer the VTID if present
  @VTID(42)
  void nnachlass(
    @MarshalAs(NativeType.VARIANT) java.lang.Object nnachlass);


  /**
   * <p>
   * Getter method for the COM property "LKUNDKARTE"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(36) //= 0x24. The runtime will prefer the VTID if present
  @VTID(43)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object lkundkarte();


  /**
   * <p>
   * Setter method for the COM property "LKUNDKARTE"
   * </p>
   * @param lkundkarte Mandatory java.lang.Object parameter.
   */

  @DISPID(36) //= 0x24. The runtime will prefer the VTID if present
  @VTID(44)
  void lkundkarte(
    @MarshalAs(NativeType.VARIANT) java.lang.Object lkundkarte);


  /**
   * <p>
   * Getter method for the COM property "NKUNDKONTO"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(38) //= 0x26. The runtime will prefer the VTID if present
  @VTID(45)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object nkundkonto();


  /**
   * <p>
   * Setter method for the COM property "NKUNDKONTO"
   * </p>
   * @param nkundkonto Mandatory java.lang.Object parameter.
   */

  @DISPID(38) //= 0x26. The runtime will prefer the VTID if present
  @VTID(46)
  void nkundkonto(
    @MarshalAs(NativeType.VARIANT) java.lang.Object nkundkonto);


  /**
   * <p>
   * Getter method for the COM property "LRGGEWAEHLT"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(40) //= 0x28. The runtime will prefer the VTID if present
  @VTID(47)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object lrggewaehlt();


  /**
   * <p>
   * Setter method for the COM property "LRGGEWAEHLT"
   * </p>
   * @param lrggewaehlt Mandatory java.lang.Object parameter.
   */

  @DISPID(40) //= 0x28. The runtime will prefer the VTID if present
  @VTID(48)
  void lrggewaehlt(
    @MarshalAs(NativeType.VARIANT) java.lang.Object lrggewaehlt);


  /**
   * <p>
   * Getter method for the COM property "NRGBETRAG"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(42) //= 0x2a. The runtime will prefer the VTID if present
  @VTID(49)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object nrgbetrag();


  /**
   * <p>
   * Setter method for the COM property "NRGBETRAG"
   * </p>
   * @param nrgbetrag Mandatory java.lang.Object parameter.
   */

  @DISPID(42) //= 0x2a. The runtime will prefer the VTID if present
  @VTID(50)
  void nrgbetrag(
    @MarshalAs(NativeType.VARIANT) java.lang.Object nrgbetrag);


  /**
   * <p>
   * Getter method for the COM property "NRGNUMMER"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(44) //= 0x2c. The runtime will prefer the VTID if present
  @VTID(51)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object nrgnummer();


  /**
   * <p>
   * Setter method for the COM property "NRGNUMMER"
   * </p>
   * @param nrgnummer Mandatory java.lang.Object parameter.
   */

  @DISPID(44) //= 0x2c. The runtime will prefer the VTID if present
  @VTID(52)
  void nrgnummer(
    @MarshalAs(NativeType.VARIANT) java.lang.Object nrgnummer);


  /**
   * <p>
   * Getter method for the COM property "CRGDATUM"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(46) //= 0x2e. The runtime will prefer the VTID if present
  @VTID(53)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object crgdatum();


  /**
   * <p>
   * Setter method for the COM property "CRGDATUM"
   * </p>
   * @param crgdatum Mandatory java.lang.Object parameter.
   */

  @DISPID(46) //= 0x2e. The runtime will prefer the VTID if present
  @VTID(54)
  void crgdatum(
    @MarshalAs(NativeType.VARIANT) java.lang.Object crgdatum);


  /**
   * @param cDatabase Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(48) //= 0x30. The runtime will prefer the VTID if present
  @VTID(55)
  boolean db_open(
    java.lang.String cDatabase);


  /**
   * @return  Returns a value of type int
   */

  @DISPID(49) //= 0x31. The runtime will prefer the VTID if present
  @VTID(56)
  int getkundennr();


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(50) //= 0x32. The runtime will prefer the VTID if present
  @VTID(57)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object db_close();


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(51) //= 0x33. The runtime will prefer the VTID if present
  @VTID(58)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object clearKundenProps();


  // Properties:
}

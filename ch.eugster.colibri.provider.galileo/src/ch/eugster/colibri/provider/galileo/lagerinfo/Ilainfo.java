package ch.eugster.colibri.provider.galileo.lagerinfo  ;

import com4j.Com4jObject;
import com4j.DISPID;
import com4j.DefaultMethod;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

/**
 * lagerinfo.LaInfo
 */
@IID("{EF8D3483-503F-11D8-B851-0002E3178697}")
public interface Ilainfo extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "BESTCHECK"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(7)
  @DefaultMethod
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object bestcheck();


  /**
   * <p>
   * Setter method for the COM property "BESTCHECK"
   * </p>
   * @param bestcheck Mandatory java.lang.Object parameter.
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(8)
  @DefaultMethod
  void bestcheck(
    @MarshalAs(NativeType.VARIANT) java.lang.Object bestcheck);


  /**
   * <p>
   * Getter method for the COM property "DOLOG"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(9)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object dolog();


  /**
   * <p>
   * Setter method for the COM property "DOLOG"
   * </p>
   * @param dolog Mandatory java.lang.Object parameter.
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(10)
  void dolog(
    @MarshalAs(NativeType.VARIANT) java.lang.Object dolog);


  /**
   * <p>
   * Getter method for the COM property "CLOGNAME"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(11)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object clogname();


  /**
   * <p>
   * Setter method for the COM property "CLOGNAME"
   * </p>
   * @param clogname Mandatory java.lang.Object parameter.
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(12)
  void clogname(
    @MarshalAs(NativeType.VARIANT) java.lang.Object clogname);


  /**
   * <p>
   * Getter method for the COM property "KBESTLABEL"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(13)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object kbestlabel();


  /**
   * <p>
   * Setter method for the COM property "KBESTLABEL"
   * </p>
   * @param kbestlabel Mandatory java.lang.Object parameter.
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(14)
  void kbestlabel(
    @MarshalAs(NativeType.VARIANT) java.lang.Object kbestlabel);


  /**
   * <p>
   * Getter method for the COM property "LBESTLABEL"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(15)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object lbestlabel();


  /**
   * <p>
   * Setter method for the COM property "LBESTLABEL"
   * </p>
   * @param lbestlabel Mandatory java.lang.Object parameter.
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(16)
  void lbestlabel(
    @MarshalAs(NativeType.VARIANT) java.lang.Object lbestlabel);


  /**
   * <p>
   * Getter method for the COM property "LMENGELABEL"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(17)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object lmengelabel();


  /**
   * <p>
   * Setter method for the COM property "LMENGELABEL"
   * </p>
   * @param lmengelabel Mandatory java.lang.Object parameter.
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(18)
  void lmengelabel(
    @MarshalAs(NativeType.VARIANT) java.lang.Object lmengelabel);


  /**
   * <p>
   * Getter method for the COM property "OCHECK"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(19)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ocheck();


  /**
   * <p>
   * Setter method for the COM property "OCHECK"
   * </p>
   * @param ocheck Mandatory java.lang.Object parameter.
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(20)
  void ocheck(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ocheck);


  /**
   * <p>
   * Getter method for the COM property "OTITEL"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(14) //= 0xe. The runtime will prefer the VTID if present
  @VTID(21)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object otitel();


  /**
   * <p>
   * Setter method for the COM property "OTITEL"
   * </p>
   * @param otitel Mandatory java.lang.Object parameter.
   */

  @DISPID(14) //= 0xe. The runtime will prefer the VTID if present
  @VTID(22)
  void otitel(
    @MarshalAs(NativeType.VARIANT) java.lang.Object otitel);


  /**
   * <p>
   * Getter method for the COM property "CUVP"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(16) //= 0x10. The runtime will prefer the VTID if present
  @VTID(23)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cuvp();


  /**
   * <p>
   * Setter method for the COM property "CUVP"
   * </p>
   * @param cuvp Mandatory java.lang.Object parameter.
   */

  @DISPID(16) //= 0x10. The runtime will prefer the VTID if present
  @VTID(24)
  void cuvp(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cuvp);


  /**
   * <p>
   * Getter method for the COM property "NUVP"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(18) //= 0x12. The runtime will prefer the VTID if present
  @VTID(25)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object nuvp();


  /**
   * <p>
   * Setter method for the COM property "NUVP"
   * </p>
   * @param nuvp Mandatory java.lang.Object parameter.
   */

  @DISPID(18) //= 0x12. The runtime will prefer the VTID if present
  @VTID(26)
  void nuvp(
    @MarshalAs(NativeType.VARIANT) java.lang.Object nuvp);


  /**
   * <p>
   * Getter method for the COM property "CEIGENPREIS"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(20) //= 0x14. The runtime will prefer the VTID if present
  @VTID(27)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ceigenpreis();


  /**
   * <p>
   * Setter method for the COM property "CEIGENPREIS"
   * </p>
   * @param ceigenpreis Mandatory java.lang.Object parameter.
   */

  @DISPID(20) //= 0x14. The runtime will prefer the VTID if present
  @VTID(28)
  void ceigenpreis(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ceigenpreis);


  /**
   * <p>
   * Getter method for the COM property "NEIGENPREIS"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(22) //= 0x16. The runtime will prefer the VTID if present
  @VTID(29)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object neigenpreis();


  /**
   * <p>
   * Setter method for the COM property "NEIGENPREIS"
   * </p>
   * @param neigenpreis Mandatory java.lang.Object parameter.
   */

  @DISPID(22) //= 0x16. The runtime will prefer the VTID if present
  @VTID(30)
  void neigenpreis(
    @MarshalAs(NativeType.VARIANT) java.lang.Object neigenpreis);


  /**
   * <p>
   * Getter method for the COM property "CPREISTYP"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(24) //= 0x18. The runtime will prefer the VTID if present
  @VTID(31)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cpreistyp();


  /**
   * <p>
   * Setter method for the COM property "CPREISTYP"
   * </p>
   * @param cpreistyp Mandatory java.lang.Object parameter.
   */

  @DISPID(24) //= 0x18. The runtime will prefer the VTID if present
  @VTID(32)
  void cpreistyp(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cpreistyp);


  /**
   * <p>
   * Getter method for the COM property "LDOPREIS"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(26) //= 0x1a. The runtime will prefer the VTID if present
  @VTID(33)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ldopreis();


  /**
   * <p>
   * Setter method for the COM property "LDOPREIS"
   * </p>
   * @param ldopreis Mandatory java.lang.Object parameter.
   */

  @DISPID(26) //= 0x1a. The runtime will prefer the VTID if present
  @VTID(34)
  void ldopreis(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ldopreis);


  /**
   * <p>
   * Getter method for the COM property "LDOCALCEP"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(28) //= 0x1c. The runtime will prefer the VTID if present
  @VTID(35)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ldocalcep();


  /**
   * <p>
   * Setter method for the COM property "LDOCALCEP"
   * </p>
   * @param ldocalcep Mandatory java.lang.Object parameter.
   */

  @DISPID(28) //= 0x1c. The runtime will prefer the VTID if present
  @VTID(36)
  void ldocalcep(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ldocalcep);


  /**
   * <p>
   * Getter method for the COM property "LDOLIBREKA"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(30) //= 0x1e. The runtime will prefer the VTID if present
  @VTID(37)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ldolibreka();


  /**
   * <p>
   * Setter method for the COM property "LDOLIBREKA"
   * </p>
   * @param ldolibreka Mandatory java.lang.Object parameter.
   */

  @DISPID(30) //= 0x1e. The runtime will prefer the VTID if present
  @VTID(38)
  void ldolibreka(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ldolibreka);


  /**
   * <p>
   * Getter method for the COM property "LCOMBOOK"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(32) //= 0x20. The runtime will prefer the VTID if present
  @VTID(39)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object lcombook();


  /**
   * <p>
   * Setter method for the COM property "LCOMBOOK"
   * </p>
   * @param lcombook Mandatory java.lang.Object parameter.
   */

  @DISPID(32) //= 0x20. The runtime will prefer the VTID if present
  @VTID(40)
  void lcombook(
    @MarshalAs(NativeType.VARIANT) java.lang.Object lcombook);


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(34) //= 0x22. The runtime will prefer the VTID if present
  @VTID(41)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object open();


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(35) //= 0x23. The runtime will prefer the VTID if present
  @VTID(42)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object infofelder();


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(36) //= 0x24. The runtime will prefer the VTID if present
  @VTID(43)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object addinfo();


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(37) //= 0x25. The runtime will prefer the VTID if present
  @VTID(44)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object geteppreis();


  // Properties:
}

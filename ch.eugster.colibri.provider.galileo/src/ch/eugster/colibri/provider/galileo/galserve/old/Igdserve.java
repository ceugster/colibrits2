package ch.eugster.colibri.provider.galileo.galserve.old  ;

import com4j.*;

@IID("{B94E7B41-4E78-11D8-B851-0002E3178697}")
public interface Igdserve extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Getter method for the COM property "CVERSION"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(7)
  @DefaultMethod
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cversion();


  /**
   * <p>
   * Setter method for the COM property "CVERSION"
   * </p>
   * @param cversion Mandatory java.lang.Object parameter.
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(8)
  @DefaultMethod
  void cversion(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cversion);


  /**
   * <p>
   * Getter method for the COM property "CVERSIONSNR"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(9)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cversionsnr();


  /**
   * <p>
   * Setter method for the COM property "CVERSIONSNR"
   * </p>
   * @param cversionsnr Mandatory java.lang.Object parameter.
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(10)
  void cversionsnr(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cversionsnr);


  /**
   * <p>
   * Getter method for the COM property "gefunden"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(11)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object gefunden();


  /**
   * <p>
   * Setter method for the COM property "gefunden"
   * </p>
   * @param gefunden Mandatory java.lang.Object parameter.
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(12)
  void gefunden(
    @MarshalAs(NativeType.VARIANT) java.lang.Object gefunden);


  /**
   * <p>
   * Getter method for the COM property "AUTOR"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(13)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object autor();


  /**
   * <p>
   * Setter method for the COM property "AUTOR"
   * </p>
   * @param autor Mandatory java.lang.Object parameter.
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(14)
  void autor(
    @MarshalAs(NativeType.VARIANT) java.lang.Object autor);


  /**
   * <p>
   * Getter method for the COM property "TITEL"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(15)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object titel();


  /**
   * <p>
   * Setter method for the COM property "TITEL"
   * </p>
   * @param titel Mandatory java.lang.Object parameter.
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(16)
  void titel(
    @MarshalAs(NativeType.VARIANT) java.lang.Object titel);


  /**
   * <p>
   * Getter method for the COM property "VERLAG"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(17)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object verlag();


  /**
   * <p>
   * Setter method for the COM property "VERLAG"
   * </p>
   * @param verlag Mandatory java.lang.Object parameter.
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(18)
  void verlag(
    @MarshalAs(NativeType.VARIANT) java.lang.Object verlag);


  /**
   * <p>
   * Getter method for the COM property "MWST"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(19)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object mwst();


  /**
   * <p>
   * Setter method for the COM property "MWST"
   * </p>
   * @param mwst Mandatory java.lang.Object parameter.
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(20)
  void mwst(
    @MarshalAs(NativeType.VARIANT) java.lang.Object mwst);


  /**
   * <p>
   * Getter method for the COM property "PREIS"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(14) //= 0xe. The runtime will prefer the VTID if present
  @VTID(21)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object preis();


  /**
   * <p>
   * Setter method for the COM property "PREIS"
   * </p>
   * @param preis Mandatory java.lang.Object parameter.
   */

  @DISPID(14) //= 0xe. The runtime will prefer the VTID if present
  @VTID(22)
  void preis(
    @MarshalAs(NativeType.VARIANT) java.lang.Object preis);


  /**
   * <p>
   * Getter method for the COM property "WGRUPPE"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(16) //= 0x10. The runtime will prefer the VTID if present
  @VTID(23)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object wgruppe();


  /**
   * <p>
   * Setter method for the COM property "WGRUPPE"
   * </p>
   * @param wgruppe Mandatory java.lang.Object parameter.
   */

  @DISPID(16) //= 0x10. The runtime will prefer the VTID if present
  @VTID(24)
  void wgruppe(
    @MarshalAs(NativeType.VARIANT) java.lang.Object wgruppe);


  /**
   * <p>
   * Getter method for the COM property "BESTELLT"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(18) //= 0x12. The runtime will prefer the VTID if present
  @VTID(25)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object bestellt();


  /**
   * <p>
   * Setter method for the COM property "BESTELLT"
   * </p>
   * @param bestellt Mandatory java.lang.Object parameter.
   */

  @DISPID(18) //= 0x12. The runtime will prefer the VTID if present
  @VTID(26)
  void bestellt(
    @MarshalAs(NativeType.VARIANT) java.lang.Object bestellt);


  /**
   * <p>
   * Getter method for the COM property "LAGERABHOLFACH"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(20) //= 0x14. The runtime will prefer the VTID if present
  @VTID(27)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object lagerabholfach();


  /**
   * <p>
   * Setter method for the COM property "LAGERABHOLFACH"
   * </p>
   * @param lagerabholfach Mandatory java.lang.Object parameter.
   */

  @DISPID(20) //= 0x14. The runtime will prefer the VTID if present
  @VTID(28)
  void lagerabholfach(
    @MarshalAs(NativeType.VARIANT) java.lang.Object lagerabholfach);


  /**
   * <p>
   * Getter method for the COM property "ISBN"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(22) //= 0x16. The runtime will prefer the VTID if present
  @VTID(29)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object isbn();


  /**
   * <p>
   * Setter method for the COM property "ISBN"
   * </p>
   * @param isbn Mandatory java.lang.Object parameter.
   */

  @DISPID(22) //= 0x16. The runtime will prefer the VTID if present
  @VTID(30)
  void isbn(
    @MarshalAs(NativeType.VARIANT) java.lang.Object isbn);


  /**
   * <p>
   * Getter method for the COM property "BZNR"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(24) //= 0x18. The runtime will prefer the VTID if present
  @VTID(31)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object bznr();


  /**
   * <p>
   * Setter method for the COM property "BZNR"
   * </p>
   * @param bznr Mandatory java.lang.Object parameter.
   */

  @DISPID(24) //= 0x18. The runtime will prefer the VTID if present
  @VTID(32)
  void bznr(
    @MarshalAs(NativeType.VARIANT) java.lang.Object bznr);


  /**
   * <p>
   * Getter method for the COM property "BESTNUMMER"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(26) //= 0x1a. The runtime will prefer the VTID if present
  @VTID(33)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object bestnummer();


  /**
   * <p>
   * Setter method for the COM property "BESTNUMMER"
   * </p>
   * @param bestnummer Mandatory java.lang.Object parameter.
   */

  @DISPID(26) //= 0x1a. The runtime will prefer the VTID if present
  @VTID(34)
  void bestnummer(
    @MarshalAs(NativeType.VARIANT) java.lang.Object bestnummer);


  /**
   * <p>
   * Getter method for the COM property "GELOESCHT"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(28) //= 0x1c. The runtime will prefer the VTID if present
  @VTID(35)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object geloescht();


  /**
   * <p>
   * Setter method for the COM property "GELOESCHT"
   * </p>
   * @param geloescht Mandatory java.lang.Object parameter.
   */

  @DISPID(28) //= 0x1c. The runtime will prefer the VTID if present
  @VTID(36)
  void geloescht(
    @MarshalAs(NativeType.VARIANT) java.lang.Object geloescht);


  /**
   * <p>
   * Getter method for the COM property "KUNDENNR"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(30) //= 0x1e. The runtime will prefer the VTID if present
  @VTID(37)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object kundennr();


  /**
   * <p>
   * Setter method for the COM property "KUNDENNR"
   * </p>
   * @param kundennr Mandatory java.lang.Object parameter.
   */

  @DISPID(30) //= 0x1e. The runtime will prefer the VTID if present
  @VTID(38)
  void kundennr(
    @MarshalAs(NativeType.VARIANT) java.lang.Object kundennr);


  /**
   * <p>
   * Getter method for the COM property "KEINRABATT"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(32) //= 0x20. The runtime will prefer the VTID if present
  @VTID(39)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object keinrabatt();


  /**
   * <p>
   * Setter method for the COM property "KEINRABATT"
   * </p>
   * @param keinrabatt Mandatory java.lang.Object parameter.
   */

  @DISPID(32) //= 0x20. The runtime will prefer the VTID if present
  @VTID(40)
  void keinrabatt(
    @MarshalAs(NativeType.VARIANT) java.lang.Object keinrabatt);


  /**
   * <p>
   * Getter method for the COM property "VTRANSWRITE"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(34) //= 0x22. The runtime will prefer the VTID if present
  @VTID(41)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vtranswrite();


  /**
   * <p>
   * Setter method for the COM property "VTRANSWRITE"
   * </p>
   * @param vtranswrite Mandatory java.lang.Object parameter.
   */

  @DISPID(34) //= 0x22. The runtime will prefer the VTID if present
  @VTID(42)
  void vtranswrite(
    @MarshalAs(NativeType.VARIANT) java.lang.Object vtranswrite);


  /**
   * <p>
   * Getter method for the COM property "VLAGERUPDATE"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(36) //= 0x24. The runtime will prefer the VTID if present
  @VTID(43)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vlagerupdate();


  /**
   * <p>
   * Setter method for the COM property "VLAGERUPDATE"
   * </p>
   * @param vlagerupdate Mandatory java.lang.Object parameter.
   */

  @DISPID(36) //= 0x24. The runtime will prefer the VTID if present
  @VTID(44)
  void vlagerupdate(
    @MarshalAs(NativeType.VARIANT) java.lang.Object vlagerupdate);


  /**
   * <p>
   * Getter method for the COM property "VNUMMER"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(38) //= 0x26. The runtime will prefer the VTID if present
  @VTID(45)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vnummer();


  /**
   * <p>
   * Setter method for the COM property "VNUMMER"
   * </p>
   * @param vnummer Mandatory java.lang.Object parameter.
   */

  @DISPID(38) //= 0x26. The runtime will prefer the VTID if present
  @VTID(46)
  void vnummer(
    @MarshalAs(NativeType.VARIANT) java.lang.Object vnummer);


  /**
   * <p>
   * Getter method for the COM property "VPREIS"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(40) //= 0x28. The runtime will prefer the VTID if present
  @VTID(47)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vpreis();


  /**
   * <p>
   * Setter method for the COM property "VPREIS"
   * </p>
   * @param vpreis Mandatory java.lang.Object parameter.
   */

  @DISPID(40) //= 0x28. The runtime will prefer the VTID if present
  @VTID(48)
  void vpreis(
    @MarshalAs(NativeType.VARIANT) java.lang.Object vpreis);


  /**
   * <p>
   * Getter method for the COM property "VMWST"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(42) //= 0x2a. The runtime will prefer the VTID if present
  @VTID(49)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vmwst();


  /**
   * <p>
   * Setter method for the COM property "VMWST"
   * </p>
   * @param vmwst Mandatory java.lang.Object parameter.
   */

  @DISPID(42) //= 0x2a. The runtime will prefer the VTID if present
  @VTID(50)
  void vmwst(
    @MarshalAs(NativeType.VARIANT) java.lang.Object vmwst);


  /**
   * <p>
   * Getter method for the COM property "VWGRUPPE"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(44) //= 0x2c. The runtime will prefer the VTID if present
  @VTID(51)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vwgruppe();


  /**
   * <p>
   * Setter method for the COM property "VWGRUPPE"
   * </p>
   * @param vwgruppe Mandatory java.lang.Object parameter.
   */

  @DISPID(44) //= 0x2c. The runtime will prefer the VTID if present
  @VTID(52)
  void vwgruppe(
    @MarshalAs(NativeType.VARIANT) java.lang.Object vwgruppe);


  /**
   * <p>
   * Getter method for the COM property "VMENGE"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(46) //= 0x2e. The runtime will prefer the VTID if present
  @VTID(53)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vmenge();


  /**
   * <p>
   * Setter method for the COM property "VMENGE"
   * </p>
   * @param vmenge Mandatory java.lang.Object parameter.
   */

  @DISPID(46) //= 0x2e. The runtime will prefer the VTID if present
  @VTID(54)
  void vmenge(
    @MarshalAs(NativeType.VARIANT) java.lang.Object vmenge);


  /**
   * <p>
   * Getter method for the COM property "VRABATT"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(48) //= 0x30. The runtime will prefer the VTID if present
  @VTID(55)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vrabatt();


  /**
   * <p>
   * Setter method for the COM property "VRABATT"
   * </p>
   * @param vrabatt Mandatory java.lang.Object parameter.
   */

  @DISPID(48) //= 0x30. The runtime will prefer the VTID if present
  @VTID(56)
  void vrabatt(
    @MarshalAs(NativeType.VARIANT) java.lang.Object vrabatt);


  /**
   * <p>
   * Getter method for the COM property "VBESTELLT"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(50) //= 0x32. The runtime will prefer the VTID if present
  @VTID(57)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vbestellt();


  /**
   * <p>
   * Setter method for the COM property "VBESTELLT"
   * </p>
   * @param vbestellt Mandatory java.lang.Object parameter.
   */

  @DISPID(50) //= 0x32. The runtime will prefer the VTID if present
  @VTID(58)
  void vbestellt(
    @MarshalAs(NativeType.VARIANT) java.lang.Object vbestellt);


  /**
   * <p>
   * Getter method for the COM property "VLAGERABHOLFACH"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(52) //= 0x34. The runtime will prefer the VTID if present
  @VTID(59)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vlagerabholfach();


  /**
   * <p>
   * Setter method for the COM property "VLAGERABHOLFACH"
   * </p>
   * @param vlagerabholfach Mandatory java.lang.Object parameter.
   */

  @DISPID(52) //= 0x34. The runtime will prefer the VTID if present
  @VTID(60)
  void vlagerabholfach(
    @MarshalAs(NativeType.VARIANT) java.lang.Object vlagerabholfach);


  /**
   * <p>
   * Getter method for the COM property "VKUNDENNR"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(54) //= 0x36. The runtime will prefer the VTID if present
  @VTID(61)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vkundennr();


  /**
   * <p>
   * Setter method for the COM property "VKUNDENNR"
   * </p>
   * @param vkundennr Mandatory java.lang.Object parameter.
   */

  @DISPID(54) //= 0x36. The runtime will prefer the VTID if present
  @VTID(62)
  void vkundennr(
    @MarshalAs(NativeType.VARIANT) java.lang.Object vkundennr);


  /**
   * <p>
   * Getter method for the COM property "VCOUPONNR"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(56) //= 0x38. The runtime will prefer the VTID if present
  @VTID(63)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vcouponnr();


  /**
   * <p>
   * Setter method for the COM property "VCOUPONNR"
   * </p>
   * @param vcouponnr Mandatory java.lang.Object parameter.
   */

  @DISPID(56) //= 0x38. The runtime will prefer the VTID if present
  @VTID(64)
  void vcouponnr(
    @MarshalAs(NativeType.VARIANT) java.lang.Object vcouponnr);


  /**
   * <p>
   * Getter method for the COM property "VWGNAME"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(58) //= 0x3a. The runtime will prefer the VTID if present
  @VTID(65)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vwgname();


  /**
   * <p>
   * Setter method for the COM property "VWGNAME"
   * </p>
   * @param vwgname Mandatory java.lang.Object parameter.
   */

  @DISPID(58) //= 0x3a. The runtime will prefer the VTID if present
  @VTID(66)
  void vwgname(
    @MarshalAs(NativeType.VARIANT) java.lang.Object vwgname);


  /**
   * <p>
   * Getter method for the COM property "VEBOOK"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(60) //= 0x3c. The runtime will prefer the VTID if present
  @VTID(67)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object vebook();


  /**
   * <p>
   * Setter method for the COM property "VEBOOK"
   * </p>
   * @param vebook Mandatory java.lang.Object parameter.
   */

  @DISPID(60) //= 0x3c. The runtime will prefer the VTID if present
  @VTID(68)
  void vebook(
    @MarshalAs(NativeType.VARIANT) java.lang.Object vebook);


  /**
   * <p>
   * Getter method for the COM property "BESTAND"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(62) //= 0x3e. The runtime will prefer the VTID if present
  @VTID(69)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object bestand();


  /**
   * <p>
   * Setter method for the COM property "BESTAND"
   * </p>
   * @param bestand Mandatory java.lang.Object parameter.
   */

  @DISPID(62) //= 0x3e. The runtime will prefer the VTID if present
  @VTID(70)
  void bestand(
    @MarshalAs(NativeType.VARIANT) java.lang.Object bestand);


  /**
   * <p>
   * Getter method for the COM property "LASTVKDAT"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(64) //= 0x40. The runtime will prefer the VTID if present
  @VTID(71)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object lastvkdat();


  /**
   * <p>
   * Setter method for the COM property "LASTVKDAT"
   * </p>
   * @param lastvkdat Mandatory java.lang.Object parameter.
   */

  @DISPID(64) //= 0x40. The runtime will prefer the VTID if present
  @VTID(72)
  void lastvkdat(
    @MarshalAs(NativeType.VARIANT) java.lang.Object lastvkdat);


  /**
   * <p>
   * Getter method for the COM property "MENGE"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(66) //= 0x42. The runtime will prefer the VTID if present
  @VTID(73)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object menge();


  /**
   * <p>
   * Setter method for the COM property "MENGE"
   * </p>
   * @param menge Mandatory java.lang.Object parameter.
   */

  @DISPID(66) //= 0x42. The runtime will prefer the VTID if present
  @VTID(74)
  void menge(
    @MarshalAs(NativeType.VARIANT) java.lang.Object menge);


  /**
   * <p>
   * Getter method for the COM property "GALDB"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(68) //= 0x44. The runtime will prefer the VTID if present
  @VTID(75)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object galdb();


  /**
   * <p>
   * Setter method for the COM property "GALDB"
   * </p>
   * @param galdb Mandatory java.lang.Object parameter.
   */

  @DISPID(68) //= 0x44. The runtime will prefer the VTID if present
  @VTID(76)
  void galdb(
    @MarshalAs(NativeType.VARIANT) java.lang.Object galdb);


  /**
   * <p>
   * Getter method for the COM property "GALVERSION"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(70) //= 0x46. The runtime will prefer the VTID if present
  @VTID(77)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object galversion();


  /**
   * <p>
   * Setter method for the COM property "GALVERSION"
   * </p>
   * @param galversion Mandatory java.lang.Object parameter.
   */

  @DISPID(70) //= 0x46. The runtime will prefer the VTID if present
  @VTID(78)
  void galversion(
    @MarshalAs(NativeType.VARIANT) java.lang.Object galversion);


  /**
   * <p>
   * Getter method for the COM property "LCDGEFUNDEN"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(72) //= 0x48. The runtime will prefer the VTID if present
  @VTID(79)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object lcdgefunden();


  /**
   * <p>
   * Setter method for the COM property "LCDGEFUNDEN"
   * </p>
   * @param lcdgefunden Mandatory java.lang.Object parameter.
   */

  @DISPID(72) //= 0x48. The runtime will prefer the VTID if present
  @VTID(80)
  void lcdgefunden(
    @MarshalAs(NativeType.VARIANT) java.lang.Object lcdgefunden);


  /**
   * <p>
   * Getter method for the COM property "LCDSUCHE"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(74) //= 0x4a. The runtime will prefer the VTID if present
  @VTID(81)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object lcdsuche();


  /**
   * <p>
   * Setter method for the COM property "LCDSUCHE"
   * </p>
   * @param lcdsuche Mandatory java.lang.Object parameter.
   */

  @DISPID(74) //= 0x4a. The runtime will prefer the VTID if present
  @VTID(82)
  void lcdsuche(
    @MarshalAs(NativeType.VARIANT) java.lang.Object lcdsuche);


  /**
   * <p>
   * Getter method for the COM property "CBIBINI"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(76) //= 0x4c. The runtime will prefer the VTID if present
  @VTID(83)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cbibini();


  /**
   * <p>
   * Setter method for the COM property "CBIBINI"
   * </p>
   * @param cbibini Mandatory java.lang.Object parameter.
   */

  @DISPID(76) //= 0x4c. The runtime will prefer the VTID if present
  @VTID(84)
  void cbibini(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cbibini);


  /**
   * <p>
   * Getter method for the COM property "LCDERROR"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(78) //= 0x4e. The runtime will prefer the VTID if present
  @VTID(85)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object lcderror();


  /**
   * <p>
   * Setter method for the COM property "LCDERROR"
   * </p>
   * @param lcderror Mandatory java.lang.Object parameter.
   */

  @DISPID(78) //= 0x4e. The runtime will prefer the VTID if present
  @VTID(86)
  void lcderror(
    @MarshalAs(NativeType.VARIANT) java.lang.Object lcderror);


  /**
   * <p>
   * Getter method for the COM property "CCDERROR"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(80) //= 0x50. The runtime will prefer the VTID if present
  @VTID(87)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ccderror();


  /**
   * <p>
   * Setter method for the COM property "CCDERROR"
   * </p>
   * @param ccderror Mandatory java.lang.Object parameter.
   */

  @DISPID(80) //= 0x50. The runtime will prefer the VTID if present
  @VTID(88)
  void ccderror(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ccderror);


  /**
   * <p>
   * Getter method for the COM property "CDATAPATH"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(82) //= 0x52. The runtime will prefer the VTID if present
  @VTID(89)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cdatapath();


  /**
   * <p>
   * Setter method for the COM property "CDATAPATH"
   * </p>
   * @param cdatapath Mandatory java.lang.Object parameter.
   */

  @DISPID(82) //= 0x52. The runtime will prefer the VTID if present
  @VTID(90)
  void cdatapath(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cdatapath);


  /**
   * <p>
   * Getter method for the COM property "NICHTBUCHEN"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(84) //= 0x54. The runtime will prefer the VTID if present
  @VTID(91)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object nichtbuchen();


  /**
   * <p>
   * Setter method for the COM property "NICHTBUCHEN"
   * </p>
   * @param nichtbuchen Mandatory java.lang.Object parameter.
   */

  @DISPID(84) //= 0x54. The runtime will prefer the VTID if present
  @VTID(92)
  void nichtbuchen(
    @MarshalAs(NativeType.VARIANT) java.lang.Object nichtbuchen);


  /**
   * <p>
   * Getter method for the COM property "LLOG"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(86) //= 0x56. The runtime will prefer the VTID if present
  @VTID(93)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object llog();


  /**
   * <p>
   * Setter method for the COM property "LLOG"
   * </p>
   * @param llog Mandatory java.lang.Object parameter.
   */

  @DISPID(86) //= 0x56. The runtime will prefer the VTID if present
  @VTID(94)
  void llog(
    @MarshalAs(NativeType.VARIANT) java.lang.Object llog);


  /**
   * <p>
   * Getter method for the COM property "CLOGNAME"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(88) //= 0x58. The runtime will prefer the VTID if present
  @VTID(95)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object clogname();


  /**
   * <p>
   * Setter method for the COM property "CLOGNAME"
   * </p>
   * @param clogname Mandatory java.lang.Object parameter.
   */

  @DISPID(88) //= 0x58. The runtime will prefer the VTID if present
  @VTID(96)
  void clogname(
    @MarshalAs(NativeType.VARIANT) java.lang.Object clogname);


  /**
   * <p>
   * Getter method for the COM property "CANREDE"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(90) //= 0x5a. The runtime will prefer the VTID if present
  @VTID(97)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object canrede();


  /**
   * <p>
   * Setter method for the COM property "CANREDE"
   * </p>
   * @param canrede Mandatory java.lang.Object parameter.
   */

  @DISPID(90) //= 0x5a. The runtime will prefer the VTID if present
  @VTID(98)
  void canrede(
    @MarshalAs(NativeType.VARIANT) java.lang.Object canrede);


  /**
   * <p>
   * Getter method for the COM property "CTITEL"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(92) //= 0x5c. The runtime will prefer the VTID if present
  @VTID(99)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ctitel();


  /**
   * <p>
   * Setter method for the COM property "CTITEL"
   * </p>
   * @param ctitel Mandatory java.lang.Object parameter.
   */

  @DISPID(92) //= 0x5c. The runtime will prefer the VTID if present
  @VTID(100)
  void ctitel(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ctitel);


  /**
   * <p>
   * Getter method for the COM property "CVORNAME"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(94) //= 0x5e. The runtime will prefer the VTID if present
  @VTID(101)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cvorname();


  /**
   * <p>
   * Setter method for the COM property "CVORNAME"
   * </p>
   * @param cvorname Mandatory java.lang.Object parameter.
   */

  @DISPID(94) //= 0x5e. The runtime will prefer the VTID if present
  @VTID(102)
  void cvorname(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cvorname);


  /**
   * <p>
   * Getter method for the COM property "CNAME1"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(96) //= 0x60. The runtime will prefer the VTID if present
  @VTID(103)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cnamE1();


  /**
   * <p>
   * Setter method for the COM property "CNAME1"
   * </p>
   * @param cnamE1 Mandatory java.lang.Object parameter.
   */

  @DISPID(96) //= 0x60. The runtime will prefer the VTID if present
  @VTID(104)
  void cnamE1(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cnamE1);


  /**
   * <p>
   * Getter method for the COM property "CNAME2"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(98) //= 0x62. The runtime will prefer the VTID if present
  @VTID(105)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cnamE2();


  /**
   * <p>
   * Setter method for the COM property "CNAME2"
   * </p>
   * @param cnamE2 Mandatory java.lang.Object parameter.
   */

  @DISPID(98) //= 0x62. The runtime will prefer the VTID if present
  @VTID(106)
  void cnamE2(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cnamE2);


  /**
   * <p>
   * Getter method for the COM property "CNAME3"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(100) //= 0x64. The runtime will prefer the VTID if present
  @VTID(107)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cnamE3();


  /**
   * <p>
   * Setter method for the COM property "CNAME3"
   * </p>
   * @param cnamE3 Mandatory java.lang.Object parameter.
   */

  @DISPID(100) //= 0x64. The runtime will prefer the VTID if present
  @VTID(108)
  void cnamE3(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cnamE3);


  /**
   * <p>
   * Getter method for the COM property "CSTRASSE"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(102) //= 0x66. The runtime will prefer the VTID if present
  @VTID(109)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cstrasse();


  /**
   * <p>
   * Setter method for the COM property "CSTRASSE"
   * </p>
   * @param cstrasse Mandatory java.lang.Object parameter.
   */

  @DISPID(102) //= 0x66. The runtime will prefer the VTID if present
  @VTID(110)
  void cstrasse(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cstrasse);


  /**
   * <p>
   * Getter method for the COM property "CLAND"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(104) //= 0x68. The runtime will prefer the VTID if present
  @VTID(111)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cland();


  /**
   * <p>
   * Setter method for the COM property "CLAND"
   * </p>
   * @param cland Mandatory java.lang.Object parameter.
   */

  @DISPID(104) //= 0x68. The runtime will prefer the VTID if present
  @VTID(112)
  void cland(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cland);


  /**
   * <p>
   * Getter method for the COM property "CPLZ"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(106) //= 0x6a. The runtime will prefer the VTID if present
  @VTID(113)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cplz();


  /**
   * <p>
   * Setter method for the COM property "CPLZ"
   * </p>
   * @param cplz Mandatory java.lang.Object parameter.
   */

  @DISPID(106) //= 0x6a. The runtime will prefer the VTID if present
  @VTID(114)
  void cplz(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cplz);


  /**
   * <p>
   * Getter method for the COM property "CORT"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(108) //= 0x6c. The runtime will prefer the VTID if present
  @VTID(115)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cort();


  /**
   * <p>
   * Setter method for the COM property "CORT"
   * </p>
   * @param cort Mandatory java.lang.Object parameter.
   */

  @DISPID(108) //= 0x6c. The runtime will prefer the VTID if present
  @VTID(116)
  void cort(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cort);


  /**
   * <p>
   * Getter method for the COM property "CTELEFON"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(110) //= 0x6e. The runtime will prefer the VTID if present
  @VTID(117)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ctelefon();


  /**
   * <p>
   * Setter method for the COM property "CTELEFON"
   * </p>
   * @param ctelefon Mandatory java.lang.Object parameter.
   */

  @DISPID(110) //= 0x6e. The runtime will prefer the VTID if present
  @VTID(118)
  void ctelefon(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ctelefon);


  /**
   * <p>
   * Getter method for the COM property "CTELEFON2"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(112) //= 0x70. The runtime will prefer the VTID if present
  @VTID(119)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ctelefoN2();


  /**
   * <p>
   * Setter method for the COM property "CTELEFON2"
   * </p>
   * @param ctelefoN2 Mandatory java.lang.Object parameter.
   */

  @DISPID(112) //= 0x70. The runtime will prefer the VTID if present
  @VTID(120)
  void ctelefoN2(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ctelefoN2);


  /**
   * <p>
   * Getter method for the COM property "CTELEFAX"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(114) //= 0x72. The runtime will prefer the VTID if present
  @VTID(121)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ctelefax();


  /**
   * <p>
   * Setter method for the COM property "CTELEFAX"
   * </p>
   * @param ctelefax Mandatory java.lang.Object parameter.
   */

  @DISPID(114) //= 0x72. The runtime will prefer the VTID if present
  @VTID(122)
  void ctelefax(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ctelefax);


  /**
   * <p>
   * Getter method for the COM property "CNATEL"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(116) //= 0x74. The runtime will prefer the VTID if present
  @VTID(123)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cnatel();


  /**
   * <p>
   * Setter method for the COM property "CNATEL"
   * </p>
   * @param cnatel Mandatory java.lang.Object parameter.
   */

  @DISPID(116) //= 0x74. The runtime will prefer the VTID if present
  @VTID(124)
  void cnatel(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cnatel);


  /**
   * <p>
   * Getter method for the COM property "CEMAIL"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(118) //= 0x76. The runtime will prefer the VTID if present
  @VTID(125)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cemail();


  /**
   * <p>
   * Setter method for the COM property "CEMAIL"
   * </p>
   * @param cemail Mandatory java.lang.Object parameter.
   */

  @DISPID(118) //= 0x76. The runtime will prefer the VTID if present
  @VTID(126)
  void cemail(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cemail);


  /**
   * <p>
   * Getter method for the COM property "NNACHLASS"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(120) //= 0x78. The runtime will prefer the VTID if present
  @VTID(127)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object nnachlass();


  /**
   * <p>
   * Setter method for the COM property "NNACHLASS"
   * </p>
   * @param nnachlass Mandatory java.lang.Object parameter.
   */

  @DISPID(120) //= 0x78. The runtime will prefer the VTID if present
  @VTID(128)
  void nnachlass(
    @MarshalAs(NativeType.VARIANT) java.lang.Object nnachlass);


  /**
   * <p>
   * Getter method for the COM property "OCHECK"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(122) //= 0x7a. The runtime will prefer the VTID if present
  @VTID(129)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object ocheck();


  /**
   * <p>
   * Setter method for the COM property "OCHECK"
   * </p>
   * @param ocheck Mandatory java.lang.Object parameter.
   */

  @DISPID(122) //= 0x7a. The runtime will prefer the VTID if present
  @VTID(130)
  void ocheck(
    @MarshalAs(NativeType.VARIANT) java.lang.Object ocheck);


  /**
   * <p>
   * Getter method for the COM property "LKUNDKARTE"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(124) //= 0x7c. The runtime will prefer the VTID if present
  @VTID(131)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object lkundkarte();


  /**
   * <p>
   * Setter method for the COM property "LKUNDKARTE"
   * </p>
   * @param lkundkarte Mandatory java.lang.Object parameter.
   */

  @DISPID(124) //= 0x7c. The runtime will prefer the VTID if present
  @VTID(132)
  void lkundkarte(
    @MarshalAs(NativeType.VARIANT) java.lang.Object lkundkarte);


  /**
   * <p>
   * Getter method for the COM property "NKUNDKONTO"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(126) //= 0x7e. The runtime will prefer the VTID if present
  @VTID(133)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object nkundkonto();


  /**
   * <p>
   * Setter method for the COM property "NKUNDKONTO"
   * </p>
   * @param nkundkonto Mandatory java.lang.Object parameter.
   */

  @DISPID(126) //= 0x7e. The runtime will prefer the VTID if present
  @VTID(134)
  void nkundkonto(
    @MarshalAs(NativeType.VARIANT) java.lang.Object nkundkonto);


  /**
   * <p>
   * Getter method for the COM property "CRGERROR"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(128) //= 0x80. The runtime will prefer the VTID if present
  @VTID(135)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object crgerror();


  /**
   * <p>
   * Setter method for the COM property "CRGERROR"
   * </p>
   * @param crgerror Mandatory java.lang.Object parameter.
   */

  @DISPID(128) //= 0x80. The runtime will prefer the VTID if present
  @VTID(136)
  void crgerror(
    @MarshalAs(NativeType.VARIANT) java.lang.Object crgerror);


  /**
   * <p>
   * Getter method for the COM property "LKUNDFOUND"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(130) //= 0x82. The runtime will prefer the VTID if present
  @VTID(137)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object lkundfound();


  /**
   * <p>
   * Setter method for the COM property "LKUNDFOUND"
   * </p>
   * @param lkundfound Mandatory java.lang.Object parameter.
   */

  @DISPID(130) //= 0x82. The runtime will prefer the VTID if present
  @VTID(138)
  void lkundfound(
    @MarshalAs(NativeType.VARIANT) java.lang.Object lkundfound);


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(132) //= 0x84. The runtime will prefer the VTID if present
  @VTID(139)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object iswws();


  /**
   * @param cDatabase Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(133) //= 0x85. The runtime will prefer the VTID if present
  @VTID(140)
  boolean do_NOpen(
    java.lang.String cDatabase);


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(134) //= 0x86. The runtime will prefer the VTID if present
  @VTID(141)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object do_NClose();


  /**
   * @param cNummer Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(135) //= 0x87. The runtime will prefer the VTID if present
  @VTID(142)
  boolean do_NSearch(
    java.lang.String cNummer);


  /**
   * @param cNummer Mandatory java.lang.String parameter.
   * @param nMenge Mandatory int parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(136) //= 0x88. The runtime will prefer the VTID if present
  @VTID(143)
  boolean do_delabholfach(
    java.lang.String cNummer,
    int nMenge);


  /**
   * @param cNummer Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(137) //= 0x89. The runtime will prefer the VTID if present
  @VTID(144)
  boolean do_verkauf(
    java.lang.String cNummer);


  /**
   * @return  Returns a value of type boolean
   */

  @DISPID(138) //= 0x8a. The runtime will prefer the VTID if present
  @VTID(145)
  boolean do_wgverkauf();


  /**
   * @return  Returns a value of type boolean
   */

  @DISPID(139) //= 0x8b. The runtime will prefer the VTID if present
  @VTID(146)
  boolean do_wgstorno();


  /**
   * @param cNummer Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(140) //= 0x8c. The runtime will prefer the VTID if present
  @VTID(147)
  boolean do_storno(
    java.lang.String cNummer);


  /**
   * @param cBSWG Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(141) //= 0x8d. The runtime will prefer the VTID if present
  @VTID(148)
  java.lang.String getgalwg(
    java.lang.String cBSWG);


  /**
   * @param cWGruppe Mandatory java.lang.String parameter.
   * @return  Returns a value of type double
   */

  @DISPID(142) //= 0x8e. The runtime will prefer the VTID if present
  @VTID(149)
  double getekrabatt(
    java.lang.String cWGruppe);


  /**
   * @param nKundennr Mandatory int parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(143) //= 0x8f. The runtime will prefer the VTID if present
  @VTID(150)
  boolean do_getkunde(
    int nKundennr);


  /**
   * @param cNummer Mandatory java.lang.String parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(144) //= 0x90. The runtime will prefer the VTID if present
  @VTID(151)
  boolean do_teststorno(
    java.lang.String cNummer);


  /**
   * @param nRgNummer Mandatory int parameter.
   * @return  Returns a value of type boolean
   */

  @DISPID(145) //= 0x91. The runtime will prefer the VTID if present
  @VTID(152)
  boolean do_BucheRechnung(
    int nRgNummer);


  // Properties:
}

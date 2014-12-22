package ch.eugster.colibri.provider.galileo.wgserve.old  ;

import com4j.Com4jObject;
import com4j.DISPID;
import com4j.DefaultMethod;
import com4j.IID;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

@IID("{9C3F93A1-270D-11D8-B851-0002E3178697}")
public interface Iwgserve extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Specifies the file name of the user-defined class library that contains the object's class.
   * </p>
   * <p>
   * Getter method for the COM property "ClassLibrary"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(7)
  @DefaultMethod
  java.lang.String classLibrary();


  /**
   * <p>
   * Specifies the graphics file or field to display on the control.
   * </p>
   * <p>
   * Getter method for the COM property "Picture"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(8)
  java.lang.String picture();


  /**
   * <p>
   * Getter method for the COM property "wgtext"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(9)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object wgtext();


  /**
   * <p>
   * Setter method for the COM property "wgtext"
   * </p>
   * @param wgtext Mandatory java.lang.Object parameter.
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(10)
  void wgtext(
    @MarshalAs(NativeType.VARIANT) java.lang.Object wgtext);


  /**
   * <p>
   * Getter method for the COM property "konto"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(11)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object konto();


  /**
   * <p>
   * Setter method for the COM property "konto"
   * </p>
   * @param konto Mandatory java.lang.Object parameter.
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(12)
  void konto(
    @MarshalAs(NativeType.VARIANT) java.lang.Object konto);


  /**
   * <p>
   * Getter method for the COM property "box1"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(13)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object box1();


  /**
   * <p>
   * Setter method for the COM property "box1"
   * </p>
   * @param box1 Mandatory java.lang.Object parameter.
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(14)
  void box1(
    @MarshalAs(NativeType.VARIANT) java.lang.Object box1);


  /**
   * <p>
   * Getter method for the COM property "box2"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(15)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object box2();


  /**
   * <p>
   * Setter method for the COM property "box2"
   * </p>
   * @param box2 Mandatory java.lang.Object parameter.
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(16)
  void box2(
    @MarshalAs(NativeType.VARIANT) java.lang.Object box2);


  /**
   * <p>
   * Getter method for the COM property "descbox1"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(17)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object descbox1();


  /**
   * <p>
   * Setter method for the COM property "descbox1"
   * </p>
   * @param descbox1 Mandatory java.lang.Object parameter.
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(18)
  void descbox1(
    @MarshalAs(NativeType.VARIANT) java.lang.Object descbox1);


  /**
   * <p>
   * Getter method for the COM property "descbox2"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(19)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object descbox2();


  /**
   * <p>
   * Setter method for the COM property "descbox2"
   * </p>
   * @param descbox2 Mandatory java.lang.Object parameter.
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(20)
  void descbox2(
    @MarshalAs(NativeType.VARIANT) java.lang.Object descbox2);


  /**
   * <p>
   * Getter method for the COM property "wglist"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(14) //= 0xe. The runtime will prefer the VTID if present
  @VTID(21)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object wglist();


  /**
   * <p>
   * Setter method for the COM property "wglist"
   * </p>
   * @param wglist Mandatory java.lang.Object parameter.
   */

  @DISPID(14) //= 0xe. The runtime will prefer the VTID if present
  @VTID(22)
  void wglist(
    @MarshalAs(NativeType.VARIANT) java.lang.Object wglist);


  /**
   * <p>
   * Getter method for the COM property "gefunden"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(16) //= 0x10. The runtime will prefer the VTID if present
  @VTID(23)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object gefunden();


  /**
   * <p>
   * Setter method for the COM property "gefunden"
   * </p>
   * @param gefunden Mandatory java.lang.Object parameter.
   */

  @DISPID(16) //= 0x10. The runtime will prefer the VTID if present
  @VTID(24)
  void gefunden(
    @MarshalAs(NativeType.VARIANT) java.lang.Object gefunden);


  /**
   * <p>
   * Getter method for the COM property "bestaetigt"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(18) //= 0x12. The runtime will prefer the VTID if present
  @VTID(25)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object bestaetigt();


  /**
   * <p>
   * Setter method for the COM property "bestaetigt"
   * </p>
   * @param bestaetigt Mandatory java.lang.Object parameter.
   */

  @DISPID(18) //= 0x12. The runtime will prefer the VTID if present
  @VTID(26)
  void bestaetigt(
    @MarshalAs(NativeType.VARIANT) java.lang.Object bestaetigt);


  /**
   * <p>
   * Getter method for the COM property "CVERSION"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(20) //= 0x14. The runtime will prefer the VTID if present
  @VTID(27)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cversion();


  /**
   * <p>
   * Setter method for the COM property "CVERSION"
   * </p>
   * @param cversion Mandatory java.lang.Object parameter.
   */

  @DISPID(20) //= 0x14. The runtime will prefer the VTID if present
  @VTID(28)
  void cversion(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cversion);


  /**
   * <p>
   * Getter method for the COM property "CVERSIONSNR"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(22) //= 0x16. The runtime will prefer the VTID if present
  @VTID(29)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object cversionsnr();


  /**
   * <p>
   * Setter method for the COM property "CVERSIONSNR"
   * </p>
   * @param cversionsnr Mandatory java.lang.Object parameter.
   */

  @DISPID(22) //= 0x16. The runtime will prefer the VTID if present
  @VTID(30)
  void cversionsnr(
    @MarshalAs(NativeType.VARIANT) java.lang.Object cversionsnr);


  /**
   * @param cDatabase Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(24) //= 0x18. The runtime will prefer the VTID if present
  @VTID(31)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object do_open(
    java.lang.Object cDatabase);


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(25) //= 0x19. The runtime will prefer the VTID if present
  @VTID(32)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object do_close();


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(26) //= 0x1a. The runtime will prefer the VTID if present
  @VTID(33)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object do_getwglist();


  /**
   * @param cWG Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(27) //= 0x1b. The runtime will prefer the VTID if present
  @VTID(34)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object do_getwg(
    java.lang.Object cWG);


  /**
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(28) //= 0x1c. The runtime will prefer the VTID if present
  @VTID(35)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object do_getchangedwglist();


  /**
   * @param cWG Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(29) //= 0x1d. The runtime will prefer the VTID if present
  @VTID(36)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object do_setbestaetigt(
    java.lang.Object cWG);


  /**
   * <p>
   * Specifies the name used to reference an object in code.
   * </p>
   * <p>
   * Getter method for the COM property "Name"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(30) //= 0x1e. The runtime will prefer the VTID if present
  @VTID(37)
  java.lang.String name();


  /**
   * <p>
   * Specifies the name used to reference an object in code.
   * </p>
   * <p>
   * Setter method for the COM property "Name"
   * </p>
   * @param name Mandatory java.lang.String parameter.
   */

  @DISPID(30) //= 0x1e. The runtime will prefer the VTID if present
  @VTID(38)
  void name(
    java.lang.String name);


  /**
   * <p>
   * Occurs when an object is created.
   * </p>
   * @param initParm1 Mandatory java.lang.Object parameter.
   * @param initParm2 Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(32) //= 0x20. The runtime will prefer the VTID if present
  @VTID(39)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object init(
    @MarshalAs(NativeType.VARIANT) java.lang.Object initParm1,
    @MarshalAs(NativeType.VARIANT) java.lang.Object initParm2);


  /**
   * <p>
   * Occurs when an object is released.
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(33) //= 0x21. The runtime will prefer the VTID if present
  @VTID(40)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object destroy();


  /**
   * <p>
   * Occurs when there is a run-time error in a method.
   * </p>
   * @param nError Mandatory int parameter.
   * @param cMethod Mandatory java.lang.String parameter.
   * @param nLine Mandatory int parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(34) //= 0x22. The runtime will prefer the VTID if present
  @VTID(41)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object error(
    int nError,
    java.lang.String cMethod,
    int nLine);


  /**
   * <p>
   * Contains the expression entered for a property value in the property sheet.
   * </p>
   * @param cPropertyName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(35) //= 0x23. The runtime will prefer the VTID if present
  @VTID(42)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object readExpression(
    java.lang.String cPropertyName);


  /**
   * <p>
   * Returns the text of the specified method.
   * </p>
   * @param cMethod Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(36) //= 0x24. The runtime will prefer the VTID if present
  @VTID(43)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object readMethod(
    java.lang.String cMethod);


  /**
   * <p>
   * Resets the property/method to the inherited value.
   * </p>
   * @param cProperty Mandatory int parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(37) //= 0x25. The runtime will prefer the VTID if present
  @VTID(44)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object resetToDefault(
    int cProperty);


  /**
   * <p>
   * Saves an instance of an object as a class definition in a class library.
   * </p>
   * @param cClassLibName Mandatory java.lang.String parameter.
   * @param cClassName Mandatory java.lang.String parameter.
   * @param cDescription Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(38) //= 0x26. The runtime will prefer the VTID if present
  @VTID(45)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object saveAsClass(
    java.lang.String cClassLibName,
    java.lang.String cClassName,
    java.lang.String cDescription);


  /**
   * <p>
   * Writes an expression to a property.
   * </p>
   * @param cPropertyName Mandatory java.lang.String parameter.
   * @param cExpression Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(39) //= 0x27. The runtime will prefer the VTID if present
  @VTID(46)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object writeExpression(
    java.lang.String cPropertyName,
    java.lang.String cExpression);


  /**
   * <p>
   * Writes the specified text to the specified method.
   * </p>
   * @param cMethodName Mandatory java.lang.String parameter.
   * @param cMethodText Mandatory java.lang.String parameter.
   * @param lCreateMethod Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(40) //= 0x28. The runtime will prefer the VTID if present
  @VTID(47)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object writeMethod(
    java.lang.String cMethodName,
    java.lang.String cMethodText,
    java.lang.String lCreateMethod);


  /**
   * <p>
   * Adds a new property to an object.
   * </p>
   * @param cPropertyName Mandatory java.lang.String parameter.
   * @param vNewValue Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(41) //= 0x29. The runtime will prefer the VTID if present
  @VTID(48)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object addProperty(
    java.lang.String cPropertyName,
    @MarshalAs(NativeType.VARIANT) java.lang.Object vNewValue);


  /**
   * <p>
   * Specifies the name of the Visual FoxPro base class on which the referenced object is based.
   * </p>
   * <p>
   * Getter method for the COM property "BaseClass"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(42) //= 0x2a. The runtime will prefer the VTID if present
  @VTID(49)
  java.lang.String baseClass();


  /**
   * <p>
   * Returns the name of the class that an object is based on.
   * </p>
   * <p>
   * Getter method for the COM property "Class"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(43) //= 0x2b. The runtime will prefer the VTID if present
  @VTID(50)
  java.lang.String _class();


  /**
   * <p>
   * Returns the name of the parent class on which the object is based.
   * </p>
   * <p>
   * Getter method for the COM property "ParentClass"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(44) //= 0x2c. The runtime will prefer the VTID if present
  @VTID(51)
  java.lang.String parentClass();


  /**
   * <p>
   * References the container object of a control.
   * </p>
   * <p>
   * Getter method for the COM property "Parent"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(45) //= 0x2d. The runtime will prefer the VTID if present
  @VTID(52)
  int parent();


  /**
   * <p>
   * Stores information about an object.
   * </p>
   * <p>
   * Getter method for the COM property "Comment"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(46) //= 0x2e. The runtime will prefer the VTID if present
  @VTID(53)
  java.lang.String comment();


  /**
   * <p>
   * Stores information about an object.
   * </p>
   * <p>
   * Setter method for the COM property "Comment"
   * </p>
   * @param comment Mandatory java.lang.String parameter.
   */

  @DISPID(46) //= 0x2e. The runtime will prefer the VTID if present
  @VTID(54)
  void comment(
    java.lang.String comment);


  /**
   * <p>
   * Stores any extra data needed for your program.
   * </p>
   * <p>
   * Getter method for the COM property "Tag"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(48) //= 0x30. The runtime will prefer the VTID if present
  @VTID(55)
  java.lang.String tag();


  /**
   * <p>
   * Stores any extra data needed for your program.
   * </p>
   * <p>
   * Setter method for the COM property "Tag"
   * </p>
   * @param tag Mandatory java.lang.String parameter.
   */

  @DISPID(48) //= 0x30. The runtime will prefer the VTID if present
  @VTID(56)
  void tag(
    java.lang.String tag);


  /**
   * <p>
   * Specifies the height of an object on the screen.
   * </p>
   * <p>
   * Getter method for the COM property "Height"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(50) //= 0x32. The runtime will prefer the VTID if present
  @VTID(57)
  int height();


  /**
   * <p>
   * Specifies the height of an object on the screen.
   * </p>
   * <p>
   * Setter method for the COM property "Height"
   * </p>
   * @param height Mandatory int parameter.
   */

  @DISPID(50) //= 0x32. The runtime will prefer the VTID if present
  @VTID(58)
  void height(
    int height);


  /**
   * <p>
   * Specifies the width of an object.
   * </p>
   * <p>
   * Getter method for the COM property "Width"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(52) //= 0x34. The runtime will prefer the VTID if present
  @VTID(59)
  int width();


  /**
   * <p>
   * Specifies the width of an object.
   * </p>
   * <p>
   * Setter method for the COM property "Width"
   * </p>
   * @param width Mandatory int parameter.
   */

  @DISPID(52) //= 0x34. The runtime will prefer the VTID if present
  @VTID(60)
  void width(
    int width);


  /**
   * <p>
   * Adds an object to a container object at run time.
   * </p>
   * @param cName Mandatory java.lang.String parameter.
   * @param cClass Mandatory java.lang.String parameter.
   * @param cOLEClass Mandatory java.lang.String parameter.
   * @param aInit1 Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(54) //= 0x36. The runtime will prefer the VTID if present
  @VTID(61)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object addObject(
    java.lang.String cName,
    java.lang.String cClass,
    java.lang.String cOLEClass,
    @MarshalAs(NativeType.VARIANT) java.lang.Object aInit1);


  /**
   * <p>
   * Adds an object to a container object at run time.
   * </p>
   * @param cName Mandatory java.lang.String parameter.
   * @param cClass Mandatory java.lang.String parameter.
   * @param cModule Mandatory java.lang.String parameter.
   * @param cInApplication Mandatory java.lang.String parameter.
   * @param cOLEClass Mandatory java.lang.String parameter.
   * @param aInit1 Mandatory java.lang.Object parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(55) //= 0x37. The runtime will prefer the VTID if present
  @VTID(62)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object newObject(
    java.lang.String cName,
    java.lang.String cClass,
    java.lang.String cModule,
    java.lang.String cInApplication,
    java.lang.String cOLEClass,
    @MarshalAs(NativeType.VARIANT) java.lang.Object aInit1);


  /**
   * <p>
   * Removes a specified object from a Container object at run time.
   * </p>
   * @param cObjectName Mandatory java.lang.String parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(56) //= 0x38. The runtime will prefer the VTID if present
  @VTID(63)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object removeObject(
    java.lang.String cObjectName);


  /**
   * <p>
   * Specifies a context ID for a topic in a Help file to provide context-sensitive Help.
   * </p>
   * <p>
   * Getter method for the COM property "HelpContextID"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(57) //= 0x39. The runtime will prefer the VTID if present
  @VTID(64)
  int helpContextID();


  /**
   * <p>
   * Specifies a context ID for a topic in a Help file to provide context-sensitive Help.
   * </p>
   * <p>
   * Setter method for the COM property "HelpContextID"
   * </p>
   * @param helpContextID Mandatory int parameter.
   */

  @DISPID(57) //= 0x39. The runtime will prefer the VTID if present
  @VTID(65)
  void helpContextID(
    int helpContextID);


  /**
   * <p>
   * Specifies a Help topic context ID to provide What's This help for an object.
   * </p>
   * <p>
   * Getter method for the COM property "WhatsThisHelpID"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(59) //= 0x3b. The runtime will prefer the VTID if present
  @VTID(66)
  int whatsThisHelpID();


  /**
   * <p>
   * Specifies a Help topic context ID to provide What's This help for an object.
   * </p>
   * <p>
   * Setter method for the COM property "WhatsThisHelpID"
   * </p>
   * @param whatsThisHelpID Mandatory int parameter.
   */

  @DISPID(59) //= 0x3b. The runtime will prefer the VTID if present
  @VTID(67)
  void whatsThisHelpID(
    int whatsThisHelpID);


  /**
   * <p>
   * Displays the Help topic specified for an object with the WhatsThisHelpID property.
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(61) //= 0x3d. The runtime will prefer the VTID if present
  @VTID(68)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object showWhatsThis();


  /**
   * <p>
   * An array for accessing the controls in a container object.
   * </p>
   * <p>
   * Getter method for the COM property "Controls"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(62) //= 0x3e. The runtime will prefer the VTID if present
  @VTID(69)
  int controls();


  /**
   * <p>
   * Specifies the number of controls in a container object.
   * </p>
   * <p>
   * Getter method for the COM property "ControlCount"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(63) //= 0x3f. The runtime will prefer the VTID if present
  @VTID(70)
  int controlCount();


  /**
   * <p>
   * An array for accessing the objects in a container object.
   * </p>
   * <p>
   * Getter method for the COM property "Objects"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(64) //= 0x40. The runtime will prefer the VTID if present
  @VTID(71)
  int objects();


  // Properties:
}

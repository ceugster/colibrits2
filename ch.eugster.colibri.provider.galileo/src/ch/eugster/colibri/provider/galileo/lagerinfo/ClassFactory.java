package ch.eugster.colibri.provider.galileo.lagerinfo  ;

import com4j.*;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
  private ClassFactory() {} // instanciation is not allowed


  /**
   * lagerinfo.LaInfo
   */
  public static ch.eugster.colibri.provider.galileo.lagerinfo.Ilainfo createLaInfo() {
    return COM4J.createInstance( ch.eugster.colibri.provider.galileo.lagerinfo.Ilainfo.class, "{EF8D3482-503F-11D8-B851-0002E3178697}" );
  }
}

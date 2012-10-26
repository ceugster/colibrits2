package ch.eugster.colibri.provider.galileo.galserve  ;

import com4j.*;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
  private ClassFactory() {} // instanciation is not allowed


  public static ch.eugster.colibri.provider.galileo.galserve.Igdserve creategdserve() {
    return COM4J.createInstance( ch.eugster.colibri.provider.galileo.galserve.Igdserve.class, "{B94E7B40-4E78-11D8-B851-0002E3178697}" );
  }
}

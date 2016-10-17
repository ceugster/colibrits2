package ch.eugster.colibri.provider.galileo.galserve.old  ;

import com4j.*;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
  private ClassFactory() {} // instanciation is not allowed


  public static ch.eugster.colibri.provider.galileo.galserve.old.Igdserve creategdserve() {
    return COM4J.createInstance( ch.eugster.colibri.provider.galileo.galserve.old.Igdserve.class, "{B94E7B40-4E78-11D8-B851-0002E3178697}" );
  }
}

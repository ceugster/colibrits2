package ch.eugster.colibri.provider.galileo.wgserve.old  ;

import com4j.COM4J;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
  private ClassFactory() {} // instanciation is not allowed


  public static ch.eugster.colibri.provider.galileo.wgserve.old.Iwgserve createwgserve() {
    return COM4J.createInstance( ch.eugster.colibri.provider.galileo.wgserve.old.Iwgserve.class, "{9C3F93A0-270D-11D8-B851-0002E3178697}" );
  }
}

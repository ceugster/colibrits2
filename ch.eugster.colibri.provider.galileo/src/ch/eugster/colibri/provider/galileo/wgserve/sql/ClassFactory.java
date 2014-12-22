package ch.eugster.colibri.provider.galileo.wgserve.sql  ;

import com4j.COM4J;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
  private ClassFactory() {} // instanciation is not allowed


  /**
   * wgserve2g.wgserve2G
   */
  public static ch.eugster.colibri.provider.galileo.wgserve.sql.Iwgserve2g createwgserve2g() {
    return COM4J.createInstance( ch.eugster.colibri.provider.galileo.wgserve.sql.Iwgserve2g.class, "{C17F8636-6116-4A5F-9FBD-00EDA02436C6}" );
  }
}

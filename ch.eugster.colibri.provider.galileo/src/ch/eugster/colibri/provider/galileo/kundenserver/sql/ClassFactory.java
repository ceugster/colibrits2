package ch.eugster.colibri.provider.galileo.kundenserver.sql  ;

import com4j.*;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
  private ClassFactory() {} // instanciation is not allowed


  /**
   * kundenserver2g.kundenserver2g
   */
  public static ch.eugster.colibri.provider.galileo.kundenserver.sql.Ikundenserver2g createkundenserver2g() {
    return COM4J.createInstance( ch.eugster.colibri.provider.galileo.kundenserver.sql.Ikundenserver2g.class, "{2F49BB6A-C419-4CD7-8AC5-2F17F7DEE9A1}" );
  }
}

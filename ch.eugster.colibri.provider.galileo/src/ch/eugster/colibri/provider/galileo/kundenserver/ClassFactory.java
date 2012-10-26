package ch.eugster.colibri.provider.galileo.kundenserver  ;

import com4j.*;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
  private ClassFactory() {} // instanciation is not allowed


  /**
   * kundenserver.kundenserver
   */
  public static ch.eugster.colibri.provider.galileo.kundenserver.Ikundenserver createkundenserver() {
    return COM4J.createInstance( ch.eugster.colibri.provider.galileo.kundenserver.Ikundenserver.class, "{1DAA0DEE-0086-4FB7-8587-B66D13E75AC3}" );
  }
}

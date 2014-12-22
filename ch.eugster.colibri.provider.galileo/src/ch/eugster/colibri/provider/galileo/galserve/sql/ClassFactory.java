package ch.eugster.colibri.provider.galileo.galserve.sql  ;

import com4j.COM4J;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
  private ClassFactory() {} // instanciation is not allowed


  /**
   * galserve2g.gdserve2g
   */
  public static ch.eugster.colibri.provider.galileo.galserve.sql.Igdserve2g creategdserve2g() {
    return COM4J.createInstance( ch.eugster.colibri.provider.galileo.galserve.sql.Igdserve2g.class, "{D692A1C5-4DB7-4944-AD56-D7EBC8E52499}" );
  }
}

package ch.eugster.colibri.provider.galileo.galserve;

import com4j.DISPID;
import com4j.DefaultMethod;
import com4j.MarshalAs;
import com4j.NativeType;
import com4j.ReturnValue;
import com4j.VTID;

public interface Igdserve {

	// Methods:
	/**
	 * <p>
	 * Getter method for the COM property "GEFUNDEN"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@DefaultMethod
	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object gefunden();

	/**
	 * <p>
	 * Setter method for the COM property "GEFUNDEN"
	 * </p>
	 * @param gefunden Mandatory java.lang.Object parameter.
	 */

	@DefaultMethod
	void gefunden(@MarshalAs(NativeType.VARIANT) java.lang.Object gefunden);

	/**
	 * <p>
	 * Getter method for the COM property "AUTOR"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object autor();

	/**
	 * <p>
	 * Setter method for the COM property "AUTOR"
	 * </p>
	 * @param autor Mandatory java.lang.Object parameter.
	 */

	void autor(@MarshalAs(NativeType.VARIANT) java.lang.Object autor);

	/**
	 * <p>
	 * Getter method for the COM property "TITEL"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object titel();

	/**
	 * <p>
	 * Setter method for the COM property "TITEL"
	 * </p>
	 * @param titel Mandatory java.lang.Object parameter.
	 */

	void titel(@MarshalAs(NativeType.VARIANT) java.lang.Object titel);

	/**
	 * <p>
	 * Getter method for the COM property "VERLAG"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object verlag();

	/**
	 * <p>
	 * Setter method for the COM property "VERLAG"
	 * </p>
	 * @param verlag Mandatory java.lang.Object parameter.
	 */

	void verlag(@MarshalAs(NativeType.VARIANT) java.lang.Object verlag);

	/**
	 * <p>
	 * Getter method for the COM property "MWST"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object mwst();

	/**
	 * <p>
	 * Setter method for the COM property "MWST"
	 * </p>
	 * @param mwst Mandatory java.lang.Object parameter.
	 */

	void mwst(@MarshalAs(NativeType.VARIANT) java.lang.Object mwst);

	/**
	 * <p>
	 * Getter method for the COM property "PREIS"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object preis();

	/**
	 * <p>
	 * Setter method for the COM property "PREIS"
	 * </p>
	 * @param preis Mandatory java.lang.Object parameter.
	 */

	void preis(@MarshalAs(NativeType.VARIANT) java.lang.Object preis);

	/**
	 * <p>
	 * Getter method for the COM property "WGRUPPE"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object wgruppe();

	/**
	 * <p>
	 * Setter method for the COM property "WGRUPPE"
	 * </p>
	 * @param wgruppe Mandatory java.lang.Object parameter.
	 */

	void wgruppe(@MarshalAs(NativeType.VARIANT) java.lang.Object wgruppe);

	/**
	 * <p>
	 * Getter method for the COM property "bestellt"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object bestellt();

	/**
	 * <p>
	 * Setter method for the COM property "bestellt"
	 * </p>
	 * @param bestellt Mandatory java.lang.Object parameter.
	 */

	void bestellt(@MarshalAs(NativeType.VARIANT) java.lang.Object bestellt);

	/**
	 * <p>
	 * Getter method for the COM property "lagerabholfach"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object lagerabholfach();

	/**
	 * <p>
	 * Setter method for the COM property "lagerabholfach"
	 * </p>
	 * @param lagerabholfach Mandatory java.lang.Object parameter.
	 */

	void lagerabholfach(
			@MarshalAs(NativeType.VARIANT) java.lang.Object lagerabholfach);

	/**
	 * <p>
	 * Getter method for the COM property "ISBN"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object isbn();

	/**
	 * <p>
	 * Setter method for the COM property "ISBN"
	 * </p>
	 * @param isbn Mandatory java.lang.Object parameter.
	 */

	void isbn(@MarshalAs(NativeType.VARIANT) java.lang.Object isbn);

	/**
	 * <p>
	 * Getter method for the COM property "BZNR"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object bznr();

	/**
	 * <p>
	 * Setter method for the COM property "BZNR"
	 * </p>
	 * @param bznr Mandatory java.lang.Object parameter.
	 */

	void bznr(@MarshalAs(NativeType.VARIANT) java.lang.Object bznr);

	/**
	 * <p>
	 * Getter method for the COM property "BESTNUMMER"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object bestnummer();

	/**
	 * <p>
	 * Setter method for the COM property "BESTNUMMER"
	 * </p>
	 * @param bestnummer Mandatory java.lang.Object parameter.
	 */

	void bestnummer(@MarshalAs(NativeType.VARIANT) java.lang.Object bestnummer);

	/**
	 * <p>
	 * Getter method for the COM property "GELOESCHT"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object geloescht();

	/**
	 * <p>
	 * Setter method for the COM property "GELOESCHT"
	 * </p>
	 * @param geloescht Mandatory java.lang.Object parameter.
	 */

	void geloescht(@MarshalAs(NativeType.VARIANT) java.lang.Object geloescht);

	/**
	 * <p>
	 * Getter method for the COM property "kundennr"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object kundennr();

	/**
	 * <p>
	 * Setter method for the COM property "kundennr"
	 * </p>
	 * @param kundennr Mandatory java.lang.Object parameter.
	 */

	void kundennr(@MarshalAs(NativeType.VARIANT) java.lang.Object kundennr);

	/**
	 * <p>
	 * Getter method for the COM property "KEINRABATT"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object keinrabatt();

	/**
	 * <p>
	 * Setter method for the COM property "KEINRABATT"
	 * </p>
	 * @param keinrabatt Mandatory java.lang.Object parameter.
	 */

	void keinrabatt(@MarshalAs(NativeType.VARIANT) java.lang.Object keinrabatt);

	/**
	 * <p>
	 * Getter method for the COM property "VTRANSWRITE"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object vtranswrite();

	/**
	 * <p>
	 * Setter method for the COM property "VTRANSWRITE"
	 * </p>
	 * @param vtranswrite Mandatory java.lang.Object parameter.
	 */

	void vtranswrite(@MarshalAs(NativeType.VARIANT) java.lang.Object vtranswrite);

	/**
	 * <p>
	 * Getter method for the COM property "VLAGERUPDATE"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object vlagerupdate();

	/**
	 * <p>
	 * Setter method for the COM property "VLAGERUPDATE"
	 * </p>
	 * @param vlagerupdate Mandatory java.lang.Object parameter.
	 */

	void vlagerupdate(
			@MarshalAs(NativeType.VARIANT) java.lang.Object vlagerupdate);

	/**
	 * <p>
	 * Getter method for the COM property "VNUMMER"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object vnummer();

	/**
	 * <p>
	 * Setter method for the COM property "VNUMMER"
	 * </p>
	 * @param vnummer Mandatory java.lang.Object parameter.
	 */

	void vnummer(@MarshalAs(NativeType.VARIANT) java.lang.Object vnummer);

	/**
	 * <p>
	 * Getter method for the COM property "VPREIS"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object vpreis();

	/**
	 * <p>
	 * Setter method for the COM property "VPREIS"
	 * </p>
	 * @param vpreis Mandatory java.lang.Object parameter.
	 */

	void vpreis(@MarshalAs(NativeType.VARIANT) java.lang.Object vpreis);

	/**
	 * <p>
	 * Getter method for the COM property "VMWST"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object vmwst();

	/**
	 * <p>
	 * Setter method for the COM property "VMWST"
	 * </p>
	 * @param vmwst Mandatory java.lang.Object parameter.
	 */

	void vmwst(@MarshalAs(NativeType.VARIANT) java.lang.Object vmwst);

	/**
	 * <p>
	 * Getter method for the COM property "VWGRUPPE"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object vwgruppe();

	/**
	 * <p>
	 * Setter method for the COM property "VWGRUPPE"
	 * </p>
	 * @param vwgruppe Mandatory java.lang.Object parameter.
	 */

	void vwgruppe(@MarshalAs(NativeType.VARIANT) java.lang.Object vwgruppe);

	/**
	 * <p>
	 * Getter method for the COM property "VMENGE"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object vmenge();

	/**
	 * <p>
	 * Setter method for the COM property "VMENGE"
	 * </p>
	 * @param vmenge Mandatory java.lang.Object parameter.
	 */

	void vmenge(@MarshalAs(NativeType.VARIANT) java.lang.Object vmenge);

	/**
	 * <p>
	 * Getter method for the COM property "VRABATT"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object vrabatt();

	/**
	 * <p>
	 * Setter method for the COM property "VRABATT"
	 * </p>
	 * @param vrabatt Mandatory java.lang.Object parameter.
	 */

	void vrabatt(@MarshalAs(NativeType.VARIANT) java.lang.Object vrabatt);

	/**
	 * <p>
	 * Getter method for the COM property "VBESTELLT"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object vbestellt();

	/**
	 * <p>
	 * Setter method for the COM property "VBESTELLT"
	 * </p>
	 * @param vbestellt Mandatory java.lang.Object parameter.
	 */

	void vbestellt(@MarshalAs(NativeType.VARIANT) java.lang.Object vbestellt);

	/**
	 * <p>
	 * Getter method for the COM property "VLAGERABHOLFACH"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object vlagerabholfach();

	/**
	 * <p>
	 * Setter method for the COM property "VLAGERABHOLFACH"
	 * </p>
	 * @param vlagerabholfach Mandatory java.lang.Object parameter.
	 */

	void vlagerabholfach(
			@MarshalAs(NativeType.VARIANT) java.lang.Object vlagerabholfach);

	/**
	 * <p>
	 * Getter method for the COM property "VKUNDENNR"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object vkundennr();

	/**
	 * <p>
	 * Setter method for the COM property "VKUNDENNR"
	 * </p>
	 * @param vkundennr Mandatory java.lang.Object parameter.
	 */

	void vkundennr(@MarshalAs(NativeType.VARIANT) java.lang.Object vkundennr);

	/**
	 * <p>
	 * Getter method for the COM property "VCOUPONNR"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object vcouponnr();

	/**
	 * <p>
	 * Setter method for the COM property "VCOUPONNR"
	 * </p>
	 * @param vcouponnr Mandatory java.lang.Object parameter.
	 */

	void vcouponnr(@MarshalAs(NativeType.VARIANT) java.lang.Object vcouponnr);

	/**
	 * <p>
	 * Getter method for the COM property "VWGNAME"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object vwgname();

	/**
	 * <p>
	 * Setter method for the COM property "VWGNAME"
	 * </p>
	 * @param vwgname Mandatory java.lang.Object parameter.
	 */

	void vwgname(@MarshalAs(NativeType.VARIANT) java.lang.Object vwgname);

	/**
	 * <p>
	 * Getter method for the COM property "VEBOOK"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object vebook();

	/**
	 * <p>
	 * Setter method for the COM property "VEBOOK"
	 * </p>
	 * @param vebook Mandatory java.lang.Object parameter.
	 */

	void vebook(@MarshalAs(NativeType.VARIANT) java.lang.Object vebook);

	/**
	 * <p>
	 * Getter method for the COM property "BESTAND"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object bestand();

	/**
	 * <p>
	 * Setter method for the COM property "BESTAND"
	 * </p>
	 * @param bestand Mandatory java.lang.Object parameter.
	 */

	void bestand(@MarshalAs(NativeType.VARIANT) java.lang.Object bestand);

	/**
	 * <p>
	 * Getter method for the COM property "LASTVKDAT"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object lastvkdat();

	/**
	 * <p>
	 * Setter method for the COM property "LASTVKDAT"
	 * </p>
	 * @param lastvkdat Mandatory java.lang.Object parameter.
	 */

	void lastvkdat(@MarshalAs(NativeType.VARIANT) java.lang.Object lastvkdat);

	/**
	 * <p>
	 * Getter method for the COM property "MENGE"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object menge();

	/**
	 * <p>
	 * Setter method for the COM property "MENGE"
	 * </p>
	 * @param menge Mandatory java.lang.Object parameter.
	 */

	void menge(@MarshalAs(NativeType.VARIANT) java.lang.Object menge);

	/**
	 * <p>
	 * Getter method for the COM property "GALDB"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object galdb();

	/**
	 * <p>
	 * Setter method for the COM property "GALDB"
	 * </p>
	 * @param galdb Mandatory java.lang.Object parameter.
	 */

	void galdb(@MarshalAs(NativeType.VARIANT) java.lang.Object galdb);

	/**
	 * <p>
	 * Getter method for the COM property "GALVERSION"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object galversion();

	/**
	 * <p>
	 * Setter method for the COM property "GALVERSION"
	 * </p>
	 * @param galversion Mandatory java.lang.Object parameter.
	 */

	void galversion(@MarshalAs(NativeType.VARIANT) java.lang.Object galversion);

	/**
	 * <p>
	 * Getter method for the COM property "LCDGEFUNDEN"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object lcdgefunden();

	/**
	 * <p>
	 * Setter method for the COM property "LCDGEFUNDEN"
	 * </p>
	 * @param lcdgefunden Mandatory java.lang.Object parameter.
	 */

	void lcdgefunden(@MarshalAs(NativeType.VARIANT) java.lang.Object lcdgefunden);

	/**
	 * <p>
	 * Getter method for the COM property "LCDSUCHE"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object lcdsuche();

	/**
	 * <p>
	 * Setter method for the COM property "LCDSUCHE"
	 * </p>
	 * @param lcdsuche Mandatory java.lang.Object parameter.
	 */

	void lcdsuche(@MarshalAs(NativeType.VARIANT) java.lang.Object lcdsuche);

	/**
	 * <p>
	 * Getter method for the COM property "CBIBINI"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object cbibini();

	/**
	 * <p>
	 * Setter method for the COM property "CBIBINI"
	 * </p>
	 * @param cbibini Mandatory java.lang.Object parameter.
	 */

	void cbibini(@MarshalAs(NativeType.VARIANT) java.lang.Object cbibini);

	/**
	 * <p>
	 * Getter method for the COM property "LCDERROR"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object lcderror();

	/**
	 * <p>
	 * Setter method for the COM property "LCDERROR"
	 * </p>
	 * @param lcderror Mandatory java.lang.Object parameter.
	 */

	void lcderror(@MarshalAs(NativeType.VARIANT) java.lang.Object lcderror);

	/**
	 * <p>
	 * Getter method for the COM property "CCDERROR"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object ccderror();

	/**
	 * <p>
	 * Setter method for the COM property "CCDERROR"
	 * </p>
	 * @param ccderror Mandatory java.lang.Object parameter.
	 */

	void ccderror(@MarshalAs(NativeType.VARIANT) java.lang.Object ccderror);

	/**
	 * <p>
	 * Getter method for the COM property "CLIZENZPATH"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object clizenzpath();

	/**
	 * <p>
	 * Setter method for the COM property "CLIZENZPATH"
	 * </p>
	 * @param clizenzpath Mandatory java.lang.Object parameter.
	 */

	void clizenzpath(@MarshalAs(NativeType.VARIANT) java.lang.Object clizenzpath);

	/**
	 * <p>
	 * Getter method for the COM property "NICHTBUCHEN"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object nichtbuchen();

	/**
	 * <p>
	 * Setter method for the COM property "NICHTBUCHEN"
	 * </p>
	 * @param nichtbuchen Mandatory java.lang.Object parameter.
	 */

	void nichtbuchen(@MarshalAs(NativeType.VARIANT) java.lang.Object nichtbuchen);

	/**
	 * <p>
	 * Getter method for the COM property "LLOG"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object llog();

	/**
	 * <p>
	 * Setter method for the COM property "LLOG"
	 * </p>
	 * @param llog Mandatory java.lang.Object parameter.
	 */

	void llog(@MarshalAs(NativeType.VARIANT) java.lang.Object llog);

	/**
	 * <p>
	 * Getter method for the COM property "CLOGNAME"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object clogname();

	/**
	 * <p>
	 * Setter method for the COM property "CLOGNAME"
	 * </p>
	 * @param clogname Mandatory java.lang.Object parameter.
	 */

	void clogname(@MarshalAs(NativeType.VARIANT) java.lang.Object clogname);

	/**
	 * <p>
	 * Getter method for the COM property "LERROR"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object lerror();

	/**
	 * <p>
	 * Setter method for the COM property "LERROR"
	 * </p>
	 * @param lerror Mandatory java.lang.Object parameter.
	 */

	void lerror(@MarshalAs(NativeType.VARIANT) java.lang.Object lerror);

	/**
	 * <p>
	 * Getter method for the COM property "CERRTEXT"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object cerrtext();

	/**
	 * <p>
	 * Setter method for the COM property "CERRTEXT"
	 * </p>
	 * @param cerrtext Mandatory java.lang.Object parameter.
	 */

	void cerrtext(@MarshalAs(NativeType.VARIANT) java.lang.Object cerrtext);

	/**
	 * <p>
	 * Getter method for the COM property "CANREDE"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object canrede();

	/**
	 * <p>
	 * Setter method for the COM property "CANREDE"
	 * </p>
	 * @param canrede Mandatory java.lang.Object parameter.
	 */

	void canrede(@MarshalAs(NativeType.VARIANT) java.lang.Object canrede);

	/**
	 * <p>
	 * Getter method for the COM property "CTITEL"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object ctitel();

	/**
	 * <p>
	 * Setter method for the COM property "CTITEL"
	 * </p>
	 * @param ctitel Mandatory java.lang.Object parameter.
	 */

	void ctitel(@MarshalAs(NativeType.VARIANT) java.lang.Object ctitel);

	/**
	 * <p>
	 * Getter method for the COM property "CVORNAME"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object cvorname();

	/**
	 * <p>
	 * Setter method for the COM property "CVORNAME"
	 * </p>
	 * @param cvorname Mandatory java.lang.Object parameter.
	 */

	void cvorname(@MarshalAs(NativeType.VARIANT) java.lang.Object cvorname);

	/**
	 * <p>
	 * Getter method for the COM property "CNAME1"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object cnamE1();

	/**
	 * <p>
	 * Setter method for the COM property "CNAME1"
	 * </p>
	 * @param cnamE1 Mandatory java.lang.Object parameter.
	 */

	void cnamE1(@MarshalAs(NativeType.VARIANT) java.lang.Object cnamE1);

	/**
	 * <p>
	 * Getter method for the COM property "CNAME2"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object cnamE2();

	/**
	 * <p>
	 * Setter method for the COM property "CNAME2"
	 * </p>
	 * @param cnamE2 Mandatory java.lang.Object parameter.
	 */

	void cnamE2(@MarshalAs(NativeType.VARIANT) java.lang.Object cnamE2);

	/**
	 * <p>
	 * Getter method for the COM property "CNAME3"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object cnamE3();

	/**
	 * <p>
	 * Setter method for the COM property "CNAME3"
	 * </p>
	 * @param cnamE3 Mandatory java.lang.Object parameter.
	 */

	void cnamE3(@MarshalAs(NativeType.VARIANT) java.lang.Object cnamE3);

	/**
	 * <p>
	 * Getter method for the COM property "CSTRASSE"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object cstrasse();

	/**
	 * <p>
	 * Setter method for the COM property "CSTRASSE"
	 * </p>
	 * @param cstrasse Mandatory java.lang.Object parameter.
	 */

	void cstrasse(@MarshalAs(NativeType.VARIANT) java.lang.Object cstrasse);

	/**
	 * <p>
	 * Getter method for the COM property "CLAND"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object cland();

	/**
	 * <p>
	 * Setter method for the COM property "CLAND"
	 * </p>
	 * @param cland Mandatory java.lang.Object parameter.
	 */

	void cland(@MarshalAs(NativeType.VARIANT) java.lang.Object cland);

	/**
	 * <p>
	 * Getter method for the COM property "CPLZ"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object cplz();

	/**
	 * <p>
	 * Setter method for the COM property "CPLZ"
	 * </p>
	 * @param cplz Mandatory java.lang.Object parameter.
	 */

	void cplz(@MarshalAs(NativeType.VARIANT) java.lang.Object cplz);

	/**
	 * <p>
	 * Getter method for the COM property "CORT"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object cort();

	/**
	 * <p>
	 * Setter method for the COM property "CORT"
	 * </p>
	 * @param cort Mandatory java.lang.Object parameter.
	 */

	void cort(@MarshalAs(NativeType.VARIANT) java.lang.Object cort);

	/**
	 * <p>
	 * Getter method for the COM property "CTELEFON"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object ctelefon();

	/**
	 * <p>
	 * Setter method for the COM property "CTELEFON"
	 * </p>
	 * @param ctelefon Mandatory java.lang.Object parameter.
	 */

	void ctelefon(@MarshalAs(NativeType.VARIANT) java.lang.Object ctelefon);

	/**
	 * <p>
	 * Getter method for the COM property "CTELEFON2"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object ctelefoN2();

	/**
	 * <p>
	 * Setter method for the COM property "CTELEFON2"
	 * </p>
	 * @param ctelefoN2 Mandatory java.lang.Object parameter.
	 */

	void ctelefoN2(@MarshalAs(NativeType.VARIANT) java.lang.Object ctelefoN2);

	/**
	 * <p>
	 * Getter method for the COM property "CTELEFAX"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object ctelefax();

	/**
	 * <p>
	 * Setter method for the COM property "CTELEFAX"
	 * </p>
	 * @param ctelefax Mandatory java.lang.Object parameter.
	 */

	void ctelefax(@MarshalAs(NativeType.VARIANT) java.lang.Object ctelefax);

	/**
	 * <p>
	 * Getter method for the COM property "CNATEL"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object cnatel();

	/**
	 * <p>
	 * Setter method for the COM property "CNATEL"
	 * </p>
	 * @param cnatel Mandatory java.lang.Object parameter.
	 */

	void cnatel(@MarshalAs(NativeType.VARIANT) java.lang.Object cnatel);

	/**
	 * <p>
	 * Getter method for the COM property "CEMAIL"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object cemail();

	/**
	 * <p>
	 * Setter method for the COM property "CEMAIL"
	 * </p>
	 * @param cemail Mandatory java.lang.Object parameter.
	 */

	void cemail(@MarshalAs(NativeType.VARIANT) java.lang.Object cemail);

	/**
	 * <p>
	 * Getter method for the COM property "NNACHLASS"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object nnachlass();

	/**
	 * <p>
	 * Setter method for the COM property "NNACHLASS"
	 * </p>
	 * @param nnachlass Mandatory java.lang.Object parameter.
	 */

	void nnachlass(@MarshalAs(NativeType.VARIANT) java.lang.Object nnachlass);

	/**
	 * <p>
	 * Getter method for the COM property "OCHECK"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object ocheck();

	/**
	 * <p>
	 * Setter method for the COM property "OCHECK"
	 * </p>
	 * @param ocheck Mandatory java.lang.Object parameter.
	 */

	void ocheck(@MarshalAs(NativeType.VARIANT) java.lang.Object ocheck);

	/**
	 * <p>
	 * Getter method for the COM property "LKUNDKARTE"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object lkundkarte();

	/**
	 * <p>
	 * Setter method for the COM property "LKUNDKARTE"
	 * </p>
	 * @param lkundkarte Mandatory java.lang.Object parameter.
	 */

	void lkundkarte(@MarshalAs(NativeType.VARIANT) java.lang.Object lkundkarte);

	/**
	 * <p>
	 * Getter method for the COM property "NKUNDKONTO"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object nkundkonto();

	/**
	 * <p>
	 * Setter method for the COM property "NKUNDKONTO"
	 * </p>
	 * @param nkundkonto Mandatory java.lang.Object parameter.
	 */

	void nkundkonto(@MarshalAs(NativeType.VARIANT) java.lang.Object nkundkonto);

	/**
	 * <p>
	 * Getter method for the COM property "CRGERROR"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object crgerror();

	/**
	 * <p>
	 * Setter method for the COM property "CRGERROR"
	 * </p>
	 * @param crgerror Mandatory java.lang.Object parameter.
	 */

	void crgerror(@MarshalAs(NativeType.VARIANT) java.lang.Object crgerror);

	/**
	 * <p>
	 * Getter method for the COM property "LKUNDFOUND"
	 * </p>
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object lkundfound();

	/**
	 * <p>
	 * Setter method for the COM property "LKUNDFOUND"
	 * </p>
	 * @param lkundfound Mandatory java.lang.Object parameter.
	 */

	void lkundfound(@MarshalAs(NativeType.VARIANT) java.lang.Object lkundfound);

	/**
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object iswws();

	/**
	 * @param cLizpath Mandatory java.lang.String parameter.
	 * @return  Returns a value of type boolean
	 */

	boolean do_NOpen(java.lang.String cLizpath);

	/**
	 * @return  Returns a value of type java.lang.Object
	 */

	@ReturnValue(type = NativeType.VARIANT)
	java.lang.Object do_NClose();

	/**
	 * @param cNummer Mandatory java.lang.String parameter.
	 * @return  Returns a value of type boolean
	 */

	boolean do_NSearch(java.lang.String cNummer);

	/**
	 * @param cNummer Mandatory java.lang.String parameter.
	 * @param nMenge Mandatory int parameter.
	 * @return  Returns a value of type boolean
	 */

	boolean do_delabholfach(java.lang.String cNummer, int nMenge);

	/**
	 * @param cNummer Mandatory java.lang.String parameter.
	 * @return  Returns a value of type boolean
	 */

	boolean do_verkauf(java.lang.String cNummer);

	/**
	 * @return  Returns a value of type boolean
	 */

	boolean do_wgverkauf();

	/**
	 * @return  Returns a value of type boolean
	 */

	boolean do_wgstorno();

	/**
	 * @param cNummer Mandatory java.lang.String parameter.
	 * @return  Returns a value of type boolean
	 */

	boolean do_storno(java.lang.String cNummer);

	/**
	 * @param cBSWG Mandatory java.lang.String parameter.
	 * @return  Returns a value of type java.lang.String
	 */

	java.lang.String getgalwg(java.lang.String cBSWG);

	/**
	 * @param cWGruppe Mandatory java.lang.String parameter.
	 * @return  Returns a value of type double
	 */

	double getekrabatt(java.lang.String cWGruppe);

	/**
	 * @param nKundennr Mandatory int parameter.
	 * @return  Returns a value of type boolean
	 */

	boolean do_getkunde(int nKundennr);

	/**
	 * @param nRgNummer Mandatory int parameter.
	 * @return  Returns a value of type boolean
	 */

	boolean do_BucheRechnung(int nRgNummer);

}
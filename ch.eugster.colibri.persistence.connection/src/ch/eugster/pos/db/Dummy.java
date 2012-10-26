/*
 * Created on 10.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ch.eugster.pos.db;

import java.util.Hashtable;

/**
 * @author administrator
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Dummy {

	/**
	 * 
	 */
	public Dummy() {
		super();
	}
	/**
	 * saves the values in ht to the fields of the current class. Has to be overridden in subclasses
	 * @param ht contains the name-value pair of the tables fields
	 */
	public void save(@SuppressWarnings("rawtypes") Hashtable ht) {
/*		Enumeration e = ht.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			if (fieldExists(key)) {
				Object o = ht.get(key);
				if (key.equals("id")) {
					id = (o instanceof Long) ? ((Long) o) : new Long(0l);
				}
				else if (key.equals("percentage")) {
					percentage = (o instanceof Double) ? (Double) o : new Double(0d);
				}
				else if (key.equals("validationDate")) {
					validationDate = (o instanceof Date) ? (Date) o : new Date();
				}
			}
		}*/
	}
/*	public void setValue(String name, Object value) {
		if (name.equals("visible")) {
			Assert.isTrue(value instanceof Boolean);
			visible = ((Boolean) value).booleanValue();
		}
		else if (name.equals("name")) {
			Assert.isTrue(value instanceof String);
			this.name = value.toString();
		}
	}
	
	public Object getValue(String name) {
		Object o = null;
		if (name.equals("visible")) {
			o = new Boolean(visible);
		}
		else if (name.equals("name")) {
			o = name;
		}
		return o;
	}
	
	public Object getDefault(String name) {
		Object o = null;
		if (name.equals("visible")) {
			o = new Boolean(false);
		}
		else if (name.equals("name")) {
			o = "";
		}
		return o;
	}
*/
}

/*
 * Created on 20.05.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ch.eugster.pos.db;

/**
 * @author administrator
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class Key extends Table
{
	
	public boolean textEditable = false;
	public int row = Table.INTEGER_DEFAULT_ZERO;
	public int column = Table.INTEGER_DEFAULT_ZERO;
	public int bgRed = Table.INTEGER_DEFAULT_255;
	public int bgGreen = Table.INTEGER_DEFAULT_255;
	public int bgBlue = Table.INTEGER_DEFAULT_255;
	public int bgRed2 = Table.INTEGER_DEFAULT_255;
	public int bgGreen2 = Table.INTEGER_DEFAULT_255;
	public int bgBlue2 = Table.INTEGER_DEFAULT_255;
	public int fgRed = Table.INTEGER_DEFAULT_ZERO;
	public int fgGreen = Table.INTEGER_DEFAULT_ZERO;
	public int fgBlue = Table.INTEGER_DEFAULT_ZERO;
	public double fontSize = 0d;
	public int fontStyle = Table.INTEGER_DEFAULT_ZERO;
	public int align = Table.INTEGER_DEFAULT_ZERO;
	public int valign = Table.INTEGER_DEFAULT_ZERO;
	public String text = ""; //$NON-NLS-1$
	public String command = ""; //$NON-NLS-1$
	public String imagepath = ""; //$NON-NLS-1$
	public int relHorizontalTextPos = Table.INTEGER_DEFAULT_ZERO;
	public int relVerticalTextPos = Table.INTEGER_DEFAULT_ZERO;
	public String className = ""; //$NON-NLS-1$
	public Integer actionType = new Integer(Table.INTEGER_DEFAULT_ZERO);
	
	/**
	 * 
	 */
	public Key()
	{
		super();
	}
	
	public void copyId(Key key)
	{
		this.setId(key.getId());
	}
	
	// public PosButton createButton(UserPanel context, PosEventListener
	// listener)
	// {
	// PosButton button = this.createButton(context);
	// if (button != null && button.getAction() instanceof Action)
	// {
	// Action act = (Action) button.getAction();
	// if (act.getKey() instanceof CustomKey)
	// {
	// CustomKey k = (CustomKey) act.getKey();
	// if (k.setDefaultTab)
	// {
	// act.addPosEventListener(listener);
	// }
	// }
	// }
	// return button;
	// }
	
	// public PosButton createButton(UserPanel context)
	// {
	// Action action = this.getPosAction(context);
	// if (action == null)
	// {
	// return null;
	// }
	// PosButton button = new PosButton(context, action);
	// button.setFont(button.getFont().deriveFont(this.fontStyle, (float)
	// this.fontSize));
	// button.setHorizontalAlignment(this.align);
	// button.setVerticalAlignment(this.valign);
	// button.setForeground(new Color(this.fgRed, this.fgGreen, this.fgBlue));
	// button.setBackground(new Color(this.bgRed, this.bgGreen, this.bgBlue));
	// button.setFailoverBackgroundColor(button.getBackground());
	// button.setText(this.text);
	//		if (this.text.startsWith("<HTML>")) { //$NON-NLS-1$
	// action.addPropertyChangeListener(button);
	// }
	// button.setActionCommand(this.command);
	//		
	// /*
	// * Hier können spezielle Buttons noch behandelt werden (v.a. Listener
	// * konfigurieren...
	// */
	// if (action instanceof ToggleAction)
	// {
	// action.addPropertyChangeListener(button);
	// }
	// else if (action instanceof ExitAction)
	// {
	// }
	//		
	// if (this.imagepath != null && this.imagepath.length() > 0)
	// {
	// File file = new File(this.imagepath);
	// if (file.exists())
	// {
	// button.setIcon(new ImageIcon(file.getAbsolutePath()));
	// }
	// }
	// return button;
	// }
	//	
	// protected abstract Action getPosAction(UserPanel context);
	//	
	// protected Action createAction(UserPanel context)
	// {
	// Action action = null;
	// try
	// {
	// Class a = Class.forName(this.className);
	// Class[] params = new Class[2];
	// params[0] = UserPanel.class;
	// params[1] = Key.class;
	// Constructor c = a.getConstructor(params);
	// Object[] p = new Object[2];
	// p[0] = context;
	// p[1] = this;
	// action = (Action) c.newInstance(p);
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	// // catch (ClassNotFoundException e) {
	//		////			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
	// // }
	// // catch (IllegalAccessException e) {
	//		////			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
	// // }
	// // catch (InstantiationException e) {
	//		////			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
	// // }
	// // catch (NoSuchMethodException e) {
	//		////			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
	// // }
	// // catch (InvocationTargetException e) {
	//		////			LogManager.getLogManager().getLogger("colibri").severe(e.getLocalizedMessage()); //$NON-NLS-1$
	// // }
	// return action;
	// }
	//	
	// public boolean isRemovable()
	// {
	// return true;
	// }
	//	
	// protected Element getJDOMRecordAttributes()
	// {
	// Element record = super.getJDOMRecordAttributes();
	//		
	//		Element te = new Element("field"); //$NON-NLS-1$
	//		te.setAttribute("name", "textEditable"); //$NON-NLS-1$ //$NON-NLS-2$
	//		te.setAttribute("value", Boolean.toString(this.textEditable)); //$NON-NLS-1$
	// record.addContent(te);
	//		
	//		Element rw = new Element("field"); //$NON-NLS-1$
	//		rw.setAttribute("name", "row"); //$NON-NLS-1$ //$NON-NLS-2$
	//		rw.setAttribute("value", Integer.toString(this.row)); //$NON-NLS-1$
	// record.addContent(rw);
	//		
	//		Element cl = new Element("field"); //$NON-NLS-1$
	//		cl.setAttribute("name", "column"); //$NON-NLS-1$ //$NON-NLS-2$
	//		cl.setAttribute("value", Integer.toString(this.column)); //$NON-NLS-1$
	// record.addContent(cl);
	//		
	//		Element br = new Element("field"); //$NON-NLS-1$
	//		br.setAttribute("name", "bg-red"); //$NON-NLS-1$ //$NON-NLS-2$
	//		br.setAttribute("value", Integer.toString(this.bgRed)); //$NON-NLS-1$
	// record.addContent(br);
	//		
	//		Element bg = new Element("field"); //$NON-NLS-1$
	//		bg.setAttribute("name", "bg-green"); //$NON-NLS-1$ //$NON-NLS-2$
	//		bg.setAttribute("value", Integer.toString(this.bgGreen)); //$NON-NLS-1$
	// record.addContent(bg);
	//		
	//		Element bb = new Element("field"); //$NON-NLS-1$
	//		bb.setAttribute("name", "bg-blue"); //$NON-NLS-1$ //$NON-NLS-2$
	//		bb.setAttribute("value", Integer.toString(this.bgBlue)); //$NON-NLS-1$
	// record.addContent(bb);
	//		
	//		Element br2 = new Element("field"); //$NON-NLS-1$
	//		br2.setAttribute("name", "bg-red2"); //$NON-NLS-1$ //$NON-NLS-2$
	//		br2.setAttribute("value", Integer.toString(this.bgRed2)); //$NON-NLS-1$
	// record.addContent(br2);
	//		
	//		Element bg2 = new Element("field"); //$NON-NLS-1$
	//		bg2.setAttribute("name", "bg-green2"); //$NON-NLS-1$ //$NON-NLS-2$
	//		bg2.setAttribute("value", Integer.toString(this.bgGreen2)); //$NON-NLS-1$
	// record.addContent(bg2);
	//		
	//		Element bb2 = new Element("field"); //$NON-NLS-1$
	//		bb2.setAttribute("name", "bg-blue2"); //$NON-NLS-1$ //$NON-NLS-2$
	//		bb2.setAttribute("value", Integer.toString(this.bgBlue2)); //$NON-NLS-1$
	// record.addContent(bb2);
	//		
	//		Element fr = new Element("field"); //$NON-NLS-1$
	//		fr.setAttribute("name", "fg-red"); //$NON-NLS-1$ //$NON-NLS-2$
	//		fr.setAttribute("value", Integer.toString(this.fgRed)); //$NON-NLS-1$
	// record.addContent(fr);
	//		
	//		Element fg = new Element("field"); //$NON-NLS-1$
	//		fg.setAttribute("name", "fg-green"); //$NON-NLS-1$ //$NON-NLS-2$
	//		fg.setAttribute("value", Integer.toString(this.fgGreen)); //$NON-NLS-1$
	// record.addContent(fg);
	//		
	//		Element fb = new Element("field"); //$NON-NLS-1$
	//		fb.setAttribute("name", "fg-blue"); //$NON-NLS-1$ //$NON-NLS-2$
	//		fb.setAttribute("value", Integer.toString(this.fgBlue)); //$NON-NLS-1$
	// record.addContent(fb);
	//		
	//		Element fs = new Element("field"); //$NON-NLS-1$
	//		fs.setAttribute("name", "font-size"); //$NON-NLS-1$ //$NON-NLS-2$
	//		fs.setAttribute("value", Double.toString(this.fontSize)); //$NON-NLS-1$
	// record.addContent(fs);
	//		
	//		Element fy = new Element("field"); //$NON-NLS-1$
	//		fy.setAttribute("name", "font-style"); //$NON-NLS-1$ //$NON-NLS-2$
	//		fy.setAttribute("value", Integer.toString(this.fontStyle)); //$NON-NLS-1$
	// record.addContent(fy);
	//		
	//		Element al = new Element("field"); //$NON-NLS-1$
	//		al.setAttribute("name", "align"); //$NON-NLS-1$ //$NON-NLS-2$
	//		al.setAttribute("value", Integer.toString(this.align)); //$NON-NLS-1$
	// record.addContent(al);
	//		
	//		Element va = new Element("field"); //$NON-NLS-1$
	//		va.setAttribute("name", "valign"); //$NON-NLS-1$ //$NON-NLS-2$
	//		va.setAttribute("value", Integer.toString(this.valign)); //$NON-NLS-1$
	// record.addContent(va);
	//		
	//		Element tx = new Element("field"); //$NON-NLS-1$
	//		tx.setAttribute("name", "text"); //$NON-NLS-1$ //$NON-NLS-2$
	//		tx.setAttribute("value", this.text); //$NON-NLS-1$
	// record.addContent(tx);
	//		
	//		Element co = new Element("field"); //$NON-NLS-1$
	//		co.setAttribute("name", "command"); //$NON-NLS-1$ //$NON-NLS-2$
	//		co.setAttribute("value", this.command); //$NON-NLS-1$
	// record.addContent(co);
	//		
	//		Element ip = new Element("field"); //$NON-NLS-1$
	//		ip.setAttribute("name", "imagepath"); //$NON-NLS-1$ //$NON-NLS-2$
	//		ip.setAttribute("value", this.imagepath); //$NON-NLS-1$
	// record.addContent(ip);
	//		
	//		Element rh = new Element("field"); //$NON-NLS-1$
	//		rh.setAttribute("name", "rel-horizontal-text-pos"); //$NON-NLS-1$ //$NON-NLS-2$
	//		rh.setAttribute("value", Integer.toString(this.relHorizontalTextPos)); //$NON-NLS-1$
	// record.addContent(rh);
	//		
	//		Element rv = new Element("field"); //$NON-NLS-1$
	//		rv.setAttribute("name", "rel-vertical-text-pos"); //$NON-NLS-1$ //$NON-NLS-2$
	//		rv.setAttribute("value", Integer.toString(this.relVerticalTextPos)); //$NON-NLS-1$
	// record.addContent(rv);
	//		
	//		Element cn = new Element("field"); //$NON-NLS-1$
	//		cn.setAttribute("name", "class-name"); //$NON-NLS-1$ //$NON-NLS-2$
	//		cn.setAttribute("value", this.className); //$NON-NLS-1$
	// record.addContent(cn);
	//		
	//		Element at = new Element("field"); //$NON-NLS-1$
	//		at.setAttribute("name", "action-type"); //$NON-NLS-1$ //$NON-NLS-2$
	//		at.setAttribute("value", this.actionType.toString()); //$NON-NLS-1$
	// record.addContent(at);
	//		
	// return record;
	// }
	//	
	// protected void getData(Element record)
	// {
	// super.getData(record);
	//		
	//		List fields = record.getChildren("field"); //$NON-NLS-1$
	// Iterator iter = fields.iterator();
	// while (iter.hasNext())
	// {
	// Element field = (Element) iter.next();
	//			if (field.getAttributeValue("name").equals("text-editable")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.textEditable = new Boolean(field.getAttributeValue("value")).booleanValue(); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("row")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.row = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("column")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.column = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("bg-red")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.bgRed = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("bg-green")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.bgGreen = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("bg-blue")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.bgBlue = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("bg-red2")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.bgRed2 = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("bg-green2")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.bgGreen2 = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("bg-blue2")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.bgBlue2 = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("fg-red")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.fgRed = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("fg-green")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.fgGreen = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("fg-blue")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.fgBlue = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("font-size")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.fontSize = XMLLoader.getDouble(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("font-style")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.fontStyle = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("align")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.align = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("valign")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.valign = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("text")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.text = field.getAttributeValue("value"); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("command")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.command = field.getAttributeValue("value"); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("imagepath")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.imagepath = field.getAttributeValue("value"); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("rel-horizontal-text-pos")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.relHorizontalTextPos = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("rel-vertical-text-pos")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.relVerticalTextPos = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("class-name")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.className = field.getAttributeValue("value"); //$NON-NLS-1$
	// }
	//			else if (field.getAttributeValue("name").equals("action-type")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				this.actionType = new Integer(XMLLoader.getInt(field.getAttributeValue("value"))); //$NON-NLS-1$
	// }
	// }
	// }
	//	
}

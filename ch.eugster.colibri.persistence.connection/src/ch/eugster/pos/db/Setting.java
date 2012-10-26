/*
 * Created on 08.07.2003
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
public class Setting extends Table
{
	
	@SuppressWarnings("unused")
	private static Setting setting;
	
	// look-and-feel
	// private String lookAndFeelClassname;
	// com-server
	public boolean use;
	public String comClassname;
	public boolean hold;
	public int update;
	public String path;
	public boolean showAddCustomerMessage;
	public boolean searchCd;
	public String cdPath;
	public int receiptNumberLength = 6;
	
	private ComServer comServer;
	
	/**
	 * 
	 */
	private Setting()
	{
		super();
	}
	
	// public static Setting getInstance()
	// {
	// if (Setting.setting == null)
	// {
	// if (Database.getCurrent().isStandard())
	// Setting.setting = Setting.selectById(new Long(1l));
	// else
	// Setting.setting = Setting.getById(new Long(1l));
	//			
	// Setting.setting.comServer = Setting.setting.new
	// ComServer(Setting.setting);
	//			
	// }
	// return Setting.setting;
	// }
	//	
	public ComServer getComServer()
	{
		return this.comServer;
	}
	
	public int getReceiptNumberLength()
	{
		return this.receiptNumberLength;
	}
	
	public void setReceiptNumberLength(int receiptNumberLength)
	{
		this.receiptNumberLength = receiptNumberLength;
	}
	
	// public static void readDBRecords()
	// {
	// Setting setting = Setting.getInstance();
	// Setting.put(setting);
	// }
	//	
	// public static Setting getById(Long id)
	// {
	// return (Setting) Setting.records.get(id);
	// }
	//	
	// private static void clearData()
	// {
	// Setting.records.clear();
	// }
	//	
	// private static void put(Setting setting)
	// {
	// Setting.records.put(setting.getId(), setting);
	// }
	//	
	// public static Element writeXMLRecords(Element root)
	// {
	//		Element table = Database.getTemporary().getTable("setting"); //$NON-NLS-1$
	// if (table == null)
	// {
	//			table = new Element("table"); //$NON-NLS-1$
	//			table.setAttribute("name", "setting"); //$NON-NLS-1$ //$NON-NLS-2$
	// root.addContent(table);
	// }
	//		
	// Enumeration entries = Setting.records.elements();
	// while (entries.hasMoreElements())
	// {
	// Setting rec = (Setting) entries.nextElement();
	//			
	//			Element record = new Element("record"); //$NON-NLS-1$
	//			record.setAttribute("id", rec.getId().toString()); //$NON-NLS-1$
	//			record.setAttribute("timestamp", new Long(rec.timestamp.getTime()).toString()); //$NON-NLS-1$
	//			record.setAttribute("deleted", new Boolean(rec.deleted).toString()); //$NON-NLS-1$
	//			
	//			//			Element field = new Element("field"); //$NON-NLS-1$
	//			//			field.setAttribute("name", "lookAndFeelClassname"); //$NON-NLS-1$ //$NON-NLS-2$
	//			//			field.setAttribute("value", rec.lookAndFeelClassname); //$NON-NLS-1$
	// // record.addContent(field);
	//			
	//			Element field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "use"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", new Boolean(rec.use).toString()); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "comClassname"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", rec.comClassname); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "hold"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", new Boolean(rec.hold).toString()); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "update"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", new Integer(rec.update).toString()); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "path"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", rec.path); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "showAddCustomerMessage"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", new Boolean(rec.showAddCustomerMessage).toString()); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "searchCd"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", new Boolean(rec.searchCd).toString()); //$NON-NLS-1$
	// record.addContent(field);
	//			
	//			field = new Element("field"); //$NON-NLS-1$
	//			field.setAttribute("name", "cdPath"); //$NON-NLS-1$ //$NON-NLS-2$
	//			field.setAttribute("value", rec.cdPath); //$NON-NLS-1$
	// record.addContent(field);
	//			
	// table.addContent(record);
	// }
	// return root;
	// }
	//	
	// public static void readXML()
	// {
	// Setting.clearData();
	//		Element[] elements = Database.getTemporary().getRecords("setting"); //$NON-NLS-1$
	// for (int i = 0; i < elements.length; i++)
	// {
	// Setting record = new Setting();
	//			record.setId(new Long(XMLLoader.getLong(elements[i].getAttributeValue("id")))); //$NON-NLS-1$
	//			record.timestamp = XMLLoader.getTimestampFromLong(elements[i].getAttributeValue("timestamp")); //$NON-NLS-1$
	//			record.deleted = new Boolean(elements[i].getAttributeValue("deleted")).booleanValue(); //$NON-NLS-1$
	//			
	//			List fields = elements[i].getChildren("field"); //$NON-NLS-1$
	// Iterator iter = fields.iterator();
	// while (iter.hasNext())
	// {
	// Element field = (Element) iter.next();
	//				//				if (field.getAttributeValue("name").equals("lookAndFeelClassname")) { //$NON-NLS-1$ //$NON-NLS-2$
	//				//					record.lookAndFeelClassname = field.getAttributeValue("value"); //$NON-NLS-1$
	// // }
	//				if (field.getAttributeValue("name").equals("use")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.use = XMLLoader.getBoolean(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("comClassname")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.comClassname = field.getAttributeValue("value"); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("hold")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.hold = XMLLoader.getBoolean(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("update")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.update = XMLLoader.getInt(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("path")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.path = field.getAttributeValue("value"); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("showAddCustomerMessage")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.showAddCustomerMessage = XMLLoader.getBoolean(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("searchCd")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.searchCd = XMLLoader.getBoolean(field.getAttributeValue("value")); //$NON-NLS-1$
	// }
	//				else if (field.getAttributeValue("name").equals("cdPath")) { //$NON-NLS-1$ //$NON-NLS-2$
	//					record.cdPath = field.getAttributeValue("value"); //$NON-NLS-1$
	// }
	// }
	// Setting.put(record);
	// }
	// }
	//	
	// // public void setLookAndFeelClassname(String classname)
	// // {
	// // this.lookAndFeelClassname = classname;
	// // }
	// //
	// // public String getLookAndFeelClassname()
	// // {
	// // return this.lookAndFeelClassname;
	// // }
	//	
	// private static Hashtable records = new Hashtable();
	
	public class ComServer
	{
		private Setting setting;
		
		ComServer(Setting setting)
		{
			this.setting = setting;
		}
		
		public boolean isUse()
		{
			return this.setting.use;
		}
		
		public void setUse(boolean use)
		{
			this.setting.use = use;
		}
		
		public String getClassname()
		{
			return this.setting.comClassname;
		}
		
		public void setClassname(String classname)
		{
			this.setting.comClassname = classname;
		}
		
		public boolean isHold()
		{
			return this.setting.hold;
		}
		
		public void setHold(boolean hold)
		{
			this.setting.hold = hold;
		}
		
		public int getUpdate()
		{
			return this.setting.update;
		}
		
		public void setUpdate(int update)
		{
			this.setting.update = update;
		}
		
		public String getPath()
		{
			return this.setting.path;
		}
		
		public void setPath(String path)
		{
			this.setting.path = path;
		}
		
		public boolean isShowAddCustomerMessage()
		{
			return this.setting.showAddCustomerMessage;
		}
		
		public void setShowAddCustomerMessage(boolean showAddCustomerMessage)
		{
			this.setting.showAddCustomerMessage = showAddCustomerMessage;
		}
		
		public boolean isSearchCd()
		{
			return this.setting.searchCd;
		}
		
		public void setSearchCd(boolean searchCd)
		{
			this.setting.searchCd = searchCd;
		}
		
		public String getCdPath()
		{
			return this.setting.cdPath;
		}
		
		public void setCdPath(String cdPath)
		{
			this.setting.cdPath = cdPath;
		}
	}
}

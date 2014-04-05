package ch.eugster.colibri.persistence.connection;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class ColibriContext implements Context {

	@Override
	public Object addToEnvironment(String arg0, Object arg1)
			throws NamingException 
	{
		return null;
	}

	@Override
	public void bind(Name key, Object value) throws NamingException 
	{
	}

	@Override
	public void bind(String key, Object value) throws NamingException 
	{
	}

	@Override
	public void close() throws NamingException 
	{
	}

	@Override
	public Name composeName(Name key1, Name key2) throws NamingException 
	{
		return null;
	}

	@Override
	public String composeName(String key1, String key2) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context createSubcontext(Name key) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context createSubcontext(String key) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroySubcontext(Name key) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroySubcontext(String key) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NameParser getNameParser(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NameParser getNameParser(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name arg0)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String arg0)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name arg0)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String arg0)
			throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookup(Name name) throws NamingException 
	{
		return "stored value";
	}

	@Override
	public Object lookup(String name) throws NamingException {
		// TODO Auto-generated method stub
		return "stored value";
	}

	@Override
	public Object lookupLink(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookupLink(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rebind(Name arg0, Object arg1) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rebind(String arg0, Object arg1) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Object removeFromEnvironment(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rename(Name arg0, Name arg1) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(String arg0, String arg1) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unbind(Name arg0) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unbind(String arg0) throws NamingException {
		// TODO Auto-generated method stub

	}

}

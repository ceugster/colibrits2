/*
 * Created on 21.07.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model.product;

import java.text.NumberFormat;

public class Customer
{
	private static NumberFormat nf = NumberFormat.getNumberInstance();

	private String code;

	private Integer id; // Kundennummer

	private String address; // CSTRASSE

	private String salutation; // CANREDE

	private String personalTitle; // CTITEL

	private String firstname; // CVORNAME

	private String lastname; // CNAME1

	private String lastname2; // CNAME2

	private String lastname3; // CNAME3

	private String country; // CLAND

	private String zip; // CPLZ

	private String city; // CORT

	private String phone; // CTELEFON

	private String phone2; // CTELEFON2

	private String fax; // CTELEFAX

	private String mobile; // CNATEL

	private String email; // CEMAIL

	private double account; // NKUNDKARTE

	private double tmpAccount = 0D;
	
	private boolean hasAccount; // LKUNDKARTE

	private double discount; // NNACHLASS

	private String providerId;

	public Customer()
	{
	}

	public Customer(final Integer id)
	{
		setId(id);
	}

	public double getAccount()
	{
		return account + tmpAccount;
	}

	public String getAddress()
	{
		return address == null ? "" : address;
	}

	public String getCity()
	{
		return city == null ? "" : city;
	}

	public String getCode()
	{
		return code == null ? "" : code;
	}

	public String getCountry()
	{
		return country == null ? "" : country;
	}

	public double getDiscount()
	{
		return discount;
	}

	public String getEmail()
	{
		return email == null ? "" : email;
	}

	public String getFax()
	{
		return fax == null ? "" : fax;
	}

	public String getFirstname()
	{
		return firstname == null ? "" : firstname;
	}

	public String getFullname()
	{
		final StringBuffer buffer = new StringBuffer();
		if (!getLastname().isEmpty())
		{
			buffer.append(lastname);
		}
		if (!getFirstname().isEmpty())
		{
			if (buffer.length() > 0)
			{
				buffer.append(", ");
			}
			buffer.append(firstname);
		}
		return buffer.toString();
	}

	public boolean getHasAccount()
	{
		return hasAccount;
	}

	public Integer getId()
	{
		return id;
	}

	public String getLastname()
	{
		return lastname == null ? "" : lastname;
	}

	public String getLastname2()
	{
		return lastname2 == null ? "" : lastname2;
	}

	public String getLastname3()
	{
		return lastname3 == null ? "" : lastname3;
	}

	public String getMobile()
	{
		return mobile == null ? "" : mobile;
	}

	public String getPersonalTitle()
	{
		return personalTitle == null ? "" : personalTitle;
	}

	public String getPhone()
	{
		return phone == null ? "" : phone;
	}

	public String getPhone2()
	{
		return phone2 == null ? "" : phone2;
	}

	public String getProviderId()
	{
		return providerId == null ? "" : providerId;
	}

	public String getSalutation()
	{
		return salutation == null ? "" : salutation;
	}

	public String getTitleLine()
	{
		final StringBuffer buffer = new StringBuffer("Kunde: ");
		buffer.append(getId().toString());
		buffer.append(" - ");
		buffer.append(getFullname());
		if (getHasAccount())
		{
			buffer.append(", Kontostand: ");
			buffer.append(Customer.nf.format(account));
		}
		else
		{
			buffer.append(", kein Konto");
		}
		return buffer.toString();
	}

	public String getZip()
	{
		return zip == null ? "" : zip;
	}

	public void setAccount(final double account)
	{
		this.account = account;
	}

	public void setAddress(final String address)
	{
		this.address = address;
	}

	public void setCity(final String city)
	{
		this.city = city;
	}

	public void setCountry(final String country)
	{
		this.country = country;
	}

	public void setDiscount(final double discount)
	{
		this.discount = discount;
	}

	public void setEmail(final String email)
	{
		this.email = email;
	}

	public void setFax(final String fax)
	{
		this.fax = fax;
	}

	public void setFirstname(final String firstname)
	{
		this.firstname = firstname;
	}

	public void setHasAccount(final boolean hasAccount)
	{
		this.hasAccount = hasAccount;
	}

	public void setId(final Integer id)
	{
		this.id = id;
	}

	public void setLastname(final String lastname)
	{
		this.lastname = lastname;
	}

	public void setLastname2(final String lastname2)
	{
		this.lastname2 = lastname2;
	}

	public void setLastname3(final String lastname3)
	{
		this.lastname3 = lastname3;
	}

	public void setMobile(final String mobile)
	{
		this.mobile = mobile;
	}

	public void setPersonalTitle(final String personalTitle)
	{
		this.personalTitle = personalTitle;
	}

	public void setPhone(final String phone)
	{
		this.phone = phone;
	}

	public void setPhone2(final String phone2)
	{
		this.phone2 = phone2;
	}

	public void setProviderId(final String providerId)
	{
		this.providerId = providerId;
	}

	public void setSalutation(final String salutation)
	{
		this.salutation = salutation;
	}

	public void setZip(final String zip)
	{
		this.zip = zip;
	}
	
	public void addAccount(double amount)
	{
		this.tmpAccount = amount;
	}
}

package ch.eugster.colibri.persistence.model.product;

public interface IProductGroupType
{
	String asPlural();

	ProductGroupGroup getParent();

	boolean isAsDefaultProductGroupAvailable();

	String toCode();

	double computePrice(double price);
}

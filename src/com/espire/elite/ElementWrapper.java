package com.espire.elite;
import org.w3c.dom.Element;
public class ElementWrapper {

	private Element element;
	private String versionNumber;

	public ElementWrapper(Element element,String versionNumber) {
		this.element=element;
		this.versionNumber=versionNumber;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((versionNumber == null) ? 0 : versionNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementWrapper other = (ElementWrapper) obj;
		if (versionNumber == null) {
			if (other.versionNumber != null)
				return false;
		} else if (!versionNumber.equals(other.versionNumber))
			return false;
		return true;
	}


	public Element getElement() {
		return element;
	}


	public String getVersionNumber() {
		return versionNumber;
	}
	
	
}

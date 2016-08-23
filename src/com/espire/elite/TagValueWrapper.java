package com.espire.elite;

/**
 * @author samrat.roy
 *
 */
public class TagValueWrapper {
public String getTagValue() {
		return tagValue;
	}

	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}

	public String getOtherKey() {
		return otherKey;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((otherKey == null) ? 0 : otherKey.hashCode());
		result = prime * result + ((tagValue == null) ? 0 : tagValue.hashCode());
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
		TagValueWrapper other = (TagValueWrapper) obj;
		if (otherKey == null) {
			if (other.otherKey != null)
				return false;
		} else if (!otherKey.equals(other.otherKey))
			return false;
		if (tagValue == null) {
			if (other.tagValue != null)
				return false;
		} else if (!tagValue.equals(other.tagValue))
			return false;
		return true;
	}

	public void setOtherKey(String otherKey) {
		this.otherKey = otherKey;
	}

private String tagValue;

/**
 * for Progrtam unit other key is ProgramUnitType
 */
private String otherKey;

	
}

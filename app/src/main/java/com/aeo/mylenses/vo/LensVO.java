package com.aeo.mylenses.vo;

public class LensVO {

	private Integer id;
	private String dateLeft;
	private String dateRight;
	private int expirationLeft;
	private int expirationRight;
	private int typeLeft;
	private int typeRight;
	private int inUseLeft;
	private int inUseRight;
	private int numDaysNotUsedLeft;
	private int numDaysNotUsedRight;
	private int countUnitLeft;
	private int countUnitRight;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDateLeft() {
		return dateLeft;
	}

	public void setDateLeft(String dateLeft) {
		this.dateLeft = dateLeft;
	}

	public String getDateRight() {
		return dateRight;
	}

	public void setDateRight(String dateRight) {
		this.dateRight = dateRight;
	}

	public int getExpirationLeft() {
		return expirationLeft;
	}

	public void setExpirationLeft(int expirationLeft) {
		this.expirationLeft = expirationLeft;
	}

	public int getExpirationRight() {
		return expirationRight;
	}

	public void setExpirationRight(int expirationRight) {
		this.expirationRight = expirationRight;
	}

	public int getTypeLeft() {
		return typeLeft;
	}

	public void setTypeLeft(int typeLeft) {
		this.typeLeft = typeLeft;
	}

	public int getTypeRight() {
		return typeRight;
	}

	public void setTypeRight(int typeRight) {
		this.typeRight = typeRight;
	}

	public int getInUseLeft() {
		return inUseLeft;
	}

	public void setInUseLeft(int inUseLeft) {
		this.inUseLeft = inUseLeft;
	}

	public int getInUseRight() {
		return inUseRight;
	}

	public void setInUseRight(int inUseRight) {
		this.inUseRight = inUseRight;
	}

	public int getNumDaysNotUsedLeft() {
		return numDaysNotUsedLeft;
	}

	public void setNumDaysNotUsedLeft(int numDaysNotUsedLeft) {
		this.numDaysNotUsedLeft = numDaysNotUsedLeft;
	}

	public int getNumDaysNotUsedRight() {
		return numDaysNotUsedRight;
	}

	public void setNumDaysNotUsedRight(int numDaysNotUsedRight) {
		this.numDaysNotUsedRight = numDaysNotUsedRight;
	}

	public int getCountUnitLeft() {
		return countUnitLeft;
	}

	public void setCountUnitLeft(int countUnitLeft) {
		this.countUnitLeft = countUnitLeft;
	}

	public int getCountUnitRight() {
		return countUnitRight;
	}

	public void setCountUnitRight(int countUnitRight) {
		this.countUnitRight = countUnitRight;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dateLeft == null) ? 0 : dateLeft.hashCode());
		result = prime * result
				+ ((dateRight == null) ? 0 : dateRight.hashCode());
		result = prime * result + expirationLeft;
		result = prime * result + expirationRight;
		result = prime * result + inUseLeft;
		result = prime * result + inUseRight;
		result = prime * result + typeLeft;
		result = prime * result + typeRight;
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
		LensVO other = (LensVO) obj;
		if (dateLeft == null) {
			if (other.dateLeft != null)
				return false;
		} else if (!dateLeft.equals(other.dateLeft))
			return false;
		if (dateRight == null) {
			if (other.dateRight != null)
				return false;
		} else if (!dateRight.equals(other.dateRight))
			return false;
		if (expirationLeft != other.expirationLeft)
			return false;
		if (expirationRight != other.expirationRight)
			return false;
		if (inUseLeft != other.inUseLeft)
			return false;
		if (inUseRight != other.inUseRight)
			return false;
		if (typeLeft != other.typeLeft)
			return false;
		if (typeRight != other.typeRight)
			return false;
		if (countUnitLeft != other.countUnitLeft)
			return false;
		if (countUnitRight != other.countUnitRight)
			return false;
		return true;
	}

}

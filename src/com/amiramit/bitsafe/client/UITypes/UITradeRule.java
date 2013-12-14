package com.amiramit.bitsafe.client.UITypes;

import java.io.Serializable;
import java.util.Date;

import com.amiramit.bitsafe.shared.FieldVerifier;

public abstract class UITradeRule implements UIElement, Serializable {

	private static final long serialVersionUID = 1L;

	private Long dbKey;
	private Date createDate;
	private String name;
	private boolean active;

	public UITradeRule(final Long dbKey, final Date createDate,
			final String name, final boolean active) {
		this.dbKey = dbKey;
		this.createDate = createDate;
		this.name = name;
		this.active = active;
	}
	
	protected UITradeRule() {
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getName() {
		return name;
	}

	public boolean getActive() {
		return active;
	}

	public Long getDbKey() {
		return dbKey;
	}

	public void setDbKey(Long dbKey) {
		this.dbKey = dbKey;
	}

	@Override
	public void verify() throws UIVerifyException {
		// dbKey must be null when sending from ui to server
		// createDate must be null when sending from ui to server
		FieldVerifier.verifyIsNull(dbKey);
		FieldVerifier.verifyIsNull(createDate);
		FieldVerifier.verifyString(name);
		
		// No need to check for 'active' field ...
	}
}

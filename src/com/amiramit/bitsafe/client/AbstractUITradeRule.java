package com.amiramit.bitsafe.client;

import java.io.Serializable;
import java.util.Date;

import com.amiramit.bitsafe.shared.FieldVerifier;

public abstract class AbstractUITradeRule implements UIElement, Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final Long INVALID_DB_ID = null;
	private Long dbID;
	private Date createDate;
	private String name;
	private Boolean active;

	public AbstractUITradeRule(final Long dbID, final Date createDate, final String name, final Boolean active) {
		super();
		this.dbID = dbID;
		this.createDate = createDate;
		this.name = name;
		this.active = active;
	}

	public AbstractUITradeRule(final Date createDate, final String name, final Boolean active) {
		this(INVALID_DB_ID, createDate, name, active);
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getName() {
		return name;
	}

	public Boolean getActive() {
		return active;
	}

	public Long getDbID() {
		return dbID;
	}

	@Override
	public void verify() throws UIVerifyException {
		FieldVerifier.verifyNotNull(dbID);
		FieldVerifier.verifyNotNull(createDate);
		FieldVerifier.verifyString(name);
		FieldVerifier.verifyNotNull(active);
	}
}

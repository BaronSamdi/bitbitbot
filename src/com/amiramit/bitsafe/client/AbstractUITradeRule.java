package com.amiramit.bitsafe.client;

import java.io.Serializable;
import java.util.Date;

import com.amiramit.bitsafe.shared.FieldVerifier;

public abstract class AbstractUITradeRule implements UIElement, Serializable {

    private static final long serialVersionUID = 1L;
    
    public static final Long INVALID_DB_ID = null;
	private Long dbKey;
	private Date createDate;
	private String name;
	private Boolean active;

	public AbstractUITradeRule(final Long dbKey, final Date createDate, final String name, final Boolean active) {
		super();
		this.dbKey = dbKey;
		this.createDate = createDate;
		this.name = name;
		this.active = active;
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

	public Long getDbKey() {
		return dbKey;
	}

	@Override
	public void verify() throws UIVerifyException {
		FieldVerifier.verifyNotNull(dbKey);
		FieldVerifier.verifyNotNull(createDate);
		FieldVerifier.verifyString(name);
		FieldVerifier.verifyNotNull(active);
	}
}

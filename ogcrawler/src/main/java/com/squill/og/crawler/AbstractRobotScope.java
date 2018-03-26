package com.squill.og.crawler;

import crawlercommons.robots.BaseRobotRules;

public abstract class AbstractRobotScope implements IRobotScope {

	private BaseRobotRules robotRules;
	
	@Override
	public void setRobotRules(BaseRobotRules robotRules) {
		this.robotRules = robotRules;
	}
	
	protected abstract boolean ifScoped(String link);
	
	@Override
	public final boolean isScoped(String link) {
		if(!robotRules.isAllowed(link))
			return false;
		return ifScoped(link);
	}
}

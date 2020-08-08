package com.alchemist.service;

public abstract interface Service {
	
	/**
	 * Get the name of service, usually the command prefix, e.g. "holo"
	 * @return
	 */
	public abstract String getServiceName();
	
	/**
	 * Get the service's manual
	 * @return
	 */
	public abstract String getServiceMan();
}

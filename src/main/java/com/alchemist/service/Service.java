package com.alchemist.service;

public abstract interface Service {
	
	/**
	 * Get the name of service, usually the command prefix, e.g. "holo"
	 * @return
	 */
	public default String getServiceName() { return null; };
	
	/**
	 * Get the service's manual
	 * @return
	 */
	public default String getServiceMan() { return null; };
	
	/**
	 * Override this method if service termination needs to be notified
	 */
	public default void terminate() {}
	
}

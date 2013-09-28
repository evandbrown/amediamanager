package com.amediamanager.config;

/**
 * Identifies a resource (e.g. database schema) that can be provisioned.
 * @author evbrown
 *
 */
public interface ProvisionableResource {
	public enum ProvisionState {
		UNPROVISIONED,
		PROVISIONING,
		PROVISIONED
	}
	public ProvisionState getState();
	public String getName();
	public void provision();
}

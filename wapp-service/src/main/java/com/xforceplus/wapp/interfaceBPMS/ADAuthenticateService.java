package com.xforceplus.wapp.interfaceBPMS;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.springframework.stereotype.Service;

/**
 * Authenticate user by AD(Active Directory) server.
 *
 * @author ewang35
 *
 */
@Service(value = "ADAuthenticateService")
public class ADAuthenticateService {
	/**
	 * this hash table used to store all of parameters being used to initialize
	 * LDAP server.
	 */
	private static Hashtable<String, String> env = new Hashtable<String, String>();

	/**
	 * a instance of DirContext
	 */
	private DirContext ctx = null;

	/**
	 * the URL of LDAP server.
	 */
	private String[] ldapServerURL = new String[]{"ldap://S08010NT0002CN.CN.Wal-Mart.com:389/"
			,"ldap://S08010NT0002CN.CN.Wal-Mart.com/"
	};

	/**
	 * use default parameters to configure a LDAP server
	 *
	 * @param userId
	 *            a user id to bind
	 * @param passwd
	 *            the password of userId
	 */
	public ADAuthenticateService(String userId, String passwd) {

		// initializing, and set default LDAP server.
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://S08010NT0002CN.CN.Wal-Mart.com:389/");
		env.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");

		env.put(Context.SECURITY_PRINCIPAL, userId);
		env.put(Context.SECURITY_CREDENTIALS, passwd);

	}

	public ADAuthenticateService() {

		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://S08010NT0002CN.CN.Wal-Mart.com:389/");
		env.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");

	}

	/**
	 * Initialize a context connected to LDAP server. Successfully creating
	 * context means that authentication is successful, and return true false or
	 * not.
	 *
	 * @return true if successfully authenticate, else return false.
	 * @throws NamingException
	 */
	private boolean checkAuth() throws NamingException {
		boolean flag = false;
		try {
			ctx = new InitialDirContext(env);
			flag = true;
		} catch (Exception e) {
			// do nothing;

		} finally {
			try {
				if (ctx != null) {
					ctx.close();
				}
			} catch (NamingException e) {
				// do nothing
			}
		}
		return flag;
	}

	/**
	 * Use provider URL list to reset LDAP server.
	 *
	 * @param ldapServerURL
	 * @return
	 */
	public boolean authenticate(String[] ldapServerURL) {

		boolean flag = false;
		if (ldapServerURL == null) {
			// use default LDAP server to authenticate
			try {
				flag = checkAuth();
			} catch (NamingException e) {
				// do nothing
			}
		} else {
			for (int i = 0; i < ldapServerURL.length; i++) {
				env.put(Context.PROVIDER_URL, ldapServerURL[i]);
				try {
					flag = checkAuth();
				} catch (AuthenticationException authe) {
					// throw new ADAuthenticationException(authe);
				} catch (NamingException e) {
					// throw new ADAuthenticationException(
					// "Exception happened when accessing AD server", e);
				}
			}
		}
		return flag;
	}

	/**
	 * Use provider URL list to reset LDAP server. URL list set by spring
	 * container.
	 *
	 * @param userId
	 *            The user name to login AD server.
	 * @param passwd
	 *            the password to login AD server.
	 * @return
	 * @throws NamingException
	 */
	public boolean authenticate(String userId, String passwd)
			throws NamingException {

		env.put(Context.SECURITY_PRINCIPAL, userId);
		env.put(Context.SECURITY_CREDENTIALS, passwd);
		// ldapServerURL = null;
		if (ldapServerURL == null) {
			// use default LDAP server to authenticate
			return checkAuth();

		}

		boolean flag = false;
		// try provider URL list specified by spring container
		for (int i = 0; i < ldapServerURL.length; i++) {
			env.put(Context.PROVIDER_URL, ldapServerURL[i]);
			flag = checkAuth();
			if (flag) {
				break;
			}

		}

		return flag;
	}

	/**
	 * set the URL of LDAP server.
	 *
	 * @param ldapServerURL
	 */
	public void setLdapServerURL(String[] ldapServerURL) {
		this.ldapServerURL = ldapServerURL;
	}

}

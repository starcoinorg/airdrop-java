package org.starcoin.htpasswd.authentication.provider;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.Crypt;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pieces from all over, auth matching from
 * https://github.com/gitblit/gitblit/blob/master/src/main/java/com/gitblit/auth/HtpasswdAuthProvider.java
 *
 * @author rweneck
 */
public class HtpasswdAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(HtpasswdAuthenticationProvider.class);

    private final Map<String, String> htUsers = new ConcurrentHashMap<String, String>();

    //private volatile long lastModified;

    public HtpasswdAuthenticationProvider(String htpasswd) {
        Pattern entry = Pattern.compile("^([^:]+):(.+)");
        Scanner scanner = new Scanner(htpasswd);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
                Matcher m = entry.matcher(line);
                if (m.matches()) {
                    htUsers.put(m.group(1), m.group(2));
                }
            }
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        // use the credentials to try to authenticate against the third party
        // system
        if (authenticatedAgainstThirdPartySystem(name, password)) {
            List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
            grantedAuths.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return "ROLE_VENDOR";
                }
            });
            return new UsernamePasswordAuthenticationToken(name, password,
                    grantedAuths);
        } else {
            throw new AuthenticationException(
                    "Unable to auth against htpasswd") {
            };
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private boolean authenticatedAgainstThirdPartySystem(String username,
                                                         String password) {
        //read();
        boolean authenticated = false;
        String storedPwd = htUsers.get(username);
        if (storedPwd == null) {
            return false;
        }

        // test Apache MD5 variant encrypted password
        if (storedPwd.startsWith("$apr1$")) {
            if (storedPwd.equals(Md5Crypt.apr1Crypt(password, storedPwd))) {
                LOG.info("Apache MD5 encoded password matched for user '"
                        + username + "'");
                authenticated = true;
            }
        }
        // test unsalted SHA password
        else if (storedPwd.startsWith("{SHA}")) {
            String passwd64 = Base64.encodeBase64String(DigestUtils
                    .sha1(password));
            if (storedPwd.substring("{SHA}".length()).equals(passwd64)) {
                LOG.info("Unsalted SHA-1 encoded password matched for user '"
                        + username + "'");
                authenticated = true;
            }
        }
        // test libc crypt() encoded password
        else if (storedPwd.equals(Crypt.crypt(password, storedPwd))) {
            LOG.info("Libc crypt encoded password matched for user '"
                    + username + "'");
            authenticated = true;
        }
        // test clear text
        else if (storedPwd.equals(password)) {
            LOG.info("Clear text password matched for user '" + username
                    + "'");
            authenticated = true;
        }

        return authenticated;
    }

//    /**
//     * Reads the realm file and rebuilds the in-memory lookup tables.
//     */
//    protected synchronized void read() {
//        boolean forceReload = false;
//        // externalize the file to a property that gets injected
//        // consider some kind of caching mechanism
//        File htpasswdFile = new File(
//                "/Users/rweneck/sourceCode/springSecurityHtpasswd/.htpasswd");
//
//        if (htpasswdFile.exists()
//                && (forceReload || (htpasswdFile.lastModified() != lastModified))) {
//            lastModified = htpasswdFile.lastModified();
//            htUsers.clear();
//
//            Pattern entry = Pattern.compile("^([^:]+):(.+)");
//
//            Scanner scanner = null;
//            try {
//                scanner = new Scanner(new FileInputStream(htpasswdFile));
//                while (scanner.hasNextLine()) {
//                    String line = scanner.nextLine().trim();
//                    if (!line.isEmpty() && !line.startsWith("#")) {
//                        Matcher m = entry.matcher(line);
//                        if (m.matches()) {
//                            htUsers.put(m.group(1), m.group(2));
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                log.severe(MessageFormat.format("Failed to read {0}",
//                        htpasswdFile));
//            } finally {
//                if (scanner != null) {
//                    scanner.close();
//                }
//            }
//        }
//    }

}
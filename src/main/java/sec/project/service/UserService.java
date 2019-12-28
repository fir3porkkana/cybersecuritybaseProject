package sec.project.service;

import java.util.Arrays;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {
    
    public HashMap<String, String> accounts;

    @PostConstruct
    public void init() {
        // DO NOT CHANGE THESE VALUES!
        accounts = new HashMap<>();
        accounts.put("ted", "$2a$10$nKOFU.4/iK9CqDIlBkmMm.WZxy2XKdUSlImsG8iKsAP57GMcXwLTS");
        accounts.put("bossman","adminlikeaboss");
        accounts.put("admin", "qwerty");
        accounts.put("lol","yes");
        // ---
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!accounts.containsKey(username)) {
            throw new UsernameNotFoundException("No such user: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                username,
                accounts.get(username),
                false,
                false,
                false,
                false,
                Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
    
    public void remove(String name) {
        if(accounts.containsKey(name)) {
            this.accounts.remove(name);
        }
    }
    
    public HashMap<String, String> accounts() {
        return this.accounts;
    }
}

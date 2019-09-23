//
// Feel free to use these solutions in your work.
//
package alekseybykov.portfolio.component.services.user;

import alekseybykov.portfolio.component.entities.User;

/**
 * @author  aleksey.n.bykov@gmail.com
 * @version 1.0
 * @since   2019-09-24
 */
public interface UserService {
    User findByLogin(String login);
}

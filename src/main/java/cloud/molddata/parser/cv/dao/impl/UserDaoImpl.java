package cloud.molddata.parser.cv.dao.impl;

import cloud.molddata.parser.cv.dao.UserDao;
import cloud.molddata.parser.cv.model.UserSecurity;
import cloud.molddata.parser.cv.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;


    @SuppressWarnings("unchecked")
    @Transactional
    public void save(UserSecurity user, String sessionID) {
        String nameUserAuth = user.getUsername();
        if (user.getUsername() == null)
            entityManager.persist(user);
        else {
            entityManager.merge(user);
        }
        Query usersBySession = entityManager.createQuery("SELECT us FROM Users us where us.sessionID LIKE :sessionID").setParameter("sessionID", sessionID);
        List<Users> usersList = usersBySession.getResultList();

        Users users = usersList.get(0);
        users.setNameAuth(nameUserAuth);
        entityManager.persist(users);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public void authorization(String nameAuth, String sessionID) {
        try {
            Query querySessionID = entityManager.createNamedQuery("Users.findBySessionID").setParameter("idReq", sessionID);

            Users userLogin = (Users) querySessionID.getSingleResult();

            userLogin.setNameAuth(nameAuth);
            entityManager.merge(userLogin);

        } catch (NoResultException NRE) {
            Users usersNew = new Users(sessionID, nameAuth);
            entityManager.persist(usersNew);
        }
    }

    @SuppressWarnings("unchecked")
    public UserSecurity findByUserName(String username) {
        Query query = entityManager.createQuery("Select us from UserSecurity us where us.username LIKE :username").setParameter("username", username);
        List<UserSecurity> users = query.getResultList();

        try {
            return users.get(0);
        }catch (NullPointerException npe){
            return null;
        }
    }
}
package cloud.molddata.parser.cv.dao.impl;


import cloud.molddata.parser.cv.dao.UserUploadDao;
import cloud.molddata.parser.cv.model.*;
import cloud.molddata.parser.cv.parser.FileParcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class UserUploadDaoImpl implements UserUploadDao {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Transactional
    public List<Users> getListUsersByName(String nameAuth, String sessionID) {
        if (("000").equals(sessionID) && ("anonymousUser").equals(nameAuth)) {
            Query query = entityManager.createQuery("Select us from Users us WHERE us.nameAuth like :nameAuth").setParameter("nameAuth", "anonymousUser");
            return query.getResultList();
        }
        if (("anonymousUser").equals(nameAuth)) {
            Query querySessionID = entityManager.createNamedQuery("Users.findBySessionID").setParameter("idReq", sessionID);
            return querySessionID.getResultList();
        }
        Query query = entityManager.createQuery("Select us from Users us WHERE us.nameAuth like :nameAuth").setParameter("nameAuth", nameAuth);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<Users> getListUsersAll() {
        Query queryUsersAll = entityManager.createNamedQuery("Users.findAll");
        return queryUsersAll.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<UserSecurity> getListUsersAuth() {
        Query queryUsersAll = entityManager.createNamedQuery("UserSecurity.findAll");
        return queryUsersAll.getResultList();
    }


    @Transactional
    public void createUser(String sessionID) {
        Query usersBySession = entityManager.createNamedQuery("Users.findBySessionID").setParameter("idReq", sessionID);
        List<Users> usersList = usersBySession.getResultList();
        if (usersList.size() == 0) {
            Users users = new Users(sessionID, "anonymousUser");
            entityManager.persist(users);
        }
    }

}

package cloud.molddata.parser.cv.dao.impl;


import cloud.molddata.parser.cv.dao.CVParserDao;
import cloud.molddata.parser.cv.model.CV;
import cloud.molddata.parser.cv.model.Contact;
import cloud.molddata.parser.cv.model.UploadedFile;
import cloud.molddata.parser.cv.model.Users;
import cloud.molddata.parser.cv.parser.FileParcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class CVParserDaoImpl implements CVParserDao {

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public void saveParsedCV(List<UploadedFile> activeFilesInSession) {
        String sessionID = activeFilesInSession.get(0).getSessionID();

        FileParcer.CVparser(activeFilesInSession); //ready CV in CLASS
        List<CV> cvList = FileParcer.getcvList();

        for (CV cv : cvList) {
            Contact contact = cv.getContact();

            cv.setContact(contact);
            contact.setCv(cv);

            entityManager.persist(contact);
            entityManager.persist(cv);

            Long tempID = getIDbySession(sessionID);
            addCV(tempID, cv);
        }
    }

    @Transactional
    private Long getIDbySession(String sessionID) {
        Query usersBySession = entityManager.createNamedQuery("Users.findBySessionID").setParameter("idReq", sessionID);
        Users users = (Users) usersBySession.getSingleResult();
        return users.getId();
    }

    @Transactional
    private void addCV(Long idUSER, CV cv) {
        Users user = entityManager.find(Users.class, idUSER);
        user.addCV(cv);
        entityManager.persist(user);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<CV> getListCV() {
        return entityManager.createQuery("Select cv from CV cv").getResultList();
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public Contact getContactInfo(String id_cont) {
        return entityManager.find(Contact.class, Long.parseLong(id_cont));
    }

    @Transactional
    public String getParseStatus(UploadedFile activeFileInSession) {
        return FileParcer.parseStatus(activeFileInSession);
    }
}

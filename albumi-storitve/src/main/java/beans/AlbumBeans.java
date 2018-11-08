package beans;

import entities.Album;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class AlbumBeans {

    private final static Logger LOGGER = Logger.getLogger(AlbumBeans.class.getName());

    @PersistenceContext(unitName = "album-jpa")
    private EntityManager em;

    @PostConstruct
    public void start() {
        LOGGER.log(Level.INFO, "Ustvarjanje AlbumBeans zrno");
    }

    @PreDestroy
    public void reset() {
        LOGGER.log(Level.INFO, "Uničenje AlbumBeans zrno");
    }

    public List<Album> getAlbumList() {

        TypedQuery<Album> query = em.createNamedQuery("Album.getAll", Album.class);

        return query.getResultList();

    }

    public Album getAlbum(Integer id) {

        Album slika = em.find(Album.class, id);

        if (slika == null) {
            throw new NotFoundException();
        }

        return slika;
    }

    public Album createAlbum(Album slika) {

        try {
            beginTx();
            em.persist(slika);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return slika;
    }

    public Album putAlbum(String id, Album album) {

        Album c = em.find(Album.class, id);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            album.setId(c.getId());
            album = em.merge(album);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return album;
    }

    public boolean deleteAlbum(String id) {

        Album album = em.find(Album.class, id);

        if (album != null) {
            try {
                beginTx();
                em.remove(album);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }


    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }
}

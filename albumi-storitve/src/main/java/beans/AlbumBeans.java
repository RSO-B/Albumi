package beans;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import configuration.AppProperties;
import dtos.Slika;
import entities.Album;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.json.simple.JSONObject;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class AlbumBeans {

    private Logger log = Logger.getLogger(AlbumBeans.class.getName());

    @PersistenceContext(unitName = "album-jpa")
    private EntityManager em;

    private Client httpClient;

    @Inject
    private AppProperties appProperties;

    @Inject
    @DiscoverService("rso-slike")
    private Optional<String> baseUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
    }

    @PreDestroy
    public void reset() {
        log.log(Level.INFO, "Uničenje UporabnikBeans zrno");
    }

    public List<Album> getAlbumList(QueryParameters query) {

        List<Album> albumList  = JPAUtils.queryEntities(em, Album.class, query);

        for (Album a: albumList) {
            a.setSlikeList(getSlikaList(a.getId()));
        }

        return albumList;
    }
    public Long getAlbumCout(QueryParameters query) {

        return JPAUtils.queryEntitiesCount(em, Album.class, query);
    }

    public Album getAlbum(Integer id) {

        Album album = em.find(Album.class, id);

        if (album == null) {
            throw new NotFoundException();
        }

        List<Slika> slikaList = getSlikaList(album.getId());
        album.setSlikeList(slikaList);

        return album;
    }

    @Transactional
    public void createAlbum(Album a) {

        if(a.getSlikeList() != null){
            insertSlikaKlic(a);
            a.setSlikeList(new ArrayList<>());
        }

        if (a != null) {
            em.persist(a);
        }
    }

    private void insertSlikaKlic(Album a) {
        for (Slika slika: a.getSlikeList()) {
            JSONObject json = new JSONObject();
            json.put("album_id", slika.getAlbum_id());
            json.put("naslov", slika.getNaslov());
            json.put("opis", slika.getOpis());
            json.put("path", slika.getPath());

            if (appProperties.isExternalServicesEnabled() && baseUrl.isPresent()) {
                try {
                    httpClient
                            .target(baseUrl.get() + "/v1/slike/")
                            .request()
                            .build("POST", Entity.json(json))
                            .invoke();
                } catch (WebApplicationException | ProcessingException e) {
                    log.severe(e.getMessage());
                }
            }
        }
    }

    public Album insertSlika(String idAlbum, List<Slika> slikaList){

        Album album = getAlbum(Integer.parseInt(idAlbum));
        JSONObject json;

        for (Slika slika: slikaList) {
            json = new JSONObject();
            json.put("album_id", idAlbum);
            json.put("naslov", slika.getNaslov());
            json.put("opis", slika.getOpis());
            json.put("path", slika.getPath());

            if (appProperties.isExternalServicesEnabled() && baseUrl.isPresent()) {
                try {
                    httpClient
                            .target(baseUrl.get() + "/v1/slike/")
                            .request()
                            .build("POST", Entity.json(json))
                            .invoke();
                } catch (WebApplicationException | ProcessingException e) {
                    log.severe(e.getMessage());
                }
            }
        }

        album.setSlikeList(slikaList);

        return album;

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

    @Timed
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Timeout(value = 20,unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "getSlikaFallback")
    public List<Slika> getSlikaList(Integer album_id) {

        if (appProperties. isExternalServicesEnabled()) {
            try {
                return httpClient
                        .target(appProperties.getUrlSlike() + "/v1/slike?filter=album_id:EQ:" + album_id)
                        .request().get(new GenericType<List<Slika>>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                throw new InternalServerErrorException(e);
            }
        }

        return null;

    }

    public List<Slika> getSlikaFallback(Integer albumId){
        return Collections.emptyList();
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

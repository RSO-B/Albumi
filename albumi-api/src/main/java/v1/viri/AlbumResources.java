package v1.viri;

import beans.AlbumBeans;
import com.kumuluz.ee.rest.beans.QueryParameters;
import dtos.Slika;
import entities.Album;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@ApplicationScoped
@Path("/album")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AlbumResources {

    @Inject
    private AlbumBeans albumBeans;

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getAlbumList(){
        QueryParameters query = QueryParameters.query(uriInfo.getRequestUri().getQuery()).build();

        List<Album> albumList = albumBeans.getAlbumList(query);
        Long count = albumBeans.getAlbumCout(query);
        return Response.ok(albumList).header("X-Total-Count", count).build();
    }



    @GET
    @Path("/{id}")
    public Response getAlbum(@PathParam("id") Integer id) {

        Album album = albumBeans.getAlbum(id);

        if (album == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(album).build();
    }

    @POST
    public Response creteAlbum(Album album) {

        if ((album.getNaslov() == null || album.getNaslov().isEmpty()) || (album.getOpis() == null
                || album.getOpis().isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            albumBeans.createAlbum(album);
        }

        if (album.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(album).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(album).build();
        }
    }

//    @PUT
//    @Path("{id}")
//    public Response putAlbum(@PathParam("id") String id, Album album) {
//
//        album = albumBeans.putAlbum(id, album);
//
//        if (album == null) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        } else {
//            if (album.getId() != null)
//                return Response.status(Response.Status.OK).entity(album).build();
//            else
//                return Response.status(Response.Status.NOT_MODIFIED).build();
//        }
//    }

    @PUT
    @Path("/{idAlbum}")
    public Response insertSlika(@PathParam("idAlbum") String idAlbum, List<Slika> slikaList){

        Album album = albumBeans.insertSlika(idAlbum, slikaList);
        return Response.status(Response.Status.OK).entity(album).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteSlika(@PathParam("id") String id) {

        boolean deleted = albumBeans.deleteAlbum(id);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}

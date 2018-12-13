package v1.viri;

import beans.AlbumBeans;
import entities.Album;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
@Path("/album")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SlikeResources {

    @Inject
    private AlbumBeans albumBeans;

    @GET
    public Response getAlbumList(){
        List<Album> uporabnikList = albumBeans.getAlbumList();
        return Response.ok(uporabnikList).build();
    }



    @GET
    @Path("/{id}")
    public Response getSlika(@PathParam("id") Integer id) {

        Album album = albumBeans.getAlbum(id);

        if (album == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(album).build();
    }

    @POST
    public Response createSlika(Album album) {

        if ((album.getNaslov() == null || album.getNaslov().isEmpty()) || (album.getOpis() == null
                || album.getOpis().isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            album = albumBeans.createAlbum(album);
        }

        if (album.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(album).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(album).build();
        }
    }
    @PUT
    @Path("{id}")
    public Response putSlika(@PathParam("id") String id, Album album) {

        album = albumBeans.putAlbum(id, album);

        if (album == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (album.getId() != null)
                return Response.status(Response.Status.OK).entity(album).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
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

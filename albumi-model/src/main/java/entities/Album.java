package entities;

import dtos.Slika;

import javax.persistence.*;
import java.util.List;

@Entity
@NamedQueries(value =
        {
                @NamedQuery(name = "Album.getAll", query="SELECT s FROM Album s"),
        })
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String naslov;

    private String opis;

//    @Transient
    private List<Slika> slikeList;


    public Album(String naslov, String opis) {
        this.naslov = naslov;
        this.opis = opis;
    }

    public Album() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNaslov() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public List<Slika> getSlikeList() {
        return slikeList;
    }

    public void setSlikeList(List<Slika> slikeList) {
        this.slikeList = slikeList;
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", naslov='" + naslov + '\'' +
                ", opis='" + opis + '\'' +
                ", slikeList=" + slikeList +
                '}';
    }
}

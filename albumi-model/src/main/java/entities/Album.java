package entities;

import javax.persistence.*;

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

    //List slik


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

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", naslov='" + naslov + '\'' +
                ", opis='" + opis + '\'' +
                '}';
    }
}

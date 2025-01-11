package model;

public class Utilizator {
    private int id;
    private String nume;
    private String email;
    private String tipUtilizator;

    public Utilizator(int id, String nume, String email, String tipUtilizator) {
        this.id = id;
        this.nume = nume;
        this.email = email;
        this.tipUtilizator = tipUtilizator;
    }

    public int getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public String getEmail() {
        return email;
    }

    public String getTipUtilizator() {
        return tipUtilizator;
    }

    @Override
    public String toString() {
        return "Utilizator{" +
                "id=" + id +
                ", nume='" + nume + '\'' +
                ", email='" + email + '\'' +
                ", tipUtilizator='" + tipUtilizator + '\'' +
                '}';
    }
}

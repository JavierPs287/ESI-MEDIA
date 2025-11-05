package edu.uclm.esi.esimedia.be_esimedia.model;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "USUARIOS")
public class Usuario extends User {
    private String alias;
    private Date fechaNacimiento;
    private boolean esVIP = false;

    // Getters and Setters
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }
    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public boolean getEsVIP() {
        return esVIP;
    }
    public void setEsVIP(boolean esVIP) {
        this.esVIP = esVIP;
    }
}

package edu.uclm.esi.esimedia.be_esimedia.dto;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import java.time.Instant;


public class UsuarioDTO extends UserDTO {
    
    private String alias;
    private Instant birthDate;
    private boolean vip = false;

    public UsuarioDTO() { /* Constructor vacío (para @ModelAttribute) */ }

    public UsuarioDTO(User user, Usuario usuario) {
        super.initializeFromModel(user);
        this.alias = usuario.getAlias();
        this.birthDate = usuario.getBirthDate();
        this.vip = usuario.isVip();
     }

    // Getters and Setters
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Instant getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Instant birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }
}

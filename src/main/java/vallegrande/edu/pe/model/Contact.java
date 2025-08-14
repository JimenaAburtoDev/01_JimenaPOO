package vallegrande.edu.pe.model;

import java.util.Objects;

/**
 * Modelo que representa un contacto en la agenda.
 */
public class Contact {
    private String id;
    private String name;
    private String email;
    private String phone;

    public Contact(String id, String name, String email, String phone){
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String id(){ return  id;}
    public String name(){ return name;}
    public String email(){ return  email;}
    public String phone(){ return phone;}
    //Setters
    public  void setName(String name) { this.name = name;}
    public void SetName(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    @Override
    public String toString(){
        return String.format("Contact{id='%s', email='%s' , name='%s' , phone='%s'}" , id,name,email,phone);
    }
    @Override
    public boolean equals(Object o){
        if(this == o ) return true;
        if(!(o instanceof Contact)) return false;
        Contact contact = ( Contact) o;
        return  Objects.equals(id, contact.id);
    }
     @Override
    public int hashCode(){
        return  Objects.hash(id);
    }
}
package vallegrande.edu.pe.controller;

import vallegrande.edu.pe.model.Contact;
import vallegrande.edu.pe.model.ContactRepository;

import java.util.List;
import java.util.Optional;

/**
 * Controlador que maneja la lógica entre la vista y el modelo.
 */
public class ContactController {
   private final ContactRepository repo;
   public ContactController(ContactRepository repo){
       this.repo = repo;
   }
public Contact create(String id, String name, String email, String phone){
       Contact c = new Contact(id, name, email, phone);
       return repo.save(c);
}
    // buscar contacto por ID
    public Optional<Contact> get(String id) {
        return repo.findById(id);
    }
    //Listar todos los contactos
    public List <Contact> list(){
        return repo.findALL();
    }
    //Eliminar el contacto por ID
    public boolean delete(String id){
        return  repo.deleteById(id);
    }
}
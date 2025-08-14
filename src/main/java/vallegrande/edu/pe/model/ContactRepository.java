package vallegrande.edu.pe.model;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el repositorio de contactos.
 */
public interface ContactRepository {
Contact save( Contact contact);
Optional<Contact> findById(String id);;
List<Contact> findALL();

    List<Contact> findAll();

    boolean deleteById (String id);
}
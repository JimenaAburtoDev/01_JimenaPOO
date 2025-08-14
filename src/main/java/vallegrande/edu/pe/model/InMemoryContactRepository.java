package vallegrande.edu.pe.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria del repositorio de contactos.
 */
public class InMemoryContactRepository implements ContactRepository {
    // Usamos un ConcurrentHashMap para hilos seguros (aunque en este ejemplo no es crítico)
    private final Map<String, Contact> store = new ConcurrentHashMap<>();

    @Override
    public Contact save(Contact contact) {
        store.put(contact.id(), contact);
        return contact;
    }

    @Override
    public Optional<Contact> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Contact> findALL() {
        return List.of();
    }

    @Override
    public List<Contact> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean deleteById(String id) {
        return store.remove(id) != null;
    }
}
package com.absensi.absensi.repository;

import com.absensi.absensi.model.Dosen;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface DosenRepository extends MongoRepository<Dosen , String> {
    Optional<Dosen> findByUsername(String username);
    List<Dosen> findByUsernameAndIdNot(String username , String id);
}

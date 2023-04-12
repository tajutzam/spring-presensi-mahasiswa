package com.absensi.absensi.repository;

import com.absensi.absensi.model.Matakuliah;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface MatakuliahRepository extends MongoRepository<Matakuliah , String> {

    Optional<Matakuliah> findByName(String name);
}

package com.absensi.absensi.repository;


import com.absensi.absensi.model.Jurusan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface JurusanRepository extends MongoRepository<Jurusan , String> {
    Optional<Jurusan> findByJurusan(String jurusan);
}

package com.absensi.absensi.repository;

import com.absensi.absensi.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public interface StudentRepository extends MongoRepository<Student , String> {
    Optional<Student> findByNim(String nim);
    List<Student> findByNimNot(String nim);


    List<Student> findByNamaContaining(String nama);

    List<Student> findByNimAndIdNot(String nim, String id);

}

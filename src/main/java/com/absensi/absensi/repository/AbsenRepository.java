package com.absensi.absensi.repository;

import com.absensi.absensi.model.Absen;
import com.absensi.absensi.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AbsenRepository extends MongoRepository<Absen , String> {
    List<Absen> findByStudent(Student student);

    void deleteByStudent(Student student);
}

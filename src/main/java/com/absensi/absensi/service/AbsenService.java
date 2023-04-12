package com.absensi.absensi.service;

import com.absensi.absensi.model.Absen;
import com.absensi.absensi.model.Student;

import java.util.List;
import java.util.Optional;

public interface AbsenService {

    List<Absen> findByStudent(Student student);
    List<Absen> findAll();
    boolean insert(Absen absen);

    boolean deleteById(String id);

    Optional<Absen> findById(String id);
    boolean update(Absen absen , String id);
}

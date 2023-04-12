package com.absensi.absensi.service;

import com.absensi.absensi.model.Absen;
import com.absensi.absensi.model.Jurusan;
import com.absensi.absensi.model.Student;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

public interface StudentService {


    Optional<Student> addStudent(Student student, Jurusan jurusan);

    Optional<Student> login(Student student, HttpServletRequest request);

    Optional<Student> findByNim(String nim);


    List<Absen> recordAbsensi(String nim);

    void Logout();

    List<Student> findByNimNot(String nim);


    Boolean updateStudent(Student student, String nim);


    List<Student> findAll();


    Optional<Student> findById(String id);

    boolean deleteByID(String id);

    List<Student> findByName(String name);

    List<Student> findByNimAndIdNot(String nim, String id);

}

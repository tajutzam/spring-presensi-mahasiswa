package com.absensi.absensi.service.impl;

import com.absensi.absensi.model.Absen;
import com.absensi.absensi.model.Jurusan;
import com.absensi.absensi.model.enums.Role;
import com.absensi.absensi.model.Student;
import com.absensi.absensi.repository.AbsenRepository;
import com.absensi.absensi.repository.StudentRepository;
import com.absensi.absensi.service.AbsenService;
import com.absensi.absensi.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {


    @Autowired
    @Qualifier("customAuthenticationManager")
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AbsenService absenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private AbsenRepository absenRepository;

    @Override
    public Optional<Student> addStudent(Student student, Jurusan jurusan) {
        Optional<Student> studentOptional = studentRepository.findByNim(student.getNim());
        if (studentOptional.isPresent()) {
            return Optional.empty();
        } else {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
            student.setJurusan(jurusan);
            student.setRole(Role.STUDENT);
            studentRepository.insert(student);

            return Optional.of(student);
        }
    }

    @Override
    public Optional<Student> login(Student student, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(student.getNim());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(student.getNim(), student.getPassword(), userDetails.getAuthorities());
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            Student student1 = studentRepository.findByNim(student.getNim()).get();
            return Optional.of(student1);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Student> findByNim(String nim) {
        return studentRepository.findByNim(nim);
    }

    @Override
    public List<Absen> recordAbsensi(String nim) {
        Optional<Student> optional = findByNim(nim);
        if (optional.isPresent()) {
            return absenService.findByStudent(optional.get());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void Logout() {

    }

    @Override
    public List<Student> findByNimNot(String nim) {
        return studentRepository.findByNimNot(nim);
    }

    @Override
    public Boolean updateStudent(Student student, String nim) {
        List<Student> studentList = findByNimAndIdNot(student.getNim(), student.getId());
        if (studentList.isEmpty()) {
            Optional<Student> studentOpti = findByNim(nim);
            if (studentOpti.isPresent()) {
                Student update = studentOpti.get();
                List<Absen> absens = absenService.findByStudent(update);
                update.setNim(student.getNim());
                update.setNama(student.getNama());
                update.setEmail(student.getEmail());
                update.setKelas(student.getKelas());
                studentRepository.save(update);

                absens.forEach(absen -> {
                    absen.setStudent(update);
                    absenRepository.save(absen);
                });
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll(Sort.by("nama"));
    }

    @Override
    public Optional<Student> findById(String id) {
        return studentRepository.findById(id);
    }

    @Override
    public boolean deleteByID(String id) {
        Optional<Student> studentOptional = findById(id);
        if (studentOptional.isPresent()) {
            absenRepository.deleteByStudent(studentOptional.get());
            studentRepository.delete(studentOptional.get());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Student> findByName(String name) {
        return studentRepository.findByNamaContaining(name);
    }

    @Override
    public List<Student> findByNimAndIdNot(String nim, String id) {
        return studentRepository.findByNimAndIdNot(nim, id);
    }
}

package com.absensi.absensi.service.impl;

import com.absensi.absensi.model.Absen;
import com.absensi.absensi.model.Student;
import com.absensi.absensi.repository.AbsenRepository;
import com.absensi.absensi.service.AbsenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AbsenServiceImpl implements AbsenService {


    @Autowired
    private AbsenRepository absenRepository;

    @Override
    public List<Absen> findByStudent(Student student) {
        return absenRepository.findByStudent(student);
    }

    @Override
    public List<Absen> findAll() {
        return absenRepository.findAll();
    }

    @Override
    public boolean insert(Absen absen) {
        try {
            Absen insert = absenRepository.insert(absen);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public boolean deleteById(String id) {
        absenRepository.deleteById(id);
        if (findById(id).isPresent()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Optional<Absen> findById(String id) {
        return absenRepository.findById(id);
    }

    @Override
    public boolean update(Absen absen, String id) {
        Optional<Absen> absenOptional = findById(id);
        if (absenOptional.isPresent()) {
            Absen absenOld = absenOptional.get();
            absenOld.setStatus(absen.getStatus());
            absenRepository.save(absenOld);
            return true;
        }
        return false;
    }
}

package com.absensi.absensi.service;

import com.absensi.absensi.model.Matakuliah;

import java.util.List;
import java.util.Optional;

public interface MatakuliahService {
    List<Matakuliah> findAll();
    Optional<Matakuliah> findById(String id);
}

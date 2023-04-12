package com.absensi.absensi.service.impl;

import com.absensi.absensi.model.Matakuliah;
import com.absensi.absensi.repository.MatakuliahRepository;
import com.absensi.absensi.service.MatakuliahService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatakuliahServiceImpl implements MatakuliahService {

    @Autowired
    private MatakuliahRepository matakuliahRepository;

    @Override
    public List<Matakuliah> findAll() {
        return matakuliahRepository.findAll();
    }

    @Override
    public Optional<Matakuliah> findById(String id) {
        return matakuliahRepository.findById(id);
    }
}

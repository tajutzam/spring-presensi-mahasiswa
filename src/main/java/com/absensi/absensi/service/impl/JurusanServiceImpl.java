package com.absensi.absensi.service.impl;

import com.absensi.absensi.model.Jurusan;
import com.absensi.absensi.repository.JurusanRepository;
import com.absensi.absensi.service.JurusanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JurusanServiceImpl implements JurusanService {


    @Autowired
    private JurusanRepository jurusanRepository;

    @Override
    public List<Jurusan> findAll() {
        return jurusanRepository.findAll();
    }
}

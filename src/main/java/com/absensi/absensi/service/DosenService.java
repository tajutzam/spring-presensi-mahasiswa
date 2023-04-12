package com.absensi.absensi.service;

import com.absensi.absensi.model.Dosen;

import java.util.List;
import java.util.Optional;

public interface DosenService {


   boolean login(Dosen dosen);


   Optional<Dosen> findByUsername(String username);

   Boolean addDosen(Dosen dosen);

   boolean updateDosen(Dosen dosen , String id);

   List<Dosen> findByUsernameIdNot(String username , String nim);

   List<Dosen> findAll();

   Optional<Dosen> findById(String id);
}

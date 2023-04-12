package com.absensi.absensi.service.impl;

import com.absensi.absensi.model.Dosen;
import com.absensi.absensi.model.enums.Role;
import com.absensi.absensi.repository.DosenRepository;
import com.absensi.absensi.service.DosenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DosenServiceImpl implements DosenService {


    @Autowired
    @Qualifier("authenticationManagerDosen")
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("userDetailsServiceDosen")
    private UserDetailsService userDetailsService;
    @Autowired
    private DosenRepository dosenRepository;

    @Override
    public boolean login(Dosen dosen) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(dosen.getUsername());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(dosen.getUsername(), dosen.getPassword(), userDetails.getAuthorities());
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            Optional<Dosen> dosen1 = dosenRepository.findByUsername(dosen.getUsername());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<Dosen> findByUsername(String username) {

        return dosenRepository.findByUsername(username);
    }

    @Override
    public Boolean addDosen(Dosen dosen) {
        Optional<Dosen> dosenOptional = dosenRepository.findByUsername(dosen.getUsername());
        if (dosenOptional.isPresent()) {
            return false;
        }
        dosen.setRole(Role.DOSEN);
        dosen.setPassword(passwordEncoder.encode(dosen.getPassword()));
        dosenRepository.insert(dosen);
        return true;
    }

    @Override
    public boolean updateDosen(Dosen dosen, String id) {
        List<Dosen> dosenList = findByUsernameIdNot(dosen.getUsername(), id);
        if (dosenList.isEmpty()) {
            Optional<Dosen> optionalDosen = dosenRepository.findById(id);
            if (optionalDosen.isPresent()) {
                var newDosen = optionalDosen.get();
                newDosen.setNamaDosen(dosen.getNamaDosen());
                newDosen.setUsername(dosen.getUsername());
                newDosen.setMatakuliah(dosen.getMatakuliah());
                dosenRepository.save(newDosen);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public List<Dosen> findByUsernameIdNot(String username, String id) {
        return dosenRepository.findByUsernameAndIdNot(username, id);
    }

    @Override
    public List<Dosen> findAll() {
        return dosenRepository.findAll();
    }

    @Override
    public Optional<Dosen> findById(String id) {
        return dosenRepository.findById(id);
    }
}

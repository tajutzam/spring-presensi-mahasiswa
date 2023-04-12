package com.absensi.absensi.controller;

import com.absensi.absensi.model.*;
import com.absensi.absensi.model.enums.STATUS;
import com.absensi.absensi.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dosen")
public class DosenController {

    @Autowired
    DosenService dosenService;

    @Autowired
    private AbsenService absenService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private JurusanService jurusanService;

    @Autowired
    private MatakuliahService matakuliahService;

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("dosen" , new Dosen());
        return "dosen/login";
    }

    @PostMapping("/authenticate")
    public String postLogin(@ModelAttribute("dosen") Dosen dosen , RedirectAttributes attributes){
        boolean login = dosenService.login(dosen);
        System.out.println(login);
        if(login){
            attributes.addFlashAttribute("message" , "berhasil login");
            return "redirect:dashboard";
        }else{
            return "dosen/login";
        }
    }

    @GetMapping("/dashboard")
    public String home(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null){
            String username = authentication.getName();
            Optional<Dosen> dosenOptional = dosenService.findByUsername(username);
            if(dosenOptional.isPresent()){
                List<Absen> absens = absenService.findAll();
                List<Jurusan> jurusans = jurusanService.findAll();
                model.addAttribute("jurusans" , absens);
                model
                        .addAttribute("absens" , absens );
                model.addAttribute("dosen" , dosenOptional.get());
                return "dosen/dashboard";
            }else{
                return "login";
            }
        }else{
        return "login";
        }
    }


    @GetMapping("/mahasiswa")
    public String findAllMahasiswa(Model model){
        List<Student> students = studentService.findAll();
        model
                .addAttribute("students" , students);
        System.out.println(students);
        return "dosen/mahasiswa";
    }

    @PostMapping("/mahasiswa/delete/{id}")
    public RedirectView deleteMahasiswa(@PathVariable("id") String id , RedirectAttributes atrAttributes){
        boolean isDelete = studentService.deleteByID(id);
        if(isDelete){
            System.out.println("deleted");
            atrAttributes.addFlashAttribute("message" , "berhasil menghapus mahasiswa");
            return new RedirectView("/dosen/mahasiswa");
        }else{
            atrAttributes.addFlashAttribute("error" , "gagal menghapus mahasiswa");
            return new RedirectView("/dosen/mahasiswa");
        }
    }

    @GetMapping("/mahasiswa/{id}")
    public String findById(@PathVariable("id") String id , Model model){
        System.out.println(id);
        Optional<Student> studentOptional = studentService.findById(id);
        if(studentOptional.isPresent()){
            model.addAttribute("mahasiswa" , studentOptional.get());
            return "dosen/edit_mahasiswa";
        }else{
            return "redirect:dosen/mahasiswa";
        }
    }


    @GetMapping("/edit")
    public String edit(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            String username = authentication.getName();
            Optional<Dosen> dosenOptional = dosenService.findByUsername(username);
            if(dosenOptional.isPresent()){
                String id = dosenOptional.get().getId();
                List<Matakuliah> matakuliahs = matakuliahService.findAll();
                model.addAttribute("dosen" , dosenOptional.get());
                model.addAttribute("matkuls" , matakuliahs);

                return "dosen/edit_dosen";
            }else{
                return "student/login";
            }
        }else{
           return "student/login";
        }
    }

    @PostMapping("/edit")
    public RedirectView update(RedirectAttributes attributes , @ModelAttribute("dosen") Dosen dosen  , HttpServletRequest request, @RequestParam("matkulSelected") String selectedmatkul) throws ServletException {
        String id = dosen.getId();
        Optional<Matakuliah> matakuliahOptional = matakuliahService.findById(selectedmatkul);
        if(matakuliahOptional.isPresent()){
            dosen.setMatakuliah(matakuliahOptional.get());
            boolean isUpdate = dosenService.updateDosen(dosen, id);
            if(isUpdate){
                attributes.addFlashAttribute("message" , "berhasil memperbarui data dosen silahkan login kembali");
                request.logout();
                return new RedirectView("/login");
            }else{
                attributes.addFlashAttribute("error" , "gagal memperbarui data , username sudah digunakan");
                return new RedirectView("/dosen/edit");
            }
        }else{
            attributes.addFlashAttribute("error" , "gagal memperbarui data matkul tidak ditemukan");
            return new RedirectView("/dosen/dashboard");
        }

    }


    @PostMapping("/logout")
    public RedirectView logout(HttpServletRequest request , RedirectAttributes attributes) throws ServletException {
        request.logout();
        attributes.addFlashAttribute("message" , "berhasil logout");
        return new RedirectView("/login");
    }

    @PostMapping("/mahasiswa/search")
    public ModelAndView searchMahasiswaByName(@RequestParam("name") String name , Model model){
        List<Student> students = studentService.findByName(name);
        System.out.println("searching");
        System.out.println(students);
        System.out.println(name);
        model.addAttribute("students" , students);
        return new ModelAndView("/dosen/mahasiswa");
    }

    @GetMapping("/mahasiswa/add")
    public String getAddPage(Model model){
        model.addAttribute("mhs" , new Student());
        model.addAttribute("jurusans" , jurusanService.findAll());
        return "dosen/tambah_mahasiswa";
    }


    @PostMapping("/mahasiswa/add/post")
    public RedirectView addMahasiswa(@ModelAttribute("mhs") Student student , RedirectAttributes attributes , @ModelAttribute("jurusan")Jurusan jurusan){
        Optional<Student> addStudent = studentService.addStudent(student, jurusan);
        if(addStudent.isPresent()){
            attributes.addFlashAttribute("message" , "berhasil menambahkan mahasiswa");
        }else{
            attributes.addFlashAttribute("error" , "gagal menambahkan student , nim sudah digunakan");
        }
        return new RedirectView("/dosen/mahasiswa");
    }


    @PostMapping("/mahasiswa/{id}")
    public RedirectView updateMahasiswa(@PathVariable("id") String id , @ModelAttribute("mahasiswa") Student studentParameter , RedirectAttributes atributes){
        Optional<Student> student = studentService.findById(id);
        if(student.isPresent()){
            Student newStudent = student.get();
            newStudent.setNama(studentParameter.getNama());
            newStudent.setEmail(studentParameter.getEmail());
            newStudent.setNim(studentParameter.getNim());
            newStudent.setKelas(studentParameter.getKelas());
            System.out.println(studentParameter.getNim());
            Boolean isUpdate = studentService.updateStudent(newStudent, newStudent.getNim());
            if(isUpdate){
                atributes.addFlashAttribute("message" , "berhasil memperbarui mahasiswa");
                return new RedirectView("/dosen/mahasiswa");
            }else{
                atributes.addFlashAttribute("error" , "gagal memperbarui mahasiswa , nim sudah digunakan");
                return new RedirectView("/dosen/mahasiswa");
            }
        }else{
            atributes.addFlashAttribute("error" , "gagal memperbarui mahasiswa , mahasiswa tidak ditemukan");
            return new RedirectView("/dosen/mahasiswa");
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("dosen" , new Dosen());
        model.addAttribute("matkuls" , matakuliahService.findAll());
        return "dosen/register";
    }

    @PostMapping("/register")
    public RedirectView postRegister(@ModelAttribute("dosen") Dosen dosen , RedirectAttributes atr , @RequestParam("matkulSelected") String idMatkul){
        Optional<Matakuliah> matakuliahOptional = matakuliahService.findById(idMatkul);
        if(matakuliahOptional.isPresent()){
            dosen.setMatakuliah(matakuliahOptional.get());
            Boolean isAdded = dosenService.addDosen(dosen);
            if(isAdded){
                atr.addFlashAttribute("message" , "berhasil registrasi dosen");
                return new RedirectView("/student/login");
            }else{
                atr.addFlashAttribute("error" , "gagal registrasi dosen , username sudah digunakan");
                return new RedirectView("/student/login");
            }
        }else{
            atr.addFlashAttribute("error" , "gagal registrasi dosen , username sudah digunakan");
            return new RedirectView("/dosen/register");
        }
    }


    @PostMapping("/absensi/update")
    public RedirectView updateStatusAbsensi(@RequestParam("statusSelected") String status , @RequestParam("id") String id , RedirectAttributes attributes){
        Absen absen = new Absen();
        System.out.println(status);
        switch (status) {
            case "HADIR" -> absen.setStatus(STATUS.HADIR);
            case "IZIN" -> absen.setStatus(STATUS.IZIN);
            case "SAKIT" -> absen.setStatus(STATUS.SAKIT);
            case "ALFA" -> absen.setStatus(STATUS.ALFA);
        }
        boolean isUpdate = absenService.update(absen, id);
        if(isUpdate){
            attributes.addFlashAttribute("message" , "berhasil memperbarui absensi");
        }else{
            attributes.addFlashAttribute("error" , "gagal memperbarui absensi");
        }
        return new RedirectView("/dosen/dashboard");
    }

    @PostMapping("/absensi/delete/{id}")
    public RedirectView deleteAbsensi(@PathVariable("id") String id , RedirectAttributes attributes){
        boolean isDelete = absenService.deleteById(id);
        if(isDelete){
          attributes.addFlashAttribute("message" , "berhasil menghapus record absensi");
        }else{
            attributes.addFlashAttribute("error" , "gagal menghapus record absensi");
        }
        return new RedirectView("/dosen/dashboard");
    }
}

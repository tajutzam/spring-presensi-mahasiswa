package com.absensi.absensi.controller;


import com.absensi.absensi.model.*;
import com.absensi.absensi.model.enums.STATUS;
import com.absensi.absensi.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {


    @Autowired
    StudentService studentService;

    @Autowired
    private DosenService dosenService;
@Autowired
private MatakuliahService matakuliahService;
    @Autowired
    private AbsenService absenService;

    @Autowired
    private JurusanService jurusanService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model
                .addAttribute("mhs" , new Student());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null){
            if(authentication.getName().equalsIgnoreCase("anonymousUser")){
                return "student/login";
            }else{
                return "redirect:dashboard";
            }
        }else{
            return "student/login";
        }
    }
    @PostMapping("/authenticate")
    public String authenticate(@ModelAttribute("mhs") Student student , RedirectAttributes attributes  , HttpServletRequest request)  {
        Optional<Student> studentOptional = studentService.login(student , request);
        if(studentOptional.isPresent()){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean isDosen = false;
            boolean isStudnt = false;
            for (GrantedAuthority grantedAuthority : authorities) {
                if (grantedAuthority.getAuthority().equals("DOSEN")) {
                    isDosen = true;
                    break;
                } else if (grantedAuthority.getAuthority().equals("STUDENT")) {
                    isStudnt = true;
                    break;
                }
            }
            if(isDosen){
                return "dosen/dashboard";
            }else{
                if(isStudnt){
                    attributes.addAttribute("mhs" , studentOptional.get());
                    return "redirect:/dashboard";
                }
            }
        }else{
            attributes.addFlashAttribute("error" , "Failed login , please check your nim or password");
            return "redirect:login";
        }
        return null;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var name = SecurityContextHolder.getContext().getAuthentication();
        if(name != null){
            List<Absen> records = studentService.recordAbsensi(name.getName());
            model.addAttribute("records" , records);
            model.addAttribute("dosens" , dosenService.findAll());
            return "student/dashbord";
        }else{
            return "redirect:login";
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        model
                .addAttribute("jurusans" , jurusanService.findAll());
        model.addAttribute("mhs" , new Student());
        return "student/register";
    }

    @PostMapping("/register")
    public String registerPost(@ModelAttribute("jurusan") Jurusan jurusan, @ModelAttribute("mhs") Student student, RedirectAttributes redirectAttributes) {
        Optional<Student> addStudent = studentService.addStudent(student, jurusan);
        if (!addStudent.isPresent()) {
            redirectAttributes.addFlashAttribute("success", false);
            redirectAttributes.addFlashAttribute("message", "Nim sudah digunakan");
            return "redirect:register";
        } else {
            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Success register");
            return "redirect:login";
        }
    }

    @PostMapping("/logout")
    public String logoutDo(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        request.logout();
        return "redirect:login";
    }


    @PostMapping("/absen")
    public String absen(@RequestParam("dosen") String dosen ,  @RequestParam("date") String date ,  @RequestParam("status") String status , RedirectAttributes redirectAttributes) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Absen absen = new Absen();
        Optional<Dosen> dosenOptional = dosenService.findById(dosen);
        if(dosenOptional.isPresent()){
            absen.setDosen(dosenOptional.get());
            Date dateTime = formatter.parse(date);
            absen.setDate(dateTime);
            switch (status){
                case "hadir" : absen.setStatus(STATUS.HADIR);
                    break;
                case "izin" : absen.setStatus(STATUS.IZIN);
                    break;
                case "alpha" : absen.setStatus(STATUS.ALFA);
                    break;
                case "sakit" : absen.setStatus(STATUS.SAKIT);
                    break;
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication != null){
                Optional<Student> studentOptional = studentService.findByNim(authentication.getName());
                absen.setStudent(studentOptional.get());
                absen.setDate(dateTime);
                absenService.insert(absen);
                redirectAttributes.addFlashAttribute("message" , "Berhasil melakukan presensi");
                return "redirect:dashboard";
            }else{
                return "redirect:login";
            }
        }else{
           redirectAttributes.addFlashAttribute("error" , "dosen tidak ditemukan");
            return "redirect:dashboard";
        }
    }

    @GetMapping("/edit")
    public String edit(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated()){
            Optional<Student> student = studentService.findByNim(authentication.getName());
            if(student.isPresent()){
                model
                        .addAttribute("mhs" , student);
                return "student/edit";
            }else{
                return "redirect:login";
            }
        }else {
            return "redirect:login";
        }
    }

    @PostMapping("/absensi/update")
    public RedirectView updateStatusAbsensi(@RequestParam("statusSelected") String status , @RequestParam("id") String id , RedirectAttributes attributes){
        Absen absen = new Absen();
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
        return new RedirectView("/student/dashboard");
    }

    @PostMapping("update")
    public String update(@ModelAttribute("mhs") Student student , RedirectAttributes attributes , HttpServletRequest request) throws ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Boolean isUpdate = studentService.updateStudent(student , authentication.getName());
        if(isUpdate){
            attributes.addFlashAttribute("message" , "success update");
            request.logout();
            return "redirect:login";
        }else{
            attributes.addFlashAttribute("error" , "gagal update");
            return "redirect:edit";
        }
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

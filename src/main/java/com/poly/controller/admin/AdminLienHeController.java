package com.poly.controller.admin;

import com.poly.entity.LienHe;
import com.poly.repository.LienHeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class AdminLienHeController {

    @Autowired
    private LienHeRepository lienHeRepository;

    @GetMapping("/admin/lien-he")
    public String inbox(Model model) {
        model.addAttribute("messages", lienHeRepository.findAllByOrderByNgayGuiDesc());
        model.addAttribute("unreadCount", lienHeRepository.countUnread());
        return "admin/lienhe/inbox";
    }

    @PostMapping("/admin/lien-he/{id}/read")
    public String markRead(@PathVariable Integer id) {
        lienHeRepository.findById(id).ifPresent(m -> {
            m.setDaDoc(true);
            lienHeRepository.save(m);
        });
        return "redirect:/admin/lien-he";
    }

    @PostMapping("/admin/lien-he/{id}/delete")
    public String delete(@PathVariable Integer id) {
        lienHeRepository.deleteById(id);
        return "redirect:/admin/lien-he";
    }
}

package com.tienda.controlador;

import com.tienda.modelo.Cupon;
import com.tienda.servicio.CuponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/cupones")
public class AdminCuponController {

    @Autowired
    private CuponService cuponService;

    @GetMapping
    public String listarCupones(Model model) {
        model.addAttribute("cupones", cuponService.obtenerTodos());
        model.addAttribute("cuponForm", new Cupon());
        return "admin/admin_cupones";
    }

    @PostMapping
    public String guardarCupon(@ModelAttribute Cupon cupon) {
        cuponService.guardar(cupon);
        return "redirect:/admin/cupones";
    }

    @PostMapping("/actualizar")
    public String actualizarCupon(@ModelAttribute Cupon cupon) {
        System.out.println("Actualizando cupón con ID: " + cupon);
        cuponService.actualizar(cupon);
        return "redirect:/admin/cupones";
    }
}

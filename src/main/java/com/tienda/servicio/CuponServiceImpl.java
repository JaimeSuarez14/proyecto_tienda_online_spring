package com.tienda.servicio;

import com.tienda.modelo.Cupon;
import com.tienda.repositorio.CuponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CuponServiceImpl implements CuponService {

    @Autowired
    private CuponRepository cuponRepository;

    /**
     * Busca un cupón por su código (nombreClave).
     * @param nombreClave El código del cupón.
     * @return El cupón encontrado, o Optional.empty() si no existe.
     */
    @Override
    public Optional<Cupon> buscarCuponPorCodigo(String nombreClave) {
        return cuponRepository.findByNombreClave(nombreClave);
    }

    /**
     * Valida si un cupón es válido (activo y no expirado).
     * @param cupon El cupón a validar.
     * @return true si el cupón es válido, false en caso contrario.
     */
    @Override
    public boolean esValido(Cupon cupon) {
        if (cupon == null) {
            return false;
        }

        // Verificar si el cupón está activo
        if (!cupon.isActivo()) {
            return false;
        }

        // Verificar si el cupón ha expirado
        if (cupon.getFechaExpiracion() != null && cupon.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }
}

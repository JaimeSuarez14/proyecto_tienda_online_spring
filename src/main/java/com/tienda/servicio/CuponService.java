package com.tienda.servicio;

import com.tienda.modelo.Cupon;
import java.util.Optional;

public interface CuponService {
    /**
     * Busca un cupón por su código (nombreClave).
     * @param nombreClave El código del cupón.
     * @return El cupón encontrado, o Optional.empty() si no existe.
     */
    Optional<Cupon> buscarCuponPorCodigo(String nombreClave);

    /**
     * Valida si un cupón es válido (activo y no expirado).
     * @param cupon El cupón a validar.
     * @return true si el cupón es válido, false en caso contrario.
     */
    boolean esValido(Cupon cupon);
}

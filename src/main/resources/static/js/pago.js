// Variables globales que se inicializarán desde el HTML
let metodoPagoGlobal = '';
let numeroBoletaGlobal = '';
let totalPagarGlobal = 0;

/**
 * Inicializa las variables globales con los datos del servidor.
 * @param {string} metodoPago - El método de pago seleccionado.
 * @param {string} numeroBoleta - El número de boleta generado.
 * @param {number} totalPagar - El monto total a pagar.
 */
function inicializarVariables(metodoPago, numeroBoleta, totalPagar) {
    metodoPagoGlobal = metodoPago;
    numeroBoletaGlobal = numeroBoleta;
    totalPagarGlobal = totalPagar;
}

/**
 * Muestra el modal de verificación.
 */
function mostrarModalVerificacion() {
    const modal = document.getElementById('verificacionModal');
    if (modal) {
        modal.classList.remove('hidden');
    }
}

/**
 * Oculta el modal de verificación.
 */
function ocultarModalVerificacion() {
    const modal = document.getElementById('verificacionModal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

/**
 * Inicia el proceso de verificación de pago.
 * Por ahora, simplemente simula una espera y luego envía el formulario.
 */
function iniciarVerificacion() {
    const form = document.querySelector('form[action="/pago/confirmar"]');
    
    if (form) {
        form.addEventListener('submit', function(event) {
            // Prevenir el envío inmediato para mostrar el modal
            event.preventDefault();
            
            // Mostrar el modal de "verificando"
            mostrarModalVerificacion();
            
            // Simular una pequeña espera antes de enviar el formulario.
            // Esto le da tiempo al usuario de ver el modal.
            setTimeout(() => {
                // Enviar el formulario de forma programática
                form.submit();
            }, 1500); // 1.5 segundos de espera
        });
    }
}

// La lógica de verificación automática que tenías antes podría ir aquí si la necesitas.
// Por ahora, la hemos simplificado para asegurar que el formulario se envíe.

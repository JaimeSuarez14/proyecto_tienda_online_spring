(() => {
    const modal = document.getElementById('productModal');
    const form = document.getElementById('productForm');
    const modalTitle = document.getElementById('modalTitle');
    const productId = document.getElementById('productId');

    // Estado del ordenamiento
    let ordenSeleccionado = localStorage.getItem('ordenProductos') || 'precio-asc';

    const openProductModal = () => {
        modalTitle.innerText = 'Nuevo Producto';
        form.reset();
        productId.value = '';
        modal.classList.remove('hidden');
    };

    const closeProductModal = () => {
        modal.classList.add('hidden');
    };

    const editProduct = (button) => {
        modalTitle.innerText = 'Editar Producto';
        productId.value = button.dataset.id;
        document.getElementById('nombre').value = button.dataset.nombre;
        document.getElementById('descripcion').value = button.dataset.descripcion;
        document.getElementById('categoria').value = button.dataset.categoria;
        document.getElementById('marca').value = button.dataset.marca;
        document.getElementById('unidadMedida').value = button.dataset.unidadmedida;
        document.getElementById('precio').value = button.dataset.precio;
        document.getElementById('descuento').value = button.dataset.descuento;
        document.getElementById('stock').value = button.dataset.stock;
        document.getElementById('imagenUrl').value = button.dataset.imagenurl;
        document.getElementById('disponible').checked = button.dataset.disponible === 'true';
        document.getElementById('promocion').checked = button.dataset.promocion === 'true';
        modal.classList.remove('hidden');
    };

    const submitProductForm = async (event) => {
        event.preventDefault();

        const formData = new FormData(form);
        const productData = {
            disponible: false,
            promocion: false
        };
        for (const [key, value] of formData.entries()) {
            if (key === 'disponible' || key === 'promocion') {
                productData[key] = value === 'on';
            } else {
                productData[key] = value;
            }
        }

        try {
            const response = await fetch('/admin/productos/guardar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(productData)
            });

            const result = await response.json();

            if (response.ok) {
                alert(result.message);
                closeProductModal();
                window.location.reload();
            } else {
                alert(result.message || 'Error al guardar el producto.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error de conexion. Intentelo de nuevo.');
        }
    };

    const deleteProduct = async (button) => {
        const  productIdValue = button.dataset.id;
        if (!confirm('¿Estas seguro de que quieres eliminar este producto? Esta accion es irreversible.')) {
            return;
        }

        try {
            const response = await fetch(`/admin/productos/eliminar/${productIdValue}`, {
                method: 'DELETE'
            });

            const result = await response.json();

            if (response.ok) {
                alert(result.message);
                window.location.reload();
            } else {
                alert(result.message || 'Error al eliminar el producto.');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error de conexion. Intentelo de nuevo.');
        }
    };

    // Función para ordenar productos en la tabla
    const ordenarProductos = (tipoOrden) => {
        const tbody = document.querySelector('table tbody');
        const filas = Array.from(tbody.querySelectorAll('tr'));

        filas.sort((filaA, filaB) => {
            // Obtener el precio de cada fila (columna 7, índice 6)
            const precioA = parseFloat(filaA.querySelectorAll('td')[6].textContent.replace('S/ ', '').replace(',', ''));
            const precioB = parseFloat(filaB.querySelectorAll('td')[6].textContent.replace('S/ ', '').replace(',', ''));

            if (tipoOrden === 'precio-asc') {
                return precioA - precioB;
            } else if (tipoOrden === 'precio-desc') {
                return precioB - precioA;
            }
        });

        // Limpiar tabla y agregar filas ordenadas
        filas.forEach(fila => tbody.appendChild(fila));
    };

    // Función para actualizar la UI de la opción seleccionada
    const actualizarIndicadorOrdenamiento = () => {
        document.getElementById('check-asc').classList.add('hidden');
        document.getElementById('check-desc').classList.add('hidden');

        if (ordenSeleccionado === 'precio-asc') {
            document.getElementById('check-asc').classList.remove('hidden');
        } else if (ordenSeleccionado === 'precio-desc') {
            document.getElementById('check-desc').classList.remove('hidden');
        }
    };

    // Configurar event listeners para los botones de ordenamiento
    document.querySelectorAll('.btn-ordenar').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const tipoOrden = btn.dataset.orden;
            ordenSeleccionado = tipoOrden;
            localStorage.setItem('ordenProductos', tipoOrden);
            ordenarProductos(tipoOrden);
            actualizarIndicadorOrdenamiento();
        });
    });

    // Aplicar ordenamiento al cargar la página
    document.addEventListener('DOMContentLoaded', () => {
        actualizarIndicadorOrdenamiento();
        if (document.querySelector('table tbody tr')) {
            ordenarProductos(ordenSeleccionado);
        }
    });
    // Si el DOM ya está listo al ejecutar este script, aplicar el ordenamiento
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            actualizarIndicadorOrdenamiento();
            if (document.querySelector('table tbody tr')) {
                ordenarProductos(ordenSeleccionado);
            }
        });
    } else {
        actualizarIndicadorOrdenamiento();
        if (document.querySelector('table tbody tr')) {
            ordenarProductos(ordenSeleccionado);
        }
    }

    document.getElementById('btnNuevoProducto')?.addEventListener('click', openProductModal);
    document.querySelectorAll('.js-modal-close').forEach((button) => {
        button.addEventListener('click', closeProductModal);
    });
    form?.addEventListener('submit', submitProductForm);

    window.openProductModal = openProductModal;
    window.closeProductModal = closeProductModal;
    window.editProduct = editProduct;
    window.deleteProduct = deleteProduct;
})();

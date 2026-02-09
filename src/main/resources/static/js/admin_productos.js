(() => {
    const modal = document.getElementById('productModal');
    const form = document.getElementById('productForm');
    const modalTitle = document.getElementById('modalTitle');
    const productId = document.getElementById('productId');

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

    const deleteProduct = async (productIdValue) => {
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

// src/main/resources/static/admin_equipment.js

document.addEventListener('DOMContentLoaded', function() {
    // Add Equipment
    document.getElementById('addEquipmentForm')?.addEventListener('submit', function(e) {
        e.preventDefault();
        const form = e.target;
        fetch('/admin/api/add-equipment', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `name=${encodeURIComponent(form.name.value)}&description=${encodeURIComponent(form.description.value)}&quantity=${encodeURIComponent(form.quantity.value)}`
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) location.reload();
                else alert('Failed to add equipment');
            });
    });

    // Edit Equipment
    document.querySelectorAll('.edit-equipment-form').forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            const equipmentId = form.dataset.equipmentid;
            fetch(`/admin/api/update-equipment/${equipmentId}`, {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: `name=${encodeURIComponent(form.name.value)}&description=${encodeURIComponent(form.description.value)}&quantity=${encodeURIComponent(form.quantity.value)}`
            })
                .then(res => res.json())
                .then(data => {
                    if (data.success) location.reload();
                    else alert('Failed to update equipment');
                });
        });
    });

    // Delete Equipment
    document.querySelectorAll('.delete-equipment-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            if (confirm('Are you sure you want to delete this equipment?')) {
                fetch(`/admin/api/delete-equipment/${btn.dataset.equipmentid}`, {method: 'POST'})
                    .then(res => res.json())
                    .then(data => {
                        if (data.success) location.reload();
                        else alert('Failed to delete equipment');
                    });
            }
        });
    });
});
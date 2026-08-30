// src/main/resources/static/admin_members.js

document.addEventListener('DOMContentLoaded', function() {
    // Add User
    document.getElementById('addUserForm')?.addEventListener('submit', function(e) {
        e.preventDefault();
        const form = e.target;
        fetch('/admin/api/add-user', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: `name=${encodeURIComponent(form.name.value)}&email=${encodeURIComponent(form.email.value)}&password=${encodeURIComponent(form.password.value)}`
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) location.reload();
                else alert(data.message || 'Failed to add user');
            });
    });

    // Edit User
    document.querySelectorAll('.edit-user-form').forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            const userId = form.dataset.userid;
            fetch(`/admin/api/update-user/${userId}`, {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: `name=${encodeURIComponent(form.name.value)}&email=${encodeURIComponent(form.email.value)}&password=${encodeURIComponent(form.password.value)}`
            })
                .then(res => res.json())
                .then(data => {
                    if (data.success) location.reload();
                    else alert(data.message || 'Failed to update user');
                });
        });
    });

    // Delete User
    document.querySelectorAll('.delete-user-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            if (confirm('Are you sure you want to delete this user?')) {
                fetch(`/admin/api/member-delete-user/${btn.dataset.userid}`, {method: 'POST'})
                    .then(res => res.json())
                    .then(data => {
                        if (data.success) location.reload();
                        else alert('Failed to delete user');
                    });
            }
        });
    });
});
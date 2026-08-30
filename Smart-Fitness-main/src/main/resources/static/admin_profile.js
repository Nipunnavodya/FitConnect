// src/main/resources/static/admin_profile.js

document.addEventListener('DOMContentLoaded', function() {
    // Profile edit form toggle (if you use JS for this)
    const editBtn = document.getElementById('editProfileBtn');
    const cancelBtn = document.getElementById('cancelEditBtn');
    const viewMode = document.getElementById('viewProfileMode');
    const editMode = document.getElementById('editProfileMode');

    if (editBtn && cancelBtn && viewMode && editMode) {
        editBtn.addEventListener('click', () => {
            viewMode.classList.add('hidden');
            editMode.classList.remove('hidden');
        });
        cancelBtn.addEventListener('click', () => {
            viewMode.classList.remove('hidden');
            editMode.classList.add('hidden');
        });
    }

    // Profile update form
    document.getElementById('profileEditForm')?.addEventListener('submit', function(e) {
        // Optionally use AJAX, or let the form submit normally
        // e.preventDefault();
        // ... AJAX logic here ...
    });
});
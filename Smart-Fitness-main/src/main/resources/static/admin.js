document.addEventListener('DOMContentLoaded', function() {
    // Initialize components
    initializeModals();
    initializeSearch();
    initializeFilters();
    setupEventListeners();

    // Handle user search
    const userSearch = document.getElementById('userSearch');
    if (userSearch) {
        userSearch.addEventListener('input', debounce(function(e) {
            filterUsers(e.target.value);
        }, 300));
    }

    // Handle role filter
    const roleFilter = document.getElementById('roleFilter');
    if (roleFilter) {
        roleFilter.addEventListener('change', function(e) {
            fetch(`/admin/api/users/${e.target.value}`)
                .then(response => response.json())
                .then(users => updateUsersTable(users))
                .catch(error => showNotification('Error loading users', 'error'));
        });
    }

    // Export users functionality
    const exportBtn = document.getElementById('exportUsers');
    if (exportBtn) {
        exportBtn.addEventListener('click', exportUsers);
    }

    // Edit user form handler
    const editUserForm = document.getElementById('editUserForm');
    if (editUserForm) {
        editUserForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const userId = this.getAttribute('data-user-id');
            updateUser(userId, new FormData(this));
        });
    }

    // Modal elements
    const createAdminModal = document.getElementById('createAdminModal');
    const addAdminBtn = document.getElementById('addAdminBtn');
    const cancelAdminBtn = document.getElementById('cancelAdminBtn');
    const closeModal = document.querySelector('.close-modal');

    // Show modal
    if (addAdminBtn) {
        addAdminBtn.addEventListener('click', function() {
            createAdminModal.style.display = 'block';
        });
    }

    // Hide modal with cancel button
    if (cancelAdminBtn) {
        cancelAdminBtn.addEventListener('click', function() {
            createAdminModal.style.display = 'none';
        });
    }

    // Hide modal with X button
    if (closeModal) {
        closeModal.addEventListener('click', function() {
            createAdminModal.style.display = 'none';
        });
    }

    // Hide modal when clicking outside
    window.addEventListener('click', function(event) {
        if (event.target === createAdminModal) {
            createAdminModal.style.display = 'none';
        }
    });

    // Handle Create Admin Form
    const createAdminForm = document.getElementById('createAdminForm');
    if (createAdminForm) {
        createAdminForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const name = document.getElementById('adminName').value;
            const email = document.getElementById('adminEmail').value;
            const password = document.getElementById('adminPassword').value;

            fetch('/admin/api/create-admin', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: `name=${encodeURIComponent(name)}&email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else if (response.status === 401) {
                        window.location.href = '/login';
                        throw new Error('Not authenticated');
                    } else {
                        throw new Error('Request failed');
                    }
                })
                .then(data => {
                    if (data.success) {
                        createAdminModal.style.display = 'none';
                        createAdminForm.reset();
                        showNotification(data.message, 'success');
                        setTimeout(() => {
                            window.location.reload();
                        }, 1000);
                    } else {
                        showNotification(data.message, 'error');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    if (error.message !== 'Not authenticated') {
                        showNotification('An error occurred. Please try again.', 'error');
                    }
                });
        });
    }

    // Handle Delete User buttons
    const deleteButtons = document.querySelectorAll('.delete-user');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            const userId = this.getAttribute('data-id');
            if (confirm('Are you sure you want to delete this user?')) {
                deleteUser(userId);
            }
        });
    });

    function deleteUser(userId) {
        fetch(`/admin/api/delete-user/${userId}`, {
            method: 'POST',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else if (response.status === 401) {
                    window.location.href = '/login';
                    throw new Error('Not authenticated');
                } else {
                    throw new Error('Request failed');
                }
            })
            .then(data => {
                if (data.success) {
                    showNotification(data.message, 'success');
                    setTimeout(() => {
                        window.location.reload();
                    }, 1000);
                } else {
                    showNotification(data.message, 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                if (error.message !== 'Not authenticated') {
                    showNotification('An error occurred. Please try again.', 'error');
                }
            });
    }

    function updateUser(userId, formData) {
        fetch(`/admin/api/update-user/${userId}`, {
            method: 'POST',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: new URLSearchParams(formData)
        })
            .then(handleResponse)
            .then(data => {
                if (data.success) {
                    showNotification(data.message, 'success');
                    closeModal('editUserModal');
                    reloadUsersList();
                }
            })
            .catch(handleError);
    }

    function exportUsers() {
        const users = Array.from(document.querySelectorAll('.user-row'))
            .map(row => ({
                name: row.querySelector('.user-name').textContent,
                email: row.querySelector('.user-email').textContent,
                role: row.querySelector('.user-role').textContent,
                status: row.querySelector('.user-status').textContent
            }));

        const csv = convertToCSV(users);
        downloadCSV(csv, 'users-export.csv');
    }

    function filterUsers(searchTerm) {
        const rows = document.querySelectorAll('.user-row');
        rows.forEach(row => {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(searchTerm.toLowerCase()) ? '' : 'none';
        });
    }

    function handleResponse(response) {
        if (response.ok) return response.json();
        if (response.status === 401) {
            window.location.href = '/login';
            throw new Error('Not authenticated');
        }
        throw new Error('Request failed');
    }

    function handleError(error) {
        console.error('Error:', error);
        if (error.message !== 'Not authenticated') {
            showNotification('An error occurred. Please try again.', 'error');
        }
    }

    function debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    function convertToCSV(arr) {
        const array = [Object.keys(arr[0])].concat(arr);
        return array.map(row => {
            return Object.values(row)
                .map(String)
                .map(v => v.replaceAll('"', '""'))
                .map(v => `"${v}"`)
                .join(',');
        }).join('\n');
    }

    function downloadCSV(csv, filename) {
        const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.setAttribute('download', filename);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    // Helper function to show notification
    function showNotification(message, type) {
        let notification = document.getElementById('notification');

        if (!notification) {
            notification = document.createElement('div');
            notification.id = 'notification';
            notification.style.position = 'fixed';
            notification.style.top = '20px';
            notification.style.right = '20px';
            notification.style.padding = '1rem';
            notification.style.borderRadius = '4px';
            notification.style.zIndex = '9999';
            notification.style.maxWidth = '300px';
            notification.style.boxShadow = '0 3px 10px rgba(0, 0, 0, 0.2)';
            notification.style.transition = 'opacity 0.3s ease-in-out';
            document.body.appendChild(notification);
        }

        if (type === 'success') {
            notification.style.backgroundColor = '#d4edda';
            notification.style.color = '#155724';
            notification.style.borderLeft = '4px solid #28a745';
        } else if (type === 'error') {
            notification.style.backgroundColor = '#f8d7da';
            notification.style.color = '#721c24';
            notification.style.borderLeft = '4px solid #dc3545';
        }

        notification.textContent = message;
        notification.style.opacity = '1';

        setTimeout(() => {
            notification.style.opacity = '0';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 3000);
    }
});
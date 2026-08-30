document.addEventListener('DOMContentLoaded', function() {
    // Handle Login Form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            fetch('/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: `email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Display warning if user is not verified
                        if (data.warning) {
                            showAlert(data.warning, 'warning');
                            // Wait for 1 second before redirecting
                            setTimeout(() => {
                                window.location.href = data.redirect;
                            }, 1000);
                        } else {
                            window.location.href = data.redirect;
                        }
                    } else {
                        showAlert(data.message, 'error');
                    }
                })
                .catch(error => {
                    showAlert('An error occurred. Please try again.', 'error');
                    console.error('Error:', error);
                });
        });
    }

    // Handle Forgot Password Form
    const forgotPasswordForm = document.getElementById('forgotPasswordForm');
    if (forgotPasswordForm) {
        forgotPasswordForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const email = document.getElementById('email').value;

            fetch('/api/forgot-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: `email=${encodeURIComponent(email)}`
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showAlert(data.message, 'success');
                    } else {
                        showAlert(data.message, 'error');
                    }
                })
                .catch(error => {
                    showAlert('An error occurred. Please try again.', 'error');
                    console.error('Error:', error);
                });
        });
    }

    // Handle Reset Password Form
    const resetPasswordForm = document.getElementById('resetPasswordForm');
    if (resetPasswordForm) {
        resetPasswordForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const code = document.getElementById('code').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            // Check if passwords match
            if (password !== confirmPassword) {
                showAlert('Passwords do not match', 'error');
                return;
            }

            fetch('/api/reset-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: `code=${encodeURIComponent(code)}&password=${encodeURIComponent(password)}`
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showAlert(data.message, 'success');
                        // Wait for 2 seconds before redirecting
                        setTimeout(() => {
                            window.location.href = data.redirect;
                        }, 2000);
                    } else {
                        showAlert(data.message, 'error');
                    }
                })
                .catch(error => {
                    showAlert('An error occurred. Please try again.', 'error');
                    console.error('Error:', error);
                });
        });
    }

    // Helper function to show alerts
    function showAlert(message, type) {
        const alertElement = document.getElementById('alert');
        if (alertElement) {
            alertElement.textContent = message;
            alertElement.className = 'alert';

            if (type === 'success') {
                alertElement.classList.add('alert-success');
            } else if (type === 'error') {
                alertElement.classList.add('alert-danger');
            } else if (type === 'warning') {
                alertElement.classList.add('alert-warning');
            }

            alertElement.classList.remove('hidden');

            // Automatically hide after 5 seconds if it's a success message
            if (type === 'success') {
                setTimeout(() => {
                    alertElement.classList.add('hidden');
                }, 5000);
            }
        }
    }
});
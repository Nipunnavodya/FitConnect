document.addEventListener('DOMContentLoaded', function() {
    // Modal elements
    const addWorkoutModal = document.getElementById('addWorkoutModal');
    const addWorkoutBtn = document.getElementById('addWorkoutBtn');
    const cancelWorkoutBtn = document.getElementById('cancelWorkoutBtn');
    const closeModal = document.querySelector('.close-modal');

    // Show modal
    if (addWorkoutBtn) {
        addWorkoutBtn.addEventListener('click', function() {
            addWorkoutModal.style.display = 'block';
        });
    }

    // Hide modal with cancel button
    if (cancelWorkoutBtn) {
        cancelWorkoutBtn.addEventListener('click', function() {
            addWorkoutModal.style.display = 'none';
        });
    }

    // Hide modal with X button
    if (closeModal) {
        closeModal.addEventListener('click', function() {
            addWorkoutModal.style.display = 'none';
        });
    }

    // Hide modal when clicking outside
    window.addEventListener('click', function(event) {
        if (event.target === addWorkoutModal) {
            addWorkoutModal.style.display = 'none';
        }
    });

    // Handle Add Workout Form
    const addWorkoutForm = document.getElementById('addWorkoutForm');
    if (addWorkoutForm) {
        addWorkoutForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const workoutName = document.getElementById('workoutName').value;
            const workoutDescription = document.getElementById('workoutDescription').value;
            const workoutSchedule = document.getElementById('workoutSchedule').value;

            fetch('/api/add-workout', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: `name=${encodeURIComponent(workoutName)}&description=${encodeURIComponent(workoutDescription)}&schedule=${encodeURIComponent(workoutSchedule)}`
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else if (response.status === 401) {
                        // Handle unauthorized error (redirect to login)
                        window.location.href = '/login';
                        throw new Error('Not authenticated');
                    } else {
                        throw new Error('Request failed');
                    }
                })
                .then(data => {
                    if (data.success) {
                        // Close modal
                        addWorkoutModal.style.display = 'none';

                        // Reset form
                        addWorkoutForm.reset();

                        // Add new workout to the list without refreshing
                        addWorkoutToList(data.workoutPlan);

                        // Show success message
                        showNotification(data.message, 'success');

                        // Update stats
                        updateWorkoutStats();
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

    // Handle Complete Workout buttons
    setupCompleteWorkoutButtons();

    // Helper function to add new workout to the list
    function addWorkoutToList(workout) {
        const workoutsList = document.getElementById('workoutsList');
        const emptyState = workoutsList.querySelector('.empty-state');

        // Remove empty state if it exists
        if (emptyState) {
            emptyState.remove();
        }

        // Create new workout card
        const workoutCard = document.createElement('div');
        workoutCard.className = 'workout-card';
        workoutCard.id = `workout-${workout.id}`;

        workoutCard.innerHTML = `
            <div class="workout-header">
                <h3>${workout.name}</h3>
                <span class="status-tag pending">Pending</span>
            </div>

            <p class="workout-description">${workout.description}</p>

            <div class="workout-schedule">
                <strong>Schedule:</strong> <span>${workout.schedule}</span>
            </div>

            <div class="workout-actions">
                <button class="btn btn-success complete-workout" data-id="${workout.id}">Mark Complete</button>
            </div>
        `;

        workoutsList.appendChild(workoutCard);

        // Setup event listener for the new complete button
        setupCompleteWorkoutButtons();
    }

    // Setup event listeners for complete workout buttons
    function setupCompleteWorkoutButtons() {
        const completeButtons = document.querySelectorAll('.complete-workout');

        completeButtons.forEach(button => {
            // Only add event listener if not already added
            if (!button.hasAttribute('data-listener')) {
                button.setAttribute('data-listener', 'true');

                button.addEventListener('click', function() {
                    const workoutId = this.getAttribute('data-id');

                    fetch('/api/complete-workout', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                            'X-Requested-With': 'XMLHttpRequest'
                        },
                        body: `workoutId=${encodeURIComponent(workoutId)}`
                    })
                        .then(response => {
                            if (response.ok) {
                                return response.json();
                            } else if (response.status === 401) {
                                // Handle unauthorized error (redirect to login)
                                window.location.href = '/login';
                                throw new Error('Not authenticated');
                            } else {
                                throw new Error('Request failed');
                            }
                        })
                        .then(data => {
                            if (data.success) {
                                // Update workout card to show completed status
                                const workoutCard = document.getElementById(`workout-${workoutId}`);
                                if (workoutCard) {
                                    workoutCard.classList.add('completed');

                                    // Update status tag
                                    const statusTag = workoutCard.querySelector('.status-tag');
                                    if (statusTag) {
                                        statusTag.textContent = 'Completed';
                                        statusTag.classList.remove('pending');
                                        statusTag.classList.add('completed');
                                    }

                                    // Remove actions
                                    const actionsDiv = workoutCard.querySelector('.workout-actions');
                                    if (actionsDiv) {
                                        actionsDiv.remove();
                                    }

                                    // Show success message
                                    showNotification(data.message, 'success');

                                    // Update stats
                                    updateWorkoutStats();
                                }
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
        });
    }

    // Update workout statistics
    function updateWorkoutStats() {
        // Get all workout cards
        const workoutCards = document.querySelectorAll('.workout-card');
        const totalWorkouts = workoutCards.length;

        // Count completed workouts
        const completedWorkouts = document.querySelectorAll('.workout-card.completed').length;

        // Calculate completion rate
        const completionRate = totalWorkouts > 0 ? Math.round((completedWorkouts / totalWorkouts) * 100) : 0;

        // Update stat cards
        const totalWorkoutsElement = document.querySelector('.stat-card:nth-child(1) .stat-value');
        const completedWorkoutsElement = document.querySelector('.stat-card:nth-child(2) .stat-value');
        const completionRateElement = document.querySelector('.stat-card:nth-child(3) .stat-value');

        if (totalWorkoutsElement) totalWorkoutsElement.textContent = totalWorkouts;
        if (completedWorkoutsElement) completedWorkoutsElement.textContent = completedWorkouts;
        if (completionRateElement) completionRateElement.innerHTML = `<span>${completionRate}</span>%`;
    }

    // Helper function to show notification
    function showNotification(message, type) {
        // Create notification element if it doesn't exist
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

        // Set notification type
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

        // Hide notification after 3 seconds
        setTimeout(() => {
            notification.style.opacity = '0';

            // Remove from DOM after fade out
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 3000);
    }
});
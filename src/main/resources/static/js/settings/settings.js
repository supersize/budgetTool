// Settings Management
$(document).ready(function() {
    let currentUser = null;

    // Initialize
    init();

    function init() {
        loadUserProfile();
        initializeEventListeners();
    }

    // Initialize Event Listeners
    function initializeEventListeners() {
        // Tab Navigation
        $('.settings-nav a').click(function(e) {
            e.preventDefault();
            const tab = $(this).data('tab');
            switchTab(tab);
        });

        // Profile Form Submit
        $('#profileForm').submit(function(e) {
            e.preventDefault();
            updateProfile();
        });

        // Password Form Submit
        $('#passwordForm').submit(function(e) {
            e.preventDefault();
            changePassword();
        });
    }

    // Switch Tab
    function switchTab(tab) {
        // Update navigation
        $('.settings-nav a').removeClass('active');
        $(`.settings-nav a[data-tab="${tab}"]`).addClass('active');

        // Update content
        $('.settings-tab').hide();
        $(`#${tab}`).show();
    }

    // Load User Profile
    function loadUserProfile() {
        showLoading();

        fetch(ctxPath + 'Settings/profile', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(data => {
            hideLoading();
            if (data.success && data.data) {
                currentUser = data.data;
                populateProfileForm(currentUser);
                populateAccountInfo(currentUser);
            } else {
                console.error('Failed to load profile:', data.message);
                showAlert('Failed to load profile data', 'danger');
            }
        })
        .catch(error => {
            console.error('Error loading profile:', error);
            hideLoading();
            showAlert('Failed to load profile data', 'danger');
        });
    }

    // Populate Profile Form
    function populateProfileForm(user) {
        $('#firstName').val(user.firstName || '');
        $('#lastName').val(user.lastName || '');
        $('#email').val(user.email || '');
        $('#phoneNumber').val(user.phoneNumber || '');
        $('#dateOfBirth').val(user.dateOfBirth || '');
        $('#occupation').val(user.occupation || '');
        $('#incomeRange').val(user.incomeRange || '');
    }

    // Populate Account Info
    function populateAccountInfo(user) {
        if (user.emailVerified) {
            $('#emailVerifiedBadge').removeClass('bg-warning').addClass('bg-success').text('Verified');
        } else {
            $('#emailVerifiedBadge').removeClass('bg-success').addClass('bg-warning').text('Not Verified');
        }

        if (user.createdAt) {
            const createdDate = new Date(user.createdAt);
            $('#memberSince').text(createdDate.toLocaleDateString('en-GB', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            }));
        }
    }

    // Update Profile
    function updateProfile() {
        const $button = $('#saveProfileBtn');
        
        // Validate form
        if (!$('#profileForm')[0].checkValidity()) {
            $('#profileForm')[0].reportValidity();
            return;
        }

        const updateData = {
            firstName: $('#firstName').val(),
            lastName: $('#lastName').val(),
            phoneNumber: $('#phoneNumber').val() || null,
            dateOfBirth: $('#dateOfBirth').val() || null,
            occupation: $('#occupation').val() || null,
            incomeRange: $('#incomeRange').val() || null
        };

        setButtonLoading($button, true);

        fetch(ctxPath + 'Settings/profile', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updateData)
        })
        .then(response => response.json())
        .then(data => {
            setButtonLoading($button, false);
            if (data.success) {
                currentUser = data.data;
                showAlert('Profile updated successfully!', 'success');
                
                // Update user name in header if changed
                if (data.data.fullName) {
                    $('.user-name').text(data.data.fullName);
                    $('.account-name').text(data.data.fullName);
                }
            } else {
                showAlert(data.message || 'Failed to update profile', 'danger');
            }
        })
        .catch(error => {
            console.error('Error updating profile:', error);
            setButtonLoading($button, false);
            showAlert('Failed to update profile. Please try again.', 'danger');
        });
    }

    // Change Password
    function changePassword() {
        const $button = $('#changePasswordBtn');
        
        // Validate form
        if (!$('#passwordForm')[0].checkValidity()) {
            $('#passwordForm')[0].reportValidity();
            return;
        }

        const currentPassword = $('#currentPassword').val();
        const newPassword = $('#newPassword').val();
        const confirmPassword = $('#confirmPassword').val();

        // Validate passwords match
        if (newPassword !== confirmPassword) {
            showAlert('New passwords do not match', 'warning');
            return;
        }

        // Validate password length
        if (newPassword.length < 8) {
            showAlert('Password must be at least 8 characters long', 'warning');
            return;
        }

        const passwordData = {
            currentPassword: currentPassword,
            newPassword: newPassword,
            confirmPassword: confirmPassword
        };

        setButtonLoading($button, true);

        fetch(ctxPath + 'Settings/password', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(passwordData)
        })
        .then(response => response.json())
        .then(data => {
            setButtonLoading($button, false);
            if (data.success) {
                showAlert('Password changed successfully!', 'success');
                $('#passwordForm')[0].reset();
            } else {
                showAlert(data.message || 'Failed to change password', 'danger');
            }
        })
        .catch(error => {
            console.error('Error changing password:', error);
            setButtonLoading($button, false);
            showAlert('Failed to change password. Please try again.', 'danger');
        });
    }

    // Utility Functions
    function showLoading() {
        $('#loadingState').show();
        $('#settingsContent').hide();
    }

    function hideLoading() {
        $('#loadingState').hide();
        $('#settingsContent').show();
    }

    function setButtonLoading(button, isLoading) {
        const $button = $(button);
        if (isLoading) {
            $button.prop('disabled', true);
            $button.data('original-text', $button.html());
            $button.html('<span class="spinner-border spinner-border-sm me-2"></span>Loading...');
        } else {
            $button.prop('disabled', false);
            const originalText = $button.data('original-text');
            if (originalText) {
                $button.html(originalText);
            }
        }
    }
});

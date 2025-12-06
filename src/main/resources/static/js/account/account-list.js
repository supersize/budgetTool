// Account List Management
$(document).ready(function() {
    let accounts = [];
    let filteredAccounts = [];

    // Initialize
    init();

    function init() {
        loadAccounts();
        initializeEventListeners();
    }

    // Initialize Event Listeners
    function initializeEventListeners() {
        // Search and Filter
        $('#searchInput').on('input', filterAccounts);
        $('#accountTypeFilter').on('change', filterAccounts);
        $('#statusFilter').on('change', filterAccounts);
        $('#resetFilterBtn').click(resetFilters);

        // Add Account
        $('#saveAccountBtn').click(saveAccount);

        // Edit Account
        $('#updateAccountBtn').click(updateAccount);

        // Delete Account
        $('#confirmDeleteBtn').click(deleteAccount);

        // Modal close events - reset forms
        $('#addAccountModal').on('hidden.bs.modal', function() {
            $('#addAccountForm')[0].reset();
        });

        $('#editAccountModal').on('hidden.bs.modal', function() {
            $('#editAccountForm')[0].reset();
        });
    }

    // Load Accounts from API
    function loadAccounts() {
        showLoading();

        fetch(ctxPath + 'Accounts', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                accounts = data.data || [];
                filteredAccounts = [...accounts];
                renderAccounts();
                updateSummary();
            } else {
                console.error('Failed to load accounts:', data.message);
                accounts = [];
                filteredAccounts = [];
                renderAccounts();
                updateSummary();
            }
        })
        .catch(error => {
            console.error('Error loading accounts:', error);
            // Show empty state when API is not ready
            accounts = [];
            filteredAccounts = [];
            renderAccounts();
            updateSummary();
        });
    }

    // Render Accounts
    function renderAccounts() {
        const $tbody = $('#accountTableBody');
        const $loadingState = $('#loadingState');
        const $emptyState = $('#emptyState');
        const $accountTable = $('#accountTable');

        hideLoading();

        if (filteredAccounts.length === 0) {
            $accountTable.hide();
            $emptyState.show();
            $('#accountCount').text('0 accounts');
            return;
        }

        $emptyState.hide();
        $accountTable.show();
        $('#accountCount').text(`${filteredAccounts.length} account${filteredAccounts.length > 1 ? 's' : ''}`);

        const rows = filteredAccounts.map(account => {
            return `
                <tr class="account-row" data-account-id="${account.id}">
                    <td>
                        <div class="d-flex align-items-center">
                            <i class="bi bi-bank me-2 text-primary"></i>
                            <strong>${escapeHtml(account.bankName)}</strong>
                        </div>
                    </td>
                    <td>
                        <code>${escapeHtml(account.accountNumber)}</code>
                    </td>
                    <td>
                        <span class="account-type-badge ${account.accountType.toLowerCase()}">
                            ${formatAccountType(account.accountType)}
                        </span>
                    </td>
                    <td>
                        <span class="currency-symbol">${escapeHtml(account.currency)}</span>
                    </td>
                    <td>
                        <span class="balance-amount ${account.balance >= 0 ? 'balance-positive' : 'balance-negative'}">
                            ${formatCurrency(account.balance, account.currency)}
                        </span>
                    </td>
                    <td>
                        <span class="status-badge ${account.isActive ? 'active' : 'inactive'}">
                            <i class="bi bi-circle-fill"></i>
                            ${account.isActive ? 'Active' : 'Inactive'}
                        </span>
                    </td>
                    <td>
                        <small class="text-muted">${formatDate(account.createdAt)}</small>
                    </td>
                    <td>
                        <div class="action-buttons">
                            <button class="action-btn edit" data-account-id="${account.id}" title="Edit">
                                <i class="bi bi-pencil"></i>
                            </button>
                            <button class="action-btn delete" data-account-id="${account.id}" title="Delete">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');

        $tbody.html(rows);

        // Bind event handlers for dynamically created buttons
        $('.action-btn.edit').click(function() {
            const accountId = $(this).data('account-id');
            openEditModal(accountId);
        });

        $('.action-btn.delete').click(function() {
            const accountId = $(this).data('account-id');
            openDeleteModal(accountId);
        });
    }

    // Update Summary Cards
    function updateSummary() {
        const activeAccounts = accounts.filter(acc => acc.isActive);
        const totalBalance = activeAccounts.reduce((sum, acc) => {
            return sum + parseFloat(acc.balance);
        }, 0);
        const uniqueBanks = new Set(activeAccounts.map(acc => acc.bankName));

        $('#totalBalance').text(`£${totalBalance.toFixed(2)}`);
        $('#activeAccounts').text(activeAccounts.length);
        $('#banksCount').text(uniqueBanks.size);
    }

    // Filter Accounts
    function filterAccounts() {
        const searchTerm = $('#searchInput').val().toLowerCase();
        const accountType = $('#accountTypeFilter').val();
        const status = $('#statusFilter').val();

        filteredAccounts = accounts.filter(account => {
            const matchesSearch = account.bankName.toLowerCase().includes(searchTerm) ||
                                account.accountNumber.toLowerCase().includes(searchTerm);
            const matchesType = !accountType || account.accountType === accountType;
            const matchesStatus = !status || account.isActive.toString() === status;

            return matchesSearch && matchesType && matchesStatus;
        });

        renderAccounts();
    }

    // Reset Filters
    function resetFilters() {
        $('#searchInput').val('');
        $('#accountTypeFilter').val('');
        $('#statusFilter').val('');
        filterAccounts();
    }

    // Save New Account
    function saveAccount() {
        const $form = $('#addAccountForm');
        
        if (!$form[0].checkValidity()) {
            $form[0].reportValidity();
            return;
        }

        const formData = {
            bankName: $('#bankName').val(),
            accountNumber: $('#accountNumber').val(),
            accountType: $('#accountType').val(),
            currency: $('#currency').val(),
            initialBalance: parseFloat($('#initialBalance').val()) || 0
        };

        const $button = $('#saveAccountBtn');
        setButtonLoading($button, true);

        fetch(ctxPath + 'Accounts', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Close modal
                $('#addAccountModal').modal('hide');
                
                // Reload accounts
                loadAccounts();
                
                showAlert('Account created successfully!', 'success');
            } else {
                showAlert(data.message || 'Failed to create account', 'danger');
            }
        })
        .catch(error => {
            console.error('Error creating account:', error);
            showAlert('Failed to create account. Please try again.', 'danger');
        })
        .finally(() => {
            setButtonLoading($button, false);
        });
    }

    // Open Edit Modal
    function openEditModal(accountId) {
        const account = accounts.find(acc => acc.id === accountId);
        if (!account) return;

        $('#editAccountId').val(account.id);
        $('#editBankName').val(account.bankName);
        $('#editAccountNumber').val(account.accountNumber);
        $('#editAccountType').val(account.accountType);
        $('#editCurrency').val(account.currency);
        $('#editBalance').val(formatCurrency(account.balance, account.currency));
        $('#editIsActive').prop('checked', account.isActive);

        $('#editAccountModal').modal('show');
    }

    // Update Account
    function updateAccount() {
        const $form = $('#editAccountForm');
        
        if (!$form[0].checkValidity()) {
            $form[0].reportValidity();
            return;
        }

        const accountId = $('#editAccountId').val();
        const formData = {
            bankName: $('#editBankName').val(),
            accountNumber: $('#editAccountNumber').val(),
            accountType: $('#editAccountType').val(),
            currency: $('#editCurrency').val(),
            isActive: $('#editIsActive').is(':checked')
        };

        const $button = $('#updateAccountBtn');
        setButtonLoading($button, true);

        fetch(ctxPath + `Accounts/${accountId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Close modal
                $('#editAccountModal').modal('hide');
                
                // Reload accounts
                loadAccounts();
                
                showAlert('Account updated successfully!', 'success');
            } else {
                showAlert(data.message || 'Failed to update account', 'danger');
            }
        })
        .catch(error => {
            console.error('Error updating account:', error);
            showAlert('Failed to update account. Please try again.', 'danger');
        })
        .finally(() => {
            setButtonLoading($button, false);
        });
    }

    // Open Delete Modal
    function openDeleteModal(accountId) {
        $('#deleteAccountId').val(accountId);
        $('#deleteAccountModal').modal('show');
    }

    // Delete Account
    function deleteAccount() {
        const accountId = $('#deleteAccountId').val();
        const $button = $('#confirmDeleteBtn');
        setButtonLoading($button, true);

        fetch(ctxPath + `Accounts/${accountId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Close modal
                $('#deleteAccountModal').modal('hide');
                
                // Reload accounts
                loadAccounts();
                
                showAlert('Account deleted successfully!', 'success');
            } else {
                showAlert(data.message || 'Failed to delete account', 'danger');
            }
        })
        .catch(error => {
            console.error('Error deleting account:', error);
            showAlert('Failed to delete account. Please try again.', 'danger');
        })
        .finally(() => {
            setButtonLoading($button, false);
        });
    }

    // Utility Functions
    function showLoading() {
        $('#loadingState').css('display', 'flex');
        $('#emptyState').hide();
        $('#accountTable').hide();
    }

    function hideLoading() {
        $('#loadingState').hide();
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

    function formatCurrency(amount, currency) {
        const symbols = {
            'GBP': '£',
            'USD': '$',
            'EUR': '€',
            'KRW': '₩'
        };
        const symbol = symbols[currency] || currency;
        return `${symbol}${parseFloat(amount).toFixed(2)}`;
    }

    function formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-GB', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    }

    function formatAccountType(type) {
        const types = {
            'SAVINGS': 'Savings',
            'CHECKING': 'Checking',
            'BUSINESS': 'Business'
        };
        return types[type] || type;
    }

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    function showAlert(message, type) {
        // Temporary - replace with toast notification
        alert(message);
    }
});

// Transaction History Management
$(document).ready(function() {
    let transactions = [];
    let filteredTransactions = [];
    let accounts = [];

    // Initialize
    init();

    function init() {
        loadAccounts();
        loadTransactions();
        initializeEventListeners();
    }

    // Initialize Event Listeners
    function initializeEventListeners() {
        // Search and Filter
        $('#searchInput').on('input', filterTransactions);
        $('#accountFilter').on('change', filterTransactions);
        $('#typeFilter').on('change', filterTransactions);
        $('#statusFilter').on('change', filterTransactions);
        $('#dateRangeFilter').on('change', handleDateRangeChange);
        $('#startDate, #endDate').on('change', filterTransactions);
        $('#resetFilterBtn').click(resetFilters);

        // Export
        $('#exportBtn').click(exportTransactions);
    }

    // Load Accounts for Filter
    function loadAccounts() {
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
                populateAccountFilter();
            }
        })
        .catch(error => {
            console.error('Error loading accounts:', error);
        });
    }

    // Populate Account Filter Dropdown
    function populateAccountFilter() {
        const $select = $('#accountFilter');
        $select.empty();
        $select.append('<option value="">All Accounts</option>');
        
        accounts.forEach(account => {
            $select.append(`
                <option value="${account.id}">
                    ${escapeHtml(account.bankName)} - ${escapeHtml(account.accountNumber)}
                </option>
            `);
        });
    }

    // Load Transactions from API
    function loadTransactions() {
        showLoading();

        fetch(ctxPath + 'Transactions', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                transactions = data.data || [];
                filteredTransactions = [...transactions];
                renderTransactions();
                updateSummary();
            } else {
                console.error('Failed to load transactions:', data.message);
                transactions = [];
                filteredTransactions = [];
                renderTransactions();
                updateSummary();
            }
        })
        .catch(error => {
            console.error('Error loading transactions:', error);
            transactions = [];
            filteredTransactions = [];
            renderTransactions();
            updateSummary();
        });
    }

    // Render Transactions
    function renderTransactions() {
        const $tbody = $('#transactionTableBody');
        const $emptyState = $('#emptyState');
        const $transactionTable = $('#transactionTable');

        hideLoading();

        if (filteredTransactions.length === 0) {
            $transactionTable.hide();
            $emptyState.show();
            $('#transactionCount').text('0 transactions');
            return;
        }

        $emptyState.hide();
        $transactionTable.show();
        $('#transactionCount').text(`${filteredTransactions.length} transaction${filteredTransactions.length > 1 ? 's' : ''}`);

        const rows = filteredTransactions.map(transaction => {
            const isIncome = transaction.transactionType === 'DEPOSIT' || transaction.transactionType === 'TRANSFER_IN';
            const amountClass = isIncome ? 'amount-positive' : 'amount-negative';
            const amountSign = isIncome ? '+' : '-';

            return `
                <tr class="transaction-row" data-transaction-id="${transaction.id}">
                    <td>
                        <div><strong>${formatDateTime(transaction.createdAt)}</strong></div>
                        <small class="text-muted">${formatTime(transaction.createdAt)}</small>
                    </td>
                    <td>
                        <span class="transaction-type-badge ${transaction.transactionType.toLowerCase()}">
                            ${getTypeIcon(transaction.transactionType)}
                            ${formatTransactionType(transaction.transactionType)}
                        </span>
                    </td>
                    <td>
                        <div><strong>${escapeHtml(transaction.bankName)}</strong></div>
                        <small class="text-muted">${escapeHtml(transaction.accountNumber)}</small>
                    </td>
                    <td>${escapeHtml(transaction.description || '-')}</td>
                    <td><code>${escapeHtml(transaction.referenceNumber)}</code></td>
                    <td class="text-end">
                        <span class="${amountClass}">
                            ${amountSign}${formatCurrency(transaction.amount, transaction.currency)}
                        </span>
                    </td>
                    <td>
                        <span class="status-badge ${transaction.status.toLowerCase()}">
                            <i class="bi bi-circle-fill"></i>
                            ${formatStatus(transaction.status)}
                        </span>
                    </td>
                    <td class="text-end">
                        <strong>${formatCurrency(transaction.balanceAfter, transaction.currency)}</strong>
                    </td>
                    <td>
                        <div class="action-buttons">
                            <button class="action-btn view" data-transaction-id="${transaction.id}" title="View Details">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');

        $tbody.html(rows);

        // Bind event handlers
        $('.action-btn.view').click(function() {
            const transactionId = $(this).data('transaction-id');
            showTransactionDetail(transactionId);
        });
    }

    // Update Summary Cards
    function updateSummary() {
        const totalCount = filteredTransactions.length;
        
        const income = filteredTransactions
            .filter(t => t.transactionType === 'DEPOSIT' || t.transactionType === 'TRANSFER_IN')
            .reduce((sum, t) => sum + parseFloat(t.amount), 0);
        
        const expense = filteredTransactions
            .filter(t => t.transactionType === 'WITHDRAWAL' || t.transactionType === 'TRANSFER_OUT')
            .reduce((sum, t) => sum + parseFloat(t.amount), 0);
        
        const net = income - expense;

        $('#totalTransactions').text(totalCount.toLocaleString());
        $('#totalIncome').text(`£${income.toFixed(2)}`);
        $('#totalExpense').text(`£${expense.toFixed(2)}`);
        
        const $netAmount = $('#netAmount');
        $netAmount.text(`£${Math.abs(net).toFixed(2)}`);
        $netAmount.removeClass('text-success text-danger');
        if (net > 0) {
            $netAmount.addClass('text-success');
        } else if (net < 0) {
            $netAmount.addClass('text-danger');
        }
    }

    // Filter Transactions
    function filterTransactions() {
        const searchTerm = $('#searchInput').val().toLowerCase();
        const accountId = $('#accountFilter').val();
        const type = $('#typeFilter').val();
        const status = $('#statusFilter').val();
        const dateRange = $('#dateRangeFilter').val();

        filteredTransactions = transactions.filter(transaction => {
            const matchesSearch = 
                (transaction.description && transaction.description.toLowerCase().includes(searchTerm)) ||
                transaction.referenceNumber.toLowerCase().includes(searchTerm) ||
                (transaction.toAccountHolderName && transaction.toAccountHolderName.toLowerCase().includes(searchTerm));
            
            const matchesAccount = !accountId || transaction.accountId.toString() === accountId;
            const matchesType = !type || transaction.transactionType === type;
            const matchesStatus = !status || transaction.status === status;
            const matchesDate = checkDateRange(transaction.createdAt, dateRange);

            return matchesSearch && matchesAccount && matchesType && matchesStatus && matchesDate;
        });

        renderTransactions();
        updateSummary();
    }

    // Check Date Range
    function checkDateRange(dateString, range) {
        const date = new Date(dateString);
        const now = new Date();

        switch(range) {
            case 'all':
                return true;
            case 'today':
                return date.toDateString() === now.toDateString();
            case 'week':
                const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
                return date >= weekAgo;
            case 'month':
                const monthAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
                return date >= monthAgo;
            case 'year':
                const yearAgo = new Date(now.getTime() - 365 * 24 * 60 * 60 * 1000);
                return date >= yearAgo;
            case 'custom':
                const startDate = $('#startDate').val();
                const endDate = $('#endDate').val();
                if (startDate && endDate) {
                    const start = new Date(startDate);
                    const end = new Date(endDate);
                    end.setHours(23, 59, 59, 999);
                    return date >= start && date <= end;
                }
                return true;
            default:
                return true;
        }
    }

    // Handle Date Range Change
    function handleDateRangeChange() {
        const value = $(this).val();
        if (value === 'custom') {
            $('#customDateRange, #customDateRange2').show();
        } else {
            $('#customDateRange, #customDateRange2').hide();
            filterTransactions();
        }
    }

    // Reset Filters
    function resetFilters() {
        $('#searchInput').val('');
        $('#accountFilter').val('');
        $('#typeFilter').val('');
        $('#statusFilter').val('');
        $('#dateRangeFilter').val('week');
        $('#customDateRange, #customDateRange2').hide();
        filterTransactions();
    }

    // Show Transaction Detail
    function showTransactionDetail(transactionId) {
        const transaction = transactions.find(t => t.id === transactionId);
        if (!transaction) return;

        const isTransfer = transaction.transactionType === 'TRANSFER_OUT' || transaction.transactionType === 'TRANSFER_IN';
        const isIncome = transaction.transactionType === 'DEPOSIT' || transaction.transactionType === 'TRANSFER_IN';

        let detailHtml = `
            <div class="detail-row">
                <span class="detail-label">Transaction ID:</span>
                <span class="detail-value">${transaction.id}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Reference Number:</span>
                <span class="detail-value"><code>${escapeHtml(transaction.referenceNumber)}</code></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Type:</span>
                <span class="detail-value">
                    <span class="transaction-type-badge ${transaction.transactionType.toLowerCase()}">
                        ${getTypeIcon(transaction.transactionType)}
                        ${formatTransactionType(transaction.transactionType)}
                    </span>
                </span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Account:</span>
                <span class="detail-value">${escapeHtml(transaction.bankName)} - ${escapeHtml(transaction.accountNumber)}</span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Amount:</span>
                <span class="detail-value ${isIncome ? 'amount-positive' : 'amount-negative'}">
                    ${isIncome ? '+' : '-'}${formatCurrency(transaction.amount, transaction.currency)}
                </span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Status:</span>
                <span class="detail-value">
                    <span class="status-badge ${transaction.status.toLowerCase()}">
                        <i class="bi bi-circle-fill"></i>
                        ${formatStatus(transaction.status)}
                    </span>
                </span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Balance After:</span>
                <span class="detail-value"><strong>${formatCurrency(transaction.balanceAfter, transaction.currency)}</strong></span>
            </div>
        `;

        if (isTransfer) {
            if (transaction.toAccountNumber) {
                detailHtml += `
                    <div class="detail-row">
                        <span class="detail-label">To Account:</span>
                        <span class="detail-value">${escapeHtml(transaction.toAccountNumber)}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">To Holder:</span>
                        <span class="detail-value">${escapeHtml(transaction.toAccountHolderName || '-')}</span>
                    </div>
                `;
            }
            if (transaction.transferMessage) {
                detailHtml += `
                    <div class="detail-row">
                        <span class="detail-label">Message:</span>
                        <span class="detail-value">${escapeHtml(transaction.transferMessage)}</span>
                    </div>
                `;
            }
        }

        if (transaction.description) {
            detailHtml += `
                <div class="detail-row">
                    <span class="detail-label">Description:</span>
                    <span class="detail-value">${escapeHtml(transaction.description)}</span>
                </div>
            `;
        }

        detailHtml += `
            <div class="detail-row">
                <span class="detail-label">Created At:</span>
                <span class="detail-value">${formatDateTime(transaction.createdAt)} ${formatTime(transaction.createdAt)}</span>
            </div>
        `;

        if (transaction.processedAt) {
            detailHtml += `
                <div class="detail-row">
                    <span class="detail-label">Processed At:</span>
                    <span class="detail-value">${formatDateTime(transaction.processedAt)} ${formatTime(transaction.processedAt)}</span>
                </div>
            `;
        }

        $('#transactionDetailBody').html(detailHtml);
        $('#transactionDetailModal').modal('show');
    }

    // Export Transactions
    function exportTransactions() {
        // Simple CSV export
        const csv = generateCSV();
        downloadCSV(csv, `transactions_${new Date().getTime()}.csv`);
        showAlert('Transactions exported successfully!', 'success');
    }

    // Generate CSV
    function generateCSV() {
        const headers = ['Date', 'Type', 'Account', 'Description', 'Reference', 'Amount', 'Status', 'Balance After'];
        const rows = filteredTransactions.map(t => {
            const isIncome = t.transactionType === 'DEPOSIT' || t.transactionType === 'TRANSFER_IN';
            const amount = `${isIncome ? '+' : '-'}${t.amount}`;
            return [
                formatDateTime(t.createdAt),
                formatTransactionType(t.transactionType),
                `${t.bankName} - ${t.accountNumber}`,
                t.description || '',
                t.referenceNumber,
                amount,
                formatStatus(t.status),
                t.balanceAfter
            ].map(field => `"${field}"`).join(',');
        });

        return [headers.join(','), ...rows].join('\n');
    }

    // Download CSV
    function downloadCSV(csv, filename) {
        const blob = new Blob([csv], { type: 'text/csv' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.click();
        window.URL.revokeObjectURL(url);
    }

    // Utility Functions
    function showLoading() {
        $('#loadingState').css('display', 'flex');
        $('#emptyState').hide();
        $('#transactionTable').hide();
    }

    function hideLoading() {
        $('#loadingState').hide();
    }

    function getTypeIcon(type) {
        const icons = {
            'DEPOSIT': '<i class="bi bi-arrow-down-circle"></i>',
            'WITHDRAWAL': '<i class="bi bi-arrow-up-circle"></i>',
            'TRANSFER_OUT': '<i class="bi bi-arrow-right-circle"></i>',
            'TRANSFER_IN': '<i class="bi bi-arrow-left-circle"></i>'
        };
        return icons[type] || '';
    }

    function formatTransactionType(type) {
        const types = {
            'DEPOSIT': 'Deposit',
            'WITHDRAWAL': 'Withdrawal',
            'TRANSFER_OUT': 'Transfer Out',
            'TRANSFER_IN': 'Transfer In'
        };
        return types[type] || type;
    }

    function formatStatus(status) {
        const statuses = {
            'COMPLETED': 'Completed',
            'PENDING': 'Pending',
            'FAILED': 'Failed',
            'CANCELLED': 'Cancelled'
        };
        return statuses[status] || status;
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

    function formatDateTime(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-GB', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    }

    function formatTime(dateString) {
        const date = new Date(dateString);
        return date.toLocaleTimeString('en-GB', {
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
});

$(document).ready(function() {
    // Load dashboard data
    loadDashboardData();

    // Account card click events
    $('.account-item').click(function() {
        const accountId = $(this).data('account-id');
        if (accountId) {
            window.location.href = `/accounts/${accountId}`;
        }
    });

    // Transaction item click events
    $('.transaction-item').click(function() {
        const transactionId = $(this).data('transaction-id');
        if (transactionId) {
            // Show transaction details modal or navigate to page
            showTransactionDetails(transactionId);
        }
    });

    // Chart animations
    animateCharts();

    // Real-time data updates (every 30 seconds)
    setInterval(updateRealTimeData, 30000);

    // Add savings goal button
    $('.goal-actions .btn').click(function() {
        showAddGoalModal();
    });

    // Refresh data when page becomes visible
    $(document).on('visibilitychange', function() {
        if (!document.hidden) {
            loadDashboardData();
        }
    });
});

// Load dashboard data
function loadDashboardData() {
    // In actual implementation, fetch data from server
    /*
    $.ajax({
        url: '/api/dashboard/data',
        method: 'GET',
        success: function(data) {
            updateAccountSummary(data.accounts);
            updateRecentTransactions(data.transactions);
            updateSpendingAnalysis(data.spending);
            updateSavingsGoals(data.goals);
        },
        error: function() {
            showAlert('Failed to load dashboard data.', 'danger');
        }
    });
    */

    // Temporary data for UI update
    const mockData = {
        accounts: [
            {
                id: 1,
                bank: 'DBS Bank',
                accountNumber: '***1234',
                balance: 15000,
                currency: 'GBP',
                percentage: 70
            },
            {
                id: 2,
                bank: 'Barclays',
                accountNumber: '***5678',
                balance: 8500,
                currency: 'GBP',
                percentage: 40
            }
        ],
        totalBalance: 23500,
        monthlyChange: 1500,
        currency: 'GBP',
        transactions: [
            {
                id: 1,
                type: 'deposit',
                title: 'Salary Deposit',
                account: 'DBS Bank ***1234',
                amount: 5000,
                currency: 'GBP',
                date: '2024-01-15 09:30'
            },
            {
                id: 2,
                type: 'transfer',
                title: 'Transfer to Sarah Wilson',
                account: 'Barclays ***5678',
                amount: -200,
                currency: 'GBP',
                date: '2024-01-14 14:20'
            }
        ]
    };

    setTimeout(() => {
        updateDashboardUI(mockData);
    }, 500);
}

// Update dashboard UI
function updateDashboardUI(data) {
    // Update total balance
    $('.balance-amount').text(`£${formatCurrency(data.totalBalance)}`);

    // Update monthly change
    const changeElement = $('.balance-change');
    if (data.monthlyChange >= 0) {
        changeElement.removeClass('text-danger').addClass('text-success');
        changeElement.html(`<i class="bi bi-arrow-up"></i> +£${formatCurrency(data.monthlyChange)} (this month)`);
    } else {
        changeElement.removeClass('text-success').addClass('text-danger');
        changeElement.html(`<i class="bi bi-arrow-down"></i> -£${formatCurrency(Math.abs(data.monthlyChange))} (this month)`);
    }

    // Update account information
    updateAccountCards(data.accounts);

    // Re-run chart animations
    animateCharts();
}

// Update account cards
function updateAccountCards(accounts) {
    const accountsContainer = $('.accounts-grid');
    accountsContainer.empty();

    accounts.forEach(account => {
        const currencySymbol = account.currency === 'GBP' ? '£' : 'S$';
        const accountCard = `
            <div class="account-item" data-account-id="${account.id}">
                <div class="account-info">
                    <div class="account-bank">${account.bank}</div>
                    <div class="account-number">${account.accountNumber}</div>
                    <div class="account-balance">${currencySymbol}${formatCurrency(account.balance)}</div>
                </div>
                <div class="account-chart">
                    <div class="chart-bar" data-height="${account.percentage}%"></div>
                </div>
            </div>
        `;
        accountsContainer.append(accountCard);
    });

    // Add click events to new account cards
    $('.account-item').click(function() {
        const accountId = $(this).data('account-id');
        if (accountId) {
            window.location.href = `/accounts/${accountId}`;
        }
    });
}

// Animate charts
function animateCharts() {
    $('.chart-bar').each(function(index) {
        const $bar = $(this);
        const targetHeight = $bar.data('height') || $bar.attr('style')?.match(/height:\s*(\d+)%/)?.[1] + '%' || '50%';

        // Animation delay
        setTimeout(() => {
            $bar.css('height', '0%');
            setTimeout(() => {
                $bar.css({
                    'height': targetHeight,
                    'transition': 'height 1s cubic-bezier(0.25, 0.46, 0.45, 0.94)'
                });
            }, 100);
        }, index * 200);
    });

    // Progress bar animation
    $('.progress-bar').each(function(index) {
        const $progressBar = $(this);
        const targetWidth = $progressBar.attr('style')?.match(/width:\s*(\d+)%/)?.[1] + '%' || '0%';

        setTimeout(() => {
            $progressBar.css('width', '0%');
            setTimeout(() => {
                $progressBar.css({
                    'width': targetWidth,
                    'transition': 'width 1.5s cubic-bezier(0.25, 0.46, 0.45, 0.94)'
                });
            }, 200);
        }, index * 300);
    });
}

// Update real-time data
function updateRealTimeData() {
    // Update notification count
    /*
    $.ajax({
        url: '/api/notifications/count',
        method: 'GET',
        success: function(data) {
            const badge = $('.notification-badge');
            if (data.count > 0) {
                badge.text(data.count).show();
            } else {
                badge.hide();
            }
        }
    });
    */
}

// Show transaction details
function showTransactionDetails(transactionId) {
    // In actual implementation, fetch detailed data from server
    /*
    $.ajax({
        url: `/api/transactions/${transactionId}`,
        method: 'GET',
        success: function(data) {
            displayTransactionModal(data);
        },
        error: function() {
            showAlert('Failed to load transaction details.', 'danger');
        }
    });
    */

    // Temporary modal display
    showAlert('Transaction details feature is coming soon.', 'info');
}

// Show add savings goal modal
function showAddGoalModal() {
    const modalHtml = `
        <div class="modal fade" id="addGoalModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">
                            <i class="bi bi-target me-2"></i>Add New Savings Goal
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="addGoalForm">
                            <div class="mb-3">
                                <label for="goalTitle" class="form-label">Goal Name</label>
                                <input type="text" class="form-control" id="goalTitle" 
                                       placeholder="e.g. New Car, Wedding Fund, etc." required>
                            </div>
                            <div class="mb-3">
                                <label for="goalAmount" class="form-label">Target Amount</label>
                                <div class="input-group">
                                    <input type="number" class="form-control" id="goalAmount" 
                                           placeholder="0" required>
                                    <select class="form-select" style="max-width: 80px;">
                                        <option value="GBP">£</option>
                                        <option value="SGD">S$</option>
                                    </select>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="goalDeadline" class="form-label">Target Date</label>
                                <input type="date" class="form-control" id="goalDeadline" required>
                            </div>
                            <div class="mb-3">
                                <label for="goalDescription" class="form-label">Description (Optional)</label>
                                <textarea class="form-control" id="goalDescription" rows="3" 
                                         placeholder="Enter a brief description of your goal"></textarea>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-success" id="confirmAddGoal">Add Goal</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    // Remove existing modal and add new one
    $('#addGoalModal').remove();
    $('body').append(modalHtml);

    // Show modal
    $('#addGoalModal').modal('show');

    // Add goal confirmation button event
    $('#confirmAddGoal').click(function() {
        const title = $('#goalTitle').val();
        const amount = $('#goalAmount').val();
        const deadline = $('#goalDeadline').val();
        const description = $('#goalDescription').val();

        if (!title || !amount || !deadline) {
            showAlert('Please fill in all required fields.', 'warning');
            return;
        }

        if (amount <= 0) {
            showAlert('Please enter a valid target amount.', 'warning');
            return;
        }

        showLoading($(this));

        // In actual implementation, send goal addition request to server
        setTimeout(() => {
            hideLoading($(this));
            $('#addGoalModal').modal('hide');
            showAlert('New savings goal has been added!', 'success');

            // Refresh goals list
            loadSavingsGoals();
        }, 1500);
    });

    // Modal cleanup
    $('#addGoalModal').on('hidden.bs.modal', function() {
        $(this).remove();
    });
}

// Load savings goals
function loadSavingsGoals() {
    // In actual implementation, fetch goal data from server
    /*
    $.ajax({
        url: '/api/savings-goals',
        method: 'GET',
        success: function(data) {
            updateSavingsGoalsUI(data);
        },
        error: function() {
            showAlert('Failed to load savings goals.', 'danger');
        }
    });
    */

    showAlert('Savings goals have been updated.', 'success', 2000);
}

// Enhance card interactions
function enhanceCardInteractions() {
    // Account card hover effects
    $('.account-item').hover(
        function() {
            $(this).find('.chart-bar').css('transform', 'scaleY(1.1)');
        },
        function() {
            $(this).find('.chart-bar').css('transform', 'scaleY(1)');
        }
    );

    // Quick action button hover effects
    $('.quick-action-btn').hover(
        function() {
            $(this).find('.action-icon').css('transform', 'scale(1.1) rotate(5deg)');
        },
        function() {
            $(this).find('.action-icon').css('transform', 'scale(1) rotate(0deg)');
        }
    );

    // Transaction hover effects
    $('.transaction-item').hover(
        function() {
            $(this).find('.transaction-icon').css('transform', 'scale(1.1)');
        },
        function() {
            $(this).find('.transaction-icon').css('transform', 'scale(1)');
        }
    );
}

// Animate numbers
function animateNumbers() {
    $('.balance-amount, .stat-value').each(function() {
        const $this = $(this);
        const text = $this.text();
        const numMatch = text.match(/[\d,]+/);

        if (numMatch) {
            const targetNum = parseInt(numMatch[0].replace(/,/g, ''));
            const prefix = text.substring(0, text.indexOf(numMatch[0]));
            const suffix = text.substring(text.indexOf(numMatch[0]) + numMatch[0].length);

            let currentNum = 0;
            const increment = targetNum / 50;
            const duration = 20;

            const timer = setInterval(() => {
                currentNum += increment;
                if (currentNum >= targetNum) {
                    currentNum = targetNum;
                    clearInterval(timer);
                }

                $this.text(prefix + formatCurrency(Math.floor(currentNum)) + suffix);
            }, duration);
        }
    });
}

// Run animations after page load
$(window).on('load', function() {
    setTimeout(() => {
        animateNumbers();
        enhanceCardInteractions();
    }, 800);
});

// Keyboard shortcuts
$(document).keydown(function(e) {
    // Ctrl/Cmd + D: Deposit modal
    if ((e.ctrlKey || e.metaKey) && e.key === 'd') {
        e.preventDefault();
        $('#depositModal').modal('show');
    }

    // Ctrl/Cmd + W: Withdraw modal
    if ((e.ctrlKey || e.metaKey) && e.key === 'w') {
        e.preventDefault();
        $('#withdrawModal').modal('show');
    }

    // Ctrl/Cmd + T: Transfer modal
    if ((e.ctrlKey || e.metaKey) && e.key === 't') {
        e.preventDefault();
        $('#transferModal').modal('show');
    }

    // ESC: Close all modals
    if (e.key === 'Escape') {
        $('.modal').modal('hide');
    }
});

// Dark mode toggle (basic structure for future implementation)
function toggleDarkMode() {
    $('body').toggleClass('dark-mode');
    const isDark = $('body').hasClass('dark-mode');
    localStorage.setItem('darkMode', isDark);
}

// Initialize dark mode
function initDarkMode() {
    const isDark = localStorage.getItem('darkMode') === 'true';
    if (isDark) {
        $('body').addClass('dark-mode');
    }
}

// Debounce function for performance optimization
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

// Optimized window resize event
$(window).resize(debounce(function() {
    // Re-adjust chart sizes
    animateCharts();
}, 250));

// Cleanup on page unload
$(window).on('beforeunload', function() {
    // Clear timers
    clearInterval(updateRealTimeData);
});

// Currency formatting helper (override from common.js for multi-currency support)
function formatCurrency(amount) {
    return parseInt(amount).toLocaleString('en-GB');
}
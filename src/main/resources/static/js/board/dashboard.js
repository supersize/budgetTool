// Dashboard Management
$(document).ready(function() {
    // Initialize
    init();

    function init() {
        loadDashboardData();
    }

    // Load Dashboard Data
    function loadDashboardData() {
        fetch(ctxPath + 'Dashboard', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success && data.data) {
                renderDashboard(data.data);
            } else {
                console.error('Failed to load dashboard:', data.message);
            }
        })
        .catch(error => {
            console.error('Error loading dashboard:', error);
        });
    }

    // Render Dashboard
    function renderDashboard(dashboardData) {
        // Update welcome stats
        updateWelcomeStats(dashboardData);
        
        // Update account overview
        updateAccountOverview(dashboardData);
        
        // Update recent transactions
        updateRecentTransactions(dashboardData.recentTransactions);
    }

    // Update Welcome Stats
    function updateWelcomeStats(data) {
        $('.welcome-stats .stat-item:first-child span strong').text(data.totalAccounts);
        
        const growthPercentage = data.monthlyGrowthPercentage.toFixed(1);
        const growthSign = data.monthlyGrowthPercentage >= 0 ? '+' : '';
        $('.welcome-stats .stat-item:last-child span strong').text(growthSign + growthPercentage + '%');
    }

    // Update Account Overview
    function updateAccountOverview(data) {
        // Update total balance
        $('.balance-amount').text(formatCurrency(data.totalBalance));
        
        // Update monthly growth
        const growthAmount = data.monthlyGrowth;
        const growthClass = parseFloat(growthAmount) >= 0 ? 'text-success' : 'text-danger';
        const growthIcon = parseFloat(growthAmount) >= 0 ? 'bi-arrow-up' : 'bi-arrow-down';
        const growthSign = parseFloat(growthAmount) >= 0 ? '+' : '';
        
        $('.balance-change')
            .removeClass('text-success text-danger')
            .addClass(growthClass)
            .html(`<i class="bi ${growthIcon}"></i> ${growthSign}${formatCurrency(growthAmount)} (this month)`);
        
        // Update account breakdown
        if (data.accountOverviews && data.accountOverviews.length > 0) {
            const accountsHtml = data.accountOverviews.slice(0, 2).map(account => {
                return `
                    <div class="account-item">
                        <div class="account-info">
                            <div class="account-bank">${escapeHtml(account.bankName)}</div>
                            <div class="account-number">${maskAccountNumber(account.accountNumber)}</div>
                            <div class="account-balance">${getCurrencySymbol(account.currency)}${parseFloat(account.balance).toFixed(2)}</div>
                        </div>
                        <div class="account-chart">
                            <div class="chart-bar" style="height: ${account.percentage}%;"></div>
                        </div>
                    </div>
                `;
            }).join('');
            
            $('.accounts-grid').html(accountsHtml);
        }
    }

    // Update Recent Transactions
    function updateRecentTransactions(transactions) {
        if (!transactions || transactions.length === 0) {
            $('.transactions-list').html('<div class="text-center text-muted py-4">No recent transactions</div>');
            return;
        }

        const transactionsHtml = transactions.map(transaction => {
            const iconClass = getTransactionIcon(transaction.type);
            const iconBg = getTransactionIconBg(transaction.type);
            const amountClass = transaction.isIncome ? 'text-success' : 'text-danger';
            const amountSign = transaction.isIncome ? '+' : '-';
            
            return `
                <div class="transaction-item">
                    <div class="transaction-icon ${iconBg}">
                        <i class="bi ${iconClass}"></i>
                    </div>
                    <div class="transaction-details">
                        <div class="transaction-title">${escapeHtml(transaction.title)}</div>
                        <div class="transaction-account">${escapeHtml(transaction.accountInfo)}</div>
                        <div class="transaction-time">${formatDateTime(transaction.createdAt)}</div>
                    </div>
                    <div class="transaction-amount ${amountClass}">
                        ${amountSign}${getCurrencySymbol(transaction.currency)}${parseFloat(transaction.amount).toFixed(2)}
                    </div>
                </div>
            `;
        }).join('');

        $('.transactions-list').html(transactionsHtml);
    }

    // Utility Functions
    function getTransactionIcon(type) {
        const icons = {
            'DEPOSIT': 'bi-arrow-down-left',
            'WITHDRAWAL': 'bi-arrow-up-right',
            'TRANSFER_OUT': 'bi-arrow-right',
            'TRANSFER_IN': 'bi-arrow-left'
        };
        return icons[type] || 'bi-credit-card';
    }

    function getTransactionIconBg(type) {
        const backgrounds = {
            'DEPOSIT': 'bg-success',
            'WITHDRAWAL': 'bg-danger',
            'TRANSFER_OUT': 'bg-primary',
            'TRANSFER_IN': 'bg-info'
        };
        return backgrounds[type] || 'bg-warning';
    }

    function formatCurrency(amount) {
        return '£' + parseFloat(amount).toFixed(2);
    }

    function getCurrencySymbol(currency) {
        const symbols = {
            'GBP': '£',
            'USD': '$',
            'EUR': '€',
            'KRW': '₩'
        };
        return symbols[currency] || currency;
    }

    function maskAccountNumber(accountNumber) {
        if (!accountNumber || accountNumber.length < 4) {
            return accountNumber;
        }
        return '***' + accountNumber.substring(accountNumber.length - 4);
    }

    function formatDateTime(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('en-GB', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        }) + ' ' + date.toLocaleTimeString('en-GB', {
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

// Reports Management
$(document).ready(function() {
    let charts = {};

    // Initialize
    init();

    function init() {
        loadReportData();
        initializeEventListeners();
    }

    // Initialize Event Listeners
    function initializeEventListeners() {
        $('#refreshBtn').click(loadReportData);
    }

    // Load Report Data
    function loadReportData() {
        showLoading();

        fetch(ctxPath + 'Reports', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(data => {
            hideLoading();
            if (data.success && data.data) {
                renderReport(data.data);
            } else {
                console.error('Failed to load report:', data.message);
                showAlert('Failed to load report data', 'danger');
            }
        })
        .catch(error => {
            console.error('Error loading report:', error);
            hideLoading();
            showAlert('Failed to load report data', 'danger');
        });
    }

    // Render Report
    function renderReport(reportData) {
        // Update summary cards
        updateSummaryCards(reportData.monthlySummary);
        
        // Render charts
        renderIncomeExpenseChart(reportData.dailyTransactionSummary);
        renderTransactionTypeChart(reportData.transactionTypeSummary);
        renderAccountBalanceChart(reportData.accountBalanceSummary);
        
        // Render transaction summary table
        renderTransactionSummaryTable(reportData.transactionTypeSummary);
        
        $('#reportContent').show();
    }

    // Update Summary Cards
    function updateSummaryCards(summary) {
        $('#totalIncome').text(formatCurrency(summary.totalIncome));
        $('#totalExpense').text(formatCurrency(summary.totalExpense));
        
        const netAmount = parseFloat(summary.netAmount);
        const $netAmount = $('#netAmount');
        $netAmount.text(formatCurrency(Math.abs(netAmount)));
        
        // Color code net amount
        $netAmount.removeClass('text-success text-danger');
        if (netAmount > 0) {
            $netAmount.addClass('text-success');
        } else if (netAmount < 0) {
            $netAmount.addClass('text-danger');
        }
        
        $('#totalTransactions').text(summary.transactionCount);
    }

    // Render Income vs Expense Chart
    function renderIncomeExpenseChart(dailyData) {
        const ctx = document.getElementById('incomeExpenseChart');
        
        // Destroy existing chart
        if (charts.incomeExpense) {
            charts.incomeExpense.destroy();
        }

        const labels = dailyData.map(d => d.date);
        const incomeData = dailyData.map(d => parseFloat(d.income));
        const expenseData = dailyData.map(d => parseFloat(d.expense));

        charts.incomeExpense = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: 'Income',
                        data: incomeData,
                        borderColor: 'rgb(102, 126, 234)',
                        backgroundColor: 'rgba(102, 126, 234, 0.1)',
                        tension: 0.4,
                        fill: true
                    },
                    {
                        label: 'Expense',
                        data: expenseData,
                        borderColor: 'rgb(245, 87, 108)',
                        backgroundColor: 'rgba(245, 87, 108, 0.1)',
                        tension: 0.4,
                        fill: true
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    },
                    tooltip: {
                        mode: 'index',
                        intersect: false,
                        callbacks: {
                            label: function(context) {
                                return context.dataset.label + ': £' + context.parsed.y.toFixed(2);
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return '£' + value;
                            }
                        }
                    }
                }
            }
        });
    }

    // Render Transaction Type Chart
    function renderTransactionTypeChart(typeData) {
        const ctx = document.getElementById('transactionTypeChart');
        
        // Destroy existing chart
        if (charts.transactionType) {
            charts.transactionType.destroy();
        }

        const labels = typeData.map(d => formatTransactionType(d.type));
        const data = typeData.map(d => parseFloat(d.amount));
        const colors = [
            'rgba(102, 126, 234, 0.8)',
            'rgba(245, 87, 108, 0.8)',
            'rgba(67, 233, 123, 0.8)',
            'rgba(79, 172, 254, 0.8)'
        ];

        charts.transactionType = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: colors,
                    borderWidth: 2,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'bottom'
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const label = context.label || '';
                                const value = '£' + context.parsed.toFixed(2);
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = ((context.parsed / total) * 100).toFixed(1) + '%';
                                return label + ': ' + value + ' (' + percentage + ')';
                            }
                        }
                    }
                }
            }
        });
    }

    // Render Account Balance Chart
    function renderAccountBalanceChart(accountData) {
        const ctx = document.getElementById('accountBalanceChart');
        
        // Destroy existing chart
        if (charts.accountBalance) {
            charts.accountBalance.destroy();
        }

        const labels = accountData.map(d => d.accountName);
        const data = accountData.map(d => parseFloat(d.balance));

        charts.accountBalance = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Balance',
                    data: data,
                    backgroundColor: 'rgba(79, 172, 254, 0.8)',
                    borderColor: 'rgba(79, 172, 254, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return 'Balance: £' + context.parsed.y.toFixed(2);
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return '£' + value;
                            }
                        }
                    }
                }
            }
        });
    }

    // Render Transaction Summary Table
    function renderTransactionSummaryTable(typeData) {
        const $tbody = $('#transactionSummaryTable');
        
        if (typeData.length === 0) {
            $tbody.html('<tr><td colspan="3" class="text-center text-muted">No data available</td></tr>');
            return;
        }

        const rows = typeData.map(item => {
            return `
                <tr>
                    <td>
                        <span class="type-badge ${item.type.toLowerCase()}">
                            ${formatTransactionType(item.type)}
                        </span>
                    </td>
                    <td class="text-end"><strong>£${parseFloat(item.amount).toFixed(2)}</strong></td>
                    <td class="text-end">${item.count}</td>
                </tr>
            `;
        }).join('');

        $tbody.html(rows);
    }

    // Utility Functions
    function showLoading() {
        $('#loadingState').show();
        $('#reportContent').hide();
    }

    function hideLoading() {
        $('#loadingState').hide();
    }

    function formatCurrency(amount) {
        return '£' + parseFloat(amount).toFixed(2);
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
});

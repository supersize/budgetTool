$(document).ready(function() {
    // Load accounts when modals are opened
    $('#depositModal, #withdrawModal, #transferModal').on('show.bs.modal', function() {
        loadAccountsForTransactions();
    });

    // 사이드바 토글 (모바일)
    $('#sidebarToggle').click(function() {
        $('#sidebar').toggleClass('show');
    });

    // 사이드바 외부 클릭시 닫기 (모바일)
    $(document).click(function(e) {
        if ($(window).width() <= 992) {
            if (!$(e.target).closest('#sidebar, #sidebarToggle').length) {
                $('#sidebar').removeClass('show');
            }
        }
    });

    // 윈도우 리사이즈 처리
    $(window).resize(function() {
        if ($(window).width() > 992) {
            $('#sidebar').removeClass('show');
        }
    });

    // 로그아웃 버튼 클릭
    $('#logoutBtn').click(function(e) {
        e.preventDefault();
        $('#logoutModal').modal('show');
    });

    // 로그아웃 확인
    $('#confirmLogout').click(function() {
        logout();
    });

    // 입금 모달 처리
    $('#confirmDeposit').click(function() {
        processDeposit();
    });

    // 출금 모달 처리
    $('#confirmWithdraw').click(function() {
        processWithdrawal();
    });

    // 송금 모달 처리
    $('#confirmTransfer').click(function() {
        processTransfer();
    });

    // 금액 입력 필드 포맷팅
    $('input[type="number"]').on('input', function() {
        const value = $(this).val();
        if (value) {
            const numericValue = value.replace(/[^0-9.]/g, '');
            $(this).val(numericValue);
        }
    });

    // 계좌번호 입력 포맷팅
    $('#transferToAccount').on('input', function() {
        let value = $(this).val().replace(/[^0-9]/g, '');
        if (value.length > 3 && value.length <= 6) {
            value = value.substring(0, 3) + '-' + value.substring(3);
        } else if (value.length > 6) {
            value = value.substring(0, 3) + '-' + value.substring(3, 6) + '-' + value.substring(6, 12);
        }
        $(this).val(value);
    });

    // 모달 초기화
    $('.modal').on('hidden.bs.modal', function() {
        $(this).find('form')[0]?.reset();
        $(this).find('.is-invalid').removeClass('is-invalid');
        $(this).find('.error-message').hide();
    });

    // 툴팁 초기화
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

// Load Accounts for Transaction Modals
function loadAccountsForTransactions() {
    fetch(ctxPath + 'Accounts', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success && data.data) {
            populateAccountSelects(data.data);
        }
    })
    .catch(error => {
        console.error('Error loading accounts:', error);
    });
}

// Populate Account Select Dropdowns
function populateAccountSelects(accounts) {
    const $depositAccount = $('#depositAccount');
    const $withdrawAccount = $('#withdrawAccount');
    const $transferFromAccount = $('#transferFromAccount');

    // Clear existing options except the first one
    $depositAccount.find('option:not(:first)').remove();
    $withdrawAccount.find('option:not(:first)').remove();
    $transferFromAccount.find('option:not(:first)').remove();

    // Populate with actual accounts
    accounts.forEach(account => {
        const currencySymbol = getCurrencySymbol(account.currency);
        const optionText = `${account.bankName} - ${account.accountNumber} (${currencySymbol}${parseFloat(account.balance).toFixed(2)})`;
        const option = `<option value="${account.id}" data-currency="${account.currency}">${optionText}</option>`;
        
        if (account.isActive) {
            $depositAccount.append(option);
            $withdrawAccount.append(option);
            $transferFromAccount.append(option);
        }
    });
}

// Process Deposit
function processDeposit() {
    const accountId = $('#depositAccount').val();
    const amount = $('#depositAmount').val();
    const description = $('#depositMemo').val();

    if (!accountId) {
        showAlert('Please select an account', 'warning');
        return;
    }

    if (!amount || parseFloat(amount) <= 0) {
        showAlert('Please enter a valid amount', 'warning');
        return;
    }

    const $button = $('#confirmDeposit');
    showLoading($button);

    const requestData = {
        accountId: parseInt(accountId),
        amount: parseFloat(amount),
        description: description || 'Deposit'
    };

    fetch(ctxPath + 'Transactions/deposit', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
    .then(response => response.json())
    .then(data => {
        hideLoading($button);
        if (data.success) {
            $('#depositModal').modal('hide');
            showAlert('Deposit completed successfully!', 'success');
            
            // Reload page after 1 second
            setTimeout(() => {
                location.reload();
            }, 1000);
        } else {
            showAlert(data.message || 'Failed to process deposit', 'danger');
        }
    })
    .catch(error => {
        console.error('Error processing deposit:', error);
        hideLoading($button);
        showAlert('Failed to process deposit. Please try again.', 'danger');
    });
}

// Process Withdrawal
function processWithdrawal() {
    const accountId = $('#withdrawAccount').val();
    const amount = $('#withdrawAmount').val();
    const description = $('#withdrawMemo').val();

    if (!accountId) {
        showAlert('Please select an account', 'warning');
        return;
    }

    if (!amount || parseFloat(amount) <= 0) {
        showAlert('Please enter a valid amount', 'warning');
        return;
    }

    const $button = $('#confirmWithdraw');
    showLoading($button);

    const requestData = {
        accountId: parseInt(accountId),
        amount: parseFloat(amount),
        description: description || 'Withdrawal'
    };

    fetch(ctxPath + 'Transactions/withdraw', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
    .then(response => response.json())
    .then(data => {
        hideLoading($button);
        if (data.success) {
            $('#withdrawModal').modal('hide');
            showAlert('Withdrawal completed successfully!', 'success');
            
            // Reload page after 1 second
            setTimeout(() => {
                location.reload();
            }, 1000);
        } else {
            showAlert(data.message || 'Failed to process withdrawal', 'danger');
        }
    })
    .catch(error => {
        console.error('Error processing withdrawal:', error);
        hideLoading($button);
        showAlert('Failed to process withdrawal. Please try again.', 'danger');
    });
}

// Process Transfer
function processTransfer() {
    const fromAccountId = $('#transferFromAccount').val();
    let toAccountNumber = $('#transferToAccount').val();
    let result = toAccountNumber.split("-")
    toAccountNumber = '';
    for(var item of result)
       toAccountNumber += item;
    const toAccountHolderName = $('#transferToName').val();
    const amount = $('#transferAmount').val();
    const transferMessage = $('#transferMemo').val();

    if (!fromAccountId) {
        showAlert('Please select a source account', 'warning');
        return;
    }

    if (!toAccountNumber) {
        showAlert('Please enter recipient account number', 'warning');
        return;
    }

    if (!toAccountHolderName) {
        showAlert('Please enter recipient name', 'warning');
        return;
    }

    if (!amount || parseFloat(amount) <= 0) {
        showAlert('Please enter a valid amount', 'warning');
        return;
    }

    const $button = $('#confirmTransfer');
    showLoading($button);

    const requestData = {
        fromAccountId: parseInt(fromAccountId),
        toAccountNumber: toAccountNumber,
        toAccountHolderName: toAccountHolderName,
        amount: parseFloat(amount),
        transferMessage: transferMessage || ''
    };

    fetch(ctxPath + 'Transactions/transfer', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
    .then(response => response.json())
    .then(data => {
        hideLoading($button);
        if (data.success) {
            $('#transferModal').modal('hide');
            showAlert(`Transfer to ${toAccountHolderName} completed successfully!`, 'success');
            
            // Reload page after 1 second
            setTimeout(() => {
                location.reload();
            }, 1000);
        } else {
            showAlert(data.message || 'Failed to process transfer', 'danger');
        }
    })
    .catch(error => {
        console.error('Error processing transfer:', error);
        hideLoading($button);
        showAlert('Failed to process transfer. Please try again.', 'danger');
    });
}

// 유틸리티 함수들
function showLoading(button) {
    button.prop('disabled', true);
    const originalText = button.html();
    button.data('original-text', originalText);
    button.html('<div class="spinner-border spinner-border-sm me-2" role="status"></div>처리중...');
}

function hideLoading(button) {
    button.prop('disabled', false);
    const originalText = button.data('original-text');
    if (originalText) {
        button.html(originalText);
    }
}

function showAlert(message, type = 'info', duration = 5000) {
    const toastId = 'toast-' + Date.now();
    
    // Toast types and icons
    const toastConfig = {
        'success': {
            bgClass: 'bg-success',
            icon: 'bi-check-circle-fill',
            iconColor: 'text-white'
        },
        'danger': {
            bgClass: 'bg-danger',
            icon: 'bi-exclamation-triangle-fill',
            iconColor: 'text-white'
        },
        'warning': {
            bgClass: 'bg-warning',
            icon: 'bi-exclamation-triangle-fill',
            iconColor: 'text-dark'
        },
        'info': {
            bgClass: 'bg-info',
            icon: 'bi-info-circle-fill',
            iconColor: 'text-white'
        }
    };

    const config = toastConfig[type] || toastConfig['info'];

    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center ${config.bgClass} ${config.iconColor} border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi ${config.icon} me-2"></i>
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

    // Append to toast container
    $('.toast-container').append(toastHtml);

    // Initialize and show toast
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, {
        autohide: true,
        delay: duration
    });
    
    toast.show();

    // Remove from DOM after hidden
    toastElement.addEventListener('hidden.bs.toast', function() {
        $(this).remove();
    });
}

function formatCurrency(amount) {
    return parseInt(amount).toLocaleString('ko-KR');
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

function logout() {
    showAlert('signing out...', 'info', 2000);
    fetch(ctxPath + "logout", {
        method: "post"
    })
        .then(response => response.json())
        .then(data => {
            console.log("here is logout.")
            console.log(data)

            location.href = ctxPath + 'auth/login'
        })
        .catch(error => console.error('logout failed. : ', error))

    // 실제 구현시 사용할 코드
    /*
    $.ajax({
        url: '/api/auth/logout',
        method: 'POST',
        headers: {
            'X-Requested-With': 'XMLHttpRequest',
            [csrfHeader]: csrfToken
        },
        success: function() {
            window.location.href = '/login';
        },
        error: function() {
            showAlert('로그아웃 처리 중 오류가 발생했습니다.', 'danger');
        }
    });
    */
}

// AJAX 에러 핸들러
$(document).ajaxError(function(event, jqXHR, ajaxSettings, thrownError) {
    console.error('AJAX Error:', thrownError);

    let message = '요청 처리 중 오류가 발생했습니다.';

    if (jqXHR.status === 401) {
        message = '인증이 필요합니다. 다시 로그인해주세요.';
        setTimeout(() => {
            window.location.href = ctxPath + '/login';
        }, 2000);
    } else if (jqXHR.status === 403) {
        message = '접근 권한이 없습니다.';
    } else if (jqXHR.status === 500) {
        message = '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
    }

    showAlert(message, 'danger');
});

// Spring Boot와의 연동을 위한 CSRF 토큰 처리
const csrfToken = $('meta[name="_csrf"]').attr('content');
const csrfHeader = $('meta[name="_csrf_header"]').attr('content');

// AJAX 요청시 CSRF 토큰 자동 추가
$.ajaxSetup({
    beforeSend: function(xhr) {
        if (csrfToken && csrfHeader) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        }
    }
});
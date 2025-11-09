$(document).ready(function() {


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
        // 실제 구현시 서버에 로그아웃 요청
        logout();
    });

    // 입금 모달 처리
    $('#confirmDeposit').click(function() {
        const account = $('#depositAccount').val();
        const amount = $('#depositAmount').val();
        const memo = $('#depositMemo').val();

        if (!account) {
            showAlert('계좌를 선택해주세요.', 'warning');
            return;
        }

        if (!amount || amount <= 0) {
            showAlert('올바른 금액을 입력해주세요.', 'warning');
            return;
        }

        showLoading($(this));

        // 실제 구현시 서버에 입금 요청
        setTimeout(() => {
            hideLoading($(this));
            $('#depositModal').modal('hide');
            showAlert('입금이 완료되었습니다.', 'success');

            // 폼 초기화
            $('#depositForm')[0].reset();

            // 페이지 새로고침 또는 데이터 업데이트
            location.reload();
        }, 2000);
    });

    // 출금 모달 처리
    $('#confirmWithdraw').click(function() {
        const account = $('#withdrawAccount').val();
        const amount = $('#withdrawAmount').val();
        const memo = $('#withdrawMemo').val();

        if (!account) {
            showAlert('계좌를 선택해주세요.', 'warning');
            return;
        }

        if (!amount || amount <= 0) {
            showAlert('올바른 금액을 입력해주세요.', 'warning');
            return;
        }

        showLoading($(this));

        // 실제 구현시 서버에 출금 요청
        setTimeout(() => {
            hideLoading($(this));
            $('#withdrawModal').modal('hide');
            showAlert('출금이 완료되었습니다.', 'success');

            // 폼 초기화
            $('#withdrawForm')[0].reset();

            // 페이지 새로고침 또는 데이터 업데이트
            location.reload();
        }, 2000);
    });

    // 송금 모달 처리
    $('#confirmTransfer').click(function() {
        const fromAccount = $('#transferFromAccount').val();
        const toAccount = $('#transferToAccount').val();
        const toName = $('#transferToName').val();
        const amount = $('#transferAmount').val();
        const memo = $('#transferMemo').val();

        if (!fromAccount) {
            showAlert('출금 계좌를 선택해주세요.', 'warning');
            return;
        }

        if (!toAccount) {
            showAlert('받는 계좌번호를 입력해주세요.', 'warning');
            return;
        }

        if (!toName) {
            showAlert('받는 분의 이름을 입력해주세요.', 'warning');
            return;
        }

        if (!amount || amount <= 0) {
            showAlert('올바른 금액을 입력해주세요.', 'warning');
            return;
        }

        showLoading($(this));

        // 실제 구현시 서버에 송금 요청
        setTimeout(() => {
            hideLoading($(this));
            $('#transferModal').modal('hide');
            showAlert(`${toName}님께 ${formatCurrency(amount)}원이 송금되었습니다.`, 'success');

            // 폼 초기화
            $('#transferForm')[0].reset();

            // 페이지 새로고침 또는 데이터 업데이트
            location.reload();
        }, 2000);
    });

    // 금액 입력 필드 포맷팅
    $('input[type="number"]').on('input', function() {
        const value = $(this).val();
        if (value) {
            // 숫자만 허용
            const numericValue = value.replace(/[^0-9]/g, '');
            $(this).val(numericValue);
        }
    });

    // 계좌번호 입력 포맷팅
    $('#transferToAccount').on('input', function() {
        let value = $(this).val().replace(/[^0-9]/g, '');

        // 계좌번호 자동 하이픈 추가 (예: 123-456-789012)
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
    const alertId = 'alert-' + Date.now();
    const alertTypes = {
        'success': 'alert-success',
        'danger': 'alert-danger',
        'warning': 'alert-warning',
        'info': 'alert-info'
    };

    const alertClass = alertTypes[type] || alertTypes['info'];
    const iconTypes = {
        'success': 'bi-check-circle-fill',
        'danger': 'bi-exclamation-triangle-fill',
        'warning': 'bi-exclamation-triangle-fill',
        'info': 'bi-info-circle-fill'
    };
    const iconClass = iconTypes[type] || iconTypes['info'];

    const alertHtml = `
        <div id="${alertId}" class="alert ${alertClass} alert-dismissible fade show position-fixed" 
             style="top: 90px; right: 20px; z-index: 1050; min-width: 300px;">
            <i class="bi ${iconClass} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;

    $('body').append(alertHtml);

    // 자동 제거
    setTimeout(() => {
        $(`#${alertId}`).alert('close');
    }, duration);
}

function formatCurrency(amount) {
    return parseInt(amount).toLocaleString('ko-KR');
}

function logout() {
    showAlert('signing out...', 'info', 2000);
    fetch(ctxPath + "logout")
        .then(response => response.json())
        .then(data => {
            console.log("here is logout.")
            console.log(data)

            location.href = ctxPath + 'login'
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
            window.location.href = '/login';
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
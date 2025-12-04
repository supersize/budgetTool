$(document).ready(function() {
    let currentStep = 1;
    let resendTimer = 0;
    let resendInterval = null;

    // 이메일 검증 정규식
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    // Step 1: 이메일 입력 및 다음 단계
    $('#nextStep1').click(function() {
        const email = $('#email').val().trim();

        if (!validateEmail(email)) return;

        showLoading($(this));


        const isEmailUsedURL = ctxPath + "Auth/isEmailInUse"
        // 서버에 이메일 확인 요청 (실제 구현시 AJAX 호출)
        fetch(`${isEmailUsedURL}?inputEmail=${encodeURIComponent(email)}`)
            .then(response => response.json())
            .then(data => {
                $('#userEmail').text(email);
                const isEmailUsed = data.data

                if (!isEmailUsed) {
                    alert("This Email is not in used. Please check it again.")
                    return false;
                }

                goToStep(2);
            })
            .catch(error => console.error('email cannot be found ', error))
            .finally(() => hideLoading($(this)))
    });

    // Step 2: 비밀번호 입력 및 다음 단계
    $('#nextStep2').click(function() {
        const email = $('#email').val();
        const password = $('#password').val();

        if (!validatePassword(password)) {
            return;
        }

        showLoading($(this));

        //data setting
        const data = new URLSearchParams();
        data.append("email", email)
        data.append("passwordHash", password)

        fetch(ctxPath + "Auth/login", {
            method: 'post'
            , headers: { 'Content-Type' : 'application/x-www-form-urlencoded'}
            , body: data
        })
        .then(response => response.json())
        .then(data => {
            $('#userEmail').text(email);
            hideLoading($(this));

            // alert(ctxPath + "main")
            data.status != "success" ? alert(data.message) : location.href = ctxPath + "main"
        })
        .catch(error => console.error('email cannot be found ', error))
        .finally(() => hideLoading($(this)))


        // 서버에 로그인 요청 (실제 구현시 AJAX 호출)
        // setTimeout(() => {
        //     hideLoading($(this));
        //
        //     // 로그인 성공시 2FA 단계로
        //     const email = $('#email').val();
        //     $('#maskedEmail').text(maskEmail(email));
        //     // goToStep(3);
        //     startResendTimer();
        // }, 1500);
    });

    // Step 3: OTP 인증
    $('#verifyOtp').click(function(e) {
        e.preventDefault();
        const otp = getOtpValue();

        if (!validateOtp(otp)) {
            return;
        }

        showLoading($(this));

        // 서버에 OTP 확인 요청 (실제 구현시 AJAX 호출)
        setTimeout(() => {
            hideLoading($(this));

            // OTP 인증 성공
            showOtpSuccess('Verification completed successfully!');

            setTimeout(() => {
                // 대시보드로 리다이렉트 (실제 구현시)
                alert('Login successful! Redirecting to dashboard...');
                // window.location.href = '/dashboard';
            }, 1000);
        }, 1500);
    });

    // 이전 단계 버튼들
    $('#backStep1').click(() => goToStep(1));
    $('#backStep2').click(() => goToStep(2));

    // 비밀번호 표시/숨김 토글
    $('#togglePassword').click(function() {
        const passwordField = $('#password');
        const icon = $(this).find('i');

        if (passwordField.attr('type') === 'password') {
            passwordField.attr('type', 'text');
            icon.removeClass('bi-eye').addClass('bi-eye-slash');
        } else {
            passwordField.attr('type', 'password');
            icon.removeClass('bi-eye-slash').addClass('bi-eye');
        }
    });

    // OTP 입력 필드 자동 포커스 및 이동
    $('.otp-input').on('input', function() {
        const value = $(this).val();

        // 숫자만 허용
        if (!/^\d*$/.test(value)) {
            $(this).val('');
            return;
        }

        // 다음 필드로 자동 이동
        if (value.length === 1) {
            const nextInput = $(this).next('.otp-input');
            if (nextInput.length > 0) {
                nextInput.focus();
            }
        }

        // 모든 필드가 채워졌는지 확인
        if (getOtpValue().length === 6) {
            clearOtpError();
        }
    });

    // OTP 입력 필드 백스페이스 처리
    $('.otp-input').on('keydown', function(e) {
        if (e.key === 'Backspace' && $(this).val() === '') {
            const prevInput = $(this).prev('.otp-input');
            if (prevInput.length > 0) {
                prevInput.focus();
            }
        }
    });

    // 코드 재전송
    $('#resendCode').click(function() {
        if (resendTimer > 0) return;

        const email = $('#email').val();

        // 서버에 코드 재전송 요청 (실제 구현시 AJAX 호출)
        $.ajax({
            url: '/api/auth/resend-otp',
            method: 'POST',
            data: { email: email },
            success: function() {
                showOtpSuccess('Verification code has been resent.');
                startResendTimer();
            },
            error: function() {
                showOtpError('Failed to send code. Please try again.');
            }
        });
    });

    // Enter 키 처리
    $('#email').keypress(function(e) {
        if (e.which === 13) $('#nextStep1').click();
    });

    $('#password').keypress(function(e) {
        if (e.which === 13) $('#nextStep2').click();
    });

    // 함수들
    function goToStep(step) {
        // 이전 단계 숨기기
        $('.form-step').removeClass('active');
        $('.step').removeClass('active completed');

        // 현재 단계 표시
        $(`#step${step}`).addClass('active');
        $(`#step-${step}`).addClass('active');

        // 완료된 단계들 표시
        for (let i = 1; i < step; i++) {
            $(`#step-${i}`).addClass('completed');
        }

        currentStep = step;

        // 첫 번째 입력 필드에 포커스
        setTimeout(() => {
            $(`#step${step} .form-control:first`).focus();
        }, 300);
    }

    function validateEmail(email) {
        clearError('emailError');

        if (!email) {
            showError('emailError', 'Please enter your email address.');
            return false;
        }

        if (!emailRegex.test(email)) {
            showError('emailError', 'Please enter a valid email format.');
            return false;
        }

        return true;
    }

    function validatePassword(password) {
        clearError('passwordError');

        if (!password) {
            showError('passwordError', 'Please enter your password.');
            return false;
        }

        if (password.length < 6) {
            showError('passwordError', 'Password must be at least 6 characters long.');
            return false;
        }

        return true;
    }

    function validateOtp(otp) {
        clearOtpError();

        if (otp.length !== 6) {
            showOtpError('Please enter all 6 digits of the verification code.');
            return false;
        }

        if (!/^\d{6}$/.test(otp)) {
            showOtpError('Only numbers are allowed.');
            return false;
        }

        return true;
    }

    function getOtpValue() {
        let otp = '';
        for (let i = 1; i <= 6; i++) {
            otp += $(`#otp${i}`).val();
        }
        return otp;
    }

    function clearOtpInputs() {
        $('.otp-input').val('');
        $('#otp1').focus();
    }

    function maskEmail(email) {
        const [username, domain] = email.split('@');
        const maskedUsername = username.length > 2
            ? username[0] + '*'.repeat(username.length - 2) + username[username.length - 1]
            : username[0] + '*';
        return maskedUsername + '@' + domain;
    }

    function showError(elementId, message) {
        $(`#${elementId}`).text(message).show();
        $(`#${elementId}`).closest('.mb-3').find('.form-control').addClass('is-invalid');
    }

    function clearError(elementId) {
        $(`#${elementId}`).hide();
        $(`#${elementId}`).closest('.mb-3').find('.form-control').removeClass('is-invalid');
    }

    function showOtpError(message) {
        $('#otpError').text(message).show();
        $('.otp-input').addClass('is-invalid');
    }

    function clearOtpError() {
        $('#otpError').hide();
        $('.otp-input').removeClass('is-invalid');
    }

    function showOtpSuccess(message) {
        $('#otpSuccess').text(message).show();
        $('.otp-input').addClass('is-valid');
    }

    function showLoading(button) {
        button.prop('disabled', true);
        button.find('.btn-text').hide();
        button.find('.loading-spinner').show();
    }

    function hideLoading(button) {
        button.prop('disabled', false);
        button.find('.btn-text').show();
        button.find('.loading-spinner').hide();
    }

    function startResendTimer() {
        resendTimer = 60;
        $('#resendCode').prop('disabled', true);

        resendInterval = setInterval(() => {
            resendTimer--;
            $('#resendTimer').text(`(${resendTimer}s)`);

            if (resendTimer <= 0) {
                clearInterval(resendInterval);
                $('#resendCode').prop('disabled', false);
                $('#resendTimer').text('');
            }
        }, 1000);
    }

    // 실제 Spring Boot 백엔드와 연동할 때 사용할 AJAX 함수들
    function checkEmailExists(email) {
        return $.ajax({
            url: '/api/auth/check-email',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ email: email }),
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                [csrfHeader]: csrfToken // CSRF 토큰 (Thymeleaf에서 설정)
            }
        });
    }

    function loginUser(email, password, rememberMe) {



        return $.ajax({
            url: '/Auth/login',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                email: email,
                password: password,
                rememberMe: rememberMe
            }),
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                [csrfHeader]: csrfToken
            }
        });
    }

    function verifyOtpCode(otp) {
        return $.ajax({
            url: '/api/auth/verify-otp',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ otp: otp }),
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                [csrfHeader]: csrfToken
            }
        });
    }

    function resendOtpCode(email) {
        return $.ajax({
            url: '/api/auth/resend-otp',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ email: email }),
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                [csrfHeader]: csrfToken
            }
        });
    }

    // 페이지 로드시 첫 번째 입력 필드에 포커스
    $('#email').focus();
});
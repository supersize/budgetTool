$(document).ready(function() {
    let currentStep = 1;
    let userEmail = '';
    let verificationToken = '';
    let resendTimer = null;
    let resendCountdown = 60;

    // Step 관리
    function showStep(step) {
        $('.form-step').removeClass('active');
        $(`#step${step}`).addClass('active');

        // Step indicator 업데이트
        $('.step').removeClass('active');
        for (let i = 1; i <= step; i++) {
            $(`#step-${i}`).addClass('active');
        }

        currentStep = step;
    }

    // 이메일 유효성 검사
    function validateEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    // 로딩 상태 관리
    function setButtonLoading(button, isLoading) {
        const $button = $(button);
        const $spinner = $button.find('.loading-spinner');
        const $text = $button.find('.btn-text');

        if (isLoading) {
            $button.prop('disabled', true);
            $spinner.show();
        } else {
            $button.prop('disabled', false);
            $spinner.hide();
        }
    }

    // 에러 메시지 표시
    function showError(elementId, message) {
        $(`#${elementId}`).text(message).show();
    }

    function hideError(elementId) {
        $(`#${elementId}`).text('').hide();
    }

    // Step 1: 이메일 전송
    $('#sendCodeBtn').click(function() {
        const email = $('#email').val().trim();

        hideError('emailError');

        if (!email) {
            showError('emailError', 'Please enter your email address.');
            return;
        }

        if (!validateEmail(email)) {
            showError('emailError', 'Please enter a valid email address.');
            return;
        }

        setButtonLoading(this, true);

        const requestBody = {
            email: email
        };

        fetch(ctxPath + "Auth/send-verification", {
            method: "post"
            ,headers: { 'Content-Type' : 'application/json'}
            , body: JSON.stringify(requestBody)
        })
        .then(response => response.json())
        .then(data => {
            // $('#userEmail').text(email);
            // hideLoading($(this));

            alert("you did it!")
            // data.status != "success" ? alert(data.message) : location.href = ctxPath + "main"
        })
        .catch(error => console.error('email cannot be found ', error))
        // .finally(() => setButtonLoading('#sendCodeBtn', false))

        // API 호출
        // $.ajax({
        //     url: '/api/auth/forgot-password/send-code',
        //     method: 'POST',
        //     contentType: 'application/json',
        //     data: JSON.stringify({ email: email }),
        //     success: function(response) {
        //         userEmail = email;
        //         $('#maskedEmail').text(maskEmail(email));
        //         showStep(2);
        //         startResendTimer();
        //     },
        //     error: function(xhr) {
        //         const errorMsg = xhr.responseJSON?.message || 'Failed to send verification code. Please try again.';
        //         showError('emailError', errorMsg);
        //     },
        //     complete: function() {
        //         setButtonLoading('#sendCodeBtn', false);
        //     }
        // });
    });

    // 이메일 마스킹
    function maskEmail(email) {
        const [username, domain] = email.split('@');
        const maskedUsername = username.charAt(0) + '***' + username.charAt(username.length - 1);
        return maskedUsername + '@' + domain;
    }

    // Resend 타이머
    function startResendTimer() {
        resendCountdown = 60;
        $('#resendCode').prop('disabled', true);

        resendTimer = setInterval(function() {
            resendCountdown--;
            $('#resendTimer').text(`(${resendCountdown}s)`);

            if (resendCountdown <= 0) {
                clearInterval(resendTimer);
                $('#resendCode').prop('disabled', false);
                $('#resendTimer').text('');
            }
        }, 1000);
    }

    // Resend 버튼
    $('#resendCode').click(function() {
        setButtonLoading(this, true);

        $.ajax({
            url: '/api/auth/forgot-password/send-code',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ email: userEmail }),
            success: function(response) {
                alert('Verification code has been resent.');
                startResendTimer();
            },
            error: function(xhr) {
                const errorMsg = xhr.responseJSON?.message || 'Failed to resend code.';
                showError('otpError', errorMsg);
            },
            complete: function() {
                setButtonLoading('#resendCode', false);
            }
        });
    });

    // OTP 입력 핸들링
    $('.otp-input').on('input', function() {
        const $this = $(this);
        const value = $this.val();

        // 숫자만 입력
        if (!/^\d*$/.test(value)) {
            $this.val('');
            return;
        }

        // 다음 입력 필드로 이동
        if (value.length === 1) {
            const nextInput = $this.next('.otp-input');
            if (nextInput.length) {
                nextInput.focus();
            }
        }
    });

    $('.otp-input').on('keydown', function(e) {
        const $this = $(this);

        // Backspace 처리
        if (e.key === 'Backspace' && $this.val() === '') {
            const prevInput = $this.prev('.otp-input');
            if (prevInput.length) {
                prevInput.focus();
            }
        }
    });

    // Step 2: 코드 검증
    $('#verifyCodeBtn').click(function() {
        const otp = getOTPValue();

        hideError('otpError');

        if (otp.length !== 6) {
            showError('otpError', 'Please enter all 6 digits.');
            return;
        }

        setButtonLoading(this, true);

        $.ajax({
            url: '/Auth/forgot-password/verify-code',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                email: userEmail,
                code: otp
            }),
            success: function(response) {
                verificationToken = response.token;
                showStep(3);
            },
            error: function(xhr) {
                const errorMsg = xhr.responseJSON?.message || 'Invalid verification code.';
                showError('otpError', errorMsg);
                clearOTPInputs();
            },
            complete: function() {
                setButtonLoading('#verifyCodeBtn', false);
            }
        });
    });

    function getOTPValue() {
        let otp = '';
        for (let i = 1; i <= 6; i++) {
            otp += $(`#otp${i}`).val();
        }
        return otp;
    }

    function clearOTPInputs() {
        $('.otp-input').val('');
        $('#otp1').focus();
    }

    // 비밀번호 토글
    $('#toggleNewPassword').click(function() {
        togglePasswordVisibility('#newPassword', this);
    });

    $('#toggleConfirmPassword').click(function() {
        togglePasswordVisibility('#confirmPassword', this);
    });

    function togglePasswordVisibility(inputId, button) {
        const $input = $(inputId);
        const $icon = $(button).find('i');

        if ($input.attr('type') === 'password') {
            $input.attr('type', 'text');
            $icon.removeClass('bi-eye').addClass('bi-eye-slash');
        } else {
            $input.attr('type', 'password');
            $icon.removeClass('bi-eye-slash').addClass('bi-eye');
        }
    }

    // 비밀번호 유효성 실시간 검사
    $('#newPassword').on('input', function() {
        const password = $(this).val();

        // 최소 8자
        if (password.length >= 8) {
            $('#req-length').addClass('valid');
        } else {
            $('#req-length').removeClass('valid');
        }

        // 대문자
        if (/[A-Z]/.test(password)) {
            $('#req-uppercase').addClass('valid');
        } else {
            $('#req-uppercase').removeClass('valid');
        }

        // 소문자
        if (/[a-z]/.test(password)) {
            $('#req-lowercase').addClass('valid');
        } else {
            $('#req-lowercase').removeClass('valid');
        }

        // 숫자
        if (/\d/.test(password)) {
            $('#req-number').addClass('valid');
        } else {
            $('#req-number').removeClass('valid');
        }

        // 특수문자
        if (/[!@#$%^&*]/.test(password)) {
            $('#req-special').addClass('valid');
        } else {
            $('#req-special').removeClass('valid');
        }
    });

    // Step 3: 비밀번호 재설정
    $('#resetPasswordBtn').click(function() {
        const newPassword = $('#newPassword').val();
        const confirmPassword = $('#confirmPassword').val();

        hideError('newPasswordError');
        hideError('confirmPasswordError');

        // 비밀번호 유효성 검사
        if (!validatePassword(newPassword)) {
            showError('newPasswordError', 'Password does not meet requirements.');
            return;
        }

        if (newPassword !== confirmPassword) {
            showError('confirmPasswordError', 'Passwords do not match.');
            return;
        }

        setButtonLoading(this, true);

        $.ajax({
            url: '/api/auth/forgot-password/reset',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                email: userEmail,
                token: verificationToken,
                newPassword: newPassword
            }),
            success: function(response) {
                $('#successStep').show();
                $('.form-step').not('#successStep').hide();
                $('.step-indicator').hide();
            },
            error: function(xhr) {
                const errorMsg = xhr.responseJSON?.message || 'Failed to reset password.';
                showError('newPasswordError', errorMsg);
            },
            complete: function() {
                setButtonLoading('#resetPasswordBtn', false);
            }
        });
    });

    function validatePassword(password) {
        return password.length >= 8 &&
            /[A-Z]/.test(password) &&
            /[a-z]/.test(password) &&
            /\d/.test(password) &&
            /[!@#$%^&*]/.test(password);
    }

    // Back 버튼
    $('#backToStep1').click(function() {
        clearInterval(resendTimer);
        clearOTPInputs();
        showStep(1);
    });

    // 초기화
    setButtonLoading('.btn-primary', false);
});
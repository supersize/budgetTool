$(document).ready(function() {
    let currentStep = 1;
    let resendTimer = 0;
    let resendInterval = null;

    // 정규식 패턴들
    const patterns = {
        email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        password: {
            minLength: /.{8,}/,
            uppercase: /[A-Z]/,
            lowercase: /[a-z]/,
            number: /[0-9]/,
            special: /[!@#$%^&*(),.?":{}|<>]/
        },
        phone: /^01[0-9]-\d{4}-\d{4}$/,
        name: /^[a-zA-Z가-힣\s]{2,30}$/
    };

    // Step 1: 기본 정보 입력 및 다음 단계
    $('#nextStep1').click(function() {
        if (validateStep1()) {
            showLoading($(this));

            // 이메일 중복 확인 (실제 구현시 AJAX 호출)
            const email = $('#email').val().trim();
            checkEmailAvailability(email)
                .then(() => {
                    hideLoading($(this));
                    goToStep(2);
                })
                .catch((errorMsg) => {
                    console.log(errorMsg)
                    hideLoading($(this));
                    showError('emailError', errorMsg || 'Email is already registered.');
                });
        }
    });

    // Step 2: 보안 설정 및 다음 단계
    $('#nextStep2').click(function() {
        if (validateStep2()) {
            showLoading($(this));

            setTimeout(() => {
                hideLoading($(this));
                goToStep(3);
            }, 800);
        }
    });

    // Step 3: 프로필 정보 및 다음 단계
    $('#nextStep3').click(function() {
        if (validateStep3()) {
            showLoading($(this));

            // 회원가입 데이터 준비 및 이메일 인증 전송
            const userData = collectUserData();
            sendVerificationEmail(userData)
                .then(() => {
                    hideLoading($(this));
                    const email = $('#email').val();
                    $('#maskedEmail').text(maskEmail(email));
                    goToStep(4);
                    startResendTimer();
                })
                .catch((error) => {
                    hideLoading($(this));
                    showError('generalError', error.message || 'Failed to send verification email.');
                });
        }
    });

    // Step 4: 회원가입 완료
    $('#completeSignup').click(function(e) {
        e.preventDefault();

        if (validateStep4()) {
            showLoading($(this));

            const otp = getOtpValue();
            const userData = collectUserData();

            completeRegistration(userData, otp)
                .then(() => {
                    hideLoading($(this));
                    showOtpSuccess('Account created successfully!');

                    setTimeout(() => {
                        $('#successModal').modal('show');
                    }, 1000);
                })
                .catch((error) => {
                    hideLoading($(this));
                    showOtpError(error.message || 'Verification failed. Please try again.');
                });
        }
    });

    // 이전 단계 버튼들
    $('#backStep1').click(() => goToStep(1));
    $('#backStep2').click(() => goToStep(2));
    $('#backStep3').click(() => goToStep(3));

    // 비밀번호 표시/숨김 토글
    $('#togglePassword, #toggleConfirmPassword').click(function() {
        const targetId = $(this).attr('id') === 'togglePassword' ? '#password' : '#confirmPassword';
        const passwordField = $(targetId);
        const icon = $(this).find('i');

        if (passwordField.attr('type') === 'password') {
            passwordField.attr('type', 'text');
            icon.removeClass('bi-eye').addClass('bi-eye-slash');
        } else {
            passwordField.attr('type', 'password');
            icon.removeClass('bi-eye-slash').addClass('bi-eye');
        }
    });

    // 패스워드 강도 체크
    $('#password').on('input', function() {
        const password = $(this).val();
        updatePasswordStrength(password);
    });

    // 패스워드 확인 실시간 검증
    $('#confirmPassword').on('input', function() {
        const password = $('#password').val();
        const confirmPassword = $(this).val();

        if (confirmPassword && password !== confirmPassword) {
            showError('confirmPasswordError', 'Passwords do not match.');
        } else {
            clearError('confirmPasswordError');
        }
    });

    // 전화번호 포맷팅
    $('#phoneNumber').on('input', function() {
        let value = $(this).val().replace(/[^\d]/g, '');

        if (value.length <= 3) {
            value = value;
        } else if (value.length <= 7) {
            value = value.substring(0, 3) + '-' + value.substring(3);
        } else {
            value = value.substring(0, 3) + '-' + value.substring(3, 7) + '-' + value.substring(7, 11);
        }

        $(this).val(value);
    });

    // OTP 입력 필드 자동 포커스 및 이동 (로그인과 동일)
    $('.otp-input').on('input', function() {
        const value = $(this).val();

        if (!/^\d*$/.test(value)) {
            $(this).val('');
            return;
        }

        if (value.length === 1) {
            const nextInput = $(this).next('.otp-input');
            if (nextInput.length > 0) {
                nextInput.focus();
            }
        }

        if (getOtpValue().length === 6) {
            clearOtpError();
        }
    });

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

        const userData = collectUserData();

        resendVerificationEmail(userData)
            .then(() => {
                showOtpSuccess('Verification code has been resent.');
                startResendTimer();
            })
            .catch(() => {
                showOtpError('Failed to send code. Please try again.');
            });
    });

    // Enter 키 처리
    $('#firstName, #lastName, #email, #dateOfBirth').keypress(function(e) {
        if (e.which === 13) $('#nextStep1').click();
    });

    $('#password, #confirmPassword').keypress(function(e) {
        if (e.which === 13) $('#nextStep2').click();
    });

    $('#phoneNumber, #occupation').keypress(function(e) {
        if (e.which === 13) $('#nextStep3').click();
    });

    // 단계 이동 함수
    function goToStep(step) {
        $('.form-step').removeClass('active');
        $('.step, .step-label').removeClass('active completed');

        $(`#step${step}`).addClass('active');
        $(`#step-${step}`).addClass('active');
        $(`#label-${step}`).addClass('active');

        for (let i = 1; i < step; i++) {
            $(`#step-${i}`).addClass('completed');
            $(`#label-${i}`).addClass('completed');
        }

        currentStep = step;

        // 접근성 - 스크린 리더에게 단계 변경 알림
        const stepNames = ['Basic Information', 'Security Settings', 'Profile Information', 'Email Verification'];
        $('#step-announcement').text(`Step ${step}: ${stepNames[step-1]}`);

        setTimeout(() => {
            $(`#step${step} .form-control:first, #step${step} .otp-input:first`).focus();
        }, 300);
    }

    // Step 1 검증
    function validateStep1() {
        let isValid = true;

        const firstName = $('#firstName').val().trim();
        const lastName = $('#lastName').val().trim();
        const email = $('#email').val().trim();
        const dateOfBirth = $('#dateOfBirth').val();

        clearError('firstNameError');
        clearError('lastNameError');
        clearError('emailError');
        clearError('dateOfBirthError');

        if (!firstName) {
            showError('firstNameError', 'First name is required.');
            isValid = false;
        } else if (!patterns.name.test(firstName)) {
            showError('firstNameError', 'Please enter a valid first name.');
            isValid = false;
        }

        if (!lastName) {
            showError('lastNameError', 'Last name is required.');
            isValid = false;
        } else if (!patterns.name.test(lastName)) {
            showError('lastNameError', 'Please enter a valid last name.');
            isValid = false;
        }

        if (!email) {
            showError('emailError', 'Email address is required.');
            isValid = false;
        } else if (!patterns.email.test(email)) {
            showError('emailError', 'Please enter a valid email address.');
            isValid = false;
        }

        if (!dateOfBirth) {
            showError('dateOfBirthError', 'Date of birth is required.');
            isValid = false;
        } else {
            const today = new Date();
            const birthDate = new Date(dateOfBirth);
            const age = today.getFullYear() - birthDate.getFullYear();

            if (age < 18) {
                showError('dateOfBirthError', 'You must be at least 18 years old.');
                isValid = false;
            } else if (age > 120) {
                showError('dateOfBirthError', 'Please enter a valid date of birth.');
                isValid = false;
            }
        }

        return isValid;
    }

    // Step 2 검증
    function validateStep2() {
        let isValid = true;

        const password = $('#password').val();
        const confirmPassword = $('#confirmPassword').val();

        clearError('passwordError');
        clearError('confirmPasswordError');

        if (!password) {
            showError('passwordError', 'Password is required.');
            isValid = false;
        } else if (!isPasswordStrong(password)) {
            showError('passwordError', 'Password must be at least 8 characters with uppercase, lowercase, and numbers.');
            isValid = false;
        }

        if (!confirmPassword) {
            showError('confirmPasswordError', 'Please confirm your password.');
            isValid = false;
        } else if (password !== confirmPassword) {
            showError('confirmPasswordError', 'Passwords do not match.');
            isValid = false;
        }

        return isValid;
    }

    // Step 3 검증
    function validateStep3() {
        let isValid = true;

        const phoneNumber = $('#phoneNumber').val();
        const occupation = $('#occupation').val();

        clearError('phoneNumberError');
        clearError('occupationError');

        if (!phoneNumber) {
            showError('phoneNumberError', 'Phone number is required.');
            isValid = false;
        } else if (!patterns.phone.test(phoneNumber)) {
            showError('phoneNumberError', 'Please enter a valid phone number (010-1234-5678).');
            isValid = false;
        }

        if (!occupation) {
            showError('occupationError', 'Please select your occupation.');
            isValid = false;
        }

        return isValid;
    }

    // Step 4 검증
    function validateStep4() {
        let isValid = true;

        const otp = getOtpValue();
        const agreeTerms = $('#agreeTerms').is(':checked');

        clearOtpError();
        clearError('agreeTermsError');

        if (otp.length !== 6) {
            showOtpError('Please enter all 6 digits of the verification code.');
            isValid = false;
        } else if (!/^\d{6}$/.test(otp)) {
            showOtpError('Only numbers are allowed.');
            isValid = false;
        }

        if (!agreeTerms) {
            showError('agreeTermsError', 'You must agree to the terms and conditions.');
            isValid = false;
        }

        return isValid;
    }

    // 패스워드 강도 업데이트
    function updatePasswordStrength(password) {
        const strengthContainer = $('#passwordStrength');

        if (!password) {
            strengthContainer.removeClass('weak fair good strong').html('');
            return;
        }

        let score = 0;
        let feedback = [];

        if (patterns.password.minLength.test(password)) score++;
        else feedback.push('at least 8 characters');

        if (patterns.password.uppercase.test(password)) score++;
        else feedback.push('uppercase letter');

        if (patterns.password.lowercase.test(password)) score++;
        else feedback.push('lowercase letter');

        if (patterns.password.number.test(password)) score++;
        else feedback.push('number');

        if (patterns.password.special.test(password)) score++;

        const levels = ['weak', 'fair', 'good', 'strong', 'strong'];
        const level = levels[score] || 'weak';

        strengthContainer
            .removeClass('weak fair good strong')
            .addClass(level)
            .html(`<div class="strength-bar"></div>`);
    }

    // 패스워드 강도 확인
    function isPasswordStrong(password) {
        return patterns.password.minLength.test(password) &&
            patterns.password.uppercase.test(password) &&
            patterns.password.lowercase.test(password) &&
            patterns.password.number.test(password);
    }

    // 사용자 데이터 수집
    function collectUserData() {
        const financialGoals = [];
        $('input[name="financialGoals"]:checked').each(function() {
            financialGoals.push($(this).val());
        });

        return {
            firstName: $('#firstName').val().trim(),
            lastName: $('#lastName').val().trim(),
            email: $('#email').val().trim(),
            dateOfBirth: $('#dateOfBirth').val(),
            password: $('#password').val(),
            phoneNumber: $('#phoneNumber').val(),
            occupation: $('#occupation').val(),
            incomeRange: $('#incomeRange').val(),
            financialGoals: financialGoals
        };
    }

    // OTP 관련 함수들 (로그인과 동일)
    function getOtpValue() {
        let otp = '';
        for (let i = 1; i <= 6; i++) {
            otp += $(`#otp${i}`).val();
        }
        return otp;
    }

    function maskEmail(email) {
        const [username, domain] = email.split('@');
        const maskedUsername = username.length > 2
            ? username[0] + '*'.repeat(username.length - 2) + username[username.length - 1]
            : username[0] + '*';
        return maskedUsername + '@' + domain;
    }

    // 에러 및 성공 메시지 관리
    function showError(elementId, message) {
        $(`#${elementId}`).text(message).show();
        $(`#${elementId}`).closest('.mb-3').find('.form-control, .otp-input, select').addClass('is-invalid');
    }

    function clearError(elementId) {
        $(`#${elementId}`).hide();
        $(`#${elementId}`).closest('.mb-3').find('.form-control, .otp-input, select').removeClass('is-invalid');
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

    // 로딩 스피너 관리
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

    // 재전송 타이머
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

    // Spring Boot 백엔드와 연동할 AJAX 함수들
    function checkEmailAvailability(email) {
        const params = new URLSearchParams();
        params.append("inputEmail", email);

        return new Promise((resolve, reject) => {
            fetch(ctxPath + "Auth/isEmailInUse", {
                method: "post"
                , body: params
            })
                .then(response => {
                    if (!response.ok)
                        throw new Error("HTTP error or another error occurs while checking if email is in use.")
                    return response.json();
                })
                .then(isEmailInUse => {
                    console.log(isEmailInUse)
                    if (!isEmailInUse.success) {
                        throw new Error(isEmailInUse.message)
                        return reject(isEmailInUse)
                    }

                    if (isEmailInUse.data)
                        return reject(null)

                    resolve(isEmailInUse)
                })
                .catch(error => {
                    reject(error.message)
                })
        })


        // return new Promise((resolve, reject) => {
        //     // 실제 구현시 AJAX 호출
        //     setTimeout(() => {
        //         // 임시로 일부 이메일을 중복으로 처리
        //         if (email === 'test@example.com') {
        //             reject(new Error('This email is already registered.'));
        //         } else {
        //             resolve();
        //         }
        //     }, 1000);
        //
        //     /* 실제 구현시 사용할 코드
        //     $.ajax({
        //         url: '/api/auth/check-email',
        //         method: 'POST',
        //         contentType: 'application/json',
        //         data: JSON.stringify({ email: email }),
        //         headers: {
        //             'X-Requested-With': 'XMLHttpRequest',
        //             [csrfHeader]: csrfToken
        //         }
        //     }).done(resolve).fail(reject);
        //     */
        // });
    }

    function sendVerificationEmail(userData) {
        return new Promise((resolve, reject) => {
            // 실제 구현시 AJAX 호출
            setTimeout(() => {
                resolve();
            }, 1500);

            /* 실제 구현시 사용할 코드
            $.ajax({
                url: '/api/auth/send-verification',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(userData),
                headers: {
                    'X-Requested-With': 'XMLHttpRequest',
                    [csrfHeader]: csrfToken
                }
            }).done(resolve).fail(reject);
            */
        });
    }

    function resendVerificationEmail(userData) {
        return new Promise((resolve, reject) => {
            // 실제 구현시 AJAX 호출
            setTimeout(() => {
                resolve();
            }, 1000);

            /* 실제 구현시 사용할 코드
            $.ajax({
                url: '/api/auth/resend-verification',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ email: userData.email }),
                headers: {
                    'X-Requested-With': 'XMLHttpRequest',
                    [csrfHeader]: csrfToken
                }
            }).done(resolve).fail(reject);
            */

            fetch()
        });
    }

    function completeRegistration(userData, otp) {
        return new Promise((resolve, reject) => {
            // 실제 구현시 AJAX 호출
            setTimeout(() => {
                if (otp === '123456') { // 임시 테스트 OTP
                    resolve();
                } else {
                    reject(new Error('Invalid verification code.'));
                }
            }, 1500);

            /* 실제 구현시 사용할 코드
            $.ajax({
                url: '/api/auth/register',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ ...userData, otp: otp }),
                headers: {
                    'X-Requested-With': 'XMLHttpRequest',
                    [csrfHeader]: csrfToken
                }
            }).done(resolve).fail(reject);
            */
        });
    }

    // 페이지 로드시 첫 번째 입력 필드에 포커스
    $('#firstName').focus();
});
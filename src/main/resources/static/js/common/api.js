const apiWrapper = (function () {
    async function request(endPoint, options) {
        const headers = {...options.headers}

        // content-type config
        if (!options.body && typeof options.body === 'object'
            && !(options.body instanceof FormData) && !headers['Content-Type']) {
            headers['Content-Type'] = 'application/json'
        }

        const config = {
            ...options
            , credentials: 'include'
            , headers
        }

        try {
            let response = await fetch(ctxPath + endPoint, config)

            // 401 error - token regen
            if (response.status == 401 && !options._retry) {
                const refreshResponse = await fetch(ctxPath + 'Auth/refresh', {
                    method: "post"
                    , credentials: 'include'
                })

                if (refreshResponse.ok) {
                    return apiRequest(endPoint, {...options, _retry: true})
                } else {
                    window.location.href = ctxPath + "login"
                    throw new Error("Expired Authentication.")
                }
            }

            // parsing response
            const contentType = response.headers.get('Content-Type')
            let result;

            if (contentType?.includes('application/json')) {
                result = await response.json();

                // ApiResponse 구조 처리
                if (result.success === false) {
                    // 서버에서 실패 응답을 보낸 경우
                    throw new ApiError(result.message, result);
                }

                // 성공 시 data 필드만 반환 (옵션)
                return result.data;

            } else if (contentType?.includes('application/octet-stream')) {
                return await response.blob();
            } else {
                return await response.text();
            }
        } catch (error) {
            console.error('API 오류:', error);
            throw error;
        }
    };

    // 공개 API만 반환
    return {
        getMethod: (endpoint) => request(endpoint, { method: 'GET' }),

        post: (endpoint, data) => request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        }),

        put: (endpoint, data) => request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        }),

        delete: (endpoint) => request(endpoint, { method: 'DELETE' }),

        postFile: (endpoint, formData) => request(endpoint, {
            method: 'POST',
            body: formData
        })
    }});


// 커스텀 에러 클래스
class ApiError extends Error {
    constructor(message, response) {
        super(message);
        this.name = 'ApiError';
        this.response = response; // { success, message, data, timestamp }
        this.timestamp = response?.timestamp;
    }
};

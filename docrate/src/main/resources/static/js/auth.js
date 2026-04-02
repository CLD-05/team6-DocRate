async function fetchWithAuth(url, options = {}) {
    const originalOptions = {
        ...options,
        credentials: 'include'
    };

    let response = await fetch(url, originalOptions);

    // 401이 아니면 그대로 반환
    if (response.status !== 401) {
        return response;
    }

    console.log('Access Token 만료 또는 인증 실패 → 재발급 시도');

    // refresh token으로 재발급 요청
    const reissueResponse = await fetch('/token/reissue', {
        method: 'POST',
        credentials: 'include'
    });

    // 재발급 실패 시 로그인 페이지로 이동
    if (!reissueResponse.ok) {
        console.log('재발급 실패 → 로그인 페이지 이동');
        window.location.href = '/login';
        return Promise.reject(new Error('토큰 재발급 실패'));
    }

    console.log('재발급 성공 → 원래 요청 재시도');

    // 재발급 성공 후 원래 요청 다시 시도
    response = await fetch(url, originalOptions);
    return response;
}